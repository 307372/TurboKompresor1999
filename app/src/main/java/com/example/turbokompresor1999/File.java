package com.example.turbokompresor1999;

import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;

public class File {
    public String name;
    long original_size;   // size of data before compression (in bytes)

    BitSet flags = null;   // 16 flags, indicate compression method
    String path = null;
    Long compressed_size = null; // size of compressed data (in bytes)
    File sibling_file = null;  // ptr to next sibling file in memory
    Long lookup_id = null;
    WeakReference<Folder> parent = new WeakReference<>(null);

    File(){}
    File(byte[] name,
         long lookup_id,
         byte[] path,
         short flags,
         long original_size,
         long compressed_size,
         File sibling_file)
    {
        this.name = new String(name, StandardCharsets.UTF_8);
        this.lookup_id = lookup_id != 0 ? lookup_id : null;
        this.path = new String(path, StandardCharsets.UTF_8);
        this.sibling_file = sibling_file;
        this.flags = BitSet.valueOf(new byte[] {(byte) flags,(byte) (flags << 8)});
        this.original_size = original_size;
        this.compressed_size = compressed_size != 0 ? compressed_size : null;
    }

//*
    @Override public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("File " + name + "\n");
        builder.append((path != null ? "At: " + path : "No path avaliable") + "\n");
        builder.append("Original size: " + original_size + "\n");
        builder.append((compressed_size != null ? "Compressed size: " + compressed_size : "Compressed size unknown") + "\n");
        builder.append((flags != null ? "Flags: " + flags : "No flags") + "\n");
        builder.append((parent.get() != null ? "In folder: " + parent.get().name : "Not in any folder, somehow") + "\n");
        builder.append((sibling_file != null ? "Next file: " + sibling_file.name : "No next file") + "\n");
        builder.append((lookup_id != null ? "Lookup id value: lookup_id" : "No lookup id yet") + "\n");

        return builder.toString();
    }

    public String recursive_string() {
        return "\n" + this + (sibling_file != null ? sibling_file.recursive_string() : "");
    }//*/
}
