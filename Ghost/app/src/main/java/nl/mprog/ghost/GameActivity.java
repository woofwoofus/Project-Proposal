package nl.mprog.ghost;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Jan Geestman 10375406
 * This activity takes care of all visual aspects of the game
 */
public class GameActivity extends Activity{

    TextView player1NameBox;
    TextView player2NameBox;
    static TextView player1GhostBox;
    static TextView player2GhostBox;
    static TextView currentWordTextView;
    EditText nextCharacterEditText;
    private SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        sharedPrefs = getSharedPreferences("userData", 0);

        Intent mainMenuIntent = getIntent();
        Game game = new Game(mainMenuIntent, GameActivity.this);

        player1NameBox = (TextView) findViewById(R.id.Player1NameTextView);
        player2NameBox = (TextView) findViewById(R.id.Player2NameTextView);
        player1GhostBox = (TextView) findViewById(R.id.Player1GhostTextView);
        player2GhostBox = (TextView) findViewById(R.id.Player2GhostTextView);
        currentWordTextView = (TextView) findViewById(R.id.CurrentWordTextView);

        player1NameBox.setText(Game.getPlayerName(1));
        player2NameBox.setText(Game.getPlayerName(2));

        updateGhost();
        updateGameWord();
        updateHighlights();

        nextCharacterEditText = (EditText) findViewById(R.id.NextCharacterEditText);
        nextCharacterEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        // Catching the 'Enter' key on the soft keyboard (does nothing instead of starting new line)
                    }
                }
                return false;
            }
        });
    }

    // Commits entered character as part of the game word
    private void commitCharacter(char letter) {
        if (Game.turnHandler(letter)){ //true means gameword is in the lexicon
            if (Game.gameOver()){ // If the game is over
                Intent HighscoreIntent = new Intent(this, HighscoreActivity.class);
                startActivity(HighscoreIntent);
                finish(); // Closes activity preventing a Back call
            } else {
                // Construction of alert dialog for when the round is over
                AlertDialog roundLossDialog = new AlertDialog.Builder(this).create();
                roundLossDialog.setTitle("YOU LOSE");
                if (Game.getLossReason()){ //if true gameword was a real word, if false gameword can not become a real word
                    roundLossDialog.setMessage(Game.getPlayerName(Game.getTurn()) + " won this round! " +
                            "\nThe guessed word is not in the dictionary");
                } else {
                    roundLossDialog.setMessage(Game.getPlayerName(Game.getTurn()) + " won this round! " +
                            "\nThe guessed word is in the dictionary");
                }
                roundLossDialog.setButton(-1, "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                roundLossDialog.show();
            }
        }
        updateGameWord();
        updateGhost();
        updateHighlights();
    }

    // Calls commitCharacter(char)
    public void commitCharacter(View view) {
        String text = nextCharacterEditText.getText().toString();
        if (text.length() > 0) {
            char c = text.charAt(0);
            commitCharacter(c);
        } else {
            Toast.makeText(this, "Please enter a character", Toast.LENGTH_SHORT).show();
        }
    }

    // Updates the player Ghost textviews
    public static void updateGhost() {
        player1GhostBox.setText(Game.getPlayerGhost(1) + "_");
        player2GhostBox.setText(Game.getPlayerGhost(2) + "_");
    }

    // Updates the gameword textview
    public static void updateGameWord() {
        currentWordTextView.setText(Game.getCurrentWord() + "_");
    }

    // Updates the highlights of the playername textviews
    public void updateHighlights() {
        if (Game.getTurn() == 1) {
            player1NameBox.setBackgroundColor(Color.GREEN);
            player1NameBox.setTextColor(Color.WHITE);
            player2NameBox.setBackgroundColor(Color.WHITE);
            player2NameBox.setTextColor(Color.BLACK);
        } else {
            player1NameBox.setBackgroundColor(Color.WHITE);
            player1NameBox.setTextColor(Color.BLACK);
            player2NameBox.setBackgroundColor(Color.GREEN);
            player2NameBox.setTextColor(Color.WHITE);
        }
    }

    // Saves game in progress to file when activity stops
    @Override
    public void onStop(){
        super.onStop();
        if (!(Game.gameOver())) {
            SharedPreferences.Editor sharedPrefEditor = sharedPrefs.edit();
            sharedPrefEditor.putBoolean("gameInProgress", true);
            sharedPrefEditor.putString("oldPlayer1Name", Game.getPlayerName(1));
            sharedPrefEditor.putString("oldPlayer2Name", Game.getPlayerName(2));
            sharedPrefEditor.putString("oldPlayer1Ghost", Game.getPlayerGhost(1));
            sharedPrefEditor.putString("oldPlayer2Ghost", Game.getPlayerGhost(2));
            sharedPrefEditor.putString("oldGameWord", Game.getCurrentWord());
            sharedPrefEditor.putInt("oldTurn", Game.getTurn());

            sharedPrefEditor.apply();
        }
    }

    // Starts the Highscores activity
    public void startHighscoresActivity(View view) {
        Intent highscoresActivityIntent = new Intent(GameActivity.this, HighscoreActivity.class);
        startActivity(highscoresActivityIntent);
    }

    // Starts the Settings activity
    public void startSettingsActivity(View view) {
        Intent settingsActivityIntent = new Intent(GameActivity.this, SettingsActivity.class);
        startActivity(settingsActivityIntent);
    }
}
