package com.example.purrpalsapp.ui.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.purrpalsapp.R;
import com.example.purrpalsapp.ui.owner.fragment_pet_owner_page;
import com.example.purrpalsapp.ui.renter.fragment_renter_page;


public class fragment_home extends Fragment {

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);

        ImageButton btnPetOwner = view.findViewById(R.id.btnPetOwner);
        ImageButton btnRenter = view.findViewById(R.id.btnRenter);


        // get name for txvWelcome from login


        btnPetOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment_pet_owner_page = new fragment_pet_owner_page();
                replaceFragment(fragment_pet_owner_page);
            }
        });

        btnRenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment_renter_page = new fragment_renter_page();
                replaceFragment(fragment_renter_page);
            }
        });


        return view;
    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();

    }
}