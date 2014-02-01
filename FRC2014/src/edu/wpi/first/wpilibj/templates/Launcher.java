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

    DoubleSolenoid lockingSolenoids = new DoubleSolenoid(1, 5, 6);//currently in solenoid port 1 for testing
    
    Solenoid pressureSolenoidR = new Solenoid(1, 1);
    Solenoid pressureSolenoidL = new Solenoid(1, 2);
    Solenoid exhaustSolenoid = new Solenoid(2, 1);//defaults to exhaust
    
    //Solenoid testLockingSolenoid = new Solenoid(8);//do we need this?
    
    DoubleSolenoid retractingSolenoid = new DoubleSolenoid(1, 3, 4);
    
    DigitalInput isPistonHome = new DigitalInput(1); //curently in I/O port 1 for testing

    AnalogChannel pressureSensor = new AnalogChannel(1);

    boolean isReadyToShoot;
    double  currentPressure;
    boolean isThreadRunning = false;
    boolean isShootThreadRunning = false;

    public void unlockShootingPistons() {
        lockingSolenoids.set(DoubleSolenoid.Value.kForward); //false is allowing the pisotn to retract releasing the shooting piston
    }

    public void lockShootingPistons() {
        if (isPistonHome.get()) {
            lockingSolenoids.set(DoubleSolenoid.Value.kReverse); //true is allowing the piston to extend locking the shooting pistions
        }
    }
    public void addPressure(){
        pressureSolenoidR.set(true);
        pressureSolenoidL.set(true);
    }
    public void exhaustPressure(){
        exhaustSolenoid.set(true);
    }
    public void retractShootingPistons(){
        retractingSolenoid.set(DoubleSolenoid.Value.kForward);
    }

    public boolean chargeShootingPistons(final double targetPressure) {
        final Thread thread = new Thread(new Runnable() {
            public void run() {
                isThreadRunning = true;
                while (targetPressure > currentPressure) {
                    currentPressure = pressureSensor.getValue(); //we want to change this .getValue(that returns a voltage) to instead return pressure in psi
                    addPressure();
                }
                while (targetPressure < currentPressure) {
                    currentPressure = pressureSensor.getValue(); //we want to change this .getValue(that returns a voltage) to instead return pressure in psi 
                    exhaustPressure();
                }
            }
        });
        if (!isThreadRunning) {
            thread.start();
        }
        return isReadyToShoot;
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
