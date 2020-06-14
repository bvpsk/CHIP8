from random import randint
from time import sleep
import sys
class CPU:
    _load_start = 0x200
    def __init__(self, mem_size = 4096):
        self.mem_size = mem_size
        self.V = [0x00] * 16
        # self.stack = [0x00] * 16
        self.stack = [] #
        self.RAM = [0x00] * mem_size
        self.I = 0x0000
        self.PC = 0x0000
        self.SP = 0x00
        self.sound = 0x00
        self.delay = 0x00
        self.screen = None
        self.keypad = None

        hexChars = [
              0xF0, 0x90, 0x90, 0x90, 0xF0, # 0
              0x20, 0x60, 0x20, 0x20, 0x70, # 1
              0xF0, 0x10, 0xF0, 0x80, 0xF0, # 2
              0xF0, 0x10, 0xF0, 0x10, 0xF0, # 3
              0x90, 0x90, 0xF0, 0x10, 0x10, # 4
              0xF0, 0x80, 0xF0, 0x10, 0xF0, # 5
              0xF0, 0x80, 0xF0, 0x90, 0xF0, # 6
              0xF0, 0x10, 0x20, 0x40, 0x40, # 7
              0xF0, 0x90, 0xF0, 0x90, 0xF0, # 8
              0xF0, 0x90, 0xF0, 0x10, 0xF0, # 9
              0xF0, 0x90, 0xF0, 0x90, 0x90, # A
              0xE0, 0x90, 0xE0, 0x90, 0xE0, # B
              0xF0, 0x80, 0x80, 0x80, 0xF0, # C
              0xE0, 0x90, 0x90, 0x90, 0xE0, # D
              0xF0, 0x80, 0xF0, 0x80, 0xF0, # E
              0xF0, 0x80, 0xF0, 0x80, 0x80 # F
          ]
        for i in range(len(hexChars)):
            self.RAM[i] = hexChars[i]
        
    
    def load(self, rom):
        for i in range(len(rom)):
            self.RAM[self._load_start + i] = rom[i]
        self.PC = self._load_start
        self.SP = 0x00
        self.sound = 0x00
        self.delay = 0x00
        self.stack = [] #
    
    def cycle(self):
        try:
            self.fde()
        except:
            pass
    
    def fde(self):
        #   Fetch
        #   I = 16*16 * msb + lsb = abcd
        msb = self.RAM[self.PC]
        lsb = self.RAM[self.PC + 1]
        a = msb // 16
        b = msb % 16
        c = lsb // 16
        d = lsb % 16
        self.PC = self.PC + 2

        #   Decode and Execute
        if a == 0x0:
            if d == 0x0:
                #   Clear display
                self.screen.clearScreen()
            else:
                #   Return from a subroutine.
                # self.PC = self.stack[self.SP]
                # self.SP -= 1
                self.PC = self.stack.pop() #
        elif a == 0x1:
            #   Jump to location nnn.
            self.PC = 256 * b + lsb
        elif a == 0x2:
            #   Call subroutine at nnn.
            # self.SP += 1
            # self.stack[self.SP] = self.PC
            self.stack.append(self.PC) #
            self.PC = 256 * b + lsb
        elif a == 0x3:
            #   Skip next instruction if Vx = kk.
            if self.V[b] == lsb:
                self.PC += 2
        elif a == 0x4:
            #   Skip next instruction if Vx != kk.
            if self.V[b] != lsb:
                self.PC = self.PC + 2
        elif a == 0x5:
            #   Skip next instruction if Vx = Vy.
            if self.V[b] == self.V[c]:
                self.PC += 2        
        elif a == 0x6:
            #   Set Vx = kk.
            self.V[b] = lsb
        elif a == 0x7:
            #   Set Vx = Vx + kk.
            self.V[b] += lsb
        elif a == 0x8:
            #   Arithmetic, Logical and Shift operations
            if d == 0x0:
                #   Set Vx = Vy.
                self.V[b] = self.V[c]
            elif d == 0x1:
                #   Set Vx = Vx OR Vy.
                self.V[b] = self.V[b] | self.V[c]
            elif d == 0x2:
                #   Set Vx = Vx AND Vy.
                self.V[b] = self.V[b] & self.V[c]
            elif d == 0x3:
                #   Set Vx = Vx XOR Vy.
                self.V[b] = self.V[b] ^ self.V[c]
            elif d == 0x4:
                #   Set Vx = Vx + Vy, set VF = carry.
                self.V[b] += self.V[c]
                self.V[0xf] = 0
                if self.V[b] > 0xff:
                    self.V[0xf] = 1
                    self.V[b] -= 256
            elif d == 0x5:
                #   Set Vx = Vx - Vy, set VF = NOT borrow.
                self.V[0xf] = 1
                if self.V[b] < self.V[c]:
                    self.V[0xf] = 0
                self.V[b] -= self.V[c]
                if self.V[b] < 0:
                    self.V[b] += 256
            elif d == 0x6:
                #   Set Vx = Vx SHR 1.
                self.V[0xf] = self.V[b] & 0x1
                self.V[b] = self.V[b] >> 1
            elif d == 0x7:
                #   Set Vx = Vy - Vx, set VF = NOT borrow.
                self.V[0xf] = 0
                if self.V[b] < self.V[c]:
                    self.V[0xf] = 1
                self.V[b] = self.V[c] - self.V[b]
                if self.V[b] < 0:
                    self.V[b] += 256
            elif d == 0xE:
                #   Set Vx = Vx SHL 1.
                self.V[0xf] = self.V[b] & 0x80
                self.V[b] = self.V[b] << 1
                if self.V[b] > 255:
                    self.V[b] -= 256
        elif a == 0x9:
            #   Skip next instruction if Vx != Vy.
            if self.V[b] != self.V[c]:
                self.PC += 2
        elif a == 0xa:
            #   Set I = nnn.
            self.I = lsb + 256 * b
        elif a == 0xb:
            #   Jump to location nnn + V0.
            self.PC = lsb + 256 * b + self.V[0]
        elif a == 0xc:
            #   Set Vx = random byte AND kk.
            self.V[b] = randint(0, 255) & lsb
        elif a == 0xd:
            #   Display n-byte sprite starting at memory location I at (Vx, Vy), set VF = collision.
            self.V[0xf] = 0
            for i in range(d):
                flag = self.screen.draw(self.V[b], self.V[c] + i, self.RAM[self.I + i])
                self.V[0xf] |= flag
        elif a == 0xe:
            #   Keyboard instructions
            if lsb == 0x9e:
                #   Skip next instruction if key with the value of Vx is pressed.
                if self.keypad.val == self.V[b]:
                    self.PC += 2
            elif lsb == 0xa1:
                #   Skip next instruction if key with the value of Vx is not pressed.
                if self.keypad.val != self.V[b]:
                    self.PC += 2
        elif a == 0xf:
            if lsb == 0x07:
                #   Set Vx = delay timer value.
                self.V[b] = self.delay
            elif lsb == 0x0a:
                #   Wait for a key press, store the value of the key in Vx.
                self.keypad.waitForKey()
                self.V[b] = self.keypad.val
            elif lsb == 0x15:
                #   Set delay timer = Vx.
                self.delay = self.V[b]
                sleep(1/120)
            elif lsb == 0x18:
                #   Set sound timer = Vx.
                # self.sound = self.V[b]
                sleep(1/120)
                # sys.stdout.write('\a')
            elif lsb == 0x1e:
                #   Set I = I + Vx.
                self.I += self.V[b]
            elif lsb == 0x29:
                #   Set I = location of sprite for digit Vx.
                self.I = 5 * self.V[b]
            elif lsb == 0x33:
                #   Store BCD representation of Vx in memory locations I, I+1, and I+2.
                val = self.V[b]
                self.RAM[self.I + 2] = val % 10
                val //= 10
                self.RAM[self.I + 1] = val % 10
                val //= 10
                self.RAM[self.I] = val
            elif lsb == 0x55:
                #   Store registers V0 through Vx in memory starting at location I.
                for i in range(b + 1):
                    self.RAM[self.I + i] = self.V[i]
            elif lsb == 0x65:
                #   Read registers V0 through Vx from memory starting at location I.
                for i in range(b + 1):
                    self.V[i] = self.RAM[self.I + i]
        
        
        






