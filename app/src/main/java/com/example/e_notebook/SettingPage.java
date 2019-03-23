package com.example.e_notebook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

public class SettingPage extends AppCompatActivity {

    Handler mHandler;
    TextView username_text;
    TextView sex_text;
    TextView phone_text;
    TextView email_text;
    TextView edit_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_page);

        username_text = (TextView)findViewById(R.id.set_username_text);
        sex_text = (TextView)findViewById(R.id.set_gender_text);
        phone_text = (TextView)findViewById(R.id.set_phone_text);
        email_text = (TextView)findViewById(R.id.set_email_text);
        edit_text = (TextView)findViewById(R.id.set_edit_text);

        ((Button)findViewById(R.id.set_logout_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences("who_login", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("WhoIsLogin", "");
                editor.apply();

                Intent intent = new Intent(SettingPage.this, notebookspage.class);
                startActivity(intent);
                NoteBooks.instance.finish();
                finish();
            }
        });

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:{
                        phone_text.setEnabled(true);
                        email_text.setEnabled(true);
                        edit_text.setText("Save");
                        break;
                    }
                    case 2:{
                        phone_text.setText(phone_text.getText());
                        email_text.setText(email_text.getText());
                        phone_text.setEnabled(false);
                        email_text.setEnabled(false);
                        edit_text.setText("Edit");
                        new SaveUserInfo().execute("");
                        break;
                    }
                    case 3:{
                        String[] userinfo = msg.obj.toString().split("&");
                        username_text.setText(userinfo[0]);
                        sex_text.setText(userinfo[1]);
                        email_text.setText(userinfo[2]);
                        phone_text.setText(userinfo[3]);
                        break;
                    }
                    default:break;
                }
            }
        };

        ((TextView)findViewById(R.id.set_edit_text)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String flag = edit_text.getText().toString();
                        Message msg = new Message();
                        if(flag.equals("Edit")){
                            msg.what = 1;
                        }else{
                            msg.what = 2;
                        }
                        mHandler.sendMessage(msg);
                    }
                }).start();
            }
        });

        new SetUserInfo().execute("");
    }

    //set the username and gender
    class SetUserInfo extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            SharedPreferences pref = getSharedPreferences("who_login", MODE_PRIVATE);
            String username = pref.getString("WhoIsLogin", "");
            EnotebookHttpClient ehc = new EnotebookHttpClient();
            JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.put("username", username);
            }catch (JSONException e){
                return "Unknown Error";
            }

            String response = ehc.send("http://10.253.221.78:81/Enotebook_server/getuserinfo.php", jsonObject.toString(), "application/json");
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            parseGetUserInfoResponse(result);
        }
    }

    //parse the response of the getuserinfo request
    public void parseGetUserInfoResponse(final String response){
        if(response.equals("Unknown Error")){
            Toast.makeText(SettingPage.this, "Error:Unknown Error", Toast.LENGTH_SHORT).show();
            return;
        }else if(response.equals("DataBaseConnect Error")){
            Toast.makeText(SettingPage.this, "Error:DataBaseConnect Error", Toast.LENGTH_SHORT).show();
            return;
        }else if(response.equals("DataBaseQuery Error")){
            Toast.makeText(SettingPage.this, "Error:DataBaseQuery Error", Toast.LENGTH_SHORT).show();
            return;
        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message msg = new Message();
                    msg.what = 3;
                    msg.obj = response;
                    mHandler.sendMessage(msg);
                }
            }).start();
        }
    }

    //save userinfo that user have input
    class SaveUserInfo extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            String email = email_text.getText().toString();
            String phone = phone_text.getText().toString();
            String username = username_text.getText().toString();
            EnotebookHttpClient ehc = new EnotebookHttpClient();
            JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.put("username", username);
                jsonObject.put("email", email);
                jsonObject.put("phone", phone);
            }catch (JSONException e){
                return "Unknown Error";
            }

            String response = ehc.send("http://10.253.221.78:81/Enotebook_server/senduserinfo.php", jsonObject.toString(), "application/json");
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            parseSendUserInfoResponse(result);
        }
    }

    //parse the response of the SendUserInfo request
    public void parseSendUserInfoResponse(String response){
        if(response.equals("Unknown Error")){
            Toast.makeText(SettingPage.this, "Upload Error:Unknown Error", Toast.LENGTH_SHORT).show();
            return;
        }else if(response.equals("DataBaseConnect Error")){
            Toast.makeText(SettingPage.this, "Upload Error:DataBaseConnect Error", Toast.LENGTH_SHORT).show();
            return;
        }else if(response.equals("DataBaseUpdate Error")){
            Toast.makeText(SettingPage.this, "Upload Error:DataBaseUpdate Error", Toast.LENGTH_SHORT).show();
            return;
        }else{
            Toast.makeText(SettingPage.this, "Setting Successfully", Toast.LENGTH_SHORT).show();
        }
    }
}
