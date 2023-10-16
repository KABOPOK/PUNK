package ru.kabopok.punk_jv.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.kabopok.punk_jv.R;
import ru.kabopok.punk_jv.classes.Product;
import ru.kabopok.punk_jv.classes.ProductAdapter;
import ru.kabopok.punk_jv.classes.User;
import ru.kabopok.punk_jv.current.Online;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private Button inputButton;
    private EditText numberData;
    private EditText passwordData;
    private Button registration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Intent regIntentA = new Intent(MainActivity.this, HomeActivity.class);
        //startActivity(regIntentA);
        //Set Data
        {
            registration = (Button) findViewById(R.id.register_button);
            numberData = (EditText) findViewById(R.id.input_text_number);
            passwordData = (EditText) findViewById(R.id.input_text_password);
            inputButton = (Button) findViewById(R.id.in_button);
        }
        registration.setOnClickListener((v)->{
            Intent regIntent = new Intent(MainActivity.this, RegistrationActivity.class);
            startActivity(regIntent);
        });
        inputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InToApp();
            }
        });
    }

    private void InToApp() {
        String number = numberData.getText().toString();
        String password = passwordData.getText().toString();
        chekInBase(number, password);
    }

    private void chekInBase(String number, String password) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Users").child(number).exists()) {

                    User user = dataSnapshot.child("Users").child(number).getValue(User.class);
                    if(user.getPassword().equals(password)){
                        Online.setCurrentUser(user);
                        Toast.makeText(MainActivity.this, " вечер в хату ", Toast.LENGTH_LONG).show();
                        Intent regIntent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(regIntent);
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "такой попки я ещё не видал -> занеси свою попку в мою базу " + number, Toast.LENGTH_LONG).show();
                    Intent regIntent = new Intent(MainActivity.this, RegistrationActivity.class);
                    startActivity(regIntent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}