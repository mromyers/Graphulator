package com.example.mrm.views;
import com.example.mrm.database.DatabaseManager;
import com.example.mrm.graphulator.R;
import com.example.mrm.parser.Parser;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.util.LinkedList;

import com.example.mrm.parser.Expr;

class Point {
    final float x,y;
    final boolean isGap;
    Point(float x, float y){
        this.x = x; this.y = y;
        this.isGap =  Float.isNaN(x) || Float.isInfinite(x)
                   || Float.isNaN(y) || Float.isInfinite(y);
    }
}


public class GraphView extends View {
    Context context;
    public float epsilon;
    int height, width;
    public float x_min, x_max, y_min, y_max, weight = 0f;
    private float x_scale, y_scale;
    private Parser parser;
    private int[] defColors = {
            Color.RED,Color.rgb(255, 153, 0),Color.rgb(255,127,0),Color.rgb(255, 126, 0),
            Color.YELLOW,Color.rgb(204, 255, 51),Color.GREEN,Color.rgb(51, 153, 102),
            Color.CYAN, Color.BLUE,Color.rgb(153, 102, 255),Color.rgb(204, 0, 255),
            Color.rgb(184, 77, 184),Color.rgb(204, 0, 102),Color.MAGENTA,Color.rgb(255, 166, 201)};
    Paint border_paint, axes_paint;

    Slot[] slots; int numSlots;

    Canvas canvas;

    public GraphView(Context context){
        this(context, null);
        this.context = context;
    }

    public GraphView(Context context, AttributeSet attrs){
        this(context, attrs, 0);
        this.context = context;
    }

