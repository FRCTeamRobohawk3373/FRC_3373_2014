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
    
    public String showPSI(double psi, String meterID){
        SmartDashboard.putNumber("PSI " + meterID, psi);
        return ("PSI " + meterID);
    }
    
    public String showMotorSpeed(double motorSpeed, String motorID){
        SmartDashboard.putNumber("Motor " + "" + motorID + "" + " Speed", motorSpeed);
        return ("Motor " + "" + motorID + "" + " Speed");
    }
    
    public void showPotMeasure(double potMeasure, String potID){
        SmartDashboard.putNumber("Pot " + potID + "Measure", potMeasure);
    }
}
