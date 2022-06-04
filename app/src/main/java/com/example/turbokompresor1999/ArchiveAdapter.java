package com.example.turbokompresor1999;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ArchiveAdapter extends RecyclerView.Adapter<ArchiveAdapter.ArchiveViewHolder>{

    public static final class ViewType {
        public static final int FILEVIEW=100;
        public static final int FOLDERVIEW=200;
    }

    Context context;
    ArchiveManager archiveManager;

    public ArchiveAdapter(Context ctx, ArchiveManager manager) {
        context = ctx;
        archiveManager = manager;
    }

    @NonNull
    @Override
    public ArchiveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //if (viewType == ViewType.FILEVIEW) {
            ArchiveStructureView itemView = new ArchiveStructureView(parent.getContext());

            itemView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));

            return new ArchiveViewHolder(itemView);
        /*}
        else if (viewType == ViewType.FOLDERVIEW) {

        }*/
    }

    @Override
    public void onBindViewHolder(@NonNull ArchiveViewHolder holder, int position) {
        holder.structureView.update(archiveManager.getContentOfCurrentFolder().get(position).get());
    }

    @Override
    public int getItemCount() {
        return archiveManager.getContentOfCurrentFolder().size();
    }

    public class ArchiveViewHolder extends RecyclerView.ViewHolder {
        ArchiveStructureView structureView;

        public ArchiveViewHolder(@NonNull View itemView) {
            super(itemView);
            structureView = (ArchiveStructureView) itemView;
        }
    }

    public void descend(Folder folder) {
        archiveManager.changeCurrentFolder(folder);
        notifyDataSetChanged();
    }

    public void ascend() {
        Folder parent = archiveManager.currentFolder.get().parent.get();
        if (parent != null) {
            archiveManager.changeCurrentFolder(parent);
            notifyDataSetChanged();
        }
    }

}
