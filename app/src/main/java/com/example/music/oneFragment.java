package com.example.music;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class oneFragment extends Fragment
{

    ListView lvTrack;
    MainActivity obj= new MainActivity();
    ContentResolver contentResolver;
    String[] tracks= new String[]{};
    String[] tracksurl= new String[]{};
    String[] tracksdur= new String[]{};
    static ArrayList<String> trackList;
    static ArrayList<String> sorted_trackList;
    static ArrayList<String> trackurl;
    static ArrayList<String> trackduration;
    static ArrayAdapter ta;
    Uri uri;
    Cursor cursor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_one, container, false);
        lvTrack=(ListView) v.findViewById(R.id.lvTrack);
        getAllMedia();

        sorted_trackList=new ArrayList<>(trackList);
        Collections.sort(sorted_trackList);
        String[] temp=new String[]{};
        ta= new ArrayAdapter<String>(getContext(),R.layout.row,sorted_trackList);
        lvTrack.setAdapter(ta);


        lvTrack.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String songName = (String) lvTrack.getItemAtPosition(i);
                System.out.println(songName+"---------------------------------");
                Intent intent = new Intent(getContext(),PlayerActivity.class);
                intent.putExtra("songname",songName);
                intent.putExtra("pos",i);
                intent.putExtra("playcheck",true);
                startActivity(intent);
                MainActivity.ftrackname.setText(songName);
                MainActivity.ftrackname.setSelected(true);

            }
        });
        return v;
    }

    protected  void getAllMedia()
    {
        trackList=new ArrayList<>(Arrays.asList(tracks));
        trackurl=new ArrayList<>(Arrays.asList(tracksurl));
        trackduration=new ArrayList<>(Arrays.asList(tracksdur));

        contentResolver= getActivity().getContentResolver();
        uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        cursor=contentResolver.query(uri,null,null,null,null,null);


        if(cursor==null)
        {
            Toast.makeText(getContext(),"wrong",Toast.LENGTH_SHORT);
        }else if(!cursor.moveToFirst())
            Toast.makeText(getContext(),"no Music",Toast.LENGTH_LONG);
        else
        {

            songDB songdb;
            do
            {
                int title=cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int url=cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                int duration=cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                String dura=cursor.getString(duration);
                String urll=cursor.getString(url);
                String st=cursor.getString(title);
                trackList.add(st);
                trackurl.add(urll);
                trackduration.add(dura);

            }while(cursor.moveToNext());

        }

    }


    public void search(String s)
    {
        System.out.println(s+"----------------------------------------------------------------------");
       // getAllMedia();
        if(ta!=null){
            ta.getFilter().filter(s);

        }
    }
}