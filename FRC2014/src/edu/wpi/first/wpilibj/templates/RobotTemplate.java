package edu.wpi.first.wpilibj.templates;

/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/


import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
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
    Pickup pickup = new Pickup();
    PiSocket socket = new PiSocket();
    Diagnostics diag = new Diagnostics();
    LiveWindow liveWindow = new LiveWindow();
    Timer robotTimer = new Timer();
    Deadband deadband = new Deadband();
    
    int LX = 1;
    int LY = 2;
    int triggers = 3;
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
        if (isDisabled()){
            try {
                socket.disconnect();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        if (isEnabled() && isOperatorControl()){
            robotTimer.start();
            launcher.launcherTimer.start();
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
            
            socket.globalVariableUpdateAndListener();

            /*********"*****
             * Driver Code *
             ***************/            
            if (driveStick.isAPushed()){               
                System.out.println("Switching Orientation");
                drive.orientationSwitcher();
            }
            
            if (driveStick.isXPushed()){
                pickup.targetPos = 30;
            }
            
            if (driveStick.isYPushed()){
                pickup.targetPos = 120;
            }
            
            if (driveStick.getRawAxis(triggers) < -.3){
                pickup.grabBall();
            }
            
            if (driveStick.getRawAxis(triggers) > .3){
                pickup.releaseBall();
            }
                
            drive.speedModifier(driveStick.isLBHeld(), driveStick.isRBHeld());
            
            drive.drive(driveStick.getRawAxis(LX), driveStick.getRawAxis(RX), driveStick.getRawAxis(LY));


            
            System.out.println("Trigger Value: " + driveStick.getRawAxis(triggers));
            
                       
            /***********
             * Shooter *
             ***********/
            if (shootStick.isRBPushed()){
                pickup.grabBall();
            }
            
            if (shootStick.isLBPushed()){
                pickup.releaseBall();
            }
            
            if (deadband.zero(shootStick.getRawAxis(LY), .1) != 0){
                pickup.targetPos = pickup.moveAccordingToJoystick(shootStick.getRawAxis(LY));
            }
            
            if (shootStick.isBPushed()){
                //TODO: PREDEF CHARGEPOSA
            }
                
            if (shootStick.isYPushed()){
                //TODO: PREDEF CHARGEPOSB
            }
            
            if (shootStick.isXPushed()){
                //TODO: VISION CHARGEPOS
            }
            
            if (shootStick.isAPushed()){
                launcher.shoot();
            }
            
            /*****************
             * Miscellaneous *
             *****************/
            pickup.goToPos(.5); 
            
            SmartDashboard.putNumber("Distance", socket.distanceDouble);
            SmartDashboard.putBoolean("Is HOT", socket.isHot);
            SmartDashboard.putBoolean("isDistanceValid", socket.isDistanceValid);
            
            driveStick.clearButtons();
            shootStick.clearButtons();
        }
        try {
            socket.sendString('c');
            socket.disconnect();
        } catch (IOException ex) {
            ex.printStackTrace();
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
        double currentPressurePSI;
        double currentTime;
        robotTimer.start();
        launcher.launcherTimer.start();
        liveWindow.setEnabled(false);
        launcher.lockShootingPistons();
        
        while(isTest()){
            liveWindow.setEnabled(false);
            if(driveStick.isAPushed()){
                launcher.targetPressure += 5;
            } else if(driveStick.isBPushed()){
                launcher.targetPressure -= 5;
            } else if(driveStick.isYPushed()){
                launcher.returnCatapultToHome();
            } else if(driveStick.isStartPushed()){
                launcher.chargeShootingPistons();
            } else if(driveStick.isXPushed()){
                launcher.shoot();
            } else {
                //launcher.holdPressure();
                //launcher.retractingSolenoidL.set(false);
            }
            /*
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
            */

            /*if(driveStick.isXPushed()){
                launcher.lockShootingPistons();
            }*/

            if(driveStick.isBackPushed()){
                //launcher.retractShootingPistons();//aka get ready to shoot again
                launcher.doNothingLockingPistons();
            }
            
            if(shootStick.isRBPushed()){
                pickup.grabBall();
            } else if(shootStick.isLBPushed()){
                pickup.releaseBall();
            }
            if(shootStick.isStartPushed()){
                pickup.doNothingBall();
            }
            
            currentTime = robotTimer.get();
            
            currentPressurePSI = launcher.pressureInCylinder();
            analogInput = launcher.pressureSensor.getVoltage();
            potInput = launcher.potSensor.getVoltage();
            
            SmartDashboard.putNumber("Target Pressure", launcher.targetPressure);
            SmartDashboard.putNumber("Robot Time:", robotTimer.get());
            SmartDashboard.putNumber("Pressure PSI", currentPressurePSI);
            SmartDashboard.putNumber("Pressure Voltage", analogInput);
            SmartDashboard.putNumber("Pot Sensor", potInput);
            driveStick.clearButtons();
            shootStick.clearButtons();
        }
        
        
    }
}
