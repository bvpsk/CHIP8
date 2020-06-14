from cpu import CPU
from gui import GUI
from time import sleep

cpu = CPU()

mem = []
romdata = open('../roms/pong.ch8', 'rb').read()
for index, val in enumerate(romdata):
    mem.append(val)
cpu.load(mem)

print("Emulator Started....")
app = GUI()
cpu.screen = app.screen
cpu.keypad = app.keypad


while 1:
    app.update()
    # sleep(0.1)
    cpu.fde()
print("Emulator Stopped.....")
app.run()

