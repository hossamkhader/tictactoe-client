package edu.ncsu.csc510.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

public class WaitingActivity extends AppCompatActivity {
    private WebSocketClientImpl ws;
    Button skipButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);
        bindView();
        setListeners();
        init_ws();

    }

    boolean init_ws() {
        try {
            this.ws = WebSocketClientSingleton.getInstance();
            this.ws.removeMessageHandler();
            this.ws.addMessageHandler(this::messageHandler);
//            testMessageHandler();
        }
        catch (Exception e) {
            Log.d("waiting_activity_init_ws", "Exception", e);
        }
        return false;
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

    // This is passed to the websocket to use as the messageHandler for this page
    void messageHandler(String message) {
        // print message to log for testing purposes
        Log.d("waitingMSG", message);
    }
}