package com.example.chippy;

import android.content.Context;
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

    Context context;
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
    Bitmap restart;

    Square line1;
    Square line2;
    Square line5;

    int lives = 5;
    int eHealth = 50;
    int BULLET_SPEED = 20;
    int numLoops = 0;
    boolean lineMovingLeft = true;
    boolean obstacleMovingDown = true;


    public GameEngine(Context context, int w, int h) {
        super(context);

        this.context = context;
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

        this.restart = BitmapFactory.decodeResource(context.getResources(), R.drawable.restart);

        this.line1 = new Square(context, 1500, 0, 60, 400);
        this.line2 = new Square(context, 1500, 600, 60, 300);
        this.line5 = new Square(context, 600, 50, 600, 60);
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

    public void restartGame(){


        // @TODO: Add your sprites

        this.player = new Player(getContext(), 100, this.screenHeight/2);
        this.enemy = new Enemy(getContext(), this.screenWidth/2, this.screenHeight/2);

        this.restart = BitmapFactory.decodeResource(context.getResources(), R.drawable.restart);

        this.line1 = new Square(context, 1500, 0, 40, 500);
        this.line2 = new Square(context, 1500, 600, 40, 300);
        this.line5 = new Square(context, 600, 50, 600, 40);

        lives = 5;

        numLoops = 0;
        gameIsRunning = false;

        startGame();
    }

    public void updatePositions(){

        numLoops = numLoops + 1;

        //obstacle 1 moving
        if (lineMovingLeft == true) {
            this.line1.setxPosition(this.line1.getxPosition() - 5);
            this.line2.setxPosition(this.line2.getxPosition() - 5);        }
        else {
            this.line1.setxPosition(this.line1.getxPosition() + 5);
            this.line2.setxPosition(this.line2.getxPosition() + 5);        }
        this.line1.updateHitbox();
        this.line2.updateHitbox();

        if(this.line1.getxPosition() < 0){
            lineMovingLeft = false;
        }

        if(this.line2.getxPosition() < 0){
            lineMovingLeft = false;
        }


        //obstacle 3 moving
        if(obstacleMovingDown == true) {
            this.line5.setyPosition(this.line5.getyPosition() + 5);
        }
        else{
            this.line5.setyPosition(this.line5.getyPosition() - 5);
        }
        this.line5.updateHitbox();

        if(this.line5.getyPosition() >= this.screenHeight - 190){
            obstacleMovingDown = false;
        }

        //enemy moving
        this.enemy.setxPosition(this.enemy.getxPosition()+2);
        this.enemy.setyPosition(this.enemy.getyPosition()+2);
        this.enemy.updateHitbox();

        if(this.enemy.getyPosition()+this.enemy.getImage().getHeight() +190 >= this.screenHeight) {
            this.enemy.setyPosition(0);
            this.enemy.updateHitbox();
        }

        if(this.enemy.getxPosition()+this.enemy.getImage().getWidth() >= this.screenWidth) {
            this.enemy.setxPosition((this.screenWidth / 4));
            this.enemy.updateHitbox();
        }


        //bullets added
        if (numLoops % 5  == 0) {
            this.player.spawnBullet();
            this.enemy.spawnBullet();
        }



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
                /*this.enemy.setxPosition(this.enemy.getxPosition()+2);
                this.enemy.setyPosition(this.enemy.getyPosition()+2);
                this.enemy.updateHitbox();*/
                eHealth = eHealth - 1;

                if(eHealth < 0){
                    this.pauseGame();
                }
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

        //player colliding with obstacles
        if(player.getHitbox().intersect(line1.getHitbox())){
            lives = lives - 1;
            if(lives < 0){
                this.pauseGame();
            }
        }

        if(player.getHitbox().intersect(line2.getHitbox())){
            lives = lives - 1;
            if(lives < 0){
                this.pauseGame();
            }
        }


        if(player.getHitbox().intersect(line5.getHitbox())){
            lives = lives - 1;
            if(lives < 0){
                this.pauseGame();
            }
        }

    }

    public void redrawSprites(){

        if (this.holder.getSurface().isValid()) {
            this.canvas = this.holder.lockCanvas();

            // configure the drawing tools
            this.canvas.drawColor(Color.argb(255,255,255,255));
            paintbrush.setColor(Color.WHITE);

            // DRAW THE BACKGROUND
                      // -----------------------------
                               canvas.drawBitmap(this.background,
                                              0,
                                              0,
                                            paintbrush);


            // DRAW THE PLAYER HITBOX
            // ------------------------
            // 1. change the paintbrush settings so we can see the hitbox

            paintbrush.setColor(Color.BLUE);
            paintbrush.setStyle(Paint.Style.FILL);
            paintbrush.setStrokeWidth(5);
            canvas.drawRect(
                    this.line1.getxPosition(),
                    this.line1.getyPosition(),
                    this.line1.getxPosition() + this.line1.getWidth(),
                    this.line1.getyPosition() + this.line1.getHeight(),
                    paintbrush
            );
            canvas.drawRect(
                    this.line1.getHitbox(),
                    paintbrush
            );

            canvas.drawRect(
                    this.line2.getxPosition(),
                    this.line2.getyPosition(),
                    this.line2.getxPosition() + this.line2.getWidth(),
                    this.line2.getyPosition() + this.line2.getHeight(),
                    paintbrush
            );
            canvas.drawRect(
                    this.line2.getHitbox(),
                    paintbrush
            );


            canvas.drawRect(
                    this.line5.getxPosition(),
                    this.line5.getyPosition(),
                    this.line5.getxPosition() + this.line5.getWidth(),
                    this.line5.getyPosition() + this.line5.getHeight(),
                    paintbrush
            );
            canvas.drawRect(
                    this.line5.getHitbox(),
                    paintbrush
            );


            // draw player graphic on screen
            paintbrush.setColor(Color.BLUE);
            paintbrush.setStyle(Paint.Style.STROKE);
            paintbrush.setStrokeWidth(5);

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

            //restart button
            paintbrush.setTextSize(100);
            if(lives < 1) {
                canvas.drawBitmap(this.restart, 200, 50, paintbrush);
                canvas.drawText("YOU LOST ",
                        this.screenWidth / 2,
                        this.screenHeight / 2,
                        paintbrush
                );
            }

            paintbrush.setColor(Color.WHITE);
            paintbrush.setTextSize(60);
            canvas.drawText("Lives remaining: " + lives,
                    900,
                    100,
                    paintbrush
            );

            paintbrush.setTextSize(100);
            if(eHealth < 1) {
                canvas.drawBitmap(this.restart, 200, 50, paintbrush);

                canvas.drawText("YOU WON ",
                        this.screenWidth / 2,
                        this.screenHeight / 2,
                        paintbrush
                );
            }

            //canvas.drawText("RESTART GAME", 200, 50, paintbrush);
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
                int newX = this.player.getxPosition() + (int) (xn1 * 50);
                int newY = this.player.getyPosition() + (int) (yn1 * 50);
                this.player.setxPosition(newX);
                this.player.setyPosition(newY);

                // 4. update the bullet hitbox position
                this.player.updateHitbox();
                break;

            case MotionEvent.ACTION_DOWN:
                break;

        }

        //restart pixels

       if(event.getX() > 200 && event.getX() <= 300 ){
           if(event.getY() > 100 && event.getY() <= 150){

               Log.d(TAG, "Person's pressed: "
                       + event.getX() + ","
                       + event.getY());

               restartGame();
           }
       }
       return true;
    }


}
