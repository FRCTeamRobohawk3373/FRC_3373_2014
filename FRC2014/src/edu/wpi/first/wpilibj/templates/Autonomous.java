/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.wpilibj.templates;
import edu.wpi.first.wpilibj.templates.Drive;
/**
 *
 * @author RoboHawks
 */
public class Autonomous {
    Drive drive = new Drive();
    Launcher launcher = new Launcher();
    public void enterAutoBonusZone(long moveTime){
        drive.drive(0, 0, .75);
        try {
            Thread.sleep(moveTime);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        drive.drive(0, 0, 0);
    }
    
    public void shootAuto(boolean isHot){
        launcher.chargeShootingPistons(60);
        while (!isHot){
            try {
                Thread.sleep(10L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        launcher.unlockShootingPistons();
        
    }
}