    public GraphView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.GraphView,
                0, 0);
        try {
            parser = new Parser();
            x_min = a.getFloat(R.styleable.GraphView_x_min, -20f);
            x_max = a.getFloat(R.styleable.GraphView_x_max, 20f);
            y_min = a.getFloat(R.styleable.GraphView_y_min, -10f);
            y_max = a.getFloat(R.styleable.GraphView_y_max,10f);
            epsilon = a.getFloat(R.styleable.GraphView_epsilon, 0.1f);
            border_paint = new Paint();
            border_paint.setAlpha(a.getInt(R.styleable.GraphView_borderAlpha, 255));
            border_paint.setStyle(Paint.Style.STROKE);
            border_paint.setColor(a.getColor(R.styleable.GraphView_borderColor, Color.BLACK));
            border_paint.setStrokeWidth(a.getInt(R.styleable.GraphView_borderWidth, 2));
            axes_paint = new Paint();
            axes_paint.setAlpha(a.getInt(R.styleable.GraphView_axesAlpha, 255));
            axes_paint.setStyle(Paint.Style.STROKE);
            axes_paint.setColor(a.getColor(R.styleable.GraphView_axesColor, Color.BLACK));
            axes_paint.setStrokeWidth(a.getInt(R.styleable.GraphView_axesWidth, 2));
            numSlots = a.getInt(R.styleable.GraphView_numCurves,10);
            initializeSlots();

        } finally{
            a.recycle();
        }
    }

    void initializeSlots(){
        slots = new Slot[numSlots];
        for (int i = 0; i < numSlots; i++){
            slots[i] = new Slot();
            setSlotColor(i, defaultColor(i));
            slots[i].active = false;
        }
    }

    int defaultColor(int slot){
        return defColors[slot%16];
    }

    public void setSlots(LinkedList<ContentValues> list){
        //numSlots = list.size();
        initializeSlots();
        int i = 0;
        for(ContentValues cv : list){
            setSlot(cv);
        }
    }

    void setSlot(ContentValues cv){
        int i = cv.getAsInteger("slot");
        slots[i].set(cv.getAsString("function"));
        slots[i].paint.setColor(cv.getAsInteger("color"));
        slots[i].active = cv.getAsBoolean("active");
    }

    public ContentValues getSlot(int i){
        ContentValues cv = new ContentValues();
        cv.put("slot", i);
        cv.put("function", slots[i].text);
        cv.put("color",slots[i].paint.getColor());
        cv.put("active", slots[i].active);
        return cv;
    }

    public LinkedList<ContentValues> getSlots(){
        LinkedList<ContentValues> list = new LinkedList<>();
        for(int i = 0; i < numSlots; i++)
            list.push(getSlot(i));
        return list;
    }


    @Override
    protected void onDraw(Canvas canvas){

        connectCanvas(canvas);
        //Borders
        canvas.drawLine(0,0,width,0,border_paint);
        canvas.drawLine(0,0,0,height,border_paint);
        canvas.drawLine(0,height,width,height,border_paint);
        canvas.drawLine(width,0,width,height,border_paint);

        //Axes
        drawLine(0, y_min, 0, y_max, axes_paint);
        drawLine(x_min, 0, x_max, 0, axes_paint);

        //Slots
        for(Slot s : slots)
            s.drawToCanvas();

        this.invalidate();
    }

    void setBounds(float x0, float x1, float y0, float y1){
        x_min = x0; x_max = x1;
        y_min = y0; y_max = y1;
    }

    void connectCanvas(Canvas c){
        canvas = c;
        height = canvas.getHeight();
        x_scale = width / (x_max - x_min);
        y_scale = height / (y_max - y_min);
        width = canvas.getWidth();
    }

    float xScale(float x){return (x - x_min) * x_scale;}
    float yScale(float y){return (y_max - y) * y_scale;}

    void drawLine(float x0, float y0, float x1, float y1, Paint paint){
        canvas.drawLine(xScale(x0), yScale(y0), xScale(x1), yScale(y1), paint);
    }

    void drawLine(Point p0, Point p1, Paint paint){
        if(p0.isGap || p1.isGap)
            return;
        else
            drawLine(p0.x, p0.y, p1.x, p1.y, paint);
    }

    public void setSlotColor(int i, int color){
        if(i < numSlots)
            slots[i].paint.setColor(color);
    }
    public void setSlot(int i, String text){
        if(i < numSlots){
            slots[i].set(text);
        }
    }
    public void setSlot(int i, String text, int color){
        setSlot(i,text);
        setSlotColor(i, color);
    }

    public void clearSlot(int i){
        if(i < numSlots)
            slots[i].clear();
    }
    public String getSlotText(int slot){
        if(slot < numSlots && slots[slot].active)
            return slots[slot].text;
        else
            return "";
    }

    public String[] getSlotsText(){
        String[] slotsText = new String[numSlots];
        for(int i = 0; i < numSlots; i++) {
            if (slots[i].active)
                slotsText[i] = slots[i].text;
            else
                slotsText[i] = "";
        }
        return slotsText;
    }
    public boolean[] getActiveSlots(){
        boolean[] activeSlots = new boolean[numSlots];
        for(int i = 0; i < numSlots; i++)
            activeSlots[i] = slots[i].active;
        return activeSlots;
    }
    public int[] getSlotColors(){
        int[] slotColors = new int[numSlots];
        for(int i = 0; i < numSlots; i++)
            slotColors[i] = slots[i].paint.getColor();

        return slotColors;
    }

    class Slot {
        Expr expr;
        String text;
        Paint paint;
        private LinkedList<Point> savedSegment;
        boolean active;

        public Slot(){
            savedSegment = new LinkedList<Point>();
            paint = new Paint();
            clear();
        }

        public Slot(Expr expr, Paint paint) {
            this.expr = expr;
            this.paint = paint;
            paint.setAlpha(255);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);
            this.savedSegment = new LinkedList<Point>();
        }
        LinkedList<Point> getSavedSegment(){return savedSegment;}

        public void resetSavedSegment(){
            LinkedList<Point> list = new LinkedList<Point>();
            float x = x_max;
            while (x_min <= x) {
                list.push(new Point(x, expr.eval(x)));
                x -= epsilon;
            }
            list.push(new Point(x_min, expr.eval(x_min)));
            savedSegment = list;

        }

        public void clear(){
            expr = null;
            text = "";
            active = false;
            savedSegment.clear();
        }
        public void set(String text){
            this.text = text;
            expr = parser.parse(text);
            resetSavedSegment();
            active = true;
        }

        public void drawToCanvas(){
            paint.setStrokeWidth(weight);
            if(active) {
                Point prev = new Point(Float.NaN, Float.NaN);
                for (Point p : savedSegment) {
                    drawLine(prev, p, paint);
                    prev = p;

                }
            }

        }

    }
}