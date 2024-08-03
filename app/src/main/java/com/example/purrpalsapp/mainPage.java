package com.example.purrpalsapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.example.purrpalsapp.ui.chat.ChatFragment;
import com.example.purrpalsapp.ui.chat.ChatListFragment;
import com.example.purrpalsapp.ui.home.fragment_home;
import com.example.purrpalsapp.ui.profile.fragment_profile;
import com.example.purrpalsapp.ui.search.fragment_search;
import com.example.purrpalsapp.ui.settings.fragment_setting;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class mainPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sharedPreferences.getString("theme", "default");

        switch (theme) {
            case "light":
                setTheme(R.style.AppTheme_Light);
                break;
            case "dark":
                setTheme(R.style.AppTheme_Dark);
                break;
            case "default":
            default:
                setTheme(R.style.AppTheme);
                break;
        }


        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();

                if (itemId == R.id.home) {

                    replaceFragment(new fragment_home());

                    return true;
                } else if (itemId == R.id.searchAndFilter) {

                    replaceFragment(new fragment_search());

                    return true;
                } else if (itemId == R.id.chatRoom) {

                    replaceFragment(new ChatListFragment());

                    return true;
                } else if (itemId == R.id.profile) {

                    replaceFragment(new fragment_profile());
                    return true;
                } else if (itemId == R.id.setting) {
                    replaceFragment(new fragment_setting());
                    return true;
                }

                return false;
            }
        });

        // Initialize default fragment if savedInstanceState is null
        if (savedInstanceState == null) {
            replaceFragment(new fragment_home());
        }
    }


    private void replaceFragment(Fragment fragment) {


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();
    }
}