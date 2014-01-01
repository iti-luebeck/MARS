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

    /**
     *
     * @param location Location of the fish
     * @return Nearest location
     */
    public Vector3f getNearestLocation(Vector3f location);

    /**
     *
     * @param map Map of the foodsource
     */
    public void addToMap(FoodSourceMap map);

    /**
     *
     * @param location Location of the fish
     * @param amount The amount that can be eaten by a fish 
     * @return Saturation which is granted to the fish
     */
    public float feed(Vector3f location, float amount);
     
     
}
