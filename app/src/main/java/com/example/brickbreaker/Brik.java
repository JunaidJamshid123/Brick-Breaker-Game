package com.example.brickbreaker;

public class Brik {
    private boolean isvisible;
    public int row,col,width,height;
    public Brik(){

    }

    public Brik(int row, int col, int width, int height) {
        this.isvisible = true;
        this.row = row;
        this.col = col;
        this.width = width;
        this.height = height;
    }
    public void setVisible(){
        isvisible = false;
    }
    public boolean getVisible(){
        return isvisible;
    }
}
