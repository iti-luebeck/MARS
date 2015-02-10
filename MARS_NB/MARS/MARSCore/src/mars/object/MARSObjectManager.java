/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.object;

import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 * Creates an MARSObjectManager. You register your auv's and simobjects here. The complete life cycle
 * is managed here.
 * 
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class MARSObjectManager implements Lookup.Provider{

    //lookup stuff
    private InstanceContent content = new InstanceContent();
    private Lookup lookup = new AbstractLookup(content);
    
    @Override
    public Lookup getLookup() {
        return lookup;
    }
    
}
