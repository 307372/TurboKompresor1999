package com.example.turbokompresor1999;

import java.lang.ref.WeakReference;

public class ArchiveStructure {
    public String name = "";
    public Long lookup_id = null;
    public String path = null;
    WeakReference<Folder> parent = new WeakReference<>(null);

    void setName(String p_name) {name = p_name;}
    void setLookupId(long p_lookup_id) {lookup_id = p_lookup_id;}
    void setPath(String p_path) {path = p_path;}
    void setParent(WeakReference<Folder> p_parent) {parent = p_parent;}
}
