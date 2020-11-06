package com.example.gravitypaint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class CanvasView extends View {

    private Bitmap image;
    private Canvas canvas;

    public CanvasView(Context context){
        super(context);
    }

    public Canvas getCanvas(){
        return canvas;
    }

    @Override

    protected void onLayout(boolean changed, int left, int top, int right, int bottom){
        super.onLayout(changed, left, top, right, bottom);
        Bitmap.Config config = Bitmap.Config.RGB_565;
        image = Bitmap.createBitmap(getWidth(), getHeight(), config);
        canvas = new  Canvas(image);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        canvas.drawRect(left, top, right, bottom, paint);
    }

    @Override

    public void onDraw(Canvas canvas){
        super.onDraw(canvas);

        Paint myPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        myPaint.setColor(Color.BLACK);

        canvas.drawBitmap(image, 0,0, myPaint);
    }

    public Bitmap getImage() {
        return image;
    }
    public void setImage(Bitmap image) {
        this.image = image;
    }
}

