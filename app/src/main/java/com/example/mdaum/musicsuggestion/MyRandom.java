package com.example.mdaum.musicsuggestion;

import java.util.*;

public class MyRandom { //just easier to use than Rand...

  private Random rn;

  public MyRandom(boolean Seeded){ //seeded is used for research purposes...do I want to generate same psuedo-random sequence or not?
	  rn=new Random();
	  if(Seeded)rn.setSeed(100);
  }

  public  int rand(int lo, int hi) { //lo is inclusive, hi is exclusive
     int n = hi - lo + 1;
     int i = rn.nextInt() % n;
     if (i < 0) i = -i;
     return lo + i;
  }

  @SuppressWarnings("deprecation")
public  String nextString(int lo, int hi) { //random a-z string of random size b/w lo (inclusive) and hi (exclusive)
     int n = rand(lo, hi);
     byte b[] = new byte[n];
     for (int i = 0; i < n; i++)
     b[i] = (byte)rand('a', 'z');
     return new String(b, 0);
  }

  public String nextString() {
     return nextString(5, 25);
  }
  
}