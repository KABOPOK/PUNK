package ru.kabopok.punk_jv.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.kabopok.punk_jv.R;
import ru.kabopok.punk_jv.classes.Product;
import ru.kabopok.punk_jv.classes.ProductAdapter;
import ru.kabopok.punk_jv.current.Online;

public class HomeActivity extends AppCompatActivity {

    RecyclerView rvProducts;
    ProductAdapter productAdapter;
    SearchView searchView;
    List<Product> productList = new ArrayList<>();

    Button profileButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        rvProducts = findViewById(R.id.rvProducts);
        searchView = findViewById(R.id.searchView);
        profileButton = findViewById(R.id.profile_Button);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                showFilterList(newText);
                return false;
            }
        });

        profileButton.setOnClickListener((v)->{
            Intent profileIntent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(profileIntent);
        });
        setData();
        prepareRV();
    }


    private void showFilterList(String newText) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : productList) {
            if (product.getProductName().toLowerCase().contains(newText)) {
                filteredList.add(product);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "раскупили такие", Toast.LENGTH_LONG).show();
        } else {
            productAdapter.setProductList(filteredList);
        }
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
        Intent productIntent = new Intent(HomeActivity.this, ProductActivity.class);
        startActivity(productIntent);
    }

    private void setData() {
        productList.add(new Product("1","1","1","1","1"));
        productList.add(new Product("1","1","1","1","1"));
        productList.add(new Product("1","1","1","1","1"));
    }
}