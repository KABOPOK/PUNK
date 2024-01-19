package ru.kabopok.punk_jv.activities;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import me.relex.circleindicator.CircleIndicator;
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
    CircleIndicator indicator;
    ImageView pickBack;
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
        indicator = findViewById(R.id.indicator);
        toUserProfileButton = findViewById(R.id.toProfileUser_Button);
        pickBack = findViewById(R.id.pick_back);

        nameOfUser.setText(currentProduct.getProductName());
        ownerProductName.setText(currentProduct.getProductOwnerName());
        priceOfProduct.setText(currentProduct.getProductPrice());
        productInfo.setText(currentProduct.getProductInfo());
        setAdapter();
        toUserProfileButton.setOnClickListener(v -> {
            Intent userIntent = new Intent(ProductActivity.this, UserProfileActivity.class);
            startActivity(userIntent);
        });
        pickBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToProductHome  = new Intent(ProductActivity.this, HomeActivity.class);
                startActivity(backToProductHome);
            }
        });
        setOnBackPressed();
        indicator.setViewPager(viewPager);
    }

    private void setAdapter(){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this,null,currentProduct.getImagesURLs(), this::zoomPicture, this::setImageCounter);
        viewPager.setAdapter(viewPagerAdapter);
    }
    private void zoomPicture(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_zoom);
        ViewPager pager  = dialog.findViewById(R.id.custom_ViewPager_dialog);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this,null,currentProduct.getImagesURLs(),null, this::setImageCounter);
        pager.setAdapter(viewPagerAdapter);

        ImageView closeDialog = dialog.findViewById(R.id.custom_button_dialog);
        CircleIndicator indicator = (CircleIndicator) dialog.findViewById(R.id.indicator);
        indicator.setViewPager(pager);
        //Or this one instead:
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //img.setImageURI(photo);
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void setOnBackPressed() {
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent backToProductHome  = new Intent(ProductActivity.this, HomeActivity.class);
                startActivity(backToProductHome);
            }
        });
    }

    void setImageCounter(int current, int amount){
    }
}