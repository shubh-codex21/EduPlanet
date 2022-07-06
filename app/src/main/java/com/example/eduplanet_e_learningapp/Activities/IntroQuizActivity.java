package com.example.eduplanet_e_learningapp.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eduplanet_e_learningapp.Modals.QuestionModal;
import com.example.eduplanet_e_learningapp.R;
import com.example.eduplanet_e_learningapp.databinding.ActivityIntroQuizBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Random;

public class IntroQuizActivity extends AppCompatActivity {
    int noOfQuestions;
    ArrayList<QuestionModal> questionArrayList;
    Random random;
    int currentScore = 0, incorrectScore = 0, questionNo = 1, Index = 0, curPos, progress = 20;
    Handler handler = new Handler();
    QuestionModal question;
    ProgressDialog progressDialog;
    BottomSheetDialog bottomDialog;
    ArrayList<String> correctResponse, incorrectResponse;
    TextView chosenOption;
    MaterialCardView chosenCard;
    Boolean isOptionClicked = false, isSkipUsed = false;
    int classLevel;

    ActivityIntroQuizBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        random = new Random();
        questionArrayList = new ArrayList<>();
        correctResponse = new ArrayList<>();
        incorrectResponse = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading Questions...");
        progressDialog.show();

        classLevel = getIntent().getIntExtra("ClassLevel",1);

        noOfQuestions = 5;

        addQuestions(classLevel);
        addResponses();

        binding.optionA.setOnClickListener(this::onClick);
        binding.optionB.setOnClickListener(this::onClick);
        binding.optionC.setOnClickListener(this::onClick);
        binding.optionD.setOnClickListener(this::onClick);
        binding.skipCard.setOnClickListener(this::onClick);
        binding.checkAnswerCard.setOnClickListener(this::onClick);

