package ru.kabopok.punk_jv.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

    Product currentProduct = Online.getCurrentProduct();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        userPhoto = findViewById(R.id.userProfilePhoto_ImageView);
        userName = findViewById(R.id.nameOfUser_TextView);
        userGender = findViewById(R.id.genderOfUser_TextView);
        userNumber = findViewById(R.id.numberOfUser_TextView);
        getUserdata(currentProduct.getProductOwner());
    }

    private void getUserdata(String number) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        final User[] user = new User[1];
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Users").child(number).exists()) {
                    user[0] = dataSnapshot.child("Users").child(number).getValue(User.class);
                    userName.setText(user[0].getName());
                    userGender.setText(user[0].getGender());
                    userNumber.setText(user[0].getNumber());
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
        Picasso.with(this).load(user[0].getPhotoUserUrl()).into(userPhoto);
    }
}