package com.example.mdaum.musicsuggestion;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import org.json.simple.parser.ParseException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // android typically does not allow network connections on the UI thread
        // these lines revoke this policy
        // will cause the app to crash if bad network connection
        // TODO: run the network stuff asynchronously on another thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // generate random songs
        ArrayList<SongInfo> list = new ArrayList<SongInfo>();
        try
        {
            list = RandomSongListGenerator.RandSongList();
        }
        catch (ParseException | IOException e)
        {
            Log.d("ERROR", e.toString());
        }

        // log the list
        for (SongInfo s : list) {
            Log.d("Song", s.toString());
        }
    }
}
