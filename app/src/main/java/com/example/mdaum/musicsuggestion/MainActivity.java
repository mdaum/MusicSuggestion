package com.example.mdaum.musicsuggestion;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.parser.ParseException;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener {

    public MediaPlayer mp;
    int music_length = 0;

    boolean isInit = false;

    public ArrayList<SongInfo> list = new ArrayList<SongInfo>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!isInit)
        {
            init();
            isInit = true;
        }
    }

    private void init()
    {
        // android typically does not allow network connections on the UI thread
        // these lines revoke this policy
        // will cause the app to crash if bad network connection
        // TODO: run the network stuff asynchronously on another thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // setup vars
        mp = new MediaPlayer();

        // generate random songs
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

        if(list.size() != 0)
        {
            playSong(list.get(0));

        }
    }
    // method to play a song
    private void playSong(SongInfo s)
    {
        if(s.preview_url != null)
        {
            try
            {
                // setup the media player asynchronously
                mp.setDataSource(s.preview_url);
                mp.prepareAsync();
                mp.setOnPreparedListener(this);

                //logging genre logic
                String toLog=s.genres.size()+" genres: ";//build out string to Log in while loop
                for(String str: s.genres){//append to toLog
                    toLog+=str+", ";
                }
                //log it
                Log.d("GENRE",toLog); //extra , but whatevs

            }
            catch (Exception e)
            {
                Log.d("ERROR", e.toString());
            }
        }
        else
        {
            Log.d("ERROR", "Song has null preview url");
        }
    }

    public void startTutorial(View v)
    {
        Intent tut = new Intent(this, TutorialActivity.class);
        startActivity(tut);
    }

    // listener for when media player has buffered enough to play
    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public void onStop()
    {
        Log.d("STOP", "stopping...");
        super.onStop();
        mp.pause();
    }
    @Override
    public void onPause()
    {
        Log.d("PAUSE", "pausing...");
        super.onPause();
        mp.pause();
    }
    @Override
    public void onResume()
    {
        Log.d("RESUME", "resuming...");
        super.onResume();
        mp.start();
    }
    @Override
    public void onRestart()
    {
        Log.d("RESTART", "restarting...");
        super.onRestart();
        mp.start();
    }
}
