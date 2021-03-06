package com.example.filehunt;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.filehunt.Adapter.Adapter_PhotosFolder;
import com.example.filehunt.Model.Model_Docs;
import com.example.filehunt.Model.Model_images;
import com.example.filehunt.Utils.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static com.example.filehunt.Class.Constants.AUDIO;
import static com.example.filehunt.Class.Constants.IMAGES;
import static com.example.filehunt.Class.Constants.POSITION;
import static com.example.filehunt.Class.Constants.VIDEO;

public class Category_Explore_Activity extends AppCompatActivity {

    int position;
    Context ctx;
    boolean boolean_folder;
    GridView gv_folder;
    Adapter_PhotosFolder obj_adapter;
    public static ArrayList<Model_images> al_images = new ArrayList<>();

    Uri uri;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx=Category_Explore_Activity.this;

        setContentView(R.layout.category_explore_activity);
        gv_folder = (GridView)findViewById(R.id.gv_folder);

        if(getIntent().getExtras() !=null)

           position=getIntent().getIntExtra(POSITION,0) ;


        switch (position)
        {
            case 0:
                Load_Media(position);
                break;
            case 1:
                FetchVideos();
                    break;
            case 2:
               FetchAudio();
                break;
            case 3:

                break;

                }

                gv_folder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(position==0)
                {
                Intent intent = new Intent(getApplicationContext(), PhotosActivityRe.class);
                intent.putExtra("value",i);
                startActivity(intent);
                }
                else if(position==1)
                {
                    Intent intent = new Intent(getApplicationContext(), VideoActivityRe.class);
                    intent.putExtra("value",i);
                    startActivity(intent);
                }

                else if(position==2)
                {
                    Intent intent = new Intent(getApplicationContext(), AudioActivityRe.class);
                    intent.putExtra("value",i);
                    startActivity(intent);
                }

            }
        });


        }

      public ArrayList<Model_images>  Load_Media(int  MediaType)
    {
        al_images.clear();

        int int_position = 0;

        Cursor cursor;
        int column_index_data, column_index_folder_name,column_index_date_modified;

        String absolutePathOfImage = null;
        if(MediaType==IMAGES)
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        else if(MediaType==VIDEO)
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//            else if(MediaType==AUDIO)
//            uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;



        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.MediaColumns.DATE_MODIFIED};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = getApplicationContext().getContentResolver().query( uri, projection, null, null, orderBy + " DESC");

          column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
          column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
          column_index_date_modified= cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED);

        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            Log.e("Column", absolutePathOfImage);
            Log.e("Folder", cursor.getString(column_index_folder_name));

            for (int i = 0; i < al_images.size(); i++) {
                if (al_images.get(i).getStr_folder().equals(cursor.getString(column_index_folder_name))) {
                    boolean_folder = true;
                    int_position = i;
                    break;
                } else {
                    boolean_folder = false;
                }
            }


            if (boolean_folder) {

                ArrayList<String> al_path = new ArrayList<>();
                al_path.addAll(al_images.get(int_position).getAl_imagepath());
                al_path.add(absolutePathOfImage);
                al_images.get(int_position).setAl_imagepath(al_path);

            } else {
                ArrayList<String> al_path = new ArrayList<>();
                al_path.add(absolutePathOfImage);
                Model_images obj_model = new Model_images();
                obj_model.setStr_folder(cursor.getString(column_index_folder_name));
                obj_model.setDate_modified(LongToDate(cursor.getString(column_index_date_modified)));
                obj_model.setAl_imagepath(al_path);

                al_images.add(obj_model);


            }


        }


