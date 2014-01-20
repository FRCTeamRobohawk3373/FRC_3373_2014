/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.wpilibj.templates;

/**
 *
 * @author Jamie
 */
public class NewMath {
    /****
     * @param a base number
     * @param b exponent
     * @return returns a to the b power
     */
    public double pow(double a, double b){
        double base = a;
        for (int i = 0; i < b-1; i++){
            a *= base;
        }
        return a;
    }

}
