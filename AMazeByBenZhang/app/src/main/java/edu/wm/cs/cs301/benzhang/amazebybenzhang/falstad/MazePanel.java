package edu.wm.cs.cs301.benzhang.amazebybenzhang.falstad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles maze graphics.
 */
public class MazePanel extends View {

    private static final String TAG = "MazePanel";

    private Bitmap immediateBitmap;

    private Bitmap bufferBitmap;
    private Canvas bufferCanvas;
    private int color;

    private Paint paint;

    private List<NeedDimensions> reportTo;

    private Rect bitmapSize;
    private Rect canvasSize;

    /**
     * Constructor with one context parameter.
     * @param context
     */
    public MazePanel(Context context) {
        super(context);
        reportTo = new ArrayList<NeedDimensions>();
        initPaint();
    }
    /**
     * Constructor with two parameters: context and attributes.
     * @param context
     * @param app
     */
    public MazePanel(Context context, AttributeSet app) {
        super(context, app);
        reportTo = new ArrayList<NeedDimensions>();
        initPaint();
    }
    /**
     * Draws given canvas.
     * @param c canvas
     */
    @Override
	public void onDraw(Canvas c) {
        if(isInEditMode()){
            super.onDraw(c);
            return;
        }
        super.onDraw(c);
        if(immediateBitmap == null){
            return;
        }
        //Log.v(TAG, "onDraw called");
        //Log.v(TAG, "Bitmap status is " + bufferBitmap);
        c.drawBitmap(immediateBitmap, bitmapSize, canvasSize, null);
//        paint.setStyle(Paint.Style.STROKE);
//        setColor(Color.BLUE);
//        c.drawLine(0, 0, getWidth(), getHeight(), paint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmapSize = new Rect(0, 0, getCanvasWidth(), getCanvasHeight());
        canvasSize = new Rect(0, 0, getWidth(), getHeight());
        createBitmapsIfNeeded(true);
        reportDimensions(getCanvasWidth(), getCanvasHeight());
        invalidate();
        Log.v(TAG, "Size changed; reporting dimensions to viewers needing dimensions");
    }

    protected void initPaint(){
        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //p.setFilterBitmap(true);
        //p.setDither(true);
        paint.setColor(this.color);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(1);
    }

    public void addToReport(NeedDimensions dimNeeder){
        reportTo.add(dimNeeder);
    }

    private void reportDimensions(int width, int height){
        for(NeedDimensions needer : reportTo){
            needer.reportDimensions(width, height);
        }
    }

    private int getCanvasWidth(){
        return (getWidth() > 0) ? getWidth() : Constants.VIEW_WIDTH; // Constants.VIEW_WIDTH;
    }

    private int getCanvasHeight(){
        return (getHeight() > 0) ? getHeight() : Constants.VIEW_HEIGHT; //Constants.VIEW_HEIGHT;
    }

    /**
     * Sets private buffer bitmap to a clean instance
     */
    public void createBitmapsIfNeeded(boolean force){
//        if(immediateBitmap == null){
//            immediateBitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
//        }
        if(bufferBitmap == null || force){
            bufferBitmap = Bitmap.createBitmap(this.getCanvasWidth(), this.getCanvasHeight(), Bitmap.Config.ARGB_8888);
            bufferCanvas = new Canvas(bufferBitmap);
        }
    }
//
    /**
     * Deploys the buffer, replacing the old immediate which is displayed in onDraw
     */
    private void pushBitmapImmediate(){
        immediateBitmap = Bitmap.createBitmap(bufferBitmap);
    }
    
    /**
     * Measures the view and its content to determine the measured width and the measured height.
     * @param width
     * @param height
     */
    @Override
    public void onMeasure(int width, int height) {
        //setMeasuredDimension(width, height);
        super.onMeasure(width, height);
        //Log.v(TAG, "measured!");
    }
    
    /**
     * Updates maze graphics.
     */
    public void update() {
        if(getWidth() < 1 || getHeight() < 1){
            return; // not yet initialized view
        }
        pushBitmapImmediate();
        //invalidateUI();
        bufferCanvas.drawColor(Color.WHITE); // clear canvas
    }

    public void invalidateUI(){
        //Log.v(TAG, "invalidated");
        invalidate();
    }
    
    /**
     * Takes in color string, sets paint color to corresponding color. 
     * @param color string
     */
    public void setColor(String color) {
        this.setColor( Color.parseColor(color) );
    }
    
    /**
     * Sets paint object color attribute to given color.
     * @param color
     */
    public void setColor(int color) {
        //Log.v(TAG, "setting color " + color);
        this.color = color;
        this.paint.setColor(color);
    }
    
    /**
     * Takes in color integer values [0-255], returns corresponding color-int value. 
     * @param red red value
     * @param green green value
     * @param blue blue value
     */
    public static int getColorEncoding(int red, int green, int blue) {
        return Color.rgb(red, green, blue);
    }
    
    /**
     * Returns the RGB value representing the current color. 
     * @return integer RGB value
     */
    public int getColor() {
	    return this.color;
    }

    /**
     * Takes in rectangle params, fills rectangle in canvas based on these. 
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public void fillRect(int x, int y, int width, int height) {
        //Log.v(TAG, "Filling rect " + x + "," + y + ":" + width + "," + height);
        //Log.v(TAG, "Color is " + Integer.toHexString(getColor()));
        paint.setStyle(Paint.Style.FILL);
        bufferCanvas.drawRect(new Rect(x, y, x + width, y + height), paint);
        //bufferCanvas.drawRect(x, y, width, height, paint);
    }
    
    /**
     * Takes in polygon params, fills polygon in canvas based on these. 
     * Paint is always that for corn.
     * @param xPoints
     * @param yPoints
     * @param nPoints
     */
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints){
        //Log.v(TAG, "Filling polygon");
        Path poly = new Path();
        poly.moveTo(xPoints[0], yPoints[0]);
        for(int i = 1; i < nPoints; i++){
            poly.lineTo(xPoints[i], yPoints[i]);
        }
        paint.setStyle(Paint.Style.FILL);
        bufferCanvas.drawPath(poly, paint);
    }
    
    /**
     * Takes in line params, draws line in canvas based on these. 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public void drawLine(int x1, int y1, int x2, int y2) {
        //Log.v(TAG, "Drawing line");
        paint.setStyle(Paint.Style.STROKE);
	    bufferCanvas.drawLine(x1, y1, x2, y2, paint);
    }
    
    /**
     * Takes in oval params, fills oval in canvas based on these. 
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public void fillOval(int x, int y, int width, int height) {
        //Log.v(TAG, "Filling oval");
        paint.setStyle(Paint.Style.FILL);
	    bufferCanvas.drawOval(new RectF(x, y, x + width, y + height), paint);
    }

}
