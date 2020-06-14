import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

import javax.swing.JLabel;

public class CPU {
    public JLabel currHelp, nextHelp;
    public int[] RAM = new int[0xfff + 1];
    public int I, PC, SP, delayTimer, soundTimer, instr, next, keyVal = 0x10, entryPoint = 0x200;
    public int rI = 0, rPC = 1, rSP = 2, rDelay = 3, rSound = 4, rKeyVal = 5, rInstr = 6, rNext = 7;
    public int[] V = new int[0xf + 1];
    public int[] stack = new int[0xf + 1];
    public MyTableModel RAMTable, VTable, StackTable, RegsTable;
    public Object[][] ramObj, VObj, StackObj, RegsObj;
    public Screen screen;
    public int[] hexChars = {
              0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
              0x20, 0x60, 0x20, 0x20, 0x70, // 1
              0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
              0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
              0x90, 0x90, 0xF0, 0x10, 0x10, // 4
              0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
              0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
              0xF0, 0x10, 0x20, 0x40, 0x40, // 7
              0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
              0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
              0xF0, 0x90, 0xF0, 0x90, 0x90, // A
              0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
              0xF0, 0x80, 0x80, 0x80, 0xF0, // C
              0xE0, 0x90, 0x90, 0x90, 0xE0, // D
              0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
              0xF0, 0x80, 0xF0, 0x80, 0x80 // F
    };

    CPU() {
        PC = 0x200;
        delayTimer = 0x0;
        soundTimer = 0x0;
        for(int i = 0; i < hexChars.length; i++) RAM[i] = hexChars[i];
    }

    public void reset(){
        PC = 0x200;
        delayTimer = 0x0;
        soundTimer = 0x0;
        SP = 0;

    }

    public void loadRom(String path) {
        try {
            File file = new File(path);
            DataInputStream stream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
            byte[] rom = new byte[(int) file.length()];
            stream.read(rom);
            int[] data = new int[(int) file.length()];
            for (int i = 0; i < rom.length; i++) {
                data[i] = rom[i];
                if (data[i] < 0) {
                    data[i] += 256;
                }
                RAM[entryPoint + i] = data[i];
                ramObj[entryPoint + i][1] = Integer.toHexString(data[i]);
                RAMTable.fireTableCellUpdated(entryPoint + i, 1);
            }
            // PC = loc + rom.length;
            // RegsObj[rPC][1] = Integer.toHexString(PC);
            // RegsTable.fireTableCellUpdated(rPC, 1);
            stream.close();
        } catch (Exception e) {
            // e.printStackTrace();
            int[] li = new int[0];
            System.out.println("Length of file is : " + li.length);
        }
    }

