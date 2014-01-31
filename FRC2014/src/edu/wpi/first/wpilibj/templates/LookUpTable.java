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
public class LookUpTable {
    public double lookUpValue(double currentDistance, double[] distanceArray, double[] valueArray){ //distanceArray is an array that corresponds to distance from the target from the wall and valueArray is the values corresponding to the distance in the distanceArray
        int i;
        if(currentDistance < distanceArray[distanceArray.length - 1]){ //makes sure that we are within the range of the distanceArray
            for (i = 0; i < distanceArray.length; i++){
                if(currentDistance == distanceArray[i]){
                    return valueArray[i];
                } else if(currentDistance > distanceArray[i]){
                    
                }
            }
        } else {
            return 0;//if we are out of range don't do anything!
        }
        return 0;//we should never get here
    }
}