        binding.progressBar.setProgress(20);

    }

    private void addQuestions(int classLevel) {

        if (classLevel == 1){

            questionArrayList.add(new QuestionModal("10 digit greatest number is", "9000000000", "45839", "2854630289"
                    , "9999999999", "9999999999"));

            questionArrayList.add(new QuestionModal("Who is the current president of US?", "Joe Biden", "Narendra Modi"
                    , "Vladimir Putin", "Donald Trump", "Joe Biden"));

            questionArrayList.add(new QuestionModal("Which of the following is the body part?", "Mango", "Nose", "Table"
                    , "Lion", "Nose"));

            questionArrayList.add(new QuestionModal("Do you know who is the Missile Man of India", "Dr. APJ Abdul Kalam",
                    "Ram Nath Kovind", "Rabindranath Tagore", "None of the above", "Dr. APJ Abdul Kalam"));

            questionArrayList.add(new QuestionModal("Add 800+3800", "5400", "-3750", "4600", "-5400",
                    "4600"));

            questionArrayList.add(new QuestionModal("What is the brain of computer?","Desktop","Mouse","CPU","Processor",
                    "CPU"));

        } else if (classLevel == 2){



        } else if (classLevel == 3){



        }

        curPos = random.nextInt(questionArrayList.size());
        setNextQuestion();

    }

    private void setNextQuestion() {
        progressDialog.dismiss();
        setVisibility();
        setCardVisibility();
        setCardClickableTrue();
        binding.skipCard.setClickable(true);

        if (Index < noOfQuestions) {
            question = questionArrayList.get(curPos);

            binding.questionNo.setText("Question " + questionNo++ + "/" + noOfQuestions);
            binding.question.setText(question.getQuestion());
            binding.optionA.setText(question.getOptionA());
            binding.optionB.setText(question.getOptionB());
            binding.optionC.setText(question.getOptionC());
            binding.optionD.setText(question.getOptionD());

            questionArrayList.remove(curPos);

        } else {
            finishQuiz();
        }
    }

    private void setVisibility() {
        binding.question.setVisibility(View.VISIBLE);
        binding.optionA.setVisibility(View.VISIBLE);
        binding.optionB.setVisibility(View.VISIBLE);
        binding.optionC.setVisibility(View.VISIBLE);
        binding.optionD.setVisibility(View.VISIBLE);
        binding.questionNo.setVisibility(View.VISIBLE);
    }

    private void setCardVisibility() {
        binding.opACard.setVisibility(View.VISIBLE);
        binding.opBCard.setVisibility(View.VISIBLE);
        binding.opCCard.setVisibility(View.VISIBLE);
        binding.opDCard.setVisibility(View.VISIBLE);
    }

    private void setCardClickableTrue() {
        binding.opACard.setClickable(true);
        binding.opBCard.setClickable(true);
        binding.opCCard.setClickable(true);
        binding.opDCard.setClickable(true);
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.option_A:

                if (binding.opACard.isClickable()) {
                    setCardClickableFalse();
                    chosenOption = binding.optionA;
                    chosenCard = binding.opACard;
                    binding.opACard.setStrokeColor(getResources().getColor(R.color.black));
                    binding.checkAnswerCard.setCardBackgroundColor(getResources().getColor(R.color.chroma_green));
                    isOptionClicked = true;
                } else {
                    break;
                }
                break;

            case R.id.option_B:

                if (binding.opACard.isClickable()) {
                    setCardClickableFalse();
                    chosenOption = binding.optionB;
                    chosenCard = binding.opBCard;
                    binding.opBCard.setStrokeColor(getResources().getColor(R.color.black));
                    binding.checkAnswerCard.setCardBackgroundColor(getResources().getColor(R.color.chroma_green));
                    isOptionClicked = true;
                } else {
                    break;
                }
                break;

            case R.id.option_C:

                if (binding.opACard.isClickable()) {
                    setCardClickableFalse();
                    chosenOption = binding.optionC;
                    chosenCard = binding.opCCard;
                    binding.opCCard.setStrokeColor(getResources().getColor(R.color.black));
                    binding.checkAnswerCard.setCardBackgroundColor(getResources().getColor(R.color.chroma_green));
                    isOptionClicked = true;
                } else {
                    break;
                }
                break;

            case R.id.option_D:

                if (binding.opACard.isClickable()) {
                    setCardClickableFalse();
                    chosenOption = binding.optionD;
                    chosenCard = binding.opDCard;
                    binding.opDCard.setStrokeColor(getResources().getColor(R.color.black));
                    binding.checkAnswerCard.setCardBackgroundColor(getResources().getColor(R.color.chroma_green));
                    isOptionClicked = true;
                } else {
                    break;
                }
                break;

            case R.id.skipCard:

                if (!isSkipUsed) {
                    skipQuestion();
                } else {
                    Toast.makeText(this, "You have used your Skip.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.checkAnswerCard:

                checkAnswer(chosenOption, chosenCard);
                isOptionClicked = false;
                binding.checkAnswerCard.setCardBackgroundColor(getResources().getColor(R.color.light_grey));
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkAnswer(TextView option, MaterialCardView card) {

        if (option.getText().toString().trim().toLowerCase()
                .equals(question.getCorrectAnswer().trim().toLowerCase())) {

            currentScore++;
            card.setStrokeColor(getColorStateList(R.color.green));
            showBottomDialog(true);

        } else {
            incorrectScore++;
            card.setStrokeColor(getColorStateList(R.color.red));

            if (binding.optionA.getText().toString().trim().toLowerCase()
                    .equals(question.getCorrectAnswer().trim().toLowerCase())) {
                binding.opACard.setStrokeColor(getColorStateList(R.color.green));
            } else if (binding.optionB.getText().toString().trim().toLowerCase()
                    .equals(question.getCorrectAnswer().trim().toLowerCase())) {
                binding.opBCard.setStrokeColor(getColorStateList(R.color.green));
            } else if (binding.optionC.getText().toString().trim().toLowerCase()
                    .equals(question.getCorrectAnswer().trim().toLowerCase())) {
                binding.opCCard.setStrokeColor(getColorStateList(R.color.green));
            } else if (binding.optionD.getText().toString().trim().toLowerCase()
                    .equals(question.getCorrectAnswer().trim().toLowerCase())) {
                binding.opDCard.setStrokeColor(getColorStateList(R.color.green));
            }

            showBottomDialog(false);

        }


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void resetCardStrokeColors() {
        binding.opACard.setStrokeColor(getColorStateList(R.color.blue1));
        binding.opBCard.setStrokeColor(getColorStateList(R.color.blue1));
        binding.opCCard.setStrokeColor(getColorStateList(R.color.blue1));
        binding.opDCard.setStrokeColor(getColorStateList(R.color.blue1));
    }

    private void setCardClickableFalse() {
        binding.opACard.setClickable(false);
        binding.opBCard.setClickable(false);
        binding.opCCard.setClickable(false);
        binding.opDCard.setClickable(false);
    }

    private void skipQuestion() {
        binding.skipCard.setClickable(false);
        isSkipUsed = true;

        if (Index < noOfQuestions) {
            handler.postDelayed(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void run() {
                    resetCardStrokeColors();
                    Index++;
                    curPos = random.nextInt(questionArrayList.size());
                    setNextQuestion();
                    setProgressBarProgress();
                }
            }, 500);
        } else {
            finishQuiz();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceAsColor")
    private void showBottomDialog(Boolean isAnswerCorrect) {
        bottomDialog = new BottomSheetDialog(this, R.style.BottomSheetTheme);
        bottomDialog.setCancelable(false);

        View bsView = LayoutInflater.from(this).
                inflate(R.layout.quizfor1to5_bottomsheet_layout,
                        this.findViewById(R.id.checkAnswer_bottomsheet));

        TextView response = bsView.findViewById(R.id.response);
        TextView nextBtn = bsView.findViewById(R.id.nextBtn);

        int correctResIndex = random.nextInt(correctResponse.size());
        int incorrectResIndex = random.nextInt(incorrectResponse.size());

        if (Index < noOfQuestions-1) {
            nextBtn.setText("Next");
        } else {
            nextBtn.setText("Finish");
        }


        if (isAnswerCorrect) {
            response.setText(correctResponse.get(correctResIndex));
            bsView.findViewById(R.id.checkAnswer_bottomsheet).setBackgroundColor(getResources().getColor(R.color.green2));
            nextBtn.setBackground(getDrawable(R.drawable.background1));
        } else {
            response.setText(incorrectResponse.get(incorrectResIndex));
            bsView.findViewById(R.id.checkAnswer_bottomsheet).setBackgroundColor(getResources().getColor(R.color.red2));
            nextBtn.setBackground(getDrawable(R.drawable.background2));
        }

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (Index < noOfQuestions) {
                    bottomDialog.dismiss();
                    resetCardStrokeColors();
                    Index++;
                    curPos = random.nextInt(questionArrayList.size());
                    setNextQuestion();
                    setProgressBarProgress();
                } else {
                    bottomDialog.dismiss();
                    finishQuiz();
                }
            }

        });

        bottomDialog.setContentView(bsView);
        bottomDialog.show();
    }

    private void finishQuiz() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Redirecting to Score Board");
        progressDialog.show();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(IntroQuizActivity.this,IntroQuizScoreActivity.class);
                intent.putExtra("noOfQuestions",noOfQuestions);
                intent.putExtra("correctAnswers",currentScore);
                startActivity(intent);
                finish();

                progressDialog.dismiss();
            }
        },2000);

    }

    private void addResponses() {
        correctResponse.add("Excellent...");
        correctResponse.add("Very Good");
        correctResponse.add("Amazing!!!");
        correctResponse.add("Well done");
        correctResponse.add("Outstanding!");

        incorrectResponse.add("Oops!!!");
        incorrectResponse.add("Wrong");
        incorrectResponse.add("Better luck next time...");
        incorrectResponse.add("Try next");
        incorrectResponse.add("Try again");

    }

    private void setProgressBarProgress(){
        int nextProgress = (progress + 20);
        binding.progressBar.animateProgress(2000, progress, nextProgress); // (animationDuration, oldProgress, newProgress)
        progress = nextProgress;
    }

}