    public String getHelp(int pc){
        int msb = RAM[pc];
        int lsb = RAM[pc + 1];
        int a = (msb & 0xf0) >> 4;
        int d = lsb & 0x0f;
        switch(a){
            case 0x0:
                switch(d){
                    case 0x0:
                        return "<html>00E0 - CLS <br/> Clear the display.</html>";
                    case 0xe:
                        return "<html>00EE - RET<br/>Return from a subroutine.<br/>The interpreter sets the program counter to the address at the top of the stack, then subtracts 1 from the stack pointer. </html>";
                }
                break;
            case 0x1:
                return "<html>1nnn - JP addr<br/>Jump to location nnn.<br/>The interpreter sets the program counter to nnn.</html>";
            case 0x2:
                return "<html>2nnn - CALL addr<br/>Call subroutine at nnn.<br/>The interpreter increments the stack pointer, then puts the current PC on the top of the stack. The PC is then set to nnn. </html>";
            case 0x3:
                return "<html>3xkk - SE Vx, byte<br/>Skip next instruction if Vx = kk.<br/>The interpreter compares register Vx to kk, and if they are equal, increments the program counter by 2. </html>";
            case 0x4:
                return "<html>4xkk - SNE Vx, byte<br/>Skip next instruction if Vx != kk.<br/>The interpreter compares register Vx to kk, and if they are not equal, increments the program counter by 2. </html>";
            case 0x5:
                return "<html>5xy0 - SE Vx, Vy<br/>Skip next instruction if Vx = Vy.<br/>The interpreter compares register Vx to register Vy, and if they are equal, increments the program counter by 2. </html>";
            case 0x6:
                return "<html> 6xkk - LD Vx, byte<br/>Set Vx = kk.<br/>The interpreter puts the value kk into register Vx.</html>";
            case 0x7:
                return "<html>7xkk - ADD Vx, byte<br/>Set Vx = Vx + kk.<br/>Adds the value kk to the value of register Vx, then stores the result in Vx.  </html>";
            case 0x8:
                switch(d){
                    case 0x0:
                        return "<html>8xy0 - LD Vx, Vy<br/>Set Vx = Vy.<br/>Stores the value of register Vy in register Vx. </html>";
                    case 0x1:
                        return "<html> 8xy1 - OR Vx, Vy<br/>Set Vx = Vx OR Vy.<br/>Performs a bitwise OR on the values of Vx and Vy, then stores the result in Vx. A bitwise OR compares the corrseponding bits from two values, and if either bit is 1, then the same bit in the result is also 1. Otherwise, it is 0. </html>";
                    case 0x2:
                        return "<html>8xy2 - AND Vx, Vy<br/>Set Vx = Vx AND Vy.<br/>Performs a bitwise AND on the values of Vx and Vy, then stores the result in Vx. A bitwise AND compares the corrseponding bits from two values, and if both bits are 1, then the same bit in the result is also 1. Otherwise, it is 0.  </html>";
                    case 0x3:
                        return "<html>8xy3 - XOR Vx, Vy<br/>Set Vx = Vx XOR Vy.<br/>Performs a bitwise exclusive OR on the values of Vx and Vy, then stores the result in Vx. An exclusive OR compares the corrseponding bits from two values, and if the bits are not both the same, then the corresponding bit in the result is set to 1. Otherwise, it is 0.  </html>";
                    case 0x4:
                        return "<html>8xy4 - ADD Vx, Vy<br/>Set Vx = Vx + Vy, set VF = carry.<br/>The values of Vx and Vy are added together. If the result is greater than 8 bits (i.e., > 255,) VF is set to 1, otherwise 0. Only the lowest 8 bits of the result are kept, and stored in Vx. </html>";
                    case 0x5:
                        return "<html> 8xy5 - SUB Vx, Vy<br/>Set Vx = Vx - Vy, set VF = NOT borrow.<br/>If Vx > Vy, then VF is set to 1, otherwise 0. Then Vy is subtracted from Vx, and the results stored in Vx.</html>";
                    case 0x6:
                        return "<html>8xy6 - SHR Vx {, Vy}<br/>Set Vx = Vx SHR 1.<br/>If the least-significant bit of Vx is 1, then VF is set to 1, otherwise 0. Then Vx is divided by 2. </html>";
                    case 0x7:
                        return "<html>8xy7 - SUBN Vx, Vy<br/>Set Vx = Vy - Vx, set VF = NOT borrow. <br/>If Vy > Vx, then VF is set to 1, otherwise 0. Then Vx is subtracted from Vy, and the results stored in Vx. </html>";
                    case 0xe:
                        return "<html> 8xyE - SHL Vx {, Vy}<br/>Set Vx = Vx SHL 1.<br/>If the most-significant bit of Vx is 1, then VF is set to 1, otherwise to 0. Then Vx is multiplied by 2.</html>";
                }
                break;
            case 0x9:
                return "<html>9xy0 - SNE Vx, Vy<br/>Skip next instruction if Vx != Vy.<br/>The values of Vx and Vy are compared, and if they are not equal, the program counter is increased by 2. </html>";
            case 0xa:
                return "<html> Annn - LD I, addr<br/>Set I = nnn.<br/>The value of register I is set to nnn.</html>";
            case 0xb:
                return "<html>Bnnn - JP V0, addr<br/>Jump to location nnn + V0.<br/>The program counter is set to nnn plus the value of V0. </html>";
            case 0xc:
                return "<html>Cxkk - RND Vx, byte<br/>Set Vx = random byte AND kk.<br/>The interpreter generates a random number from 0 to 255, which is then ANDed with the value kk. The results are stored in Vx. See instruction 8xy2 for more information on AND. </html>";
            case 0xd:
                return "<html>Dxyn - DRW Vx, Vy, nibble<br/>Display n-byte sprite starting at memory location I at (Vx, Vy), set VF = collision.<br/>The interpreter reads n bytes from memory, starting at the address stored in I. These bytes are then displayed as sprites on screen at coordinates (Vx, Vy). Sprites are XORed onto the existing screen. If this causes any pixels to be erased, VF is set to 1, otherwise it is set to 0. If the sprite is positioned so part of it is outside the coordinates of the display, it wraps around to the opposite side of the screen. See instruction 8xy3 for more information on XOR, and section 2.4, Display, for more information on the Chip-8 screen and sprites. </html>";
            case 0xe:
                switch(lsb){
                    case 0x9e:
                        return "<html>Ex9E - SKP Vx<br/>Skip next instruction if key with the value of Vx is pressed.      <br/>Checks the keyboard, and if the key corresponding to the value of Vx is currently in the down position, PC is increased by 2. </html>";
                    case 0xa1:
                        return "<html>ExA1 - SKNP Vx<br/>Skip next instruction if key with the value of Vx is not pressed.<br/>Checks the keyboard, and if the key corresponding to the value of Vx is currently in the up position, PC is increased by 2. </html>";
                }
                break;
            case 0xf:
                switch(lsb){
                    case 0x7:
                        return "<html>Fx07 - LD Vx, DT<br/>Set Vx = delay timer value.<br/>The value of DT is placed into Vx. </html>";
                    case 0xa:
                        return "<html>Fx0A - LD Vx, K<br/>Wait for a key press, store the value of the key in Vx.<br/>All execution stops until a key is pressed, then the value of that key is stored in Vx. </html>";
                    case 0x15:
                        return "<html>Fx15 - LD DT, Vx<br/>Set delay timer = Vx.<br/>DT is set equal to the value of Vx. </html>";
                    case 0x18:
                        return "<html>Fx18 - LD ST, Vx<br/>Set sound timer = Vx.<br/>ST is set equal to the value of Vx. </html>";
                    case 0x1e:
                        return "<html>Fx1E - ADD I, Vx<br/>Set I = I + Vx.<br/>The values of I and Vx are added, and the results are stored in I. </html>";
                    case 0x29:
                        return "<html>Fx29 - LD F, Vx<br/>Set I = location of sprite for digit Vx.<br/>The value of I is set to the location for the hexadecimal sprite corresponding to the value of Vx. See section 2.4, Display, for more information on the Chip-8 hexadecimal font. </html>";
                    case 0x33:
                        return "<html> Fx33 - LD B, Vx<br/>Store BCD representation of Vx in memory locations I, I+1, and I+2.<br/>The interpreter takes the decimal value of Vx, and places the hundreds digit in memory at location in I, the tens digit at location I+1, and the ones digit at location I+2.</html>";
                    case 0x55:
                        return "<html>Fx55 - LD [I], Vx<br/>Store registers V0 through Vx in memory starting at location I.<br/>The interpreter copies the values of registers V0 through Vx into memory, starting at the address in I. </html>";
                    case 0x65:
                        return "<html>Fx65 - LD Vx, [I]<br/>Read registers V0 through Vx from memory starting at location I.<br/>The interpreter reads values from memory starting at location I into registers V0 through Vx. </html>";
                }
                break;
            
            
        }
		return null;
    }

