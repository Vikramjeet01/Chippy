package com.example.chippy;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    GameEngine chippy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        chippy = new GameEngine(this, size.x, size.y);

        setContentView(chippy);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //Pause the game

        chippy.pauseGame();
    }

    @Override
    protected void onResume() {
        super.onResume();


        chippy.startGame();
    }

}
