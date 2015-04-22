/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.server;

import java.util.EventListener;
import javax.swing.Icon;
import mars.auv.AUV_Manager;

/**
 * This interface needs to be implemented when someone wants to program an
 * client to an middleware i.e. ros,tcp/ip,imc,player,... This allows MARS to
 * communicate with other frameworks like ROS.
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@Deprecated
public interface MARSClient extends EventListener {

    /**
     *
     * @return
     */
    public Icon getIcon();

    /**
     *
     * @return
     */
    public String getName();

    /**
     *
     */
    public void init();

    /**
     *
     */
    public void connectToServer();

    /**
     *
     */
    public void disconnectFromServer();

    /**
     *
     */
    public void start();

    /**
     *
     */
    public void stop();

    /**
     *
     */
    public void cleanup();

    /**
     *
     * @param ip
     */
    public void setServerIP(String ip);

    /**
     *
     * @return
     */
    public String getServerIP();

    /**
     *
     * @param port
     */
    public void setServerPort(int port);

    /**
     *
     * @return
     */
    public int getServerPort();

    /**
     *
     * @param e
     */
    void onNewData(MARSClientEvent e);

    /**
     *
     * @param listener
     */
    public void addAdListener(EventListener listener);

    /**
     *
     * @param listener
     */
    public void removeAdListener(EventListener listener);

    /**
     *
     */
    public void removeAllListener();

    /**
     *
     * @param event
     */
    public void notifyAdvertisement(MARSClientEvent event);

    /**
     *
     * @param auvManager
     */
    public void setAUVManager(AUV_Manager auvManager);
}
