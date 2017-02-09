package com.fantasticfour.esurvey.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fantasticfour.esurvey.Global.Config;
import com.fantasticfour.esurvey.Global.Database;
import com.fantasticfour.esurvey.Global.GlobalFunctions;
import com.fantasticfour.esurvey.Objects.Question;
import com.fantasticfour.esurvey.Objects.ServerResponse;
import com.fantasticfour.esurvey.Objects.Survey;
import com.fantasticfour.esurvey.Interface.RequestInterface;
import com.fantasticfour.esurvey.Objects.SurveyPage;
import com.fantasticfour.esurvey.RecyclerTouchListener;
import com.fantasticfour.esurvey.SurveyAdapter;

import com.fantasticfour.esurvey.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.R.attr.id;
import static android.R.attr.logo;

public class MainActivity extends AppCompatActivity {
    private int userId;

    TextView surveyCountText;
    ProgressDialog progressDialog;
    SharedPreferences preferences;
    Database db;
    List<Survey> surveys = new ArrayList<>();

    //COMPONENTS
    TextView emptyLabel;
    Button tapToRefresh;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    SurveyAdapter surveyAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sync:
                syncSurveys();
                return true;
            case R.id.action_logout:
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                if (editor.commit()) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surveyCountText = (TextView) findViewById(R.id.survey_count);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        emptyLabel = (TextView) findViewById(R.id.label_empty);
        emptyLabel.setMovementMethod(LinkMovementMethod.getInstance());

        preferences = getSharedPreferences("eSurvey", MODE_PRIVATE);
        userId = preferences.getInt("user_id", 0);
        db = new Database(this);

//        refreshSurvey();


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSurveys();
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, AnswerActivity.class);
                Survey survey = surveys.get(position);
                intent.putExtra("survey_id", survey.getId());
                intent.putExtra("survey_title", survey.getSurvey_title());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        String source = getIntent().getStringExtra("source");
        if (source.matches("login")) {
            showLoading();
            surveys = db.getSurveys(userId);
            updateRecyclerView();
            getSurveys();
        } else if (source.matches("launcher")) {
            showLoading();
            surveys = db.getSurveys(userId);
            updateRecyclerView();
            endLoading();
        } else { //register
            endLoading();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSurveys();
    }

    private void syncSurveys() {
        boolean sending = false;
        if (GlobalFunctions.isOnline(getApplicationContext())) {
            Log.d(Config.TAG, "survey sync");
            progressDialog = ProgressDialog.show(this, "Syncing Survey Reponses", "Please Wait", true);
            for (Survey survey : surveys) {
                for (final com.fantasticfour.esurvey.Objects.Response surveyResponse : survey.getResponses()) {
                    if (surveyResponse.getSynced() == 0) {
                        sending = true;
                        final int responseId = surveyResponse.getId();
                        RequestInterface request = GlobalFunctions.getInterface();
                        request.sendResponse(surveyResponse).enqueue(new Callback<Integer>() {
                            @Override
                            public void onResponse(Call<Integer> call, retrofit2.Response<Integer> response) {
                                Log.d(Config.TAG, "onResponse: " +response.code());
                                if (response.code() == 200) {
                                    db.updateResponse(surveyResponse.getId());
                                    surveyResponse.setSynced(1);
                                    updateSurveys();
                                    Toast.makeText(MainActivity.this, "Responses Synced Successfully", Toast.LENGTH_SHORT).show();

                                }

                                endLoading();
                            }

                            @Override
                            public void onFailure(Call<Integer> call, Throwable t) {
                                Log.e(Config.TAG, t.getMessage());

                                endLoading();
                            }
                        });
                    }
                }
            }
            if (!sending) {
                Toast.makeText(this, "No Responses need to Sync", Toast.LENGTH_SHORT).show();
                endLoading();
            }
        } else {
            Toast.makeText(this, "No Active Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSurveys() {
        surveys = db.getSurveys(userId);
        surveyCountText.setText("Displaying " + surveys.size() + (surveys.size() > 1 ? "Surveys" : "Survey"));
        updateRecyclerView();
        updateSurveyCount();
//        if(surveys.size()>0){
//            Log.d(Config.TAG, "update na ha");
//            for (Survey survey : surveys) {
//                survey.setResponses(db.getResponses(survey.getId()));
//            }
//
//            updateRecyclerView();
//        }
    }

    private void updateSurveyCount() {
        surveyCountText.setText("Displaying " + surveys.size() + (surveys.size() > 1 ? " Surveys" : " Survey"));
    }

    public void getSurveys() {
        if (GlobalFunctions.isOnline(getApplicationContext())) {
            RequestInterface request = GlobalFunctions.getInterface();
            request.getSurveys(userId).enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                    ServerResponse serverResponse = response.body();
                    surveys = serverResponse.getSurveys();
                    db.removeDeletedSurveys(userId, surveys);
                    for (Survey survey : surveys) {
                        db.storeSurvey(survey);
                        survey.setResponses(db.getResponses(survey.getId()));

                        //Download VOICE Question Here
//                        for (SurveyPage page : survey.getPages()) {
//                            for (Question question : page.getQuestions()) {
//                                Log.d(Config.TAG, "onResponse: QUestion downloading...");
//                                File speech = new File(getFilesDir() + "/question" + question.getId() + ".mp3");
//                                if (!speech.exists()) {
//                                    downloadSpeech(question.getId());
//                                }
//                            }
//                        }
                    }
                    surveys = db.getSurveys(userId);
                    updateRecyclerView();
                    swipeRefreshLayout.setRefreshing(false);
                    endLoading();
                    updateSurveyCount();
                    Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                    Log.e(Config.TAG, t.getMessage());
                    swipeRefreshLayout.setRefreshing(false);
                    endLoading();
                }
            });
        } else {
            GlobalFunctions.noInternetToast(this);
            swipeRefreshLayout.setRefreshing(false);
            endLoading();
        }
    }

    private void downloadSpeech(final int id) {
        RequestInterface request = GlobalFunctions.getInterface();
        request.downloadSpeech(id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                FileOutputStream outputStream;
                try {
                    outputStream = openFileOutput("question" + id + ".mp3", Context.MODE_PRIVATE);
                    outputStream.write(response.body().bytes());
                    outputStream.close();
                    Log.d(Config.TAG, "onResponse: Download Success!");
                } catch (Exception e) {
                    Log.e(Config.TAG, "onResponse: Download Fail", e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(Config.TAG, "onFailure: Download Fail!", t);
            }
        });
    }

    private void playSpeech(int id) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(getFilesDir() + "/question" + id + ".mp3");
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
            mediaPlayer.start();
            Log.d(Config.TAG, "onClick: Played");
        } catch (IOException e) {
            Log.e("EARL IS REAL", "onClick: FAIL", e);
        }
    }

    private void updateRecyclerView() {
        Log.d(Config.TAG, "updated recycler view ||");
        surveyAdapter = new SurveyAdapter(surveys);
        recyclerView.setAdapter(surveyAdapter);

        if (surveys.size() > 0) {
            emptyLabel.setVisibility(View.GONE);
        } else {
            emptyLabel.setVisibility(View.VISIBLE);
        }
    }

    private void showLoading() {
        progressDialog = ProgressDialog.show(this, "Loading Surveys", "Please Wait", true);
    }

    private void endLoading() {
        progressDialog.dismiss();
    }

    public void refreshSurvey() {
//        tapToRefresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d(Config.TAG, "refresh");
//                if(surveys.size() < 1){
//                    showLoading();
//                    getSurveys();
//                }
//            }
//        });
    }


}
