package ru.kabopok.punk_jv.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;

import ru.kabopok.punk_jv.R;
import ru.kabopok.punk_jv.databinding.ActivityHomeBinding;
import ru.kabopok.punk_jv.databinding.ActivityMainBinding;
import ru.kabopok.punk_jv.fragments.FavouriteProductsFragment;
import ru.kabopok.punk_jv.fragments.HomeFragment;
import ru.kabopok.punk_jv.fragments.ProfileFragment;
import ru.kabopok.punk_jv.fragments.PublishProductFragment;
import ru.kabopok.punk_jv.fragments.UserProductsFragment;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());
        binding.navigationPanelBottomNavigationView.setBackground(null);
        binding.navigationPanelBottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.home){
                replaceFragment(new HomeFragment());
            }
            else if(item.getItemId() == R.id.favourite_menu) {
                replaceFragment(new FavouriteProductsFragment());
            }
            else if(item.getItemId() == R.id.pushProduct_menu) {
                replaceFragment(new PublishProductFragment());
            }
            else if(item.getItemId() == R.id.cart_menu) {
                replaceFragment(new UserProductsFragment());
            }
            else if(item.getItemId() == R.id.profile_menu) {
                replaceFragment(new ProfileFragment());
            }
            return true;
        });
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.Frame_layout, fragment);
        fragmentTransaction.commit();
    }
}