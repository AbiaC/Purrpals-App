package com.example.purrpalsapp.ui.owner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.purrpalsapp.R;
import com.example.purrpalsapp.ui.cat.profile.fragment_create_cat_profile;
import com.example.purrpalsapp.ui.cat.profile.fragment_view_cat_profile;
import com.example.purrpalsapp.ui.cat.rent.PostCatForRentFragment;
import com.example.purrpalsapp.ui.cat.schedule.CatScheduleFragment;


public class fragment_pet_owner_page extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pet_owner_page, container, false);

        Button create_cat_profile_button = view.findViewById(R.id.create_cat_profile_button);
        Button view_cat_profile_button = view.findViewById(R.id.view_cat_profile_button);
        Button post_cat_rent_button = view.findViewById(R.id.post_cat_rent_button);
        Button view_cat_schedule_button = view.findViewById(R.id.view_cat_schedule_button);

        view_cat_schedule_button.setOnClickListener(v -> {
            Fragment fragment_view_cat_schedule = new CatScheduleFragment();
            replaceFragment(fragment_view_cat_schedule);
        });
        post_cat_rent_button.setOnClickListener(v -> {
            Fragment fragment_post_cat_for_rent = new PostCatForRentFragment();
            replaceFragment(fragment_post_cat_for_rent);
        });

        create_cat_profile_button.setOnClickListener(v -> {
            Fragment fragment_create_cat_profile = new fragment_create_cat_profile();
            replaceFragment(fragment_create_cat_profile);
        });

        view_cat_profile_button.setOnClickListener(v -> {
            Fragment fragment_view_cat_profile = new fragment_view_cat_profile();
            replaceFragment(fragment_view_cat_profile);
        });


        return view;
    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();

    }
}