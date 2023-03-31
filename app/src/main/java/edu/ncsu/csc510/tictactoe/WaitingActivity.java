package edu.ncsu.csc510.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class WaitingActivity extends AppCompatActivity {
    private WebSocketClientImpl ws;
    Button skipButton;
    TextView gameIdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);
        gameIdView = findViewById(R.id.text_display_gameId);
        GameState gameState = WebSocketClientSingleton.getGameState();
        gameIdView.setText(gameState.getGame_id());
        bindView();
        setListeners();
        init_ws();
        //Let the server know I am waiting for the next screen
        //this.ws.send("I am waiting" );

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
        //Should be notified from the server to go the game screen.
        //Intent gameActivity = new Intent(WaitingActivity.this, TicTacToeActivity.class);
        //            startActivity((gameActivity));
    }
}