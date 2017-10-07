package com.example.jake.w4_p4;

        import android.content.res.TypedArray;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.TextView;
        import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String logFlag = "W4_P4";  // Marker for logging events from the app.

    private ImageView hangmanView;
    private TextView wordView, hintView;
    private LinearLayout letterRow1, letterRow2, letterRow3, letterRow4;
    private Button newGameButton;

    private char[] letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static Button[] letterButtons = new Button[26];
    private static boolean firstGame = true;
    private static String wordToSolve;
    private static String wordToDisplay;
    private static int lives = 6;
    private static String hint;
    private static Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(logFlag, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up view references
        hangmanView = (ImageView) findViewById(R.id.hangmanView);
        wordView = (TextView) findViewById(R.id.wordView);
        hintView = (TextView) findViewById(R.id.hintView);
        letterRow1 = (LinearLayout) findViewById(R.id.letterRow1);
        letterRow2 = (LinearLayout) findViewById(R.id.letterRow2);
        letterRow3 = (LinearLayout) findViewById(R.id.letterRow3);
        letterRow4 = (LinearLayout) findViewById(R.id.letterRow4);
        newGameButton = (Button) findViewById(R.id.newGameButton);

        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpNewGame();
            }
        });

        int letterCount = 0;

        for (final char letter : letters) {
            final Button b = new Button(this);
            b.setTag(String.valueOf(letter));
            b.setSaveEnabled(true);

            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            p.width = 120;
            p.weight = 0f;

            b.setLayoutParams(p);
            b.setText(String.valueOf(letter));

            View.OnClickListener letterClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processGuess(letter);
                    b.setEnabled(false);
                }
            };
            b.setOnClickListener(letterClickListener);

            if (letterCount < 7) {
                letterRow1.addView(b);
            } else if (letterCount < 14) {
                letterRow2.addView(b);
            } else if (letterCount < 21) {
                letterRow3.addView(b);
            } else {
                letterRow4.addView(b);
            }
            letterButtons[letterCount] = b;
            letterCount++;
        }

        if (savedInstanceState != null) {
            for (Button b : letterButtons) {
                b.setEnabled(savedInstanceState.getBoolean((String) b.getTag()));
            }
            wordToSolve = savedInstanceState.getString("WORD_SOLVED_KEY");
            wordToDisplay = savedInstanceState.getString("WORD_DISPLAY_KEY");
            wordView.setText(wordToDisplay);
            hint = savedInstanceState.getString("HINT_KEY");
            if (hintView != null) {
                hintView.setText("HINT: " + hint);
            }
            firstGame = savedInstanceState.getBoolean("FIRST_GAME");
            lives = savedInstanceState.getInt("LIVES_LEFT");
            setHangmanImage();
        }

        if (firstGame) {
            setUpNewGame();
            firstGame = false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(logFlag, "onSaveInstanceState");
        for (Button b : letterButtons) {
            outState.putBoolean((String) b.getTag(), b.isEnabled());
        }
        outState.putString("WORD_SOLVED_KEY", wordToSolve);
        outState.putString("WORD_DISPLAY_KEY", wordToDisplay);
        outState.putString("HINT_KEY", hint);
        outState.putBoolean("FIRST_GAME", firstGame);
        outState.putInt("LIVES_LEFT", lives);
        super.onSaveInstanceState(outState);
    }

    private void processGuess(char letter) {
        String response;
        boolean gameEnd = false;
        boolean correct = false;
        for (int i = 0; i < wordToSolve.length(); i++) {
            if (wordToSolve.charAt(i) == letter) {
                char[] wordArray = wordToDisplay.toCharArray();
                wordArray[i * 2] = letter;
                wordToDisplay = String.valueOf(wordArray);
                wordView.setText(wordToDisplay);
                correct = true;
            }
        }
        if (correct) {
            if (wordToDisplay.indexOf('_') == -1) {
                response = "Good job, you guessed the word!";
                gameEnd = true;
            } else {
                response = "Correct!";
            }
        } else {
            lives--;
            if (lives == 0) {
                response = "No more guesses. Game over!";
                gameEnd = true;
            } else {
                response = "Wrong! Try again.";
            }
            setHangmanImage();
        }

        if (gameEnd) {
            for (Button b : letterButtons) {
                b.setEnabled(false);
            }
        }
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void setUpNewGame() {
        for (Button b : letterButtons) {
            b.setEnabled(true);
        }

        TypedArray words = getResources().obtainTypedArray(R.array.hangman_words);
        int index = (int) (Math.random() * words.length());
        int arrayId = words.getResourceId(index, R.array.word0);
        String[] word = getResources().getStringArray(arrayId);

        wordToSolve = word[0];
        hint = word[1];
        wordToDisplay = new String(new char[wordToSolve.length()]).replace("\0", "_");
        wordToDisplay = wordToDisplay.replace("", " ").trim();
        wordView.setText(wordToDisplay);
        if (hintView != null) {
            hintView.setText("HINT: " + word[1]);
        }
        lives = 6;
        setHangmanImage();
    }

    private void setHangmanImage() {
        switch (lives) {
            case 6:
                hangmanView.setImageResource(R.drawable.hangman_0);
                break;
            case 5:
                hangmanView.setImageResource(R.drawable.hangman_1);
                break;
            case 4:
                hangmanView.setImageResource(R.drawable.hangman_2);
                break;
            case 3:
                hangmanView.setImageResource(R.drawable.hangman_3);
                break;
            case 2:
                hangmanView.setImageResource(R.drawable.hangman_4);
                break;
            case 1:
                hangmanView.setImageResource(R.drawable.hangman_5);
                break;
            case 0:
                hangmanView.setImageResource(R.drawable.hangman_6);
                break;
            default:
                hangmanView.setImageResource(R.drawable.hangman_0);
                break;
        }
    }

}
