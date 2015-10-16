package nl.mprog.ghost;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
/**
 * Created by Jan Geestman 10375406
 * This activity allows the user to
 */
public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences sharedPrefs = getSharedPreferences("userData", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPrefs.edit();

        // Languages that can be selected
        ArrayList<String> languageList = new ArrayList<>();
        languageList.add("dutch");
        languageList.add("english");

        Spinner languageSpinner = (Spinner) findViewById(R.id.languageSpinner);
        ArrayAdapter<String> languageSpinnerAdapter = new ArrayAdapter<>(this,R.layout.name_spinner_item,languageList);
        languageSpinner.setAdapter(languageSpinnerAdapter);

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
                String selectedLanguage = ((TextView)view).getText().toString();
                editor.putString("language", selectedLanguage);
                editor.apply();
            }

            // Required Override
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    // Starts Main Menu activity
    public void StartMainMenuActivity(View view) {
        Intent MainMenuIntent = new Intent(SettingsActivity.this, MainMenuActivity.class);
        startActivity(MainMenuIntent);
    }
}
