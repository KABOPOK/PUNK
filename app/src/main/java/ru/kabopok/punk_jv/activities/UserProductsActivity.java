package ru.kabopok.punk_jv.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import ru.kabopok.punk_jv.R;
import ru.kabopok.punk_jv.classes.Product;
import ru.kabopok.punk_jv.classes.ProductAdapter;
import ru.kabopok.punk_jv.classes.User;
import ru.kabopok.punk_jv.current.Online;

public class UserProductsActivity extends AppCompatActivity {

    RecyclerView rvProducts;
    ProductAdapter productAdapter;
    TextView title;
    List<Product> productList = new ArrayList<>();
    Button profileButton;
    User currentUser = Online.getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_products);
        title = findViewById(R.id.title_TextView);
        rvProducts = findViewById(R.id.rvUserProducts);
        profileButton = findViewById(R.id.profile_Button1);
        title.setText("ну, типо, твои товары");
        profileButton.setOnClickListener((v)->{
            Intent profileIntent = new Intent(UserProductsActivity.this, ProfileActivity.class);
            startActivity(profileIntent);
        });
        setData();
        prepareRV();
    }

    private void prepareRV() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rvProducts.setLayoutManager(linearLayoutManager);
        prepareAdapter();
    }

    private void prepareAdapter() {
        productAdapter = new ProductAdapter(productList, this, this::selectedProduct);
        rvProducts.setAdapter(productAdapter);
    }

    private void selectedProduct(Product product) {
        Online.setCurrentProduct(product);
        Intent productIntent = new Intent(UserProductsActivity.this, ProductActivity.class);
        startActivity(productIntent);
    }

    private void setData() {
        List idOdProducts = new ArrayList<String>();
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.child("Users")
                        .child(currentUser.getNumber()).child("UserProducts").getChildren())
                {
                    String productId = postSnapshot.getValue(String.class);
                    idOdProducts.add(productId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(int i = 0; i < idOdProducts.size(); ++i){
                    if(dataSnapshot.child("Products").child(idOdProducts.get(i).toString()).exists()){
                        Product product = dataSnapshot.child("Products").child(idOdProducts.get(i).toString()).getValue(Product.class);
                        productList.add(product);
                    }
                }
                prepareAdapter();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}