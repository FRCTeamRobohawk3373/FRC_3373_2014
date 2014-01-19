/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.wpilibj.templates.PhilipTest;
import edu.wpi.first.wpilibj.Solenoid;
/**
 *
 * @author RoboHawks
 */
public class Pneumatics {
    Solenoid solenoid = new Solenoid(3);
    
    public void extend(){ //extends the piston power by powering the solenoid, hopefully
        solenoid.set(true);
    }
    
    public void retract() { //retracts the piston
        solenoid.set(false);
    }
}