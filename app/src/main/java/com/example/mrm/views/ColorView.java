package com.example.mrm.views;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.mrm.graphulator.R;

public class ColorView extends View{
    private Paint paint;
    boolean square;
    public ColorView(Context context){
        this(context,null);

    }
    public ColorView(Context context, AttributeSet attrs){
        this(context,attrs,0);

    }
    public ColorView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        paint = new Paint();
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ColorView,
                0, 0);
        paint.setColor(a.getColor(R.styleable.ColorView_color, Color.WHITE));
        square = a.getBoolean(R.styleable.ColorView_square, true);


        a.recycle();

    }
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        int x = canvas.getWidth()/2,
            y = canvas.getHeight()/2;
        canvas.drawCircle(x, y, Math.min(x, y), paint);
    }

    public int getColor(){
        return paint.getColor();
    }
    public void setColor(int color){
        paint.setColor(color);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (square) {
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);

            int size;
            if (widthMode == MeasureSpec.EXACTLY && widthSize > 0) {
                size = widthSize;
            } else if (heightMode == MeasureSpec.EXACTLY && heightSize > 0) {
                size = heightSize;
            } else {
                size = widthSize < heightSize ? widthSize : heightSize;
            }

            int finalMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
            super.onMeasure(finalMeasureSpec, finalMeasureSpec);
        } else{
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

    }

}