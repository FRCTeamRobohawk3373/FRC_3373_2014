import socket


TCP_IP = 'localhost'
TCP_PORT = 3373
BUFFER_SIZE = 1024

def server():
    #Start the Server
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.bind(('', TCP_PORT))
    s.listen(1)
    
    #Pause and Wait for Connection
    conn, addr = s.accept()
    print 'Connection address:', addr
    
    #Recieve and Send Data 
    while (True):
        print("Waiting For Data")
        data = conn.recv(BUFFER_SIZE)
        print('Received Data: ' + data)

        if not data: break

        if len(data) >= 1:
            if data[0] == "@":
                #Parse Data
                command = data.replace("@", "")
            
                #Do Things Here!!
                print("Command: " + command)
                response = "@" + command + "@\n" #echo
                
                #Respond
                print('Sending: ' + response)
                conn.send(response.encode("utf-16"))
    
def shutdown():
    command = "/usr/bin/sudo /sbin/shutdown -r now"
    import subprocess
    process = subprocess.Popen(command.split(), stdout=subprocess.PIPE)
    output = process.communicate()[0]
    print output

server()
