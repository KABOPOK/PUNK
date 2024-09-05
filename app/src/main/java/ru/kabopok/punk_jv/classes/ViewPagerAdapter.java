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

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import ru.kabopok.punk_jv.R;

public class ViewPagerAdapter extends PagerAdapter {

    Context context;
    ArrayList<String> imagesPaths;
    LayoutInflater layoutInflater;
    ItemClickListener itemClickListener;
    ImageDeletionListener imageDeletionListener;
    boolean hideDeleteButton;

    public ViewPagerAdapter(Context context, ArrayList<String> imagesPaths, ItemClickListener itemClickListener,ImageDeletionListener imageDeletionListener) {
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        this.imagesPaths = imagesPaths;
        this.itemClickListener = itemClickListener;
        this.imageDeletionListener = imageDeletionListener;
    }
    public ViewPagerAdapter(Context context, ArrayList<String> imagesPaths, ItemClickListener itemClickListener,ImageDeletionListener imageDeletionListener, boolean hideDeleteButton) {
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        this.imagesPaths = imagesPaths;
        this.itemClickListener = itemClickListener;
        this.imageDeletionListener = imageDeletionListener;
        this.hideDeleteButton = hideDeleteButton;
    }

    @Override
    public int getCount() {
        return  imagesPaths.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        //TextView text;
        View view = layoutInflater.inflate(R.layout.custom_single_image,container, false);
        ImageView imageView = view.findViewById(R.id.custom_image_ImageView);
        ImageView delete_product = view.findViewById(R.id.delete_product);
        if(hideDeleteButton){
            hideDeleteButton(delete_product);
        }
       // text = view.findViewById(R.id.text);
        Glide.with(context).load(imagesPaths.get(position)).into(imageView);
        Objects.requireNonNull(container).addView(view);

        imageView.setOnClickListener(v -> {
            if(itemClickListener != null) {
                itemClickListener.ItemClick();
            }
        });
        delete_product.setOnClickListener(v -> {
            if(itemClickListener != null && !hideDeleteButton) {
                imageDeletionListener.deleteImage(position);
            }
        });
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

    public interface ItemClickListener{
        void ItemClick();
    }
    public void hideDeleteButton(ImageView delete_product){
        delete_product.setImageResource(0);
    }

    public interface ImageDeletionListener{
         void deleteImage(int position);
    }
}
