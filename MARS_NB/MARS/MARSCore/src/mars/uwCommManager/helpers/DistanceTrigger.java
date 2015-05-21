/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.helpers;

import mars.Helper.SoundHelper;

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
    
    private final int SURFACE_BOUNCES;
    
    private final int FLOOR_BOUNCES;
    
    private final long TRAVEL_TIME;
    
    private boolean hitAUV;
    
    /**
     * 
     * Create a new distanceTrigger with a given distance and a AUV that is found at that distance
     * @since 0.1
     * @param distance the distance to the AUV
     * @param auvName the name of the AUV
     */
    public DistanceTrigger(final float distance, final String auvName, final float temperature,final float speedOfSound) {
        this.AUV_NAME = auvName;
        this.DISTANCE = distance;
        this.SURFACE_BOUNCES = 0;
        this.FLOOR_BOUNCES = 0;
        this.hitAUV = false;
        TRAVEL_TIME = (long)(distance / speedOfSound*1000);
    }
    
    
    
    /**
     * @since 0.2
     * @param distance the distance to the AUV
     * @param auvName the name of the AUV
     * @param surfaceBounces count of collisions with the ocean surface
     * @param floorBounces count of collisions with the ocean floor
     */
    public DistanceTrigger(final float distance, final String auvName, final int surfaceBounces, final int floorBounces, final float speedOfSound, final boolean hitAUV) {
        this.AUV_NAME = auvName;
        this.DISTANCE = distance;
        this.SURFACE_BOUNCES = surfaceBounces;
        this.FLOOR_BOUNCES = floorBounces;
        this.hitAUV = hitAUV;
        TRAVEL_TIME = (long)(distance / speedOfSound*1000);
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
        return SURFACE_BOUNCES;
    }
    
    /**
     * get the count of ocean floor bounces
     * @return how often the signal collides with the ocean floor
     */
    public int getFloorBounces() {
        return FLOOR_BOUNCES;
    }
    
    public int getTotalBounces() {
        return SURFACE_BOUNCES + FLOOR_BOUNCES;
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
    
    /**
     * @since 0.2
     * @return the time the message will take to it's reciever
     */
    public long getTraveTimel() {
        return TRAVEL_TIME;
    }
    
    /**
     * 
     * @since 0.2
     * @return If this trace hit an AUV different from its target
     */
    public boolean gethitAUV() {
        return hitAUV;
    }
    
    /**
     * Warning you can not undo this
     * @since 0.3
     */
    public void hitAUV() {
        hitAUV = true;
    }
}
