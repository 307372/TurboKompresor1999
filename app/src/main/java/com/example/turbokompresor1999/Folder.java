package com.example.turbokompresor1999;

import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Folder extends ArchiveStructure {
    // path = extraction path
    public Folder child_dir = null;                     // ptr to first subfolder in memory
    public Folder sibling_dir = null;                   // ptr to next sibling folder in memory
    public File child_file = null;                      // ptr to first file in memory

    Folder(){}
    Folder(byte[] name,
           long lookup_id,
           byte[] path,
           Folder child_dir,
           Folder sibling_dir,
           File child_file)
    {
        this.name = new String(name, StandardCharsets.UTF_8);
        this.lookup_id = lookup_id != 0 ? lookup_id : null;
        this.path = new String(path, StandardCharsets.UTF_8);
        this.child_dir = child_dir;
        this.sibling_dir = sibling_dir;
        this.child_file = child_file;
    }

    public boolean isEmpty() {
        return child_file == null && child_dir == null;
    }

    public ArrayList<WeakReference<ArchiveStructure>> getContent() {
        ArrayList<WeakReference<ArchiveStructure>> content = new ArrayList<>();

        Folder nextSibling = child_dir; // child dir goes inside current folder
        while(nextSibling != null) {
            content.add(new WeakReference<>(nextSibling));
            nextSibling = nextSibling.sibling_dir; // then we get all folders inside current folder
        }

        File nextFile = child_file;
        while(nextFile != null) {
            content.add(new WeakReference<>(nextFile));
            nextFile = nextFile.sibling_file;
        }

        return content;
    }

    @Override public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("Folder " + (name != null ? name : "") + "\n");
        builder.append(((path != null ? "At: " + path : "No path avaliable")) + "\n");
        builder.append((parent.get() != null ? "In folder: " + parent.get().name : "This is the root folder") + "\n");
        builder.append((child_dir != null ? "Next folder: " + child_dir.name : "Contains no folder") + "\n");
        builder.append((sibling_dir != null ? "Next folder: " + sibling_dir.name : "No next folder") + "\n");
        builder.append((child_file != null ? "Next file: " + child_file.name : "No next file") + "\n");
        builder.append((lookup_id != null ? "Lookup id value: lookup_id" : "No lookup id yet") + "\n");

        return builder.toString();
    }

    public String recursive_string() {
        return "\n" + this
                + (child_dir != null ? child_dir.recursive_string() : "")
                + (sibling_dir != null ? sibling_dir.recursive_string() : "")
                + (child_file != null ? child_file.recursive_string() : "");
    }
}
