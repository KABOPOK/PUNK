package ru.kabopok.punk_jv.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ru.kabopok.punk_jv.R;
import ru.kabopok.punk_jv.classes.User;
import ru.kabopok.punk_jv.current.Online;

public class ProfileActivity extends AppCompatActivity {

    ImageView userPhoto;
    TextView userGender;

    TextView userPassword;
    TextView usesrNumber;
    TextView name;
    Button pushProductButton;

    User currentUser = Online.getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        name = findViewById(R.id.profileUsername_TextView);
        pushProductButton = findViewById(R.id.profilePushProduct_Button);
        userPhoto = findViewById(R.id.userMainProfilePhoto_ImageView);
        userGender = findViewById(R.id.userProfileGender_TextView);
        userPassword = findViewById(R.id.userProfilePassword_TextView);
        usesrNumber = findViewById(R.id.userProfileNumber_TextView);


        name.setText(currentUser.getName());
        userGender.setText("Я" + currentUser.getGender());
        userPassword.setText(currentUser.getPassword());
        usesrNumber.setText(currentUser.getNumber());
        pushProductButton.setOnClickListener((v)->{
            Intent profileIntent = new Intent(ProfileActivity.this, PublishProductActivity.class);
            startActivity(profileIntent);
        });

        Picasso.with(this).load(Online.getCurrentUser().getPhotoUserUrl()).into(userPhoto);
    }
}