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
    private ArrayList<Pixel> pixels, removedPixels;
    private ArrayList<EnemyPixel> enemyPixels, removedEnenyPixels;
    private boolean mousePressed;
    private BufferedImage layer;
    public ArrayList<Short>[] collisionGrid;
    int time = 0;

    public Magic(){
        pixels = new ArrayList<>();
        removedPixels = new ArrayList<>();
        enemyPixels = new ArrayList<>();
        removedEnenyPixels = new ArrayList<>();
        mousePressed = false;
        layer = new BufferedImage(Game.getInstance().getGameWidth()/5, Game.getInstance().getGameHeight()/5, BufferedImage.TYPE_INT_RGB);
    }

    public void update(){
        collisionGrid = new ArrayList[layer.getWidth()*layer.getHeight()];

        for (int i = 0; i < pixels.size(); i++) {
            pixels.get(i).update((short)(i+Short.MIN_VALUE));
        }

        for (EnemyPixel pixel: enemyPixels){
            pixel.update();
        }

        Point mouseLocation = Game.mouseLocation();
        double o = mouseLocation.y-Game.getInstance().getGameHeight()/2,
                a = mouseLocation.x-Game.getInstance().getGameWidth(),
                t = Math.atan2(o, a);
        if(mousePressed){
            for (int i = 0; i < 100; i++) {
                double t1 = t+(Math.random()-0.5)/5;
                pixels.add(new Pixel((short)(Math.round(Erf.erfInv(Math.random()*2-1))+Game.getInstance().getGameWidth()/10), (short)(Math.round(Erf.erfInv(Math.random()*2-1))), (float) (Math.cos(t)*5), (float)(Math.sin(t)*5), (byte)(Math.random()<.5?1:0), (short)(150), this));
            }
        }
        time++;
        for (int i = 0; i < 100; i++) {
            double t1 = Math.sin(time/60.0);
            enemyPixels.add(new EnemyPixel((short)(Math.round(Erf.erfInv(Math.random()*2-1))-Game.getInstance().getGameWidth()/10), (short)(Math.round(Erf.erfInv(Math.random()*2-1))), (float) (Math.cos(t1)*5), (float)(Math.sin(t1)*5), (byte)(Math.random()<.5?1:0), (short)(150), this));
        }
        pixels.removeAll(removedPixels);
        removedPixels.clear();
        enemyPixels.removeAll(removedEnenyPixels);
        removedEnenyPixels.clear();
    }

    public void render(Graphics2D g){

        for(Pixel pixel : pixels){
            int renderX = pixel.x+layer.getWidth()/2;
            int renderY = pixel.y+layer.getHeight()/2;
            if(renderX >= 0 && renderY >= 0 && renderX < layer.getWidth() && renderY < layer.getHeight()){
                int color = pixel.getColor()==0?0x0080ff:0x0000ff;
                layer.setRGB(renderX, renderY, color);
            }
        }

        boolean[] addressedPixels = new boolean[layer.getWidth()*layer.getHeight()];

        for(EnemyPixel pixel : enemyPixels){
            int renderX = pixel.x+layer.getWidth()/2;
            int renderY = pixel.y+layer.getHeight()/2;
            int color = pixel.getColor() == 0 ? 0xff0000 : 0x800000;
            if(renderX >= 0 && renderY >= 0 && renderX < layer.getWidth() && renderY < layer.getHeight()){
                if(!addressedPixels[renderY*layer.getWidth()+renderX]){
                    addressedPixels[renderY*layer.getWidth()+renderX] = true;
                    if (layer.getRGB(renderX, renderY) == -16777216) {
                        layer.setRGB(renderX, renderY, color);
                    } else if (Math.random() < 0.5) {
                        layer.setRGB(renderX, renderY, color);
                    }
                }
            }
        }

        g.drawImage(layer, 0, 0, Game.getInstance().getGameWidth(), Game.getInstance().getGameHeight(), null);
        layer = new BufferedImage(Game.getInstance().getGameWidth()/5, Game.getInstance().getGameHeight()/5, BufferedImage.TYPE_INT_RGB);
    }

    public void removeEnemyPixel(EnemyPixel pixel){
        removedEnenyPixels.add(pixel);
    }

    public void removePixel(Pixel pixel){
        removedPixels.add(pixel);
    }

    public void mousePressed(MouseEvent e){
        if(e.getButton()==1){
            mousePressed = true;
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == 1) {
            mousePressed = false;
        }
    }

    public void removePixel(int index) {
        pixels.remove(index-Short.MIN_VALUE);
    }

    public int getWidth(){
        return layer.getWidth();
    }

    public int getHeight(){
        return layer.getHeight();
    }

    public Pixel getPixel(int i) {
        return pixels.get(i-Short.MIN_VALUE);
    }
}