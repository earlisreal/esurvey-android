package com.fantasticfour.esurvey.Global;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;

import com.fantasticfour.esurvey.Objects.Question;
import com.fantasticfour.esurvey.Objects.QuestionChoice;
import com.fantasticfour.esurvey.Objects.QuestionOption;
import com.fantasticfour.esurvey.Objects.QuestionRow;
import com.fantasticfour.esurvey.Objects.QuestionType;
import com.fantasticfour.esurvey.Objects.Response;
import com.fantasticfour.esurvey.Objects.ResponseDetail;
import com.fantasticfour.esurvey.Objects.Survey;
import com.fantasticfour.esurvey.Objects.SurveyPage;
import com.fantasticfour.esurvey.Objects.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by earl on 9/20/2016.
 */
public class Database extends SQLiteOpenHelper {
    //VARIABLE DEFINITIONS
    private static String DATABASE_NAME = "eSurvey";
    private static int DATABASE_VERSION = 13;

    //SQL STATEMENTS
    private static String CREATE_TABLE_USERS =
    "CREATE TABLE users (id INTEGER PRIMARY KEY, username TEXT, first_name TEXT, last_name TEXT, email TEXT)";

    private static String CREATE_TABLE_SURVEYS =
    "CREATE TABLE surveys (id INTEGER PRIMARY KEY, user_id INTEGER, survey_title TEXT, created_at TEXT, updated_at TEXT)";

    private static String CREATE_TABLE_SURVEY_PAGES =
    "CREATE TABLE survey_pages (id INTEGER PRIMARY KEY, survey_id INTEGER, page_no INTEGER, page_title TEXT, page_description TEXT)";

    private static String CREATE_TABLE_QUESTION_TYPES =
    "CREATE TABLE question_types (id INTEGER PRIMARY KEY, type TEXT, has_choices INTEGER)";

    private static String CREATE_TABLE_QUESTIONS =
    "CREATE TABLE questions (id INTEGER PRIMARY KEY, survey_page_id INTEGER, question_type_id INTEGER, question_title TEXT, order_no INTEGER, is_mandatory INTEGER)";

    private static String CREATE_TABLE_QUESTION_OPTIONS =
    "CREATE TABLE question_options (id INTEGER PRIMARY KEY, question_id INTEGER, max_rating INTEGER)";

    private static String CREATE_TABLE_QUESTION_CHOICES =
    "CREATE TABLE question_choices (id INTEGER PRIMARY KEY, question_id INTEGER, label TEXT)";

    private static String CREATE_TABLE_QUESTION_ROWS =
            "CREATE TABLE question_rows (id INTEGER PRIMARY KEY, question_id INTEGER, label TEXT)";

    private static String CREATE_TABLE_RESPONSES =
    "CREATE TABLE responses (id INTEGER PRIMARY KEY AUTOINCREMENT, survey_id INTEGER, synced INTEGER DEFAULT 0, created_at TEXT)";

    private static String CREATE_TABLE_RESPONSE_DETAILS =
    "CREATE TABLE response_details (id INTEGER PRIMARY KEY AUTOINCREMENT, response_id INTEGER, question_id INTEGER, text_answer TEXT, choice_id INTEGER)";



    public Database(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_USERS);
        sqLiteDatabase.execSQL(CREATE_TABLE_SURVEYS);
        sqLiteDatabase.execSQL(CREATE_TABLE_SURVEY_PAGES);
        sqLiteDatabase.execSQL(CREATE_TABLE_QUESTION_TYPES);
        sqLiteDatabase.execSQL(CREATE_TABLE_QUESTIONS);
        sqLiteDatabase.execSQL(CREATE_TABLE_QUESTION_OPTIONS);
        sqLiteDatabase.execSQL(CREATE_TABLE_QUESTION_CHOICES);
        sqLiteDatabase.execSQL(CREATE_TABLE_QUESTION_ROWS);
        sqLiteDatabase.execSQL(CREATE_TABLE_RESPONSES);
        sqLiteDatabase.execSQL(CREATE_TABLE_RESPONSE_DETAILS);

