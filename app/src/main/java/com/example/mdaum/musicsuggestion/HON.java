package com.example.mdaum.musicsuggestion;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class HON extends AppCompatActivity implements MediaPlayer.OnPreparedListener {
    TextView info;
    AlertDialog.Builder alert;
    EditText et;
    ImageView honimg;
    Button hot,not,replay,done;

    //these objects are instantiatied once we launch the suggest portion of app
    ImageView suggestimg;
    Button next,prev,replay_suggest;
    TextView suggest_info;

    public MediaPlayer mp;
    boolean isInit = false;
    public ArrayList<SongInfo> songs = new ArrayList<SongInfo>();
    public ArrayList<SongInfo> suggested_songs; //to be instantiated and loaded up by suggest() void method

    int currSongHON; //counter for which song we are currently playing
    int currSongSuggest; //suggest version of this
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
        this.honimg=(ImageView)findViewById(R.id.honview);

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
                    playSong(songs.get(0),true);
                    currSongHON =0;
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

    // method to play a song, boolean inHon indicates which ImageView we will update
    private void playSong(SongInfo s,boolean inHON)
    {
        if(s.preview_url != null)
        {
            try
            {
                // setup the media player asynchronously
                mp.setDataSource(s.preview_url);
                mp.prepareAsync();
                mp.setOnPreparedListener(this);

/*                //logging genre logic
                String toLog=s.genres.size()+" genres: ";//build out string to Log in while loop
                for(String str: s.genres){//append to toLog
                    toLog+=str+", ";
                }
                //log it
                Log.d("GENRE",toLog); //extra , but whatevs*/

                //if there is album art for the song, we get the bitmap for it and set our imgview
                //this is where we use the boolean inHON
                if(s.albumArt.get(0)!=null) {
                    Log.d("IMAGEURL", s.albumArt.get(0).url);
                    if(inHON)honimg.setImageBitmap(getbmpfromURL(s.albumArt.get(0).url));
                    else suggestimg.setImageBitmap(getbmpfromURL(s.albumArt.get(0).url));
                }
                else{
                    Log.d("IMAGEURL","coudn't find one");
                    if(inHON)honimg.setImageResource(R.drawable.noart);
                    else suggestimg.setImageResource(R.drawable.noart);
                }

            }
            catch (Exception e)
            {
                Log.d("ERROR", Log.getStackTraceString(e));
            }
            //need to either update suggest or HON info text view
            if(inHON)info.setText(s.toString());
            else suggest_info.setText(s.toString());
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
        mp.reset();
        setContentView(R.layout.activity_suggest);
        //now initialize gui objects
        suggestimg= (ImageView) findViewById(R.id.suggestview);
        next= (Button) findViewById(R.id.next);
        prev= (Button) findViewById(R.id.prev);
        replay_suggest= (Button) findViewById(R.id.replay_suggest);
        suggest_info=(TextView) findViewById(R.id.songInfo_Suggest);
        //now we suggest...
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
            songs.get(currSongHON).hot=true;
            Log.d("HONCLICK","it's hot");
        }
        else if(clicked.getText().equals("NOT")){
            songs.get(currSongHON).hot=false;
            Log.d("HONCLICK","it's not");
        }

        //play next song
        currSongHON++;
       if(currSongHON<songs.size()) playSong(songs.get(currSongHON),true);
        else DoneClick(v);//force the app to move on to done phase...done rating songs...
    }

    public void SuggestClick(View v){
        Button clicked = (Button)v;
        mp.stop();
        mp.reset();
        //if next...wrap counter around if necessary and play it
        if(clicked.getText().equals("Next")){
            currSongSuggest++;
            if(currSongSuggest==numSuggestSongs)currSongSuggest=0;
            playSong(suggested_songs.get(currSongSuggest),false);
        }
        //if prev...same deal but backwards
        else if(clicked.getText().equals("Previous")){
            currSongSuggest--;
            if(currSongSuggest==-1)currSongSuggest=numSuggestSongs-1;
            playSong(suggested_songs.get(currSongSuggest),false);
        }
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
    {//this will produce an ArrayList of SongInfoObjects....and we will set that to a global var
        //then we can loop through it just like HON
        //for now just going to make the arrayList the first x songs from songs
        suggested_songs=new ArrayList<SongInfo>();
        for(int i=0;i<numSuggestSongs;i++){
            suggested_songs.add(songs.get(i));
        }
        //note we will also play the first song from here.
        currSongSuggest=0;
        playSong(suggested_songs.get(0),false);
    }

    //this will make http reqest and get the bitmap for the image url
    public Bitmap getbmpfromURL(String surl){
        try {
            URL url = new URL(surl);
            HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
            urlcon.setDoInput(true);
            urlcon.connect();
            InputStream in = urlcon.getInputStream();
            Bitmap mIcon = BitmapFactory.decodeStream(in);
            urlcon.disconnect();
            return  mIcon;
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onBackPressed(){
    //for now we don't want user pressing back button here...this disables it essentially
        mp.pause();
        mp.reset();
        super.onBackPressed();
    }


}
