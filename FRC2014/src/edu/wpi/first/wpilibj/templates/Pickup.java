/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Talon;

/**
 *
 * @author Jamie
 */
public class Pickup {
    AnalogChannel pickupPot = new AnalogChannel(1); //Voltage from one to five
    Talon actuateTalon = new Talon(1);
    Deadband deadband = new Deadband();
    
    double maxAngle = 120;
    double maxVoltage = 5;
    final double angleTolerance = 3;
    double diagSpeed = .5;
    
    public void goToPos(final double pos, final double speed){
        final Thread thread = new Thread(new Runnable() {
            public void run() {
                while (deadband.zero(Math.abs(getPickupPos() - pos), angleTolerance, 1)){
                    if (deadband.zero(Math.abs(getPickupPos() - pos), angleTolerance, 1)){
                        goToAngle(pos, getPickupPos(), speed);
                    }
                }
            }
        });
        thread.start();
    }
    
    public double getPickupPos(){
        double angle = pickupPot.getVoltage() * (maxAngle/maxVoltage);
        return angle;
    }
    
    public void goToAngle(double target, double currentPos, double speed){
        if (target < currentPos){
            actuateTalon.set(speed);
        } else if (target > currentPos){
            actuateTalon.set(-speed);
        } else {
            actuateTalon.set(0);
        }
    }
    
    public double angleDiag(boolean up, boolean down){
        if (up && !down){
            actuateTalon.set(diagSpeed);
        } else if (down && !up){
            actuateTalon.set(-diagSpeed);
        } else {
            actuateTalon.set(0);
        }
        return getPickupPos();
    }
}
