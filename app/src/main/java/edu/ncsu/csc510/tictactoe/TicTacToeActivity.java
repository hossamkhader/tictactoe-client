package edu.ncsu.csc510.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URI;
import java.util.Arrays;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;


public class TicTacToeActivity extends AppCompatActivity {

    private WebSocketClientImpl ws;
    private final String game_id = "0000000000";

    private String winner;

    void init_ws() {
        try {
            TextView serverAddress = findViewById(R.id.server_address);
            String url = String.format("ws://%s:8000", serverAddress.getText());
            this.ws = new WebSocketClientImpl(new URI(url));
            this.ws.addHeader("game-id", "0000000000");
            this.ws.connectBlocking();
            this.ws.addMessageHandler(this::updateClient);
        }
        catch (Exception e) {
            Log.d("init_ws", "Exception", e);
        }
    }

    void updateClient(String message) {
        try {
            JSONObject obj = (JSONObject) new JSONParser().parse(message);
            JSONObject game = (JSONObject) obj.get(String.format("game-%s", game_id));
            activePlayer = (String) game.get("activePlayer");
            winner = (String) game.get("winner");
        }
        catch (Exception e) {
            Log.d("updateClient", "Exception", e);

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tictactoe);
    }

    protected void onStop() {
        super.onStop();
        if(this.ws!= null)
        this.ws.close();
    }

        boolean gameActive = true;

    // Player representation
    // 0 - X
    // 1 - O
    String activePlayer = "0";
    String[] gameState = {"2", "2", "2", "2", "2", "2", "2", "2", "2"};

    // State meanings:
    //    0 - X
    //    1 - O
    //    2 - Null
    // put all win positions in a 2D array
    int[][] winPositions = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6}};
    public static int counter = 0;

    // this function will be called every time a
    // players tap in an empty box of the grid
    @SuppressLint("SetTextI18n")
    public void playerTap(View view) {
        if (winner != null) {
            gameReset(view);
        }

        ImageView img = (ImageView) view;
        int tappedImage = Integer.parseInt(img.getTag().toString());
        TextView status = findViewById(R.id.status);

        if (activePlayer.compareTo("0") == 0) {
            img.setImageResource(R.drawable.x);
            status.setText("O's Turn - Tap to play");
        }
        if (activePlayer.compareTo("1") == 0) {
            img.setImageResource(R.drawable.o);
            status.setText("X's Turn - Tap to play");
        }

        try {
            if (this.ws==null || !this.ws.isOpen()) {
                init_ws();
            }
            JSONArray op_list = new JSONArray();
            JSONObject op = new JSONObject();

            op.put("op", "replace");
            op.put("path", String.format("/game-%s/piece-%s", game_id, tappedImage));
            op.put("value", activePlayer);
            op_list.add(op);
            this.ws.send(op_list.toString());
            Thread.sleep(500);

            if (winner != null) {
                if (winner.compareTo("0") == 0) {
                    status.setText("X has won");
                }
                if (winner.compareTo("1") == 0) {
                    status.setText("O has won");
                }
            }
        }
        catch (Exception e) {
            Log.d("playerTap", "Exception", e);
        }

    }

    // reset the game
    @SuppressLint("SetTextI18n")
    public void gameReset(View view) {
        gameActive = true;
        activePlayer = "0";
        Arrays.fill(gameState, "2");
        // remove all the images from the boxes inside the grid
        ((ImageView) findViewById(R.id.imageView0)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView1)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView2)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView3)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView4)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView5)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView6)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView7)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView8)).setImageResource(0);

        TextView status = findViewById(R.id.status);
        status.setText("X's Turn - Tap to play");
    }
}