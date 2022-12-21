package com.example.myawesomequiz;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import java.util.*;
import java.util.Collection;
import java.util.List;

public class QuizActivity extends AppCompatActivity {
    public  static final String EXTRA_SCORE = "extraScore";
    private static final long COUNTDOWN_IN_MILLIS = 30000;
    private TextView textViewQuestion;
    private TextView textViewScore;
    private TextView getTextViewQuestion;
    private TextView textViewQuestionCount;
    private TextView textViewCountDown;
    private TextView textViewDifficulty;
    private RadioGroup rbGroup;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private Button buttonConfirmNext;
    private String imageViewQuestion;

    private static final String KEY_SCORE = "keyScore";
    private static final String KEY_QUESTION_COUNT="keyQuestionCount";
    private static final String KEY_MILLIS_LEFT="keyMillisLeft";
    private static final String KEY_ANSWERED="keyAnswered";
    private static final String KEY_QUESTION_LIST="keyQuestionList";

    private ColorStateList textColorDefaultRb;
    private ColorStateList textColorDefaultCd;

    private CountDownTimer countDownTimer;
    private long timerLeftInMillis;


    private ArrayList<Question> questionList;
    private int questinCounter;
    private int questionCountTotal;
    private Question currentQuestion;

    private int score;
    private boolean answered;

    private long backPressedTime;

    private ImageView img;
    private static final int PICK_IMAGE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        img = (ImageView)findViewById(R.id.image_view_question);
        textViewQuestion = findViewById(R.id.text_view_question);
        textViewScore = findViewById(R.id.text_view_score);
        textViewQuestionCount = findViewById(R.id.text_view_question_count);
        textViewDifficulty = findViewById(R.id.text_view_difficulty);
        textViewCountDown = findViewById((R.id.text_view_countdown));

        rbGroup = findViewById(R.id.radio_group);
        rb1= findViewById(R.id.radio_button1);
        rb2= findViewById(R.id.radio_button2);
        rb3= findViewById(R.id.radio_button3);
        buttonConfirmNext = findViewById((R.id.button_confirm_next));

        textColorDefaultRb = rb1.getTextColors();
        textColorDefaultCd = textViewCountDown.getTextColors();

        Intent intent = getIntent();
        String difficulty = intent.getStringExtra(MainActivity.EXTRA_DIFFICULTY);

        textViewDifficulty.setText("Билет: " + difficulty);

        if(savedInstanceState == null) {
            QuizDbHelper dbHelper = new QuizDbHelper(this);
            questionList = dbHelper.getQuestions(difficulty);
            questionCountTotal = questionList.size();
            Collections.shuffle(questionList);

            showNextQuestion();
        }else {
            questionList = savedInstanceState.getParcelableArrayList(KEY_QUESTION_LIST);

            questionCountTotal = questionList.size();
            questinCounter = savedInstanceState.getInt(KEY_QUESTION_COUNT);
            currentQuestion = questionList.get(questinCounter - 1);
            score = savedInstanceState.getInt(KEY_SCORE);
            timerLeftInMillis = savedInstanceState.getLong(KEY_MILLIS_LEFT);
            answered = savedInstanceState.getBoolean(KEY_ANSWERED);

            if(!answered){
                startCountDown();
            }else{
                updateCountDownText();
                showSolution();
            }
        }
        buttonConfirmNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (!answered){
                    if (rb1.isChecked()|| rb2.isChecked() || rb3.isChecked()){
                        checkAnswer();
                    }else {
                        Toast.makeText(QuizActivity.this, "Пожалуйста выберите ответ", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    showNextQuestion();
                }
            }
        });
    }
    private void showNextQuestion(){
        rb1.setTextColor(textColorDefaultRb);
        rb2.setTextColor(textColorDefaultRb);
        rb3.setTextColor(textColorDefaultRb);
        rbGroup.clearCheck();

        if (questinCounter < questionCountTotal){
            currentQuestion = questionList.get(questinCounter);

            textViewQuestion.setText((currentQuestion.getQuestion()));
            rb1.setText(currentQuestion.getOption1());
            rb2.setText(currentQuestion.getOption2());
            rb3.setText(currentQuestion.getOption3());

            questinCounter++;
            textViewQuestionCount.setText(("Вопросы:")+questinCounter+ "/" + questionCountTotal);
            answered = false;
            buttonConfirmNext.setText("Подтверждать");

            timerLeftInMillis= COUNTDOWN_IN_MILLIS;
            startCountDown();

        }else{
            finishQuiz();
        }
    }

    private void startCountDown(){
        countDownTimer=new CountDownTimer(timerLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerLeftInMillis=millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timerLeftInMillis = 0;
                updateCountDownText();
                checkAnswer();
            }
        }.start();
    }

    private void updateCountDownText(){
    int minutes = (int)(timerLeftInMillis/1000)/60;
    int seconds=(int) (timerLeftInMillis/1000)%60;
    String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

    textViewCountDown.setText(timeFormatted);

    if(timerLeftInMillis<10000){
        textViewCountDown.setTextColor(Color.RED);
        }else{
            textViewCountDown.setTextColor(textColorDefaultCd);
        }
    }

    private void checkAnswer(){
        answered=true;

        countDownTimer.cancel();
        RadioButton rbSelected = findViewById(rbGroup.getCheckedRadioButtonId());
        int answerNr = rbGroup.indexOfChild(rbSelected) +1;

        if (answerNr == currentQuestion.getAnswerNr()) {
            score++;
            textViewScore.setText("Score" + score);
        }

        showSolution();
    }
private void showSolution(){
        rb1.setTextColor(Color.RED);
        rb2.setTextColor(Color.RED);
        rb3.setTextColor(Color.RED);

        switch (currentQuestion.getAnswerNr()){
            case 1:
                rb1.setTextColor(Color.GREEN);
                textViewQuestion.setText("Ответ 1 правильный" );
                break;
            case 2:
                rb2.setTextColor(Color.GREEN);
                textViewQuestion.setText("Ответ 2 правильный" );
                break;
            case 3:
                rb3.setTextColor(Color.GREEN);
                textViewQuestion.setText("Ответ 3 правильный" );
                break;

        }
        if (questinCounter < questionCountTotal) {
            buttonConfirmNext.setText("Следующий");
        }else {
            buttonConfirmNext.setText("Финиш");
        }
}
    private void finishQuiz(){
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_SCORE, score);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            finish();
        }else{
            Toast.makeText(this, "Press back again to finish", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected  void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SCORE, score);
        outState.putInt(KEY_QUESTION_COUNT, questinCounter);
        outState.putLong(KEY_MILLIS_LEFT, timerLeftInMillis);
        outState.putBoolean(KEY_ANSWERED, answered);
        outState.putParcelableArrayList(KEY_QUESTION_LIST, questionList);
    }

}