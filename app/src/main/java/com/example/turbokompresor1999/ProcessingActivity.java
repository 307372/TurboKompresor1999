package com.example.turbokompresor1999;

import static com.example.turbokompresor1999.ProcessingPreparationActivity.getHowMuchProgressIs100Percent;

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
    Thread worker;
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

    File fileToExtract;
    int requestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing);
        findViewsById();

        Bundle extras = getIntent().getExtras();
        requestCode = extras.getInt("requestCode");

        if (requestCode == Codes.Request.addFile) {
            path = extras.getString("pathToFile");
            amountOfBlocks = extras.getLong("amountOfBlocks");
            flags = extras.getInt("flags");
            maxPartialProgress = extras.getLong("partialProgressFor100Percent");
            parent_lookup_id = ArchiveManager.getInstance().currentFolder.get().lookup_id;
        } else if (requestCode == Codes.Request.extractFile) {
            path = extras.getString("outputFolderPath");
            fileToExtract = (File) ArchiveManager.getInstance()
                    .getStructureFromCurrentContent(extras.getLong("lookup_id"));
            maxPartialProgress = getHowMuchProgressIs100Percent(fileToExtract.flags, fileToExtract.original_size);
        }

        tvCurrentFileName.setText(new java.io.File(path).getName());


        //TODO: make it so that multiple files can be processed in a row
        partialProgressBar.setMax((int) maxPartialProgress);
        totalProgressBar.setMax(1);

        mainThreadHandler = new Handler();
        startDate = Calendar.getInstance().getTime();

        if (requestCode == Codes.Request.addFile) {
            worker = new CompressionThread(path, parent_lookup_id, flags);
            worker.start();
        } else if (requestCode == Codes.Request.extractFile)
        {
            worker = new DecompressionThread(path, fileToExtract.lookup_id);
            worker.start();
        }

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
        if (requestCode == Codes.Request.addFile) {
            if (success) setResult(Codes.Result.fileAdded);
            else setResult(Codes.Result.failedToAdd);
        } else if (requestCode == Codes.Request.extractFile) {
            if (success) setResult(Codes.Result.extractedSuccessfuly);
            else setResult(Codes.Result.failedToExtract);
        }
    }

    private native int getProcessingProgress();

    class CompressionThread extends Thread {

        String path;
        int flags;
        long parent_lookup_id;

        CompressionThread(String pathToFile, long parent_lookup_id, int flags) {
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

    class DecompressionThread extends Thread {
        String outputFolderPath;
        long lookup_id;

        DecompressionThread(String outputFolderPath, long lookup_id) {
            this.outputFolderPath = outputFolderPath;
            this.lookup_id = lookup_id;
        }

        @Override
        public void run() {
            processingDone = false;
            success = false;

            success = decompressStructure(outputFolderPath, lookup_id);
            processingDone = true;
            mainThreadHandler.post(ProcessingActivity.this::setResult);
        }

        private native boolean decompressStructure(String outputPath, long lookup_id);
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