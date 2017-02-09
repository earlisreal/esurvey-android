package com.fantasticfour.esurvey.Fragment;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.fantasticfour.esurvey.Activity.MainActivity;
import com.fantasticfour.esurvey.Global.Config;
import com.fantasticfour.esurvey.Global.Database;
import com.fantasticfour.esurvey.Objects.Question;
import com.fantasticfour.esurvey.Objects.QuestionChoice;
import com.fantasticfour.esurvey.Objects.QuestionRow;
import com.fantasticfour.esurvey.Objects.ResponseDetail;
import com.fantasticfour.esurvey.Objects.SurveyPage;
import com.fantasticfour.esurvey.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.fantasticfour.esurvey.Global.Config.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class SurveyPageFragment extends Fragment {

    public interface ButtonClickListener{
        void onSubmit(List<ResponseDetail> details);
    }

    public SurveyPageFragment() {
        // Required empty public constructor
    }

    ButtonClickListener buttonListener;
    LinearLayout content;

    SurveyPage page;
    List<Question> questions;

    String filesDir;

    private HashMap<String, View> questionMap = new HashMap<>();

    EditText speechET;

    TextToSpeech tts;

    Context context;

    Database db;

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        Activity activity = context instanceof MainActivity ? ((MainActivity) context) : null;
//        try {
//            buttonListener = (ButtonClickListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement ButtonClickListener");
//        }
//    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity.getApplicationContext();
//        Activity activity = context instanceof MainActivity ? ((MainActivity) context) : null;
        try {
            buttonListener = (ButtonClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ButtonClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_survey_page, container, false);


        Bundle args = getArguments();
        db = new Database(getContext());
        page = db.getPage(args.getInt("survey_page_id"));
        Log.d(TAG, "pages passed -> " +page.getId());

        filesDir = args.getString("dir");

        questions = page.getQuestions();
        content = (LinearLayout) view.findViewById(R.id.content);

        loadQuestions(view);
        submitResponse(view);

        tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

            }
        });

        return view;
    }

    private void loadQuestions(View view){
        int questionNumber = 1;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams wrapParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 30);
        wrapParams.setMargins(0, 0, 0, 30);


        if(!page.getPage_description().matches("")){
            TextView description = new TextView(getContext());
            description.setText(page.getPage_description());
            content.addView(description, layoutParams);
        }

        for(final Question question : questions){
            LinearLayout questionTitle = new LinearLayout(getContext());
            questionTitle.setOrientation(LinearLayout.HORIZONTAL);

            TextView title = new TextView(getContext());
            title.setText("Q" +questionNumber++ +". " +question.getQuestion_title());
            title.setId(6996+question.getId());



            Button speech = new Button(getContext());
            speech.setText("S");

            ImageButton play = new ImageButton(getContext());
            play.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
//            speech.setWidth(50);

            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: Play speech now!");
//                    playSpeech(question.getId());
                textToSpeech(question.getQuestion_title());
                }
            });

            questionTitle.addView(play);
            questionTitle.addView(title);

            content.addView(questionTitle);


            String type = question.getQuestion_type().getType();
            final EditText et;
            switch (type){
                case "Textbox":
                    LinearLayout questionContainer = new LinearLayout(getContext());
                    questionTitle.setOrientation(LinearLayout.HORIZONTAL);
                    et = new EditText(getContext());

                    LinearLayout.LayoutParams etParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                    etParams.setMargins(0, 0, 0, 30);

                    ImageButton speak = new ImageButton(getContext());
                    speak.setImageResource(android.R.drawable.ic_btn_speak_now);

                    speak.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            speechET = et;
                            speak();
                        }
                    });

                    questionContainer.addView(et, etParams);
                    questionContainer.addView(speak, wrapParams);

                    content.addView(questionContainer, layoutParams);
                    questionMap.put("question"+question.getId(), et);
                    break;
                case "Text Area":
                    et = new EditText(getContext());
                    et.setSingleLine(false);
                    et.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                    et.setLines(4);
                    content.addView(et, layoutParams);
                    questionMap.put("question"+question.getId(), et);
                    break;
                case "Multiple Choice":
                    RadioGroup group = new RadioGroup(getContext());
                    group.setOrientation(LinearLayout.VERTICAL);
                    for (QuestionChoice choice : question.getChoices()){
                        RadioButton radio = new RadioButton(getContext());
                        radio.setText(choice.getLabel());
                        radio.setId(choice.getId());
                        group.addView(radio);
                    }
                    content.addView(group, layoutParams);
                    questionMap.put("question"+question.getId(), group);
                    break;
                case "Dropdown":
                    Spinner spinner = new Spinner(getContext());
                    List<String> choices = new ArrayList<>();
                    for (QuestionChoice choice : question.getChoices()){
                        choices.add(choice.getLabel());
                    }
                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, choices);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    content.addView(spinner,wrapParams);
                    questionMap.put("question"+question.getId(), spinner);
                    break;

                case "Checkbox":
                    for (QuestionChoice choice : question.getChoices()){
                        CheckBox checkbox = new CheckBox(getContext());
                        checkbox.setText(choice.getLabel());
                        content.addView(checkbox);
                        questionMap.put("choice"+choice.getId(), checkbox);
                    }
                    break;
                case "Rating Scale":
                    RatingBar rating = new RatingBar(getContext(), null, android.R.attr.ratingBarStyleIndicator);
                    rating.setIsIndicator(false);
                    rating.setNumStars(question.getOption().getMax_rating());
                    rating.setStepSize(1);
                    content.addView(rating, wrapParams);
                    questionMap.put("question"+question.getId(), rating);
                    break;
                case "Likert Scale":
                    for (QuestionRow row : question.getRows()){
                        TextView rowTitle = new TextView(getContext());
                        rowTitle.setText(row.getLabel());
                        rowTitle.setId(row.getId());
                        content.addView(rowTitle);

                        RadioGroup rowGroup = new RadioGroup(getContext());
                        rowGroup.setOrientation(LinearLayout.VERTICAL);
                        for (QuestionChoice choice : question.getChoices()){
                            RadioButton radio = new RadioButton(getContext());
                            radio.setText(choice.getLabel());
                            radio.setId(choice.getId());
                            rowGroup.addView(radio);
                        }
                        content.addView(rowGroup, layoutParams);
                        questionMap.put("row"+row.getId(), rowGroup);
                    }
                    break;
                default:
                    //
            }
        }
    }

    private void playSpeech(int id) {
              MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            File file = new File(filesDir + "/question" + id + ".mp3");
            Log.d(TAG, "playSpeech: " +file.exists());
            if(file.exists()){
//                FileInputStream test = getContext().openFileInput("question" + id + ".mp3");
//            mediaPlayer.reset();
//            mediaPlayer.setDataSource(test.getFD());
                mediaPlayer.setDataSource(file.getPath());
//                mediaPlayer.setDataSource(filesDir + "/question" + id + ".mp3");
                mediaPlayer.prepare(); // might take long! (for buffering, etc)
                mediaPlayer.start();
                Log.d(TAG, "onClick: Played");
            }
        } catch (IOException e) {
            Log.e("EARL IS REAL", "onClick: FAIL", e);
        }

    }

    private void textToSpeech(String text){
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onPause() {
        if(tts != null){
            tts.stop();
            tts.shutdown();
        }
        super.onPause();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("EARL IS REAL", "onActivityResult: CODE -> " +resultCode);
        if(requestCode == 100 && data != null){
            Log.d(TAG, "onActivityResult: SPEECH -> " +data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
            speechET.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
//            output.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
        }
    }

    public List<ResponseDetail> getAnswers(){
        boolean hasError = false;
        List<ResponseDetail> details = new ArrayList<ResponseDetail>();
        for(Question question : questions) {
            String textAnswer = null;
            int choiceId = -1;
            switch (question.getQuestion_type().getType()) {
                case "Textbox":
                case "Text Area":
                    EditText edit = (EditText) questionMap.get("question"+question.getId());
                    textAnswer = edit.getText().toString();
                    if(question.getIs_mandatory() == 1) {
                        if (textAnswer.matches("")) {
                            edit.setError("Required Question");
                            hasError = true;
                        }
                    }
                    break;
                case "Multiple Choice":
                    RadioGroup group = (RadioGroup) questionMap.get("question"+question.getId());
                    choiceId = group.getCheckedRadioButtonId();
                    Log.d(TAG, "mc choice selected -> " +choiceId);
                    if(question.getIs_mandatory() == 1){
                        Log.d(TAG, "mandatory mc");
                        if(choiceId <= 0){
                            Log.d(TAG, "mc has error");
                            TextView label = (TextView) content.findViewById(6996+question.getId());
                            label.setError("Required Question");
                            hasError = true;
                        }
                    }
                    break;
                case "Dropdown":
                    Spinner spinner = (Spinner) questionMap.get("question"+question.getId());
                    List<QuestionChoice> choices = question.getChoices();
                    choiceId = choices.get(spinner.getSelectedItemPosition()).getId();
                    break;

                case "Rating Scale":
                    RatingBar rating = (RatingBar) questionMap.get("question"+question.getId());
                    Log.d(TAG, "rating -> " +rating.getRating());
                    if(question.getIs_mandatory() == 1){
                        if(rating.getRating() == 0){
                            hasError = true;
                            setErrorLabel(question.getId());
                        }
                    }
                    textAnswer = ""+(int)rating.getRating();
                    break;
                case "Checkbox":
                    List<ResponseDetail> checkList = new ArrayList<>();
                    CheckBox lastCheckbox;
                    for (QuestionChoice choice : question.getChoices()){
                        CheckBox checkbox = (CheckBox) questionMap.get("choice"+choice.getId());
                        lastCheckbox = checkbox;
                        Log.d(TAG, "checkbox -> " +checkbox.getText().toString() +" selected -> " +checkbox.isSelected());
                        if(checkbox.isChecked()){
                            checkList.add(new ResponseDetail(0, question.getId(), null, choice.getId()));
                        }
                    }
                    if(question.getIs_mandatory() == 1){
                        if(checkList.size() < 1){
                            setErrorLabel(question.getId());
                            hasError = true;
                        }
                    }
                    details.addAll(checkList);
                    continue;
                case "Likert Scale":
                    List<ResponseDetail> rows = new ArrayList<>();
                    for (QuestionRow row : question.getRows()){
                        RadioGroup rowGroup = (RadioGroup) questionMap.get("row"+row.getId());
                        choiceId = rowGroup.getCheckedRadioButtonId();
                        Log.d(TAG, "ls choice selected -> " +choiceId);
                        rows.add(new ResponseDetail(0, question.getId(), null, choiceId, row.getId()));
                        if(question.getIs_mandatory() == 1){
                            Log.d(TAG, "mandatory ls");
                            if(choiceId <= 0){
                                Log.d(TAG, "mc has error");
                                TextView label = (TextView) content.findViewById(6996+question.getId());
                                label.setError("Please Rate All Fields");
                                hasError = true;
                                break;
                            }
                        }
                    }
                    if(question.getIs_mandatory() == 1){
                        if(rows.size() < 1){
                            setErrorLabel(question.getId());
                            hasError = true;
                        }
                    }
                    details.addAll(rows);
                    continue;
                    default:
                        //

            }
            details.add(new ResponseDetail(0, question.getId(), textAnswer, choiceId));
        }
        if(hasError) return null;

        return details;
    }

    public void setErrorLabel(int questionId){
        TextView label = (TextView) content.findViewById(6996+questionId);
        label.setError("Required Question");
    }


    //SET THIS ON SUBMIT BUTTON
    public void submitResponse(View view){
        //CREATE RESPONSE DETAILS
        //COLLECT QUESTION ANSWERS
//        Button submit = (Button) view.findViewById(R.id.submit);
//        submit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                List<ResponseDetail> details = new ArrayList<ResponseDetail>();
//                for(Question question : questions){
//                    String textAnswer = null;
//                    int choiceId = -1;
//                    switch (question.getQuestion_type().getType()){
//                        case "textbox":
//                        case "textarea":
//                            EditText edit = (EditText)content.findViewById(question.getId());
//                            textAnswer = edit.getText().toString();
//                            break;
//                        case "multiple choice":
//                            RadioGroup group = (RadioGroup) content.findViewById(question.getId());
//
//                            choiceId = group.getCheckedRadioButtonId();
//                            break;
//                    }
//                    details.add(new ResponseDetail(0, question.getId(), textAnswer, choiceId));
//                }
//
//                buttonListener.onSubmit(details);
//            }
//        });
    }

}


