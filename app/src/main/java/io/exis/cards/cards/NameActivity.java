package io.exis.cards.cards;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Allows user to change screen name
 *
 * Created by luke on 3/30/16.
 */
public class NameActivity extends Activity {

    private EditText nameField;
    private TextView border;
    private String screenName;
    private RelativeLayout background;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        preferences = getPreferences(MODE_PRIVATE);

        background = (RelativeLayout) findViewById(R.id.background);
        nameField = (EditText) findViewById(R.id.name_field);
        nameField.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        border = (TextView) findViewById(R.id.border);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        nameField.setOnClickListener(v -> {
            Log.i("name activity", "onClickListener fired");
            nameField.setText("");
            border.setBackgroundColor(Color.parseColor("#00a2ff"));
        });

        nameField.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                Log.i("name activity", "onFocusChangeListener fired");
                nameField.setText("");
                border.setBackgroundColor(Color.parseColor("#00a2ff"));
            }
        });

        background.setOnClickListener(v -> {
            nameField.clearFocus();
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            if (!nameField.getText().toString().equals("") &&
                    !nameField.getText().toString().equals("Enter Screen Name")) {
                screenName = nameField.getText().toString();
            }
            MainActivity.setScreenName(screenName);
            border.setBackgroundColor(Color.parseColor("#ffffff"));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle bundle = getIntent().getExtras();
        screenName = bundle.getString("key_screen_name", "");

        screenName = preferences.getString("screenName", "");

        if(!screenName.equals("")){
            nameField.setText(screenName);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(!nameField.getText().toString().equals("") &&
                !nameField.getText().toString().equals("Enter Screen Name")) {
            screenName = nameField.getText().toString();
        }
        Log.i("onPause", "saving screen name " + screenName);
        MainActivity.setScreenName(screenName);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("screenName", screenName);
        editor.apply();

        Log.i("onPause", "Screen Name saved as " + preferences.getString("screenName", ""));
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
