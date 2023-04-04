package edu.ncsu.csc510.tictactoe;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.java_websocket.handshake.ServerHandshake;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

// Player representation
// 0 - X
// 1 - O
//int[][] winPositions = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8},
//            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
//           {0, 4, 8}, {2, 4, 6}};
//public static int counter = 0;
//boolean gameActive = true;
public class TicTacToeActivity extends AppCompatActivity {
    public static final int NEW_GAME = 0;
    public static final int JOIN = 1;
    public static final int PLAYING = 2;
    public static final int END = 3;

    private static final String XPLAYER = "0";
    private static final String OPLAYER = "1";

    private WebSocketClientImpl ws;
    private LinearLayout layout;
    private List<ImageView> imageList;
    private TextView status;

    void init_ws() {
        try {
            this.ws = WebSocketClientSingleton.getInstance();
            this.ws.addMessageHandler(this::updateClient);
            updateGameBoard();
        } catch (Exception e) {
            Log.d("init_ws", "Exception", e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tictactoe);
        layout = findViewById(R.id.linearLayout);
        status = findViewById(R.id.status);

        imageList = new ArrayList<ImageView>();
        for (int loop = 0; loop < layout.getChildCount(); loop++) {
            LinearLayout inner = (LinearLayout) layout.getChildAt(loop);
            for (int j = 0; j < inner.getChildCount(); j++) {
                if (inner.getChildAt(j) instanceof ImageView) {
                    ImageView image = (ImageView) inner.getChildAt(j);
                    imageList.add(image);
                }
            }
        }
        init_ws();
    }

    protected void onStop() {
        super.onStop();
        if (this.ws != null) {
            this.ws.close();
        }
    }

    // this function will be called every time a
    // players tap in an empty box of the grid
    @SuppressLint("SetTextI18n")
    public void playerTap(View view) {
        GameState gameState = WebSocketClientSingleton.getGameState();
        ImageView img = (ImageView) view;
        String tappedImageId = img.getResources().getResourceEntryName(img.getId());
        int boardNum = Integer.parseInt(tappedImageId.substring(tappedImageId.length() - 1));

        try {
            if (this.ws == null || !this.ws.isOpen()) {
                init_ws();
            }
            JSONObject op = gameStateToJson(boardNum);
            JSONArray data = new JSONArray();
            data.add(op);
            this.ws.send(data.toString());
        } catch (Exception e) {
            Log.d("playerTap", "Exception", e);
        }
    }

    // Update the game board and status by the opponent's move that was sent from server.
    void updateClient(String message) {
        Log.d("Message from server In updateClient: ", message);
        try {
            JSONObject obj = (JSONObject) new JSONParser().parse(message);
            GameState gameState = JsonUtility.jsonToGameState(message);
            WebSocketClientSingleton.setGameState(gameState);
            updateGameBoard();
        } catch (Exception e) {
            Log.d("updateClient", "Exception", e);
        }
    }

    //The runOnUiThread method is necessary to update UI.
    public void updateGameBoard() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                GameState gameState = WebSocketClientSingleton.getGameState();
                for (int i = 0; i < imageList.size(); i++) {
                    ImageView img = imageList.get(i);
                    if (gameState.board[i] != null) {
                        if (gameState.board[i].equals(XPLAYER)) {
                            img.setImageResource(R.drawable.x);
                            img.setTag(XPLAYER);
                            img.refreshDrawableState();
                            Log.d("updateGameBoard() gameState.board[" + i + "]: ", gameState.board[i]);
                        }
                        if (gameState.board[i].equals(OPLAYER)) {
                            img.setImageResource(R.drawable.o);
                            img.setTag(OPLAYER);
                            img.refreshDrawableState();
                            Log.d("updateGameBoard () gameState.board[" + i + "]: ", gameState.board[i]);
                        }
                    }
                }
                // Display status
                if (gameState.getWinner() != null) {
                    if (gameState.getWinner().equals(XPLAYER)) {
                        status.setText("X has won");
                        Log.d("Status is updated in displayGameState() : ", status.getText().toString());
                    }
                    if (gameState.getWinner().equals(OPLAYER)) {
                        status.setText("O has won");
                        Log.d("Status is updated in displayGameState() : ", status.getText().toString());
                    }
                } else {
                    if (gameState.getActivePlayer() != null) {
                        if (gameState.getActivePlayer().equals(XPLAYER)) {
                            status.setText("X's Turn - Tap to play");
                            Log.d("Status is updated in displayGameState() : ", status.getText().toString());
                        }
                        if (gameState.getActivePlayer().equals(OPLAYER)) {
                            status.setText("O's Turn - Tap to play");
                            Log.d("Status is updated in displayGameState() : ", status.getText().toString());
                        }
                    }
                }
            }
        };

        runOnUiThread(runnable);
    }

    //Convert object to Json string
    public JSONObject gameStateToJson(int piece) {
        GameState gameState = WebSocketClientSingleton.getGameState();

        JSONObject op = new JSONObject();
        try {
            op.put("action", "game_move");
            op.put("game_id", gameState.getGame_id());
            op.put("player_id", WebSocketClientSingleton.getUser().getPlayer_id());
            op.put("piece", "piece-" + Integer.toString(piece));
        } catch (Exception e) {
            Log.d("JSONToObject", "Exception", e);
        }
        return op;
    }

    // reset the game
    @SuppressLint("SetTextI18n")
    public void gameReset(View view) {
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

    }
}