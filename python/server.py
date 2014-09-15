import socket
from time import *

def parseData(data,outerSplitStr=' | ',innerSplitStr=':'):
    lines=data.splitlines()
    for line in lines:
        splittedLine = line.split(outerSplitStr)
        print(splittedLine)
        for element in splittedLine:
            valueStr=element.split(innerSplitStr)
            print(valueStr)

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
        parseData(dataDecoded)
        #values = dataDecoded.split(' | ')
        #print(values)
        sleep(0.03)
    client.close()

except KeyboardInterrupt:
    client.close()
    print("bye")
file.close()    
        


