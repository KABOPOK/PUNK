package ru.kabopok.punk_jv.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ru.kabopok.punk_jv.R;
import ru.kabopok.punk_jv.classes.ImagesAdapter;
import ru.kabopok.punk_jv.classes.LoadingBar;
import ru.kabopok.punk_jv.classes.Product;
import ru.kabopok.punk_jv.current.Online;

public class ProductActivity extends AppCompatActivity {

    ViewPager viewPager;
    TextView nameOfUser;
    TextView priceOfProduct;
    TextView productInfo;
    Button toUserProfileButton;
    Product currentProduct = Online.getCurrentProduct();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        viewPager = findViewById(R.id.images_ViewPager);
        nameOfUser = findViewById(R.id.nameOfProduct_TextView);
        priceOfProduct = findViewById(R.id.priceOfProduct_TextView);
        productInfo = findViewById(R.id.infoOfProduct_TextView);
        toUserProfileButton = findViewById(R.id.toProfileUser_Button);
        nameOfUser.setText(currentProduct.getProductName());
        priceOfProduct.setText(currentProduct.getProductPrice());
        productInfo.setText(currentProduct.getProductInfo());
        setAdapter();
        toUserProfileButton.setOnClickListener(v -> {
            Intent userIntent = new Intent(ProductActivity.this, UserProfileActivity.class);
            startActivity(userIntent);
        });
    }

    private void setAdapter(){
        ImagesAdapter imagesAdapter = new ImagesAdapter(this,null,currentProduct.getImagesURLs());
        viewPager.setAdapter(imagesAdapter);
    }
}