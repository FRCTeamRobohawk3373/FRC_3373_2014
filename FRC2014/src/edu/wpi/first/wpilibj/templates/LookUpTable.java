package edu.wpi.first.wpilibj.templates;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.Hashtable;

/**
 *
 * @author Jamie
 */
public class LookUpTable {

    public double lookUpValue(double currentValue, double[] independentArray, double[] dependentArray){ //independentArray is an array that corresponds to distance from the target from the wall and dependentArray is the values corresponding to the distance in the independentArray
        int i;
        double difference;
        double percentage;//this is used in the interpelation code
        double result;        
        if(currentValue < independentArray[independentArray.length - 1]){ //makes sure that we are within the range of the independentArray
            for (i = 0; i < independentArray.length; i++){
                if(currentValue == independentArray[i]){//checks our current distance to see if it matches distance(i) in independentArray
                    return dependentArray[i];
                } else if(currentValue < independentArray[i] && currentValue < independentArray[i+1]){//if current distance is inbetween distance(i)and the next distance(i+1) in independentArray
                    difference = independentArray[i+1] - independentArray[i];
                    percentage = (currentValue - independentArray[i]) / difference;
                    result = dependentArray[i] + ((dependentArray[i+1] - dependentArray[i]) * percentage);
                    return result;
                }
            }
        } else {
            return 0;//if we are out of range don't do anything!
        }
        return 0;//we should never get here
    }
}
