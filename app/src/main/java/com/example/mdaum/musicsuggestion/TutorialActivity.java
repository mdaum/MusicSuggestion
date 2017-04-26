package com.example.mdaum.musicsuggestion;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


public class TutorialActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
    }

    public void startMain(View v)
    {
        Intent main = new Intent(this, MainActivity.class);
        startActivity(main);
    }
}
