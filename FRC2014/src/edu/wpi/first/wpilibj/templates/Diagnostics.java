package edu.wpi.first.wpilibj.templates;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


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
    /**
     * Use this to display PSI on smartDashboard
     * @param psi PSI measure from sensor
     * @param meterID ID of the meter as a String
     * @return returns String of PSI and the ID
     */
    public String showPSI(double psi, String meterID){
        SmartDashboard.putNumber("PSI " + meterID, psi);
        return ("PSI " + meterID);
    }
    /**
     * 
     * @param motorSpeed speed of motor to be fed in
     * @param motorID ID of motor
     * @return returns a string with The Motor ID identified 
     */
    public String showMotorSpeed(double motorSpeed, String motorID){
        SmartDashboard.putNumber("Motor " + "" + motorID + "" + " Speed", motorSpeed);
        return ("Motor " + "" + motorID + "" + " Speed");
    }
    /**
     * 
     * @param potMeasure
     * @param potID 
     */
    public void showPotMeasure(double potMeasure, String potID){
        SmartDashboard.putNumber("Pot " + potID + " Measure", potMeasure);
    }
    /**
     * 
     * @param isCharged 
     */
    public void showChargeStatus(boolean isCharged){
        SmartDashboard.putBoolean("IsCharged: ", isCharged);
    }
}
