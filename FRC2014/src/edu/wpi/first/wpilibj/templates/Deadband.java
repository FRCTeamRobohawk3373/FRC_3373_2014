package edu.wpi.first.wpilibj.templates;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *
 * @author RoboHawks
 */
public class Deadband {
    /**
     * Removes any values within a range to filter out noise
     * @param input input double (generally a joystick)
     * @param range absolute value of a range where a value is invalid within
     * @return returns conditioned input (0 if inside of range, input if without) 
     */
    public double zero(double input, double range){
        
        if(input > -range && input < range){
            return 0.00;
        } else {
            return input;
        }
        
    }
    /**
     * Removes any values within a range to filter out noise, boolean form
     * @param input input double (generally a joystick)
     * @param range absolute value of a range where a value is invalid within
     * @param a just put a number here, was required for an override
     * @return true if value is outside of range, false if inside
     */
    public boolean zero(double input, double range, double a){
        
        if(input > -range && input < range){
            return false;
        } else {
            return true;
        }
        
    }
}
