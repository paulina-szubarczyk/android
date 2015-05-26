package com.example.paulina.myapplication;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;


import java.util.List;


public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener{


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupSimplePreferencesScreen();
    }

    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }

        PreferenceCategory fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_measure);
        addPreferencesFromResource(R.xml.pref_measure);

        fakeHeader.setTitle(R.string.pref_header_pallet);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_pallet);

        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_camera);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_camera);

        getPreference(R.string.pallet_key).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                String stringValue = newValue.toString();

                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);

                return true;
            }
        });

        getPreference(R.string.analyse_key).setOnPreferenceClickListener(this);

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        Intent intent = new Intent(preference.getContext(), GraphActivity.class);
        intent.putExtra("gradient_x", getIntent().getFloatArrayExtra("gradient_x"));
        intent.putExtra("gradient_y", getIntent().getFloatArrayExtra("gradient_y"));
        intent.putExtra("gradient", getIntent().getFloatArrayExtra("gradient"));
        intent.putExtra("histogram", getIntent().getByteArrayExtra("histogram"));
        startActivity(intent);
        return true;
    }

    private Preference getPreference(int id){
        return findPreference(getResources().getString(id));
    }

    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    private static boolean isSimplePreferences(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        if (!isSimplePreferences(this)) {
            System.out.println("TAMMMMM");
            loadHeadersFromResource(R.xml.pref_headers, target);
        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class PalletPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_pallet);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class MeasurePreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class CameraPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
    }
}
