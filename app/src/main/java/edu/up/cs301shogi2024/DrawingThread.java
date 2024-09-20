package edu.up.cs301shogi2024;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * External Citation
 * Date: 16 September 2024
 * Problem: Recommended to use the Thread class for future developments
 * Resource: ChatGPT
 * Solution: Used the example code
 * */

public class DrawingThread extends Thread {
    private SurfaceHolder surfaceHolder;
    private boolean running = false;
    private boolean needsRedraw = false;
    private final Object lock = new Object();

    private List<GamePiece> gamePieces;
    private List<Bitmap> scaledBitmaps;
    private float cellWidth;
    private float cellHeight;
    private float cellDimensions;
    private float fieldDimensions;
    private float capturedFieldRadius;

    public DrawingThread(SurfaceHolder holder, List<GamePiece> gamePieces) {
        this.surfaceHolder = holder;
        this.gamePieces = gamePieces;
        this.scaledBitmaps = new ArrayList<>();
    }

    public void setRunning(boolean isRunning) {
        synchronized (lock) {
            this.running = isRunning;
            if (running) {
                lock.notify(); // Wake up the thread if it's waiting
            }
        }
    }

    public void requestRedraw() {
        synchronized (lock) {
            needsRedraw = true;
            lock.notify(); // Notify the thread to redraw
        }
    }

    public float getCellWidth() {
        return cellWidth;
    }

    public float getCellHeight() {
        return cellHeight;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (lock) {
                if (!running) {
                    break; // Exit the loop if not running
                }
                if (!needsRedraw) {
                    try {
                        lock.wait(); // Wait until notified
                    } catch (InterruptedException e) {
                        // Handle interruption
                    }
                    continue;
                }
                needsRedraw = false;
            }

            Canvas canvas = surfaceHolder.lockCanvas();
            if (canvas != null) {
                synchronized (surfaceHolder) {
                    drawGrid(canvas);
                }
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    private void drawGrid(Canvas canvas) {
        // Clear the canvas
        canvas.drawColor(Color.WHITE);

        int width = canvas.getWidth();
        int height = canvas.getHeight();



        // Calculate cell size

        cellWidth = width / 11f;
        cellHeight = height / 9f;


        // uses the value that will make the chess board fit proper while still being squared
        cellDimensions = Math.min(cellWidth, cellHeight);

        capturedFieldRadius = (float) (0.4*cellDimensions);

        fieldDimensions = cellDimensions*9;

        // debugging
        Log.i("cellSize", "Cellwidth: "+cellWidth);
        Log.i("cellSize", "Cellheight: "+cellHeight);
        Log.i("cellSize", "Celldim: "+cellDimensions);

        // Initializing colors used
        Paint paintBlack = new Paint();
        Paint paintBackground = new Paint();
        Paint paintCapturedField = new Paint();
        paintBlack.setColor(Color.BLACK);
        paintBlack.setStrokeWidth(4);
        paintBackground.setColor(0xFF926211);
        paintCapturedField.setColor(0xd3d3d3d3);

        // add background
        canvas.drawRect(0,0,width,height, paintBlack);
        // add background color for board
        canvas.drawRect(cellDimensions,0, cellDimensions+fieldDimensions, fieldDimensions, paintBackground);

        // Draw vertical lines
        for (int i = 1; i <= 10; i++) { // start at 1-10 when adding captured pieces
            float x = i * cellDimensions;
            canvas.drawLine(x, 0, x, fieldDimensions, paintBlack);
        }

        // Draw horizontal lines
        for (int i = 0; i <= 9; i++) {
            float y = i * cellDimensions;
            canvas.drawLine(cellDimensions, y, cellDimensions+fieldDimensions, y, paintBlack);
        }

        // draw fields for captured pieces
        // Player 2
        for (int i = 0; i < 7; i++){
            float x = (float) 0.5 * cellDimensions;
            float y = (float) 0.5 * cellDimensions + i * cellDimensions;
            canvas.drawCircle(x,y,capturedFieldRadius, paintCapturedField);
        }
        // Player 1
        for (int i = 2; i < 9; i++){
            float x = (float) 10.5 * cellDimensions;
            float y = (float) 0.5 * cellDimensions + i * cellDimensions;
            canvas.drawCircle(x,y,capturedFieldRadius, paintCapturedField);
        }


        // Scale bitmaps if necessary
        scaleBitmapsIfNeeded();

        /**
         * External Citation
         * Date: 16 September 2024
         * Problem: could not figure out how to load game pieces
         * Resource: ChatGPT
         * Solution: I used their example code.
         * */

        // Draw all game pieces
        synchronized (gamePieces) {
            for (int i = 0; i < gamePieces.size(); i++) {
                GamePiece piece = gamePieces.get(i);
                Bitmap scaledBitmap = scaledBitmaps.get(i);

                // Ensure the position is within bounds
                int row = Math.max(0, Math.min(piece.getRow(), 8));
                int col = Math.max(0, Math.min(piece.getCol(), 8));

                // Calculate the position to draw the Bitmap
                float left = col * cellDimensions;
                float top = row * cellDimensions;

                // Draw the Bitmap on the canvas
                canvas.drawBitmap(scaledBitmap, cellDimensions+left, top, null);
            }
        }
    }

    private void scaleBitmapsIfNeeded() {
        if (scaledBitmaps.isEmpty() || scaledBitmaps.get(0).getWidth() != (int) cellDimensions) {
            scaledBitmaps.clear();
            synchronized (gamePieces) {
                for (GamePiece piece : gamePieces) {
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(piece.getBitmap(), (int) cellDimensions, (int) cellDimensions, true);
                    scaledBitmaps.add(scaledBitmap);
                }
            }
        }
    }
}
