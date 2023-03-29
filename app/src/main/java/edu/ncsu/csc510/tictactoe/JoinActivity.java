package edu.ncsu.csc510.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.RadioGroup;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.ByteArrayOutputStream;

public class JoinActivity extends AppCompatActivity {
    ImageView image_view_obj;
    RadioGroup radiogroup_obj;
    EditText gameid_textbox_obj;
    Button submit_btn_obj;

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
            this.ws.removeMessageHandler();
            this.ws.addMessageHandler(this::messageHandler);
//            testMessageHandler();
        }
        catch (Exception e) {
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
    private void setListener(){
        radiogroup_obj.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_join_game || checkedId == R.id.radio_spectator_game) {
                    gameid_textbox_obj.setVisibility(View.VISIBLE);
                } else {
                    gameid_textbox_obj.setVisibility(View.INVISIBLE);
                }
            }
        });
        submit_btn_obj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivities();
            }
        });
    }
    private void switchActivities() {
        Intent switchActivityIntent = new Intent(this, WaitingActivity.class);
        startActivity(switchActivityIntent);
    }

    // This is passed to the websocket to use as the messageHandler for this page
    void messageHandler(String message) {
        // print message to log for testing purposes
        Log.d("joinMSG", message);
    }

    // This method was just used to repeat the set username action on this page
    // and ensure that the websocket was correctly using this page's messageHandler now
//    void testMessageHandler() {
//        JSONObject action = new JSONObject();
//        try {
//            action.put("action", "set_player_name");
//            action.put("username", "TEST_USERNAME");
//        } catch (Exception e) {
//            Log.d("JSONToObject", "Exception", e);
//        }
//        JSONArray data = new JSONArray();
//        data.add(action);
//        this.ws.send(data.toString());
//    }

}