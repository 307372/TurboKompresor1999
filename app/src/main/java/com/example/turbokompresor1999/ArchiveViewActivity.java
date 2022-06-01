package com.example.turbokompresor1999;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ArchiveViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("No archive selected!");
        actionBar.setSubtitle("Load or create new archive to start");
        actionBar.setIcon(R.drawable.ic_extract);   // TODO: tk2k icon goes here
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        ArchiveManager archiveManager = new ArchiveManager();


        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.archive_view_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actAddFile:
                startActivity(new Intent(ArchiveViewActivity.this, ProcessingPreparationActivity.class));
                break;
            case R.id.actExtract:
                break;
            case R.id.actLoadArchive:
                break;
            case R.id.actNewArchive:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}