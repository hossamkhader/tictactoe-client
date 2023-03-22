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
    Spinner spinner;
    ArrayAdapter adapter;
    ImageView imageView;
    String gamename;
    Button switchToSecondActivity;
    String[] games = {"Tic Tac Toe", "Future Games", "..."};
    AutoCompleteTextView autoCompleteTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        imageView = (ImageView)findViewById(R.id.gamelogo);
        spinner = (Spinner) findViewById(R.id.GameSelection);
// Create an ArrayAdapter using the string array and a default spinner layout
        adapter = ArrayAdapter.createFromResource(this,
                R.array.Games, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        final EditText gameid_textbox = (EditText) findViewById(R.id.textbox_game_id);
        gameid_textbox.setVisibility(View.INVISIBLE);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radio_join_game || checkedId == R.id.radio_spectator_game){
                    gameid_textbox.setVisibility(View.VISIBLE);
                } else {
                    gameid_textbox.setVisibility(View.INVISIBLE);
                }
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedItem = spinner.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), selectedItem, Toast.LENGTH_LONG).show();
                changeimage();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView){}
        });
        switchToSecondActivity = findViewById(R.id.button_login);
        switchToSecondActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivities();
            }
        });
    }
    private void changeimage() {
        gamename = spinner.getSelectedItem().toString();
        if (gamename.equals("Tic Tac Toe")){
            imageView.setImageResource(R.drawable.tictactoe);
        } else if (gamename.equals("Future Games")) {
            imageView.setImageResource(R.drawable.future_game);
        }
    }
    private void switchActivities() {
        Intent switchActivityIntent = new Intent(this, TicTacToe.class);
        startActivity(switchActivityIntent);
    }
}
