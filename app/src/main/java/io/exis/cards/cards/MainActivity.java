package io.exis.cards.cards;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

public class MainActivity extends AppCompatActivity {

    static final String CONTENT_RATING = "contentRating";
    public boolean adult = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            adult = savedInstanceState.getBoolean(CONTENT_RATING);
        }

        this.setPoints();
    }

    public void startGame(View view) {
        Intent intent = new Intent(view.getContext(), Game.class);
        view.getContext().startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
    }

    public void onRadioButtonClicked(View view){
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.radio_pg13:
                if (checked) {
                    adult = false;
                    break;
                }
            case R.id.radio_adult:
                if (checked) {
                    adult = true;
                    break;
                }
        }
    }//end onRadioButtonClicked method

    /*
     * @TODO
     * Set points to previously saved value
     *
     */
    private void setPoints(){
        // 1. Restore from network

        // 2. Display correct number in activity
    }

}
