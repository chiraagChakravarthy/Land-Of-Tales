package combat_test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener {
    private static Game instance;

    public static final String TITLE = "War Of Circles";
    //This is the main class. The entire game is ran through this single class.
    private static final long serialVersionUID = 1L;
    public static Window window;
    private static Thread thread;
    private boolean running;
    private static long runningTime = 0;
    private static Point previousMouseLocation;

    private CombatTest combatTest;
    int frames;

    private Game() {
        window = new Window(TITLE, 2, this);
        addKeyListener(this);
        addMouseListener(this);
        requestFocus();

        previousMouseLocation = mouseLocation();
        running = true;
        thread = new Thread(this);
        frames = 0;
        thread.start();
    }

    public static void main(String[] args) {
        instance = new Game();
    }

    private void start(){
        combatTest = new CombatTest();
    }

    public void run() {

        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        int ticks = 60;
        double ns = 1000000000 / ticks;
        double delta = 0;

        int frames = 0;
        int updates = 0;
        //Allows for the logging of the ticks and frames each second
        //Game Loop\\
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        start();
        while (running)
        //Boolean which controls the running of the game loop. Were it to equal false, the game would simply freeze.
        {
            /////////////////////////////
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta > 1) {
                tick();
                updates++;
                delta--;
            }
            render();
            frames++;
            /////////////////////////////
            //A tick is the game's equivalent of an instant. The code above allows time to be constant in a loop that varies
            //in the length of each iteration

            if (System.currentTimeMillis() - timer >= 1000) {
                System.out.println("FPS: " + frames + ", Ticks: " + updates);
                updates = 0;
                this.frames = frames;
                frames = 0;
                timer += 1000;
                //Logs the Frames and the ticks that have passed since the last logging. The minimum time between each
                //logging is a second. (The max being however long the tick and drawTo take to process), so the actual
                //message being logged is a tad misleading
            }
        }
        //Game Loop\\
        stop();
    }

    private void stop() {
        //A method to stop the game loop and kill the game thread.
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void tick() {
        combatTest.update();
        Point mouse = mouseLocation();
        if(mouse.x!=previousMouseLocation.x || mouse.y!=previousMouseLocation.y){
            previousMouseLocation = (Point) mouse.clone();
            mouseMoved();
        }
        runningTime++;
    }

    public void render() {
        BufferStrategy bs = getBufferStrategy();
        //Instead of drawing directly to the canvas, drawing to a buffer strategy allows for a concept called triple buffering
        if (bs == null) {
            createBufferStrategy(3);
            //triple buffering in the long term greatly increases performance. Instead of replacing every pixel, triple buffering
            //only changes the pixels that weren't present before. It also searches for and remembers patterns in a single runtime
            //iteration.
            return;
        }
        Graphics2D g = (Graphics2D)bs.getDrawGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, window.getWidth(), window.getHeight());
        combatTest.render(g);
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(40.0f));
        g.drawString(String.valueOf(frames), 100, 100);
        g.dispose();
        bs.show();
    }

    public void keyTyped(KeyEvent e) {
        //Irrelevant to program
    }

    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();

    }

    public void keyReleased(KeyEvent e) {
        int k = e.getKeyCode();
        //k is an integer representing the key that was released

    }

    public void mouseClicked(MouseEvent e) {
        //Irrelevant to program
    }

    public void mousePressed(MouseEvent e) {
        //The mouse event itself is passed because it contains information of the mouse button pressed and the location of the mouse
        combatTest.mousePressed(e);
    }

    public void mouseReleased(MouseEvent e) {
        //The mouse event itself is passed because it contains information of the mouse button released and the location of the mouse

        combatTest.mouseReleased(e);
    }

    private void mouseMoved(){

    }

    public void mouseEntered(MouseEvent e) {
        //Irrelevant to program
    }

    public void mouseExited(MouseEvent e) {
        //Irrelevant to program
    }

    public static long getRunningTime() {
        return runningTime;
    }

    public static Point mouseLocation(){
        Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(mouseLocation, window.getFrame());
        return mouseLocation;
    }

    public int getGameWidth(){
        return window.getWidth();
    }

    public int getGameHeight(){
        return window.getHeight();
    }

    public static Game getInstance() {
        return instance;
    }
}