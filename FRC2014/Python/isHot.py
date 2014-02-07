import SimpleCV
import re
import thread
import socket
import unicodedata
import time

DEBUG = True

TCP_IP = 'localhost'
TCP_PORT = 3373
BUFFER_SIZE = 1024

camURL = 'http://192.168.0.4:80/jpg/image.jpg'

ratioLow = 1.1
ratioHigh = 1.6

screenSize = (800, 600)
if DEBUG: display = SimpleCV.Display(screenSize)

isHot = False
distance = 26.55

def main():
    #thread.start_new_thread(vision, ())
    thread.start_new_thread(server, ())
        
    while(True):
        time.sleep(1)

def vision():
    
    print("Starting Vision")
    
    while (True):

        img = SimpleCV.Image(camURL)
        
        greenDist = img.colorDistance(SimpleCV.Color.AQUAMARINE)
        filtered = img - greenDist
        
        blobs = filtered.findBlobs(minsize = 300)
        
        if blobs:
            rectangles = blobs.filter([b.isRectangle(0.3) for b in blobs])
            if rectangles:
                
                checkIsHot(rectangles)
                getDistance(rectangles)

                if DEBUG:
                    debugPrint(rectangles)
                    drawingLayer = SimpleCV.DrawingLayer(screenSize)
                    drawRects(rectangles, filtered, drawingLayer)
                    filtered.applyLayers()
                    
        if DEBUG: filtered.save(display)

def debugPrint(rectangles):
    if len(rectangles) > 1:
        rec1H = float(rectangles[-1].height())
        rec2W = float(rectangles[-2].width())
        print("ratio: " + str(rec1H / rec2W))
    print("isHot: " + str(isHot))
    print("distance: " + str(distance))

def drawRects(rectangles, filtered, drawingLayer):
    drawingLayer.centeredRectangle(rectangles[-1].coordinates(), (rectangles[-1].width(), rectangles[-1].height()), SimpleCV.Color.YELLOW, 1, False, -1)
    if len(rectangles) > 1:
        drawingLayer.centeredRectangle(rectangles[-2].coordinates(), (rectangles[-2].width(), rectangles[-2].height()), SimpleCV.Color.YELLOW, 1, False, -1) 
    filtered.addDrawingLayer(drawingLayer)

def checkIsHot(rectangles):
    if len(rectangles) > 1:
        rec1H = float(rectangles[-1].height())
        rec2W = float(rectangles[-2].width())
        ratio = rec1H / rec2W

        isHot = (ratio > ratioLow) and (ratio < ratioHigh)
    else:
        isHot = False

def getDistance(rectangles):
    distance = str(rectangles[-1].height())
    
def server():
    #Start the Server
    print("Starting Server")
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 3373)
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
        
        if not data: break
        
        print('Received Data: ' + data)

        if data[0] == "@":
            #Parse Data
            command = unicodedata.normalize("NFD", unicode(re.sub(r'[^a-zA-Z0-9]',"", unicode(data))))
            
            #Make Response
            print("Command: " + command)
            response = ""
            
            if command == "ISHOT":
                response = str(isHot)
            elif command == "DISTANCE":
                response = str(distance)
            elif command == "SHUTDOWN":
                shutdown()
                response = "AS YOU WISH, MY BENEVOLENT DICTATOR"
            else:
                response = "INVALID"
                
            #Format Response    
            response = "@" + response + "@\n"
            
            #Respond
            print('Sending: ' + response)
            conn.send(response.encode("utf-16"))
            
    print("Connection Closed")

def shutdown():
    command = "/usr/bin/sudo /sbin/shutdown -r now"
    import subprocess
    process = subprocess.Popen(command.split(), stdout=subprocess.PIPE)
    output = process.communicate()[0]
    print output

main()
