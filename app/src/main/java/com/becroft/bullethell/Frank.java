package com.becroft.bullethell;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

public class Frank {

    RectF rect;
    float frankHeight;
    float frankWidth;
    boolean teleporting = false;

    Bitmap bitmap;

    public Frank(Context context, float screenX, float screenY) {
        frankHeight = screenY/10;
        frankWidth = frankHeight/2;

        rect = new RectF(screenX/2,screenY/2, (screenX/2) + frankWidth, (screenY/2) + frankHeight);

        // prepare BMP load Frank from his .png
        // Frank looks after his own resources ENCAPSULATION
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.frank);
    }

    boolean teleport(float newX, float newY){

        // Did Frank manage to telport?
        boolean success = false;

        // Move frank to new position if not already teleporting
        if(!teleporting){

            // make central to touch point
            rect.left = newX - frankWidth / 2;
            rect.top = newY - frankHeight / 2;
            rect.bottom = rect.top + frankHeight;
            rect.right = rect.left + frankWidth;

            teleporting = true;

            // notify teleporting was successful
            success = true;
        }
        return success;
    }

    void setTeleportAvailable(){
        teleporting = false;
    }

    RectF getRect(){
        return rect;
    }

    Bitmap getBitmap(){
        return bitmap;
    }
}
