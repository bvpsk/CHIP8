import tkinter as tk
from tkinter import ttk
class Pixel:
    def __init__(self, parent, r, c, scale, active):
        self.parent = parent
        self.active = active
        self.element = parent.create_rectangle(r*scale, c*scale, (r+1)*scale, (c+1)*scale, fill = 'black', outline = "")
        self.val = 0
    def toggle(self, val = 1):
        self.val ^= val
        self.parent.itemconfig(self.element, fill = 'green' if self.val == 1 else 'black')
        if self.val == 1:
            if self not in self.active:
                self.active.add(self)
        elif self in self.active:
            self.active.remove(self)
    def clear(self):
        self.val = 0
        self.parent.itemconfig(self.element, fill = 'black')


class Screen:
    def __init__(self, res, scale, parent):
        self.res = res
        self.scale = scale
        self.parent = parent
        self.pixels = []
        self.active = set()
        for r in range(res[1]):
            arr = []
            for c in range(res[0]):
                arr.append(Pixel(parent, r, c, scale, self.active))
            self.pixels.append(arr)
    
    def draw(self, x, y, val):
        val = list(map(int, bin(val).lstrip("0b")))
        val = [0] * (8 - len(val)) + val
        y = y % 32
        flag = 0
        for i in range(8):
            pixel = self.pixels[(x + i) % 64][y]
            v = pixel.val
            pixel.toggle(val[i])
            flag |= (v & ~(pixel.val))
        return flag
    def clearScreen(self):
        for pixel in self.active:
            pixel.clear()
        self.active.clear()


class Keypad:
    def __init__(self, parent):
        self.val = 0x10
        self.parent = parent
        self.keypad = tk.Canvas(parent, height = 240, width = 240, bg = "red", highlightthickness=0)
        self.var = tk.IntVar()

        # self.buttons = []
        # for c in '0123456789ABCDEF':
        #     self.buttons.append(ttk.Button(self.keypad, text = c, style = 'W.TButton', command = lambda : self.keyPress(int(c, 16))))

        

        # self.buttons[0].grid(row = 3, column = 1)
        # self.buttons[1].grid(row = 0, column = 0)
        # self.buttons[2].grid(row = 0, column = 1)
        # self.buttons[3].grid(row = 0, column = 2)
        # self.buttons[4].grid(row = 1, column = 0)
        # self.buttons[5].grid(row = 1, column = 1) 
        # self.buttons[6].grid(row = 1, column = 2)
        # self.buttons[7].grid(row = 2, column = 0)
        # self.buttons[8].grid(row = 2, column = 1)
        # self.buttons[9].grid(row = 2, column = 2)
        # self.buttons[0xa].grid(row = 3, column = 0)
        # self.buttons[0xb].grid(row = 3, column = 2)
        # self.buttons[0xc].grid(row = 0, column = 3)
        # self.buttons[0xd].grid(row = 1, column = 3)
        # self.buttons[0xe].grid(row = 2, column = 3)
        # self.buttons[0xf].grid(row = 3, column = 3)

        self.B1 = ttk.Button(self.keypad, text = '1', style = 'W.TButton', command = lambda: self.keyPress(1))
        self.B0 = ttk.Button(self.keypad, text = '0', style = 'W.TButton', command = lambda: self.keyPress(0))
        self.B2 = ttk.Button(self.keypad, text = '2', style = 'W.TButton', command = lambda: self.keyPress(2))
        self.B3 = ttk.Button(self.keypad, text = '3', style = 'W.TButton', command = lambda: self.keyPress(3))
        self.B4 = ttk.Button(self.keypad, text = '4', style = 'W.TButton', command = lambda: self.keyPress(4))
        self.B5 = ttk.Button(self.keypad, text = '5', style = 'W.TButton', command = lambda: self.keyPress(5))
        self.B6 = ttk.Button(self.keypad, text = '6', style = 'W.TButton', command = lambda: self.keyPress(6))
        self.B7 = ttk.Button(self.keypad, text = '7', style = 'W.TButton', command = lambda: self.keyPress(7))
        self.B8 = ttk.Button(self.keypad, text = '8', style = 'W.TButton', command = lambda: self.keyPress(8))
        self.B9 = ttk.Button(self.keypad, text = '9', style = 'W.TButton', command = lambda: self.keyPress(9))
        self.Ba = ttk.Button(self.keypad, text = 'A', style = 'W.TButton', command = lambda: self.keyPress(10))
        self.Bb = ttk.Button(self.keypad, text = 'B', style = 'W.TButton', command = lambda: self.keyPress(11))
        self.Bc = ttk.Button(self.keypad, text = 'C', style = 'W.TButton', command = lambda: self.keyPress(12))
        self.Bd = ttk.Button(self.keypad, text = 'D', style = 'W.TButton', command = lambda: self.keyPress(13))
        self.Be = ttk.Button(self.keypad, text = 'E', style = 'W.TButton', command = lambda: self.keyPress(14))
        self.Bf = ttk.Button(self.keypad, text = 'F', style = 'W.TButton', command = lambda: self.keyPress(15))


        self.B0.grid(row = 3, column = 1)
        self.B1.grid(row = 0, column = 0)
        self.B2.grid(row = 0, column = 1)
        self.B3.grid(row = 0, column = 2)
        self.B4.grid(row = 1, column = 0)
        self.B5.grid(row = 1, column = 1) 
        self.B6.grid(row = 1, column = 2)
        self.B7.grid(row = 2, column = 0)
        self.B8.grid(row = 2, column = 1)
        self.B9.grid(row = 2, column = 2)
        self.Ba.grid(row = 3, column = 0)
        self.Bb.grid(row = 3, column = 2)
        self.Bc.grid(row = 0, column = 3)
        self.Bd.grid(row = 1, column = 3)
        self.Be.grid(row = 2, column = 3)
        self.Bf.grid(row = 3, column = 3)

        self.keypad.place(x = 30, y = 300)
    
    def waitForKey(self):
        print("Waiting....")
        self.keypad.wait_variable(self.var)
        print(f'{self.var} : Waiting ended....')
    
    def reset(self):
        self.val = 0x10

    def keyPress(self, val):
        print(f'{hex(val)} is pressed.')
        self.var.set(val)
        self.val = val
        self.keypad.after(100, self.reset)
    
    
        