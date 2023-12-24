package ru.kabopok.punk_jv.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.kabopok.punk_jv.R;
import ru.kabopok.punk_jv.activities.ProductActivity;
import ru.kabopok.punk_jv.classes.Photo;
import ru.kabopok.punk_jv.classes.Product;
import ru.kabopok.punk_jv.classes.ProductAdapter;
import ru.kabopok.punk_jv.classes.User;
import ru.kabopok.punk_jv.current.Online;

public class FavouriteProductsFragment extends Fragment {
    RecyclerView rvProducts;
    ProductAdapter productAdapter;
    TextView title;
    List<Product> productList = new ArrayList<>();
    User currentUser = Online.getCurrentUser();
    Boolean AdapterPrepared = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_favourite_products, container, false);
        title = view.findViewById(R.id.title_TextView);
        rvProducts = view.findViewById(R.id.rvUserProducts);
        title.setText("ну, типо, твои товары");
        setData();
        prepareRV();

        return view;
    }
    private void prepareRV() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext(),LinearLayoutManager.VERTICAL,false);
        rvProducts.setLayoutManager(linearLayoutManager);
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
    private void setData() {
        List idOdProducts = new ArrayList<String>();
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.child("Users")
                        .child(currentUser.getNumber()).child("FavouriteProducts").getChildren())
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
                if(!AdapterPrepared) {
                    for (int i = 0; i < idOdProducts.size(); ++i) {
                        if (dataSnapshot.child("Products").child(idOdProducts.get(i).toString()).exists()) {
                            Product product = dataSnapshot.child("Products").child(idOdProducts.get(i).toString()).getValue(Product.class);
                            ArrayList<String> photos = new ArrayList<>();
                            for (DataSnapshot postSnapshotPhoto : dataSnapshot.child("Products").child(idOdProducts.get(i).toString()).child("images").getChildren()) {
                                Photo photo = postSnapshotPhoto.getValue(Photo.class);
                                photos.add(photo.getURL());
                            }
                            product.setImagesURLs(photos);
                            productList.add(product);
                        }
                        else {
                            Task<Void> removeTask = rootRef.child("Users").child(currentUser.getNumber()).child("FavouriteProducts").child(idOdProducts.get(i).toString()).removeValue();
                            removeTask.addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            });
                        }
                    }
                    prepareAdapter();
                    AdapterPrepared=true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void activateHeart(Product product){
        addToFavourite(product);
    }
    private void deactivateHeart(Product product) {
        removeFromFavourite(product);
    }
    private void addToFavourite(Product product) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("Users").child(currentUser.getNumber()).exists()){
                    HashMap<String, Object> currentProductName = new HashMap<>();
                    currentProductName.put(product.getProductKey(), product.getProductKey());
                    rootRef.child("Users").child(currentUser.getNumber()).child("FavouriteProducts").updateChildren(currentProductName);
                }
                else{
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void removeFromFavourite(Product product) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getNumber()).child("FavouriteProducts").child(product.getProductKey());
        Task<Void> removeTask = rootRef.removeValue();
        removeTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                product.getProductOwner();
            }
        });
    }
}