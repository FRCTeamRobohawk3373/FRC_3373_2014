package edu.wpi.first.wpilibj.templates;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import edu.wpi.first.wpilibj.templates.Drive;
/**
 *
 * @author RoboHawks
 */
public class Autonomous {
    Drive drive = new Drive();
    Launcher launcher = new Launcher();
    Pickup pickup = new Pickup();
    /**
     * Method that moves the robot forward for X amount of time to get into auto zone
     * @param moveTime time in milliseconds for the robot to move forward
     */
    public void enterAutoBonusZone(long moveTime){
        drive.drive(0, 0, .75);
        try {
            Thread.sleep(moveTime);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        drive.drive(0, 0, 0);
    }
    /**
     * method to shoot during autonomous
     * @param isHot checks whether the vision target is hot for bonus points, fed in from server code
     */
    public void shootAuto(boolean isHot){
        pickup.targetPos = pickup.minVoltage;
        pickup.goToPos(.5);
        //launcher.targetPressure = 55;
        //  \elkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk  3oeldc launcher.chargeShootingPistons();
        while (!isHot){
            try {
                Thread.sleep(10L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        launcher.shoot();
        
    }
}
