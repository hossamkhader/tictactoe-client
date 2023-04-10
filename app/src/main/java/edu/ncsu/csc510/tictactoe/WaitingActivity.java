package edu.ncsu.csc510.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WaitingActivity extends AppCompatActivity {
    private WebSocketClientImpl ws;
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
            this.ws.addMessageHandler(this::messageHandler);
//            testMessageHandler();
        }
        catch (Exception e) {
            Log.d("waiting_activity_init_ws", "Exception", e);
        }
        return false;
    }

    private void bindView() {
    }

    private void setListeners() {
    }

    // This is passed to the websocket to use as the messageHandler for this page
    void messageHandler(String message) {
        // print message to log for testing purposes
        Log.d("waitingMSG", message);
        try {
            JSONObject obj = (JSONObject) new JSONParser().parse(message);
            GameState gameState = JsonUtility.jsonToGameState(message);
            WebSocketClientSingleton.setGameState(gameState);
            Intent switchActivityIntent = null;
            if (gameState.getP1() != null) {
                switchActivityIntent = new Intent(this, TicTacToeActivity.class);
                startActivity(switchActivityIntent);
                this.ws.removeMessageHandler();
                return;
            }
        }catch (ParseException e) {
            Log.d("messageHandler: ", "ParseException: ", e);
        }
    }
}