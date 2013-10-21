/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.sensors;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import javax.swing.event.EventListenerList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import mars.CommunicationDeviceEvent;
import mars.MARS_Main;
import mars.PhysicalEnvironment;
import mars.PhysicalExchanger;
import mars.auv.CommunicationManager;
import mars.gui.plot.PhysicalExchangerListener;
import mars.states.SimState;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso( {UnderwaterModem.class,WiFi.class} )
public abstract class CommunicationDevice extends Sensor{

    protected CommunicationManager com_manager;
    protected EventListenerList listeners = new EventListenerList();
    
    public CommunicationDevice() {
        super();
    }

    public CommunicationDevice(Sensor sensor) {
        super(sensor);
    }

    public CommunicationDevice(SimState simstate) {
        super(simstate);
    }

    public CommunicationDevice(MARS_Main mars, PhysicalEnvironment pe) {
        super(mars, pe);
    }
    
        
    /**
     * 
     * @return
     */
    public CommunicationManager getCommunicationManager() {
        return com_manager;
    }

    /**
     * 
     * @param com_manager
     */
    public void setCommunicationManager(CommunicationManager com_manager) {
        this.com_manager = com_manager;
    }

    @Override
    public void initAfterJAXB() {
        super.initAfterJAXB();
    }

    @Override
    public void init(Node auv_node) {
        
    }

    @Override
    public void update(float tpf) {
    }

    @Override
    public PhysicalExchanger copy() {
       return null;
    }

    @Override
    public void reset() {
    }

    public abstract Vector3f getWorldPosition();
    
    public abstract float getPropagationDistance();
    
    public abstract void publish(String msg);
    
    public void addAdListener( PhysicalExchangerListener listener )
    {
      listeners.add( PhysicalExchangerListener.class, listener );
    }

    public void removeAdListener( PhysicalExchangerListener listener )
    {
      listeners.remove( PhysicalExchangerListener.class, listener );
    }
    
    public void removeAllListener(){
        //listeners.
    }

    protected synchronized void notifyAdvertisement( CommunicationDeviceEvent event )
    {
      for ( PhysicalExchangerListener l : listeners.getListeners( PhysicalExchangerListener.class ) )
        l.onNewData( event );
    }
}
