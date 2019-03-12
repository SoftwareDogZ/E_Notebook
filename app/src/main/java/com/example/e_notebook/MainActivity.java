package com.example.e_notebook;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new CountDownTimer(3000, 1000){
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(MainActivity.this, notebookspage.class);
                startActivity(intent);
                finish();
                //function: overridePendingTransition
                //parameters: the mode in which the secondactivity in, the mode in which the firstactivity out
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }.start();
    }
}
