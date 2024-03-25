package ru.kabopok.punk_jv.fragments;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import ru.kabopok.punk_jv.R;
import ru.kabopok.punk_jv.activities.HomeActivity;
import ru.kabopok.punk_jv.activities.ProductActivity;
import ru.kabopok.punk_jv.activities.ProfileActivity;
import ru.kabopok.punk_jv.classes.LoadingBar;
import ru.kabopok.punk_jv.classes.Photo;
import ru.kabopok.punk_jv.classes.Product;
import ru.kabopok.punk_jv.classes.ProductAdapter;
import ru.kabopok.punk_jv.classes.QuitDialog;
import ru.kabopok.punk_jv.classes.User;
import ru.kabopok.punk_jv.current.Online;

import androidx.appcompat.widget.SearchView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class HomeFragment extends Fragment {

    RecyclerView rvProducts;
    ImageView imageView;
    ProductAdapter productAdapter;
    QuitDialog quitDialog;
    EditText searchView;
    List<Product> productList = new ArrayList<>();

    User currentUser = Online.getCurrentUser();

    Boolean AdapterPrepared = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        rvProducts = view.findViewById(R.id.rvProducts);
        searchView = view.findViewById(R.id.searchView2);
        imageView = view.findViewById(R.id.circleImageView);
        searchView.clearFocus();
        quitDialog = new QuitDialog(getActivity(),getContext());
        //searchView.setQueryHint(Html.fromHtml("<font color = #7A7A7A>" + "find" + "</font>"));
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Perform search as the user types
                String query = s.toString().trim();
                showFilterList(query);
                imageView.setImageResource(R.drawable.ic_close);
                if(query.equals("")){
                    imageView.setImageResource(R.drawable.ic_black_close);
                    searchView.clearFocus();
                    imageView.setImageResource(R.drawable.ic_black_close);
                    View view = getView();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.clearFocus();
                searchView.setText("");
                imageView.setImageResource(R.drawable.ic_black_close);
                View view = getView();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
        setData();
        prepareRV();
        setOnBackPressed();
        return view;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(requireActivity().getCurrentFocus().getWindowToken(), 0);
    }

    private void showFilterList(String newText) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : productList) {
            if (product.getProductName().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(product);
            }
        }
        if (filteredList.isEmpty()) {} else {
            productAdapter.setProductList(filteredList);
        }
    }
    private void prepareRV() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext(),LinearLayoutManager.VERTICAL,false);
        rvProducts.setLayoutManager(linearLayoutManager);
        //prepareAdapter();
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
        addToFavourite(product);
    }
    private void deactivateHeart(Product product) {
        removeFromFavourite(product);
    }

    private void setData() {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!AdapterPrepared) {
                    for (DataSnapshot postSnapshot : dataSnapshot.child("Products").getChildren()) {
                        Product product = postSnapshot.getValue(Product.class);
                        if(product.getBooked()){continue;}
                        ArrayList<String> photos = new ArrayList<>();
                        if(!postSnapshot.child("images").exists()){
                            //Product product = postSnapshot.getValue(Product.class);
                            //deleteInvalidProduct(product);
                            continue;
                        }
                        for (DataSnapshot postSnapshotPhoto : postSnapshot.child("images").getChildren()) {
                            Photo photo = postSnapshotPhoto.getValue(Photo.class);
                            photos.add(photo.getURL());
                        }
                        product.setImagesURLs(photos);
                        productList.add(product);
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

    private void deleteInvalidProduct(Product product) {
        //delete from myProducts
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getNumber()).child("UserProducts").child(product.getProductKey());
        Task<Void> removeTask = rootRef.removeValue();
        removeTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });
        //delete product
        removeTask = FirebaseDatabase.getInstance().getReference("Products").child(product.getProductKey()).removeValue();
        removeTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                final StorageReference storeRef = FirebaseStorage.getInstance().getReference();
                ArrayList<String> photos = product.getImagesPathList();
                for(int i =0; i < photos.size(); ++i) {
                    StorageReference reference = storeRef.child(photos.get(i));
                    reference.delete();
                }
            }
        });
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

    private void setOnBackPressed() {
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                quitDialog.show();
                searchView.clearFocus();
            }
        });
    }
}