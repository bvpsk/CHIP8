import java.awt.*;
import javax.swing.*;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

class MyTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;
    final Object[][] data;
    final String[] columns;

    MyTableModel(Object[][] d, String[] column) {
        data = d;
        columns = column;
    }

    @Override
    public String getColumnName(int col) {
        return columns[col];
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }
}

public class Interface {
    public CPU cpu = null;
    public Keypad keypad = null;
    public Screen screen = null;

    public Interface(CPU cpu, Screen screen, Keypad keypad) {
        this.cpu = cpu;
        this.keypad = keypad;
        this.screen = screen;

        JFrame emulator = new JFrame("CHIP-8 Emulator");
        // emulator.setLayout(new GridLayout(3, 1));
        emulator.setLayout(new BoxLayout(emulator.getContentPane(), BoxLayout.Y_AXIS));
        emulator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        emulator.setSize(screen.rows * screen.scale, 600);
        // emulator.setSize(screen.rows * screen.scale, 4 * screen.cols * screen.scale);
        // emulator.add(screen);
        // emulator.add(keypad);
        // emulator.setVisible(true);

        // int[] s = cpu.loadRom("roms/MAZE.dms");
        final JFrame frame = new JFrame("CHIP-8 Debugger");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 870);
        // frame.getContentPane().setLayout(new GridLayout(1, 3));
        frame.setLayout(new GridLayout(1, 2));
        // GridBagConstraints c = new GridBagConstraints();

        Object[][] RAMData = new Object[0xfff + 1][2];
        for (int i = 0; i <= 0xfff; i++) {
            RAMData[i][0] = Integer.toHexString(i);
            RAMData[i][1] = Integer.toHexString(cpu.RAM[i]);
        }
        final String RAMColumns[] = { "RAM LOC", "VALUE" };
        final JTable RAMTable = new JTable();
        MyTableModel RAMModel = new MyTableModel(RAMData, RAMColumns);
        RAMTable.setModel(RAMModel);
        cpu.RAMTable = RAMModel;
        cpu.ramObj = RAMData;
        final JScrollPane RAMSP = new JScrollPane(RAMTable);
        RAMSP.setPreferredSize(new Dimension(150, 750));
        // frame.add(RAMSP);

        Object[][] VData = new Object[16][2];
        for (int i = 0; i <= 0xf; i++) {
            VData[i][0] = Integer.toHexString(i);
            VData[i][1] = Integer.toHexString(cpu.V[i]);
        }

        final String VColumns[] = { "V", "VALUE" };
        final JTable VTable = new JTable();
        MyTableModel VModel = new MyTableModel(VData, VColumns);
        VTable.setModel(VModel);
        cpu.VTable = VModel;
        cpu.VObj = VData;
        final JScrollPane VSP = new JScrollPane(VTable);
        VSP.setPreferredSize(new Dimension(150, 280));
        // frame.add(VSP);

        Object[][] StackData = new Object[16][2];
        for (int i = 0; i <= 0xf; i++) {
            StackData[i][0] = Integer.toHexString(i);
            StackData[i][1] = Integer.toHexString(cpu.V[i]);
        }

        final String StackColumns[] = { "STACK", "VALUE" };
        final JTable StackTable = new JTable();
        MyTableModel StackModel = new MyTableModel(StackData, StackColumns);
        StackTable.setModel(StackModel);
        cpu.StackTable = StackModel;
        cpu.StackObj = StackData;
        final JScrollPane StackSP = new JScrollPane(StackTable);
        StackSP.setPreferredSize(new Dimension(150, 280));
        // frame.add(StackSP);

        Object[][] RegsData = new Object[8][2];
        RegsData[0][0] = "I";
        RegsData[0][1] = Integer.toHexString(cpu.I);
        RegsData[1][0] = "PC";
        RegsData[1][1] = Integer.toHexString(cpu.PC);
        RegsData[2][0] = "SP";
        RegsData[2][1] = Integer.toHexString(cpu.SP);
        RegsData[3][0] = "DELAY";
        RegsData[3][1] = Integer.toHexString(cpu.delayTimer);
        RegsData[4][0] = "SOUND";
        RegsData[4][1] = Integer.toHexString(cpu.soundTimer);
        RegsData[5][0] = "KEYPAD";
        RegsData[5][1] = Integer.toHexString(cpu.keyVal);
        RegsData[6][0] = "Instruction";
        RegsData[6][1] = Integer.toHexString(cpu.I);
        RegsData[7][0] = "Next";
        RegsData[7][1] = Integer.toHexString(cpu.I);

