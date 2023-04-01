package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SettingsActivity extends AppCompatActivity {
    private Spinner listGridSpinner;

    private int listGridChoice;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        listGridSpinner = findViewById(R.id.layout_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.courseLayoutOpt, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listGridSpinner.setAdapter(adapter);

        loadSettings();
    }

    @Override
    protected void onPause() {
        saveSettings();

        super.onPause();

        Log.d("TAG", "SettingActivity === onPause");
    }

    private void saveSettings() {
        sharedPreferences = getSharedPreferences("savedData", Context.MODE_PRIVATE);

        int spinnerValue = sharedPreferences.getInt("spinnerValue", 0);
        if (spinnerValue != 0) {
            listGridSpinner.setSelection(spinnerValue);
        }
        listGridChoice = listGridSpinner.getSelectedItemPosition();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("listGridChoice", listGridChoice);
        Log.d("TAG", "user decimal choice: " + listGridChoice);

        editor.commit();
    }

    private void loadSettings() {
        sharedPreferences = getSharedPreferences("savedData", Context.MODE_PRIVATE);

        listGridChoice = sharedPreferences.getInt("listGridChoice", 0);

        listGridSpinner.setSelection(listGridChoice);
    }
}