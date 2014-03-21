package edu.wpi.first.wpilibj.templates;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


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
    double pixelDistanceDouble;
    boolean isHot;
    boolean isDistanceValid;
    int timeoutCounter = 0;
    int bufferSize = 64;
    static byte[] receiveData;
    String rawData;
    boolean isDistanceExpected = false;
    boolean isConnectThreadRunning = false;

    /**
     * Method to connect to socket server and initialize IO streams
     */
    public void connect() {
        Thread thread = new Thread(new Runnable(){
            public void run(){ 
                System.out.println("Trying to Connect");
                isConnectThreadRunning = true;
                try {
                    connection = (SocketConnection) Connector.open("socket://10.33.73.3:3373", Connector.READ_WRITE, true);
                    System.out.println("Connected");
                } catch (IOException ex) {
                    //ex.printStackTrace();   
                    System.out.println("Connection Failed");
                }

                try {
                    os = connection.openOutputStream();
                    is = connection.openInputStream();
                    isConnected = true;
                    //ISR = new InputStreamReader(connection.openInputStream(), "UTF-8");
                } catch (IOException ex){
                    //ex.printStackTrace();
                }
            isConnectThreadRunning = false;
            }
        });
        if (!isConnectThreadRunning){
            thread.start();
        }
    }
    /**
     * Tests whether the connection is active by sending a null value
     */
    public void isConnected(){
        try {
            os.write('\n');
            isConnected = true;
        } catch (Exception ex){
            isConnected = false;
            //ex.printStackTrace();
        }
    }
    /**
     * Disconnects cRIO from PI socket
     */
    public void disconnect() {
        try{
            is.close();
            os.close();
            connection.close();
            isConnected = false;
            System.out.println("Disconnected");
        } catch (Exception ex){
            System.out.println("Disconnect Failed");
            //ex.printStackTrace();
        }

    }
    /**
     * Sends a char message to the PI to get data
     * @param message message as a char that asks for pixel isHot (a), pixel distance (b), and shutdown (c)
     */
    public void sendChar(char message) {
        try {
            os.flush();
            os.write(message);
            //System.out.println("Message: " + message);
        } catch (IOException ex) {
            //ex.printStackTrace();
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
    /**
     * Method that controls takes all input and parses to give readable data in main thread
     */
    public void globalVariableUpdateAndListener(){
        Thread thread = new Thread(new Runnable(){
            public void run() {
                isUpdaterThreadRunning = true;
                while (!isShutdownRequested){
                    isConnected();
                    if (isConnected){
                        try {
                            
                            Thread.sleep(1000L);
                            sendChar('a');
                            receiveString = getRawData();
                            receiveConverter(false, receiveString);
                            
                            Thread.sleep(100L);
                            sendChar('b');
                            
                            receiveString = getRawData();
                            
                            receiveConverter(true, receiveString);
                            //System.out.println("Distance: " + distanceDouble);
                            //System.out.println("ISHOT: " + isHot);
                        } catch (Exception ex) {
                            //ex.printStackTrace();
                        }
                    } else {
                        connect();
                    }
                
                }   
                sendChar('c');
                disconnect();
                isUpdaterThreadRunning = false;
            }
        });
        
        if (!isUpdaterThreadRunning){
            thread.start();
        }
        
    }
    /**
     * Method that takes receives data and allocates to either isHot or distance
     * @param flipFlop boolean to tell method whether a double or boolean is expected, true means double expected
     * @param receivedString String received from socket server after parsing
     */
    public void receiveConverter(boolean flipFlop, String receivedString){
        if (flipFlop){
            try {
                if (!receivedString.equals("0")){
                    receivedString = receivedString.trim();
                    pixelDistanceDouble = Double.parseDouble(receivedString);
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
    /**
     * Method that takes all data received and parses it into a string useable for the robot
     * @return Returns patched string
     */
    public String getRawData() {
        try {
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
        } catch (Exception ex){
            //////ex.printStackTrace();
            return null;
        }
    }

    

}
