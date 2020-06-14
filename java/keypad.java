import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

class KeypadListener implements ChangeListener {
    public final int value;
    public CPU cpu = null;
    KeypadListener(int val, CPU cpu){
        value = val;
        this.cpu = cpu;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        ButtonModel model = (ButtonModel) e.getSource();

        if(model.isPressed()){
            this.cpu.keyVal = value;
            this.cpu.RegsObj[this.cpu.rKeyVal][1] = Integer.toHexString(value);
            this.cpu.RegsTable.fireTableCellUpdated(this.cpu.rKeyVal, 1);

        }else{
            this.cpu.keyVal = 16;
            this.cpu.RegsObj[this.cpu.rKeyVal][1] = Integer.toHexString(16);
            this.cpu.RegsTable.fireTableCellUpdated(this.cpu.rKeyVal, 1);
        }
    }
}


class Keypad extends JPanel{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    Keypad(CPU cpu){
        super();
        // setSize(new Dimension(600, 600));
        setLayout(new GridLayout(4, 4, 5, 5));
        // setPreferredSize(new Dimension(600, 400));
        JButton b0 = new JButton("0");
        JButton b1 = new JButton("1");
        JButton b2 = new JButton("2");
        JButton b3 = new JButton("3");
        JButton b4 = new JButton("4");
        JButton b5 = new JButton("5");
        JButton b6 = new JButton("6");
        JButton b7 = new JButton("7");
        JButton b8 = new JButton("8");
        JButton b9 = new JButton("9");
        JButton bA = new JButton("A");
        JButton bB = new JButton("B");
        JButton bC = new JButton("C");
        JButton bD = new JButton("D");
        JButton bE = new JButton("E");
        JButton bF = new JButton("F");

        b0.getModel().addChangeListener(new KeypadListener(0, cpu));
        b1.getModel().addChangeListener(new KeypadListener(1, cpu));
        b2.getModel().addChangeListener(new KeypadListener(2, cpu));
        b3.getModel().addChangeListener(new KeypadListener(3, cpu));
        b4.getModel().addChangeListener(new KeypadListener(4, cpu));
        b5.getModel().addChangeListener(new KeypadListener(5, cpu));
        b6.getModel().addChangeListener(new KeypadListener(6, cpu));
        b7.getModel().addChangeListener(new KeypadListener(7, cpu));
        b8.getModel().addChangeListener(new KeypadListener(8, cpu));
        b9.getModel().addChangeListener(new KeypadListener(9, cpu));
        bA.getModel().addChangeListener(new KeypadListener(0xa, cpu));
        bB.getModel().addChangeListener(new KeypadListener(0xb, cpu));
        bC.getModel().addChangeListener(new KeypadListener(0xc, cpu));
        bD.getModel().addChangeListener(new KeypadListener(0xd, cpu));
        bE.getModel().addChangeListener(new KeypadListener(0xe, cpu));
        bF.getModel().addChangeListener(new KeypadListener(0xf, cpu));


        add(b1);
        add(b2);
        add(b3);
        add(bC);
        add(b4);
        add(b5);
        add(b6);
        add(bD);
        add(b7);
        add(b8);
        add(b9);
        add(bE);
        add(bA);
        add(b0);
        add(bB);
        add(bF);
    }
}