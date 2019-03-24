package com.example.e_notebook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.UpdateAppearance;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.materialtextfield.MaterialTextField;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javaclass.IpConfig;

public class notebookspage extends AppCompatActivity {

    TextView username_text;
    TextView pwd_text;
    String username;
    String password;
    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notebookspage);

        username_text = (TextView)findViewById(R.id.username_text);
        pwd_text = (TextView)findViewById(R.id.pwd_text);

        //Register
        ((Button)findViewById(R.id.register_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(notebookspage.this, RegisterPage.class);
                startActivity(intent);
            }
        });

        //Login
        ((Button)findViewById(R.id.login_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = username_text.getText().toString();
                password = pwd_text.getText().toString();
                if(username.isEmpty() || password.isEmpty()){
                    Toast.makeText(notebookspage.this, "Please Give Your Information Details", Toast.LENGTH_SHORT).show();
                    return;
                }
                new OnlineLogin().execute(username, password);
            }
        });

        //Auto Fill Password Text Through Handler
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 1) {
                    username = username_text.getText().toString();
                    if (!username.isEmpty()) {
                        SharedPreferences pref = getSharedPreferences("enote_userinfo_" + username, MODE_PRIVATE);
                        String getPassword = pref.getString("password", "");
                        Boolean getIsAutoLogin = pref.getBoolean("isAutoLogin", false);
                        if (getIsAutoLogin)
                            ((CheckBox) findViewById(R.id.autologin_check)).setChecked(true);
                        if (!getPassword.isEmpty()) {
                            pwd_text.setText(getPassword);
                            ((CheckBox) findViewById(R.id.rempwd_check)).setChecked(true);
                        }
                    }
                }
            }
        };

        pwd_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateUI();
            }
        });
    }

    private void UpdateUI(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    class OnlineLogin extends AsyncTask<String, Void, String>{

        ProgressDialog pDlg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDlg = new ProgressDialog(notebookspage.this);
            pDlg.setMessage("Login...");
            if(pDlg.isShowing()) pDlg.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            EnotebookHttpClient ehc = new EnotebookHttpClient();
            JSONObject jsonObj = new JSONObject();
            try{
                jsonObj.put("username", strings[0]);
                jsonObj.put("password", strings[1]);
            }catch (JSONException e){
                return "Unknown Error";
            }
            String response = ehc.send(new IpConfig().getIpPath()+"login.php", jsonObj.toString(), "application/json");

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(pDlg.isShowing()) pDlg.dismiss();
            pDlg = null;
            parseLoginResponse(result);
        }
    }

    //parse the response of the login request
    public void parseLoginResponse(String response){
        if(response.equals("Unknown Error")){
            Toast.makeText(notebookspage.this, "Login Failed:Unknown Error", Toast.LENGTH_SHORT).show();
            return;
        }else if(response.equals("DataBaseConnect Error")){
            Toast.makeText(notebookspage.this, "Login Failed:Cannot Connect to Database", Toast.LENGTH_SHORT).show();
            return;
        }else if(response.equals("DataBaseQuery Error")){
            Toast.makeText(notebookspage.this, "Login Failed:Your Account Do not Exist", Toast.LENGTH_SHORT).show();
            return;
        }else if(response.equals("password is wrong")){
            Toast.makeText(notebookspage.this, "Login Failed:Your Password is Wrong", Toast.LENGTH_SHORT).show();
            return;
        }else if(response.equals("success")){
            SaveUserConfig();
            Intent intent = new Intent(notebookspage.this, NoteBooks.class);
            startActivity(intent);
            this.finish();
        }else{
            Toast.makeText(notebookspage.this, "Login Failed:Unknown Error", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    //remember password and auto login
    public void SaveUserConfig(){
        //save user's configure about his account
        String savedusername = username;
        String savedpassword = "";
        Boolean isAutoLogin = false;

        if(((CheckBox)findViewById(R.id.rempwd_check)).isChecked()){
            savedpassword = password;
        }
        if(((CheckBox)findViewById(R.id.autologin_check)).isChecked()){
            isAutoLogin = true;
        }

        SharedPreferences pref = this.getSharedPreferences("enote_userinfo_"+savedusername, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("username", savedusername);
        editor.putString("password", savedpassword);
        editor.putBoolean("isAutoLogin", isAutoLogin);
        editor.apply();

        //save if there was someone login
        SharedPreferences mpref = this.getSharedPreferences("who_login", MODE_PRIVATE);
        SharedPreferences.Editor meditor = mpref.edit();
        meditor.putString("WhoIsLogin", savedusername);
        meditor.apply();

    }

}
