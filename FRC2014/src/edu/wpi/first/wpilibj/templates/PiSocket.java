/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;
import javax.microedition.io.StreamConnection;

/**
 *
 * @author RoboHawks
 */
public class PiSocket {

    SocketConnection connection;
    DataOutputStream os = null;
    DataInputStream is = null;
    boolean isReceiveThreadRunning = false;
    static char serverChar;
    boolean isConnected = false;

    public void connect() {
        try {
            connection = (SocketConnection) Connector.open("socket://10.33.73.5:3500", Connector.READ_WRITE, true);
            os = connection.openDataOutputStream();
            is = connection.openDataInputStream();
            isConnected = true;
        } catch (IOException ex) {
            ex.printStackTrace();
            isConnected = false;
        }
    }

    public void disconnect() throws IOException {
        is.close();
        os.close();
        connection.close();
        isConnected = false;

    }

    public void sendString() throws IOException {
        os.writeChars("Test");

    }

    public char receiveString() throws IOException {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                isReceiveThreadRunning = true;
                try {
                    char threadChar = is.readChar();
                    serverChar = threadChar;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                isReceiveThreadRunning = false;
            }
        });
        if (!isReceiveThreadRunning) {
            thread.start();
        }
        
        return serverChar;
    }

}