    public void updateStack() {
        StackObj[SP - 1][1] = Integer.toHexString(stack[SP - 1]);
        StackTable.fireTableCellUpdated(SP - 1, 1);
    }

    public void updateV(int idx) {
        VObj[idx][1] = Integer.toHexString(V[idx]);
        VTable.fireTableCellUpdated(idx, 1);
    }

    public void updateRAM(int idx) {
        ramObj[idx][1] = Integer.toHexString(RAM[idx]);
        RAMTable.fireTableCellUpdated(idx, 1);
    }

    public void updateReg(int reg) {
        switch (reg) {
            case 0:
                RegsObj[rI][1] = Integer.toHexString(I);
                RegsTable.fireTableCellUpdated(rI, 1);
                break;
            case 1:
                RegsObj[rPC][1] = Integer.toHexString(PC);
                RegsTable.fireTableCellUpdated(rPC, 1);
                break;
            case 2:
                RegsObj[rSP][1] = Integer.toHexString(SP);
                RegsTable.fireTableCellUpdated(rSP, 1);
                break;
            case 3:
                RegsObj[rDelay][1] = Integer.toHexString(delayTimer);
                RegsTable.fireTableCellUpdated(rDelay, 1);
                break;
            case 4:
                RegsObj[rSound][1] = Integer.toHexString(soundTimer);
                RegsTable.fireTableCellUpdated(rSound, 1);
                break;
            case 6:
                RegsObj[rInstr][1] = Integer.toHexString(instr);
                RegsTable.fireTableCellUpdated(rInstr, 1);
            case 7:
                RegsObj[rNext][1] = Integer.toHexString(next);
                RegsTable.fireTableCellUpdated(rNext, 1);
        }
    }

