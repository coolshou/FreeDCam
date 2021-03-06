package com.troop.freecam.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.troop.freecam.R;
import com.troop.freecam.interfaces.IStyleAbleSliderValueHasChanged;

/**
 * Created by troop on 22.12.13.
 */
public class StyleAbelSlider extends View
{
    //the activity
    Context context;
    //the sliderimage stored in memory
    Drawable sliderImage;
    //the minmal value of the slider
    int min;
    //the max value of the slider
    int max;
    //the current value of the slider
    int current;
    int sliderWidth;
    int sliderHeight;
    Rect drawPosition;
    boolean horizontal;
    String titel ="";
    String value = "";
    Paint paint;
    int picID;

    int currentpixel = 50;
    int pixelProValue = 10;
    int minpixel = 0;
    int maxpixel = 100;


    private IStyleAbleSliderValueHasChanged valueHasChanged;

    public void OnValueCHanged(IStyleAbleSliderValueHasChanged valueHasChanged)
    {
        this.valueHasChanged = valueHasChanged;
    }

    public StyleAbelSlider(Context context) {
        super(context);
        //init(context);
    }

    public StyleAbelSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);

    }

    public StyleAbelSlider(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        this.context = context;
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.StyleAbelSlider,
                0, 0);

        horizontal = a.getBoolean(R.styleable.StyleAbelSlider_horizontal, false);
        picID = a.getResourceId(R.styleable.StyleAbelSlider_SliderImage, R.drawable.icon_shutter_thanos_blast);
        titel = a.getString(R.styleable.StyleAbelSlider_textview);
        value = a.getString(R.styleable.StyleAbelSlider_valuevied);
        a.recycle();
        min = 0;
        max = 100;
        current = 50;
        paint = new Paint();
        paint.setAntiAlias(true);

        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        maxpixel = getHeight();
        pixelProValue = maxpixel / max;
        currentpixel = current * pixelProValue;
        if (sliderImage == null)
        {
            sliderImage = getResources().getDrawable(picID);// Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), picID), this.getHeight(), this.getHeight(), false);
            getPosToDraw();
            int length = (int)paint.measureText(titel + value);
            canvas.drawText(titel + value, drawPosition.right, paint.getTextSize(), paint);
        }
        if (sliderImage != null)
        {
            value = current+"";
            paint.setTextSize(getWidth()/2);
            sliderImage.setBounds(drawPosition.left, drawPosition.top, drawPosition.right, drawPosition.bottom);
            sliderImage.draw(canvas);
            int length = (int)paint.measureText(value);
            paint.setColor(Color.WHITE);
            if (drawPosition.top >= getWidth()/2)
                canvas.drawText(value, drawPosition.left + (getWidth() - length) /2, drawPosition.top, paint);
            else
                canvas.drawText(value, drawPosition.left + (getWidth() - length) /2, drawPosition.bottom + getWidth()/2, paint);
            paint.setTextSize(getWidth()/4);
            length = (int)paint.measureText(titel);
            int center = (drawPosition.bottom - drawPosition.top) / 2 + drawPosition.top;
            paint.setColor(Color.BLACK);

            canvas.drawText(titel, drawPosition.left + (getWidth() - length) /2, center, paint);

        }
            //canvas.drawBitmap(sliderImage, getPosToDraw().left, getPosToDraw().top, new Paint());
    }

    private Rect getPosToDraw()
    {

        int half = getWidth() / 2;
        Rect tmp = new Rect(0, currentpixel , getWidth(), getWidth() +currentpixel);

        drawPosition = tmp;
        return tmp;
    }

    private int getValueFromDrawingPos(int posi)
    {
        int val;
        value = posi +"";

        int i = (getHeight()- (getWidth()/2))/max;
        val = (posi)/i;

        return val;
    }

    public void SetCurrentValue(int pos)
    {
        if (pos <= max && pos >= min)
        {
            current = pos;

            getPosToDraw();
            invalidate();
        }
    }

    public int getCurrent()
    {
        return current;
    }

    public void SetMaxValue(int max)
    {
        this.max = max;
        if (current > max)
            current = max;


        getPosToDraw();
        invalidate();
    }


    private void setNewDrawingPos(int val)
    {
        requestLayout();
        if (val >= 0 && val <= maxpixel - getWidth() )
        {
            currentpixel = val;
            Rect tmp = new Rect(0, currentpixel , getWidth(), getWidth() +currentpixel);

            drawPosition = tmp;
            current = currentpixel / pixelProValue;
            value = current +"";
            throwvalueHasChanged(current);

        }
    }

    boolean sliderMoving = false;
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        boolean throwevent = false;
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                if (drawPosition.contains((int)event.getX(), (int)event.getY()))
                {
                    sliderMoving = true;
                }
                throwevent = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (sliderMoving)
                {
                    if (horizontal)
                    {
                        setNewDrawingPos((int) event.getX());
                    }
                    else
                    {
                        setNewDrawingPos((int) event.getY());

                    }
                    invalidate();
                    throwevent = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (sliderMoving)
                    sliderMoving = false;
                throwevent = false;
                break;
        }
        return throwevent;
    }

    private void throwvalueHasChanged(int value)
    {
        if (valueHasChanged != null)
            valueHasChanged.ValueHasChanged(value);
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
        super.onSizeChanged(xNew, yNew, xOld, yOld);

    }

}
