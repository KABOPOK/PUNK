package ru.kabopok.punk_jv.classes;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.kabopok.punk_jv.R;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private ArrayList<Uri> uriArrayList;

    public ImageAdapter(ArrayList<Uri> uriArrayList) {
        this.uriArrayList = uriArrayList;
    }

    @NonNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.custom_single_image, parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ViewHolder holder, int position) {
        holder.imageView.setImageURI(uriArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return uriArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.custom_image_ImageView);
        }
    }
}
