package com.example.chippy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameEngine extends SurfaceView implements Runnable {

    final static String TAG="CHIPPY";

    int screenHeight;
    int screenWidth;

    // game state
    boolean gameIsRunning;

    // threading
    Thread gameThread;


    // drawing variables
    SurfaceHolder holder;
    Canvas canvas;
    Paint paintbrush;



    // -----------------------------------
    // GAME SPECIFIC VARIABLES
    // -----------------------------------

    // ----------------------------
    // ## SPRITES
    // ----------------------------

    // represent the TOP LEFT CORNER OF THE GRAPHIC
    Player player;
    Enemy enemy;

    Bitmap background;

    int lives = 5;

    public GameEngine(Context context, int w, int h) {
        super(context);


        this.holder = this.getHolder();
        this.paintbrush = new Paint();

        this.screenWidth = w;
        this.screenHeight = h;


        this.printScreenInfo();


        // @TODO: Add your sprites

        this.player = new Player(getContext(), 100, this.screenHeight/2);
        this.enemy = new Enemy(getContext(), this.screenWidth/2, this.screenHeight/2);

        this.background = BitmapFactory.decodeResource(context.getResources(), R.drawable.b1);
        // dynamically resize the background to fit the device
        this.background = Bitmap.createScaledBitmap(
                this.background,
                this.screenWidth,
                this.screenHeight,
                false
        );

    }

    private void printScreenInfo() {

        Log.d(TAG, "Screen (w, h) = " + this.screenWidth + "," + this.screenHeight);
    }


    @Override
    public void run() {
        while (gameIsRunning == true) {
            this.updatePositions();
            this.redrawSprites();
            this.setFPS();
        }
    }


    public void pauseGame() {
        gameIsRunning = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    public void startGame() {
        gameIsRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }


    int numLoops = 0;

    public void updatePositions(){

        numLoops = numLoops + 1;

        this.enemy.setxPosition(this.enemy.getxPosition()+2);
        this.enemy.setyPosition(this.enemy.getyPosition()+2);
        this.enemy.updateHitbox();

        if(this.enemy.getyPosition()+this.enemy.getImage().getHeight()+140 >= this.screenHeight) {
            this.enemy.setyPosition(0);
            this.enemy.updateHitbox();
        }

        if(this.enemy.getxPosition()+this.enemy.getImage().getWidth() >= this.screenWidth) {
            this.enemy.setxPosition((this.screenWidth / 4));
            this.enemy.updateHitbox();
        }

        if (numLoops % 5  == 0) {
            this.player.spawnBullet();
            this.enemy.spawnBullet();
        }


        int BULLET_SPEED = 20;
        for (int i = 0; i < this.player.getBullets().size();i++) {
            Rect bullet = this.player.getBullets().get(i);
            bullet.left = bullet.left + BULLET_SPEED;
            bullet.right = bullet.right + BULLET_SPEED;
        }

        for (int i = 0; i < this.player.getBullets().size();i++) {
            Rect bullet = this.player.getBullets().get(i);

            // For each bullet, check if teh bullet touched the wall
            if (bullet.right >= this.screenWidth) {
                this.player.getBullets().remove(bullet);
            }
        }

        for (int i = 0; i < this.enemy.getBullets().size();i++) {
            Rect bullet = this.enemy.getBullets().get(i);
            bullet.left = bullet.left - BULLET_SPEED;
            bullet.right = bullet.right - BULLET_SPEED;
        }

        for (int i = 0; i < this.enemy.getBullets().size();i++) {
            Rect bullet = this.enemy.getBullets().get(i);

            // For each bullet, check if teh bullet touched the wall
            if (bullet.left < 0) {
                this.enemy.getBullets().remove(bullet);
            }
        }

        for (int i = 0; i < this.player.getBullets().size();i++) {
            Rect bullet = this.player.getBullets().get(i);

            if (this.enemy.getHitbox().intersect(bullet)) {
                this.enemy.setxPosition(this.screenWidth / 2);
                this.enemy.setyPosition(this.screenHeight / 2);
                this.enemy.updateHitbox();
                //lives = lives - 1;
            }

        }

        for (int i = 0; i < this.enemy.getBullets().size();i++) {
            Rect bullet = this.enemy.getBullets().get(i);

            if (this.player.getHitbox().intersect(bullet)) {
                this.player.setxPosition(100);
                this.player.setyPosition(this.screenHeight / 2);
                this.player.updateHitbox();
                lives = lives - 1;

                if(lives < 0){
                    this.pauseGame();
                }
            }

        }

    }

    public void redrawSprites(){

        if (this.holder.getSurface().isValid()) {
            this.canvas = this.holder.lockCanvas();

            //----------------

            // configure the drawing tools
            this.canvas.drawColor(Color.argb(255,255,255,255));
            paintbrush.setColor(Color.WHITE);


            // DRAW THE PLAYER HITBOX
            // ------------------------
            // 1. change the paintbrush settings so we can see the hitbox
            paintbrush.setColor(Color.BLUE);
            paintbrush.setStyle(Paint.Style.STROKE);
            paintbrush.setStrokeWidth(5);

            canvas.drawBitmap(this.background,
                    0,
                    0,
                    paintbrush);


            // draw player graphic on screen

            canvas.drawBitmap(player.getImage(), player.getxPosition(), player.getyPosition(), paintbrush);
            // draw the player's hitbox
            canvas.drawRect(player.getHitbox(), paintbrush);

            // draw the enemy graphic on the screen
            canvas.drawBitmap(enemy.getImage(), enemy.getxPosition(), enemy.getyPosition(), paintbrush);
            // 2. draw the enemy's hitbox
            canvas.drawRect(enemy.getHitbox(), paintbrush);

            paintbrush.setColor(Color.BLACK);
            paintbrush.setStyle(Paint.Style.FILL);
            for (int i = 0; i < this.player.getBullets().size(); i++) {
                Rect bullet = this.player.getBullets().get(i);
                canvas.drawRect(bullet, paintbrush);
            }

            paintbrush.setColor(Color.RED);
            paintbrush.setStyle(Paint.Style.FILL);
            for (int i = 0; i < this.enemy.getBullets().size(); i++) {
                Rect bullet = this.enemy.getBullets().get(i);
                canvas.drawRect(bullet, paintbrush);
            }

            paintbrush.setColor(Color.WHITE);
            paintbrush.setTextSize(60);
            canvas.drawText("Lives remaining: " + lives,
                    500,
                    100,
                    paintbrush
            );
            //----------------
            this.holder.unlockCanvasAndPost(canvas);
        }

    }

    public void setFPS() {
        try {
            gameThread.sleep(100);
        }
        catch (Exception e) {

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_UP:


                double e = (int) event.getX() - this.player.getxPosition();
                double f = (int)event.getY() - this.player.getyPosition();

                // d = sqrt(a^2 + b^2)

                double d1 = Math.sqrt((e * e) + (f * f));

                // 2. calculate xn and yn constants
                // (amount of x to move, amount of y to move)
                double xn1 = (e / d1);
                double yn1 = (f / d1);

                // 3. calculate new (x,y) coordinates
                int newX = this.player.getxPosition() + (int) (xn1 * 200);
                int newY = this.player.getyPosition() + (int) (yn1 * 200);
                this.player.setxPosition(newX);
                this.player.setyPosition(newY);

                // 4. update the bullet hitbox position
                this.player.updateHitbox();





                break;

            case MotionEvent.ACTION_DOWN:

                break;

        }

        return true;
    }


}
