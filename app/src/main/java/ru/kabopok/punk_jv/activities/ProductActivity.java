package ru.kabopok.punk_jv.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ru.kabopok.punk_jv.R;
import ru.kabopok.punk_jv.classes.Product;
import ru.kabopok.punk_jv.current.Online;

public class ProductActivity extends AppCompatActivity {

    ImageView photoOfProduct;
    TextView nameOfUser;
    TextView priceOfProduct;
    TextView productInfo;
    Button toUserProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        photoOfProduct = findViewById(R.id.photoOfProduct_ImageView);
        nameOfUser = findViewById(R.id.nameOfProduct_TextView);
        priceOfProduct = findViewById(R.id.priceOfProduct_TextView);
        productInfo = findViewById(R.id.infoOfProduct_TextView);
        toUserProfileButton = findViewById(R.id.toProfileUser_Button);

        Product currentProduct = Online.getCurrentProduct();
        Picasso.with(this).load(currentProduct.getURL()).into(photoOfProduct);
        nameOfUser.setText(currentProduct.getProductName());
        priceOfProduct.setText(currentProduct.getProductPrice());
        productInfo.setText(currentProduct.getProductInfo());
    }
}