package combat_test;

import org.apache.commons.math3.special.Erf;

import java.awt.*;
import java.util.ArrayList;

public class MagicPixel {
    private static final Color[] colors;
    static {
        colors = new Color[]{
                new Color(0, 128, 255),
                new Color(0, 0, 255),
                new Color(255, 0, 0),
                new Color(128, 0, 0)
        };
    }

    private short x, y;
    private float vX, vY;
    private byte color, time, t;
    private Magic magic;
    private boolean enemy;
    private ArrayList<Integer> indexes;

    public MagicPixel(short x, short y, float vX, float vY, byte color, short time, boolean enemy, Magic magic){
        this.x = x;
        this.y = y;
        this.vX = vX;
        this.vY = vY;
        this.color = color;
        this.time = (byte)(time-128);
        this.magic = magic;
        this.enemy = enemy;
        t = Byte.MIN_VALUE;
    }

    public void update(short index){
        t++;
        if(t >= time){
            remove(index);
        } else {
            short oldX = x, oldY = y;
            x += (short)(Math.round(Erf.erfInv(Math.random()*2-1)+vX));
            y += (short)(Math.round(Erf.erfInv(Math.random()*2-1)+vY));
            if (enemy){
                int collision = magic.checkLine(new Point(oldX, oldY), new Point(x, y));
                if(collision != Integer.MAX_VALUE){
                    magic.removeEnemyPixel(this);
                    magic.removePixel(collision);
                }
            } else {
                indexes = magic.addLine(new Point(oldX, oldY), new Point(x, y), index);
            }
        }
    }

    public void remove(int index){
        if(enemy){
            magic.removeEnemyPixel(this);
        } else {
            ArrayList<Short>[] collisionGrid = magic.getCollisionGrid();
            for (int i : indexes) {
                collisionGrid[i].remove(Short.valueOf((short) (index - Short.MIN_VALUE)));
            }
            magic.removePixel(this);
        }
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

    public Color getColor() {
        return colors[color+(enemy?2:0)];
    }

    public Point getRenderCoord(){
        double rand = 0;
        return new Point(x, y);
    }
}