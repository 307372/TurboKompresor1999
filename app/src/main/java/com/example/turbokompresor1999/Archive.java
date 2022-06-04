package com.example.turbokompresor1999;

import java.nio.file.Path;

public class Archive {
    Folder root_folder;
    Path load_path;

    public void pullFromArchiveRecursive() {
        root_folder = pullWholeArchive();
    }

    public void pullFromArchive(boolean recursive) {

    }

    private native Folder pullWholeArchive();
}
