/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.wpilibj.templates;
import edu.wpi.first.wpilibj.RobotDrive;
/**
 *
 * @author Jamie
 */
public class Drive {
    private static final Drive instance = new Drive();
    
    public static Drive getInstance(){
        return instance;
    }
    
    RobotDrive mechanum = new RobotDrive(1,2,3,4);
    
    public void orienationSwitcher(boolean isShooterFront){
        if (isShooterFront){ //shooting direction is front
            mechanum.mecanumDrive_Cartesian(1, 2, 1, 0);
        } else {
            mechanum.mecanumDrive_Cartesian(-1, -2, -1,0);
        }
    }
}
