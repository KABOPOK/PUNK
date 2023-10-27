package ru.kabopok.punk_jv.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.kabopok.punk_jv.R;
import ru.kabopok.punk_jv.activities.HomeActivity;
import ru.kabopok.punk_jv.activities.ProductActivity;
import ru.kabopok.punk_jv.activities.ProfileActivity;
import ru.kabopok.punk_jv.classes.Product;
import ru.kabopok.punk_jv.classes.ProductAdapter;
import ru.kabopok.punk_jv.current.Online;

import androidx.appcompat.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {

    RecyclerView rvProducts;
    ProductAdapter productAdapter;
    SearchView searchView;
    List<Product> productList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        rvProducts = view.findViewById(R.id.rvProducts);
        searchView = view.findViewById(R.id.searchView);
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
        setData();
        prepareRV();
        return view;
    }

    private void showFilterList(String newText) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : productList) {
            if (product.getProductName().toLowerCase().contains(newText)) {
                filteredList.add(product);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(this.getContext(), "раскупили такие", Toast.LENGTH_LONG).show();
        } else {
            productAdapter.setProductList(filteredList);
        }
    }
    private void prepareRV() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext(),LinearLayoutManager.VERTICAL,false);
        rvProducts.setLayoutManager(linearLayoutManager);
        prepareAdapter();
    }

    private void prepareAdapter() {
        productAdapter = new ProductAdapter(productList, this.getContext(), this::selectedProduct,
                this::activateHeart, this::deactivateHeart);
        rvProducts.setAdapter(productAdapter);
    }

    private void selectedProduct(Product product) {
        Online.setCurrentProduct(product);
        Intent productIntent = new Intent(this.getContext(), ProductActivity.class);
        startActivity(productIntent);
    }

    private void activateHeart(Product product){
        //Toast.makeText(this,"work",Toast.LENGTH_LONG);
    }
    private void deactivateHeart(Product product){
        //Toast.makeText(this,"work", Toast.LENGTH_LONG);
    }

    private void setData() {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.child("Products").getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    productList.add(product);
                }
                prepareAdapter();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}