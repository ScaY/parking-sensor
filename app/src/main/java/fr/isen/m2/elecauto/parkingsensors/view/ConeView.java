package fr.isen.m2.elecauto.parkingsensors.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by stephane on 13/01/16.
 */
public class ConeView extends View {

    public RectF oval;
    public RectF oval2;
    private Context context;


    public ConeView(Context context) {
        super(context);
        init(context);
    }

    public ConeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ConeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        /*

        int x = 10;
        int y = 10;
        int width = 300;
        int height = 50;

        mDrawable = new ShapeDrawable(new OvalShape());
        mDrawable.getPaint().setColor(0xff74AC23);
        mDrawable.setBounds(x, y, x + width, y + height);*//*
        Bitmap b = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);*/

    }


    @Override
    protected void onDraw(Canvas canvas) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = 200;//metrics.widthPixels;
        int height = 100;//metrics.heightPixels;
        oval = new RectF(0, 0, width, height);
        Paint paint = new Paint();
        canvas.drawArc(oval, -40, -40, true, paint);

        oval2 = new RectF(0, 0, width, height / 2);
        Paint paint2 = new Paint();
        paint2.setColor(Color.RED);
        canvas.drawArc(oval2, -40, -40, true, paint2);

    }

}
