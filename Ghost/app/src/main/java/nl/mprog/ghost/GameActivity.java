package nl.mprog.ghost;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.regex.Pattern;

/**
 * Created by woofw_000 on 02/10/2015.
 */
public class GameActivity extends Activity{

    TextView player1NameBox;
    TextView player2NameBox;
    static TextView player1GhostBox;
    static TextView player2GhostBox;
    static TextView currentWordTextView;
    EditText nextCharacterEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent mainMenuIntent = getIntent();
        Game game = new Game(mainMenuIntent,GameActivity.this);

        player1NameBox = (TextView) findViewById(R.id.Player1NameTextView);
        player2NameBox = (TextView) findViewById(R.id.Player2NameTextView);
        player1GhostBox = (TextView) findViewById(R.id.Player1GhostTextView);
        player2GhostBox = (TextView) findViewById(R.id.Player2GhostTextView);
        currentWordTextView = (TextView) findViewById(R.id.CurrentWordTextView);

        player1NameBox.setText(Game.getPlayerName(1));
        player2NameBox.setText(Game.getPlayerName(2));

        updateGhost();
        updateGameWord();

        nextCharacterEditText = (EditText) findViewById(R.id.NextCharacterEditText);
        nextCharacterEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println(s.toString());
                if (count>0) {
                    Game.turnHandler(s.charAt(0));
                    updateGameWord();
                    updateGhost();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }


    public static void updateGhost() {
        player1GhostBox.setText(Game.getPlayerGhost(1) + "_");
        player2GhostBox.setText(Game.getPlayerGhost(2) + "_");
    }

    public static void updateGameWord() {
        currentWordTextView.setText(Game.getCurrentWord() + "_");
    }
}
