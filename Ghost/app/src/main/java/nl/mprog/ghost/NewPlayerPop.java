package nl.mprog.ghost;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Jan Geestman 10375406
 * This popup allows the user to input a new name.
 *
 */
public class NewPlayerPop extends Activity {

    ArrayList<String> playerList;

    EditText newPlayerNameView;
    Intent mainMenuIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.new_player_pop);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*.8),(int) (height*.3));

        newPlayerNameView = (EditText) findViewById(R.id.newPlayerEditText);
        mainMenuIntent = getIntent();
        playerList = mainMenuIntent.getExtras().getStringArrayList("playerList");

        newPlayerNameView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        commitPlayer();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public void commitPlayer() {
        final String newPlayerName = newPlayerNameView.getText().toString();
        if (playerList.contains(newPlayerName)){
            Toast.makeText(this, "This player already exists", Toast.LENGTH_SHORT).show();
        } else {
            mainMenuIntent.putExtra("newPlayerName",newPlayerName);
            setResult(1, mainMenuIntent);
            finish();
        }
    }

    public void commitPlayer(View view) {
        commitPlayer();
    }
}
