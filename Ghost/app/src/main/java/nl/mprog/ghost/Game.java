package nl.mprog.ghost;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Jan Geestman 103754
 * This class contains all of the game logic of the GHOST app.
 */
public class Game {

    private static int turn = 1;
    private static String player1Name;
    private static String player2Name;
    private static String player1Ghost;
    private static String player2Ghost;
    private static String gameWord;
    private static Lexicon lexicon;
    private static Context contextG;
    private static HashMap<String, Object> highscores = new HashMap<>();

    Game(Intent mainMenuIntent, Context context){

        contextG = context;

        SharedPreferences sharedPrefs = context.getSharedPreferences("userData", 0);
        SharedPreferences.Editor sharedPrefsEditor = sharedPrefs.edit();

        // Creates the lexicon
        try {
            String language = sharedPrefs.getString("language", "dutch");
            lexicon = new Lexicon(contextG, language);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Checks for games in progress. If there was one, game is instantiated with the
        // saved variables. Otherwise it gets the player names from the intent and sets
        // the game variables to empty.
        if (mainMenuIntent.getBooleanExtra("gameInProgress", false)){
            player1Name = sharedPrefs.getString("oldPlayer1Name", "Player 1");
            player2Name = sharedPrefs.getString("oldPlayer2Name", "Player 2");

            player1Ghost = sharedPrefs.getString("oldPlayer1Ghost", "");
            player2Ghost = sharedPrefs.getString("oldPlayer2Ghost", "");
            gameWord = sharedPrefs.getString("oldGameWord", "");
            turn = sharedPrefs.getInt("oldTurn", 1);
            sharedPrefsEditor.putBoolean("gameInProgress", false);
            sharedPrefsEditor.apply();
        }else {
            player1Name = mainMenuIntent.getExtras().getString("player1Name");
            player2Name = mainMenuIntent.getExtras().getString("player2Name");

            player1Ghost = " ";
            player2Ghost = " ";
            gameWord = "";
        }
        try {
            Scanner inputFile = new Scanner(contextG.openFileInput("highscores.txt"));
            while (inputFile.hasNext()){
                String line = inputFile.next();
                String[] content = line.split(",");
                if (!(content == null)) {
                    highscores.put(content[0], content[1]);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Main turn handler for the game logic
    // Checks if the character is a letter and appends it to the game word.
    // It then checks for game losses.
    public static boolean turnHandler(char s){
        if (Character.isLetter(s)) {
            gameWord = gameWord + s;
            lexicon.filter(gameWord);
            if ((gameWord.length()>3 && lexicon.isWord(gameWord))|(lexicon.count() == 0)){
                endGame();
                return true;
            }
            swapTurn();
        }
        return false;
    }
    // Checks if the current player has full 'GHOST'.
    // If this is the case, it commits the other player as winner.
    private static void endGame(){
        if (updateGhost(turn)){
            swapTurn();
            String winner = getPlayerName(turn);
            updateHighscores(winner);
        }
        swapTurn();
        gameWord = "";
    }

    // Swaps turn
    private static void swapTurn() {
        if (turn == 1) {
            turn = 2;
        } else {
            turn = 1;
        }
    }

    // Updates the 'GHOST' letters of the current player.
    // Returns true if all letters of 'GHOST' are present.
    private static boolean updateGhost(int turn){
        String playerGhost = getPlayerGhost(turn);
        char n = playerGhost.charAt(playerGhost.length() - 1);
        switch (n){
            case ' ':
                playerGhost = "G";
                break;
            case 'G':
                playerGhost = "GH";
                break;
            case 'H':
                playerGhost = "GHO";
                break;
            case 'O':
                playerGhost = "GHOS";
                break;
            case 'S':
                playerGhost = "GHOST";
                break;
            default:
                playerGhost = "";
                break;
            }
        setPlayerGhost(turn, playerGhost);
        return playerGhost.equals("GHOST");
    }

    private static void setPlayerGhost(int turn, String playerGhost) {
        if (turn == 1){
            player1Ghost = playerGhost;
        } else {
            player2Ghost = playerGhost;
        }
    }

    // This function is supposed to update the highscore list but it generates errors.
    // Even though the file is present on the device it still creates a nullpointer to the
    // FileOutputStream.
    private static void updateHighscores(String winner){
        int playerScore = 0;
        if (highscores.containsKey(winner)) {
            playerScore = Integer.parseInt(highscores.get(winner).toString());
            highscores.remove(winner);
        }
        highscores.put(winner, playerScore+1);
        try {
            FileOutputStream outputStream = contextG.openFileOutput("highscores.txt", Context.MODE_PRIVATE);
            for (Map.Entry<String, Object> entry : highscores.entrySet()){
                String name = entry.getKey();
                Object score = entry.getValue();
                outputStream.write((name + "," + score.toString()).getBytes());
            }
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Returns the player name of the player who's turn it is.
    public static String getPlayerName(int player){
        if (player == 1){
            return player1Name;
        } else return player2Name;
    }

    // Returns the 'GHOST' of the player who's turn it is
    public static String getPlayerGhost(int player){
        if (player == 1){
            return player1Ghost;
        } else return player2Ghost;
    }

    // Returns current game word
    public static String getCurrentWord(){
        return gameWord;
    }

    // Returns current turn
    public static int getTurn(){ return turn; }

    // Returns true if the current player has all letters of 'GHOST'
    public static boolean gameOver() {
        return getPlayerGhost(turn).equals("GHOST");
    }

    // Returns true if there are no words left in the lexicon. This denotes a loss by wrong word.
    // Returns false if there are still words left, meaning it's a correct word from the word list.
    public static boolean getLossReason() {
        return lexicon.count()==0;
    }
}
