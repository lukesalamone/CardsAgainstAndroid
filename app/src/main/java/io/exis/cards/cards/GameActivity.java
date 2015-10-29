package io.exis.cards.cards;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Chronometer;
import android.widget.Button;

/**
 * Created by luke on 10/22/15.
 */
public class GameActivity extends AppCompatActivity {

    public Chronometer chronometer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        RiffleSession riffle = new RiffleSession();

        //find game & join
        riffle.join();

        //set question


        //populate answers
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        // leave game
    }
}
