package ru.kabopok.punk_jv.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;

import ru.kabopok.punk_jv.R;
import ru.kabopok.punk_jv.current.Online;

public class PublishProductActivity extends AppCompatActivity {

    StorageReference storageReference;
    private EditText productTitle;
    private EditText productPrice;
    private EditText productInfo;
    private Button PushProduct;
    private StorageReference StoreRef;
    private ImageView productImage;
    private Uri uriOfImg;
    private String imgUrl;
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    uriOfImg = result.getData().getData();
                    Glide.with(getApplicationContext()).load(uriOfImg).into(productImage);
                }
            } else {
                Toast.makeText(PublishProductActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
            }
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_product);
        //Set Data
        {
            storageReference = FirebaseStorage.getInstance().getReference();
            StoreRef = FirebaseStorage.getInstance().getReference("Image");
            productTitle = (EditText) findViewById(R.id.product_title);
            productPrice = (EditText) findViewById(R.id.product_price);
            productInfo = (EditText) findViewById(R.id.product_info);
            PushProduct = (Button) findViewById(R.id.push_product_button);
            productImage = (ImageView) findViewById(R.id.product_image);
        }
        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);
            }
        });

        PushProduct.setOnClickListener(view -> {
            //uploadImage(uriOfImg);
            uploadImgWithCompress();
        });
    }

    private void pushProduct() {
        String name = productTitle.getText().toString();
        String price = productPrice.getText().toString();
        String info = productInfo.getText().toString();
        String imgURL = imgUrl.toString();
        String user = Online.getCurrentUser().getNumber();
        sendToBase(user,name,price,info,imgURL);
    }

    private void uploadImgWithCompress(){
        byte[] bytes = new byte[0];
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriOfImg);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40,byteArrayOutputStream);
            bytes = byteArrayOutputStream.toByteArray();
        }catch (IOException e){
            e.printStackTrace();
        }

        StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
        ref.putBytes(bytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(PublishProductActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imgUrl = uri.toString();
                        String name = productTitle.getText().toString();
                        String price = productPrice.getText().toString();
                        String info = productInfo.getText().toString();
                        String imgURL = imgUrl.toString();
                        String user = Online.getCurrentUser().getNumber();
                        sendToBase(user,name,price,info,imgURL);
                        Intent toHomeIntent = new Intent(PublishProductActivity.this, HomeActivity.class);
                        startActivity(toHomeIntent);
                    }
                });
            }
        });
    }

    private void uploadImage(Uri file) {
        StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
        ref.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(PublishProductActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imgUrl = uri.toString();
                        String name = productTitle.getText().toString();
                        String price = productPrice.getText().toString();
                        String info = productInfo.getText().toString();
                        String imgURL = imgUrl.toString();
                        String user = Online.getCurrentUser().getNumber();
                        sendToBase(user,name,price,info,imgURL);
                        Intent toHomeIntent = new Intent(PublishProductActivity.this, HomeActivity.class);
                        startActivity(toHomeIntent);
                    }
                });
                result.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.toString();
                    }
                });
                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imgUrl = uri.toString();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PublishProductActivity.this, "Failed!" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                //progressIndicator.setMax(Math.toIntExact(taskSnapshot.getTotalByteCount()));
                //progressIndicator.setProgress(Math.toIntExact(taskSnapshot.getBytesTransferred()));
            }
        });

    }
    private void sendToBase(String number, String productName, String productPrice, String productInfo, String URL) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count_long = dataSnapshot.child("Products").getChildrenCount();
                String count = String.valueOf(count_long);
                if(!dataSnapshot.child("Products").child(count).exists()){
                    HashMap<String, Object> userHashMap= new HashMap<>();
                    userHashMap.put("productName",productName);
                    userHashMap.put("productPrice",productPrice);
                    userHashMap.put("productInfo",productInfo);
                    userHashMap.put("URL",URL);
                    userHashMap.put("productOwner",Online.getCurrentUser().getNumber());
                    rootRef.child("Products").child(count).updateChildren(userHashMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
//                                        Toast.makeText(RegistrationActivity.this, " Твоя попка в базе ->" + number, Toast.LENGTH_LONG).show();
//                                        Intent regIntent = new Intent(RegistrationActivity.this, MainActivity.class);
//                                        startActivity(regIntent);
                                    }
                                    else{
//                                        Toast.makeText(RegistrationActivity.this, " Либо программист даун, либо санкции новые ввели->" + number, Toast.LENGTH_LONG).show();
//                                        Intent regIntent = new Intent(RegistrationActivity.this, MainActivity.class);
//                                        startActivity(regIntent);
                                    }
                                }
                            });
                }
                else{
//                    Toast.makeText(RegistrationActivity.this, "Already registered -> " + number, Toast.LENGTH_LONG).show();
//                    Intent regIntent = new Intent(RegistrationActivity.this, MainActivity.class);
//                    startActivity(regIntent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}