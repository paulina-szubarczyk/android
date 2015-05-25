package com.example.paulina.myapplication;

import android.annotation.TargetApi;
import android.content.Context;
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


public class SettingsActivity extends PreferenceActivity {


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

        bindPreferenceSummaryToValue(getPreference(R.string.pallet_key));
//        bindPreferenceSummaryToValue(getPreference(R.string.analyse_key));
//
//        bindPreferenceSummaryToValue(getPreference(R.string.rectangle_key));
//        bindPreferenceSummaryToValue(getPreference(R.string.changeable_key));
//        bindPreferenceSummaryToValue(getPreference(R.string.default_key));
//        bindPreferenceSummaryToValue(getPreference(R.string.block_key));
//
//        bindPreferenceSummaryToValue(getPreference(R.string.cam1_key));
//        bindPreferenceSummaryToValue(getPreference(R.string.cam1_key));

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

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };


    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }



    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class PalletPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_pallet);

            bindPreferenceSummaryToValue(findPreference("pallet"));
            bindPreferenceSummaryToValue(findPreference("analyze"));
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class MeasurePreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_measure);
            bindPreferenceSummaryToValue(findPreference("rectangle"));
            bindPreferenceSummaryToValue(findPreference("changeable"));
            bindPreferenceSummaryToValue(findPreference("default"));
            bindPreferenceSummaryToValue(findPreference("block_scale"));

        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class CameraPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_camera);
            bindPreferenceSummaryToValue(findPreference("cam"));
            bindPreferenceSummaryToValue(findPreference("cam2"));
        }
    }
}
