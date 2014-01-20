/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.wpilibj.templates;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.templates.Diagnostics;
import edu.wpi.first.wpilibj.smartdashboard.*;
/**
 *
 * @author Jamie
 */
public class Shooter {
    Talon testTalon = new Talon(5);
    Diagnostics diag = Diagnostics.getInstance();
    public void runTestMotor(double speed){
        diag.showMotorSpeed(speed, "1");
        speed = SmartDashboard.getNumber(diag.showMotorSpeed(speed, "1"));
        System.out.println(speed);
        testTalon.set(speed);
    }
}
