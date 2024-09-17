package edu.up.cs301shogi2024;

import android.graphics.Bitmap;

public class GamePiece {
    private Bitmap bitmap;
    private int row;
    private int col;

    public GamePiece(Bitmap bitmap, int row, int col) {
        this.bitmap = bitmap;
        this.row = row;
        this.col = col;
    }

    // Getters and setters
    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }
}
