import tkinter as tk
from tkinter import ttk
from devices import Screen, Keypad
from random import randint

class GUI():
    def __init__(self):
        self.root = tk.Tk()
        self.root.title("CHIP-8 Emulator")
        self.canvas = tk.Canvas(self.root, height = 500, width = 350, bg = 'blue', highlightthickness=0)
        self.display = tk.Canvas(self.canvas, height = 160, width = 320, bg = 'grey', highlightthickness=0)
        self.display.place(x = 15, y = 50)
        self.screen = Screen((32, 64), 5, self.display)
        self.keypad = Keypad(self.canvas)
        self.style = ttk.Style()
        self.style.configure('W.TButton', font = ('calibri', 15, 'bold'), foreground = 'black')
        self.canvas.pack()
    
    def run(self):
        self.root.mainloop()
    
    def runAfter(self, func, delay = 100):
        self.root.after(delay, func)
        self.root.mainloop()
    def update(self):
        self.root.update()