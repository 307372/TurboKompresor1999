package com.example.turbokompresor1999;

public class ArchiveManager {
    private static ArchiveManager instance;
    Folder root_folder;

    private ArchiveManager() {
        root_folder = new Folder();
    }

    public static ArchiveManager getInstance() {
        if (instance == null) {
            instance = new ArchiveManager();
        }
        return instance;
    }

    public void pushArchiveToCpp() {

    }

    public void pullArchiveFromCpp() {

    }

}
