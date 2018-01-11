package com.example.r.booklistingapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    private static int MAX_VALUE = 40;
    private static int MIN_VALUE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public static class BooksPreferences extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.setting_main);

            Preference maxResults = findPreference(getString(R.string.max_books_key));
            bindPreferenceSummaryToValue(maxResults);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            int intValue = Integer.parseInt(stringValue);
            if (intValue <= MAX_VALUE && intValue >= MIN_VALUE) {
                preference.setSummary(stringValue);
                return true;
            }
            Toast.makeText(getActivity(), "Please enter value between " + MIN_VALUE + " to " + MAX_VALUE, Toast.LENGTH_LONG).show();
            return false;
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }
    }
}
