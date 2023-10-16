package ru.kabopok.punk_jv.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import ru.kabopok.punk_jv.R;
import ru.kabopok.punk_jv.classes.User;
import ru.kabopok.punk_jv.current.Online;

public class ProfileActivity extends AppCompatActivity {

    TextView name;
    Button pushProductButton;

    User currentUser = Online.getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        name = findViewById(R.id.profileUsername_TextView);
        pushProductButton = findViewById(R.id.profilePushProduct_Button);

        name.setText(currentUser.getName());
        pushProductButton.setOnClickListener((v)->{
            Intent profileIntent = new Intent(ProfileActivity.this, PublishProductActivity.class);
            startActivity(profileIntent);
        });
    }
}