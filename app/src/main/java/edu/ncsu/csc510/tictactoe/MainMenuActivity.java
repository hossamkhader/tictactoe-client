package edu.ncsu.csc510.tictactoe;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.concurrent.TimeUnit;

public class MainMenuActivity extends AppCompatActivity {
    private WebSocketClientImpl ws;
    Spinner game_selection_spinner_obj;
    EditText username_textbox_obj, password_textbox_obj;
    ArrayAdapter arr_adapter;
    ImageView image_view_obj;
    String gamename;
    Button Login_btn_obj, forget_btn_obj;
    EditText server_address_textbox_obj;
    byte[] imageInByte = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        bindView();
        setListeners();
    }

    private void changeimage() {
        gamename = game_selection_spinner_obj.getSelectedItem().toString();
        if (gamename.equals("Tic Tac Toe")) {
            image_view_obj.setImageResource(R.drawable.tictactoe);
        } else if (gamename.equals("Future Games")) {
            image_view_obj.setImageResource(R.drawable.future_game);
        }
    }

    private void switchActivities() {
        if (init_ws()) {

            Bitmap bitmap = ((BitmapDrawable) image_view_obj.getDrawable()).getBitmap();
            ByteArrayOutputStream game_img_byte_array = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, game_img_byte_array);
            imageInByte = game_img_byte_array.toByteArray();

            JSONObject action = new JSONObject();
            try {
                action.put("action", "set_player_name");
                action.put("username", username_textbox_obj.getText().toString());
            } catch (Exception e) {
                Log.d("JSONToObject", "Exception", e);
            }
            JSONArray data = new JSONArray();
            data.add(action);
//            switchActivityIntent.putExtra("ws_message", data.toString());
            this.ws.send(data.toString());
        } else {
            // Create the object of AlertDialog Builder class
            AlertDialog.Builder builder = new AlertDialog.Builder(MainMenuActivity.this);

            // Set the message show for the Alert time
            builder.setMessage("Check IP Address ?");

            // Set Alert Title
            builder.setTitle("Check Server!");

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

    private void bindView() {
        image_view_obj = findViewById(R.id.imageview_gamelogo);
        game_selection_spinner_obj = findViewById(R.id.scroll_GameSelection);
        username_textbox_obj = findViewById(R.id.textbox_user_name);
        password_textbox_obj = findViewById(R.id.textbox_password);
        forget_btn_obj = findViewById(R.id.button_forget);
        Login_btn_obj = findViewById(R.id.button_login);
        server_address_textbox_obj = findViewById(R.id.server_address);
        // Create an ArrayAdapter using the string array and a default spinner layout
        arr_adapter = ArrayAdapter.createFromResource(this,
                R.array.Games, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        game_selection_spinner_obj.setAdapter(arr_adapter);
    }

    class WSException extends Exception {
        public WSException(String message) {
            super(message);
        }
    }

    boolean init_ws() {
        try {
            boolean succeed = false;

            String url = String.format("ws://%s:8000", server_address_textbox_obj.getText());
            this.ws = WebSocketClientSingleton.getInstance(URI.create(url));
            succeed = this.ws.connectBlocking(2L, TimeUnit.SECONDS);
            if (succeed) {
                this.ws.removeMessageHandler();
                this.ws.addMessageHandler(this::messageHandler);
                return true;
            } else {
                throw new WSException("Socket Fail");
            }
        } catch (Exception e) {
            Log.d("init_ws", "Exception", e);
            WebSocketClientSingleton.clearInstance();
        }
        return false;
    }

    private void setListeners() {
        //create spinner object listener so user can select game properly
        game_selection_spinner_obj.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedItem = game_selection_spinner_obj.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), selectedItem, Toast.LENGTH_LONG).show();
                changeimage();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        // Login button should disabled by default
        Login_btn_obj.setEnabled(false);
        Login_btn_obj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switchActivities();
            }
        });
        //create generic text box watch which can be applied later on each text box field
        TextWatcher mTextboxWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // check Fields For Empty Values
                checkFieldsForEmptyValues();
            }
        };
        //apply the listener to the each text field so it disables the login button properly
        server_address_textbox_obj.addTextChangedListener(mTextboxWatcher);
        username_textbox_obj.addTextChangedListener(mTextboxWatcher);

    }

    //This function checks if username and server address field is filled before login button is
    // enabled
    void checkFieldsForEmptyValues() {

        String s1 = username_textbox_obj.getText().toString();
        String s2 = server_address_textbox_obj.getText().toString();

        if (s1.equals("") || s2.equals("")) {
            Login_btn_obj.setEnabled(false);
        } else {
            Login_btn_obj.setEnabled(true);
        }
    }

    // This is passed to the websocket to use as the messageHandler for this page
    void messageHandler(String message) {
        // print message to log for testing purposes
        Log.d("mainMSG", message);
        String action, description;
        action = description = "";
        User user = WebSocketClientSingleton.getUser();
        try {
            JSONObject obj = (JSONObject) new JSONParser().parse(message);
            action = obj.get("action").toString();
            description = obj.get("description").toString();
            user.setUsername(obj.get("username").toString());
            user.setPlayer_id(obj.get("player_id").toString());
        } catch (ParseException e) {
            Log.d("messageHandler: ", "ParseException: ", e);
        }

        if (description.equals("success")) {
            Intent switchActivityIntent = new Intent(this, JoinActivity.class);
            switchActivityIntent.putExtra("picture", imageInByte);
            startActivity(switchActivityIntent);
        } else {
            //Alert Failed to log in
        }
    }
}