    public void step() throws InterruptedException {
        int msb = RAM[PC];
        int lsb = RAM[PC + 1];
        PC += 2;
        updateReg(rPC);
        // I = lsb | (msb << 8);
        // updateReg(rI);
        instr = lsb | (msb << 8);
        updateReg(rInstr);
        next = RAM[PC + 1] | (RAM[PC] << 8);
        updateReg(rNext);
        nextHelp.setText(getHelp(PC));
        currHelp.setText(getHelp(PC - 2));
        int a = (msb & 0xf0) >> 4;
        int x = msb & 0x0f;
        int y = (lsb & 0xf0) >> 4;
        int d = lsb & 0x0f;

        switch (a) {
            case 0x0:
                switch (d) {
                    case 0:
                        screen.clearScreen();
                        break;
                    case 0xe:
                        SP -= 1;
                        PC = stack[SP];
                        nextHelp.setText(getHelp(PC));
                        next = RAM[PC + 1] | (RAM[PC] << 8);
                        updateReg(rNext);
                        updateReg(rPC);
                        updateReg(rSP);
                        break;
                    default:
                        System.out.println("Unknown Instruction " + Integer.toHexString(instr));
                        break;
                }
                break;
            case 0x1:
                PC = lsb | (x << 8);
                nextHelp.setText(getHelp(PC));
                next = RAM[PC + 1] | (RAM[PC] << 8);
                updateReg(rNext);
                updateReg(rPC);
                break;
            case 0x2:
                stack[SP] = PC;
                SP += 1;
                PC = lsb | (x << 8);
                nextHelp.setText(getHelp(PC));
                next = RAM[PC + 1] | (RAM[PC] << 8);
                updateReg(rNext);
                updateReg(rPC);
                updateReg(rSP);
                updateStack();
                break;
            case 0x3:
                if (V[x] == lsb) {
                    PC += 2;
                    nextHelp.setText(getHelp(PC));
                    next = RAM[PC + 1] | (RAM[PC] << 8);
                    updateReg(rNext);
                    updateReg(rPC);
                }
                break;
            case 0x4:
                if (V[x] != lsb) {
                    PC += 2;
                    nextHelp.setText(getHelp(PC));
                    next = RAM[PC + 1] | (RAM[PC] << 8);
                    updateReg(rNext);
                    updateReg(rPC);
                }
                break;
            case 0x5:
                if (V[x] == V[y]) {
                    PC += 2;
                    nextHelp.setText(getHelp(PC));
                    next = RAM[PC + 1] | (RAM[PC] << 8);
                    updateReg(rNext);
                    updateReg(rPC);
                }
                break;
            case 0x6:
                V[x] = lsb;
                updateV(x);
                break;
            case 0x7:
                V[x] += lsb;
                updateV(x);
                break;
            case 0x8:
                switch (d) {
                    case 0:
                        V[x] = V[y];
                        updateV(x);
                        break;
                    case 1:
                        V[x] = V[x] | V[y];
                        updateV(x);
                        break;
                    case 2:
                        V[x] = V[x] & V[y];
                        updateV(x);
                        break;
                    case 3:
                        V[x] = V[x] ^ V[y];
                        updateV(x);
                        break;
                    case 4:
                        V[x] = V[x] + V[y];
                        V[0xf] = 0;
                        if (V[x] > 255) {
                            V[0xf] = 1;
                            V[x] = V[x] & 0x0ff;
                        }
                        updateV(x);
                        updateV(0xf);
                        break;
                    case 5:
                        V[x] = V[x] - V[y];
                        V[0xf] = V[x] > V[y] ? 1 : 0;
                        updateV(x);
                        updateV(0xf);
                        break;
                    case 6:
                        V[x] = V[x] >> 1;
                        V[0xf] = V[x] & 0x01;
                        updateV(x);
                        updateV(0xf);
                        break;
                    case 7:
                        V[x] = V[y] - V[x];
                        V[0xf] = V[y] > V[x] ? 1 : 0;
                        updateV(x);
                        updateV(0xf);
                        break;
                    case 0xe:
                        V[x] = V[x] << 1;
                        V[0xf] = V[x] & 0x80;
                        updateV(x);
                        updateV(0xf);
                        break;
                    default:
                        System.out.println("Unknown Instruction " + Integer.toHexString(instr));
                        break;
                }
                break;
            case 0x9:
                if (V[x] != V[y]) {
                    PC += 2;
                    nextHelp.setText(getHelp(PC));
                    next = RAM[PC + 1] | (RAM[PC] << 8);
                    updateReg(rNext);
                    updateReg(rPC);
                }
                break;
            case 0xa:
                I = lsb | (x << 8);
                updateReg(rI);
                break;
            case 0xb:
                PC = (lsb | (x << 8)) + V[0];
                nextHelp.setText(getHelp(PC));
                next = RAM[PC + 1] | (RAM[PC] << 8);
                updateReg(rNext);
                updateReg(rPC);
                break;
            case 0xc:
                V[x] = ((int) Math.floor(Math.random() * 255)) & lsb;
                updateV(x);
                break;
            case 0xd:
                V[0xf] = 0;
                for (int i = 0; i < d; i++) {
                    V[0xf] |= screen.drawByte(RAM[I + i], V[x], V[y] + i);
                }
                updateV(0xf);
                break;
            case 0xe:
                switch (d) {
                    case 0xe:
                        if (keyVal == V[x]) {
                            PC += 2;
                            nextHelp.setText(getHelp(PC));
                            next = RAM[PC + 1] | (RAM[PC] << 8);
                            updateReg(rNext);
                            updateReg(rPC);
                        }
                        break;
                    case 0x1:
                        if (keyVal != V[x]) {
                            PC += 2;
                            nextHelp.setText(getHelp(PC));
                            next = RAM[PC + 1] | (RAM[PC] << 8);
                            updateReg(rNext);
                            updateReg(rPC);
                        }
                        break;
                    default:
                        System.out.println("Unknown Instruction " + Integer.toHexString(instr));
                        break;
                }
                break;
            case 0xf:
                switch (lsb) {
                    case 0x07:
                        V[x] = delayTimer;
                        updateV(x);
                        break;
                    case 0x0a:
                        while (keyVal == 0xa) {
                        }
                        V[x] = keyVal;
                        updateV(x);
                        break;
                    case 0x15:
                        // delayTimer = V[x];
                        Thread.sleep((int)(1 * V[x]));
                        // updateReg(rDelay);
                        break;
                    case 0x18:
                        // soundTimer = V[x];
                        Thread.sleep((int)(1 * V[x]));
                        // updateReg(rSound);
                        break;
                    case 0x1e:
                        I = I + V[x];
                        updateReg(rI);
                        break;
                    case 0x29:
                        I = 5 * V[x];
                        updateReg(rI);
                        break;
                    case 0x33:
                        int val = V[x];
                        RAM[I + 2] = val % 10;
                        val = val / 10;
                        RAM[I + 1] = val % 10;
                        RAM[I] = val / 10;
                        updateRAM(I);
                        updateRAM(I + 1);
                        updateRAM(I + 2);
                        break;
                    case 0x55:
                        for(int i = 0; i <= x; i++){
                            RAM[I + i] = V[i];
                            updateRAM(I + i);
                        }
                        break;
                    case 0x65:
                        for(int i = 0; i <= x; i++){
                            V[i] = RAM[I + i];
                            updateV(i);
                        }
                        break;
                    default:
                        System.out.println("Unknown Instruction " + Integer.toHexString(instr));
                        break;
                }
                break;
                default:
                    System.out.println("Unknown Instruction " + Integer.toHexString(instr));
                    break;
        }


    }


