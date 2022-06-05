package com.example.turbokompresor1999;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

public class ProcessingActivity extends AppCompatActivity {

    String path;
    short flags;
    long parent_lookup_id;
    ProcessingThread worker;
    boolean success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing);

        Bundle extras = getIntent().getExtras();
        path = extras.getString("pathToFile");
        flags = extras.getShort("flags");
        parent_lookup_id = ArchiveManager.getInstance().currentFolder.get().lookup_id;

        worker = new ProcessingThread(path, parent_lookup_id, flags);
        worker.start();
    }

    private void notifyProcessingDone() {
        /*

        ctx.archiveAdapter.notifyDataSetChanged();*/
    }

    class ProcessingThread extends Thread {

        String path;
        short flags;
        long parent_lookup_id;

        ProcessingThread(String pathToFile, long parent_lookup_id, short flags) {
            this.path = pathToFile;
            this.flags = flags;
            this.parent_lookup_id = parent_lookup_id;
        }

        @Override
        public void run() {
            success = compressFile(path, parent_lookup_id, flags);
            ArchiveManager.getInstance().pullArchiveFromCpp();
        }

        private native boolean compressFile(String pathToFile, long parent_lookup_id, short flags);
    }
}