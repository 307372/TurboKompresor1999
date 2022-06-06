package com.example.turbokompresor1999;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ProcessingActivity extends AppCompatActivity {

    String path;
    int flags;
    long parent_lookup_id;
    long amountOfBlocks;
    ProcessingThread worker;
    ProgressPollingThread poller;
    boolean success = false;
    boolean processingDone = false;
    long maxPartialProgress = 0;
    TextView tvCurrentFileName;
    TextView tvTimePassed;
    TextView tvCurrentPhase;
    ProgressBar partialProgressBar;
    ProgressBar totalProgressBar;
    Handler mainThreadHandler;
    Date startDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing);
        findViewsById();

        Bundle extras = getIntent().getExtras();

        path = extras.getString("pathToFile");
        tvCurrentFileName.setText(new java.io.File(path).getName());

        flags = extras.getInt("flags");
        amountOfBlocks = extras.getLong("amountOfBlocks");
        maxPartialProgress = extras.getLong("partialProgressFor100Percent");
        parent_lookup_id = ArchiveManager.getInstance().currentFolder.get().lookup_id;

        //TODO: make it so that multiple files can be processed in a row
        partialProgressBar.setMax((int) maxPartialProgress);
        totalProgressBar.setMax(1);

        mainThreadHandler = new Handler();
        startDate = Calendar.getInstance().getTime();

        worker = new ProcessingThread(path, parent_lookup_id, flags);
        worker.start();

        poller = new ProgressPollingThread();
        poller.start();

        updateCurrentPhase();
    }

    void findViewsById() {
        tvCurrentFileName = findViewById(R.id.tvCurrentFileName);
        tvTimePassed = findViewById(R.id.tvTimePassed);
        tvCurrentPhase = findViewById(R.id.tvCurrentPhase);
        partialProgressBar = findViewById(R.id.partialProgressBar);
        totalProgressBar = findViewById(R.id.totalProgressBar);
    }

    void updateProgressBars(int partialProgress, int totalProgress) {
        partialProgressBar.setProgress(partialProgress);
        totalProgressBar.setProgress(totalProgress);
    }

    public void setResult() {
        if (success) setResult(Codes.Result.fileAdded);
        else setResult(Codes.Result.failedToAdd);
    }

    private native int getProcessingProgress();

    class ProcessingThread extends Thread {

        String path;
        int flags;
        long parent_lookup_id;

        ProcessingThread(String pathToFile, long parent_lookup_id, int flags) {
            this.path = pathToFile;
            this.flags = flags;
            this.parent_lookup_id = parent_lookup_id;
        }

        @Override
        public void run() {
            processingDone = false;
            success = false;

            success = compressFile(path, parent_lookup_id, flags);
            processingDone = true;
            mainThreadHandler.post(ProcessingActivity.this::setResult);
        }

        private native boolean compressFile(String pathToFile, long parent_lookup_id, int flags);
    }

    public void updateTimer() {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss:S");

            Date currentDate = Calendar.getInstance().getTime();

            long difference = currentDate.getTime() - startDate.getTime();
            if (difference < 0) {
                Date dateMax = simpleDateFormat.parse("24:00:00:0");
                Date dateMin = simpleDateFormat.parse("00:00:00:0");
                difference = (dateMax.getTime() - startDate.getTime()) + (currentDate.getTime() - dateMin.getTime());
            }

            tvTimePassed.setText(simpleDateFormat.format(new Date(difference)));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateCurrentPhase() {
        /*
        int numberOfPhases = (int) maxPartialProgress;
        if ((flags & 0b1000000000000000) != 0) --numberOfPhases;
        numberOfPhases /= amountOfBlocks;*/
        if (!processingDone) tvCurrentPhase.setText("Working...");
        else if (success) tvCurrentPhase.setText("Success!");
        else tvCurrentPhase.setText("Failure!");
    }

    class ProgressPollingThread extends Thread {

        @Override
        public void run() {
            while (!processingDone) {
                long combinedProgress = getProcessingProgress();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mainThreadHandler.post(() -> updateProgressBars((int) combinedProgress / 100, (int) combinedProgress % 100));
                mainThreadHandler.post(ProcessingActivity.this::updateTimer);

            }
            mainThreadHandler.post(() -> updateProgressBars((int) maxPartialProgress, 1));
            mainThreadHandler.post(ProcessingActivity.this::updateTimer);
            mainThreadHandler.post(ProcessingActivity.this::updateCurrentPhase);
            mainThreadHandler.post(() -> Toast.makeText(ProcessingActivity.this, "Finished", Toast.LENGTH_LONG).show());
        }
    }
}