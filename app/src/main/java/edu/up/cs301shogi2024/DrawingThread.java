package edu.up.cs301shogi2024;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;

public class DrawingThread extends Thread {

    private final SurfaceHolder surfaceHolder;
    private boolean running = false;
    private final Bitmap pieceBitmap; // Bitmap to be drawn

    public DrawingThread(SurfaceHolder holder, Bitmap pieceBitmap) {
        this.surfaceHolder = holder;
        this.pieceBitmap = pieceBitmap;
    }

    public void setRunning(boolean isRunning) {
        this.running = isRunning;
    }

    @Override
    public void run() {
        Canvas canvas;
        while (running) {
            canvas = null;
            try {
                // Lock the canvas for drawing
                canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    synchronized (surfaceHolder) {
                        // Draw the grid and the image
                        drawGrid(canvas);
                    }
                }
            } finally {
                if (canvas != null) {
                    // Unlock the canvas and post the updates
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            // Control the frame rate (optional)
            try {
                Thread.sleep(16); // Approximately 60 FPS
            } catch (InterruptedException e) {
                // Handle interruption
            }
        }
    }

    private void drawGrid(Canvas canvas) {
        // Clear the canvas
        canvas.drawColor(android.graphics.Color.WHITE);

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // Calculate cell size
        float cellWidth = width / 9f;
        float cellHeight = height / 9f;

        Paint paint = new Paint();
        paint.setColor(android.graphics.Color.BLACK);
        paint.setStrokeWidth(4);

        // Draw vertical lines
        for (int i = 0; i <= 9; i++) {
            float x = i * cellWidth;
            canvas.drawLine(x, 0, x, height, paint);
        }

        // Draw horizontal lines
        for (int i = 0; i <= 9; i++) {
            float y = i * cellHeight;
            canvas.drawLine(0, y, width, y, paint);
        }

        // Draw the Bitmap onto the grid at a specific cell
        int row = 4; // Row index (0-based)
        int col = 4; // Column index (0-based)

        // Calculate the position to draw the Bitmap
        float left = col * cellWidth;
        float top = row * cellHeight;

        // Scale the Bitmap to fit the cell
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(pieceBitmap, (int)cellWidth, (int)cellHeight, true);

        // Draw the Bitmap on the canvas
        canvas.drawBitmap(scaledBitmap, left, top, null);

        // Recycle the scaled Bitmap if necessary
        // scaledBitmap.recycle(); // Uncomment if not reusing scaledBitmap
    }

}