    public void fde() throws InterruptedException {
        int msb = RAM[PC];
        int lsb = RAM[PC + 1];
        PC += 2;
        // I = lsb | (msb << 8);
        // updateReg(rI);
        int a = (msb & 0xf0) >> 4;
        int x = msb & 0x0f;
        int y = (lsb & 0xf0) >> 4;
        int d = lsb & 0x0f;

        switch (a) {
            case 0x0:
                switch (d) {
                    case 0:
                        screen.clearScreen();
                        break;
                    case 0xe:
                        SP -= 1;
                        PC = stack[SP];
                        break;
                    default:
                        System.out.println("Unknown Instruction " + Integer.toHexString(instr));
                        break;
                }
                break;
            case 0x1:
                PC = lsb | (x << 8);
                break;
            case 0x2:
                stack[SP] = PC;
                SP += 1;
                PC = lsb | (x << 8);
                break;
            case 0x3:
                if (V[x] == lsb) {
                    PC += 2;
                }
                break;
            case 0x4:
                if (V[x] != lsb) {
                    PC += 2;
                }
                break;
            case 0x5:
                if (V[x] == V[y]) {
                    PC += 2;
                }
                break;
            case 0x6:
                V[x] = lsb;
                break;
            case 0x7:
                V[x] += lsb;
                break;
            case 0x8:
                switch (d) {
                    case 0:
                        V[x] = V[y];
                        break;
                    case 1:
                        V[x] = V[x] | V[y];
                        break;
                    case 2:
                        V[x] = V[x] & V[y];
                        break;
                    case 3:
                        V[x] = V[x] ^ V[y];
                        break;
                    case 4:
                        V[x] = V[x] + V[y];
                        V[0xf] = 0;
                        if (V[x] > 255) {
                            V[0xf] = 1;
                            V[x] = V[x] & 0x0ff;
                        }
                        break;
                    case 5:
                        V[x] = V[x] - V[y];
                        V[0xf] = V[x] > V[y] ? 1 : 0;
                        break;
                    case 6:
                        V[x] = V[x] >> 1;
                        V[0xf] = V[x] & 0x01;
                        break;
                    case 7:
                        V[x] = V[y] - V[x];
                        V[0xf] = V[y] > V[x] ? 1 : 0;
                        break;
                    case 0xe:
                        V[x] = V[x] << 1;
                        V[0xf] = V[x] & 0x80;
                        break;
                    default:
                        System.out.println("Unknown Instruction " + Integer.toHexString(instr));
                        break;
                }
                break;
            case 0x9:
                if (V[x] != V[y]) {
                    PC += 2;
                }
                break;
            case 0xa:
                I = lsb | (x << 8);
                break;
            case 0xb:
                PC = (lsb | (x << 8)) + V[0];
                break;
            case 0xc:
                V[x] = ((int) Math.floor(Math.random() * 255)) & lsb;
                break;
            case 0xd:
                V[0xf] = 0;
                for (int i = 0; i < d; i++) {
                    V[0xf] |= screen.drawByte(RAM[I + i], V[x], V[y] + i);
                }
                break;
            case 0xe:
                switch (d) {
                    case 0xe:
                        if (keyVal == V[x]) {
                            PC += 2;
                        }
                        break;
                    case 0x1:
                        if (keyVal != V[x]) {
                            PC += 2;
                        }
                        break;
                    default:
                        System.out.println("Unknown Instruction " + Integer.toHexString(instr));
                        break;
                }
                break;
            case 0xf:
                switch (lsb) {
                    case 0x07:
                        V[x] = delayTimer;
                        break;
                    case 0x0a:
                        while (keyVal == 0xa) {
                        }
                        V[x] = keyVal;
                        break;
                    case 0x15:
                        // delayTimer = V[x];
                        Thread.sleep((int)(1 * V[x]));
                        break;
                    case 0x18:
                        // soundTimer = V[x];
                        Thread.sleep((int)(1 * V[x]));
                        break;
                    case 0x1e:
                        I = I + V[x];
                        break;
                    case 0x29:
                        I = 5 * V[x];
                        break;
                    case 0x33:
                        int val = V[x];
                        RAM[I + 2] = val % 10;
                        val = val / 10;
                        RAM[I + 1] = val % 10;
                        RAM[I] = val / 10;
                        break;
                    case 0x55:
                        for(int i = 0; i <= x; i++){
                            RAM[I + i] = V[i];
                        }
                        break;
                    case 0x65:
                        for(int i = 0; i <= x; i++){
                            V[i] = RAM[I + i];
                        }
                        break;
                    default:
                        System.out.println("Unknown Instruction " + Integer.toHexString(instr));
                        break;
                }
                break;
                default:
                    System.out.println("Unknown Instruction " + Integer.toHexString(instr));
                    break;
        }


    }

    // public static void main(String[] args) {
        // int[] maze = loadRom("roms/MAZE.dms");
        // for(int i : maze)
        //     System.out.println(i);
        // System.out.println("\n\n");
        // int[] particle = loadRom("roms/particle.ch8");
        // for(int i : particle)
        //     System.out.println(i);
        // int[] p = loadRom("roms/sai.ch8");
    // }
}