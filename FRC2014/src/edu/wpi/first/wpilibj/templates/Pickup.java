package edu.wpi.first.wpilibj.templates;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Jamie
 */
public class Pickup {
    AnalogChannel pickupPot = new AnalogChannel(5); //Voltage from one to five
    Talon actuateTalon = new Talon(5);
    Deadband deadband = new Deadband();
    
    DoubleSolenoid ballGrabberSolenoid = new DoubleSolenoid(2, 7, 8);
    
    double maxVoltage = 5;
    double minVoltage = 0;
    double pickupVoltage = 0;
    double dropoffVoltage = 5;
    final double angleTolerance = 15;
    double diagSpeed = .5;
    static boolean isAtPosition = false;
    double targetPos = getPickupPos();
    boolean isGoToRunning = false;
    /**
     * Method to close pickup up arms and grab ball
     */
    public void grabBall(){
        ballGrabberSolenoid.set(DoubleSolenoid.Value.kForward);
    }
    /**
     * Method to release ball and open pickup arms
     */
    public void releaseBall(){
        ballGrabberSolenoid.set(DoubleSolenoid.Value.kReverse);
    }
    /**
     * Method to relax hold on ball
     */
    public void doNothingBall(){
        ballGrabberSolenoid.set(DoubleSolenoid.Value.kOff);
    }
    
    /**
     * Use this method to control the pickup arm. Threaded function
     * @param speed speed the arm should move at
     */
    public void goToPos(final double speed){
        final Thread thread = new Thread(new Runnable() {
            public void run() {
                isGoToRunning = true;
                isAtPosition = false;
                while (deadband.zero(Math.abs(getPickupPos() - targetPos), angleTolerance, 1)){
                        
                    System.out.println(getPickupPos());
                    goToAngle(targetPos, getPickupPos(), speed);
                    
                }
                actuateTalon.set(0);
                isAtPosition = true;
                isGoToRunning = false;
            }
        });
        if (!isGoToRunning){
            thread.start();
        }
    }
    
    /**
     * Method to convert potentiometer voltage into degrees
     * @return returns degrees
     */    
    public double getPickupPos(){
        double angle = pickupPot.getVoltage();
        return angle;
    }
    
    /**
     * Method to be used in the threaded goto method
     * @param target target angle
     * @param currentPos current angle
     * @param speed motor speed
     */
    private void goToAngle(double target, double currentPos, double speed){
        System.out.println(deadband.zero(Math.abs(target-currentPos), 5, 1));
        if (Math.abs(target-currentPos) <= 5) {
            actuateTalon.set(0);
        } else if (target < currentPos){
            actuateTalon.set(speed);
        } else if (target > currentPos){
            actuateTalon.set(-speed);
        } 
    }
    /***
     * Diagnostic method to get baseline measurements
     * @param up button press telling the arm to move up
     * @param down button press telling the arm to move down
     */
    public void angleDiag(boolean up, boolean down){
        if (up && !down){
            actuateTalon.set(diagSpeed);
        } else if (down && !up){
            actuateTalon.set(-diagSpeed);
        } else {
            actuateTalon.set(0);
        }
    }
    /**
     * Method to control pickup arm movement according to joystick double input
     * @param joystickInput double value to set movement speed for manual control. Use a Joystick getRawAxis()
     * @return returns target speed
     */
    public double moveAccordingToJoystick(double joystickInput){
        double joystickPos = targetPos + deadband.zero(joystickInput, .1);
        return joystickPos;
    }
}
