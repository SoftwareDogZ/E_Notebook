package com.example.e_notebook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javaclass.IpConfig;
import myownview.PictureAndTextEditorView;

public class UserNoteContent extends AppCompatActivity {
    public static UserNoteContent instance;

    PictureAndTextEditorView editorView;
    FloatingActionButton insert_fab;
    FloatingActionButton save_fab;
    String notename;
    String notelistname;
    String username;
    List<String> mContent;
    String[] contents;
    String content;

    private Handler mHandler;

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

        notename = getIntent().getStringExtra("notename");
        SharedPreferences pref = getSharedPreferences("notebookname", MODE_PRIVATE);
        notelistname = pref.getString("notebookname", "");
        SharedPreferences pref1 = getSharedPreferences("who_login", MODE_PRIVATE);
        username = pref1.getString("WhoIsLogin", "");

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
                mContent = editorView.getmContentList();
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("username", username);
                    jsonObject.put("notelistname", notelistname);
                    jsonObject.put("notename", notename);
                    jsonObject.put("contentNum", mContent.size());
                    for(int i = 0; i < mContent.size(); i++){
                        String content = mContent.get(i);
                        //there is no '**LIML**' in the String, then it is a path of image
                        if(content.indexOf("**LIML**") == -1){
                                String[] path = mContent.get(i).split("/");
                                content = path[path.length - 1];
                        }
                        jsonObject.put("content"+Integer.toString(i), content);
                    }
                }catch (Exception e){
                    Toast.makeText(UserNoteContent.this, "Save Failed:Unknown Error", Toast.LENGTH_SHORT).show();
                    return;
                }
                new SendNoteContent().execute(jsonObject.toString());
            }
        });

        mHandler = new Handler(){
            int i = 0;
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:{
                        editorView.insertBitmapFromServer(contents[i], (Bitmap)msg.obj, 2);
                        i++;
                        break;
                    }
                    case 2:{
                        editorView.insertWordsFromServer((String)msg.obj);
                        i++;
                        break;
                    }
                    default:break;
                }
            }
        };

        new GetNoteContent().execute("");

        instance = this;
    }

    //get permission to read SD card
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

    //open the photo album
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null){
            if(requestCode == 1){
                Uri selectedImg = data.getData();
                String imgURL = getAbsolutePath(selectedImg);
                editorView.insertBitmap(imgURL, 1);
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

    //get user note content from server
    class GetNoteContent extends AsyncTask<String, Void, String>{
        ProgressDialog pDlg = new ProgressDialog(UserNoteContent.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDlg.setCancelable(false);
            pDlg.setMessage("Loading...");
            if(!pDlg.isShowing()) pDlg.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            EnotebookHttpClient ehc = new EnotebookHttpClient();
            JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.put("username", username);
                jsonObject.put("notebookname", notelistname);
                jsonObject.put("notename", notename);
            }catch (JSONException e){
                return "Unknown Error";
            }

            String response = ehc.send(new IpConfig().getIpPath()+"sendnotecontent.php", jsonObject.toString(), "application/json");
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(pDlg.isShowing()) pDlg.dismiss();
            pDlg = null;
            parseGetNoteContent(result);
        }

        //parse the response of get note content request
        public void parseGetNoteContent(final String response){
            if(response.equals("UnKnown Error")){
                Toast.makeText(UserNoteContent.this, "Load Failed:Unknown Error", Toast.LENGTH_SHORT).show();
                return;
            }else if(response.equals("none")){
                return;
            }else{
                final Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message msg;
                        contents = response.split("#%");
                        for(int i = 0; i < contents.length; i++){
                            content = contents[i];
                            if(content.indexOf("**LIML**") == -1){
                                msg = new Message();
                                msg.what = 1;
                                Bitmap bitmap = new EnotebookHttpClient().getBitmapFromServer(new IpConfig().getRoot()+"pictures/"+content);
                                msg.obj = bitmap;
                            }else{
                                msg = new Message();
                                msg.what = 2;
                                msg.obj = content;
                            }
                            //when this thread ends, the messages will be saved in a message queue
                            //and be processed one by one
                            //So I have to find a way to handle the massage sent just now immediately
                            mHandler.sendMessage(msg);
                        }
                    }
                });
                thread.start();
                try{
                    thread.join();
                }catch (InterruptedException e){
                    Toast.makeText(UserNoteContent.this, "Load Failed:Unknown Error", Toast.LENGTH_SHORT).show();
                    return;
                }
        }
        }
    }

    //send json file to server which define the format of the user's note
    class SendNoteContent extends AsyncTask<String, Void, String>{
        ProgressDialog pDlg = new ProgressDialog(UserNoteContent.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDlg.setCancelable(false);
            pDlg.setMessage("Uploading...");
            if(!pDlg.isShowing()) pDlg.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            EnotebookHttpClient ehc = new EnotebookHttpClient();
            String response = ehc.send(new IpConfig().getIpPath()+"getnoteformat.php", strings[0], "application/json");
            Log.i("from server", response);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(pDlg.isShowing()) pDlg.dismiss();
            pDlg = null;
            parseSendNoteContent(result);
        }
    }

    //parse the response of send note format request
    public void parseSendNoteContent(String response){
        if(response.equals("success")){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    EnotebookHttpClient ehc = new EnotebookHttpClient();
                    String content;
                    for(int i = 0; i < mContent.size(); i++){
                        content = mContent.get(i);
                        if(content.indexOf("**LIML**") == -1){
                            String response =  ehc.sendBitmapToServer(content);
                            if(response.equals("failed")){
                                Toast.makeText(UserNoteContent.this, "Save Failed:Cannot Save The Images", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }
                }
            });
            thread.start();
            try{
                thread.join();
            }catch (InterruptedException e){
                Toast.makeText(UserNoteContent.this, "Save Failed:Unknown Error", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(UserNoteContent.this, "Save Successfully", Toast.LENGTH_SHORT).show();
        }
    }
}
