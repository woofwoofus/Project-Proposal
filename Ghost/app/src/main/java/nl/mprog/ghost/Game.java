package nl.mprog.ghost;

import android.content.Context;
import android.content.Intent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by woofw_000 on 28/09/2015.
 */
public class Game {

    private static int turn;
    private String language;
    private static String player1Name;
    private static String player2Name;
    private static String player1Ghost;
    private static String player2Ghost;
    private static String gameWord;
    private static Lexicon lexicon;
    private static Context context;
    private static HashMap<String, Object> highscores = new HashMap<>();

    Game(Intent mainMenuIntent, Context context){

//        language =

        try {
            lexicon = new Lexicon(context, "dutch");
        } catch (IOException e) {
            e.printStackTrace();
        }

        player1Name = mainMenuIntent.getExtras().getString("player1Name");
        player2Name = mainMenuIntent.getExtras().getString("player2Name");

        player1Ghost = " ";
        player2Ghost = " ";
        gameWord = "";

        try {
            Scanner inputFile = new Scanner(context.openFileInput("highscores.txt"));
            while (inputFile.hasNext()){
                String line = inputFile.next();
                String[] cont = line.split("/(,)");
                highscores.put(cont[0], cont[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void turnHandler(char s){
        if (Character.isLetter(s)) {
            gameWord = gameWord + s;
            lexicon.filter(gameWord);
            if ((gameWord.length()>3 && lexicon.isWord(gameWord))|(lexicon.count() == 0)){
                endGame();
            }
            if (turn == 1) {
                turn = 2;
            }else {
                turn = 1;
            }
        }
    }

    private static void endGame(){
        String loser = getPlayerName(turn);
        String loserGhost = getPlayerGhost(turn);
        if (updateGhost(turn)){
            updateHighscores();
            gameWord = "";
        }
    }


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
        if (playerGhost.equals("GHOST")){
            return true;
        } else { return false;}
    }

    private static void setPlayerGhost(int turn, String playerGhost) {
        if (turn == 1){
            player1Ghost = playerGhost;
        } else {
            player2Ghost = playerGhost;
        }
    }

    private static void updateHighscores(){
        try {
            PrintStream outputFile = new PrintStream(context.openFileOutput("highscores.txt", Context.MODE_PRIVATE));
            outputFile.flush();
            for (Map.Entry<String, Object> entry : highscores.entrySet()){
                String name = entry.getKey();
                Object score = entry.getValue();
                outputFile.println(name + "," + score.toString());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getPlayerName(int player){
        if (player == 1){
            return player1Name;
        } else return player2Name;
    }

    public static String getPlayerGhost(int player){
        if (player == 1){
            return player1Ghost;
        } else return player2Ghost;
    }

    public static String getCurrentWord(){
        return gameWord;
    }
}
