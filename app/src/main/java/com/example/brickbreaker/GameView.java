package com.example.brickbreaker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;
import java.util.logging.LogRecord;

public class GameView extends View {

    Context context;
    float ballx,bally;
    Velocity velocity = new Velocity(25,25);
    Handler handler;
    final long UPDATE_MILLIS = 30;
    Runnable runnable;
    Paint healthpaint = new Paint();
    Paint brikPaint = new Paint();
    Paint textPaint = new Paint();
    float Textsize = 120;
    float paddlex,paddley;
    float oldx,oldpaddlex;
    int points = 0;
    int life = 3;
    Bitmap ball,paddle;
    int dwidth,dHieght;
    int ballwidth,ballhight;
    MediaPlayer Mhit,Mmiss,Mbreak;
    Random random;
    Brik[] brik = new Brik[30];
    int numBrik = 0;
    int brokenBrik = 0;
    boolean gameOver = false;

    public GameView(Context context) {
        super(context);
        this.context = context;
        ball = BitmapFactory.decodeResource(getResources(),R.drawable.ball2);
        paddle = BitmapFactory.decodeResource(getResources(),R.drawable.paddle2);
        //handler = new Handler();
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
               invalidate();
            }
        };
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(Textsize);
        textPaint.setTextAlign(Paint.Align.LEFT);
        healthpaint.setColor(Color.GREEN);
        brikPaint.setColor(Color.argb(255,249,129,0));
        Display display  = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dwidth = size.x;
        dHieght = size.y;
        random = new Random();
        ballx = random.nextInt(dwidth-50);
        bally = dHieght/3;
        paddley = (dHieght*4)/5;
        paddlex = dwidth/2 - paddle.getWidth()/2;
        ballwidth = ball.getWidth();
        ballhight = ball.getHeight();
        createBriks();

    }
    private void createBriks(){
        int brickWidth = dwidth/8;
        int brickhight = dHieght/16;
        for(int col = 0;col<8;col++){
            for(int row = 0;row<3;row++){
                brik[numBrik] = new Brik(row,col,brickWidth,brickhight);
                numBrik++;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        ballx+=velocity.getX();
        bally+=velocity.getY();
        if((ballx>=dwidth-ball.getWidth()) || ballx<=0){
                velocity.setX(velocity.getX()* -1);
        }

        if(bally<=0){
            velocity.setY(velocity.getY()* -1);
        }
        if(bally> paddley+paddle.getHeight()) {
            ballx = 1 + random.nextInt(dwidth - ball.getWidth() - 1);
            bally = dHieght / 3;
            velocity.setX(xVelocity());
            velocity.setY(32);
            life--;
            if (life == 0) {
                gameOver = true;
                lunchGameOver();
            }
        }
            if(((ballx+ball.getWidth())>=paddlex) &&(ballx<=paddlex+paddle.getWidth())&&(bally+ball.getHeight()>=paddley)
            &&(bally+ball.getHeight()<=paddley+paddle.getHeight())){
                velocity.setX(velocity.getX()+1);
                velocity.setY((velocity.getY()+1)* -1);
            }

            canvas.drawBitmap(ball,ballx,bally,null);
            canvas.drawBitmap(paddle,paddlex,paddley,null);
            for(int i=0;i<numBrik;i++){
                if(brik[i].getVisible()){
                    canvas.drawRect(brik[i].col*brik[i].width+1,brik[i].row*brik[i].height+1,brik[i].col*brik[i].width+brik[i].width-1,
                            brik[i].row*brik[i].height+brik[i].height-1,brikPaint);
                }
            }
            canvas.drawText(""+points,20,Textsize,textPaint);
            if(life==2){
                healthpaint.setColor(Color.YELLOW);
            }
            else if(life==1){
                healthpaint.setColor(Color.RED);
            }
            canvas.drawRect(dwidth-20,30,dwidth-200+60*life,80,healthpaint);
            for(int i=0;i<numBrik;i++){
                if(brik[i].getVisible()){
                    if(ballx+ballwidth>=brik[i].col*brik[i].width
                    && ballx<=brik[i].col*brik[i].width+brik[i].width
                    && bally<=brik[i].row*brik[i].height+brik[i].height
                    && bally>=brik[i].row* brik[i].height){
                        velocity.setY((velocity.getY()+1)* -1);
                        brik[i].setVisible();
                        points+=10;
                        brokenBrik++;
                        if(brokenBrik==24){
                            lunchGameOver();
                        }
                    }
                }
            }

            if(brokenBrik==numBrik){
                gameOver = true;
            }
            if(!gameOver){
                handler.postDelayed(runnable,UPDATE_MILLIS);
            }
        }


    @Override
    public boolean onTouchEvent(MotionEvent event){

        float tuchx = event.getX();
        float tuchy = event.getY();
        if(tuchy>=paddley){
            int action = event.getAction();
            if(action==MotionEvent.ACTION_DOWN){
                oldx = event.getX();
                oldpaddlex = paddlex;
            }

            if(action==MotionEvent.ACTION_MOVE){
                float shift = oldx - tuchx;
                float newPaddlex = oldpaddlex - shift;
                if(newPaddlex<=0){
                    paddlex = 0;
                }else if(newPaddlex>=dwidth - paddle.getWidth()){
                     paddlex = dwidth - paddle.getWidth();

                }else{
                    paddlex = newPaddlex;
                }
            }
        }
        return true;
    }

    private void lunchGameOver(){
        handler.removeCallbacksAndMessages(null);
        Intent intent = new Intent(context,GameOver.class);
        intent.putExtra("points",points);
        context.startActivity(intent);
        ((Activity) context).finish();

    }

    private int xVelocity(){
        int[] values = {-35,-30,-25,25,30,35};
        int index = random.nextInt(6);
        return values[index];
    }
}
