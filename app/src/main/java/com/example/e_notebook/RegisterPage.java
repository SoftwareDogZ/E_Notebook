package com.example.e_notebook;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterPage extends AppCompatActivity {

    private TextView username_text;
    private TextView pwd_text;
    private TextView conf_pwd_text;
    private RadioGroup gender_group;
    private String username;
    private String password;
    private String conf_pwd;
    private String gender;
    private int radiobtn_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        username_text = (TextView) findViewById(R.id.reg_username_text);
        pwd_text = (TextView)findViewById(R.id.reg_pwd_text);
        conf_pwd_text = (TextView)findViewById(R.id.reg_confirm_text);
        gender_group = (RadioGroup)findViewById(R.id.gender_btngroup);

        gender_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radiobtn_id = gender_group.getCheckedRadioButtonId();
                RadioButton choise = (RadioButton)findViewById(radiobtn_id);
                gender = choise.getText().toString();
            }
        });

        ((Button)findViewById(R.id.reg_register_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = username_text.getText().toString();
                password = pwd_text.getText().toString();
                conf_pwd = conf_pwd_text.getText().toString();

                if(username.isEmpty() || password.isEmpty() || conf_pwd.isEmpty() || radiobtn_id == -1){
                    Toast.makeText(RegisterPage.this, "Please Give Your Information Details", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!password.equals(conf_pwd)){
                    Toast.makeText(RegisterPage.this, "Please Confirm Your Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                new OnlineRegister().execute("");
            }
        });

        ((Button)findViewById(R.id.reg_cancel_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    class OnlineRegister extends AsyncTask<String, Void, String>{

        ProgressDialog pDlg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDlg = new ProgressDialog(RegisterPage.this);
            pDlg.setCancelable(false);
            pDlg.setMessage("Registering...");
            if(!pDlg.isShowing()) pDlg.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            EnotebookHttpClient ehc = new EnotebookHttpClient();
            JSONObject jsonObj = new JSONObject();
            try{
                jsonObj.put("username", username);
                jsonObj.put("password", password);
                jsonObj.put("sex", gender);
            }catch (JSONException e){
                return "Unknown Error";
            }
            String response = ehc.send("http://192.168.154.1:81/register.php", jsonObj.toString(), "application/json");

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(pDlg.isShowing()) pDlg.dismiss();
            pDlg = null;
            parseRegisterResponse(result);
        }

    }

    //parse the response of the register request
    public void parseRegisterResponse(String response){
        if(response.equals("Unknown Error")){
            Toast.makeText(RegisterPage.this, "Register Failed:Unknown Error", Toast.LENGTH_SHORT).show();
            return;
        }else if(response.equals("DataBaseConnect Error")){
            Toast.makeText(RegisterPage.this, "Register Failed:Cannot Connect to Database", Toast.LENGTH_SHORT).show();
            return;
        }else if(response.equals("DataBaseQuery Error")){
            Toast.makeText(RegisterPage.this, "Register Failed:The User Has Existed", Toast.LENGTH_SHORT).show();
            return;
        }else if(response.equals("DataBaseInsert Error")){
            Toast.makeText(RegisterPage.this, "Register Failed:Cannot Create The Account", Toast.LENGTH_SHORT).show();
            return;
        }else if(response.equals("success")){
            this.finish();
        }else{
            Toast.makeText(RegisterPage.this, "Register Failed:Unknown Error", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}

