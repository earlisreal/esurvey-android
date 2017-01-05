package com.fantasticfour.esurvey.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fantasticfour.esurvey.Global.Config;
import com.fantasticfour.esurvey.Global.Database;
import com.fantasticfour.esurvey.Global.GlobalFunctions;
import com.fantasticfour.esurvey.Objects.User;
import com.fantasticfour.esurvey.R;
import com.fantasticfour.esurvey.Interface.RequestInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText usernameEt, firstEt, lastEt, emailEt, passwordEt, confirmEt;
    ProgressDialog progress;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        preferences = getSharedPreferences("eSurvey", MODE_PRIVATE);

        usernameEt = (EditText) findViewById(R.id.username);
        firstEt = (EditText) findViewById(R.id.first);
        lastEt = (EditText) findViewById(R.id.last);
        emailEt = (EditText) findViewById(R.id.email);
        passwordEt = (EditText) findViewById(R.id.password);
        confirmEt = (EditText) findViewById(R.id.confirm);
//        progress = (ProgressBar) findViewById(R.id.progress);
    }

    public void test(View v){
        usernameEt.setError("earl is real");
    }

    public void register(View v) {
        String username = usernameEt.getText().toString();
        String first = firstEt.getText().toString();
        String last = lastEt.getText().toString();
        String email = emailEt.getText().toString();
        String password = passwordEt.getText().toString();
        String confirm = confirmEt.getText().toString();

        if(username.isEmpty() || first.isEmpty() || last.isEmpty() ||
                email.isEmpty() || password.isEmpty() || confirm.isEmpty()){
            Toast.makeText(this, "Please Complete All Fields", Toast.LENGTH_SHORT).show();
        }else{
            if(password.equals(confirm)){
                progress = ProgressDialog.show(this, "Registering", "Please Wait...", true);
//                        progress.setVisibility(View.VISIBLE);
                RequestInterface request = GlobalFunctions.getInterface();
                User user = new User(0, username, first, last, email, password);
                request.register(user).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {

                        User user = response.body();
//                        Toast.makeText(getApplication(), newUser.toString(), Toast.LENGTH_SHORT).show();
                        Log.i(Config.TAG, String.valueOf(user.getId()));
                        Log.d(Config.TAG, user.toString());
                        progress.dismiss();
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
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("source", "login");
                            startActivity(intent);
//                        finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
//                        Toast.makeText(getApplication(), "FAIL", Toast.LENGTH_SHORT).show();
                        Log.i(Config.TAG, "onResponse: " +t.getLocalizedMessage());
                    }
                });
            }else{
                Toast.makeText(getApplicationContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
