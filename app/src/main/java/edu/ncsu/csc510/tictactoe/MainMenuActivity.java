package edu.ncsu.csc510.tictactoe;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URI;

public class MainMenuActivity extends AppCompatActivity {
    private WebSocketClientImpl ws;
    Spinner game_selection_spinner_obj;
    EditText username_textbox_obj, password_textbox_obj;
    ArrayAdapter arr_adapter;
    ImageView image_view_obj;
    String gamename;
    Button Login_btn_obj, forget_btn_obj;
    EditText serverAddress ;
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
        if(init_ws()){
            Intent switchActivityIntent = new Intent(this, JoinActivity.class);
            Bitmap bitmap = ((BitmapDrawable) image_view_obj.getDrawable()).getBitmap();
            ByteArrayOutputStream game_img_byte_array = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, game_img_byte_array);
            byte[] imageInByte = game_img_byte_array.toByteArray();
            switchActivityIntent.putExtra("picture", imageInByte);
            startActivity(switchActivityIntent);
            JSONObject action = new JSONObject();
            try {
                action.put("set_player_name", username_textbox_obj.getText().toString());
            } catch (Exception e) {
                Log.d("JSONToObject", "Exception", e);
            }
            JSONArray data = new JSONArray();
            data.add(action);
            this.ws.send(data.toString());
        }
        else{
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
        serverAddress = findViewById(R.id.server_address);
        // Create an ArrayAdapter using the string array and a default spinner layout
        arr_adapter = ArrayAdapter.createFromResource(this,
                R.array.Games, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        game_selection_spinner_obj.setAdapter(arr_adapter);
    }
    class WSException extends Exception
    {
        public WSException(String message)
        {
            super(message);
        }
    }
    boolean init_ws() {
        try {
            boolean succeed = false;
            String url = String.format("ws://%s:8000", serverAddress.getText());
            this.ws = WebSocketClientSingleton.getInstance(new URI(url));
            this.ws.addHeader("game-id", "0000000000");
            succeed = this.ws.connectBlocking();
            if(succeed)
                return true;
            else
                throw new WSException("Socket Fail");
        }
        catch (Exception e) {
            Log.d("init_ws", "Exception", e);
            WebSocketClientSingleton.clearInstance();
        }
        return false;
    }
    private void setListeners() {

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
        Login_btn_obj.setEnabled(false);
        Login_btn_obj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivities();
            }
        });
        username_textbox_obj.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().trim().length()==0){
                    Login_btn_obj.getBackground();
                    Login_btn_obj.setEnabled(false);
                } else {
                    Login_btn_obj.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
    }
}
