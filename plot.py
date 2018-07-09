import matplotlib.pyplot as plt
import matplotlib.animation as animation
import time

class graph:
    
    def __init__(self):
        self.fig = plt.figure()
        self.ax1 = self.fig.add_subplot(1,1,1)
        self.x = '0'
        self.y = '0' 

    def animate(self,i):
        #global self.x, self.y
        self.filedata = open("sampleText.txt","a+")
        self.x = str(int(self.x) + 1)
        self.y = str(int(self.y) + 2)	
        self.filedata.write(self.x+","+self.y+"\n")	
        self.filedata.close() 
        self.pullData = open("sampleText.txt","r").read()
        self.dataArray = pullData.split('\n')
        self.xar = []
        self.yar = []
        for eachLine in self.dataArray:
            if len(eachLine)>1:
                self.x,self.y = eachLine.split(',')
                self.xar.append(int(self.x))
                self.yar.append(int(self.y))
        self.ax1.clear()
        self.ax1.plot(self.xar,self.yar)
    
    def start(self):
        self.ani = animation.FuncAnimation(self.fig, self.animate, interval=1000)
        plt.show()