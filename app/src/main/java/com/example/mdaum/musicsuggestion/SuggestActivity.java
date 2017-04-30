package com.example.mdaum.musicsuggestion;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by triad02 on 4/29/17.
 */

public class SuggestActivity extends AppCompatActivity
{
    TextView tv;
    ArrayList<SongInfo> songs;
    int numSuggestSongs;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest);
        Bundle extras = getIntent().getExtras();

        tv = (TextView) findViewById(R.id.suggest);
        songs = (ArrayList<SongInfo>) extras.get("songs");
        numSuggestSongs = (int) extras.get("numSuggestSongs");

        suggest();

    }

    // default placeholder for now
    // loop through and print the info from first numSuggestSongs songs
    public void suggest()
    {
        String text = "";
        SongInfo song;
        for(int i = 0;i < numSuggestSongs;i++)
        {
            song = songs.get(i);
            text += song.toString() + "\n";
        }

        tv.setText(text);
    }
}
