package ru.kabopok.punk_jv.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import ru.kabopok.punk_jv.R;
import ru.kabopok.punk_jv.activities.HomeActivity;
import ru.kabopok.punk_jv.activities.MainActivity;
import ru.kabopok.punk_jv.activities.ProductActivity;
import ru.kabopok.punk_jv.activities.ProfileActivity;
import ru.kabopok.punk_jv.activities.PublishProductActivity;
import ru.kabopok.punk_jv.activities.RegistrationActivity;
import ru.kabopok.punk_jv.activities.UserProductsActivity;
import ru.kabopok.punk_jv.activities.UserProfileActivity;
import ru.kabopok.punk_jv.classes.LoadingBar;
import ru.kabopok.punk_jv.classes.Product;
import ru.kabopok.punk_jv.classes.User;
import ru.kabopok.punk_jv.current.Online;

public class ProfileFragment extends Fragment {
    public static final int RESULT_OK = -1;
    ImageView userPhoto;
    TextView userGender;
    TextView userPassword;
    TextView userNumber;
    TextView name;
    CircleImageView pick;
    Uri photoUserUri;
    Activity profile;
    User currentUser = Online.getCurrentUser();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        name = view.findViewById(R.id.profileUsername_TextView);
        userPhoto = view.findViewById(R.id.userMainProfilePhoto_ImageView);
        userGender = view.findViewById(R.id.userProfileGender_TextView);
        userPassword = view.findViewById(R.id.userProfilePassword_TextView);
        userNumber = view.findViewById(R.id.userProfileNumber_TextView);
        pick = view.findViewById(R.id.edit_profile_pick);

        name.setText(currentUser.getName());
        userGender.setText(currentUser.getGender());
        userPassword.setText(currentUser.getPassword());
        userNumber.setText(currentUser.getNumber());
        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(getContext(),ProfileFragment.this);
            }
        });

        Picasso.with(view.getContext()).load(Online.getCurrentUser().getPhotoUserUrl()).into(userPhoto);
        return view;
    }

    private void uploadImgWithCompress(Uri uriOfImg){
            byte[] bytes = new byte[0];
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContext().getContentResolver(), uriOfImg);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 40, byteArrayOutputStream);
                bytes = byteArrayOutputStream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();;
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
                            String imgUrl = uri.toString();
                            replacePhoto(imgUrl, path);
                        }
                    });
                }
            });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                photoUserUri = result.getUri();
                if(photoUserUri!=null){
                    uploadImgWithCompress(photoUserUri);
                }
                userPhoto.setImageURI(photoUserUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                //Exception error = result.getError();
            }
        }
    }

    private void replacePhoto(String imgUrl, String path) {
        final StorageReference storeRef = FirebaseStorage.getInstance().getReference();
        StorageReference reference = storeRef.child(currentUser.getPhotoUserCloudPath());
        reference.delete();
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("Users").child(currentUser.getNumber()).exists()){
                    HashMap<String, Object> userHashMap = new HashMap<>();
                    userHashMap.put("name",currentUser.getName());
                    userHashMap.put("number",currentUser.getNumber());
                    userHashMap.put("gender",currentUser.getGender());
                    userHashMap.put("password",currentUser.getPassword());
                    userHashMap.put("photoUserUrl", imgUrl);
                    userHashMap.put("photoUserCloudPath", path);
                    rootRef.child("Users").child(currentUser.getNumber()).updateChildren(userHashMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getActivity(), "Внешнасть не главная " + currentUser.getName(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
                Online.ChangePhotoData(imgUrl, path);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}