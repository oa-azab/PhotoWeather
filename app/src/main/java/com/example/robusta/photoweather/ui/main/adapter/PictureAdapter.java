package com.example.robusta.photoweather.ui.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.robusta.photoweather.R;
import com.example.robusta.photoweather.model.Picture;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by robusta on 8/26/18.
 */

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder> {

    private List<Picture> data = new ArrayList<>();
    private PictureCallback callback;

    public PictureAdapter(PictureCallback callback) {
        this.callback = callback;
    }

    public void updateData(List<Picture> newData) {
        data.clear();
        data.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View view = inflater.inflate(R.layout.item_picture, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView pictureImg;

        public ViewHolder(View itemView) {
            super(itemView);
            pictureImg = (ImageView) itemView.findViewById(R.id.item_picture);
            itemView.setOnClickListener(this);
        }

        public void bindData(Picture picture) {
            Picasso.get().load(new File(picture.getImageFilePath())).into(pictureImg);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                Picture picture = data.get(position);
                if (callback != null) callback.onPictureClicked(picture);
            }
        }
    }

    public interface PictureCallback {
        void onPictureClicked(Picture picture);
    }
}
