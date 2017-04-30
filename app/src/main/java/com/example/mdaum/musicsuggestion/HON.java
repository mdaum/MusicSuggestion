package com.example.mdaum.musicsuggestion;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

public class HON extends AppCompatActivity implements MediaPlayer.OnPreparedListener {
    TextView info;
    AlertDialog.Builder alert;
    EditText et;
    Button hot,not,replay,done;
    public MediaPlayer mp;
    boolean isInit = false;
    public ArrayList<SongInfo> songs = new ArrayList<SongInfo>();
    TextView tv;

    int currSong;
    int numSuggestSongs = 0;

    final int MAX_SONGS = 10; // maximum number of songs we can suggest
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hon);
        this.info= (TextView) findViewById(R.id.songInfo);
        this.hot= (Button) findViewById(R.id.hot);
        this.not= (Button) findViewById(R.id.not);
        this.replay= (Button) findViewById(R.id.replay);
        this.done = (Button)findViewById(R.id.done);
        this.alert = new AlertDialog.Builder(this);

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

        // setup alert to get user input
        // there are multiple ways to get user input
        // this is one of the simplest and easiest
        // can look into other ways potentially
        alert.setTitle("Num Songs To Suggest");
        alert.setMessage("Please enter the number of songs (max is " + MAX_SONGS + ")");
        et = new EditText(this);
        alert.setView(et);

        // if the user submits a value...
        // TODO: handle bad values
        alert.setPositiveButton("Ok!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = et.getText().toString();
                numSuggestSongs = Integer.parseInt(value);

                if(songs.size() != 0)
                {
                    Log.d("ALERT", "playing song in response to user input");
                    playSong(songs.get(0));
                    currSong=0;
                }

            }
        });

        // otherwise...
        // TODO: implement this
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        // generate random songs
        try
        {
            songs = RandomSongListGenerator.RandSongList();
        }
        catch (ParseException | IOException e)
        {
            Log.d("ERROR", e.toString());
        }

        // log the list
        for (SongInfo s : songs) {
            Log.d("Song", s.toString());
        }

        alert.show();
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
                Log.d("ERROR", Log.getStackTraceString(e));
            }
            info.setText(s.toString());
        }
        else
        {
            Log.d("ERROR", "Song has null preview url");
        }
    }

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

    // stops the music and sets up for suggestion
    // Note: This is a non-standard way of transitioning
    // Seems necessary because our objects are complex
    // and aren't easily serializable since we don't have control over
    // Spotify objects
    public void DoneClick(View v){
        mp.stop();
        setContentView(R.layout.activity_suggest);
        tv = (TextView)findViewById(R.id.suggest);
        suggest();
    }

    public void HONClick(View v){
        //check which button clicked
        Button clicked = (Button)v;
        //gotta stop the media player, and then apparently reset...
        mp.stop();
        mp.reset();
        //set hot bool for songInfo obj
        if(clicked.getText().equals("HOT")){
            songs.get(currSong).hot=true;
            Log.d("HONCLICK","it's hot");
        }
        else if(clicked.getText().equals("NOT")){
            songs.get(currSong).hot=false;
            Log.d("HONCLICK","it's not");
        }

        //play next song
        currSong++;
        playSong(songs.get(currSong));
    }

    public void ReplayClick(View v){
        //pause
        mp.pause();
        //try seeking to beginning of song
        mp.seekTo(0);
        //play
        mp.start();
    }

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

/*    @Override
    public void onBackPressed(){

    }*/


}
