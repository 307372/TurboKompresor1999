package com.example.turbokompresor1999;

import static com.example.turbokompresor1999.PermissionRequester.checkPermissions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

class Codes {

    static class Request {
        static final int addFile = 9999;
        static final int details = 1234;
        static final int newCrime = 4321;
        static final int FILE_PICKER_REQUEST_CODE = 1337;
    }
    static class Result {
        static final int fileAdded = 997;
        static final int failedToAdd = 998;
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



    RecyclerView recyclerView;
    ArchiveManager manager;
    ArchiveAdapter archiveAdapter;
    MaterialFilePicker picker;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case Codes.Request.FILE_PICKER_REQUEST_CODE:
                if (resultCode == RESULT_OK)
                {
                    String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
                    startFileProcessingPreparationActivity(filePath);
                }
                break;

            case Codes.Request.addFile:
                if (resultCode == Codes.Result.fileAdded)
                {
                    manager.pullArchiveAndUpdate();
                    archiveAdapter.notifyDataSetChanged(); //TODO: optimise this if needed
                }
                break;
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

        checkPermissions(this);
        manager = ArchiveManager.getInstance();

        //load archive by hand
        System.out.println(stringFromJNI());
        manager.pullArchiveAndUpdate();


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

        startActivityForResult(intent, Codes.Request.addFile);
    }

    private native String stringFromJNI();

    @Override
    public void onBackPressed() {
        archiveAdapter.ascend();
    }
}