package ru.kabopok.punk_jv.classes;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.Objects;

import ru.kabopok.punk_jv.R;

public class ImagesAdapter extends PagerAdapter {

    Context context;
    ArrayList<Uri> imagesUris;
    LayoutInflater layoutInflater;

    public ImagesAdapter(Context context, ArrayList<Uri> imagesUris) {
        this.context = context;
        this.imagesUris = imagesUris;
        this.layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return imagesUris.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        TextView text;
        View view = layoutInflater.inflate(R.layout.custom_single_image,container, false);
        ImageView imageView = view.findViewById(R.id.custom_image_ImageView);
        text = view.findViewById(R.id.text);
        text.setText(String.valueOf(position+1) + "/" + String.valueOf(imagesUris.size()));
        imageView.setImageURI(imagesUris.get(position));
        Objects.requireNonNull(container).addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //container.removeView((ConstraintLayout)object);
    }
}
