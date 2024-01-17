package ru.kabopok.punk_jv.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ru.kabopok.punk_jv.R;
import ru.kabopok.punk_jv.classes.User;
import ru.kabopok.punk_jv.classes.DeveloperAdapter;

public class DeveloperActivity extends AppCompatActivity {

    SearchView searchView;
    DeveloperAdapter developerAdapter;
    RecyclerView userRecycle;
    Boolean AdapterPrepared = false;
    List<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);

       // searchView = (SearchView)findViewById(R.id.searchView_developer);
        userRecycle = findViewById(R.id.developer_recycleView);
        setData();
        prepareRV();
    }

    private void setData() {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!AdapterPrepared) {
                    for (DataSnapshot postSnapshot : dataSnapshot.child("Users").getChildren()) {
                            User user = postSnapshot.getValue(User.class);
                            userList.add(user);
                    }
                    prepareAdapter();
                    AdapterPrepared = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void prepareAdapter() {
        developerAdapter = new DeveloperAdapter(userList,this,this::selectedUser);
        userRecycle.setAdapter(developerAdapter);
    }

    private void prepareRV() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        userRecycle.setLayoutManager(linearLayoutManager);
    }

    void selectedUser(User user){}
}