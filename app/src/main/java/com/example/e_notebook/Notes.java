package com.example.e_notebook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javaclass.IpConfig;
import javaclass.Note;
import javaclass.Notebook;
import javaclass.NotesAdapter;
import javaclass.myItemDecoration;
import javaclass.myStaggeredItemDecoration;

public class Notes extends AppCompatActivity {
    public static Notes instance;

    private List<Note> mNoteList = new ArrayList<>();
    RecyclerView recyclerView;
    private String mNoteName;
    String[] NoteList;
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes);

        recyclerView = findViewById(R.id.note_list);
        Intent intent = getIntent();
        mNoteName = getIntent().getStringExtra("notename");
        mNoteName = mNoteName.replace(" ", "");
        SharedPreferences pref = getSharedPreferences("notebookname", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("notebookname", mNoteName);
        editor.apply();

        ((FloatingActionButton)findViewById(R.id.createnote_fab)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new InputDialog.Builder(Notes.this)
                        .setTitle("Create Your New Note:")
                        .setInputHint("Your Note's Name")
                        .setPositiveButton("OK", new InputDialog.ButtonActionListener() {
                            @Override
                            public void onClick(CharSequence inputText) {
                                //add a new note to notelist
                                for(int i = 0; i < mNoteList.size(); i++){
                                    if(inputText.toString().equals(mNoteList.get(i).getTitle())){
                                        Toast.makeText(Notes.this, "The File Have Existed", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }

                                new SendNoteList().execute(mNoteName, inputText.toString(), new IpConfig().getRoot()+"pictures/default.png");
                            }
                        })
                        .setNegativeButton("Cancel", new InputDialog.ButtonActionListener() {
                            @Override
                            public void onClick(CharSequence inputText) {
                                //do nothing
                            }
                        }).show();
            }
        });

        new GetNoteList().execute(mNoteName);

        instance = this;
    }

    //initial the RecyclerView which contains the note items
    private void initRecyclerView(){
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        NotesAdapter notesAdapter = new NotesAdapter(mNoteList, Notes.this);
        recyclerView.setAdapter(notesAdapter);
        recyclerView.addItemDecoration(new myStaggeredItemDecoration(Notes.this));
    }

    class SendNoteList extends AsyncTask<String, Void, String>{
        ProgressDialog pDlg = new ProgressDialog(Notes.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDlg.setCancelable(false);
            pDlg.setMessage("Updating...");
            if(!pDlg.isShowing()) pDlg.show();
        }

        @Override
        protected String doInBackground(String...strings) {
            Bitmap bitmap = new EnotebookHttpClient().getBitmapFromServer(strings[2]);
            mNoteList.add(new Note(strings[1], bitmap));

            EnotebookHttpClient ehc = new EnotebookHttpClient();
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject;
            String notebook_name = strings[0];
            notebook_name = notebook_name.replace(" ","");
            SharedPreferences pref = getSharedPreferences("who_login", MODE_PRIVATE);
            String who_login = pref.getString("WhoIsLogin", "");
            if(who_login.isEmpty()) return "You Are Offline";
            try{
                for(int i = 0; i < mNoteList.size(); i++){
                    jsonObject = new JSONObject();
                    Note note = mNoteList.get(i);
                    jsonObject.put("id", i);
                    jsonObject.put("title", note.getTitle());
                    jsonObject.put("imgname", "default.png");
                    jsonObject.put("username", who_login);
                    jsonObject.put("notebook", notebook_name);
                    jsonArray.put(jsonObject);
                }
            }catch (JSONException e){
                return "Unknown Error";
            }

            String response = ehc.send(new IpConfig().getIpPath()+"getnotelist.php", jsonArray.toString(), "application/json");
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            super.onPostExecute(result);
            if(pDlg.isShowing()) pDlg.dismiss();
            pDlg = null;
        }
    }

    //parse the response of sending notelist request
    public void parseSendNoteListResponse(String response){
        if(response.equals("You Are Offline")){
            Toast.makeText(Notes.this, "Create Failed:You Are Offline", Toast.LENGTH_SHORT).show();
            return;
        }else if(response.equals("Unknown Error")){
            Toast.makeText(Notes.this, "Create Failed:Unknown Error", Toast.LENGTH_SHORT).show();
            return;
        }else if(response.equals("success")){
            Toast.makeText(Notes.this, "Create Successfully", Toast.LENGTH_SHORT).show();
            initRecyclerView();
        }
    }

    //fill the notelist
    class GetNoteList extends AsyncTask<String, Void, String>{
        ProgressDialog pDlg = new ProgressDialog(Notes.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDlg.setCancelable(false);
            pDlg.setMessage("Loading...");
            if(!pDlg.isShowing()) pDlg.show();
        }

        @Override
        protected String doInBackground(String...strings) {
            EnotebookHttpClient ehc = new EnotebookHttpClient();
            JSONObject jsonObject = new JSONObject();
            try{
                SharedPreferences pref = getSharedPreferences("who_login", MODE_PRIVATE);
                String who_login = pref.getString("WhoIsLogin", "");
                if(who_login.isEmpty()) return "You Are Offline";
                String notebook_name = strings[0];
                notebook_name = notebook_name.replaceAll("\\s", "");
                jsonObject.put("username", who_login);
                jsonObject.put("notebook", notebook_name);
            }catch (JSONException e){
                return "Unknown Error";
            }

            String response = ehc.send(new IpConfig().getIpPath()+"sendnotelist.php", jsonObject.toString(), "application/json");
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(pDlg.isShowing()) pDlg.dismiss();
            pDlg = null;
            parseGetnotesResponse(result);
            initRecyclerView();
        }
    }

    //parse the response of getting notes request
    public void parseGetnotesResponse(String response){
        if(response.equals("You Are Offline")){
            Toast.makeText(Notes.this, "Load Failed:You Are Offline", Toast.LENGTH_SHORT).show();
            return;
        }else if(response.equals("Unknow Error")){
            Toast.makeText(Notes.this, "Load Failed:Unknown Error", Toast.LENGTH_SHORT).show();
            return;
        }else if(response.equals("none")){
            Toast.makeText(Notes.this, "You don't have any notes yet! Come on and Create One!", Toast.LENGTH_SHORT).show();
            return;
        }else{
            NoteList = response.split("&");
            for(i = 0; i < NoteList.length; i++){

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String[] note = NoteList[i].split("#");
                        String imgURL = note[2].replace("../", new IpConfig().getRoot());
                        Bitmap bitmap = new EnotebookHttpClient().getBitmapFromServer(imgURL);
                        mNoteList.add(new Note(note[1], bitmap));
                    }
                });
                thread.start();
                try{
                    thread.join();
                }catch (InterruptedException e){
                    e.getStackTrace();
                    Log.i("My Info", e.getMessage());
                    return;
                }
            }
        }
    }
}
