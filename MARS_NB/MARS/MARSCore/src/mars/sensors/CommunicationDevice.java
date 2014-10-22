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
import mars.gui.plot.PhysicalExchangerListener;
import mars.states.SimState;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso( {UnderwaterModem.class,WiFi.class} )
public abstract class CommunicationDevice extends Sensor{

    /**
     *
     */
    protected EventListenerList listeners = new EventListenerList();
    
    /**
     *
     */
    public CommunicationDevice() {
        super();
    }

    /**
     *
     * @param sensor
     */
    public CommunicationDevice(Sensor sensor) {
        super(sensor);
    }

    /**
     *
     * @param simstate
     */
    public CommunicationDevice(SimState simstate) {
        super(simstate);
    }

    /**
     *
     * @param mars
     * @param pe
     */
    public CommunicationDevice(MARS_Main mars, PhysicalEnvironment pe) {
        super(mars, pe);
    }
    

    @Override
    public void initAfterJAXB() {
        super.initAfterJAXB();
    }

    @Override
    public void init(Node auv_node) {
        super.init(auv_node);
    }

    @Override
    public void update(float tpf) {
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
       return null;
    }

    @Override
    public void reset() {
    }

    /**
     *
     * @return
     */
    public abstract Vector3f getWorldPosition();
    
    /**
     *
     * @return
     */
    public abstract Float getPropagationDistance();
    
    /**
     *
     * @param msg
     */
    public abstract void publish(String msg);
    
    /**
     *
     * @param listener
     */
    public void addAdListener( PhysicalExchangerListener listener )
    {
      listeners.add( PhysicalExchangerListener.class, listener );
    }

    /**
     *
     * @param listener
     */
    public void removeAdListener( PhysicalExchangerListener listener )
    {
      listeners.remove( PhysicalExchangerListener.class, listener );
    }
    
    /**
     *
     */
    public void removeAllListener(){
        //listeners.
    }

    /**
     *
     * @param event
     */
    protected synchronized void notifyAdvertisement( CommunicationDeviceEvent event )
    {
      for ( PhysicalExchangerListener l : listeners.getListeners( PhysicalExchangerListener.class ) )
        l.onNewData( event );
    }
}
