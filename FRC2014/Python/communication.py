import socket
import unicodedata
import re
import thread
import time


TCP_IP = 'localhost'
TCP_PORT = 3373
BUFFER_SIZE = 1024

isHot = True
distance = 23.4

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
            data = unicode(data)
            data = data.replace("@", "")
            data = data.replace("\n", "")
            command = re.sub(r'[^a-zA-Z0-9]','', data)
                
            #Make Response
            print("Command: " + command)
            response = ""
            
            if unicodedata.normalize("NFD", unicode(command)) == unicodedata.normalize("NFD", unicode("ISHOT")):
                response = str(isHot)
            elif unicodedata.normalize("NFD", unicode(command)) == unicodedata.normalize("NFD", unicode("DISTANCE")):
                response = str(distance)
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
    
def main():
        thread.start_new_thread(server, ())
        while(1):
            time.sleep(1)
            
main()

#server()
