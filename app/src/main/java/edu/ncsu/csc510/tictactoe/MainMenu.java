package edu.ncsu.csc510.tictactoe;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenu extends AppCompatActivity {
    Spinner game_selection_spinner_obj;
    EditText gameid_textbox_obj, username_textbox_obj, password_textbox_obj;
    RadioGroup radiogroup_obj;
    ArrayAdapter arr_adapter;
    ImageView image_view_obj;
    String gamename;
    Button Login_btn_obj, forget_btn_obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        bindView();
        gameid_textbox_obj.setVisibility(View.INVISIBLE);
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
        Intent switchActivityIntent = new Intent(this, TicTacToe.class);
        startActivity(switchActivityIntent);
    }

    private void bindView() {
        image_view_obj = (ImageView) findViewById(R.id.imageview_gamelogo);
        game_selection_spinner_obj = (Spinner) findViewById(R.id.scroll_GameSelection);
        // Create an ArrayAdapter using the string array and a default spinner layout
        arr_adapter = ArrayAdapter.createFromResource(this,
                R.array.Games, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        game_selection_spinner_obj.setAdapter(arr_adapter);
        radiogroup_obj = (RadioGroup) findViewById(R.id.radiogroup);
        username_textbox_obj = (EditText) findViewById(R.id.textbox_user_name);
        password_textbox_obj = (EditText) findViewById(R.id.textbox_password);
        gameid_textbox_obj = (EditText) findViewById(R.id.textbox_game_id);
        forget_btn_obj = (Button) findViewById(R.id.button_forget);
        Login_btn_obj = (Button) findViewById(R.id.button_login);
    }

    private void setListeners() {
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

        Login_btn_obj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivities();
            }
        });
    }
}
