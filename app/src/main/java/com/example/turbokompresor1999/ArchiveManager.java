package com.example.turbokompresor1999;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ArchiveManager {
    private static ArchiveManager instance;
    Archive archive;
    ArrayList<WeakReference<ArchiveStructure>> contentOfCurrentFolder;
    WeakReference<Folder> currentFolder;

    private ArchiveManager() {
        clear();
    }

    public void clear()
    {
        archive = null;
        contentOfCurrentFolder = new ArrayList<>();
        currentFolder = new WeakReference<>(null);
    }

    public static ArchiveManager getInstance() {
        if (instance == null) {
            instance = new ArchiveManager();
        }
        return instance;
    }

    public boolean isArchiveOpen() {
        return archive != null;
    }

    public void updateContentOfCurrentFolder() {
        if(currentFolder.get() != null)
            contentOfCurrentFolder = currentFolder.get().getContent();
    }

    public ArrayList<WeakReference<ArchiveStructure>> getContentOfCurrentFolder()
    {
        return contentOfCurrentFolder;
    }

    public void pushArchiveToCpp() {

    }

    public ArchiveStructure getStructureFromCurrentContent(long id) {
        for (WeakReference<ArchiveStructure> ptr : contentOfCurrentFolder){
            ArchiveStructure curr = ptr.get();
            if (curr != null) {
                if (curr.lookup_id == id) {
                    return curr;
                }
            }
        }
        return null;
    }

    public void pullArchiveFromCpp() {
        archive = new Archive();
        contentOfCurrentFolder = new ArrayList<>();
        archive.pullAllFromArchive();
        currentFolder = new WeakReference<>(archive.root_folder);
        updateContentOfCurrentFolder();
    }

    public void changeCurrentFolder(Folder folder){
        currentFolder = new WeakReference<>(folder);
        updateContentOfCurrentFolder();
    }

}
