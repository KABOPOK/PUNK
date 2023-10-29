package ru.kabopok.punk_jv.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import ru.kabopok.punk_jv.activities.ProductActivity;
import ru.kabopok.punk_jv.activities.ProfileActivity;
import ru.kabopok.punk_jv.activities.PublishProductActivity;
import ru.kabopok.punk_jv.activities.UserProductsActivity;
import ru.kabopok.punk_jv.activities.UserProfileActivity;
import ru.kabopok.punk_jv.classes.Product;
import ru.kabopok.punk_jv.classes.User;
import ru.kabopok.punk_jv.current.Online;

public class ProfileFragment extends Fragment {
    ImageView userPhoto;
    TextView userGender;
    TextView userPassword;
    TextView userNumber;
    TextView name;
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


        name.setText(currentUser.getName());
        userGender.setText("Я" + currentUser.getGender());
        userPassword.setText(currentUser.getPassword());
        userNumber.setText(currentUser.getNumber());

        Picasso.with(view.getContext()).load(Online.getCurrentUser().getPhotoUserUrl()).into(userPhoto);
        return view;
    }
}