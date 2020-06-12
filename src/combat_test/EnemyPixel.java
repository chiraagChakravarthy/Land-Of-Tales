package combat_test;

import org.apache.commons.math3.special.Erf;

import java.awt.*;
import java.util.ArrayList;

public class EnemyPixel {
    public short x, y;
    public float vX, vY;
    private byte color, time;
    private Magic magic;
    private boolean collision;

    public EnemyPixel(short x, short y, float vX, float vY, byte color, short time, Magic magic){
        this.x = x;
        this.y = y;
        this.vX = vX;
        this.vY = vY;
        this.color = color;
        this.time = (byte)(time+Byte.MIN_VALUE);
        this.magic = magic;
        collision = true;
    }

    public void update(){
        time--;
        if(time == Byte.MIN_VALUE){
            remove();
        } else {
            short oldX = x, oldY = y;
            x += (short)(Math.round(Erf.erfInv(Math.random()*2-1)+vX));
            y += (short)(Math.round(Erf.erfInv(Math.random()*2-1)+vY));
            if(collision) {
                int collision = checkLine(new Point(oldX, oldY), new Point(x, y));
                if (collision != Integer.MAX_VALUE) {
                    Pixel collidedPixel = magic.getPixel(collision);
                    collidedPixel.removeCollision(collision);
                    collidedPixel.vX = vX = (this.vX + collidedPixel.vX)/2;
                    collidedPixel.vY = vY = (this.vY + collidedPixel.vY)/2;
                    //vX = -vX;
                    //vY = -vY;
                    time = (byte) Math.min(time, Byte.MIN_VALUE+30);
                    collidedPixel.time = (byte)Math.min(collidedPixel.time, Byte.MIN_VALUE+30);
                }
            }
        }
    }

    public int checkLine(Point p1, Point p2){
        int dx = p2.x-p1.x,
                dy = p2.y-p1.y;

        if(Math.abs(dx)>Math.abs(dy)){
            int sgn = dx<0?-1:1;
            int abs = dx*sgn;
            int startX, startY;
            if(p1.x < p2.x){
                startX = p1.x;
                startY = p1.y;
            } else {
                startX = p2.x;
                startY = p2.y;
            }

            float slope = (float)dy/(float)dx;
            for (int i = 0; i < abs; i ++) {
                int x = i + startX+magic.getWidth()/2;
                int y = Math.round(i*slope + startY)+magic.getHeight()/2;
                if(x>=0&&x<magic.getWidth()&&y>=0&&y<magic.getHeight()) {
                    int index = y*magic.getWidth()+x;
                    ArrayList<Short> indexes = magic.collisionGrid[index];
                    if (indexes != null && indexes.size() != 0){
                        return indexes.get(0);
                    }
                }
            }
        } else {
            int sgn = dy<0?-1:1;
            int abs = dy*sgn;
            int startX, startY;
            if(p1.y < p2.y){
                startX = p1.x;
                startY = p1.y;
            } else {
                startX = p2.x;
                startY = p2.y;
            }
            float slope = (float)dx/(float)dy;
            for (int i = 0; i < abs; i++) {
                int y = i + startY+magic.getHeight()/2;
                int x = Math.round(i*slope+ startX)+magic.getWidth()/2;
                if(x>=0&&x<magic.getWidth()&&y>=0&&y<magic.getHeight()) {
                    int index = y*magic.getWidth()+x;
                    ArrayList<Short> indexes = magic.collisionGrid[index];
                    if (indexes != null && indexes.size() != 0){
                        return indexes.get(0);
                    }
                }
            }
        }
        return Integer.MAX_VALUE;//only returned to indicate no line crossing
    }

    public void remove(){
        magic.removeEnemyPixel(this);
    }

    public short getX() {
        return x;
    }

    public short getY() {
        return y;
    }

    public float getvX() {
        return vX;
    }

    public float getvY() {
        return vY;
    }

    public Point getRenderCoord(){
        double rand = 0;
        return new Point(x, y);
    }

    public byte getColor() {
        return color;
    }
}
