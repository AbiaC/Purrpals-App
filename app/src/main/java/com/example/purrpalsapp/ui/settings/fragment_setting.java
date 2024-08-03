package com.example.purrpalsapp.ui.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.example.purrpalsapp.R;
import com.example.purrpalsapp.ui.about.aboutUsPage;
import com.example.purrpalsapp.ui.login.initialPage;
import com.example.purrpalsapp.utils.LocalDataManager;
import com.google.firebase.auth.FirebaseAuth;

public class fragment_setting extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SwitchPreferenceCompat notificationsEnabledPreference;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

        setPreferencesFromResource(R.xml.preferences, rootKey);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);


        applyTheme(sharedPreferences);

        notificationsEnabledPreference = findPreference("notifications_enabled");
        if (notificationsEnabledPreference != null) {
            notificationsEnabledPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean enabled = (Boolean) newValue;
                if (enabled) {
                    checkNotificationPermission();
                }
                return true;
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("theme")) {

            applyTheme(sharedPreferences);
        }
    }

    private void applyTheme(SharedPreferences sharedPreferences) {
        String themeValue = sharedPreferences.getString("theme", "default"); // Default to System Default
        int themeMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM; // Default value

        switch (themeValue) {
            case "light":
                themeMode = AppCompatDelegate.MODE_NIGHT_NO; // Light mode
                break;
            case "dark":
                themeMode = AppCompatDelegate.MODE_NIGHT_YES; // Dark mode
                break;
            case "default":
                themeMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM; // System default
                break;
        }

        AppCompatDelegate.setDefaultNightMode(themeMode);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.getKey().equals("about")) {
            Intent intent = new Intent(getContext(), aboutUsPage.class);
            startActivity(intent);
            return true;
        }
        if (preference.getKey().equals("logout")) {
            LocalDataManager localDataManager = new LocalDataManager(requireActivity());
            localDataManager.saveIsLoggedIn(false);
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getContext(), initialPage.class);
            startActivity(intent);
            requireActivity().finish();
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }

    private void checkNotificationPermission() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        if (!notificationManager.areNotificationsEnabled()) {

            requestNotificationPermission();
        }
    }

    private void requestNotificationPermission() {

        startActivity(new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, getContext().getPackageName()));
    }

}