        try {
            sqLiteDatabase.beginTransaction();

            insertQuestionType(sqLiteDatabase, 1, "Multiple Choice", 1);
            insertQuestionType(sqLiteDatabase, 2, "Dropdown", 1);
            insertQuestionType(sqLiteDatabase, 3, "Textbox", 0);
            insertQuestionType(sqLiteDatabase, 4, "Text Area", 0);
            insertQuestionType(sqLiteDatabase, 5, "Checkbox", 1);
            insertQuestionType(sqLiteDatabase, 6, "Rating Scale", 1);
            insertQuestionType(sqLiteDatabase, 7, "Likert Scale", 1);
            sqLiteDatabase.setTransactionSuccessful();
        }catch (SQLiteException e){
            Log.e(Config.TAG, e.getMessage());
        }finally {
            sqLiteDatabase.endTransaction();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS users");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS surveys");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS survey_pages");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS questions");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS question_choices");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS question_rows");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS question_types");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS question_options");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS responses");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS response_details");
    }

    public boolean insertUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", user.getUsername());
        cv.put("first_name", user.getFirst_name());
        cv.put("last_name", user.getLast_name());
        cv.put("email", user.getEmail());
        cv.put("id", user.getId());
        try {
            db.insertOrThrow("users", null, cv);
        }catch (SQLiteException e){
            db.close();
            return false;
        }finally {
            db.close();
        }
        return true;
    }

    public void removeDeletedSurveys(int userId, List<Survey> surveys){
        List<Survey> currentSurveys = getSurveys(userId);
        for (Survey currentSurvey : currentSurveys){
            if(!isExistingSurvey(currentSurvey, surveys)){
                deleteSurvey(currentSurvey.getId());
            }
        }
    }

    public boolean isExistingSurvey(Survey survey, List<Survey> newSurveys){
        for (Survey newSurvey : newSurveys){
            if(survey.getId() == newSurvey.getId()){
                return true;
            }
        }
        return false;
    }

    public void storeSurvey(Survey survey){
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();

            insertSurvey(db, survey);
            for (SurveyPage page : survey.getPages()){
                insertSurveyPage(db, page);

                for (Question question : page.getQuestions()) {
                    insertQuestion(db, question);

                    if(question.getQuestion_type().getHas_choices() == 1){
                        for (QuestionChoice choice : question.getChoices()) {
                            insertQuestionChoice(db, choice);
                        }
                    }

                    for (QuestionRow row : question.getRows()) {
                        insertQuestionRow(db, row);
                    }

                    if(question.getOption() != null){
                        insertQuestionOption(db, question.getOption());
                    }
                }
            }
            db.setTransactionSuccessful();
        }catch (SQLiteException e){
            Log.e(Config.TAG, e.getMessage());
        }finally {
            db.endTransaction();
            db.close();
        }
    }

    public List<Survey> getSurveys(int userId){
        SQLiteDatabase db = this.getReadableDatabase();
        List<Survey> surveys = new ArrayList<>();
        try {
            db.beginTransaction();
            Cursor c = db.query(
                    "surveys",
                    new String[]{"id", "user_id", "survey_title", "created_at", "updated_at"},
                    "user_id = ?",
                    new String[]{""+userId},
                    null,
                    null,
                    "id DESC"
            );
            for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
                Survey survey = new Survey(
                        c.getInt(c.getColumnIndex("id")),
                        c.getInt(c.getColumnIndex("user_id")),
                        c.getString(c.getColumnIndex("survey_title")),
                        c.getString(c.getColumnIndex("created_at")),
                        c.getString(c.getColumnIndex("updated_at"))
                );
                survey.setPages(getPages(survey.getId()));
                survey.setResponses(getResponses(survey.getId()));
                surveys.add(survey);
            }
            db.setTransactionSuccessful();
        }catch (SQLiteException e){
            Log.e(Config.TAG, e.getMessage());
        }finally {
            db.endTransaction();
            db.close();
        }

        return surveys;
    }

    public void deleteSurvey(int surveyId){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(
                "surveys",
                "id = ?",
                new String[]{""+surveyId}
        );
    }

    public List<SurveyPage> getPages(int surveyId){
        SQLiteDatabase db = getReadableDatabase();

        Cursor p = db.query(
                "survey_pages",
                new String[]{"id", "survey_id", "page_no", "page_title", "page_description"},
                "survey_id = ?",
                new String[]{""+surveyId},
                null,
                null,
                "page_no"
        );

        List<SurveyPage> pages = new ArrayList<>();
        for(p.moveToFirst(); !p.isAfterLast(); p.moveToNext()){
            SurveyPage page = new SurveyPage(
                    p.getInt(p.getColumnIndex("id")),
                    p.getInt(p.getColumnIndex("survey_id")),
                    p.getInt(p.getColumnIndex("page_no")),
                    p.getString(p.getColumnIndex("page_title")),
                    p.getString(p.getColumnIndex("page_description"))
            );

            page.setQuestions(getQuestion(page.getId()));

            pages.add(page);
        }

        return pages;
    }

    public SurveyPage getPage(int surveyPageId){
        SQLiteDatabase db = getReadableDatabase();
        Cursor p = db.query(
                "survey_pages",
                new String[]{"id", "survey_id", "page_no", "page_title", "page_description"},
                "id = ?",
                new String[]{""+surveyPageId},
                null,
                null,
                "page_no"
        );
        p.moveToFirst();
        SurveyPage page = new SurveyPage(
                p.getInt(p.getColumnIndex("id")),
                p.getInt(p.getColumnIndex("survey_id")),
                p.getInt(p.getColumnIndex("page_no")),
                p.getString(p.getColumnIndex("page_title")),
                p.getString(p.getColumnIndex("page_description"))
        );
        page.setQuestions(getQuestion(page.getId()));
        return page;
    }

    public List<Question> getQuestion(int surveyPageId){
        SQLiteDatabase db = getReadableDatabase();
        Cursor q = db.query(
                "questions",
                new String[]{"id", "survey_page_id", "question_type_id", "question_title", "order_no", "is_mandatory"},
                "survey_page_id = ?",
                new String[]{""+surveyPageId},
                null,
                null,
                "order_no"
        );
        List<Question> questions = new ArrayList<>();
        for(q.moveToFirst(); !q.isAfterLast(); q.moveToNext()) {
            Question question = new Question(
                    q.getInt(q.getColumnIndex("id")),
                    q.getInt(q.getColumnIndex("survey_page_id")),
                    q.getInt(q.getColumnIndex("question_type_id")),
                    q.getString(q.getColumnIndex("question_title")),
                    q.getInt(q.getColumnIndex("order_no")),
                    q.getInt(q.getColumnIndex("is_mandatory"))
            );

            Cursor t = db.query(
                    "question_types",
                    new String[]{"id", "type", "has_choices"},
                    "id = ?",
                    new String[]{""+question.getQuestion_type_id()},
                    null,
                    null,
                    null
            );
            t.moveToFirst();
            question.setQuestion_type(new QuestionType(
                    t.getInt(t.getColumnIndex("id")),
                    t.getString(t.getColumnIndex("type")),
                    t.getInt(t.getColumnIndex("has_choices"))
            ));

            Cursor o = db.query(
                    "question_options",
                    new String[]{"id", "question_id", "max_rating"},
                    "question_id = ?",
                    new String[]{""+question.getId()},
                    null,
                    null,
                    null
            );

            if(o.getCount() > 0){
                o.moveToFirst();
                question.setOption(new QuestionOption(
                        o.getInt(o.getColumnIndex("id")),
                        o.getInt(o.getColumnIndex("question_id")),
                        o.getInt(o.getColumnIndex("max_rating"))
                ));
            }

            //GET QUESTION CHOICES
            if(question.getQuestion_type().getHas_choices() == 1){

                Cursor qc = db.query(
                        "question_choices",
                        new String[]{"id", "question_id", "label"},
                        "question_id = ?",
                        new String[]{""+question.getId()},
                        null,
                        null,
                        null
                );

                List<QuestionChoice> choices = new ArrayList<>();
                for(qc.moveToFirst(); !qc.isAfterLast(); qc.moveToNext()) {
                    QuestionChoice choice = new QuestionChoice(
                            qc.getInt(qc.getColumnIndex("id")),
                            qc.getInt(qc.getColumnIndex("question_id")),
                            qc.getString(qc.getColumnIndex("label"))
                    );
                    choices.add(choice);
                }
                question.setChoices(choices);
            }

            if(question.getQuestion_type().getType().matches("Likert Scale")){
                Cursor qc = db.query(
                        "question_rows",
                        new String[]{"id", "question_id", "label"},
                        "question_id = ?",
                        new String[]{""+question.getId()},
                        null,
                        null,
                        null
                );

                List<QuestionRow> rows = new ArrayList<>();
                for(qc.moveToFirst(); !qc.isAfterLast(); qc.moveToNext()) {
                    QuestionRow row = new QuestionRow(
                            qc.getInt(qc.getColumnIndex("id")),
                            qc.getInt(qc.getColumnIndex("question_id")),
                            qc.getString(qc.getColumnIndex("label"))
                    );
                    rows.add(row);
                }
                question.setRows(rows);
            }
            questions.add(question);
        }
        return questions;
    }

    public void insertSurvey(SQLiteDatabase db, Survey survey){
        ContentValues cv = new ContentValues();
        cv.put("id", survey.getId());
        cv.put("user_id", survey.getUser_id());
        cv.put("survey_title", survey.getSurvey_title());
        cv.put("created_at", survey.getCreated_at());
        cv.put("updated_at", survey.getUpdated_at());
        db.insertOrThrow("surveys", null, cv);
    }

    public void insertSurveyPage(SQLiteDatabase db, SurveyPage surveyPage){
        ContentValues cv = new ContentValues();
        cv.put("id", surveyPage.getId());
        cv.put("survey_id", surveyPage.getSurvey_id());
        cv.put("page_no", surveyPage.getPage_no());
        cv.put("page_title", surveyPage.getPage_title());
        cv.put("page_description", surveyPage.getPage_description());
        db.insertOrThrow("survey_pages", null, cv);
    }

    private void insertQuestionType(SQLiteDatabase sqLiteDatabase, int id, String type, int hasChoices){
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("type", type);
        cv.put("has_choices", hasChoices);
        sqLiteDatabase.insert("question_types", null, cv);
    }

    public void insertQuestion(SQLiteDatabase db, Question question){
        ContentValues cv = new ContentValues();
        cv.put("id", question.getId());
        cv.put("survey_page_id", question.getSurvey_page_id());
        cv.put("question_type_id", question.getQuestion_type_id());
        cv.put("question_title", question.getQuestion_title());
        cv.put("order_no", question.getOrder_no());
        cv.put("is_mandatory", question.getIs_mandatory());
        db.insertOrThrow("questions", null, cv);
    }

    public void insertQuestionChoice(SQLiteDatabase db, QuestionChoice questionChoice){
        ContentValues cv = new ContentValues();
        cv.put("id", questionChoice.getId());
        cv.put("question_id", questionChoice.getQuestion_id());
        cv.put("label", questionChoice.getLabel());
        db.insertOrThrow("question_choices", null, cv);
    }

    public void insertQuestionRow(SQLiteDatabase db, QuestionRow questionRow){
        ContentValues cv = new ContentValues();
        cv.put("id", questionRow.getId());
        cv.put("question_id", questionRow.getQuestion_id());
        cv.put("label", questionRow.getLabel());
        db.insertOrThrow("question_rows", null, cv);
    }

    public void insertQuestionOption(SQLiteDatabase db, QuestionOption questionOption){
        ContentValues cv = new ContentValues();
        cv.put("id", questionOption.getId());
        cv.put("question_id", questionOption.getQuestion_id());
        cv.put("max_rating", questionOption.getMax_rating());
        db.insertOrThrow("question_options", null, cv);
    }

    public void insertResponse(int surveyId, List<ResponseDetail> details){
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();

            ContentValues cv = new ContentValues();
            cv.put("survey_id", surveyId);
            cv.put("created_at", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            long responseId = db.insert("responses", null, cv);

            for (ResponseDetail detail : details){
                insertResponseDetail(db, responseId, detail);
            }

            db.setTransactionSuccessful();
        }catch (SQLiteException e){
            Log.e(Config.TAG, e.getMessage());
        }finally {
            db.endTransaction();
            db.close();
        }
    }

    public void insertResponseDetail(SQLiteDatabase db, long responseId, ResponseDetail responseDetail){
        ContentValues cv = new ContentValues();
        cv.put("response_id", responseId);
        cv.put("question_id", responseDetail.getQuestion_id());
        if(responseDetail.getChoice_id() > 0){
            cv.put("choice_id", responseDetail.getChoice_id());
        }
        if(responseDetail.getText_answer() != null){
            cv.put("text_answer", responseDetail.getText_answer());
        }
        db.insert("response_details", null, cv);
    }

    public void updateResponse(int responseId){
        SQLiteDatabase db = getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("synced", 1);
        db.update(
                "responses",
                cv,
                "id = ?",
                new String[]{""+responseId}
        );
        db.close();
    }

    public List<Response> getResponses(int surveyId){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(
                "responses",
                new String[]{"id", "survey_id", "created_at", "synced"},
                "survey_id = ?",
                new String[]{""+surveyId},
                null,
                null,
                null
        );
        List<Response> responses = new ArrayList<>();
        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            Response response = new Response(c.getInt(c.getColumnIndex("id")),
                    c.getInt(c.getColumnIndex("survey_id")),
                    c.getString(c.getColumnIndex("created_at")),
                    c.getInt(c.getColumnIndex("synced")));

            Cursor d = db.query(
                    "response_details",
                    new String[]{"response_id", "question_id", "choice_id", "text_answer"},
                    "response_id = ?",
                    new String[]{""+response.getId()},
                    null,
                    null,
                    null
            );
            List<ResponseDetail> details = new ArrayList<>();
            for(d.moveToFirst(); !d.isAfterLast(); d.moveToNext()) {
                details.add(new ResponseDetail(
                        d.getInt(d.getColumnIndex("response_id")),
                        d.getInt(d.getColumnIndex("question_id")),
                        d.getString(d.getColumnIndex("text_answer")),
                        d.getInt(d.getColumnIndex("choice_id"))
                ));
            }
            response.setResponseDetails(details);
            responses.add(response);
        }
        return responses;
    }


}
