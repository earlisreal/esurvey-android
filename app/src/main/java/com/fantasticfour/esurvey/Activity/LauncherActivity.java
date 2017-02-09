package com.fantasticfour.esurvey.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.fantasticfour.esurvey.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences("eSurvey", MODE_PRIVATE);

        if(preferences.getBoolean("logged", false)){
            Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
            intent.putExtra("source", "launcher");
            startActivity(intent);

            finish();
        }else{
            setContentView(R.layout.activity_launcher);
        }
    }

    public void showLogin(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void showRegister(View v){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
