package nl.mprog.ghost;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Jan Geestman 10375406
 *
 * This is the main menu activity of the app. Here the players can input their names or
 * choose one from the list of highscore entries. Two buttons allow the players to start a game.
 * One is for a new game, the other is for loading a game already in progress.
 * The final two buttons allow the users to view the highscore list or change the settings.
 *
 * !GIANT BUG ALERT!
 * There is a massive bug which I haven't been able to fix yet. Basically the game word is persistent
 * between rounds even though the variable is emptied. I have no idea what is causing this, nor how
 * to fix it.
 * !GIANT BUG ALERT!
 *
 */
public class MainMenuActivity extends Activity {

    private SharedPreferences sharedPrefs;
    private static ArrayList<String> playerList = new ArrayList<>();
    ArrayAdapter<String> player1SpinnerAdapter, player2SpinnerAdapter;
    private ArrayList<Spinner> spinnerList;
    private FrameLayout layout_MainMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        sharedPrefs = getSharedPreferences("userData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        createPlayerList();
        createPlayerSpinners();
        createSpinnerListeners();

        layout_MainMenu = (FrameLayout) findViewById( R.id.mainMenu);
        layout_MainMenu.getForeground().setAlpha(0); // Brighten Alpha Mask
    }

    // Creates the two spinners that let's players choose a name
    private void createPlayerSpinners() {
        spinnerList = new ArrayList<>(2);
        spinnerList.add(0, (Spinner) findViewById(R.id.player1Spinner));
        spinnerList.add(1, (Spinner) findViewById(R.id.player2Spinner));

        player1SpinnerAdapter = new ArrayAdapter<>(this, R.layout.name_spinner_item, playerList);
        player2SpinnerAdapter = new ArrayAdapter<>(this, R.layout.name_spinner_item, playerList);

        spinnerList.get(0).setAdapter(player1SpinnerAdapter);
        spinnerList.get(1).setAdapter(player2SpinnerAdapter);

        spinnerList.get(0).setSelection(0,false);
        spinnerList.get(1).setSelection(0, false);
    }

    // Creates the list of players from the 'highscores.txt' file
    private void createPlayerList() {
        playerList.add("Player"); // Placeholder name for when no name is selected yet.

        try {
            Scanner inputFile = new Scanner(this.openFileInput("highscores.txt"));
            while (inputFile.hasNext()){
                String line = inputFile.next();
                String[] content = line.split(",");
                playerList.add(content[0]);
            }
            inputFile.close();
        } catch (FileNotFoundException e) {
            File f = new File(getFilesDir(), "highscores.txt");
            try {
                f.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        playerList.add("Create New Player"); // Final entry allows for the creation of a new player
    }

    // These two intemlisteners call the NewPlayerPop activity so the player can insert a new name.
    private void createSpinnerListeners(){
        spinnerList.get(0).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
                if (position == playerList.size() - 1) { // If 'Create New Player' is selected
                    layout_MainMenu.getForeground().setAlpha(220); // Dim Alpha Mask
                    Intent getNewPlayer = new Intent(MainMenuActivity.this, NewPlayerPop.class);
                    getNewPlayer.putExtra("playerList", playerList);
                    getNewPlayer.putExtra("spinnerNum", 0);
                    startActivityForResult(getNewPlayer, 1);
                }
            }
            // Required Override
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerList.get(1).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
                if (position == playerList.size() - 1) {
                    layout_MainMenu.getForeground().setAlpha(220); // Dim Alpha Mask
                    Intent getNewPlayer = new Intent(MainMenuActivity.this, NewPlayerPop.class);
                    getNewPlayer.putExtra("playerList", playerList);
                    getNewPlayer.putExtra("spinnerNum", 1);
                    startActivityForResult(getNewPlayer, 1);
                }
            }
            // Required Override
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    /**
     * This bit of wizardry reshuffles the entries int the player database so the spinners
     * still maintain their original functionality. (Create New Player stays at the bottom)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        layout_MainMenu.getForeground().setAlpha(0); // Brighten Alpha Mask
        if (requestCode == 1){
            if (resultCode == 1){
                final String newPlayerName = data.getExtras().getString("newPlayerName") ;
                final int spinnerNum = data.getExtras().getInt("spinnerNum");
                playerList.remove(playerList.size()-1);
                playerList.add(newPlayerName);
                playerList.add("Create New Player");
                if (spinnerNum == 1){
                    player1SpinnerAdapter.notifyDataSetChanged();
                } else {
                    player2SpinnerAdapter.notifyDataSetChanged();
                }
                spinnerList.get(spinnerNum).setSelection(playerList.size()-2);

            } else {
                spinnerList.get(0).setSelection(0);
                spinnerList.get(1).setSelection(0);
            }
        }
    }

    // Constructs a new game once the player names fit certain criteria
    public void passPlayersToGame(View view) {
        final String player1Name = ((Spinner)findViewById(R.id.player1Spinner)).getSelectedItem().toString();
        final String player2Name = ((Spinner)findViewById(R.id.player2Spinner)).getSelectedItem().toString();
        if (player1Name.equals("")){
            Toast.makeText(this, "Please enter a name for player 1", Toast.LENGTH_SHORT).show();
        } else if (player2Name.equals("")) {
            Toast.makeText(this, "Please enter a name for player 2", Toast.LENGTH_SHORT).show();
        } else if (player1Name.toUpperCase().equals(player2Name.toUpperCase())){
            Toast.makeText(this, "Player 1 and Player 2 cannot have the same name", Toast.LENGTH_SHORT).show();
        } else  if (player1Name.equals("Player")|(player2Name.equals("Player") |
                (player1Name.equals("Create New Player") | (player2Name.equals("Create New Player"))))) {
            Toast.makeText(this, "Please enter names for both players", Toast.LENGTH_SHORT).show();
        } else {
            Intent GameScreenIntent = new Intent(this, GameActivity.class);
            GameScreenIntent.putExtra("player1Name", player1Name);
            GameScreenIntent.putExtra("player2Name", player2Name);

            startActivity(GameScreenIntent);
        }
    }

    // Resumes an old game from save file instead of creating a new one
    public void resumeOldGame(View view) {
        Intent GameScreenIntent = new Intent(this, GameActivity.class);
        if (!(sharedPrefs.getBoolean("gameInProgress",false))){
            Toast.makeText(this, "No saved game found", Toast.LENGTH_SHORT).show();
        } else {
            GameScreenIntent.putExtra("gameInProgress", true);
            startActivity(GameScreenIntent);
        }
    }

    // Starts Highscore Activity
    public void startHighscoresActivity(View view) {
        Intent highscoresActivityIntent = new Intent(MainMenuActivity.this, HighscoreActivity.class);
        startActivity(highscoresActivityIntent);
    }

    // Starts Settings activity
    public void startSettingsActivity(View view) {
        Intent settingsActivityIntent = new Intent(MainMenuActivity.this, SettingsActivity.class);
        startActivity(settingsActivityIntent);
    }
}
