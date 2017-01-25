package com.fantasticfour.esurvey.Activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.fantasticfour.esurvey.Global.Config;
import com.fantasticfour.esurvey.Global.GlobalFunctions;
import com.fantasticfour.esurvey.Interface.RequestInterface;
import com.fantasticfour.esurvey.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Main2Activity extends AppCompatActivity {
    String TAG = "EARL IS REAL";
    EditText output;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        output = (EditText) findViewById(R.id.output);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();


                String url = "http://192.168.1.8/esurvey/android/test";
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mediaPlayer.setDataSource(url);
                    mediaPlayer.prepareAsync(); // might take long! (for buffering, etc)
//                    mediaPlayer.start();
                    Log.d(TAG, "onClick: Played");
                } catch (IOException e) {
                    Log.e("EARL IS REAL", "onClick: FAIL", e);
                }

                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
            }
        });
    }

    public void getSpeech(View v){
        EditText et = (EditText) findViewById(R.id.questionId);
        download(Integer.parseInt(et.getText().toString()));
    }

    private void download(final int id){
        RequestInterface request = GlobalFunctions.getInterface();
        request.downloadSpeech(id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                boolean saved = writeResponseBodyToDisk(response.body());

                Log.d(TAG, "onResponse: response -> " +response.toString());
                Log.d(TAG, "onResponse: FIle saved : " +saved);
//                String filename = "myfile";

                FileOutputStream outputStream;

                try {
                    outputStream = openFileOutput("question" +id +".wav", Context.MODE_WORLD_READABLE);
                    outputStream.write(response.body().bytes());
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d(TAG, "onResponse: PATH -> " +getExternalFilesDir(null) + File.separator +  "earlisrealresult.wav");

                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
//                    FileInputStream fis = new FileInputStream(getFilesDir() +"/question" +id +".wav");
                    mediaPlayer.setDataSource(getExternalFilesDir(null) + File.separator +  "earlisrealresult.wav");
                    mediaPlayer.prepare(); // might take long! (for buffering, etc)
                    mediaPlayer.start();
                    Log.d(TAG, "onClick: Played");
                } catch (IOException e) {
                    Log.e("EARL IS REAL", "onClick: FAIL", e);
                }

                testPlay(id);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure: Download Fail!", t);
            }
        });
    }

    private void testPlay(int id){
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(getFilesDir() +"/question" +id +".wav");
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
            mediaPlayer.start();
            Log.d(TAG, "onClick: Played");
        } catch (IOException e) {
            Log.e("EARL IS REAL", "onClick: FAIL", e);
        }
    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(getExternalFilesDir(null) + File.separator +  "earlisrealresult.wav");

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
    public void speak(View v){
       Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Now!");
        try{
            startActivityForResult(intent, 100);
        }catch (ActivityNotFoundException e){
            Log.e("EARL IS REAL", "speak: FAIL", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("EARL IS REAL", "onActivityResult: CODE -> " +resultCode);
        if(requestCode == 100 && data != null){
            output.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
        }
    }

}
