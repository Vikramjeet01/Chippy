package com.example.chippy;

import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.Display;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    GameEngine chippy;
    private SoundPool sounds;
    private int sExplosion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        sounds = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        sExplosion = sounds.load(this, R.raw.sound, 1);
        sounds.play(sExplosion, 1, 1, 1,3,1);

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
