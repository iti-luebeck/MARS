/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager;

/**
 *
 * @author Jasper Schwinghammer
 */
public final class DistanceTrigger implements Comparable<DistanceTrigger>{
    
    private final float DISTANCE;
    private final String AUV_NAME;
    
    public DistanceTrigger(final float distance, final String auvName) {
        this.AUV_NAME = auvName;
        this.DISTANCE = distance;
    }
    
    public float getDistance() {
        return DISTANCE;
    }
    
    public String getAUVName() {
        return AUV_NAME;
    }

    @Override
    public int compareTo(DistanceTrigger o) {
        return Float.compare(DISTANCE, o.getDistance());
    }
}
