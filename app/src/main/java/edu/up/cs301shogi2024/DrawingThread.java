package edu.up.cs301shogi2024;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.List;

public class DrawingThread extends Thread {
    private SurfaceHolder surfaceHolder;
    private boolean running = false;
    private boolean needsRedraw = false;
    private final Object lock = new Object();

    private List<GamePiece> gamePieces;
    private List<Bitmap> scaledBitmaps;
    private float cellWidth;
    private float cellHeight;

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
        canvas.drawColor(android.graphics.Color.WHITE);

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // Calculate cell size
        cellWidth = width / 9f;
        cellHeight = height / 9f;

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

        // Scale bitmaps if necessary
        scaleBitmapsIfNeeded();

        // Draw all game pieces
        synchronized (gamePieces) {
            for (int i = 0; i < gamePieces.size(); i++) {
                GamePiece piece = gamePieces.get(i);
                Bitmap scaledBitmap = scaledBitmaps.get(i);

                // Ensure the position is within bounds
                int row = Math.max(0, Math.min(piece.getRow(), 8));
                int col = Math.max(0, Math.min(piece.getCol(), 8));

                // Calculate the position to draw the Bitmap
                float left = col * cellWidth;
                float top = row * cellHeight;

                // Draw the Bitmap on the canvas
                canvas.drawBitmap(scaledBitmap, left, top, null);
            }
        }
    }

    private void scaleBitmapsIfNeeded() {
        if (scaledBitmaps.isEmpty() || scaledBitmaps.get(0).getWidth() != (int) cellWidth) {
            scaledBitmaps.clear();
            synchronized (gamePieces) {
                for (GamePiece piece : gamePieces) {
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(piece.getBitmap(), (int) cellWidth, (int) cellHeight, true);
                    scaledBitmaps.add(scaledBitmap);
                }
            }
        }
    }
}
