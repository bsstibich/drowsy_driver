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

    public RectOverlay(Context context, @Nullable AttributeSet attributes) {
        super(context, attributes);

        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10F);
        paint.setStyle(Paint.Style.STROKE);
    }

    public void frameFace(Rect newRect) {
        Log.d("rectOverlay", "drawing face");
        rect = newRect;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("drawing", "onDraw called");
        canvas.drawRect(rect, paint);
    }

}
