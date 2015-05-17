/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.object;

import mars.events.MARSObjectEvent;
import mars.events.MARSObjectListener;

/**
 * This the base interface for all objects that are in MARS. For example: AUVs, SimObjects
 * 
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public interface MARSObject {
    /** 
     *
     * @return Unique name of the MARSObject
     */
    public String getName();

    /**
     * Unique name of the MARSObject
     * @param name
     */
    public void setName(String name);
    
    /**
     *
     * @param listener
     */
    public void addMARSObjectListener(MARSObjectListener listener);

    /**
     *
     * @param listener
     */
    public void removeMARSObjectListener(MARSObjectListener listener);

    /**
     *
     */
    public void removeAllMARSObjectListener();

    /**
     *
     * @param event
     */
    public void notifyAdvertisementMARSObject(MARSObjectEvent event);
}
