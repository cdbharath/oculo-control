import matplotlib.pyplot as plt
import matplotlib.animation as animation
import time

import serial
import keyboard

class plot:
    
    def __init__():
        fig = plt.figure()
        ax1 = self.fig.add_subplot(1,1,1)
        x = '0'
        y = '0' 
        iterator = 0
        xar = []
        yar = []

    def animate(i, x_value, y_value):
        
        global xar, yar, iterator, x, y    
        
        if(iterator>500):
            xar.pop(0)
            yar.pop(0)
        
        filedata = open("values.txt","a+")
        x = str(round(float(x) + 1,3))
        y = str(round(float(value),3))	
        filedata.write(x+","+y+"\n")
        filedata.close()
        
        xar.append(int(iterator))
        yar.append(float(value))
    
        ax1.clear()
        ax1.plot(xar,yar)
        iterator = iterator + 1

    def start(x_value, y_value):        
        ani = animation.FuncAnimation(fig, animate(x_value, y_value), interval=1)
        plt.show()
