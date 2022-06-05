package com.example.turbokompresor1999;

import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;

public class File extends ArchiveStructure {
    public static class Flags {
        public static final int BWT =0;
        public static final int MTF=1;
        public static final int RLE=2;
        public static final int AC_order0=3;
        public static final int AC_order1=4;
        public static final int rANS=5;
        // a few flags are unused in this project
        public static final int BlockSizeDiv2=9;
        public static final int BlockSizeDiv4=10;
        public static final int BlockSizeDiv16=11;
        public static final int BlockSizeDiv256=12;
        public static final int SHA_256=13;
        public static final int CRC32=14;
        public static final int SHA_1=15;
    }

    long original_size;             // size of data before compression (in bytes)
    BitSet flags = null;            // 16 flags, indicate compression method
    Long compressed_size = null;    // size of compressed data (in bytes)
    File sibling_file = null;       // ptr to next sibling file in memory

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
        this.flags = BitSet.valueOf(new byte[] {(byte) (flags),(byte) (flags>>8)});
        this.original_size = original_size;
        this.compressed_size = compressed_size != 0 ? compressed_size : null;
    }


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
    }
}
