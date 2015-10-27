package io.exis.cards.cards;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

public class MainActivity extends AppCompatActivity {

    public boolean adult = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

}
