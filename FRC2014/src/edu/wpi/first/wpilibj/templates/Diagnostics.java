/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.wpilibj.templates;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
/**
 *
 * @author Jamie
 */
public class Diagnostics { //provides diagnostic methods to print out problems
    private static final Diagnostics instance = new Diagnostics();
    
    public static Diagnostics getInstance(){
        return instance;
    }
    
    public void showPSI(){
        SmartDashboard.getBoolean(null);
    }
    
    public void showMotorSpeed(double motorSpeed){
        //SmartDashboard.putNumber("MotorSpeed", motorSpeed);
    }
}
