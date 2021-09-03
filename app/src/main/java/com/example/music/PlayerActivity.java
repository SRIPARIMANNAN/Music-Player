package com.example.music;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class PlayerActivity extends AppCompatActivity
{


    Button btnplay,btnnext,btnprev,btnff,frnfr,ytbtn;
    TextView txtsname,txtsstart,txtsstop;
    static String sname;
    public static final String Extra_Name="song_name";
    
    static int position;
    static int ps;
    static int position_sorted;
    static ArrayList<String> trackList;
    static ArrayList<String> sorted_trackList;
    static ArrayList<String> trackUrl;
    static ArrayList<String> trackDuration;

    HashMap<String,songDB> songMap;
    int cpos;
    Uri uri;
    Boolean playcheck=false;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getSupportActionBar().hide();

        btnplay=findViewById(R.id.playButton);
        btnnext=findViewById(R.id.buttonnext);
        btnprev=findViewById(R.id.buttonprevious);
        mediaPlayer.seekMusic=findViewById(R.id.seekbar);
        txtsname=findViewById(R.id.txtsongName);
        txtsstart=findViewById(R.id.textstart);
        txtsstop=findViewById(R.id.textstop);
        imageView=findViewById(R.id.imageview);
        ytbtn=findViewById(R.id.ytbtn);

        Intent intent = getIntent();
        Bundle bundle=intent.getExtras();
        sname=intent.getStringExtra("songname");
        position_sorted=bundle.getInt("pos");
        trackDuration=oneFragment.trackduration;
        trackList=oneFragment.trackList;
        trackUrl=oneFragment.trackurl;
        sorted_trackList=oneFragment.sorted_trackList;
        position=trackList.indexOf(sname);
        cpos=bundle.getInt("cpos");
        playcheck=bundle.getBoolean("playcheck");


        if(cpos>0)
        {

        }
        else
        {
            cpos=0;
        }


        uri=Uri.parse(trackUrl.get(position).toString());
        sname=trackList.get(position);
        System.out.println(trackUrl.get(position).toString()+"**************************************************************");
        System.out.println(uri+"**************************************************************");
        String d=trackDuration.get(position);

        playSong(uri,sname,d,cpos,playcheck);

        btnplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.mediaPlayer.isPlaying())
                {
                    btnplay.setBackgroundResource(R.drawable.playnew);
                    MainActivity.fplay.setBackgroundResource(R.drawable.playnew);
                    mediaPlayer.mediaPlayer.pause();
                }
                else
                {
                    btnplay.setBackgroundResource(R.drawable.pauset);
                   MainActivity.fplay.setBackgroundResource(R.drawable.pauset);
                    mediaPlayer.mediaPlayer.start();
                }
            }
        });

        mediaPlayer.seekMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b)
                {
                    mediaPlayer.mediaPlayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

       btnnext.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view)
           {
               nextSong();
           }
       });
        btnprev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                prevSong();

            }
        });



        ytbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder link= new StringBuilder("https://www.youtube.com/results?search_query=");
                link.append(PlayerActivity.sname);
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(link.toString()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.google.android.youtube");
                startActivity(intent);

            }
        });

    }
    final Handler handler= new Handler();
    private void playSong(Uri uri,String songName,String duration,int cpos,Boolean playcheck)
    {

        if(playcheck)
        {
            if (mediaPlayer.mediaPlayer != null) {

                if (mediaPlayer.mediaPlayer.isPlaying()) {
                    mediaPlayer.mediaPlayer.stop();
                }
                mediaPlayer.mediaPlayer.release();
            }
            try {

                txtsname.setText(songName);
                txtsname.setSelected(true);



                double dur = Integer.parseInt(duration);
                txtsstop.setText(timerConversion((long) dur));

                mediaPlayer.seekMusic.setMax((int) dur);

                // mediaPlayer.setDataSource(PlayerActivity.this, uri);
                mediaPlayer.mediaPlayer= MediaPlayer.create(PlayerActivity.this, uri);
                System.out.println(mediaPlayer.mediaPlayer.getDuration() + "" + Integer.parseInt(duration) + "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                mediaPlayer.mediaPlayer.start();
                if (cpos != 0) {
                    mediaPlayer.mediaPlayer.seekTo(cpos);
                }
                btnplay.setBackgroundResource(R.drawable.pauset);
                imageView.setImageResource(R.drawable.hhhh);
                MainActivity.fplay.setBackgroundResource(R.drawable.pauset);


                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            double cp = mediaPlayer.mediaPlayer.getCurrentPosition();
                            txtsstart.setText(timerConversion((long) cp));
                            mediaPlayer.seekMusic.setProgress((int) cp);
                            if (!mediaPlayer.mediaPlayer.isPlaying()) {
                                btnplay.setBackgroundResource(R.drawable.playt);
                                MainActivity.fplay.setBackgroundResource(R.drawable.playt);
                            }
                            handler.postDelayed(this, 1000);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                };
                handler.postDelayed(runnable, 1000);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }









        /*if(mediaPlayer.getCurrentPosition()==Integer.parseInt(duration))
        {
            nextSong();
        }*/
    }

    public String timerConversion(long value) {
        String audioTime;
        int dur = (int) value;
        int hrs = (dur / 3600000);
        int mns = (dur / 60000) % 60000;
        int scs = dur % 60000 / 1000;

        if (hrs > 0) {
            audioTime = String.format("%02d:%02d:%02d", hrs, mns, scs);
        } else {
            audioTime = String.format("%02d:%02d", mns, scs);
        }
        return audioTime;
    }
    public void nextSong()
    {
        int poss=position_sorted;
        poss+=1;
        if(poss>=sorted_trackList.size())
        {
            poss=position_sorted;
        }
        String name=sorted_trackList.get(poss);
        System.out.println(String.valueOf(poss)+name+"************************nxt***********************************");
        int pos=trackList.indexOf(name);
        Uri uri=Uri.parse(trackUrl.get(pos));
        //String name=trackList.get(pos);
        String duration=trackDuration.get(pos);
        playSong(uri,name,duration,0,true);
        position_sorted=poss;
    }

    public void prevSong()
    {

        int poss=position_sorted;
        poss-=1;
        if(poss<0)
            {
                poss=position_sorted;
        }
        String name=sorted_trackList.get(poss);
        System.out.println(String.valueOf(poss)+name+"**************************prev*********************************");

        int pos=trackList.indexOf(name);
        Uri uri=Uri.parse(trackUrl.get(pos));
        //String name=trackList.get(pos);
        String duration=trackDuration.get(pos);
        playSong(uri,name,duration,0,true);
        position_sorted=poss;
    }


}