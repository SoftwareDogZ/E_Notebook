package com.example.e_notebook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class notebookspage extends AppCompatActivity {

    TextView username_text;
    TextView pwd_text;

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
                String username = username_text.getText().toString();
                String password = pwd_text.getText().toString();
                if(username.isEmpty() || password.isEmpty()){
                    Toast.makeText(notebookspage.this, "Please Give Your Information Details", Toast.LENGTH_SHORT).show();
                    return;
                }
                new OnlineLogin().execute(username, password);
            }
        });
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
            String response = ehc.send("http://192.168.154.1:81/login.php", jsonObj.toString(), "application/json");
            Log.i("from server:", response);
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
            Intent intent = new Intent(notebookspage.this, SettingPage.class);
            startActivity(intent);
            this.finish();
        }else{
            Toast.makeText(notebookspage.this, "Login Failed:Unknown Error", Toast.LENGTH_SHORT).show();
            return;
        }
    }

}
