package com.example.e_notebook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javaclass.*;

public class NoteBooks extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private List<Notebook> mNotebookList = new ArrayList<>();
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_books);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = (RecyclerView)findViewById(R.id.notebook_list);
        new FillNoteboolList().execute("");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.note_books, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //implement the action when users click the item in menu
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_create){
            CreateNotebook();
        }else if(id == R.id.nav_settings){
            Intent intent = new Intent(NoteBooks.this, SettingPage.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //initial the RecyclerView which contains the notebook items
    public void initRecyclerView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        NotebooksAdapter notebooksAdapter = new NotebooksAdapter(mNotebookList);
        recyclerView.setAdapter(notebooksAdapter);
        recyclerView.addItemDecoration(new myItemDecoration(NoteBooks.this));
    }

    //create a new notebook
    public void CreateNotebook(){
        new InputDialog.Builder(NoteBooks.this)
                .setTitle("Create Your New Notebook:")
                .setInputHint("Your Notebook's Name")
                .setPositiveButton("OK", new InputDialog.ButtonActionListener() {
                    @Override
                    public void onClick(CharSequence inputText) {
                        //add a new notebook to the notebooklist
                        Notebook newNotebook = new Notebook(inputText.toString(), R.drawable.defaultnote);
                        mNotebookList.add(newNotebook);

                        new SendNotebookListToServer().execute("");
                    }
                })
                .setNegativeButton("Cancel", new InputDialog.ButtonActionListener() {
                    @Override
                    public void onClick(CharSequence inputText) {
                        //do nothing
                    }
                }).show();
    }

    //fill the mNotebookList
    class FillNoteboolList extends AsyncTask<String, Void, String>{
        ProgressDialog pDlg = new ProgressDialog(NoteBooks.this);
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
                SharedPreferences pref = getSharedPreferences("who_login", MODE_PRIVATE);
                String who_login = pref.getString("WhoIsLogin", "");
                if(who_login.isEmpty()) return "You Are Offline";
                jsonObject.put("get", who_login);
            }catch (JSONException e){
                return "Unknown Error";
            }

            String response = ehc.send("http://10.253.221.78:81/Enotebook_server/sendnotebooklist.php", jsonObject.toString(), "application/json");

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(pDlg.isShowing()) pDlg.dismiss();
            pDlg = null;
            parseGetnotebooksResponse(result);
            initRecyclerView();
        }
    }

    //parse the response of the getting notebooklist request
    public void parseGetnotebooksResponse(String response){
        if(response.equals("You Are Offline")){
            Toast.makeText(NoteBooks.this, "Load Failed:You Are Offline", Toast.LENGTH_SHORT).show();
            return;
        }else if(response.equals("Unknown Error")){
            Toast.makeText(NoteBooks.this, "Load Failed:Unknown Error", Toast.LENGTH_SHORT).show();
            return;
        }else if(response.equals("none")){
            Toast.makeText(NoteBooks.this, "You don't have any notebooks yet! Come on and Create One!", Toast.LENGTH_SHORT).show();
            return;
        }else{
            String[] NotebookList = response.split("&");
            for(int i = 0; i < NotebookList.length; i++){
                String[] notebook = NotebookList[i].split("#");
                mNotebookList.add(new Notebook(notebook[i], R.drawable.defaultnote));
            }
        }
    }

    //send the notebooklist updated to server
    class SendNotebookListToServer extends AsyncTask<String, Void, String>{
        ProgressDialog pDlg = new ProgressDialog(NoteBooks.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDlg.setCancelable(false);
            pDlg.setMessage("Updating...");
            if(!pDlg.isShowing()) pDlg.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            EnotebookHttpClient ehc = new EnotebookHttpClient();
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject;
            SharedPreferences pref = getSharedPreferences("who_login", MODE_PRIVATE);
            String who_login = pref.getString("WhoIsLogin", "");
            try{
                for(int i = 0; i < mNotebookList.size(); i++){
                    jsonObject = new JSONObject();
                    Notebook notebook = mNotebookList.get(i);
                    jsonObject.put("index", i);
                    jsonObject.put("tag", notebook.getName());
                    jsonObject.put("image", notebook.getImageId());
                    jsonObject.put("username", who_login);
                    jsonArray.put(jsonObject);
                }
            }catch (JSONException e){
                return "Unknown Error";
            }

            String response = ehc.send("http://10.253.221.78:81/Enotebook_server/getnotebooklist.php", jsonArray.toString(), "application/json");
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(pDlg.isShowing()) pDlg.dismiss();
            pDlg = null;
            parseSendNotebookResponse(result);
        }
    }

    //parse the response of sending notebooklist request
    public void parseSendNotebookResponse(String response){
        if(response.equals("Unknown Error")){
            Toast.makeText(NoteBooks.this, "Create Failed:Unknown Error", Toast.LENGTH_SHORT).show();
            return;
        }else if(response.equals("success")){
            Toast.makeText(NoteBooks.this, "Create Successfully", Toast.LENGTH_SHORT).show();
            initRecyclerView();
        }
    }
}
