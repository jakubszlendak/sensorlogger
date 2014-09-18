import socket
from time import *
import numpy as np
from collections import deque
from matplotlib import pyplot as plt

class AnalogData:
#class to store points in buffered queue
    #constr
    def __init__(self,maxlLen):
        self.x = deque([0.0]*maxLen)
        self.y = deque([0.0]*maxLen)
        self.maxLen = maxLen
    def addToBuf(self,data,buffer):
        buffer.extend(data)
        
    def add(self,dataX,dataY,bufferX,bufferY):
        assert(len(dataX)==len(dataY))
        addToBuf(dataX,self.x)
        addToBuf(dataY,self.y)
        
    def flush(self):
        self.x.clear()
        self.y.clear()
    

class AnalogPlot:
    def __init__(self, data):
        plt.ion()
        self.xline, =plt.plot(data.x)
        self.yline, =plt.plot(data.y)
        data.flush()
        #plt.ylim([0,12])

    def update(self,data):
        self.xline.set_ydata(data.x)
        self.yline.set_ydata(data.y)
        data.flush()
        plt.draw()

def parseData(data,outerSplitStr=' | ',innerSplitStr=':'):
    lines=data.splitlines()
    parsedData = []
    for line in lines:
        dataVect = []
        splittedLine = line.split(outerSplitStr)
        if (len(splittedLine)==3):
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


try:
    while(True):
        data=client.recv(size)
        dataDecoded = data.decode()
        file.write(dataDecoded+'\n')
        print(parseData(dataDecoded))
        #print(values)
        sleep(0.03)
    client.close()

except KeyboardInterrupt:
    client.close()
    print("bye")
file.close()    
        


