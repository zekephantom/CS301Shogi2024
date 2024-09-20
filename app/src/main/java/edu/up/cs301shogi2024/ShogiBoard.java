package edu.up.cs301shogi2024;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Bitmap;
import java.util.*;

public class ShogiBoard extends SurfaceView implements SurfaceHolder.Callback {

    private DrawingThread drawingThread;
    private List<GamePiece> gamePieces;

    public ShogiBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);

        // Initialize the list of game pieces
        gamePieces = new ArrayList<>();
        loadGamePieces(context);
    }

    private void loadGamePieces(Context context) {
        // Load Bitmaps for different pieces
        Bitmap kinglower = BitmapFactory.decodeResource(getResources(), R.drawable.kinglower);
        Bitmap rook = BitmapFactory.decodeResource(getResources(), R.drawable.rook);
        Bitmap prom_rook = BitmapFactory.decodeResource(getResources(), R.drawable.prom_rook);
        Bitmap bishop = BitmapFactory.decodeResource(getResources(), R.drawable.bishop);
        Bitmap prom_bishop = BitmapFactory.decodeResource(getResources(), R.drawable.prom_bishop);
        Bitmap goldgen = BitmapFactory.decodeResource(getResources(), R.drawable.goldgen);
        Bitmap silvergen = BitmapFactory.decodeResource(getResources(), R.drawable.silvergen);
        Bitmap prom_silver = BitmapFactory.decodeResource(getResources(), R.drawable.prom_silver);
        Bitmap knight = BitmapFactory.decodeResource(getResources(), R.drawable.knight);
        Bitmap prom_knight = BitmapFactory.decodeResource(getResources(), R.drawable.prom_knight);
        Bitmap lance = BitmapFactory.decodeResource(getResources(), R.drawable.lance);
        Bitmap prom_lance = BitmapFactory.decodeResource(getResources(), R.drawable.prom_lance);
        Bitmap pawn = BitmapFactory.decodeResource(getResources(), R.drawable.pawn);
        Bitmap prom_pawn = BitmapFactory.decodeResource(getResources(), R.drawable.prom_pawn);


        gamePieces.add(new GamePiece(pawn, 6, 0));
        gamePieces.add(new GamePiece(pawn, 6, 1));
        gamePieces.add(new GamePiece(pawn, 5, 2));
        gamePieces.add(new GamePiece(pawn, 6, 3));
        gamePieces.add(new GamePiece(pawn, 6, 4));
        gamePieces.add(new GamePiece(pawn, 6, 5));
        gamePieces.add(new GamePiece(pawn, 5, 6));
        gamePieces.add(new GamePiece(pawn, 6, 7));
        gamePieces.add(new GamePiece(pawn, 6, 8));

        // Create GamePiece instances and add them to the list
        gamePieces.add(new GamePiece(lance, 8, 0));
        gamePieces.add(new GamePiece(lance, 8, 8));
        gamePieces.add(new GamePiece(prom_knight, 2, 2));
        gamePieces.add(new GamePiece(knight, 8, 7));
        gamePieces.add(new GamePiece(silvergen, 8, 2));
        gamePieces.add(new GamePiece(silvergen, 8, 6));
        gamePieces.add(new GamePiece(goldgen, 8, 3));
        gamePieces.add(new GamePiece(goldgen, 8, 5));
        gamePieces.add(new GamePiece(kinglower, 8, 4));
        gamePieces.add(new GamePiece(bishop, 5, 3));
        gamePieces.add(new GamePiece(rook, 7, 7));
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Start the drawing thread and pass the list of game pieces
        drawingThread = new DrawingThread(getHolder(), gamePieces);
        drawingThread.setRunning(true);
        drawingThread.start();
        drawingThread.requestRedraw(); // Initial draw
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (drawingThread != null) {
            drawingThread.setRunning(false);
            drawingThread.requestRedraw(); // Wake the thread if waiting
            boolean retry = true;
            while (retry) {
                try {
                    drawingThread.join();
                    retry = false;
                } catch (InterruptedException e) {
                    // Retry stopping the thread
                }
            }
            drawingThread = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Handle surface changes if necessary
    }
}
