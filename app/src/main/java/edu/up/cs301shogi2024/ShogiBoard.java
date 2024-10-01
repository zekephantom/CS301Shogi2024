package edu.up.cs301shogi2024;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Bitmap;
import java.util.*;


public class ShogiBoard extends SurfaceView implements SurfaceHolder.Callback {
    private DrawingThread drawingThread;
    private List<GamePiece> gamePieces;
    private List<GamePiece> capturedPieces;


    public ShogiBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);

        // Initialize the list of game pieces
        gamePieces = new ArrayList<>();
        capturedPieces = new ArrayList<>();
        loadGamePieces(context);
    }

    private void loadGamePieces(Context context) {
        // Load Bitmaps for different pieces
        Bitmap kingLower = BitmapFactory.decodeResource(getResources(), R.drawable.kinglower);
        Bitmap rook = BitmapFactory.decodeResource(getResources(), R.drawable.rook);
        Bitmap prom_rook = BitmapFactory.decodeResource(getResources(), R.drawable.prom_rook);
        Bitmap bishop = BitmapFactory.decodeResource(getResources(), R.drawable.bishop);
        Bitmap prom_bishop = BitmapFactory.decodeResource(getResources(), R.drawable.prom_bishop);
        Bitmap goldGen = BitmapFactory.decodeResource(getResources(), R.drawable.goldgen);
        Bitmap silverGen = BitmapFactory.decodeResource(getResources(), R.drawable.silvergen);
        Bitmap prom_silver = BitmapFactory.decodeResource(getResources(), R.drawable.prom_silver);
        Bitmap knight = BitmapFactory.decodeResource(getResources(), R.drawable.knight);
        Bitmap prom_knight = BitmapFactory.decodeResource(getResources(), R.drawable.prom_knight);
        Bitmap lance = BitmapFactory.decodeResource(getResources(), R.drawable.lance);
        Bitmap prom_lance = BitmapFactory.decodeResource(getResources(), R.drawable.prom_lance);
        Bitmap pawn = BitmapFactory.decodeResource(getResources(), R.drawable.pawn);
        Bitmap prom_pawn = BitmapFactory.decodeResource(getResources(), R.drawable.prom_pawn);


        // bottom side players pieces
        gamePieces.add(new GamePiece(pawn, 6, 0));
        gamePieces.add(new GamePiece(pawn, 6, 1));
        gamePieces.add(new GamePiece(pawn, 5, 2));
        gamePieces.add(new GamePiece(pawn, 6, 3));
        gamePieces.add(new GamePiece(pawn, 6, 4));
        gamePieces.add(new GamePiece(pawn, 6, 5));
        gamePieces.add(new GamePiece(pawn, 5, 6));
        gamePieces.add(new GamePiece(pawn, 6, 7));
        gamePieces.add(new GamePiece(pawn, 6, 8));
        gamePieces.add(new GamePiece(lance, 8, 0));
        gamePieces.add(new GamePiece(lance, 8, 8));
        gamePieces.add(new GamePiece(prom_knight, 2, 2));
        gamePieces.add(new GamePiece(knight, 8, 7));
        gamePieces.add(new GamePiece(silverGen, 8, 2));
        gamePieces.add(new GamePiece(silverGen, 8, 6));
        gamePieces.add(new GamePiece(goldGen, 8, 3));
        gamePieces.add(new GamePiece(goldGen, 8, 5));
        gamePieces.add(new GamePiece(kingLower, 8, 4));
        gamePieces.add(new GamePiece(bishop, 5, 3));
        gamePieces.add(new GamePiece(rook, 7, 7));

        // top side players pieces
        gamePieces.add(new GamePiece(enemy(pawn), 2, 0));
        gamePieces.add(new GamePiece(enemy(pawn), 2, 1));
        gamePieces.add(new GamePiece(enemy(pawn), 2, 3));
        gamePieces.add(new GamePiece(enemy(pawn), 3, 4));
        gamePieces.add(new GamePiece(enemy(pawn), 2, 5));
        gamePieces.add(new GamePiece(enemy(pawn), 2, 6));
        gamePieces.add(new GamePiece(enemy(pawn), 2, 7));
        gamePieces.add(new GamePiece(enemy(pawn), 2, 8));
        gamePieces.add(new GamePiece(enemy(lance), 0, 0));
        gamePieces.add(new GamePiece(enemy(lance), 0, 8));
        gamePieces.add(new GamePiece(enemy(knight), 0, 1));
        gamePieces.add(new GamePiece(enemy(knight), 0, 7));
        gamePieces.add(new GamePiece(enemy(silverGen), 1, 2));
        gamePieces.add(new GamePiece(enemy(silverGen), 1, 6));
        gamePieces.add(new GamePiece(enemy(goldGen), 1, 4));
        gamePieces.add(new GamePiece(enemy(goldGen), 1, 5));
        gamePieces.add(new GamePiece(enemy(kingLower), 0, 4));
        gamePieces.add(new GamePiece(enemy(bishop), 1, 7));
        gamePieces.add(new GamePiece(enemy(rook), 1, 3));

        // captured pieces (row: maybe 0 for left/upper pieces and 1 for right/lower pieces
            // future automate that specific piece automatically has location specified
        capturedPieces.add(new GamePiece(pawn, 8, 1));
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Start the drawing thread and pass the list of game pieces
        drawingThread = new DrawingThread(getHolder(), gamePieces, capturedPieces);
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

    /**
     *External Citation
     *Date: 19 September 2024
     *Problem: Couldn't figure out how to rotate a bitmap
     *Resource: https://stackoverflow.com/a/29982596
     *Solution: I used the example code from this answer.
     */
    // rotates bitmap 180 degrees to be used as a "enemy" piece
    public static Bitmap enemy(Bitmap source)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(180);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
