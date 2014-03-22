package edu.wpi.first.wpilibj.templates;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.templates.Diagnostics;
import edu.wpi.first.wpilibj.smartdashboard.*;

/**
 *
 * @author Jamie Dyer
 */
public class Launcher {

    DoubleSolenoid lockingSolenoids = new DoubleSolenoid(2, 4, 1);//currently in solenoid port 1, 8 for testing, TODO: change back to ports 5,6

    
    Solenoid pressureSolenoidR = new Solenoid(1, 5);
    Solenoid pressureSolenoidL = new Solenoid(1, 2);
    Solenoid exhaustSolenoid = new Solenoid(1, 6);
    
    Solenoid retractingSolenoidL = new Solenoid(1, 3);
    Solenoid retractingSolenoidR = new Solenoid(1, 7);
    
    AnalogChannel pressureSensor;
    //DigitalInput pressureSwitch = new DigitalInput(2);
    
    //Relay airCompressor = new Relay(1);
    
    Timer launcherTimer = new Timer();
    
    
    double lowestVoltagePressure = 0.5;
    double highestVoltagePressure = 4.5;
    double lowestPressure = 0;
    double highestPressure = 100;
    double pressurePSI;
    double slope;
    
    //double targetPressure = 0;//in PSI
    
    double  currentPressure;
    boolean isReadyToShoot;
    boolean hasShot = false;
    boolean isThreadRunning = false;
    boolean isShootThreadRunning = false;
    boolean isReturningThreadRunning = false;
    boolean extendThreadFlag = false;
    boolean isExtended = false;
    /**
     * Constructor that is here for no reason but we will not remove it. ITS BLACK MAGIC
     */
    public Launcher() {
        this.pressureSensor = new AnalogChannel(2);
    }

    public void unlockShootingPistons() {
        lockingSolenoids.set(DoubleSolenoid.Value.kReverse);
    }

