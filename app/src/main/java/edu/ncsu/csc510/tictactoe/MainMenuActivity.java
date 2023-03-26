package edu.ncsu.csc510.tictactoe;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;

public class MainMenuActivity extends AppCompatActivity {
    Spinner game_selection_spinner_obj;
    EditText username_textbox_obj, password_textbox_obj;
    ArrayAdapter arr_adapter;
    ImageView image_view_obj;
    String gamename;
    Button Login_btn_obj, forget_btn_obj;

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
        Intent switchActivityIntent = new Intent(this, JoinActivity.class);
        Bitmap bitmap = ((BitmapDrawable) image_view_obj.getDrawable()).getBitmap();
        ByteArrayOutputStream game_img_byte_array = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, game_img_byte_array);
        byte[] imageInByte = game_img_byte_array.toByteArray();
        switchActivityIntent.putExtra("picture", imageInByte);
        startActivity(switchActivityIntent);
    }

    private void bindView() {
        image_view_obj = findViewById(R.id.imageview_gamelogo);
        game_selection_spinner_obj = findViewById(R.id.scroll_GameSelection);
        username_textbox_obj = findViewById(R.id.textbox_user_name);
        password_textbox_obj = findViewById(R.id.textbox_password);
        forget_btn_obj = findViewById(R.id.button_forget);
        Login_btn_obj = findViewById(R.id.button_login);

        // Create an ArrayAdapter using the string array and a default spinner layout
        arr_adapter = ArrayAdapter.createFromResource(this,
                R.array.Games, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        game_selection_spinner_obj.setAdapter(arr_adapter);
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

        Login_btn_obj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivities();
            }
        });
    }
}