//        for (int i = 0; i < al_images.size(); i++) {
////            Log.e("FOLDER", al_images.get(i).getStr_folder());
////            for (int j = 0; j < al_images.get(i).getAl_imagepath().size(); j++) {
////                Log.e("FILE", al_images.get(i).getAl_imagepath().get(j));
////            }
////        }

        obj_adapter = new Adapter_PhotosFolder(getApplicationContext(),al_images);
        gv_folder.setAdapter(obj_adapter);
        return al_images;
    }
    private  ArrayList<Model_images>   FetchVideos()
    {
        al_images.clear();
        int int_position = 0;
        Cursor cursor;
        int column_index_data, column_index_folder_name,column_index_date_modified,thumb;
        String absolutePathOfImage = null;
        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.MediaColumns.DATE_MODIFIED,MediaStore.Video.Thumbnails.DATA};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = getApplicationContext().getContentResolver().query( uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        column_index_date_modified= cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED);
        thumb= cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);

        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            Log.e("Column", absolutePathOfImage);
            Log.e("Folder", cursor.getString(column_index_folder_name));
                 String thumbstr=cursor.getString(thumb);


            for (int i = 0; i < al_images.size(); i++) {
                if (al_images.get(i).getStr_folder().equals(cursor.getString(column_index_folder_name))) {
                    boolean_folder = true;
                    int_position = i;
                    break;
                } else {
                    boolean_folder = false;
                }
            }


            if (boolean_folder) {

                ArrayList<String> al_path = new ArrayList<>();
                ArrayList<String> al_vdoThumb = new ArrayList<>();
                al_path.addAll(al_images.get(int_position).getAl_imagepath());
                al_vdoThumb.addAll(al_images.get(int_position).getAl_vdoThumb());
                al_path.add(absolutePathOfImage);
                al_vdoThumb.add(thumbstr);
                al_images.get(int_position).setAl_imagepath(al_path);
                al_images.get(int_position).setAl_vdoThumb(al_vdoThumb);

            } else {
                ArrayList<String> al_path = new ArrayList<>();
                ArrayList<String> al_vdoThumb = new ArrayList<>();
                al_path.add(absolutePathOfImage);
                al_vdoThumb.add(thumbstr);
                Model_images obj_model = new Model_images();
                obj_model.setStr_folder(cursor.getString(column_index_folder_name));
                obj_model.setDate_modified(LongToDate(cursor.getString(column_index_date_modified)));
                obj_model.setAl_imagepath(al_path);
                obj_model.setAl_vdoThumb(al_vdoThumb);
                al_images.add(obj_model);
                }



        }



        obj_adapter = new Adapter_PhotosFolder(getApplicationContext(),al_images);
        gv_folder.setAdapter(obj_adapter);
        return al_images;
    }


    private ArrayList<Model_images>  FetchAudio()
    {
        al_images.clear();
        int int_position = 0;
        Cursor cursor;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";  // only music file will be fetched
        int column_index_data, column_index_duration,column_index_folder_name,column_index_date_modified;
        String absolutePathOfImage = null;
        String fileDuration=null;
        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.ALBUM,MediaStore.Audio.Media.DATE_MODIFIED,MediaStore.Audio.Media.DURATION};

        final String orderBy = MediaStore.Audio.Media.DATE_MODIFIED;
        cursor = getApplicationContext().getContentResolver().query( uri, projection, selection, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
        column_index_date_modified= cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED);
        column_index_duration=cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            fileDuration= Utility.convertDuration(cursor.getLong(column_index_duration));

            Log.e("Duration",fileDuration);
            Log.e("Column", absolutePathOfImage);
            Log.e("Folder", cursor.getString(column_index_folder_name));

            for (int i = 0; i < al_images.size(); i++) {
                if (al_images.get(i).getStr_folder().equals(cursor.getString(column_index_folder_name))) {
                    boolean_folder = true;
                    int_position = i;
                    break;
                } else {
                    boolean_folder = false;
                }
            }


            if (boolean_folder) {

                ArrayList<String> al_path = new ArrayList<>();
                ArrayList<String> al_fileduration = new ArrayList<>();

                al_path.addAll(al_images.get(int_position).getAl_imagepath());
                al_fileduration.addAll(al_images.get(int_position).getAl_FileDuration());

                al_path.add(absolutePathOfImage);
                al_fileduration.add(fileDuration);

                al_images.get(int_position).setAl_imagepath(al_path);
                al_images.get(int_position).setAl_FileDuration(al_fileduration);


                } else {
                ArrayList<String> al_path = new ArrayList<>();
                ArrayList<String> al_fileduration = new ArrayList<>();


                al_path.add(absolutePathOfImage);
                al_fileduration.add(fileDuration);
                Model_images obj_model = new Model_images();
                obj_model.setStr_folder(cursor.getString(column_index_folder_name));
                obj_model.setDate_modified(LongToDate(cursor.getString(column_index_date_modified)));
                obj_model.setAl_imagepath(al_path);
                obj_model.setAl_FileDuration(al_fileduration);
                al_images.add(obj_model);


            }


        }


//        for (int i = 0; i < al_images.size(); i++) {
//            Log.e("FOLDER", al_images.get(i).getStr_folder());
//            for (int j = 0; j < al_images.get(i).getAl_imagepath().size(); j++) {
//                Log.e("FILE", al_images.get(i).getAl_imagepath().get(j));
//            }
//        }
//

        obj_adapter = new Adapter_PhotosFolder(getApplicationContext(),al_images);
        gv_folder.setAdapter(obj_adapter);
        return al_images;


    }



    private String LongToDate(String longV)
    {
        long input=Long.parseLong(longV);
        Date date = new Date(input*1000); // *1000 gives accurate date otherwise returns 1970
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setCalendar(cal);
        cal.setTime(date);
        return sdf.format(date);

    }





}

