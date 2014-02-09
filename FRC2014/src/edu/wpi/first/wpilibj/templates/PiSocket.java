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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
    OutputStream os = null;
    InputStream is = null;
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
    int bufferSize = 64;
    static byte[] receiveData;
    String rawData;
    boolean isDistanceExpected = false;

    
    public void connect() {
        try {
            connection = (SocketConnection) Connector.open("socket://10.33.73.104:3373", Connector.READ_WRITE, true);
            isConnected = true;
            System.out.println("Connected");
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Connection Failed");
            isConnected = false;
        }
        
        try {
            os = connection.openOutputStream();
            is = connection.openInputStream();
            //ISR = new InputStreamReader(connection.openInputStream(), "UTF-8");
        } catch (IOException ex){
            ex.printStackTrace();
         }
    }

    public void disconnect() throws IOException {
        is.close();
        os.close();
        connection.close();
        isConnected = false;
        System.out.println("Disconnected");

    }

    public void sendString(char message) throws IOException{
        try {
            os.flush();
            os.write(message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    /*public String receiveString() throws IOException {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                isReceiveThreadRunning = true;
                System.out.println("Receiving");
                String character = "";
                
                try {
                    character = (is);
                    
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
    }*/
    
    public void globalVariableUpdateAndListener(){
        Thread thread = new Thread(new Runnable(){
            public void run() {
                isUpdaterThreadRunning = true;
                while (!isShutdownRequested){
                    if (isConnected){
                        try {
                            
                            Thread.sleep(1000L);
                            sendString('a');
                            receiveString = getRawData();
                            receiveConverter(false, receiveString);
                            
                            Thread.sleep(100L);
                            sendString('b');
                            
                            receiveString = getRawData();
                            
                            receiveConverter(true, receiveString);
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
                
                }   
                try {
                    sendString('c');
                    disconnect();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                isUpdaterThreadRunning = false;
            }
        });
        
        if (!isUpdaterThreadRunning){
            thread.start();
        }
        
    }
    
    public void receiveConverter(boolean flipFlop, String receivedString){
        if (flipFlop){
            try {
                if (!receivedString.equals("0")){
                    receivedString = receivedString.trim();
                    distanceDouble = Double.parseDouble(receivedString);
                    isDistanceValid = true;
                } else {
                    System.out.println("Failed");
                    isDistanceValid = false;
                }
            } catch (NumberFormatException ex){
            }
        } else if (receivedString.equals("0")){
            isDistanceValid = false;
        } else {
            receivedString = receivedString.trim();
            if (receivedString.equalsIgnoreCase("True")){
                isHot = true;
            } else if (receivedString.equals("False")){
                isHot = false;
            }
        }
    } 
    
    public String getRawData() throws IOException {
        byte[] input;
        
        if (isConnected) {
            
            if(is.available() <= bufferSize) {
                input = new byte[is.available()]; //storage space sized to fit!
                receiveData = new byte[is.available()];
                is.read(input);
                for(int i = 0; (i < input.length) && (input != null); i++) {
                    receiveData[i] = input[i]; //transfer input to full size storage
                }
            } else {
                System.out.println("PI OVERFLOW");
                is.skip(is.available()); //reset if more is stored than buffer
                return null;
            }
            
            rawData = ""; //String to transfer received data to
            System.out.println("Raw Data: "+receiveData.length);
            for (int i = 0; i < receiveData.length; i++) {
                rawData += (char) receiveData[i]; //Cast bytes to chars and concatinate them to the String
            }
            System.out.println("Raw Data: " + rawData);
            return rawData;
        } else {
            connect();
            return null;
        }
    }

    

}
