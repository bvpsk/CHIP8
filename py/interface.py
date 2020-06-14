import tkinter as tk
from tkinter import ttk
import numpy as np
from PIL import Image, ImageTk
from screen import Screen
from random import randint


def keyPress(val):
    print(f'{hex(val)} is pressed.')
    if val == 0xf:
        screen.clearScreen()
    elif val == 1:
        screen.draw(0, 0, 0x20)
        screen.draw(0, 1, 0x60)
        screen.draw(0, 2, 0x20)
        screen.draw(0, 3, 0x20)
        screen.draw(0, 4, 0x70)




root = tk.Tk()
canvas = tk.Canvas(root, height = 500, width = 350, bg = 'blue', highlightthickness=0)


display = tk.Canvas(canvas, height = 160, width = 320, bg = 'grey', highlightthickness=0,)
display.place(x = 15, y = 50)

screen = Screen((32, 64), 5, display)

keypad = tk.Canvas(canvas, height = 240, width = 240, bg = "red", highlightthickness=0,)


style = ttk.Style()
style.configure('W.TButton', font = ('calibri', 15, 'bold'), foreground = 'black')


B0 = ttk.Button(keypad, text = '0', style = 'W.TButton', command = lambda: keyPress(0))
B1 = ttk.Button(keypad, text = '1', style = 'W.TButton', command = lambda: keyPress(1))
B2 = ttk.Button(keypad, text = '2', style = 'W.TButton', command = lambda: keyPress(2))
B3 = ttk.Button(keypad, text = '3', style = 'W.TButton', command = lambda: keyPress(3))
B4 = ttk.Button(keypad, text = '4', style = 'W.TButton', command = lambda: keyPress(4))
B5 = ttk.Button(keypad, text = '5', style = 'W.TButton', command = lambda: keyPress(5))
B6 = ttk.Button(keypad, text = '6', style = 'W.TButton', command = lambda: keyPress(6))
B7 = ttk.Button(keypad, text = '7', style = 'W.TButton', command = lambda: keyPress(7))
B8 = ttk.Button(keypad, text = '8', style = 'W.TButton', command = lambda: keyPress(8))
B9 = ttk.Button(keypad, text = '9', style = 'W.TButton', command = lambda: keyPress(9))
Ba = ttk.Button(keypad, text = 'A', style = 'W.TButton', command = lambda: keyPress(10))
Bb = ttk.Button(keypad, text = 'B', style = 'W.TButton', command = lambda: keyPress(11))
Bc = ttk.Button(keypad, text = 'C', style = 'W.TButton', command = lambda: keyPress(12))
Bd = ttk.Button(keypad, text = 'D', style = 'W.TButton', command = lambda: keyPress(13))
Be = ttk.Button(keypad, text = 'E', style = 'W.TButton', command = lambda: keyPress(14))
Bf = ttk.Button(keypad, text = 'F', style = 'W.TButton', command = lambda: keyPress(15))


B0.grid(row = 3, column = 1)
B1.grid(row = 0, column = 0)
B2.grid(row = 0, column = 1)
B3.grid(row = 0, column = 2)
B4.grid(row = 1, column = 0)
B5.grid(row = 1, column = 1) 
B6.grid(row = 1, column = 2)
B7.grid(row = 2, column = 0)
B8.grid(row = 2, column = 1)
B9.grid(row = 2, column = 2)
Ba.grid(row = 3, column = 0)
Bb.grid(row = 3, column = 2)
Bc.grid(row = 0, column = 3)
Bd.grid(row = 1, column = 3)
Be.grid(row = 2, column = 3)
Bf.grid(row = 3, column = 3)


keypad.place(x = 30, y = 300)
canvas.pack()
root.mainloop()