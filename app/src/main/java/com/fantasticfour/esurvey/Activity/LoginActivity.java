package com.fantasticfour.esurvey.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fantasticfour.esurvey.Global.Config;
import com.fantasticfour.esurvey.Global.Database;
import com.fantasticfour.esurvey.Global.GlobalFunctions;
import com.fantasticfour.esurvey.Objects.User;
import com.fantasticfour.esurvey.R;
import com.fantasticfour.esurvey.Interface.RequestInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEt, passwordEt;
    ProgressDialog progress;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = getSharedPreferences("eSurvey", MODE_PRIVATE);

        if(preferences.getBoolean("logged", false)){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("source", "launcher");
            startActivity(intent);

            finish();
        }else{
            setContentView(R.layout.activity_login);
        }

        usernameEt = (EditText) findViewById(R.id.username);
        passwordEt = (EditText) findViewById(R.id.password);

//        preferences = getSharedPreferences("eSurvey", MODE_PRIVATE);
    }

    public void test(View v){
        RequestInterface request = GlobalFunctions.getInterface();
        request.getTest().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User newUser = response.body();
//                        Toast.makeText(getApplication(), newUser.toString(), Toast.LENGTH_SHORT).show();
                Log.i(Config.TAG, String.valueOf(newUser.getId()));
                Log.d(Config.TAG, newUser.toString());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d(Config.TAG, "Fail");
                Log.e(Config.TAG, t.getMessage());
            }
        });
    }

    public void login(View v){
        progress = ProgressDialog.show(this, "Logging in", "Please Wait...", true);
        String username = usernameEt.getText().toString();
        String password = passwordEt.getText().toString();

        RequestInterface request = GlobalFunctions.getInterface();
        request.login(new User(username, password)).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
//                LoginResponse login = response.body();
                Log.i(Config.TAG, ""+response.code());
//                Log.i(Config.TAG, String.valueOf(login.getStatus()));
                if(response.code() == 200){

                    User user = response.body();

                    Log.i(Config.TAG, user.toString());
                    SharedPreferences.Editor editor =  preferences.edit();
                    editor.putBoolean("logged", true);
                    editor.putInt("user_id", user.getId());
                    editor.putString("username", user.getUsername());
                    editor.putString("email", user.getEmail());
                    editor.putString("last_name", user.getLast_name());
                    editor.putString("first_name", user.getFirst_name());
                    if(editor.commit()){
                        Database db = new Database(getApplicationContext());
                        db.insertUser(user);
                        progress.dismiss();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("source", "login");
                        startActivity(intent);
//                        finish();
                    }
                }else{
                    Toast.makeText(getApplication(), "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                }
//                Log.i(Config.TAG, String.valueOf(newUser.getId()));
//                Log.d(Config.TAG, newUser.toString());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getApplication(), "Failed to connect to Server", Toast.LENGTH_SHORT).show();
                progress.dismiss();
                Log.d(Config.TAG, "Fail");
                Log.e(Config.TAG, t.getMessage());
            }
        });
    }
}
