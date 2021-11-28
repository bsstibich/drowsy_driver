package com.example.drowsy_driver;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class Overlay extends View {

    private boolean face_detected = false;
    private boolean eyes_detected = false;
    private boolean eyes_are_open = false;

    private boolean updateBorder = true;
    private Paint borderBrush;

    // private Paint paint;
    // private Rect rect = new Rect();

    public Overlay(Context context, @Nullable AttributeSet attributes) {
        super(context, attributes);

        // rectSettings();
        borderSettings();

        invalidate();
    }

    public void foundFace(boolean found) {
        if (found && !face_detected) {
            Log.d("face detection", "found face");
            face_detected = true;
            updateBorder = true;
        }
        if (!found && face_detected) {
            face_detected = false;
            eyes_detected = false;
            eyes_are_open = false;
            updateBorder = true;
            invalidate();
        }
    }

    public void foundEyes(boolean found) {
        if (found && !eyes_detected) {
            Log.d("face detection", "found eyes");
            eyes_detected = true;
            updateBorder = true;
        }
        if (!found && eyes_detected) {
            Log.d("face detection", "no eyes found");
            eyes_detected = false;
            eyes_are_open = false;
            updateBorder = true;
            invalidate();
        }
    }

    public void eyesOpen(boolean open) {
        if (open && !eyes_are_open) {
            Log.d("face detection", "eyes are open");
            eyes_are_open = true;
            updateBorder = true;
            invalidate();
        }
        if (!open && eyes_are_open) {
            Log.d("face detection", "eyes are closed");
            eyes_are_open = false;
            updateBorder = true;
            invalidate();
        }
    }

    private void updateBorderColor(boolean face, boolean eyes, boolean open) {
        if (face && eyes && open) {
            borderBrush.setColor(Color.GREEN);
        }
        else if (face && eyes) {
            borderBrush.setColor(Color.YELLOW);
        }
        else {
            borderBrush.setColor(Color.RED);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (updateBorder) {
            updateBorderColor(face_detected, eyes_detected, eyes_are_open);
            canvas.drawRect(0, 0, getWidth(), getHeight(), borderBrush);
            updateBorder = false;
        }
    }

    private void borderSettings() {
        borderBrush = new Paint();
        borderBrush.setColor(Color.RED);
        borderBrush.setStrokeWidth(50F);
        borderBrush.setStyle(Paint.Style.STROKE);
    }

    /*
    public void frameFace(Rect newRect) {
        Log.d("rectOverlay", "drawing face");
        rect = newRect;
        face_detected = true;
        invalidate();
    }

    private void rectSettings() {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10F);
        paint.setStyle(Paint.Style.STROKE);
    }
    */
}
