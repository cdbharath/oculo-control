###  ONLY ALGO IMPLEMENTATION ###

databyte = 0
readstate = 0
numchannels = 8
errorcounter = 0
bytecounter = 0
bytebuffer = []

if readstate is 0:
    if databyte is bytes('0Xa0', 'utf-8'):
        readstate = readstate + 1
elif readstate is 1:
    channels_in_packet = int(databyte)/4 - 1
    if channels_in_packet is not numchannels:
        errorcounter = errorcounter + 1
        readstate = 0
    else:
        bytecounter = 0
        readstate = readstate + 1
elif readstate is 2:
    bytebuffer[bytecounter] = databyte
    bytecounter = bytecounter + 1
    if bytecounter is 4:
        
            

