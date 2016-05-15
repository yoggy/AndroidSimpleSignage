package net.sabamiso.android.androidsimplesignage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import net.sabamiso.android.util.Config;

import static android.view.GestureDetector.*;

public class OverlayView extends View {

    SignageActivity signageActivity;
    GestureDetector gestureDetector;

    Config config;

    public OverlayView(SignageActivity context) {
        super(context);
        signageActivity = context;

        config = Config.getInstance();

        setBackgroundColor(Color.MAGENTA);
        getBackground().setAlpha(0);

        gestureDetector = new GestureDetector(context, simpleOnGestureListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent evt) {
        gestureDetector.onTouchEvent(evt);

        return config.getBoolean("enable_prevent_touch", true); // You need to prevent the motionevent for WevView at the bottom...
    }

    SimpleOnGestureListener simpleOnGestureListener = new SimpleOnGestureListener() {
        @Override
        public void onLongPress(MotionEvent event) {
            signageActivity.startSettingsActivity();
        }
    };
}
