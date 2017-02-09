package com.fantasticfour.esurvey.Activity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fantasticfour.esurvey.Fragment.SurveyPageFragment;
import com.fantasticfour.esurvey.Global.Config;
import com.fantasticfour.esurvey.Global.Database;
import com.fantasticfour.esurvey.Global.GlobalFunctions;
import com.fantasticfour.esurvey.Interface.RequestInterface;
import com.fantasticfour.esurvey.Objects.Response;
import com.fantasticfour.esurvey.Objects.ResponseDetail;
import com.fantasticfour.esurvey.Objects.ServerResponse;
import com.fantasticfour.esurvey.Objects.SurveyPage;
import com.fantasticfour.esurvey.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

public class AnswerActivity extends AppCompatActivity implements SurveyPageFragment.ButtonClickListener{
    int surveyId, index = 0;
    ProgressDialog progressDialog;
    Database db;
    FragmentTransaction transaction;
    List<SurveyPage> pages;
    List<SurveyPageFragment> fragments = new ArrayList<>();
    Button done, back;

    @Override
    public void onSubmit(List<ResponseDetail> details) {
//        Log.d(Config.TAG, "inserting response");
        db.insertResponse(surveyId, details);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.free:
                handsFree();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        done = (Button) findViewById(R.id.done);
        back = (Button) findViewById(R.id.back);

        String title = getIntent().getStringExtra("survey_title");
        surveyId = getIntent().getIntExtra("survey_id", 0);

        setTitle(title);

        db = new Database(this);

        pages = db.getPages(surveyId);

        createFragments();

        initializeFirstPage();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
            }
        });

    }

    private void handsFree(){

    }

    private void speak(){
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
            Log.d(Config.TAG, "onActivityResult: SPEECH -> " +data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
//            output.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
        }
    }

    private void replacePage(){
        if(index>0){
            back.setVisibility(View.VISIBLE);
        }else{
            back.setVisibility(View.VISIBLE);
        }
        if(index == pages.size()-1){
            done.setText("Done");
        }else{
            done.setText("Next");
        }

        if(fragments.size() > 0){
            setPageTitle();
            transaction = getSupportFragmentManager().beginTransaction();
//            Log.d(Config.TAG, "replacing index -> " +index);
            transaction.replace(R.id.container, fragments.get(index));
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private void createFragments(){
        for (SurveyPage page : pages){

            Bundle bundle = new Bundle();
            SurveyPageFragment fragment = new SurveyPageFragment();
            bundle.putInt("survey_page_id", page.getId());
            bundle.putString("dir", "" +getFilesDir());
            fragment.setArguments(bundle);
//            Log.d(Config.TAG, "page id -> " +
//                    fragment.getArguments().getInt("survey_page_id"));
            fragments.add(fragment);
        }
    }

    private void setPageTitle(){
        TextView pageTitile = (TextView) findViewById(R.id.page_title);
        if(pages.get(index).getPage_title().matches("")){
            pageTitile.setVisibility(View.GONE);
        }else{
            pageTitile.setText(pages.get(index).getPage_title());
        }
    }

    private void initializeFirstPage(){
        if(fragments.size() > 0){
            setPageTitle();
//            Log.d(Config.TAG, "start index -> " +index);
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, fragments.get(index));
            transaction.commit();
        }
//        replacePage();


        if(pages.size() > 1){
            done.setText("Next");
        }

    }

    public void back(){
        index--;
        replacePage();
    }

    public void next(){
        if(fragments.get(index).getAnswers() != null){
            if(index<pages.size()-1){
                fragments.get(index).getAnswers();
                index++;
                replacePage();
            }else{
                final Response surveyResponse = new Response();
                List<ResponseDetail> responseDetails = new ArrayList<>();
//                Log.d(Config.TAG, "submitting");
                List<ResponseDetail> details = new ArrayList<>();
                SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
                try {
                    sqLiteDatabase.beginTransaction();

                    ContentValues cv = new ContentValues();
                    String dateNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    cv.put("survey_id", surveyId);
                    cv.put("created_at", dateNow);
                    long responseId = sqLiteDatabase.insert("responses", null, cv);

                    surveyResponse.setId((int) responseId);
                    surveyResponse.setSurvey_id(surveyId);
                    surveyResponse.setCreated_at(dateNow);

                    for (SurveyPageFragment pageFragment : fragments){
                        for(ResponseDetail detail : pageFragment.getAnswers()){
                            responseDetails.add(detail);
                            db.insertResponseDetail(sqLiteDatabase, responseId, detail);
                        }
                    }

                    surveyResponse.setResponseDetails(responseDetails);

                    sqLiteDatabase.setTransactionSuccessful();
                }catch (SQLiteException e){
                    Log.e(Config.TAG, e.getMessage());
                }finally {
                    sqLiteDatabase.endTransaction();
                    sqLiteDatabase.close();
                }

                if(GlobalFunctions.isOnline(this)){
                    //send to server
                    progressDialog = ProgressDialog.show(this, "Sending Response to the Server", "Please Wait");
                    RequestInterface request = GlobalFunctions.getInterface();
                    request.sendResponse(surveyResponse).enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, retrofit2.Response<Integer> response) {
                            Log.d(Config.TAG, "earl is real -> " +response.code());
                            if(response.code() == 200){
                                Log.d(Config.TAG, "response -> " +response.body());
                                Log.d(Config.TAG, "set synced -> " +surveyResponse.getId());
                                db.updateResponse(surveyResponse.getId());
                                surveyResponse.setSynced(1);
                                progressDialog.dismiss();
                                finish();
                            }else{
                                progressDialog.dismiss();
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                            Log.e(Config.TAG, "Error sending to server",t);
                            progressDialog.dismiss();
                            finish();
                        }
                    });
                }else{
                    finish();
                }

            }
        }


    }

}
