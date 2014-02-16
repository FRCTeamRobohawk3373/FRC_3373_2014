package edu.wpi.first.wpilibj.templates;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.templates.NewMath;
import edu.wpi.first.wpilibj.templates.Deadband;
/**
 *
 * @author Jamie
 */
public class Drive {
    private static final Drive instance = new Drive();
    
    public static Drive getInstance(){
        return instance;
    }
    
    NewMath math = new NewMath(); 
    RobotDrive mechanum = new RobotDrive(1,2,3,4);
    Deadband deadband = new Deadband();
    double speed = .50; //default movement speed
    /****
     * @param driveLX X axis of the left joystick on the drive controller
     * @param driveRX X Axis of the right joystick on the drive controller
     * @param driveLY Y axis of the left joystick on the drive controller
     */
    public void drive(double driveLX, double driveRX, double driveLY){
        mechanum.mecanumDrive_Cartesian(
                math.pow(deadband.zero(driveLX, .1), 3)*speed,
                math.pow(deadband.zero(driveRX, .1), 3)*speed,
                math.pow(deadband.zero(driveLY, .1), 3)*speed, 0);
        mechanum.setSafetyEnabled(false);
    }
    
    public void orientationSwitcher(boolean isTrueFrontRequested){
        boolean isShooterFront = true;
        if (isTrueFrontRequested && isShooterFront){
            isShooterFront = false;
        } else if (isTrueFrontRequested && !isShooterFront){
            isShooterFront = false;
        }
        if (isShooterFront){
            speed = Math.abs(speed);
        } else {
            speed = -Math.abs(speed);
        }
    }
    
    public void speedModifier(boolean sniperButton, boolean turboButton){
        if (sniperButton) {
            speed *= .5;
        }
        
        if (turboButton) {
            speed *= 2;
        }
        
        if (!turboButton && !sniperButton){
            speed = .5;
        }
    }
}
