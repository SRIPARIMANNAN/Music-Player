package com.example.music;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
{

    public static final int RUNTIME_PERMISSION_CODE = 7;
    TabLayout tb;
    ViewPager viewPager;
    ContentResolver contentResolver;
    Uri uri;
    Cursor cursor;
     ArrayList<File> trackList;
    String[] tracks=new String[]{"hello"};
    ArrayList<String> arrayList;
    Button fprev;
    Button fnext;
    static Button fplay;
    static TextView ftrackname;





    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar ab=getSupportActionBar();
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
        runtimePermission();
        tb=findViewById(R.id.tabLayout);
        viewPager=findViewById(R.id.viewpager);


        arrayList = new ArrayList<>();
        arrayList.add("Tracks");
        arrayList.add("Favourites");
        arrayList.add("PlayList");

        fnext=findViewById(R.id.fnext);
        fprev=findViewById(R.id.fprevious);
        fplay=findViewById(R.id.fplay);

        ftrackname=findViewById(R.id.textView2);
        prepareViewPager(viewPager,arrayList);
        tb.setupWithViewPager(viewPager);

        fnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int poss=PlayerActivity.position_sorted;
                poss+=1;
                if(poss>=PlayerActivity.sorted_trackList.size())
                {
                    poss=PlayerActivity.position_sorted;
                }
                String name=PlayerActivity.sorted_trackList.get(poss);
                System.out.println(String.valueOf(poss)+name+"**************************nxt*********************************");

                int pos=PlayerActivity.trackList.indexOf(name);
                Uri uri=Uri.parse(PlayerActivity.trackUrl.get(pos));
                //String name=trackList.get(pos);
                String duration=PlayerActivity.trackDuration.get(pos);
                playSong(uri,name,duration);
                PlayerActivity.position_sorted=poss;
            }
        });

        fprev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int poss=PlayerActivity.position_sorted;
                poss-=1;
                if(poss<0)
                {
                    poss=PlayerActivity.position_sorted;
                }
                String name=PlayerActivity.sorted_trackList.get(poss);
                System.out.println(String.valueOf(poss)+name+"**************************prev*********************************");

                int pos=PlayerActivity.trackList.indexOf(name);
                Uri uri=Uri.parse(PlayerActivity.trackUrl.get(pos));
                //String name=trackList.get(pos);
                String duration=PlayerActivity.trackDuration.get(pos);
                playSong(uri,name,duration);
                PlayerActivity.position_sorted=poss;
            }
        });

        fplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.mediaPlayer.isPlaying())
                {
                    fplay.setBackgroundResource(R.drawable.playnew);
                    mediaPlayer.mediaPlayer.pause();
                }
                else
                {
                    fplay.setBackgroundResource(R.drawable.pauset);
                    mediaPlayer.mediaPlayer.start();
                }
            }

        });

        ftrackname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(MainActivity.this,PlayerActivity.class);
                intent.putExtra("songname",PlayerActivity.sname);
                intent.putExtra("pos",10);
                intent.putExtra("cpos",mediaPlayer.mediaPlayer.getCurrentPosition());
                intent.putExtra("playcheck",mediaPlayer.mediaPlayer.isPlaying());
                startActivity(intent);

            }
        });
    }

    private void playSong(Uri uri, String name, String duration)
    {

        if(mediaPlayer.mediaPlayer!=null) {

            if (mediaPlayer.mediaPlayer.isPlaying()) {
                mediaPlayer.mediaPlayer.stop();
            }
            mediaPlayer.mediaPlayer.release();
        }
        try{

            if(!name.equals("")){
                ftrackname.setText(name);
                ftrackname.setSelected(true);
            }
            else
            {
                ftrackname.setText(PlayerActivity.sorted_trackList.get(mediaPlayer.mediaPlayer.getAudioSessionId()));
                ftrackname.setSelected(true);
            }
            //double dur=Integer.parseInt(duration);
            //txtsstop.setText(timerConversion((long)dur));
            //seekMusic.setMax((int)dur);
            // mediaPlayer.setDataSource(PlayerActivity.this, uri);
            mediaPlayer.mediaPlayer= MediaPlayer.create(MainActivity.this,uri);
            mediaPlayer.mediaPlayer.start();
            fplay.setBackgroundResource(R.drawable.pauset);
            //imageView.setImageResource(R.drawable.hhhh);



            }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void prepareViewPager(ViewPager viewPager, ArrayList<String> arrayList)
    {
        MyAdapter adapter=new MyAdapter(getSupportFragmentManager());
        adapter.addFragment(new oneFragment(),"Tracks");
        adapter.addFragment(new twoFragment(),"Favourites");
        adapter.addFragment(new thirdFragment(),"Playlist");
        viewPager.setAdapter(adapter);

    }
    protected void runtimePermission()
    {

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

                if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){

                    AlertDialog.Builder alert_builder = new AlertDialog.Builder(MainActivity.this);
                    alert_builder.setMessage("External Storage Permission is Required.");
                    alert_builder.setTitle("Please Grant Permission.");
                    alert_builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            ActivityCompat.requestPermissions(
                                    MainActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    RUNTIME_PERMISSION_CODE

                            );
                        }
                    });

                    alert_builder.setNeutralButton("Cancel",null);

                    AlertDialog dialog = alert_builder.create();

                    dialog.show();

                }
                else {

                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            RUNTIME_PERMISSION_CODE
                    );
                }
            }else {


            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case RUNTIME_PERMISSION_CODE:
            {
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {

                }
                else
                {

                }
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
            MenuInflater inflater=getMenuInflater();
            inflater.inflate(R.menu.topmenu,menu);
        MenuItem menuItem=menu.findItem(R.id.search);
        SearchView searchView =(SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search Here");

        MenuItem.OnActionExpandListener onActionExpandListener= new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem)
            {

                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                return false;
            }
        };
        /*menu.findItem(R.id.search).setOnActionExpandListener(onActionExpandListener);
        SearchView searchView =(SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint("Search  Here");*/
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s)
            {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s)
            {
                new oneFragment().search(s);

                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch(item.getItemId())
        {
            case  R.id.search:

               break;
        }
        return super.onOptionsItemSelected(item);
    }



}
