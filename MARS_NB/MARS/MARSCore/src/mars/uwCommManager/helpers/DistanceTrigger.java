/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.helpers;

/**
 * @version 0.2
 * @author Jasper Schwinghammer
 */
public final class DistanceTrigger implements Comparable<DistanceTrigger>{
    
    /**
     * The distance between the AUVs
     */
    private final float DISTANCE;
    /**
     * The AUV that is reached at this distance
     */
    private final String AUV_NAME;
    
    private final int surfaceBounces;
    
    private final int floorBounces;
    
    /**
     * 
     * Create a new distanceTrigger with a given distance and a AUV that is found at that distance
     * @since 0.1
     * @param distance the distance to the AUV
     * @param auvName the name of the AUV
     */
    public DistanceTrigger(final float distance, final String auvName) {
        this.AUV_NAME = auvName;
        this.DISTANCE = distance;
        this.surfaceBounces = 0;
        this. floorBounces = 0;
    }
    
    
    
    /**
     * @since 0.2
     * @param distance the distance to the AUV
     * @param auvName the name of the AUV
     * @param surfaceBounces count of collisions with the ocean surface
     * @param floorBounces count of collisions with the ocean floor
     */
    public DistanceTrigger(final float distance, final String auvName, final int surfaceBounces, final int floorBounces) {
        this.AUV_NAME = auvName;
        this.DISTANCE = distance;
        this.surfaceBounces = surfaceBounces;
        this.floorBounces = floorBounces;
    }
    /**
     * @since 0.1
     * @return the distance from the start position to the AUV
     */
    public float getDistance() {
        return DISTANCE;
    }
    /**
     * 
     * @since 0.1
     * @return the name of the AUV found at given distance
     */
    public String getAUVName() {
        return AUV_NAME;
    }
    
    /**
     * get the count of surface bounces
     * @return how often the signal collides with the ocean surface
     */
    public int getSurfaceBounces() {
        return surfaceBounces;
    }
    
    /**
     * get the count of ocean floor bounces
     * @return how often the signal collides with the ocean floor
     */
    public int getFloorBounces() {
        return floorBounces;
    }
    
    public int getTotalBounces() {
        return surfaceBounces + floorBounces;
    }

    /**
     * compares two DistanceTriggers by there distance values. 
     * @param o another distance Trigger
     * @return the value of Float.compare(this.DISTANCE,o.DISTANCE)
     */
    @Override
    public int compareTo(DistanceTrigger o) {
        return Float.compare(DISTANCE, o.getDistance());
    }
}
