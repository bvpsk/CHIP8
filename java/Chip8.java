import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class Chip8 {
    public static void main(final String[] args) throws InterruptedException {
        final CPU cpu = new CPU();
        Keypad keypad = new Keypad(cpu);
        Screen screen = new Screen(64, 32, 7, Color.MAGENTA);
        cpu.screen = screen;
        Interface gui = new Interface(cpu, screen, keypad);
        screen.fireScreen(500);
        cpu.loadRom("roms/pongSP.ch8");
        Thread.sleep(1000);
        final Timer timer = new Timer(1, (ActionListener) new ActionListener() {
            public void actionPerformed(final java.awt.event.ActionEvent e) {
                try {
                    cpu.fde();
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        timer.start(); // Go go go!
    }
}