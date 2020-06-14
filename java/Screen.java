import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;

class Screen extends Canvas {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public int rows, cols, scale;
    public Color color;
    public int[][] pixels = null;

    public Screen(int rows, int cols, int scale, Color color) {
        setSize(rows * scale, cols * scale);
        this.rows = rows;
        this.cols = cols;
        this.scale = scale;
        this.color = color;
        this.pixels = new int[this.rows][this.cols];
    }

    public Screen() {
        setSize(64 * 5, 32 * 5);
        this.rows = 64;
        this.cols = 32;
        this.scale = 5;
        this.color = Color.GREEN;
        this.pixels = new int[this.rows][this.cols];
    }

    public int setPixel(int r, int c, int condition) {
        int prev = pixels[r][c];
        pixels[r][c] ^= condition;
        firePixel(r, c);
        return prev == 1 && pixels[r][c] == 0 ? 1 : 0;
    }

    public void firePixel(int r, int c) {
        Graphics g = this.getGraphics();
        g.setColor(pixels[r][c] == 1 ? color : Color.BLACK);
        g.fillRect(r * scale, c * scale, scale, scale);
    }

    public void clearScreen() {
        Graphics g = this.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, rows * scale, cols * scale);
        pixels = new int[rows][cols];
    }

    public int drawByte(int data, int r, int c) {
        int i = 0;
        int cond;
        int collision = 0;
        while (i < 8) {
            cond = data & (int) Math.pow(2, 7 - i);
            collision |= setPixel((r + i) % rows, c % cols, cond != 0 ? 1 : 0);
            i++;
        }
        return collision;
    }

    public void fireScreen(int delay) {
        final Timer timer = new Timer(delay, (ActionListener) new ActionListener() {
            public void actionPerformed(final java.awt.event.ActionEvent e) {
                System.out.println("Screen Initialized");
                clearScreen();
            }
        });
        timer.setRepeats(false); // Only execute once
        timer.start(); // Go go go!
    }

    public void rand(){
        Color color = null;
        Graphics g = this.getGraphics();
        for(int r = 0; r < this.rows; r++){
            for(int c = 0; c < this.rows; c++){
                color = new Color((int)Math.floor(Math.random() * 255), (int)Math.floor(Math.random() * 255), (int)Math.floor(Math.random() * 255));
                g.setColor(color);
                g.fillRect(r * scale, c * scale, scale, scale);
            }
        }
    }

    public void paint(Graphics g) {
    }

    public void test(){
        int x = (int)Math.floor(Math.random() * (rows * scale)), y = (int)Math.floor(Math.random() * (cols * scale));
        Graphics g = this.getGraphics();
        // System.out.println("Voilaaa");
        // System.out.println(g);
        g.drawRect(x, y, 140, 60);
    }




}