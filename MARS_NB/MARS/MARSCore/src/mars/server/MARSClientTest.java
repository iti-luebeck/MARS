/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.server;

import java.util.EventListener;
import javax.swing.Icon;
import mars.auv.AUV_Manager;
import org.openide.util.lookup.ServiceProvider;

/**
 * An example implemenation. Very bare bone.
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@ServiceProvider(service = MARSClient.class)
public class MARSClientTest implements MARSClient {

    AUV_Manager auvManager;

    /**
     *
     */
    public MARSClientTest() {
    }

    /**
     *
     * @param auvManager
     */
    @Override
    public void setAUVManager(AUV_Manager auvManager) {
        this.auvManager = auvManager;
    }

    /**
     *
     * @return
     */
    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param listener
     */
    @Override
    public void addAdListener(EventListener listener) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @return
     */
    @Override
    public Icon getIcon() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     */
    @Override
    public void stop() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     */
    @Override
    public void start() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param port
     */
    @Override
    public void setServerPort(int port) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param ip
     */
    @Override
    public void setServerIP(String ip) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     */
    @Override
    public void removeAllListener() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param listener
     */
    @Override
    public void removeAdListener(EventListener listener) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param e
     */
    @Override
    public void onNewData(MARSClientEvent e) {
        //System.out.println(System.currentTimeMillis() + " GOT DATA: " + e);
    }

    /**
     *
     * @param event
     */
    @Override
    public void notifyAdvertisement(MARSClientEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     */
    @Override
    public void init() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @return
     */
    @Override
    public int getServerPort() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     */
    @Override
    public void cleanup() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @return
     */
    @Override
    public String getServerIP() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     */
    @Override
    public void disconnectFromServer() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     */
    @Override
    public void connectToServer() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
