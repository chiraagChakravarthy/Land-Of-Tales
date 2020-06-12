package combat_test;

import org.apache.commons.math3.special.Erf;

import java.awt.*;
import java.util.ArrayList;

public class Pixel {

    public short x, y;
    public float vX, vY;
    public byte color, time;
    private Magic magic;
    private ArrayList<Integer> indexes;
    private boolean collision;

    public Pixel(short x, short y, float vX, float vY, byte color, short time, Magic magic){
        this.x = x;
        this.y = y;
        this.vX = vX;
        this.vY = vY;
        this.color = color;
        this.time = (byte)(time+Byte.MIN_VALUE);
        this.magic = magic;
        indexes = new ArrayList<>();
        collision = true;
    }

    public void update(short index){
        time--;
        if(time==Byte.MIN_VALUE){
            magic.removePixel(index);
        } else {
            short oldX = x, oldY = y;
            x += (short)(Math.round(Erf.erfInv(Math.random()*2-1)+vX));
            y += (short)(Math.round(Erf.erfInv(Math.random()*2-1)+vY));
            if (collision)
                addLine(oldX, oldY, index);
        }
    }

    public void removeCollision(int index){
        for (int i : indexes) {
            magic.collisionGrid[i].remove(Short.valueOf((short) (index)));
        }
    }

    public void addLine(short oldX, short oldY, short val){
        indexes.clear();
        int dx = x-oldX,
                dy = y-oldY;

        if(Math.abs(dx)>Math.abs(dy)){
            int sgn = dx<0?-1:1;
            int abs = dx*sgn;
            int startX, startY;
            if(oldX < x){
                startX = oldX;
                startY = oldY;
            } else {
                startX = x;
                startY = y;
            }

            float slope = (float)dy/(float)dx;
            for (int i = 0; i < abs; i ++) {
                int x = i + startX+magic.getWidth()/2;
                int y = Math.round(i*slope + startY)+magic.getHeight()/2;
                if(x>=0&&x<magic.getWidth()&&y>=0&&y<magic.getHeight()) {
                    int index = y*magic.getWidth()+x;
                    indexes.add(index);
                    ArrayList<Short> indexList = magic.collisionGrid[index];
                    if(indexList==null){
                        magic.collisionGrid[index] = indexList = new ArrayList<>();
                    }
                    indexList.add(val);
                }
            }
        } else {
            int sgn = dy<0?-1:1;
            int abs = dy*sgn;
            int startX, startY;
            if(oldY < y){
                startX = oldX;
                startY = oldY;
            } else {
                startX = x;
                startY = y;
            }
            float slope = (float)dx/(float)dy;
            for (int i = 0; i < abs; i++) {
                int y = i + startY+magic.getHeight()/2;
                int x = Math.round(i*slope+ startX)+magic.getWidth()/2;
                if(x>=0&&x<magic.getWidth()&&y>=0&&y<magic.getHeight()) {
                    int index = y*magic.getWidth()+x;
                    indexes.add(index);
                    ArrayList<Short> indexList = magic.collisionGrid[index];
                    if(indexList==null){
                        magic.collisionGrid[index] = indexList = new ArrayList<>();
                    }
                    indexList.add(val);
                }
            }
        }
    }

    public byte getColor() {
        return color;
    }

    public Point getRenderCoord(){
        return new Point(x, y);
    }
}
