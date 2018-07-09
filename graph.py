import matplotlib.pyplot as plt
import matplotlib.animation as animation
import time
import numpy

class graph:
        
    def __init__(self):
        self.fig = plt.figure()
        self.ax1 = self.fig.add_subplot(1,1,1)
        self.x = '3'
        self.y = '0' 

    def animate(self, i):
        print(self.x,self.y)
        #deletes first row
        self.f = open("sampleText.txt","r+")
        self.d = self.f.readlines()
        self.f.seek(0)
        for i in self.d:
            if i != self.d[0]:
                self.f.write(i)
        self.f.truncate()
        self.f.close()

        #writes new data for plotting
        self.filedata = open("sampleText.txt","a+")
        self.x = str(int(self.x) + 1)
        self.y = str(int(self.y) + 2)	
        self.filedata.write(self.x+","+self.y+"\n")	
        self.filedata.close()

        #save data 
        self.filedata = open("values.txt","a+")
        self.x = str(int(self.x) + 1)
        self.y = str(int(self.y) + 2)	
        self.filedata.write(self.x+","+self.y+"\n")	
        self.filedata.close()

        #plot 
        self.pullData = open("sampleText.txt","r").read()
        self.dataArray = self.pullData.split('\n')
        self.xar = []
        self.yar = []
        for eachLine in self.dataArray:
            if len(eachLine)>1:
                self.x,self.y = eachLine.split(',')
                self.xar.append(int(self.x))
                self.yar.append(int(self.y))
        self.ax1.clear()
        self.ax1.plot(self.xar,self.yar)
        self.graph_image = np.array(self.fig.canvas.renderer._renderer)
        return self.graph_image

    def start(self):     
        self.ani = animation.FuncAnimation(self.fig, self.animate, interval=1000)
        self.show = plt.show()

if __name__ ==  "__main__":
    graph1 = graph()
    graph1.start()