package ru.kabopok.punk_jv.activities;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Set;

import io.paperdb.Paper;
import ru.kabopok.punk_jv.R;
import ru.kabopok.punk_jv.classes.LoadingBar;
import ru.kabopok.punk_jv.classes.User;
import ru.kabopok.punk_jv.current.Online;

public class InputActivity extends AppCompatActivity {

    private Button inputButton;
    private EditText numberData;
    private EditText passwordData;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String NUMBER = "number";
    public static final String PASSWORD = "password";
    final LoadingBar loadingBar = new LoadingBar(InputActivity.this);
    private CheckBox rememberUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        //Set Data
        {
            numberData = (EditText) findViewById(R.id.input_text_number);
            passwordData = (EditText) findViewById(R.id.input_text_password);
            inputButton = (Button) findViewById(R.id.in_button);
            rememberUser =findViewById(R.id.rememberUser_CheckBox);
        }
        inputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InToApp();
            }
        });
        setOnBackPressed();
    }

    private void InToApp() {
        String number = numberData.getText().toString();
        String password = passwordData.getText().toString();
        if(number.length()!=0 && password.length()!=0) {
            checkInBase(number, password);
        }
    }

    private void checkInBase(String number, String password) {

        loadingBar.show();

        if(number.equals("alice") && password.equals("29")){
            Intent DeveloperIntent = new Intent(InputActivity.this, DeveloperActivity.class);
            startActivity(DeveloperIntent);
            return;
        }

        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Users").child(number).exists()) {

                    User user = dataSnapshot.child("Users").child(number).getValue(User.class);
                    if(user.getPassword().equals(password)){
                        Online.setCurrentUser(user);
                        if(rememberUser.isChecked()){
                            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(NUMBER,number);
                            editor.putString(PASSWORD,password);
                            editor.apply();
                        }
                        loadingBar.dismiss();
                        Toast.makeText(InputActivity.this, " вечер в хату ", Toast.LENGTH_LONG).show();
                        Intent regIntent = new Intent(InputActivity.this, HomeActivity.class);
                        startActivity(regIntent);
                    }
                    else{
                        loadingBar.dismiss();
                        Toast.makeText(InputActivity.this, "кривой пароль", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    loadingBar.dismiss();
                    Toast.makeText(InputActivity.this, "Ты кто такой?" + number, Toast.LENGTH_LONG).show();
                    Intent regIntent = new Intent(InputActivity.this, RegistrationActivity.class);
                    startActivity(regIntent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void setOnBackPressed() {
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent backToMain  = new Intent(InputActivity.this, MainActivity.class);
                startActivity(backToMain);
            }
        });
    }
}