package io.exis.cards.cards;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

public class MainActivity extends Activity {

    static final String CONTENT_RATING = "contentRating";
    public static boolean adult = false;
    //public RiffleSession riffle = new RiffleSession();

    View view;
    Button gameButton = (Button)view.findViewById(R.id.button);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState.getBoolean(CONTENT_RATING)) {
            adult = savedInstanceState.getBoolean(CONTENT_RATING);
        } else {
            gameButton.setEnabled(false);
        }

        setPoints();
    }

    public void startGame(View view) {
        Intent intent = new Intent(view.getContext(), GameActivity.class);
        view.getContext().startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
    }

    public void onRadioButtonClicked(View view){
        gameButton.setEnabled(true);
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
