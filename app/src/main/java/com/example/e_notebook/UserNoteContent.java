package com.example.e_notebook;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.List;

import myownview.PictureAndTextEditorView;

public class UserNoteContent extends AppCompatActivity {
    public static UserNoteContent instance;

    PictureAndTextEditorView editorView;
    FloatingActionButton insert_fab;
    FloatingActionButton save_fab;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_note_content);

        editorView = (PictureAndTextEditorView)findViewById(R.id.note_content);
        insert_fab = (FloatingActionButton)findViewById(R.id.note_content_fab_insert);
        save_fab = (FloatingActionButton)findViewById(R.id.note_content_fab_save);

        verifyStoragePermissions(this);

        insert_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });

        save_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> str = editorView.getmContentList();
                Log.i("Content", str.toString());
            }
        });

        instance = this;
    }

    //auto-get permission to read SD card
    public static void verifyStoragePermissions(Activity activity){
        try{
            int permission = ActivityCompat.checkSelfPermission(activity, "android.permission.WRITE_EXTERNAL_STORAGE");
            if(permission != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }catch (Exception e){
            e.printStackTrace();
            Log.i("my error", e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null){
            if(requestCode == 1){
                Uri selectedImg = data.getData();
                String imgURL = getAbsolutePath(selectedImg);
                editorView.insertBitmap(imgURL);
            }
        }
    }

    //get the file's absolute path in the local device
    public String getAbsolutePath(Uri uri){
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
        if(cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
}
