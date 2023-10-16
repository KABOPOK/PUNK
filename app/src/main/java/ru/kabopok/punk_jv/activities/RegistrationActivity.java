package ru.kabopok.punk_jv.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import ru.kabopok.punk_jv.R;

public class RegistrationActivity extends AppCompatActivity {
    private Button createUser;
    private EditText nameData;
    private EditText genderData;
    private EditText numberData;
    private EditText passwordData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        createUser = (Button) findViewById(R.id.create_new_user_button);
        nameData = (EditText) findViewById(R.id.create_name);
        genderData = (EditText) findViewById(R.id.create_gender);
        numberData = (EditText) findViewById(R.id.create_login);
        passwordData = (EditText) findViewById(R.id.create_password);

        createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        String name = nameData.getText().toString();
        String gender = genderData.getText().toString();
        String number = numberData.getText().toString();
        String password = passwordData.getText().toString();
        //check correct of data
        //..
        //end
        sendToBase(name, gender, number, password);
    }

    private void sendToBase(String name, String gender, String number, String password) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child("Users").child(number).exists()){
                    HashMap<String, Object> userHashMap= new HashMap<>();
                    userHashMap.put("name",name);
                    userHashMap.put("number",number);
                    userHashMap.put("gender",gender);
                    userHashMap.put("password",password);
                    rootRef.child("Users").child(number).updateChildren(userHashMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegistrationActivity.this, " Твоя попка в базе ->" + number, Toast.LENGTH_LONG).show();
                                        Intent regIntent = new Intent(RegistrationActivity.this, MainActivity.class);
                                        startActivity(regIntent);
                                    }
                                    else{
                                        Toast.makeText(RegistrationActivity.this, " Либо программист даун, либо санкции новые ввели->" + number, Toast.LENGTH_LONG).show();
                                        Intent regIntent = new Intent(RegistrationActivity.this, MainActivity.class);
                                        startActivity(regIntent);
                                    }
                                }
                            });
                }
                else{
                    Toast.makeText(RegistrationActivity.this, "Already registered -> " + number, Toast.LENGTH_LONG).show();
                    Intent regIntent = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(regIntent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}