/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.wpilibj.templates;

/**
 *
 * @author RoboHawks
 */
public class Deadband {
    public double zero(double input, double range){
        
        if(input > -range && input < range){
            return 0.00;
        } else {
            return input;
        }
        
    }
}