        final String RegsColumns[] = { "Register", "VALUE" };
        final JTable RegsTable = new JTable();
        MyTableModel RegsModel = new MyTableModel(RegsData, RegsColumns);
        RegsTable.setModel(RegsModel);
        cpu.RegsTable = RegsModel;
        cpu.RegsObj = RegsData;
        final JScrollPane RegsSP = new JScrollPane(RegsTable);
        RegsSP.setPreferredSize(new Dimension(150, 160));
        // frame.add(RegsSP);


        JPanel controller = new JPanel();
        controller.setLayout(new BoxLayout(controller, BoxLayout.X_AXIS));
        
        final JButton debugStep = new JButton("Debug");
        debugStep.addActionListener((ActionListener) new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                try {
                    cpu.step();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        });

        controller.add(debugStep);

        JFrame helper = new JFrame("Helper");
        helper.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        helper.setLayout(new BoxLayout(helper.getContentPane(), BoxLayout.Y_AXIS));
        helper.setSize(400, 400);
        JLabel h1 = new JLabel("<html><br/>Current Instruction<br/><br/></html>");
        JLabel currHelp = new JLabel("Help for Current Instruction");
        JLabel h2 = new JLabel("<html><br/>Next Instruction<br/><br/></html>");
        JLabel nextHelp = new JLabel("Help for Next Instruction");
        cpu.currHelp = currHelp;
        cpu.nextHelp = nextHelp;
        helper.add(h1);
        helper.add(currHelp);
        helper.add(h2);
        helper.add(nextHelp);

        
        JPanel r2 = new JPanel();
        r2.setLayout(new GridLayout(3, 1, 5, 5));
        r2.add(StackSP);
        r2.add(VSP);
        r2.add(RegsSP);

        frame.add(RAMSP);
        frame.add(r2);



        // c.gridx = 0;
        // c.gridy = 0;
        // c.gridheight = 3;

        // frame.add(RAMSP, c);

        // c.gridx = 1;
        // c.gridy = 0;
        // c.gridheight = 1;
        // frame.add(StackSP, c);

        // c.gridx = 1;
        // c.gridy = 1;
        // c.gridheight = 1;
        // frame.add(VSP, c);

        // c.gridx = 1;
        // c.gridy = 2;
        // c.gridheight = 1;
        // frame.add(RegsSP, c);

        // c.gridx = 2;
        // c.gridy = 0;
        // c.gridheight = 1;
        // frame.add(debug, c);
        // emulator.add(debug);

        // c.gridx = 2;
        // c.gridy = 0;
        // c.gridheight = 1;
        // frame.add(screen, c);

        // c.gridx = 2;
        // c.gridy = 1;
        // c.gridheight = 1;
        // frame.add(keypad, c);

        // c.gridx = 3;
        // c.gridy = 0;
        // c.gridheight = 1;
        // frame.add(debug, c);

        // final JButton btn = new JButton("Debug");
        // btn.addActionListener((ActionListener) new ActionListener() {
        //     public void actionPerformed(java.awt.event.ActionEvent e) {
        //         try {
        //             cpu.step();
        //         } catch (InterruptedException e1) {
        //             // TODO Auto-generated catch block
        //             e1.printStackTrace();
        //         }
        //         // myModel.fireTableCellUpdated(10, 1);
        //     }
        // });
        // frame.add(btn);

        emulator.add(screen);
        emulator.add(controller);
        emulator.add(keypad);

        
        frame.setVisible(true);
        emulator.setVisible(true);
        helper.setVisible(true);
        // int[] s = cpu.loadRom("roms/MAZE.dms");
    }

}






