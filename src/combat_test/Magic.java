package combat_test;

import org.apache.commons.math3.special.Erf;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Arrays;

public class Magic {
    private ArrayList<MagicPixel> pixels, updatedPixels, enemyPixels, updatedEnemyPixels;
    private boolean mousePressed;
    private BufferedImage layer;
    private ArrayList<Short>[] collisionGrid;

    public Magic(){
        pixels = new ArrayList<>();
        updatedPixels = new ArrayList<>();
        enemyPixels = new ArrayList<>();
        updatedEnemyPixels = new ArrayList<>();
        mousePressed = false;
        layer = new BufferedImage(Game.getInstance().getGameWidth()/5, Game.getInstance().getGameHeight()/5, BufferedImage.TYPE_INT_RGB);
    }

    public void update(){
        collisionGrid = new ArrayList[layer.getWidth()*layer.getHeight()];

        for (int i = 0; i < pixels.size(); i++) {
            pixels.get(i).update((short)(i-Short.MIN_VALUE));
        }

        for (MagicPixel pixel: enemyPixels){
            pixel.update((short)0);
        }

        if(mousePressed){
            Point mouseLocation = Game.mouseLocation();
            double o = mouseLocation.y-Game.getInstance().getGameHeight()/2,
                    a = mouseLocation.x-Game.getInstance().getGameWidth(),
                    t = Math.atan2(o, a);
            for (int i = 0; i < 100; i++) {
                double t1 = t+(Math.random()-0.5)/5;
                updatedPixels.add(new MagicPixel((short)(Math.round(Erf.erfInv(Math.random()*2-1))+Game.getInstance().getGameWidth()/10), (short)(Math.round(Erf.erfInv(Math.random()*2-1))), (float) (Math.cos(t)*5), (float)(Math.sin(t)*5), (byte)(Math.random()<.5?1:0), (short)(150), false, this));
            }
        }
        for (int i = 0; i < 100; i++) {
            double t1 = 0;
            updatedEnemyPixels.add(new MagicPixel((short)(Math.round(Erf.erfInv(Math.random()*2-1))-Game.getInstance().getGameWidth()/10), (short)(Math.round(Erf.erfInv(Math.random()*2-1))), (float) (Math.cos(t1)*5), (float)(Math.sin(t1)*5), (byte)(Math.random()<.5?1:0), (short)(150), true, this));
        }
        pixels = (ArrayList<MagicPixel>) updatedPixels.clone();
        enemyPixels = (ArrayList<MagicPixel>) updatedEnemyPixels.clone();
    }

    public void render(Graphics2D g){
        /*int[] pixels = ((DataBufferInt)layer.getRaster().getDataBuffer()).getData();
        for (int i = 0; i < lines.length; i++) {
            pixels[i] = (lines[i]==null || lines[i].size()==0? 0 : 0x0000ff);
        }*/
        for (int i = 0; i < pixels.size(); i++) {
            MagicPixel pixel = pixels.get(i);
            Color color = pixel.getColor();
            Point renderCoord = pixel.getRenderCoord();
            int renderX = renderCoord.x+layer.getWidth()/2,
                    renderY =  renderCoord.y+layer.getHeight()/2;
            if(renderX>=0&&renderX<layer.getWidth()&&renderY>=0&&renderY<layer.getHeight()) {
                layer.setRGB(renderX, renderY, color.getRGB());
            }
        }

        for (int i = 0; i < enemyPixels.size(); i++) {
            MagicPixel pixel = enemyPixels.get(i);
            Color color = pixel.getColor();
            Point renderCoord = pixel.getRenderCoord();
            int renderX = renderCoord.x+layer.getWidth()/2,
                    renderY =  renderCoord.y+layer.getHeight()/2;
            if(renderX>=0&&renderX<layer.getWidth()&&renderY>=0&&renderY<layer.getHeight()) {
                layer.setRGB(renderX, renderY, color.getRGB());
            }
        }
        g.drawImage(layer, 0, 0, Game.getInstance().getGameWidth(), Game.getInstance().getGameHeight(), null);
        layer = new BufferedImage(Game.getInstance().getGameWidth()/5, Game.getInstance().getGameHeight()/5, BufferedImage.TYPE_INT_RGB);
    }

    public void removeEnemyPixel(MagicPixel pixel){
        updatedEnemyPixels.remove(pixel);
    }

    public void removePixel(MagicPixel pixel){
        updatedPixels.remove(pixel);
    }

    public void mousePressed(MouseEvent e){
        if(e.getButton()==1){
            mousePressed = true;
        }
    }

    public void mouseReleased(MouseEvent e){
        if(e.getButton()==1){
            mousePressed = false;
        }
    }

    public ArrayList<Integer> addLine(Point p1, Point p2, short val){
        /*int dx = p2.x-p1.x,
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
            ArrayList<Integer> indexes = new ArrayList<>();
            for (int i = 0; i < abs; i ++) {
                int x = i + startX+layer.getWidth()/2;
                int y = Math.round(i*slope + startY)+layer.getHeight()/2;
                if(x>=0&&x<layer.getWidth()&&y>=0&&y<layer.getHeight()) {
                    int index = y*layer.getWidth()+x;
                    indexes.add(index);
                    ArrayList<Short> indexList = collisionGrid[index];
                    if(indexList==null){
                        collisionGrid[index] = indexList = new ArrayList<>();
                    }
                    indexList.add(val);
                }
            }
            return indexes;
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
            ArrayList<Integer> indexes = new ArrayList<>();
            for (int i = 0; i < abs; i++) {
                int y = i + startY+layer.getHeight()/2;
                int x = Math.round(i*slope+ startX)+layer.getWidth()/2;
                if(x>=0&&x<layer.getWidth()&&y>=0&&y<layer.getHeight()) {
                    int index = y*layer.getWidth()+x;
                    indexes.add(index);
                    ArrayList<Short> indexList = collisionGrid[index];
                    if(indexList==null){
                        collisionGrid[index] = indexList = new ArrayList<>();
                    }
                    indexList.add(val);
                }
            }
            return indexes;
        }*/
        int index = (p2.y+layer.getHeight()/2)*layer.getWidth()+p2.x+layer.getWidth()/2;
        if(p2.x>-layer.getWidth()/2&&p2.x<=layer.getWidth()/2&&p2.y>-layer.getHeight()/2&&p2.y<=layer.getHeight()/2){
            if(collisionGrid[index]==null){
                collisionGrid[index] = new ArrayList<>();
            }
            collisionGrid[index].add(val);
        }
        return new ArrayList<>(Arrays.asList(index));
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
                int x = i + startX+layer.getWidth()/2;
                int y = Math.round(i*slope + startY)+layer.getHeight()/2;
                if(x>=0&&x<layer.getWidth()&&y>=0&&y<layer.getHeight()) {
                    int index = y*layer.getWidth()+x;
                    ArrayList<Short> indexes = collisionGrid[index];
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
                int y = i + startY+layer.getHeight()/2;
                int x = Math.round(i*slope+ startX)+layer.getWidth()/2;
                if(x>=0&&x<layer.getWidth()&&y>=0&&y<layer.getHeight()) {
                    int index = y*layer.getWidth()+x;
                    ArrayList<Short> indexes = collisionGrid[index];
                    if (indexes != null && indexes.size() != 0){
                        return indexes.get(0);
                    }
                }
            }
        }
        return Integer.MAX_VALUE;//only returned to indicate no line crossing
    }

    public ArrayList<Short>[] getCollisionGrid() {
        return collisionGrid;
    }

    public void removePixel(int index) {
        pixels.get(index-Short.MIN_VALUE).remove(index);
    }
}