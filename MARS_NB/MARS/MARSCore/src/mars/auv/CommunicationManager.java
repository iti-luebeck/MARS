/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.auv;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import mars.PhysicalEnvironment;
import mars.events.CommunicationType;
import mars.sensors.CommunicationDevice;
import mars.sensors.CommunicationMessage;
import mars.sensors.Sensor;
import mars.sensors.UnderwaterModem;
import mars.sensors.WiFi;
import mars.server.MARS_Server;
import mars.states.SimState;

/**
 * This class is responsible for the underwater communication. It assures that
 * every auv gets the messages. Also its responsible for the noise and realistic
 * propagation of the messages.
 *
 * @author Thomas Tosik
 */
public class CommunicationManager {

    private ConcurrentLinkedQueue<CommunicationMessage> msgQueue = new ConcurrentLinkedQueue<CommunicationMessage>();

    private final HashMap<String, UnderwaterModem> uws = new HashMap<String, UnderwaterModem>();

    private MARS_Server raw_server;
    private AUV_Manager auv_manager;

    /**
     *
     * @param auv_manager
     * @param simstate
     * @param detectable
     * @param pe
     */
    public CommunicationManager(AUV_Manager auv_manager, SimState simstate, Node detectable, PhysicalEnvironment pe) {
        this.auv_manager = auv_manager;
    }

    /**
     *
     * @param raw_server
     */
    public void setServer(MARS_Server raw_server) {
        this.raw_server = raw_server;
    }

    /**
     *
     * @param tpf
     */
    public void update(float tpf) {
        //System.out.println("Time to communicate: " + tpf);
        CommunicationMessage peek = msgQueue.peek();
        if (peek != null) {
            CommunicationMessage poll = msgQueue.poll();
            updateCommunication(poll.getAuvName(), poll.getMsg(), poll.getCommunicationType());
            sendMsgs(poll.getAuvName(), poll.getMsg());
        }
        //updateComNet();
    }

    private void updateNoise() {

    }

    /*
     * Seeks the underwater modems of the other auvs and send the msgs to them
     * checks for distance and noise is also made here
     */
    private void updateCommunication(String auv_name, String msg, int communicationType) {
        AUV sender = auv_manager.getAUV(auv_name);

        CommunicationDevice senderUW;
        Vector3f senderUWPos;
        if (communicationType == CommunicationType.UNDERWATERSOUND) {
            ArrayList<Sensor> sender_uwmo = sender.getSensorsOfClass(UnderwaterModem.class.getName());
            senderUW = (UnderwaterModem) sender_uwmo.get(0);
            senderUWPos = senderUW.getWorldPosition();
        } else if (communicationType == CommunicationType.WIFI) {
            ArrayList<Sensor> sender_uwmo = sender.getSensorsOfClass(WiFi.class.getName());
            senderUW = (WiFi) sender_uwmo.get(0);
            senderUWPos = senderUW.getWorldPosition();
        } else {//no type -> error
            return;
        }

        HashMap<String, AUV> auvs = auv_manager.getAUVs();

        for (String elem : auvs.keySet()) {
            AUV auv = auvs.get(elem);

            if (auv.getAuv_param().isEnabled() && auv.hasSensorsOfClass(CommunicationDevice.class.getName()) && !auv.getName().equals(auv_name)) {
                ArrayList<Sensor> uwmo = auv.getSensorsOfClass(CommunicationDevice.class.getName());
                Iterator<Sensor> it = uwmo.iterator();
                while (it.hasNext()) {
                    CommunicationDevice mod = (CommunicationDevice) it.next();
                    Vector3f modPos = mod.getWorldPosition();
                    Vector3f distance = modPos.subtract(senderUWPos);
                    if (communicationType == CommunicationType.UNDERWATERSOUND && mod instanceof UnderwaterModem) {//check the communications ways (underwater, overwater)
                        if (Math.abs(distance.length()) <= senderUW.getPropagationDistance()) {//check if other underwatermodem isn't too far away
                            //if()//check if the receiver is also underwater
                            mod.publish(msg);
                        }
                    } else if (communicationType == CommunicationType.WIFI && mod instanceof WiFi) {
                        if (Math.abs(distance.length()) <= senderUW.getPropagationDistance()) {//check if other underwatermodem isn't too far away
                            //if()//check if the receiver is also overwater
                            mod.publish(msg);
                        }
                    }
                }
            }

        }
    }

    private void updateComNet() {
        //update list of modems
        HashMap<String, AUV> auvs = auv_manager.getAUVs();
        for (String elem : auvs.keySet()) {
            AUV auv = auvs.get(elem);
            if (auv.getAuv_param().isEnabled() && auv.hasSensorsOfClass(UnderwaterModem.class.getName())) {
                ArrayList<Sensor> sender_uwmo = auv.getSensorsOfClass(UnderwaterModem.class.getName());
                UnderwaterModem senderUW = (UnderwaterModem) sender_uwmo.get(0);
                uws.put(auv.getName(), senderUW);
            }
        }

        //filter them if dictance to great and send them to the modem for updates
        for (String elem : auvs.keySet()) {
            AUV auv = auvs.get(elem);
            if (auv.getAuv_param().isEnabled() && auv.hasSensorsOfClass(UnderwaterModem.class.getName())) {
                ArrayList<Sensor> sender_uwmo = auv.getSensorsOfClass(UnderwaterModem.class.getName());
                UnderwaterModem senderUW = (UnderwaterModem) sender_uwmo.get(0);
                senderUW.updateComNet(uws);
            }
        }
    }

    @Deprecated
    private void sendMsgs(String auv_name, String msg) {
        //System.out.println("Sending msg: " + msg);
        if (raw_server != null) {
            raw_server.sendStringToAllConnections(msg);
        }
    }

    /**
     *
     * @param auv_name
     * @param msg
     * @param communicationType
     */
    public synchronized void putMsg(String auv_name, String msg, int communicationType) {
        msgQueue.offer(new CommunicationMessage(auv_name, msg, communicationType));
        System.out.println("Added msg to bag: " + msg + " all: " + msgQueue.size());
    }
}
