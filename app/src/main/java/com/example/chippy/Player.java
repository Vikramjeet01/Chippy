package com.example.chippy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Player {

    int xPosition;
    int yPosition;

    Bitmap playerImage;

    public Player(Context context, int x, int y) {
        // 1. set up the initial position of the Enemy
        this.playerImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.player64);
        this.xPosition = x;
        this.yPosition = y;

    }

    public void setXPosition(int x) {
        this.xPosition = x;
    }
    public void setYPosition(int y) {
        this.yPosition = y;
    }
    public int getXPosition() {
        return this.xPosition;
    }
    public int getYPosition() {
        return this.yPosition;
    }

    public Bitmap getBitmap() {
        return this.playerImage;
    }
}
