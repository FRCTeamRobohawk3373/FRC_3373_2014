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
    //Diagnostics diag = new Diagnostics();
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
    double[] pixelArray = new double[]{54, 54.53, 55.3, 55.4, 55.5, 56.13, 56.4, 100.21, 103.25, 110.11, 116.42, 124.19, 132, 141.92, 153.92, 165.21, 179};
    double safeZoneForShooting = 5;//must find a value for when the ball grabber is out of the way and we can shoot TODO
    double calculatedDistance;
    boolean hasLocked = false;
    
    boolean isHotInit = false;
    

    public void autonomous() {
        if (isAutonomous() && isDisabled()){
            socket.connect();
        }
        socket.isDisabled = false;
        launcher.isDisabled = false;
        pickup.isDisabled = false;
        compressor.start();
        
        socket.globalVariableUpdateAndListener();
        pickup.targetPos = pickup.minVoltage;
        //pickup.goToPos(.5);
        launcher.lockShootingPistons();
        //launcher.targetPressure = 55;
        launcher.chargeShootingPistons(55);
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        
        pickup.targetPos = pickup.pickupVoltage;
        pickup.goToPos(.5); 
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        


        drive.drive(0,.4488,-.75);
        try {
            Thread.sleep(2500L);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        drive.drive(0,0,0);
        
        if (socket.isHot){
            isHotInit = true;
        }
        
        System.out.println("isHotInit: " + isHotInit);
        
        if (isHotInit){
            launcher.shoot();
        }
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        
        if (!isHotInit){
            launcher.shoot();
        }
        
        //launcher.shoot();
        compressor.stop();
    }

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl() {
        //SmartDashboard.putNumber("Shoot Delay", 500);
        if (isDisabled() && isOperatorControl()){
            socket.isDisabled = true;
            launcher.isDisabled = true;
            pickup.isDisabled = true;
        }
        

        
        if (isEnabled() && isOperatorControl()){
            robotTimer.start();
            launcher.launcherTimer.start();
        }
        
        while (isEnabled() && isOperatorControl()) {
            socket.isDisabled = false;
            launcher.isDisabled = false;
            pickup.isDisabled = false;
            
            /*if (!socket.isConnected){
                socket.connect();
            }*/
            //socket.isConnected();
            compressor.start();
            try {
                Thread.sleep(10L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            /*if (socket.isConnected){
                System.out.println("CalculatedDistance: " + lookup.lookUpValue(socket.pixelDistanceDouble, pixelArray, distanceArray));
            }*/
           
            //calculatedDistance = lookup.lookUpValue(socket.pixelDistanceDouble, pixelArray, distanceArray);
            
            /*if (calculatedDistance > 26){
                 socket.isDistanceValid = false;
            }*/
            
            
            //socket.globalVariableUpdateAndListener();

            /*********"*****
             * Driver Code *
             ***************/            
            if (driveStick.isAPushed()){               
                System.out.println("Switching Orientation");
                drive.orientationSwitcher();
            }
            
            if (driveStick.isXPushed()){
                pickup.targetPos = pickup.pickupVoltage;
                pickup.goToPos(.5); 
            }
            
            if (driveStick.isYPushed()){
                System.out.println("Dropping Off");
                pickup.targetPos = pickup.dropoffVoltage;
                pickup.goToPos(.5); 
            }
            //System.out.println("Joystick X: " + driveStick.getRawAxis(RX));
            //System.out.println("Joystik Y: " + driveStick.getRawAxis(LY));
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
            System.out.println("Target POS: " + pickup.targetPos);
            double manualMovement = deadband.zero((shootStick.getRawAxis(LY) * .75), .1);
            
            if (pickup.getPickupPos() <= pickup.maxVoltage && manualMovement < 0){
                System.out.println("Upper Limit");
                manualMovement = 0;
            }
            
            if (pickup.getPickupPos() >= pickup.minVoltage && manualMovement > 0){
                System.out.println("Lower Limit");
                manualMovement = 0;
            }
            System.out.println("Movement" + manualMovement);
            if (!pickup.isGoToRunning){
                //pickup.targetPos = pickup.moveAccordingToJoystick(shootStick.getRawAxis(LY));
                //if (pickup.getPickupPos() < pickup.maxVoltage && pickup.getPickupPos() > pickup.minVoltage){
                pickup.actuateTalon.set(manualMovement); //TODO FIX ABOVE IF 
  
                //}
            } 
             
            if (shootStick.isBPushed()){
                //TODO: PREDEF CHARGEPOSA
                //launcher.targetPressure = 55; //put a pressure(psi) for a pre-defined place on the field
                launcher.chargeShootingPistons(55);
            }
                
            if (shootStick.isYPushed()){
                //TODO: PREDEF CHARGEPOSB
                //launcher.targetPressure = 30; //put a pressure(psi) for a pre-defined place on the field
                launcher.chargeShootingPistons(30);
            }
            
            if (shootStick.isXPushed()){
                //if (socket.isDistanceValid){
                    //launcher.chargeShootingPistons(lookup.lookUpValue(calculatedDistance, distanceToPressureArray, pressureArray));
                //}
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
            
            //launcher.runAirCompressor();
            dsLCD.updateLCD();
            driveStick.clearButtons();
            shootStick.clearButtons();
            
            System.out.println(pickup.pickupPot.getVoltage());

        }
        //socket.sendChar('c');
        socket.disconnect();
        compressor.stop();
        hasLocked = false;
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
                    
            socket.isDisabled = false;
            launcher.isDisabled = false;
            pickup.isDisabled = false;
            if (!socket.isConnected){
                socket.connect();
            }
            socket.isConnected();
            socket.globalVariableUpdateAndListener();
            System.out.println("isHot" + socket.isHot);
            System.out.println("isConnected" + socket.isConnected);
            calculatedDistance = lookup.lookUpValue(socket.pixelDistanceDouble, pixelArray, distanceArray);
            System.out.println("Calcultated Distance" + calculatedDistance);
            compressor.start();
            liveWindow.setEnabled(false);
            /*if(driveStick.isAPushed()){
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
            }*/
            
            if(driveStick.isAHeld()){
                launcher.addPressure();
            } else if(driveStick.isBHeld()){
                launcher.exhaustPressure();
            } else if(driveStick.isYPushed()){
                launcher.shoot();//aka fire
            } else if(driveStick.isXPushed()){
                launcher.returnCatapultToHome();
            } else {
                launcher.holdPressure();
                //if nothing then don't retract
            }
            

            if(driveStick.isStartPushed()){
                launcher.lockShootingPistons();
            }

            if(driveStick.isBackPushed()){
                //launcher.retractShootingPistons();//aka get ready to shoot again
                launcher.unlockShootingPistons();
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
            //pickup.actuateTalon.set(shootStick.getRawAxis(LY) * .5);
            
            //SmartDashboard.putNumber("Target Pressure", launcher.targetPressure);
            //System.out.println("Position: " + pickup.getPickupPos());
            SmartDashboard.putNumber("Robot Time:", robotTimer.get());
            SmartDashboard.putNumber("Pressure PSI", currentPressurePSI);
            SmartDashboard.putNumber("Pressure Voltage", analogInput);
            SmartDashboard.putNumber("Pickup Position", pickup.getPickupPos());
            driveStick.clearButtons();
            shootStick.clearButtons();
        }
        compressor.stop();
        
        
    }
}
