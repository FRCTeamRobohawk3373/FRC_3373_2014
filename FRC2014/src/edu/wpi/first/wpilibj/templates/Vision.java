/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.PWM;

/**
 * PWM ID's sending:
 * 1 - check if is HOT
 * 2 - 
 * PWM ID's receiving:
 * 
 * 0-99 reserved for sending, 100-255 reserved for receiving
 * @author Jamie
 */


public class Vision {
    PWM piPWM = new PWM(1);
    
    public boolean isHot(){
        piPWM.setRaw(1);
        if (piPWM.getRaw() == 100){
            return true;
        } else return false;
    }
}
