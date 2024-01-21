package ru.kabopok.punk_jv.activities;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import com.squareup.picasso.Picasso;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import ru.kabopok.punk_jv.R;
import ru.kabopok.punk_jv.classes.ImageResizer;
import ru.kabopok.punk_jv.classes.LoadingBar;
import ru.kabopok.punk_jv.classes.QuitDialog;
import ru.kabopok.punk_jv.current.Online;

public class RegistrationActivity extends AppCompatActivity {

    final LoadingBar loadingBar = new LoadingBar(RegistrationActivity.this);
    private CheckBox checkBox;
    private ImageView photoUser;
    private Uri photoUserUri = null;
    private Button createUser;
    private EditText nameData;
    StorageReference storageReference;
    private StorageReference StoreRef;
    QuitDialog quitDialog;
    private EditText genderData;
    private EditText numberData;
    private EditText passwordData;
    String photoUserCloudPath = "images/" + UUID.randomUUID().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        storageReference = FirebaseStorage.getInstance().getReference();
        StoreRef = FirebaseStorage.getInstance().getReference("Image");
        createUser = (Button) findViewById(R.id.create_new_user_button);
        nameData = (EditText) findViewById(R.id.create_name);
        genderData = (EditText) findViewById(R.id.create_gender);
        numberData = (EditText) findViewById(R.id.create_login);
        passwordData = (EditText) findViewById(R.id.create_password);
        checkBox = findViewById(R.id.checkRegistration_CheckBox);
        photoUser = findViewById(R.id.registrationUserPhoto_ImageView);
        quitDialog = new QuitDialog(this,this);
        nameData.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        numberData.setFilters(new InputFilter[]{new InputFilter.LengthFilter(13)});
        genderData.setFilters(new InputFilter[]{new InputFilter.LengthFilter(36)});
        photoUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCropActivity();
            }
        });
        createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked() && CorrectData()){
                    loadingBar.show();
                    if(photoUserUri == null){
                        int drawableId = R.drawable.photo;  // Replace with your actual resource ID
                        Drawable drawable = getResources().getDrawable(drawableId);
                        photoUser.setImageDrawable(drawable);
                        loadingBar.dismiss();
                        String name = nameData.getText().toString();
                        String gender = genderData.getText().toString();
                        String number = numberData.getText().toString();
                        String password = passwordData.getText().toString();
                        sendToBase(name, gender, number, password, "stesnashka");
                    }
                    else {
                        uploadImgWithCompress();
                    }
                }
                else if(!checkBox.isChecked() && CorrectData()){
                    Exception Exception = null;
                    try {
                        throw Exception;
                    } catch (java.lang.Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        setOnBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                photoUserUri = result.getUri();
                photoUser.setImageURI(photoUserUri);
                //test.setImageURI(photoUserUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void startCropActivity(){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }
    private void uploadImgWithCompress(){
        byte[] bytes = new byte[0];
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUserUri);
            Bitmap newWay = ImageResizer.reduceBitmapSize(bitmap, 1000000);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            newWay.compress(Bitmap.CompressFormat.JPEG, 40,byteArrayOutputStream);
            bytes = byteArrayOutputStream.toByteArray();
        }catch (IOException e){
            e.printStackTrace();
        }
        StorageReference ref = storageReference.child(photoUserCloudPath);
        ref.putBytes(bytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(RegistrationActivity.this, "красивая девушка!!", Toast.LENGTH_SHORT).show();
                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        loadingBar.dismiss();
                        String imgUrl = uri.toString();
                        String name = nameData.getText().toString();
                        String gender = genderData.getText().toString();
                        String number = numberData.getText().toString();
                        String password = passwordData.getText().toString();
                        sendToBase(name, gender, number, password, imgUrl);
                    }
                });
            }
        });
    }

    private void sendToBase(String name, String gender, String number, String password, String imgUrl) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child("Users").child(number).exists()){
                    HashMap<String, Object> userHashMap = new HashMap<>();
                    userHashMap.put("name",name);
                    userHashMap.put("number",number);
                    userHashMap.put("gender",gender);
                    userHashMap.put("password",password);
                    userHashMap.put("photoUserUrl", imgUrl);
                    userHashMap.put("photoUserCloudPath", photoUserCloudPath);
                    rootRef.child("Users").child(number).updateChildren(userHashMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegistrationActivity.this, "Зарегано ->" + number, Toast.LENGTH_LONG).show();
                                        Intent regIntent = new Intent(RegistrationActivity.this, MainActivity.class);
                                        startActivity(regIntent);
                                    }
                                    else{
                                        Toast.makeText(RegistrationActivity.this, " Либо программист даун, либо санкции новые ввели->" + number, Toast.LENGTH_LONG).show();
                                        Intent regIntent = new Intent(RegistrationActivity.this, MainActivity.class);
                                        startActivity(regIntent);
                                    }
                                    loadingBar.dismiss();
                                }
                            });
                }
                else{
                    Toast.makeText(RegistrationActivity.this, "уже есть с таким номером-> " + number, Toast.LENGTH_LONG).show();
                    Intent regIntent = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(regIntent);
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private boolean CorrectData(){
        if(TextUtils.isEmpty(nameData.getText())){Toast.makeText(this,"Как тебя называют",Toast.LENGTH_SHORT).show(); return false;}
        if(TextUtils.isEmpty(passwordData.getText())){Toast.makeText(this,"мне плохо, меня Таня бросила, а ты пароль не ввел",Toast.LENGTH_SHORT).show(); return false;}
        if(TextUtils.isEmpty(numberData.getText())){Toast.makeText(this,"эй девушка, телефонъчик скиньте",Toast.LENGTH_SHORT).show(); return false;}
        if(TextUtils.isEmpty(genderData.getText())){Toast.makeText(this,"@token - VK/Telegram",Toast.LENGTH_SHORT).show(); return false;}
        return true;
    }
    private void setOnBackPressed() {
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent backToMain  = new Intent(RegistrationActivity.this, MainActivity.class);
                startActivity(backToMain);
            }
        });
    }
}