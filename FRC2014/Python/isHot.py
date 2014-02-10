import SimpleCV
import re
import thread
import socket
import unicodedata
import time
import math
import platform
print('Running in Python ' + platform.python_version())

DEBUG = True

TCP_IP = 'localhost'
TCP_PORT = 3373
BUFFER_SIZE = 1024

camURL = 'http://10.33.73.4:80/jpg/image.jpg'

ratioLow = 0.5
ratioHigh = 0.9

screenSize = (800, 600)
if DEBUG: display = SimpleCV.Display(screenSize)

isHot = False
distance = 0

isVisionRunning = False
isServerRunning = False

def main():
    
    global isVisionRunning
    global isServerRunning
    
    thread.start_new_thread(vision, ())
    thread.start_new_thread(server, ())
    
    while(True):
        time.sleep(1)
        if isVisionRunning == False:
            thread.start_new_thread(vision, ())
        if isServerRunning == False:
            thread.start_new_thread(server, ())

def vision():
    
    global isVisionRunning
    global distance
    
    isVisionRunning = True
    
    print("Starting Vision")
    
    try:
        while (True):
    
            img = SimpleCV.Image(camURL)
            
            greenDist = img.colorDistance(SimpleCV.Color.AQUAMARINE)
            filtered = img - greenDist
            
            #TODO Filter out white light
            
            blobs = filtered.findBlobs(minsize = 300)
            
            if blobs:
                rectangles = blobs.filter([b.isRectangle(0.3) for b in blobs])
                if rectangles and DEBUG:
                    checkIsHot(rectangles)
                    getDistance(rectangles)
                    debugPrint(rectangles)
                    
                    drawingLayer = SimpleCV.DrawingLayer(screenSize)
                    drawRects(rectangles, filtered, drawingLayer)
                    filtered.applyLayers()
                elif rectangles:
                    checkIsHot(rectangles)
                    getDistance(rectangles)
                elif DEBUG:
                    print('No Rectangles')
                else:
                    distance = 0
                    isHot = False
                        
            if DEBUG: filtered.save(display)
            
    except:
        isVisionRunning = False

def debugPrint(rectangles):
    print('\n')
    
    if len(rectangles) > 1:
        # Note, minRectHeight will grab width, minRectWidth will grab height
        
        horizontalWidth = float(rectangles[-2].minRectWidth())
        horizontalHeight = float(rectangles[-2].minRectHeight())
        verticalWidth = float(rectangles[-1].minRectWidth())
        verticalHeight = float(rectangles[-1].minRectHeight())
        
        #Meant to swap values in case they are/need to be switched
        if horizontalHeight > horizontalWidth: horizontalHeight, horizontalWidth = horizontalWidth, horizontalHeight
        if verticalHeight < verticalWidth: verticalHeight, verticalWidth = verticalWidth, verticalHeight

        print("Horizontal rectangle: \n\tWidth: " + str(horizontalWidth) + "\n\tHeight: " + str(horizontalHeight))
        print("Vertical rectangle: \n\tWidth: " + str(verticalWidth) + "\n\tHeight: " + str(verticalHeight))            
        print("Ratio (W/H): " + str(horizontalWidth / verticalHeight))
        
        # 0.694 = magic number from experiments
        #Test Code
        v = (horizontalWidth / verticalHeight) * (1.0/0.694)
        print("Cos factor: " + str(v))
        if v > 1:
            v = 1
        print('Angle: ' + str(math.acos(v) * (180/3.141592652589793238462643383)))

    print("isHot: " + str(isHot))
    print("distance: " + str(distance))

def drawRects(rectangles, filtered, drawingLayer):
    rectangles[-1].drawMinRect(drawingLayer, (255,0,0), 5, 255)
    rectangles[-2].drawMinRect(drawingLayer, (255,0,0), 5, 255)

    filtered.addDrawingLayer(drawingLayer)

def checkIsHot(rectangles):
    
    global isHot
    if len(rectangles) > 1:
        rec1W = float(rectangles[-2].width())
        rec2H = float(rectangles[-1].height())
        ratio = rec1W / rec2H

        isHot = (ratio > ratioLow) and (ratio < ratioHigh)
    else:
        isHot = False

def getDistance(rectangles):
    
    global distance
    #if len(rectangles) > 1:
        #rec1W = float(rectangles[-2].minRectWidth())
        #rec2H = float(rectangles[-1].minRectHeight())
        
        #if ((rec1W / rec2H) * (64.0 / 47.0)) <= 1.0:
            
            #angle = math.acos((rec1W / rec2H) * (64.0 / 47.0))
            #hypotenuse = rec2H
            #distance = math.cos(angle) * hypotenuse
        
    if len(rectangles) > 0:
        distance = str(rectangles[-1].minRectHeight())
    else:
        distance = 0
    
def server():
    
    global isServerRunning
    
    isServerRunning = True
    
    try:
        #Start the Server
        print("Starting Server")
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, TCP_PORT)
        s.bind(('', TCP_PORT))
        s.listen(1)
        
        #Pause and Wait for Connection
        print("Waiting for Connection...")
        conn, addr = s.accept()
        print 'Connected with:', addr
        
        #Recieve and Send Data 
        while (True):
            print("Waiting For Data...")
            data = conn.recv(BUFFER_SIZE)
            
            if not data: 
                isServerRunning = False
                break
            
            print('Received Data: ' + data)
    
            command = unicodedata.normalize("NFD", unicode(re.sub(r'[^a-zA-Z0-9]',"", unicode(data))))
    
            if command != "":
                
                #Make Response
                print("Command: " + command)
                response = ""
                
                if command == "b":
                    response = str(isHot)
                elif command == "a":
                    response = str(distance)
                elif command == "c":
                    if DEBUG:
                        shutdown()
                        response = "AS YOU WISH MY BELEVOLENT DICTATOR"
                    else:
                        response = "NO"
                else:
                    response = "INVALID"
                    
                #Format Response    
                response = (response + "\n").encode('utf-8')
                
                #Respond
                print('Sending: ' + response)
                conn.send(response)
                
        print("Connection Closed")
    except:
        isServerRunning = False

def shutdown():
    command = "/usr/bin/sudo /sbin/shutdown -r now"
    import subprocess
    process = subprocess.Popen(command.split(), stdout=subprocess.PIPE)
    output = process.communicate()[0]
    print output

main()
