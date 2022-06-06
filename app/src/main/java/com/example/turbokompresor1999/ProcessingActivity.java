package com.example.turbokompresor1999;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
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
    Handler progressHandler;
    Date startDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing);

        tvCurrentFileName = findViewById(R.id.tvCurrentFileName);
        tvTimePassed = findViewById(R.id.tvTimePassed);
        tvCurrentPhase = findViewById(R.id.tvCurrentPhase);
        partialProgressBar = findViewById(R.id.partialProgressBar);
        totalProgressBar = findViewById(R.id.totalProgressBar);

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

        progressHandler = new Handler();
        startDate = Calendar.getInstance().getTime();

        worker = new ProcessingThread(path, parent_lookup_id, flags);
        worker.start();

        poller = new ProgressPollingThread();
        poller.start();

        updateCurrentPhase();
    }

    void updateProgressBars(int partialProgress, int totalProgress) {
        partialProgressBar.setProgress(partialProgress);
        totalProgressBar.setProgress(totalProgress);
        tvCurrentPhase.setText("partial=" + partialProgress + ", total=" + totalProgress);
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
            if (success) ArchiveManager.getInstance().pullArchiveFromCpp();
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
            int days = (int) (difference / (1000 * 60 * 60 * 24));
            int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
            int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
            int sec = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours) - (1000 * 60 * min)) / 1000;
            int ms = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours) - (1000 * 60 * min) - 1000 * sec);


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
                progressHandler.post(() -> updateProgressBars((int) combinedProgress / 100, (int) combinedProgress % 100));
                progressHandler.post(ProcessingActivity.this::updateTimer);

            }
            progressHandler.post(() -> updateProgressBars((int) maxPartialProgress, 1));
            progressHandler.post(ProcessingActivity.this::updateTimer);
            progressHandler.post(ProcessingActivity.this::updateCurrentPhase);
            progressHandler.post(() -> Toast.makeText(ProcessingActivity.this, "Finished", Toast.LENGTH_LONG).show());
        }
    }
}