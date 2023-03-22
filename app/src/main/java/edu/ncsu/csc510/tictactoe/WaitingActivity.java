package edu.ncsu.csc510.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WaitingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);
        Button skipButton;
        skipButton = findViewById(R.id.skip_waiting);

        skipButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent gameActivity = new Intent(WaitingActivity.this, MainMenu.class);
                startActivity((gameActivity));
            }

        });
    }
}