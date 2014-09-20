import socket
from time import *
import numpy as np
from collections import deque
from matplotlib import pyplot as plt

class AnalogData:
#class to store points in buffered queue
    #constr
    def __init__(self):
        self.x = deque([0.0])
        self.y = deque([0.0])
        
    def addToBuf(self,data,buffer):
        buffer.extend(data)
        
    def add(self,dataX,dataY):
        assert(len(dataX)==len(dataY))
        self.addToBuf(dataX,self.x)
        self.addToBuf(dataY,self.y)
        
    def flush(self):
        self.x.clear()
        self.y.clear()
    

class AnalogPlot:
    def __init__(self, data):
        plt.ion()
        self.xline, =plt.plot(data.x,data.y)
        #self.yline, =plt.plot(data.y)
        data.flush()
        plt.ylim([0,12])

    def update(self,data):
        self.xline.set_ydata(data.x)
        self.xline.set_xdata(data.y)
       # self.yline.set_xdata(data.y)
        data.flush()
        plt.draw()

def parseData(data,dataVectLen,outerSplitStr=' | ',innerSplitStr=':'):
    lines=data.splitlines()
    parsedData = []
    for line in lines:
        dataVect = []
        splittedLine = line.split(outerSplitStr)
        if (len(splittedLine)==dataVectLen):
            for element in splittedLine:
                valueStr=element.split(innerSplitStr)
                try:
                    x=float(valueStr[-1].replace(',','.'))
                    dataVect.append(x)
                    
                except ValueError:
                    dataVect.append(0)
                    pass
            parsedData.append(dataVect)
    return parsedData
    
host = ''
port = 8888
backlog = 5
size = 1024
s = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
s.bind((host,port))
s.listen(backlog)
client,addres = s.accept()
file = open("sensorlogger.txt", "w")
file2 = open("dataoutput.txt","w")

plotData = AnalogData()
plotData.add([0],[0])
plot = AnalogPlot(plotData)
jebutnyWektorDanych = []

try:
    while(True):
        data=client.recv(size)
       #print(data)
        dataDecoded = data.decode()
        file.write(dataDecoded+'\n')
        downloadedData=parseData(dataDecoded,4)
        #print(downloadedData)
        #plotData.add(downloadedData[0],downloadedData[-1])
        #plot.update(plotData)
        jebutnyWektorDanych.extend(downloadedData)
        #file2.write(str(downloadedData[-1]) + ' ' + str(downloadedData[0])+'\n')
        sleep(0.03)
        
    client.close()


except KeyboardInterrupt:
    client.close()
    print("bye")

for each_element in jebutnyWektorDanych:
    print(str(each_element[-1]) + ' ' + str(each_element[0]))
    file2.write(str(each_element[-1]) + ' ' + str(each_element[0])+'\n')


file.close()
file2.close()
        


