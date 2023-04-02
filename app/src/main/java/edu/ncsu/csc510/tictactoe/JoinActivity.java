package edu.ncsu.csc510.tictactoe;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayOutputStream;
import java.util.Date;

public class JoinActivity extends AppCompatActivity {
    ImageView image_view_obj;
    RadioGroup radiogroup_obj;
    EditText gameid_textbox_obj;
    Button submit_btn_obj;
    String gameId;
    private WebSocketClientImpl ws;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        bindView();
        setListener();
        Bundle extras = getIntent().getExtras();
        byte[] byteArray = extras.getByteArray("picture");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        image_view_obj.setImageBitmap(bmp);
        init_ws();
    }

    boolean init_ws() {
        try {
            this.ws = WebSocketClientSingleton.getInstance();
            this.ws.addMessageHandler(this::messageHandler);
        } catch (Exception e) {
            Log.d("join_activity_init_ws", "Exception", e);
        }
        return false;
    }

    private void bindView() {
        radiogroup_obj = findViewById(R.id.radiogroup);
        gameid_textbox_obj = findViewById(R.id.textbox_game_id);
        submit_btn_obj = findViewById(R.id.button_submit);
        image_view_obj = findViewById(R.id.imageview_gamelogo);
        //hide the gameid initially
        gameid_textbox_obj.setVisibility(View.INVISIBLE);
    }

    private void setListener() {
        radiogroup_obj.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checkFields();
            }
        });
        //Disable the button by default
        submit_btn_obj.setEnabled(false);
        submit_btn_obj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String gameId = gameid_textbox_obj.getText().toString();
                switchActivities(gameId);
            }
        });
        TextWatcher mTextboxWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // check Fields For Values
                checkFields();
            }
        };
        gameid_textbox_obj.addTextChangedListener(mTextboxWatcher);
    }

    void checkFields() {
        int radio_button_selected = radiogroup_obj.getCheckedRadioButtonId();
        if (radio_button_selected == R.id.radio_new_game) {
            gameid_textbox_obj.setVisibility(View.INVISIBLE);
            submit_btn_obj.setEnabled(true);
            return;
        } else {
            gameid_textbox_obj.setVisibility(View.VISIBLE);
            String s1 = gameid_textbox_obj.getText().toString();
            if (s1.equals("")) {
                submit_btn_obj.setEnabled(false);
            } else {
                submit_btn_obj.setEnabled(true);
            }
        }
    }

    private void switchActivities(String gameId) {
        User user = WebSocketClientSingleton.getUser();
        JSONObject request = new JSONObject();
        try {
            switch (radiogroup_obj.getCheckedRadioButtonId()) {
                case R.id.radio_new_game:
                    request.put("action", "create_game");
                    request.put("player_id", user.getPlayer_id());
                    user.setMode("create");
                    break;
                case R.id.radio_join_game:
                    request.put("action", "join_game");
                    request.put("game_id", gameid_textbox_obj.getText().toString());
                    request.put("player_id", user.getPlayer_id());
                    user.setMode("join");
                    break;
                case R.id.radio_spectator_game:
                    request.put("action", "spectate_game");
                    request.put("game_id", gameid_textbox_obj.getText().toString());
                    request.put("player_id", user.getPlayer_id());
                    user.setMode("spectate");
                    break;
                default:
                    // code block
            }
        } catch (Exception e) {
            Log.d("JSONToObject", "Exception", e);
        }
        WebSocketClientSingleton.setUser(user);
        JSONArray data = new JSONArray();
        data.add(request);
        this.ws.send(data.toString());


    }


    // This is passed to the websocket to use as the messageHandler for this page
    void messageHandler(String message) {
        // print message to log for testing purposes
        Log.d("JoinActivity", message);
        User user = null;
        GameState gameState = null;
        try {
            JSONObject obj = (JSONObject) new JSONParser().parse(message);

            if (obj.containsKey("description")) {
                if (obj.get("description").toString().equals("fail")) {
                    create_fail_dialogue();
                    return;
                }
            }
        }
        catch (ParseException e) {
            Log.d("messageHandler: ", "ParseException: ", e);
        }

        try {
            Intent switchActivityIntent;
            switch (radiogroup_obj.getCheckedRadioButtonId()) {
                case R.id.radio_new_game:
                    user = WebSocketClientSingleton.getUser();
                    gameState = JsonUtility.jsonToGameState(message);
                    WebSocketClientSingleton.setGameState(gameState);
                    switchActivityIntent = new Intent(this, WaitingActivity.class);
                    startActivity(switchActivityIntent);
                    this.ws.removeMessageHandler();
                    break;
                case R.id.radio_join_game:
                    user = WebSocketClientSingleton.getUser();
                    gameState = JsonUtility.jsonToGameState(message);
                    WebSocketClientSingleton.setGameState(gameState);
                    GameState gameState_ = JsonUtility.jsonToGameState(message);
                    switchActivityIntent = new Intent(this, TicTacToeActivity.class);
                    startActivity(switchActivityIntent);
                    this.ws.removeMessageHandler();
                    break;
                case R.id.radio_spectator_game:
                    switchActivityIntent = new Intent(this, TicTacToeActivity.class);
                    startActivity(switchActivityIntent);
                    this.ws.removeMessageHandler();
                    break;
                default:
                    // code block
            }
        } catch (Exception e) {
            create_fail_dialogue();
        }
    }
    void create_fail_dialogue(){
        // Create the object of AlertDialog Builder class
        AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);

        // Set the message show for the Alert time
        builder.setMessage("Check Game ID");

        // Set Alert Title
        builder.setTitle("Game ID error");

        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(false);

        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setPositiveButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> {
            // When the user click yes button then app will close
        });

        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();
        // Show the Alert Dialog box
        alertDialog.show();
    }
}