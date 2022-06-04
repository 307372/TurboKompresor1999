package com.example.turbokompresor1999;

import static com.example.turbokompresor1999.ArchiveStructureDetailsActivity.scaleBytesToBiggerUnit;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class ArchiveStructureView extends LinearLayout {
    ImageView imageView;
    TextView titleView;
    TextView dateView;

    private WeakReference<ArchiveStructure> data;

    public ArchiveStructureView(Context context) {
        super(context);

        this.setOrientation(HORIZONTAL);

        inflate(getContext(), R.layout.archive_structure_view_layout, this);

        imageView = findViewById(R.id.imageView);
        titleView = findViewById(R.id.titleView);
        dateView = findViewById(R.id.dateView);
    }

    public void update(ArchiveStructure data) {
        this.data = new WeakReference<>(data);
        if (data instanceof File) {
            File file = (File) data;
            titleView.setText(file.name);
            dateView.setText("Original size: " + scaleBytesToBiggerUnit(file.original_size));
            imageView.setImageResource(R.mipmap.ic_file_foreground);
        }
        else if (data instanceof Folder) {
            Folder folder = (Folder) data;
            titleView.setText(data.name);
            imageView.setImageResource(R.mipmap.ic_folder_foreground);
            dateView.setText(folder.isEmpty() ? "Empty" : "");
        }
    }

    WeakReference<ArchiveStructure> getData() {
        return data;
    }
}
