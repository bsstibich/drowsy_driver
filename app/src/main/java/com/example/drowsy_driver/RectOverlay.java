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

public class RectOverlay extends View {

    private Paint paint;
    private Rect rect = new Rect();

    private boolean newFace = false;
    private boolean updateBorder = true;

    private Paint borderBrush;

    public RectOverlay(Context context, @Nullable AttributeSet attributes) {
        super(context, attributes);

        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10F);
        paint.setStyle(Paint.Style.STROKE);

        borderBrush = new Paint();
        borderBrush.setColor(Color.RED);
        borderBrush.setStrokeWidth(20F);
        borderBrush.setStyle(Paint.Style.STROKE);
        invalidate();
    }

    public void frameFace(Rect newRect) {
        Log.d("rectOverlay", "drawing face");
        rect = newRect;
        newFace = true;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (newFace) {
            Log.d("drawing", "onDraw called");
            canvas.drawRect(rect, paint);
            newFace = false;
            updateBorder = true;
        }

        if (updateBorder) {
            Log.d("border", "updating border");
            canvas.drawRect(0, 0, getWidth(), getHeight(), borderBrush);
            updateBorder = false;
        }
    }

}
