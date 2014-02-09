/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.templates.Diagnostics;
import edu.wpi.first.wpilibj.smartdashboard.*;

/**
 *
 * @author Jamie Dyer
 */
public class Launcher {

    DoubleSolenoid lockingSolenoids = new DoubleSolenoid(2, 5, 6);//currently in solenoid port 1, 8 for testing, TODO: change back to ports 5,6

    
    Solenoid pressureSolenoidR = new Solenoid(1, 8);//not the final port number
    Solenoid pressureSolenoidL = new Solenoid(2, 1);
    Solenoid exhaustSolenoid = new Solenoid(2, 3);//defaults to exhaust
    
    //Solenoid testLockingSolenoid = new Solenoid(8);//do we need this?
    
    Solenoid retractingSolenoidL = new Solenoid(2, 4);
    //Solenoid retractingSolenoidR = new Solenoid(2, 4);
    
    DigitalInput isPistonHome = new DigitalInput(1); //curently in I/O port 1 for testing, when true the piston is home and ready for launching algorithm

    AnalogChannel potSensor = new AnalogChannel(1);
    AnalogChannel pressureSensor;
    
    double lowestVoltagePressure = 0.5;
    double highestVoltagePressure = 4.5;
    double lowestPressure = 0;
    double highestPressure = 100;
    double pressurePSI;
    double slope;
    
    double  currentPressure;
    boolean isReadyToShoot;
    boolean hasShot;
    boolean isThreadRunning = false;
    boolean isShootThreadRunning = false;
    RobotTemplate robot = new RobotTemplate();

    public Launcher() {
        this.pressureSensor = new AnalogChannel(2);
    }

    public void unlockShootingPistons() {
        lockingSolenoids.set(DoubleSolenoid.Value.kForward);
    }

    public void lockShootingPistons() {
        //if (isPistonHome.get()) {
            lockingSolenoids.set(DoubleSolenoid.Value.kReverse);
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
    }
    public void retractShootingPistons(){
        exhaustSolenoid.set(true);//exhaust before returning catapult home
        retractingSolenoidL.set(true);    
    }
    public void shoot(){
        addPressure();
        unlockShootingPistons();
        hasShot = true;
    }
    public double pressureInCylinder(){
        slope = ((highestPressure - lowestPressure)/(highestVoltagePressure - lowestVoltagePressure));
        pressurePSI = (slope * (pressureSensor.getVoltage() - lowestVoltagePressure) + lowestPressure);
        return pressurePSI;
    }
    
    public void returnCatapultToHome(){
        final Thread thread = new Thread(new Runnable() {
            public void run() {
                double timeWhenStarted = robot.robotTimer.get();
                while((robot.robotTimer.get() - timeWhenStarted) <= 2){
                    retractShootingPistons();
                }
                lockShootingPistons();
            }
        });
    }
    public void chargeShootingPistons(final double targetPressure) {
        final Thread thread = new Thread(new Runnable() {
            public void run() {
                isThreadRunning = true;
                if(targetPressure != 0){
                    while (targetPressure > currentPressure) {
                        currentPressure = pressureInCylinder(); 
                        addPressure();
                    }
                    while (targetPressure < currentPressure) {
                        currentPressure = pressureInCylinder();
                        exhaustPressure();
                    }
                    while(!hasShot){
                        if ((targetPressure - currentPressure) >= 1){
                            addPressure();
                        } else if((currentPressure - targetPressure) <= -1){
                            exhaustPressure();
                        }
                        try{
                            Thread.sleep(5);
                        } catch(Exception x){
                            //do nothing
                        }
                    }
                    hasShot = true;
                }
            }
        });
        if (!isThreadRunning) {
            thread.start();
        }
    }
    
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
