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
 * @author Jamie
 */
public class Launcher {

    DoubleSolenoid lockingSolenoid = new DoubleSolenoid(1, 2);//currently in solenoid port 1 for testing
    Solenoid pressureSolenoid = new Solenoid(3);
    Solenoid exhaustSolenoid = new Solenoid(4);
    Solenoid testLockingSolenoid = new Solenoid(5);

    DigitalInput isPistonHome = new DigitalInput(1); //curently in I/O port 1 for testing

    AnalogChannel pressureSensor = new AnalogChannel(1);

    boolean isReadyToShoot;
    double currentPressure;
    boolean isThreadRunning = false;
    boolean isShootThreadRunning = false;

    public void unlockShootingPistons() {
        lockingSolenoid.set(DoubleSolenoid.Value.kForward); //false is allowing the pisotn to retract releasing the shooting piston
    }

    public void lockShootingPistons() {
        if (isPistonHome.get()) {
            lockingSolenoid.set(DoubleSolenoid.Value.kReverse); //true is allowing the piston to extend locking the shooting pistions
        }
    }

    public boolean chargeShootingPistons(final double targetPressure) {
        final Thread thread = new Thread(new Runnable() {
            public void run() {
                isThreadRunning = true;
                while (targetPressure > currentPressure) {
                    currentPressure = pressureSensor.getValue(); //we want to change this .getValue(that returns a voltage) to instead return pressure in psi
                    pressureSolenoid.set(true);
                }
                while (targetPressure < currentPressure) {
                    currentPressure = pressureSensor.getValue(); //we want to change this .getValue(that returns a voltage) to instead return pressure in psi 
                    exhaustSolenoid.set(true);
                }
            }
        });
        if (!isThreadRunning) {
            thread.start();
        }
        return isReadyToShoot;
    }

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
                isShootThreadRunning = false;
                System.out.println(isShootThreadRunning);
            }
        });
        if (!isShootThreadRunning) {
            thread.start();
        }

    }
}
