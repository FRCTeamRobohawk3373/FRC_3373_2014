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
    BufferedReader BR;
    boolean isReceiveThreadRunning = false;
    static char serverChar;
    boolean isConnected = false;

    
    public void connect() {
        try {
            connection = (SocketConnection) Connector.open("socket://10.33.73.5:3500", Connector.READ_WRITE, true);
            os = connection.openDataOutputStream();
            is = connection.openDataInputStream();
            BR = new BufferedReader(new InputStreamReader(connection.openInputStream(), "UTF-16"));
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

    public char receiveString() throws IOException {
 Thread thread = new Thread(new Runnable() {
            public void run() {
                isReceiveThreadRunning = true;
                System.out.println("Receiving");
                String character = "";
                
                try {
                    character = BR.readLine();
                    
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
    

}
