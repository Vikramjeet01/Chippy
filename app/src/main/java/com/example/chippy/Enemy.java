package com.example.chippy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Enemy {

    int xPosition;
    int yPosition;

    Bitmap enemyImage;

    public Enemy(Context context, int x, int y){
        this.enemyImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.player64);
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
        return this.enemyImage;
    }
}
