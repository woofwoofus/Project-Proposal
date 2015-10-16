package nl.mprog.ghost;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Jan Geestman 10375406
 * This activity contains the list of winners in the app.
 * It is constructed from a text file present on the user's phone
 */

public class HighscoreActivity extends Activity {

    private ArrayList<String> highscores = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        try {
            Scanner inputFile = new Scanner(this.openFileInput("highscores.txt"));
            while (inputFile.hasNext()){
                String line = inputFile.next();
                String[] content = line.split(",");
                if (!(content == null)) {
                    highscores.add(content[0] + ": " + content[1]);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ListView highscoreList = (ListView) findViewById(R.id.highscoresListView);
        ArrayAdapter highscoreAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, highscores);
        highscoreList.setAdapter(highscoreAdapter);

    }

    public void StartMainMenuActivity(View view) {
        Intent MainMenuIntent = new Intent(HighscoreActivity.this, MainMenuActivity.class);
        startActivity(MainMenuIntent);
    }
}
