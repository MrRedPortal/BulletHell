package com.becroft.bullethell;

import android.app.Activity;
import android.graphics.Point;
import android.view.Display;
import android.view.Window;
import android.os.Bundle;

// This is almost exactly the same as the Pong project

public class BulletHellActivity extends Activity {
    private BulletHellGame BHGame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Get screen resolution
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        // Call constructor to initialise BulletHellGame instance
        BHGame = new BulletHellGame(this, size.x, size.y);
        setContentView(BHGame);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BHGame.resume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        BHGame.pause();
    }
}