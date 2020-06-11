package combat_test;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class CombatTest {
    private Point2D position;
    private Magic magic;

    public CombatTest(){
        position = new Point2D.Double();
        position.setLocation(Game.window.getWidth()/2, Game.window.getHeight()/2);
        magic = new Magic();
    }

    public void update(){
        magic.update();
    }

    public void render(Graphics2D g){
        magic.render(g);
    }

    public void mousePressed(MouseEvent e){
        magic.mousePressed(e);
    }

    public void mouseReleased(MouseEvent e){
        magic.mouseReleased(e);
    }
}
