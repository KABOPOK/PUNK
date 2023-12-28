package ru.kabopok.punk_jv.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ru.kabopok.punk_jv.R;
import ru.kabopok.punk_jv.classes.ViewPagerAdapter;
import ru.kabopok.punk_jv.classes.Product;
import ru.kabopok.punk_jv.current.Online;

public class ProductActivity extends AppCompatActivity {

    ViewPager viewPager;
    TextView nameOfUser;
    TextView ownerProductName;
    TextView priceOfProduct;
    TextView productInfo;
    Button toUserProfileButton;
    Product currentProduct = Online.getCurrentProduct();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        viewPager = findViewById(R.id.images_ViewPager);
        ownerProductName = findViewById(R.id.ownerProductName_TextView);
        nameOfUser = findViewById(R.id.nameOfProduct_TextView);
        priceOfProduct = findViewById(R.id.priceOfProduct_TextView);
        productInfo = findViewById(R.id.infoOfProduct_TextView);
        toUserProfileButton = findViewById(R.id.toProfileUser_Button);
        nameOfUser.setText(currentProduct.getProductName());
        ownerProductName.setText(currentProduct.getProductOwnerName());
        priceOfProduct.setText(currentProduct.getProductPrice());
        productInfo.setText(currentProduct.getProductInfo());
        setAdapter();
        toUserProfileButton.setOnClickListener(v -> {
            Intent userIntent = new Intent(ProductActivity.this, UserProfileActivity.class);
            startActivity(userIntent);
        });
    }

    private void setAdapter(){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this,null,currentProduct.getImagesURLs(),null, this::zoomPicture);
        viewPager.setAdapter(viewPagerAdapter);
    }
    private void zoomPicture(String URL){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_zoom);
        ImageView img  = dialog.findViewById(R.id.custom_image_dialog);
        Button closeDialog = dialog.findViewById(R.id.custom_button_dialog);
        //Or this one instead:
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Picasso.with(this).load(URL).into(img);
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}