package ru.kabopok.punk_jv.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ru.kabopok.punk_jv.R;
import ru.kabopok.punk_jv.activities.ProductActivity;
import ru.kabopok.punk_jv.activities.ProfileActivity;
import ru.kabopok.punk_jv.activities.UserProductsActivity;
import ru.kabopok.punk_jv.classes.Photo;
import ru.kabopok.punk_jv.classes.Product;
import ru.kabopok.punk_jv.classes.ProductAdapter;
import ru.kabopok.punk_jv.classes.QuitDialog;
import ru.kabopok.punk_jv.classes.User;
import ru.kabopok.punk_jv.current.Online;

public class UserProductsFragment extends Fragment {
    RecyclerView rvProducts;
    ProductAdapter productAdapter;
    TextView title;
    QuitDialog quitDialog;
    List<Product> productList = new ArrayList<>();
    User currentUser = Online.getCurrentUser();

    Boolean AdapterPrepared = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_products, container, false);
        title = view.findViewById(R.id.title_TextView);
        rvProducts = view.findViewById(R.id.rvUserProducts);
        quitDialog = new QuitDialog(getActivity(),getContext());
        title.setText("ну, типо, твои товары");
        setData();
        prepareRV();
        setOnBackPressed();
        return view;
    }

    private void prepareRV() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext(),LinearLayoutManager.VERTICAL,false);
        rvProducts.setLayoutManager(linearLayoutManager);
        //prepareAdapter();
    }

    private void prepareAdapter() {
        productAdapter = new ProductAdapter(productList, this.getContext(), this::selectedProduct,true);
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
                if(!AdapterPrepared) {
                    for (int i = 0; i < idOdProducts.size(); ++i) {
                        if (dataSnapshot.child("Products").child(idOdProducts.get(i).toString()).exists()) {
                            Product product = dataSnapshot.child("Products").child(idOdProducts.get(i).toString()).getValue(Product.class);
                            ArrayList<String> photos = new ArrayList<>();
                            for (DataSnapshot postSnapshotPhoto : dataSnapshot.child("Products").child(idOdProducts.get(i).toString()).child("images").getChildren()) {
                                Photo photo = postSnapshotPhoto.getValue(Photo.class);
                                photos.add(photo.getURL());
                                product.pushImagesPath(photo.getCloudPath());
                            }
                            product.setImagesURLs(photos);
                            productList.add(product);
                        }
                    }
                    prepareAdapter();
                    AdapterPrepared = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void setOnBackPressed() {
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                quitDialog.show();
            }
        });
    }
}