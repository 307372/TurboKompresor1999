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
import android.widget.Toast;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

class Codes {

    static class Request {
        static final int details = 1234;
        static final int newCrime = 4321;
        static final int FILE_PICKER_REQUEST_CODE=1337;
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
    MaterialFilePicker picker;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Codes.Request.FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            startFileProcessingPreparationActivity(filePath);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rv_archiveRecyclerView);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.mipmap.ic_archive_toolbar);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        checkPermissions();
        manager = ArchiveManager.getInstance();

        //load archive by hand
        System.out.println(stringFromJNI());
        manager.pullArchiveFromCpp();


        //*
        //ArchiveManager archiveManager = ArchiveManager.getInstance();

        //System.out.println(manager.archive.root_folder.recursive_string());//*/
        /*Folder f = new Folder();
        f.child_file = Optional.of(new File());
        System.out.println(f.recursive_string());*/

        archiveAdapter = new ArchiveAdapter(this, manager);

        recyclerView.setAdapter(archiveAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        updateActionBarText();
    }

    public void updateActionBarText() {
        ActionBar actionBar = getSupportActionBar();
        if (manager.isArchiveOpen()) {
            actionBar.setTitle(manager.archive.root_folder.name);

            actionBar.setSubtitle(manager.archive.load_path != null ? manager.archive.load_path.toString() : "");
        } else {
            actionBar.setTitle("No archive selected!");
            actionBar.setSubtitle("Load or create new archive to start");
        }
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
                //launchFilePicker();

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

    public void launchFilePicker(View v) {
        picker = new MaterialFilePicker();
        picker.withActivity(this)
                .withCloseMenu(true)
                .withRootPath("/storage")
                .withPath("/storage")//String.valueOf(FileSystems.getDefault().getPath("/")))
                .withHiddenFiles(true)
                .withTitle("Select a file to add it")
                .withRequestCode(Codes.Request.FILE_PICKER_REQUEST_CODE)
                .start();
    }


    public void showDetails(View itemView) {
        long id = ((ArchiveStructureView)itemView.getParent()).getData().get().lookup_id;
        Intent detailsActivityIntent = new Intent(this, ArchiveStructureDetailsActivity.class);
        detailsActivityIntent.putExtra("lookup_id", id);
        detailsActivityIntent.putExtra("requestCode", Codes.Request.details);

        startActivityForResult(detailsActivityIntent, Codes.Request.details);
    }

    public void startFileProcessingPreparationActivity(String pathToFile) {
        Intent intent = new Intent(ArchiveViewActivity.this, ProcessingPreparationActivity.class);
        intent.putExtra("pathToFile", pathToFile);

        startActivity(intent);
    }

    private native String stringFromJNI();

    @Override
    public void onBackPressed() {
        archiveAdapter.ascend();
    }
}