    public void lockShootingPistons() {
        //if (isPistonHome.get()) {
            lockingSolenoids.set(DoubleSolenoid.Value.kForward);
        //}
    }
    public void doNothingLockingPistons(){
        lockingSolenoids.set(DoubleSolenoid.Value.kOff);
    }
    public void addPressure(){
        exhaustSolenoid.set(false);//close exhaust before adding pressure        
        pressureSolenoidR.set(true);
        pressureSolenoidL.set(true);
    }
    public void exhaustPressure(){
        pressureSolenoidR.set(false);//don't add pressure while we are exhausting pressure
        pressureSolenoidL.set(false);
        exhaustSolenoid.set(true);
    }
    public void holdPressure(){
        pressureSolenoidR.set(false);
        pressureSolenoidL.set(false);//don't add pressure
        exhaustSolenoid.set(false);//don't exhaust pressure
        retractingSolenoidL.set(false);//don't retract
        retractingSolenoidR.set(false);
    }
    public void retractShootingPistons(){
        exhaustSolenoid.set(true);//exhaust before returning catapult home
        retractingSolenoidL.set(true);
        retractingSolenoidR.set(true);
    }
    public void shoot(){
        final Thread thread = new Thread(new Runnable() {
            public void run() {
                extendThreadFlag = true;
                double timeWhenStarted = launcherTimer.get();
                while((launcherTimer.get() - timeWhenStarted) <= 1){
                    try {
                        Thread.sleep(1L);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    addPressure();
                }
                holdPressure();
                returnCatapultToHome();//if we decide we don't want automatic returning, get rid of this method
                extendThreadFlag = false;
                
            }
        });
        if (!extendThreadFlag) {
            unlockShootingPistons();
            isExtended = true;
            thread.start();
            hasShot = true;//we have shot
            
        }
    }
    
    public void extend(){
        final Thread thread = new Thread(new Runnable() {
            public void run() {
                isShootThreadRunning = true;
                double timeWhenStarted = launcherTimer.get();
                while((launcherTimer.get() - timeWhenStarted) <= 1){
                    try {
                        Thread.sleep(1L);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    addPressure();
                }
                holdPressure();
                //returnCatapultToHome();//if we decide we don't want automatic returning, get rid of this method
                
            }
        });
        if (!isShootThreadRunning) {
            unlockShootingPistons();
            thread.start();
            hasShot = true;//we have shot
        }
    }    

    public double pressureInCylinder(){
        slope = ((highestPressure - lowestPressure)/(highestVoltagePressure - lowestVoltagePressure));
        pressurePSI = (slope * (pressureSensor.getVoltage() - lowestVoltagePressure) + lowestPressure);
        return pressurePSI;
    }
    
    public void returnCatapultToHome(){
        final Thread thread = new Thread(new Runnable() {
            public void run() {
                isReturningThreadRunning = true;
                double timeWhenStarted = launcherTimer.get();
                while((launcherTimer.get() - timeWhenStarted) <= 5){
                    try {
                        Thread.sleep(1L);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }                    
                    retractShootingPistons();
                }
                lockShootingPistons();
                holdPressure();
                isReturningThreadRunning = false;
                isShootThreadRunning = false;
                isExtended = false;
            }
        });
        if (!isReturningThreadRunning) {
            thread.start();
            hasShot = false;//we haven't shot yet
        }
    }
    public void chargeShootingPistons(final double targetPressure) {
        final Thread thread = new Thread(new Runnable() {
            
            public void run() {
                isThreadRunning = true;
                lockShootingPistons();
                if(targetPressure != 0){
                    currentPressure = pressureInCylinder();
                    while (targetPressure < currentPressure) {
                        currentPressure = pressureInCylinder();
                        exhaustPressure();
                            try{
                                Thread.sleep(1);
                            } catch (Exception e){
                                //do nothing
                            }                        
                    }
                    holdPressure();
                    while (targetPressure > currentPressure) {
                        currentPressure = pressureInCylinder(); 
                        addPressure();
                            try{
                                Thread.sleep(1);
                            } catch (Exception e){
                                //do nothing
                            }                       
                    }
                    holdPressure();
                    while(!hasShot){
                        currentPressure = pressureInCylinder();
                        while((pressureInCylinder() - targetPressure) >= 2){
                            exhaustPressure();
                            try{
                                Thread.sleep(1);
                            } catch (Exception e){
                                //do nothing
                            }
                        }                        
                        while ((targetPressure - pressureInCylinder()) >= 2){
                            addPressure();
                            try{
                                Thread.sleep(1);
                            } catch (Exception e){
                                //do nothing
                            }
                        }
                            holdPressure();
                        try{
                            Thread.sleep(5);
                        } catch(Exception x){
                            //do nothing
                        }
                    }
                    holdPressure();
                    isThreadRunning = false;
                }
            }
        });
        if (!isThreadRunning) {
            thread.start();
        }
    }
    /*public void runAirCompressor(){
        if(pressureSwitch.get()){//if this is tripped, we have all the air pressure allowed (currently)
            airCompressor.set(Relay.Value.kOff);
        } else {
            airCompressor.set(Relay.Value.kOn);
        }
    }*/
    
/*
    public void shootThread() {
        final Thread thread = new Thread(new Runnable() {
            public void run() {
                System.out.println("In thread");
                isShootThreadRunning = true;
                testLockingSolenoid.set(false);
                pressureSolenoid.set(true);
                try {
                    long shootDelay = (long) SmartDashboard.getNumber("Shoot Delay");
                    System.out.println(shootDelay);
                    Thread.sleep(shootDelay);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                testLockingSolenoid.set(true);
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                pressureSolenoid.set(false);
                testLockingSolenoid.set(false);
                isShootThreadRunning = false;
                System.out.println(isShootThreadRunning);
            }
        });
        if (!isShootThreadRunning) {
            thread.start();
        }

    } */
}
