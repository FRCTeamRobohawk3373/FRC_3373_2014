/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.templates.*;
import java.io.IOException;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RobotTemplate extends SimpleRobot {

    /**
     * This function is called once each time the robot enters autonomous mode.
     */

    SuperJoystick driveStick = new SuperJoystick(1);
    SuperJoystick shootStick = new SuperJoystick(2);
    Drive drive = Drive.getInstance();
    Launcher launcher = new Launcher();
    Pickup BallGrabber = new Pickup();
    PiSocket socket = new PiSocket();
    LiveWindow liveWindow = new LiveWindow();
    
    int LX = 1;
    int LY = 2;
    int Triggers = 3;
    int RX = 4;
    int RY = 5;
    int DP = 6;

    public void autonomous() {

    }

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl() {
        SmartDashboard.putNumber("Shoot Delay", 500);
        if (isDisabled() && isOperatorControl()){
            
        }
        
        while (isEnabled() && isOperatorControl()) {
            if (!socket.isConnected){
                System.out.println("Trying to connect");
                socket.connect();
            }
            try {
                Thread.sleep(100L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            drive.drive(driveStick.getRawAxis(LX), driveStick.getRawAxis(RX), driveStick.getRawAxis(LY));
            if (driveStick.isAPushed()) {
                System.out.println("A Presser");
                //launcher.shootThread();
            }
            if (driveStick.isBPushed()){     
                try {
                        socket.sendString("Test \n");
                    } catch (IOException ex) {
                        System.out.println("Sending failed");
                        ex.printStackTrace();
                    }
            }
            
            try {
                System.out.print(socket.receiveString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            driveStick.clearButtons();
            shootStick.clearButtons();
        }
    }

    /**
     * This function is called once each time the robot enters test mode.
     */
    public void test() {
        /********************
        * Shooter Test Code *
        ********************/
        double analogInput;
        double potInput;
        liveWindow.setEnabled(false);
        while(isTest()){
            liveWindow.setEnabled(false);
            
            if(driveStick.isAHeld()){
                launcher.addPressure();
            } else if(driveStick.isBHeld()){
                launcher.exhaustPressure();
            } else if(driveStick.isYHeld()){
                launcher.shoot();//aka fire
            } else if(driveStick.isStartHeld()){
                launcher.retractShootingPistons();
            } else {
                launcher.holdPressure();
                launcher.retractingSolenoidL.set(false);//if nothing then don't retract
            }

            if(driveStick.isXPushed()){
                launcher.lockShootingPistons();
            }

            if(driveStick.isBackPushed()){
                //launcher.retractShootingPistons();//aka get ready to shoot again
                launcher.doNothingLockingPistons();
            }
            
            if(shootStick.isRBPushed()){
                BallGrabber.grabBall();
            } else if(shootStick.isLBPushed()){
                BallGrabber.releaseBall();
            }
            if(shootStick.isStartPushed()){
                BallGrabber.doNothingBall();
            }
        
            analogInput = launcher.pressureSensor.getVoltage();
            potInput = launcher.potSensor.getVoltage();
            
            SmartDashboard.putNumber("Pressure Sensor", analogInput);
            SmartDashboard.putNumber("Pot Sensor", potInput);
            driveStick.clearButtons();
            shootStick.clearButtons();
        }
        
        
    }
}
