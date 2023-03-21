package com.becroft.bullethell;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.Random;

public class BulletHellGame extends SurfaceView implements Runnable {

    // Are we debugging
    boolean Debugging = true;

    // Objects for the game loop/thread
    private Thread gameThread = null;
    private volatile boolean playing;
    private boolean paused;

    // Objects for drawing
    private SurfaceHolder holder;
    private Canvas canvas;
    private Paint paint;

    // Keep track of framerate
    private long FPS;
    // Seconds in Milisecond
    private final int MILLIS_IN_SECOND = 1000;

    // Holds the resolution
    private int screenX;
    private int screenY;

    // How big text will be
    private int fontSize;
    private int fontMargin;

    // These are for sound
    private SoundPool soundPool;
    private int beepID = -1;
    private int teleportID = -1;

    // Up to 10000 bullets
    private Bullet[] bullets = new Bullet[10000];
    private int numBullets = 0;
    private int spawnRate = 1;

    private Random randomX = new Random();
    private Random randomY = new Random();

    // Frank
    private Frank frank;
    private boolean hit = false;
    private int numHits;
    private int shield = 10;

    // Lets time the game
    private long StartGameTime;
    private long BestGameTime;
    private long TotalGameTime;



    // Constructor for BulletHellGame
    public BulletHellGame(Context context, int x, int y){
        super(context);

        // initialise variables
        screenX = x;
        screenY = y;
        // Font is 5% of screen width
        fontSize = screenX / 20;
        // Margin is 2% of screen width
        fontMargin = screenX / 50;
        // Initialise holder
        holder = getHolder();
        paint = new Paint();
        // Initialise soundpool
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            AudioAttributes audioAttributes = new AudioAttributes.Builder().
                    setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.
                    CONTENT_TYPE_SONIFICATION).build();

            soundPool = new SoundPool.Builder().setMaxStreams(5).
                    setAudioAttributes(audioAttributes).build();
        } else {
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }
        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;
            // Beep noise
            descriptor = assetManager.openFd("beep.ogg");
            beepID = soundPool.load(descriptor,0);
            // Teleport noise
            descriptor = assetManager.openFd("teleport.ogg");
            teleportID = soundPool.load(descriptor,0);
        } catch (IOException e){
            Log.e("error", "failed to load sound files");
        }

        for (int i = 0; i < bullets.length; i++){
            bullets[i] = new Bullet(screenX);
        }

        frank = new Frank(context, screenX, screenY);

        startGame();
    }

    // Called to start game
    public void startGame(){

    }

    // Spawns a bullet
    private void spawnBullet(){
        // increase number of bullets
        numBullets++;

        // Where to spawn next bullet and direction of travel
        int spawnX;
        int spawnY;
        int velocityX;
        int velocityY;

        // Subject to change

        //Pick a random point on screen to spawn bullet
        spawnX = randomX.nextInt(screenX);
        spawnY = randomY.nextInt(screenY);

        // horizontal direction of travel
        velocityX = 1;
        // Randomly make velocity x negative
        if(randomX.nextInt(2)==0){
            velocityX = -1;
        }

        // vertical direction of travel
        velocityY = 1;
        // Randomly make velocity y negative
        if(randomY.nextInt(2)==0){
            velocityY = -1;
        }

        // spawn the bullet
        bullets[numBullets-1].spawn(spawnX,spawnY,velocityX,velocityY);
    }

    // Handle game loop
    @Override
    public void run(){
        while(playing){
            // start frame
            long startFrameTime = System.currentTimeMillis();

            if(!paused){
                update();
                // Now all bullets have been moved we can detect collisions
                detectCollisions();
            }

            draw();

            // end frame
            long timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if(timeThisFrame >= 1){
                FPS = MILLIS_IN_SECOND/ timeThisFrame;
            }

        }
    }

    // Update all game objects
    private void update(){
        for(int i = 0; i < numBullets; i++){
            bullets[i].update(FPS);
        }
    }

    // Detect all collisions

    private void detectCollisions(){
        // Has bullet collided with wall
        // Loop each bullet in turn
        for(int i = 0; i < numBullets; i++){
            if (bullets[i].getRect().bottom > screenY){
                bullets[i].reverseYVelocity();
            } else if (bullets[i].getRect().top < 0){
                bullets[i].reverseYVelocity();
            } else if (bullets[i].getRect().left < 0){
                bullets[i].reverseXVelocity();
            } else if (bullets[i].getRect().right > screenX){
                bullets[i].reverseXVelocity();
            }
        }

        // Has bullet hit frank
        // Check each bullet for intersection with frank
        for (int i = 0; i < numBullets; i++) {
            if(RectF.intersects(bullets[i].getRect(), frank.getRect())){
                // Frank has been hit
                soundPool.play(beepID,1,1,0,0,1);
                hit = true;
                // rebound bullet that collided
                bullets[i].reverseXVelocity();
                bullets[i].reverseYVelocity();

                //keep track of hits
                numHits++;

                if(numHits==shield){
                    paused =true;
                    TotalGameTime = System.currentTimeMillis() - StartGameTime;
                    startGame();
                }
            }
        }
    }

    private void draw(){
        if(holder.getSurface().isValid()){
            canvas = holder.lockCanvas();
            canvas.drawColor(Color.argb(255,243,111,36));
            paint.setColor(Color.argb(255,255,255,255));

            // All drawing code here
            for(int i = 0; i < numBullets; i++){
                canvas.drawRect(bullets[i].getRect(),paint);
            }

            canvas.drawBitmap(frank.getBitmap(), frank.getRect().left, frank.getRect().top, paint);

            paint.setTextSize(fontSize);
            canvas.drawText("Bullets: " + numBullets + " Shield: " + (shield - numHits) +
                    " Best Time: " + BestGameTime/MILLIS_IN_SECOND, fontMargin, fontSize, paint);

            // Dont draw the current time when paused
            if(!paused){
                canvas.drawText("Seconds Survived: " + ((System.currentTimeMillis() - StartGameTime)
                        / MILLIS_IN_SECOND), fontMargin, fontMargin*30,paint);
            }

            // Debugging will go here
            if(Debugging){
                printDebuggingText();
            }
            holder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
        paused = false;
        spawnBullet();

        return true;
    }

    public void pause(){
        playing = false;
        try{
            gameThread.join();
        } catch (InterruptedException e){
            Log.e("Error", "joining thread");
        }
    }

    public void resume(){
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    private void printDebuggingText(){
        int debugSize = 35;
        int debugStart = 150;
        paint.setTextSize(debugSize);
        canvas.drawText("FPS: " + FPS, 10, debugStart + debugSize, paint);
    }

}
