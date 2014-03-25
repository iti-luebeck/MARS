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
 * client to an middleware i.e. ros,tcp/ip,imc,player,...
 * This allows MARS to communicate with other frameworks like ROS.
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public interface MARSClient extends EventListener{
    public Icon getIcon();
    public String getName();
    public void init();
    public void connectToServer();
    public void disconnectFromServer();
    public void start();
    public void stop();
    public void cleanup();
    public void setServerIP(String ip);
    public String getServerIP();
    public void setServerPort(int port);
    public int getServerPort();
    void onNewData( MARSClientEvent e );
    public void addAdListener(EventListener listener);
    public void removeAdListener(EventListener listener);
    public void removeAllListener();
    public void notifyAdvertisement(MARSClientEvent event);
    public void setAUVManager(AUV_Manager auvManager);
}
