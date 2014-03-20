package edu.wpi.first.wpilibj.templates;

/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/


import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStationLCD;
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
    LookUpTable lookup = new LookUpTable();
    DriverStationLCD dsLCD = DriverStationLCD.getInstance();
    Compressor compressor = new Compressor(3,1);
    
    int LX = 1;
    int LY = 2;
    int triggers = 3;
    int RX = 4;
    int RY = 5;
    int DP = 6;
    
    double[] distanceToPressureArray = new double[] {9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25};
    double[] distanceArray = new double[] {25, 24, 23, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9};
    double[] pressureArray = new double[]{};
    double[] pixelArray = new double[]{54, 54.53, 55.3, 55.4, 55.5, 56.13, 56.4, 100.21, 103.25, 110.11, 116.42, 124.19, 132, 141.92, 153.92, 165.21, 179, };
    double safeZoneForShooting = 2.0;//must find a value for when the ball grabber is out of the way and we can shoot
    double calculatedDistance;
    


    public void autonomous() {
        socket.connect();
        socket.globalVariableUpdateAndListener();
        pickup.targetPos = pickup.minVoltage;
        pickup.goToPos(.5);
        launcher.targetPressure = 55;
        launcher.chargeShootingPistons();
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        if (socket.isHot){
            launcher.shoot();
        } else {
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            launcher.shoot();
        }
        drive.drive(0,0,.75);
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        drive.drive(0,0,0);
        
    }

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl() {
        SmartDashboard.putNumber("Shoot Delay", 500);
        if (isDisabled()){
            //socket.disconnect();
        }
        
        if (isEnabled() && isOperatorControl()){
            robotTimer.start();
            launcher.launcherTimer.start();
        }
        
        while (isEnabled() && isOperatorControl()) {
            if (!socket.isConnected){
                socket.connect();
            }
            socket.isConnected();
            compressor.start();
            try {
                Thread.sleep(10L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            if (socket.isConnected){
                System.out.println("CalculatedDistance: " + lookup.lookUpValue(socket.pixelDistanceDouble, pixelArray, distanceArray));
            }
            
            calculatedDistance = lookup.lookUpValue(socket.pixelDistanceDouble, pixelArray, distanceArray);
            
            if (calculatedDistance > 26){
                socket.isDistanceValid = false;
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
                pickup.targetPos = pickup.pickupVoltage;
            }
            
            if (driveStick.isYPushed()){
                pickup.targetPos = pickup.dropoffVoltage;
            }
            
            if (driveStick.getRawAxis(triggers) < -.3){
                pickup.grabBall();
            }
            
            if (driveStick.getRawAxis(triggers) > .3){
                pickup.releaseBall();
            }
                
            drive.speedModifier(driveStick.isLBHeld(), driveStick.isRBHeld());
            
            drive.drive(driveStick.getRawAxis(LX), driveStick.getRawAxis(RX), driveStick.getRawAxis(LY));


            
            
                       
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
                launcher.targetPressure = 55; //put a pressure(psi) for a pre-defined place on the field
                launcher.chargeShootingPistons();
            }
                
            if (shootStick.isYPushed()){
                //TODO: PREDEF CHARGEPOSB
                launcher.targetPressure = 30; //put a pressure(psi) for a pre-defined place on the field
                launcher.chargeShootingPistons();
            }
            
            if (shootStick.isXPushed()){
                if (socket.isDistanceValid){
                    launcher.targetPressure = lookup.lookUpValue(calculatedDistance, distanceToPressureArray, pressureArray);
                }
            }
            
            if (shootStick.isAPushed()){
                if(pickup.pickupPot.getVoltage() <= safeZoneForShooting){
                    launcher.shoot();                   
                }

            }
            
            if (shootStick.isStartPushed()){
                if (!launcher.isExtended && pickup.pickupPot.getVoltage() <= safeZoneForShooting){
                    launcher.extend();
                } else {
                    launcher.returnCatapultToHome();
                }
            }
            
            /*****************
             * Miscellaneous *
             *****************/
            pickup.goToPos(.5); 
            //launcher.runAirCompressor();
            
            SmartDashboard.putBoolean("isConnected: ", socket.isConnected);
            SmartDashboard.putNumber("Distance", socket.pixelDistanceDouble);
            SmartDashboard.putBoolean("Is HOT", socket.isHot);
            SmartDashboard.putBoolean("isDistanceValid", socket.isDistanceValid);
            SmartDashboard.putBoolean("Pressure", compressor.getPressureSwitchValue());
            
            dsLCD.updateLCD();
            driveStick.clearButtons();
            shootStick.clearButtons();
        }
        socket.sendChar('c');
        socket.disconnect();
        compressor.stop();
        //compressor.free();
    }

    /**
     * This function is called once each time the robot enters test mode.
     */
    public void test() {
        /********************
        * Shooter Test Code *
        ********************/
        double analogInput;
        double currentPressurePSI;
        double currentTime;
        robotTimer.start();
        launcher.launcherTimer.start();
        liveWindow.setEnabled(false);
        launcher.lockShootingPistons();
        
        while(isTest() && isEnabled()){
            //launcher.runAirCompressor();
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
                if(pickup.pickupPot.getVoltage() <= safeZoneForShooting){
                    launcher.shoot();                   
                }
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
            
            pickup.angleDiag(shootStick.isAHeld(), shootStick.isBHeld());
            
            currentTime = robotTimer.get();
            
            currentPressurePSI = launcher.pressureInCylinder();
            analogInput = launcher.pressureSensor.getVoltage();
            pickup.actuateTalon.set(shootStick.getRawAxis(LY) * .5);
            
            SmartDashboard.putNumber("Target Pressure", launcher.targetPressure);
            SmartDashboard.putNumber("Robot Time:", robotTimer.get());
            SmartDashboard.putNumber("Pressure PSI", currentPressurePSI);
            SmartDashboard.putNumber("Pressure Voltage", analogInput);
            SmartDashboard.putNumber("Pickup Position", pickup.getPickupPos());
            driveStick.clearButtons();
            shootStick.clearButtons();
        }
        
        
    }
}
