package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.content.res.Resources;

public class SettingsActivity extends AppCompatActivity {
    private Spinner listGridSpinner;
    private SwitchCompat darkModeSwitch;
    private int listGridChoice;
    private boolean darkMode;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        listGridSpinner = findViewById(R.id.layout_spinner);
        ArrayAdapter<CharSequence> listGridAdapter = ArrayAdapter.createFromResource(this,
                R.array.courseLayoutOpt, android.R.layout.simple_spinner_item);
        listGridAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listGridSpinner.setAdapter(listGridAdapter);

        darkModeSwitch = findViewById(R.id.darkModeSwitch);

        loadSettings();

        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveSettings();
                updateTheme();
            }
        });
    }

    @Override
    protected void onPause() {
        saveSettings();

        super.onPause();

        Log.d("TAG", "SettingActivity === onPause");
    }

    private void saveSettings() {
        sharedPreferences = getSharedPreferences("savedData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        listGridChoice = listGridSpinner.getSelectedItemPosition();
        editor.putInt("listGridChoice", listGridChoice);

        darkMode = darkModeSwitch.isChecked();
        editor.putBoolean("darkMode", darkMode);


        // commit all settings changes
        editor.commit();
    }

    private void loadSettings() {
        sharedPreferences = getSharedPreferences("savedData", Context.MODE_PRIVATE);

        listGridChoice = sharedPreferences.getInt("listGridChoice", 0);
        listGridSpinner.setSelection(listGridChoice);

        darkMode = sharedPreferences.getBoolean("darkMode", false);
        darkModeSwitch.setChecked(darkMode);
        updateTheme();
    }

    private void updateTheme() {
        Log.d("TAG", "updating theme...");

        if(darkMode) {
            Log.d("TAG", "switching to darkmode");
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            Log.d("TAG", "switching to lightmode");
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}