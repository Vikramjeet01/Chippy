package com.example.chippy;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

public class GameEngine extends SurfaceView implements Runnable {

    final static String TAG="CHIPPY";

    int screenHeight;
    int screenWidth;

    boolean gameIsRunning;

    Thread gameThread;

    SurfaceHolder holder;
    Canvas canvas;
    Paint paintbrush;

    Player player;
    Enemy enemy;

    public GameEngine(Context context, int w, int h) {
        super(context);


        this.holder = this.getHolder();
        this.paintbrush = new Paint();

        this.screenWidth = w;
        this.screenHeight = h;

        for (int i = 0; i < 15; i++) {
            Random r = new Random();
            int randomXPOS = r.nextInt(this.screenWidth) + 1;
            int randomYPOS = r.nextInt(this.screenHeight) + 1;
            //Square b = new Square(getContext(), randomXPOS, randomYPOS, SQUARE_WIDTH);
        }

        //this.bullet = new Square(context, 100, this.screenHeight - 400, SQUARE_WIDTH);

        this.spawnPlayer();
        this.spawnEnemy();
    }

    private void spawnPlayer() {
        //@TODO: Start the player at the left side of screen

        player = new Player(this.getContext(), 50, this.screenHeight - 200);
    }

    private void spawnEnemy(){
        enemy = new Enemy(this.getContext(), screenWidth / 2, screenHeight / 2);
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

    }

    public void redrawSprites(){

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

                //for(int i = 0; i < bullets.size(); i++){
                    //Square bul = bullets.get(i);

                    //double e = (int) event.getX() - this.bullet.getxPosition();
                    //double f = (int)event.getY() - this.bullet.getyPosition();

                    // d = sqrt(a^2 + b^2)

                    //double d1 = Math.sqrt((e * e) + (f * f));

                    // 2. calculate xn and yn constants
                    // (amount of x to move, amount of y to move)
                    //double xn1 = (e / d1);
                    //double yn1 = (f / d1);

                    // 3. calculate new (x,y) coordinates
                    //int newX = this.bullet.getxPosition() + (int) (xn1 * 100);
                    //int newY = this.bullet.getyPosition() + (int) (yn1 * 100);
                    //this.bullet.setxPosition(newX);
                    //this.bullet.setyPosition(newY);

                    // 4. update the bullet hitbox position
                    //this.bullet.updateHitbox();


                //}



                //break;

            //case MotionEvent.ACTION_DOWN:

               // break;

        }

        return true;
    }

}
