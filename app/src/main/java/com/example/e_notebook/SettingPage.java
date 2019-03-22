package com.example.e_notebook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SettingPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_page);

        ((Button)findViewById(R.id.set_logout_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences("who_login", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("WhoIsLogin", "");
                editor.apply();

                Intent intent = new Intent(SettingPage.this, notebookspage.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
