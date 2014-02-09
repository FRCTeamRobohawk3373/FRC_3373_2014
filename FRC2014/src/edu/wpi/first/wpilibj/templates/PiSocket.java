/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import com.sun.squawk.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
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
    InputStreamReader ISR;
    boolean isReceiveThreadRunning = false;
    static String serverChar;
    boolean isConnected = false;
    static boolean isShutdownRequested = false;
    boolean isUpdaterThreadRunning = false;
    String receiveString = null;
    double distanceDouble;
    boolean isHot;
    boolean isDistanceValid;
    int timeoutCounter = 0;

    
    public void connect() {
        try {
            connection = (SocketConnection) Connector.open("socket://10.33.73.104:3373", Connector.READ_WRITE, true);
            os = connection.openDataOutputStream();
            is = connection.openDataInputStream();
            ISR = new InputStreamReader(connection.openDataInputStream(), "UTF-8");
            isConnected = true;
            System.out.println("Connected");
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Connection Failed");
            isConnected = false;
        }
    }

    public void disconnect() throws IOException {
        is.close();
        os.close();
        connection.close();
        isConnected = false;
        System.out.println("Disconnected");

    }

    public void sendString(String message) throws IOException{
        try {
            os.writeChars(message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public String receiveString() throws IOException {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                isReceiveThreadRunning = true;
                System.out.println("Receiving");
                String character = "";
                
                try {
                    character = readLine(ISR);
                    
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(character);
                //if (character != null){
                //character = String.valueOf(character);
                

                
                //}
                System.out.println(character);
                double characterDouble = Double.parseDouble(character);
                System.out.println(characterDouble + 5);


                //disconnect();


                //Iasso's IP: 209.249.85.70:3373
                isReceiveThreadRunning = false;
            }
        });

        if (!isReceiveThreadRunning) {
            thread.start();
        }
        
        return serverChar;
    }
    
    public void globalVariableUpdateAndListener(){
        Thread thread = new Thread(new Runnable(){
            public void run() {
                isUpdaterThreadRunning = true;
                while (!isShutdownRequested){
                    if (isConnected){
                        try {
                            sendString("DISTANCE");
                            receiveString = receiveString();
                            Thread.sleep(100L);
                            sendString("ISHOT");
                            receiveString = receiveString();
                            receiveConverter();
                            System.out.println("Distance: " + distanceDouble);
                            System.out.println("ISHOT: " + isHot);
                        } catch (IOException ex) {
                            connect();
                            ex.printStackTrace();
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        connect();
                    }
                
                try {
                    sendString("SHUTDOWN");
                    disconnect();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                isUpdaterThreadRunning = false;
                
                }   
            }
        });
        
        if (!isUpdaterThreadRunning){
            thread.start();
        }
        
    }
    
    public void receiveConverter(){
        if (!receiveString.equals("0") && !receiveString.equals("True") && !receiveString.equals("False")){
            distanceDouble = Double.parseDouble(receiveString);
            isDistanceValid = true;
        } else if (receiveString.equals("0")){
            isDistanceValid = false;
        }
        if (receiveString.equals("TRUE")){
            isHot = true;
        } else if (receiveString.equals("FALSE")){
            isHot = false;
        }
    } 
    
    public String readLine(Reader reader) throws IOException {
            StringBuffer line = new StringBuffer();
            int c = reader.read();

            while (c != -1 && c != '\n') {
                line.append((char)c);
                c = reader.read();
            }

            if (line.length() == 0) {
                return null;
            }

            return line.toString();
        }    

    

}
