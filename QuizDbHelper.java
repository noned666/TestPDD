package com.example.myawesomequiz;




import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myawesomequiz.QuizContract.*;

import java.util.ArrayList;


public class QuizDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "AwesomesPDD.db";
    private static final int DATABASE_VERSION = 2;

    private SQLiteDatabase db;

    public QuizDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;

        final String SQL_CREATE_QUESTIONS_TABLE = "CREATE TABLE " +
                QuizContract.QuestionsTable.TABLE_NAME + " ( " +
                QuizContract.QuestionsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuizContract.QuestionsTable.COLUMN_QUESTION + " TEXT, " +
                QuizContract.QuestionsTable.COLUMN_OPTION1 + " TEXT, " +
                QuizContract.QuestionsTable.COLUMN_OPTION2 + " TEXT, " +
                QuizContract.QuestionsTable.COLUMN_OPTION3 + " TEXT, " +
                QuizContract.QuestionsTable.COLUMN_ANSWER_NR + " INTEGER," +
                QuizContract.QuestionsTable.COLUMN_DIFFICULTY + " TEXT " +
                ")";

        db.execSQL(SQL_CREATE_QUESTIONS_TABLE);
        fillQuestionsTable();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + QuizContract.QuestionsTable.TABLE_NAME);
        onCreate(db);
    }

    private void fillQuestionsTable() {
        Question q1 = new Question("Ты лох ",
                "A) Лох", "B) Не лох", "C) Сам лох", 1, Question.TICKET_FOUR);
        addQuestion(q1);
        Question q2 = new Question("Medium: 2 правильный",
                "A", "B", "C ", 2, Question.DIFFICULTY_MEDIUM);
        addQuestion(q2);
        Question q3 = new Question("Medium: 2 правильный",
                "A", "B", "C", 2, Question.DIFFICULTY_MEDIUM);
        addQuestion(q3);
        Question q4 = new Question("Hard: 3 правильный",
                "A", "B", "C", 3, Question.DIFFICULTY_HARD);
        addQuestion(q4);
        Question q5 = new Question("Hard:  правильный",
                "A", "B", "C", 1, Question.DIFFICULTY_HARD);
        addQuestion(q5);
        Question q6 = new Question("Hard: 2 правильный",
                "A", "B", "C", 2, Question.DIFFICULTY_HARD);
        addQuestion(q6);

    }

    private void addQuestion(Question question) {
        ContentValues cv = new ContentValues();
        cv.put(QuestionsTable.COLUMN_QUESTION, question.getQuestion());
        cv.put(QuestionsTable.COLUMN_OPTION1, question.getOption1());
        cv.put(QuestionsTable.COLUMN_OPTION2, question.getOption2());
        cv.put(QuestionsTable.COLUMN_OPTION3, question.getOption3());
        cv.put(QuestionsTable.COLUMN_ANSWER_NR, question.getAnswerNr());
        cv.put(QuestionsTable.COLUMN_DIFFICULTY, question.getDifficulty());
        db.insert(QuestionsTable.TABLE_NAME, null, cv);
    }
    public ArrayList<Question> getAllQuestions(){
            ArrayList<Question> questionsList = new ArrayList<>();
            db = getReadableDatabase();
            Cursor c = db.rawQuery(" SELECT * FROM " + QuestionsTable.TABLE_NAME, null);

                    if(c.moveToFirst()){
                        do{
                            Question question = new Question();
                            question.setQuestion(c.getString(c.getColumnIndexOrThrow(QuestionsTable.COLUMN_QUESTION)));
                            question.setOption1(c.getString(c.getColumnIndexOrThrow(QuestionsTable.COLUMN_OPTION1)));
                            question.setOption2(c.getString(c.getColumnIndexOrThrow(QuestionsTable.COLUMN_OPTION2)));
                            question.setOption3(c.getString(c.getColumnIndexOrThrow(QuestionsTable.COLUMN_OPTION3)));
                            question.setAnswerNr(c.getInt(c.getColumnIndexOrThrow(QuestionsTable.COLUMN_ANSWER_NR)));
                            questionsList.add(question);
                        }while ((c.moveToNext()));
                    }
                    c.close();
                    return  questionsList;
    }




    public ArrayList<Question> getQuestions(String difficulty) {
        ArrayList<Question> questionList = new ArrayList<>();
        db = getReadableDatabase();

        String[] selectionArgs = new String[]{difficulty};
        Cursor c = db.rawQuery("SELECT * FROM " + QuestionsTable.TABLE_NAME +
                " WHERE " + QuestionsTable.COLUMN_DIFFICULTY + " = ?",selectionArgs);

        if (c.moveToFirst()) {
            do {
                Question question = new Question();
                question.setQuestion(c.getString(c.getColumnIndexOrThrow(QuestionsTable.COLUMN_QUESTION)));
                question.setOption1(c.getString(c.getColumnIndexOrThrow(QuestionsTable.COLUMN_OPTION1)));
                question.setOption2(c.getString(c.getColumnIndexOrThrow(QuestionsTable.COLUMN_OPTION2)));
                question.setOption3(c.getString(c.getColumnIndexOrThrow(QuestionsTable.COLUMN_OPTION3)));
                question.setAnswerNr(c.getInt(c.getColumnIndexOrThrow(QuestionsTable.COLUMN_ANSWER_NR)));
                question.setDifficulty(c.getString(c.getColumnIndexOrThrow(QuestionsTable.COLUMN_DIFFICULTY)));
                questionList.add(question);
            } while (c.moveToNext());
        }

        c.close();
        return questionList;
    }


}
