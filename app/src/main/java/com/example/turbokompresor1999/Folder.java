package com.example.turbokompresor1999;

import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;

public class Folder {
    public String name = null;                             // folder's name
    public Long lookup_id = null;
    public String path = null;                            // extraction path
    public Folder child_dir = null;                     // ptr to first subfolder in memory
    public Folder sibling_dir = null;                   // ptr to next sibling folder in memory
    public File child_file = null;                      // ptr to first file in memory
    public WeakReference<Folder> parent = new WeakReference<>(null);// ptr to parent folder in memory

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
