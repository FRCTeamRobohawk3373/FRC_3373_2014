/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.wpilibj.templates;
import java.util.Hashtable;

/**
 *
 * @author Jamie
 */
public class LookUpTable {

    public double lookUpValue(double currentDistance, double[] distanceArray, double[] valueArray){ //distanceArray is an array that corresponds to distance from the target from the wall and valueArray is the values corresponding to the distance in the distanceArray
        int i;
        double difference;
        double percentage;//this is used in the interpelation code
        double result;        
        if(currentDistance < distanceArray[distanceArray.length - 1]){ //makes sure that we are within the range of the distanceArray
            for (i = 0; i < distanceArray.length; i++){
                if(currentDistance == distanceArray[i]){//checks our current distance to see if it matches distance(i) in distanceArray
                    return valueArray[i];
                } else if(currentDistance > distanceArray[i] && currentDistance < distanceArray[i+1]){//if current distance is inbetween distance(i)and the next distance(i+1) in distanceArray
                    difference = distanceArray[i+1] - distanceArray[i];
                    percentage = (currentDistance - distanceArray[i]) / difference;
                    result = valueArray[i] + ((valueArray[i+1] - valueArray[i]) * percentage);
                    return result;
                }
            }
        } else {
            return 0;//if we are out of range don't do anything!
        }
        String neverGetsHere = "YOU WILL NEVER LEAVE";
        return 0;//we should never get here
    }
}
