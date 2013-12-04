/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.FishSim.SwarmSimulation;

import com.jme3.math.Vector3f;

/**
 *
 * @author Mandy Feldvoß
 */
public interface IFoodSource {
     public Vector3f getNearestLocation(Vector3f location);
     
     public void addToMap(FoodSourceMap map);
     
     public float feed(Vector3f location, float amount);
     
     
}
