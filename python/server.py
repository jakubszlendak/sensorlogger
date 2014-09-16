import socket
from time import *

def parseData(data,outerSplitStr=' | ',innerSplitStr=':'):
    lines=data.splitlines()
    parsedData = []
    for line in lines:
        dataVect = []
        splittedLine = line.split(outerSplitStr)
        print(splittedLine)
        if (len(splittedLine)==3):
            for element in splittedLine:
                valueStr=element.split(innerSplitStr)
                try:
                    x=float(valueStr[-1])
                    dataVect.append(x)
                    
                except ValueError:
                    print('zesralo sie')
                    pass
                print(valueStr[-1])
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
        


