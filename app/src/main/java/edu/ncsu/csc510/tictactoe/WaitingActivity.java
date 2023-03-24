package edu.ncsu.csc510.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

public class WaitingActivity extends AppCompatActivity {
    Button skipButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);
        bindView();
        setListeners();

    }

    private void bindView() {
        skipButton = findViewById(R.id.button_skip_waiting);
    }

    private void setListeners() {
        skipButton.setOnClickListener(v -> {
            Intent gameActivity = new Intent(WaitingActivity.this, TicTacToeActivity.class);
            startActivity((gameActivity));
        });
    }
}