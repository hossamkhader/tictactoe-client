package edu.ncsu.csc510.tictactoe;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
import org.w3c.dom.Text;

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
    //Declare timer
    private CountDownTimer cTimer = null;
    private TextView timer = null;
    private ImageView statusPlayer = null;

    ImageView image_view_obj = null;

    String lastPlayer = null;

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
        ImageView imgPlayerXO = findViewById(R.id.playerX);
        TextView username0 = findViewById(R.id.username0);
        ImageView imgPlayerO = findViewById(R.id.playerO);
        TextView username1 = findViewById(R.id.username1);
        timer = findViewById(R.id.timer);
        statusPlayer = findViewById(R.id.statusPlayer);

        //Set players' names
        GameState gameState = WebSocketClientSingleton.getGameState();
        username0.setText(gameState.getP0_name());
        username1.setText(gameState.getP1_name());

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
        //startTimer();
    }

    protected void onStop() {
        super.onStop();
        // We can't close WS here because that causes bug when going back to the create game screen
        // after playing a game. I think the alternative would be to put websocket close
        // into the transition between JoinActivity -> MainMenuActivity (which is not something we currently support)
//        if (this.ws != null) {
//            this.ws.close();
//        }
    }

    // this function will be called every time a
    // players tap in an empty box of the grid
    @SuppressLint("SetTextI18n")
    public void playerTap(View view) {

        GameState gameState = WebSocketClientSingleton.getGameState();
        ImageView img = (ImageView) view;
        String tappedImageId = img.getResources().getResourceEntryName(img.getId());
        int boardNum = Integer.parseInt(tappedImageId.substring(tappedImageId.length() - 1));
        boolean validMove = true;
        try {
            if (this.ws == null || !this.ws.isOpen()) {
                init_ws();
            }
            JSONObject op = gameStateToJson(boardNum);
            JSONArray data = new JSONArray();
            data.add(op);
            this.ws.send(data.toString());
            //cancelTimer();
        } catch (Exception e) {
            Log.d("playerTap", "Exception", e);
            //validMove = false;
        }
        if (validMove) {
           // cancelTimer();
        }
    }

    // Update the game board and status by the opponent's move that was sent from server.
    void updateClient(String message) {
        Log.d("Message from server In updateClient: ", message);
        try {
            lastPlayer = WebSocketClientSingleton.getGameState().getActivePlayer();
            JSONObject obj = (JSONObject) new JSONParser().parse(message);
            GameState gameState = JsonUtility.jsonToGameState(message);
            WebSocketClientSingleton.setGameState(gameState);
            if (lastPlayer != WebSocketClientSingleton.getGameState().getActivePlayer()) {
                cancelTimer();
            }
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
                    }else {
                        img.setImageResource(0);
                    }
                }
                // Display status
                if (gameState.getWinner() != null) {
                    String message = "";
                    if (gameState.getWinner().equals(XPLAYER)) {
                        statusPlayer.setImageResource(R.drawable.x);
                        status.setText(" has won");
                        message = gameState.getP0_name() + " has won!";
                        Log.d("Status is updated in displayGameState() : ", status.getText().toString());
                    }
                    if (gameState.getWinner().equals(OPLAYER)) {
                        statusPlayer.setImageResource(R.drawable.o);
                        status.setText(" has won");
                        message = gameState.getP1_name() + " has won!";
                        Log.d("Status is updated in displayGameState() : ", status.getText().toString());
                    }
                    showDialog(message);
                } else {
                    if (gameState.getActivePlayer() != null) {
                        if (gameState.getActivePlayer().equals(XPLAYER)) {

                            statusPlayer.setImageResource(R.drawable.x);
                            status.setText("'s Turn - Tap to play");
                            if (!lastPlayer.equals(XPLAYER)) {
                                startTimer();
                            }
                            Log.d("Status is updated in displayGameState() : ", status.getText().toString());
                        }
                        if (gameState.getActivePlayer().equals(OPLAYER)) {
                            statusPlayer.setImageResource(R.drawable.o);
                            status.setText("'s Turn - Tap to play");
                            if (!lastPlayer.equals(OPLAYER)) {
                                startTimer();
                            }
                            Log.d("Status is updated in displayGameState() : ", status.getText().toString());
                        }

                    }
                }
            }
        };

        runOnUiThread(runnable);
    }
    //start timer function
    public void startTimer() {
        cTimer = new CountDownTimer(15000, 1000) {
            public void onTick(long millisUntilFinished) {

                NumberFormat f = new DecimalFormat("00");
                long sec = (millisUntilFinished / 1000) % 60;
                timer.setText(f.format(sec));
            }
            public void onFinish() {

                timer.setText("");
                cancelTimer();
            }
        };
        cTimer.start();
    }
    //cancel timer
    public void cancelTimer() {
        if(cTimer!=null)
        {
            timer.setText("");
            cTimer.cancel();
        }
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
    //
    public void showDialog(String message){
        // Create the object of AlertDialog Builder class
        AlertDialog.Builder builder = new AlertDialog.Builder(TicTacToeActivity.this);
        // Set the message show for the Alert time
        builder.setMessage(message);
        // Set Alert Title
        builder.setTitle("Message");
        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(true);
        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setPositiveButton("REMATCH", new DialogInterface.OnClickListener (){
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                JSONObject op = new JSONObject();
                op.put("action", "rematch");
                op.put("game_id", WebSocketClientSingleton.getGameState().getGame_id());
                JSONArray jsonData = new JSONArray();
                jsonData.add(op);
                TicTacToeActivity.this.ws.send(jsonData.toString());
            }
        });
        builder.setNegativeButton("EXIT", new DialogInterface.OnClickListener (){
            @Override
            public void onClick(DialogInterface dialog, int which){
                byte[] imageInByte = null;
                Bitmap bitmap = ((BitmapDrawable) getDrawable(R.drawable.tictactoe)).getBitmap();

                ByteArrayOutputStream game_img_byte_array = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, game_img_byte_array);
                imageInByte = game_img_byte_array.toByteArray();

                dialog.dismiss();
                Intent intent = new Intent(TicTacToeActivity.this, JoinActivity.class);
                intent.putExtra("picture", imageInByte);
                startActivity(intent);
                TicTacToeActivity.this.ws.removeMessageHandler();
            }
        });
        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();
        // Show the Alert Dialog box
        alertDialog.show();
    }
}