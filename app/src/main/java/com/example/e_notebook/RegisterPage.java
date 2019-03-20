package com.example.e_notebook;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.e_notebook.EnotebookHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RegisterPage extends AppCompatActivity {

    private TextView username_text;
    private TextView pwd_text;
    private TextView conf_pwd_text;
    private RadioGroup gender_group;
    private String gender;

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
                int id = gender_group.getCheckedRadioButtonId();
                RadioButton choise = (RadioButton)findViewById(id);
                gender = choise.getText().toString();
            }
        });

        ((Button)findViewById(R.id.reg_register_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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


    class OnlineRegister extends AsyncTask<String, Void, Boolean>{

        @Override
        protected Boolean doInBackground(String... strings) {

            //需要添加判断代码

            EnotebookHttpClient ehc = new EnotebookHttpClient();
            JSONObject jsonObj = new JSONObject();
            try{
                jsonObj.put("username", username_text.getText());
                jsonObj.put("password", pwd_text.getText());
                jsonObj.put("sex", gender);
            }catch (JSONException e){

            }
            String str = ehc.send("http://192.168.154.1:81/register.php", jsonObj.toString(), "application/json");
            Log.i("get from server", str);
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
        }


    }
}
