package ru.kabopok.punk_jv.activities;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import ru.kabopok.punk_jv.R;
import ru.kabopok.punk_jv.classes.Product;
import ru.kabopok.punk_jv.classes.User;
import ru.kabopok.punk_jv.current.Online;

public class UserProfileActivity extends AppCompatActivity {

    ImageView userPhoto;
    TextView userName;
    TextView userGender;
    TextView userNumber;

    ImageView pickBack;

    User currentUser;
    Product currentProduct = Online.getCurrentProduct();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        userPhoto = findViewById(R.id.userProfilePhoto_ImageView);
        userName = findViewById(R.id.nameOfUser_TextView);
        userGender = findViewById(R.id.genderOfUser_TextView);
        userNumber = findViewById(R.id.numberOfUser_TextView);
        pickBack = findViewById(R.id.pick_back);
        getUserdata(currentProduct.getProductOwner());
        setOnBackPressed();
        pickBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToProductIntent  = new Intent(UserProfileActivity.this, ProductActivity.class);
                startActivity(backToProductIntent);
            }
        });
    }

    private void getUserdata(String number) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Users").child(number).exists()) {
                    currentUser = dataSnapshot.child("Users").child(number).getValue(User.class);
                    userName.setText(currentUser.getName());
                    userGender.setText(currentUser.getGender());
                    userNumber.setText(currentUser.getNumber());
                    setImg();
                    Toast.makeText(UserProfileActivity.this, "что за тигр этот лев?!", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(UserProfileActivity.this, "я удалил его аккаунт он дурак " + number, Toast.LENGTH_LONG).show();
                    Intent regIntent = new Intent(UserProfileActivity.this, ProductActivity.class);
                    startActivity(regIntent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setImg() {
        if(currentUser.getPhotoUserUrl()!=null){
            Picasso.with(this).load(currentUser.getPhotoUserUrl()).into(userPhoto);
        }
    }
    private void setOnBackPressed() {
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent backToProductIntent  = new Intent(UserProfileActivity.this, ProductActivity.class);
                startActivity(backToProductIntent);
            }
        });
    }
}