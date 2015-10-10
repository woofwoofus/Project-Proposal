package nl.mprog.ghost;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

public class MainMenuActivity extends Activity {

    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor sharedPrefEditor;
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        sharedPrefs = getSharedPreferences("userData", MODE_PRIVATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void passPlayersToGame(View view) {

        final String player1Name = ((TextView)findViewById(R.id.p1Box)).getText().toString();
        final String player2Name = ((TextView)findViewById(R.id.p2Box)).getText().toString();
        if (player1Name.equals("")){
            Toast.makeText(this, "Please enter a name for player 1", Toast.LENGTH_SHORT).show();
        } else if (player2Name.equals("")) {
            Toast.makeText(this, "Please enter a name for player 2", Toast.LENGTH_SHORT).show();
        }else {
            Intent GameScreenIntent = new Intent(this, GameActivity.class);
            GameScreenIntent.putExtra("player1Name", player1Name);
            GameScreenIntent.putExtra("player2Name", player2Name);
            sharedPrefEditor = sharedPrefs.edit();
            sharedPrefEditor.putString("curPlayer1", player1Name);
            sharedPrefEditor.putString("curPlayer2", player2Name);
            sharedPrefEditor.apply();

            startActivity(GameScreenIntent);
        }
    }
}
