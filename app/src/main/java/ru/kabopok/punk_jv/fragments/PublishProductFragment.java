package ru.kabopok.punk_jv.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import ru.kabopok.punk_jv.R;
import ru.kabopok.punk_jv.classes.ViewPagerAdapter;
import ru.kabopok.punk_jv.classes.LoadingBar;
import ru.kabopok.punk_jv.classes.User;
import ru.kabopok.punk_jv.current.Online;
import ru.kabopok.punk_jv.databinding.FragmentPublishProductBinding;

public class PublishProductFragment extends Fragment {

    FragmentPublishProductBinding binding;
    StorageReference storageReference;
    private EditText productTitle;
    private EditText productPrice;
    private ImageView test;
    private EditText productInfo;
    private Button PushProduct;
    private StorageReference StoreRef;
    private Uri uriOfImg;
    private String imgUrl;
    private int counter;
    private User currentUser = Online.getCurrentUser();
    private ViewPager viewPager;
    ArrayList<Uri> uriArrayList = new ArrayList<>();
    private String key;
    Button pick;
    private final int REQUEST_PERMISSION_CODE = 35;
    private final int PICK_IMAGE_CODE = 39;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_publish_product, container, false);
        storageReference = FirebaseStorage.getInstance().getReference();
        StoreRef = FirebaseStorage.getInstance().getReference("Image");
        productTitle = (EditText) view.findViewById(R.id.product_title);
        productPrice = (EditText) view.findViewById(R.id.product_price);
        productInfo = (EditText) view.findViewById(R.id.product_info);
        PushProduct = (Button) view.findViewById(R.id.push_product_button);
        viewPager = view.findViewById(R.id.images_ViewPager);
        pick = view.findViewById(R.id.pick);
        binding = FragmentPublishProductBinding.inflate(getLayoutInflater());

        final LoadingBar loadingBar = new LoadingBar(this.getActivity());

        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkUserPermission();
                 activityResultLauncher.launch("image/*");
            }
        });
        PushProduct.setOnClickListener(v -> {
            loadingBar.show();
            if(CorrectData()) {
                sendToBase();
                uploadImgWithCompress(loadingBar);
            }
        });
        return view;
    }
    ActivityResultLauncher<String> activityResultLauncher=registerForActivityResult(new ActivityResultContracts.GetMultipleContents(), new ActivityResultCallback<List<Uri>>() {
        @Override
        public void onActivityResult(List<Uri> result) {
            ArrayList<String> arrayList=new ArrayList<>();
            uriArrayList.addAll(result);
//            if (data.getClipData() != null) {
//                int x = data.getClipData().getItemCount();
//                for (int i = 0; i < x; i++) {
//                    uriArrayList.add(data.getClipData().getItemAt(i).getUri());
//                }
//            } else if (data.getData() != null) {
//                uriArrayList.add(data.getData());
//            }
            setAdapter();
//            GridLayoutManager gridLayoutManager=new GridLayoutManager(getView().getContext(),2);
//            binding.imageRecyclerView.setLayoutManager(gridLayoutManager);
//
//            modelClass modelClass=new modelClass(arrayList,Tutorial_4_Activity.this);
//            binding.imageRecyclerView.setAdapter(modelClass);
//            Toast.makeText(Tutorial_4_Activity.this, ""+arrayList.size(), Toast.LENGTH_SHORT).show();
//            // Let's run
//            // Thanks For Watching
        }
    });
    private void checkUserPermission(){
        PickImages();
    }

    private void PickImages() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),PICK_IMAGE_CODE);
        if(uriArrayList != null){
            uriArrayList.clear();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(REQUEST_PERMISSION_CODE == requestCode){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                PickImages();
            }
            else{
                Toast.makeText(this.getContext(), "permisson denied", Toast.LENGTH_SHORT);
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_CODE && resultCode == Activity.RESULT_OK){
            if(data != null) {
                if (data.getClipData() != null) {
                    int x = data.getClipData().getItemCount();
                    for (int i = 0; i < x; ++i) {
                        uriArrayList.add(data.getClipData().getItemAt(i).getUri());
                    }
                } else if (data.getData() != null) {
                    uriArrayList.add(data.getData());
                }
                setAdapter();
            }
        }
    }
    private void uploadImgWithCompress(LoadingBar loadingBar){
        for(int i =0;  i < uriArrayList.size(); ++i) {
            uriOfImg = uriArrayList.get(i);
            byte[] bytes = new byte[0];
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContext().getContentResolver(), uriOfImg);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 40, byteArrayOutputStream);
                bytes = byteArrayOutputStream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String path = "images/" + UUID.randomUUID().toString();
            StorageReference ref = storageReference.child(path);
            ref.putBytes(bytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Toast.makeText(this.getContext(), "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imgUrl = uri.toString();
                            addPhoto(imgUrl, path);
                            ++counter;
                            if(counter >= uriArrayList.size()){
                                counter=0;
                                loadingBar.dismiss();
                            }
                        }
                    });
                }
            });
        }
    }
    private void addPhoto(String imgUrl, String path) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> photoHashMap= new HashMap<>();
                photoHashMap.put("cloudPath",path);
                photoHashMap.put("URL",imgUrl);
                rootRef.child("Products").child(key).child(path).updateChildren(photoHashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void sendToBase() {
        String productName = productTitle.getText().toString();
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UUID uniqueKey = UUID.randomUUID();
                key = String.valueOf(uniqueKey);
                if(!dataSnapshot.child("Products").child(key).exists()){
                    HashMap<String, Object> userHashMap= new HashMap<>();
                    userHashMap.put("productKey",key);
                    userHashMap.put("productOwnerName",currentUser.getName());
                    userHashMap.put("productName",productName);
                    userHashMap.put("productPrice",productPrice.getText().toString());
                    userHashMap.put("productInfo",productInfo.getText().toString());
                    userHashMap.put("productOwner",currentUser.getNumber());
                    rootRef.child("Products").child(String.valueOf(uniqueKey)).updateChildren(userHashMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                    }
                                    else{

                                    }
                                }
                            });
                    HashMap<String, Object> currentProductName = new HashMap<>();
                    currentProductName.put(String.valueOf(uniqueKey),String.valueOf(uniqueKey));
                    rootRef.child("Users").child(currentUser.getNumber()).child("UserProducts").updateChildren(currentProductName);
                }
                else{

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void setAdapter(){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this.getContext(),uriArrayList,null, this::zoomPicture,null);
        viewPager.setAdapter(viewPagerAdapter);
    }
    private void zoomPicture(Uri photo){
        Dialog dialog = new Dialog(this.getContext());
        dialog.setContentView(R.layout.custom_dialog_zoom);
        ImageView img  = dialog.findViewById(R.id.custom_image_dialog);
        Button closeDialog = dialog.findViewById(R.id.custom_button_dialog);
        //Or this one instead:
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        img.setImageURI(photo);
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private boolean CorrectData(){
        if(uriArrayList.size() == 0){Toast.makeText(this.getContext(),"без фоточки нельзя!!!",Toast.LENGTH_SHORT).show();return false;}
        if(TextUtils.isEmpty(productTitle.getText())){Toast.makeText(this.getContext(),"что/кого ты продаёшь?",Toast.LENGTH_SHORT).show(); return false;}
        if(TextUtils.isEmpty(productPrice.getText())){Toast.makeText(this.getContext(),"почём эта шафка?",Toast.LENGTH_SHORT).show(); return false;}
        return true;
    }
}