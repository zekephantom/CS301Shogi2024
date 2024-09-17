package edu.up.cs301shogi2024;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Bitmap;

public class ShogiBoard extends SurfaceView implements SurfaceHolder.Callback {

    private DrawingThread drawingThread;
    private final Bitmap pieceBitmap;

    public ShogiBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Register the callback
        getHolder().addCallback(this);
        pieceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pawn);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Start the drawing thread when the surface is created
        drawingThread = new DrawingThread(getHolder(), pieceBitmap);
        drawingThread.setRunning(true);
        drawingThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Stop the drawing thread when the surface is destroyed
        boolean retry = true;
        drawingThread.setRunning(false);
        while (retry) {
            try {
                drawingThread.join();
                retry = false;
            } catch (InterruptedException e) {
                // Retry stopping the thread
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Handle surface changes if necessary
    }
}
