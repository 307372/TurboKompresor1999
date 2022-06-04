package com.example.turbokompresor1999;

import java.lang.ref.WeakReference;
import java.nio.file.Path;

public class Archive {
    Folder root_folder = new Folder();
    Path load_path;

    public void pullAllFromArchive() {
        root_folder = pullWholeArchive();
        updateParentInStructures(root_folder, null);
    }

    public void pullFromArchive(boolean recursive) {

    }

    private void updateParentInStructures(ArchiveStructure structure, Folder parent) {
        if (structure == null) return;
        structure.parent = new WeakReference<>(parent);

        if (structure instanceof Folder) {
            Folder folder = (Folder) structure;
            updateParentInStructures(folder.child_dir, folder);
            updateParentInStructures(folder.child_file, folder);
            updateParentInStructures(folder.sibling_dir, parent);
        }
        else if (structure instanceof File) {
            File file = (File) structure;
            updateParentInStructures(file.sibling_file, parent);
        }
    }

    /*public ArchiveStructure findById(long id) {



    }*/

    private native Folder pullWholeArchive();
}
