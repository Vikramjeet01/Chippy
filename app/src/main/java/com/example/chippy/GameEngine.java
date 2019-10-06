package com.example.chippy;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    Square bullet;
    int b_WIDTH = 25;

    List<Square> bullets = new ArrayList<Square>();


    public GameEngine(Context context, int w, int h) {
        super(context);


        this.holder = this.getHolder();
        this.paintbrush = new Paint();

        this.screenWidth = w;
        this.screenHeight = h;


        this.printScreenInfo();


        // @TODO: Add your sprites

        for(int i =0; i < 15; i++){
            Random r = new Random();
            int randomXPOS = r.nextInt(this.screenWidth) + 1;
            int randomYPOS = r.nextInt(this.screenHeight) + 1;
            Square b = new Square(getContext(), randomXPOS, randomYPOS, b_WIDTH );
        }

        this.bullet = new Square(context, 100, this.screenHeight - 400, b_WIDTH);

        // put initial starting postion of enemy
        this.player = new Player(getContext(), 100, this.screenHeight/2);
        this.enemy = new Enemy(getContext(), this.screenWidth/2, this.screenHeight/2);
    }

    private void printScreenInfo() {

        Log.d(TAG, "Screen (w, h) = " + this.screenWidth + "," + this.screenHeight);
    }

    private void spawnPlayer() {
        //@TODO: Start the player at the left side of screen
    }
    private void spawnEnemyShips() {
        Random random = new Random();

        //@TODO: Place the enemies in a random location

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

    public void updatePositions(){

        this.enemy.setxPosition(this.enemy.getxPosition()+5);
        this.enemy.setyPosition(this.enemy.getyPosition()+5);
        this.enemy.updateHitbox();

        if(this.enemy.getyPosition()+this.enemy.getImage().getHeight()+140 >= this.screenHeight) {
            this.enemy.setyPosition(0);
            this.enemy.updateHitbox();
        }

        if(this.enemy.getxPosition()+this.enemy.getImage().getWidth() >= this.screenWidth) {
            this.enemy.setxPosition(0);
            this.enemy.updateHitbox();
        }

        //Bullet moving towards enemy


            double e = this.enemy.getxPosition() - this.bullet.getxPosition();
            double f = this.enemy.getyPosition() - this.bullet.getyPosition();

            // d = sqrt(a^2 + b^2)

            double d1 = Math.sqrt((e * e) + (f * f));

            // 2. calculate xn and yn constants
            // (amount of x to move, amount of y to move)
            double xn1 = (e / d1);
            double yn1 = (f / d1);

            // 3. calculate new (x,y) coordinates
            int newX = this.bullet.getxPosition() + (int) (xn1 * 10);
            int newY = this.bullet.getyPosition() + (int) (yn1 * 10);
            this.bullet.setxPosition(newX);
            this.bullet.setyPosition(newY);

            // 4. update the bullet hitbox position
            this.bullet.updateHitbox();






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
            canvas.drawRect(
                    this.bullet.getxPosition(),
                    this.bullet.getyPosition(),
                    this.bullet.getxPosition() + this.bullet.getWidth(),
                    this.bullet.getyPosition() + this.bullet.getWidth(),
                    paintbrush
            );
            canvas.drawRect(
                    this.bullet.getHitbox(),
                    paintbrush
            );

            for (int i = 0; i < bullets.size(); i++){
                Square b = bullets.get(i);
                canvas.drawRect(b.getxPosition(), b.getyPosition(), b.getxPosition() + b_WIDTH, b.getxPosition() + b_WIDTH, paintbrush);
            }

            //----------------
            this.holder.unlockCanvasAndPost(canvas);
        }

    }

    public void setFPS() {
        try {
            gameThread.sleep(50);
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
