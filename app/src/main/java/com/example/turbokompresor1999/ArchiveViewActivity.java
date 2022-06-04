package com.example.turbokompresor1999;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Optional;
import java.util.UUID;

class Codes {

    static class Request {
        static final int details = 1234;
        static final int newCrime = 4321;
    }

    static class Result {
        static final int noActionRequired = 999;
        static final int crimeAdded = 1000;
        static final int crimeDeletion = 1001;
        static final int crimeModification = 1002;
    }
}

public class ArchiveViewActivity extends AppCompatActivity {

    static {
        System.loadLibrary("jnitest");
    }

    public static boolean hasPermission(Context context, String permission) {

        int res = context.checkCallingOrSelfPermission(permission);

        Log.v(TAG, "permission: " + permission + " = \t\t" +
                (res == PackageManager.PERMISSION_GRANTED ? "GRANTED" : "DENIED"));

        return res == PackageManager.PERMISSION_GRANTED;

    }

    public static boolean hasPermissions(Context context, String... permissions) {

        boolean hasAllPermissions = true;

        for(String permission : permissions) {
            //you can return false instead of assigning, but by assigning you can log all permission values
            if (! hasPermission(context, permission)) {hasAllPermissions = false; }
        }

        return hasAllPermissions;

    }

    private void checkPermissions() {

        int permissionsCode = 42;
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (!hasPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, permissionsCode);
        }
    }

    RecyclerView recyclerView;
    ArchiveManager manager;
    ArchiveAdapter archiveAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("No archive selected!");
        actionBar.setSubtitle("Load or create new archive to start");
        actionBar.setIcon(R.mipmap.ic_archive_toolbar);   // TODO: tk2k icon goes here
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        checkPermissions();
        System.out.println(stringFromJNI());
        //*
        //ArchiveManager archiveManager = ArchiveManager.getInstance();
        manager = ArchiveManager.getInstance();
        manager.pullArchiveFromCpp();

        System.out.println(manager.archive.root_folder.recursive_string());//*/
        /*Folder f = new Folder();
        f.child_file = Optional.of(new File());
        System.out.println(f.recursive_string());*/

        recyclerView = findViewById(R.id.rv_archiveRecyclerView);
        manager.updateContentOfCurrentFolder();
        archiveAdapter = new ArchiveAdapter(this, manager);

        recyclerView.setAdapter(archiveAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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

    public void onItemClicked(View itemView) {
        ArchiveStructure structure = ((ArchiveStructureView)itemView.getParent()).getData().get();

        if (structure instanceof File) {
            showDetails(itemView);
        } else if (structure instanceof Folder) {
            archiveAdapter.descend((Folder) structure);
        }
    }


    public void showDetails(View itemView) {
        long id = ((ArchiveStructureView)itemView.getParent()).getData().get().lookup_id;
        Intent detailsActivityIntent = new Intent(this, ArchiveStructureDetailsActivity.class);
        detailsActivityIntent.putExtra("lookup_id", id);
        detailsActivityIntent.putExtra("requestCode", Codes.Request.details);

        startActivityForResult(detailsActivityIntent, Codes.Request.details);
    }

    private native String stringFromJNI();

    @Override
    public void onBackPressed() {
        archiveAdapter.ascend();
    }
}