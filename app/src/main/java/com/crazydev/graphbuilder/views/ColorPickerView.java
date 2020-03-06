package com.crazydev.graphbuilder.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

public class ColorPickerView extends View implements View.OnTouchListener {

    protected static final int SET_COLOR = 0;
    protected static final int SET_SATUR = 1;

    private int	mode;

    private float cx;
    private float cy;
    private float rad_1;
    private float rad_2;
    private float r_centr; // радиусы наших окружностей

    private float r_sel_c;
    private float r_sel_s;

    private Paint p_color = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint p_satur = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint p_white = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint p_handl = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint p_center = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float deg_col; // углы поворота
    private float deg_sat = (float) Math.PI / 2; // указателей - стрелок

    private float lc; //
    private float lm; // отступы и выступы линий
    private float lw; //

    private int color;

    private int size;

    private int[] argb  = new int[] {	255, 0, 0, 0};
    private float[] hsv = new float[] {0, 1f, 1f};

    public ColorPickerView(Context context) {
        this(context, null);
    }

    public ColorPickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {

        p_color.setStyle(Style.STROKE);
        p_satur.setStyle(Style.STROKE);
        p_center.setStyle(Style.FILL_AND_STROKE);
        p_white.setStrokeWidth(2);
        p_white.setColor(Color.WHITE);
        p_white.setStyle(Style.STROKE);
        p_handl.setStrokeWidth(5);
        p_handl.setColor(Color.WHITE);
        p_handl.setStrokeCap(Cap.ROUND);

        setOnTouchListener(this);

        Random r = new Random();

        this.setSatScale(r.nextFloat() * 160 + 10);
        this.setColScale(r.nextFloat() * 360);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mWidth = measure(widthMeasureSpec);
        int mHeight = measure(heightMeasureSpec);
        size = Math.min(mWidth, mHeight);
        setMeasuredDimension(size, size);

        calculateSizes(); //  метод для расчетов всяких наших размеров

    }

    private int measure(int measureSpec) {
        int result = 0;
        int specMoge = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMoge == MeasureSpec.UNSPECIFIED) result = 200;
        else result = specSize;
        return result;
    }

    private void calculateSizes() {
        cx = size * 0.5f;
        cy = cx;
        lm = size * 0.043f;
        lw = size * 0.035f;
        rad_1 = size * 0.44f;
        r_sel_c = size * 0.39f;
        rad_2 = size * 0.34f;
        r_sel_s = size * 0.29f;
        //	rad_3 = size * 0.24f;
        //	r_sel_a = size * 0.19f;
        r_centr = size * 0.24f;

        lc = size * 0.08f;

        int[] mColors = new int[] {Color.RED, Color.GREEN, Color.BLUE, Color.RED};
        Shader s = new SweepGradient(cx, cy, mColors, null);
        p_color.setShader(s);

        p_color.setStrokeWidth(lc);
        p_satur.setStrokeWidth(lc);

    }

    protected float getAngle(float x, float y) {
        float deg = 0;
        if (x != 0) {
            deg = y / x;
        }
        deg = (float) Math.toDegrees(Math.atan(deg));

        if (x < 0) {
            deg += 180;
        } else if (x > 0 && y < 0) {
            deg += 360;
        }

        return deg;
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);

        drawColorGradient(c);
        drawSaturGradient(c);
        drawLines(c);

        drawCenter(c);

    }

    private void drawColorGradient(Canvas c) {
        c.drawCircle(cx, cy, rad_1, p_color);
    }

    private void drawSaturGradient(Canvas c) {

        SweepGradient s = null;
        int[] sg = new int[] {
                Color.HSVToColor(new float[] {deg_col, 1, 0}), Color.HSVToColor(new float[] {deg_col, 1, 1}), Color.HSVToColor(new float[] { hsv[0], 0, 1}), Color.HSVToColor(new float[] { hsv[0], 0, 0.5f}), Color.HSVToColor(new float[] {deg_col, 1, 0})
        };

        s = new SweepGradient(cx, cy, sg, null);
        p_satur.setShader(s);
        c.drawCircle(cx, cy, rad_2, p_satur);

    }

    private void drawLines(Canvas c) {
        float d = deg_col;
        c.rotate(d, cx, cy);
        c.drawLine(cx + rad_1 + lm, cy, cx + rad_1 - lm, cy, p_handl);
        c.rotate(-d, cx, cy);
        d = deg_sat;
        c.rotate(d, cx, cy);
        c.drawLine(cx + rad_2 + lm, cy, cx + rad_2 - lm, cy, p_handl);
        c.rotate(-d, cx, cy);
    }

    private void drawCenter(Canvas c) {
        c.drawCircle(cx, cy, r_centr, p_center);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                float a = Math.abs(event.getX() - cx);
                float b = Math.abs(event.getY() - cy);

                float c = (float) Math.sqrt(a * a + b * b);

                if (c > r_sel_c) {
                    mode = SET_COLOR;
                } else if (c < r_sel_c && c > r_sel_s) {
                    mode = SET_SATUR;
                }  else if (c < r_centr) {
                    //	listener.onDismiss(mColor, alpha);

                    Log.d("colorr", "red = " + Color.red(color) +
                            " green = " + Color.green(color) + " blue = " + Color.blue(color));
                }

                break;

            case MotionEvent.ACTION_MOVE:
                float x = event.getX() - cx;
                float y = event.getY() - cy;
                switch (mode) {
                    case SET_COLOR:
                        setColScale(getAngle(x, y));
                        break;

                    case SET_SATUR:
                        setSatScale(getAngle(x, y));
                        break;
                }

                break;
        }

        invalidate();
        return true;
    }

    protected void setColScale(float f) {
        deg_col = f;
        hsv[0] = f;
        color = Color.HSVToColor(argb[0], hsv);
        p_center.setColor(color);
    }

    protected void setSatScale(float f) {
        deg_sat = f;
        if (f < 90) {
            hsv[1] = 1;
            hsv[2] = f / 90;
        }
        else if (f >= 90 && f < 180) {
            hsv[1] = 1 - (f - 90) / 90;
            hsv[2] = 1;
        }
        else {
            hsv[1] = 0;
            hsv[2] = 1 - (f - 180) / 180;
        }
        color = Color.HSVToColor(argb[0], hsv);
        p_center.setColor(color);
    }

    public int getColor() {
        return this.color;
    }

}
