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

public class MainActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }




    public void startTutorial(View v)
    {
        Intent tut = new Intent(this, TutorialActivity.class);
        startActivity(tut);
    }
    public void startHotOrNot(View v)
    {
        Intent hon = new Intent(this, HON.class);
        startActivity(hon);
    }

    // listener for when media player has buffered enough to play

}
