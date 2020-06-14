import java.awt.*;
import javax.swing.*;

class gui{
    public static void main(String[] args) {
        JFrame frame = new JFrame("Chat Frame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        JMenuBar mb = new JMenuBar();
        JMenu m1 = new JMenu("FILE");
        JMenu m2 = new JMenu("HELP");
        mb.add(m1);
        mb.add(m2);

        JMenuItem mi1 = new JMenuItem("Open");
        JMenuItem mi2 = new JMenuItem("Save as");
        m1.add(mi1);
        m1.add(mi2);

        JPanel panel = new JPanel();
        JLabel label = new JLabel("Enter Text");
        JTextField tf = new JTextField(10);
        JButton b1 = new JButton("Send");
        JButton b2 = new JButton("Reset");
        panel.add(label);
        panel.add(tf);
        panel.add(b1);
        panel.add(b2);

        JTextArea ta = new JTextArea();

        frame.getContentPane().add(BorderLayout.NORTH, mb);
        frame.getContentPane().add(BorderLayout.CENTER, ta);
        frame.getContentPane().add(BorderLayout.SOUTH, panel);

        frame.setVisible(true);

    }
}