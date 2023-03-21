package com.becroft.bullethell;

import android.graphics.RectF;

public class Bullet {

    // A RectF to represent size and location of bullet
    private RectF rect;

    // How fast is bullet travelling?
    private float XVelocity;
    private float YVelocity;

    // How big is bullet
    private float width;
    private float height;

    public Bullet(int screenX){
        // configure bullet based on screen width in pixels
        width = (screenX/100);
        height = (screenX/100);
        rect = new RectF();
        YVelocity = (screenX/5);
        XVelocity = (screenX/5);
    }

    // Return reference to RectF
    RectF getRect(){
        return rect;
    }

    void update(long fps){
        // Update the left and top coordinates based on velocity and current frame rate
        rect.left = rect.left + (XVelocity/fps);
        rect.top = rect.top + (YVelocity/fps);
        // maintain size of rect
        rect.right = rect.left+width;
        rect.bottom = rect.top+height;
    }

    // Reverse Bullet vertical direction
    void reverseYVelocity(){
        YVelocity = -YVelocity;
    }
    //Reverse bullet horizontal direction
    void reverseXVelocity(){
        XVelocity = -XVelocity;
    }

    // Spawn a new bullet
    void spawn(int pX, int pY, int vX, int vY){

        //Spawn bullet at location passed in params
        rect.left = pX;
        rect.top = pY;
        rect.right = pX+width;
        rect.bottom = pY+height;

        // Aim away from player
        XVelocity = XVelocity * vX;
        YVelocity = YVelocity * vY;
    }

}
