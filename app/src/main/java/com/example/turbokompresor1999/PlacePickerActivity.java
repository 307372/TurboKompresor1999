package com.example.turbokompresor1999;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.nbsp.materialfilepicker.ui.FilePickerActivity;


public class PlacePickerActivity extends FilePickerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addMenuButtonSelectCurrentFolder();
    }


    public void addMenuButtonSelectCurrentFolder() {
        Toolbar t = findViewById(R.id.toolbar);

        Button btn=new Button(this);
        btn.setOnClickListener((View v) ->
            super.onFileClicked(new Fileder(t.getSubtitle().toString())));

        btn.setText("Select");
        Toolbar.LayoutParams l2 = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
        l2.gravity = Gravity.END;
        btn.setLayoutParams(l2);
        t.addView(btn);
    }

    @Override
    public void onFileClicked(final java.io.File clickedFile) {
        if (clickedFile.isDirectory()) {
            super.onFileClicked(clickedFile);
        }
    }

    // If the author of MaterialFilePicker reads this - make your classes more overridable.
    // this heresy below is your fault
    public static class Fileder extends java.io.File {

        public Fileder(@NonNull String pathname) {
            super(pathname);
        }

        @Override
        public boolean isDirectory() {
            return false;
        }
    }

}
