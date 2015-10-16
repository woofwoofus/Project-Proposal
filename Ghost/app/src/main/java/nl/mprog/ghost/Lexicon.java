package nl.mprog.ghost;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Jan Geestman 10375406
 */
public class Lexicon {

    private Set<String> remainingWords = new HashSet<>();
    private Set<String> dictionary;

    Lexicon(Context context, String language) throws IOException {
        InputStream IStream = context.getAssets().open(language + ".txt");
        InputStreamReader IStreamReader = new InputStreamReader(IStream);
        BufferedReader bReader = new BufferedReader(IStreamReader);
        dictionary = new HashSet<>();
        String word = bReader.readLine();
        while (!(word==null)) {
            dictionary.add(word);
            word = bReader.readLine();
        }
        reset();
        IStream.close();
        IStreamReader.close();
        bReader.close();
    }

    public void filter(String subset) {
        Set<String> toBeRemoved = new HashSet<>();
        for (String word : remainingWords) {
            if (!(word.startsWith(subset))) {
                toBeRemoved.add(word);
            }
        }
        remainingWords.removeAll(toBeRemoved);
    }

    public boolean isWord(String subset){
        return remainingWords.contains(subset);
    }

    public void reset() {
        remainingWords.addAll(dictionary);
    }

    public int count(){
        return remainingWords.size();
    }
}

