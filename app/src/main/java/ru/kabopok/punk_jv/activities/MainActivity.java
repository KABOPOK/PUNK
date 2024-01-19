package ru.kabopok.punk_jv.activities;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import ru.kabopok.punk_jv.R;
import ru.kabopok.punk_jv.classes.LoadingBar;
import ru.kabopok.punk_jv.classes.Product;
import ru.kabopok.punk_jv.classes.ProductAdapter;
import ru.kabopok.punk_jv.classes.QuitDialog;
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
    private Button registration;

    final LoadingBar loadingBar = new LoadingBar(MainActivity.this);
    QuitDialog quitDialog;
    private CheckBox rememberUser;
    private TextView punkText;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String NUMBER = "number";
    public static final String PASSWORD = "password";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Set Data
        {
            registration = (Button) findViewById(R.id.register_button);
            inputButton = (Button) findViewById(R.id.in_button);
            rememberUser = findViewById(R.id.rememberUser_CheckBox);
            punkText = findViewById(R.id.textView5);
        }
        Typeface typeface = Typeface.create("sans-serif", Typeface.NORMAL);
        quitDialog = new QuitDialog(this,this);
        punkText.setTypeface(typeface);
        inputButton.setOnClickListener((v) -> {
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            String number = sharedPreferences.getString(NUMBER, "");
            String password = sharedPreferences.getString(PASSWORD, "");
            if (number.length()!=0) {
                chekInBase(number,password);
            }
            else {
                Intent inputIntent = new Intent(MainActivity.this, InputActivity.class);
                startActivity(inputIntent);
            }
        });
        registration.setOnClickListener((v) -> {
            Intent regIntent = new Intent(MainActivity.this, RegistrationActivity.class);
            startActivity(regIntent);
        });
    }

    private void chekInBase(String number, String password) {

        loadingBar.show();

        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Users").child(number).exists()) {

                    User user = dataSnapshot.child("Users").child(number).getValue(User.class);
                    if(user.getPassword().equals(password)){
                        Online.setCurrentUser(user);
                        loadingBar.dismiss();
                        Toast.makeText(MainActivity.this, " вечер в хату ", Toast.LENGTH_LONG).show();
                        Intent regIntent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(regIntent);
                    }
                }
                else{
                    loadingBar.dismiss();
                    Toast.makeText(MainActivity.this, "Ты кто такой?" + number, Toast.LENGTH_LONG).show();
                    Intent regIntent = new Intent(MainActivity.this, RegistrationActivity.class);
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
                quitDialog.show();
            }
        });
    }
}