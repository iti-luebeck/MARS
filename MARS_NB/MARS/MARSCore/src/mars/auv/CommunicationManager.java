/*
* Copyright (c) 2015, Institute of Computer Engineering, University of Lübeck
* All rights reserved.
* 
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
* 
* * Redistributions of source code must retain the above copyright notice, this
*   list of conditions and the following disclaimer.
* 
* * Redistributions in binary form must reproduce the above copyright notice,
*   this list of conditions and the following disclaimer in the documentation
*   and/or other materials provided with the distribution.
* 
* * Neither the name of the copyright holder nor the names of its
*   contributors may be used to endorse or promote products derived from
*   this software without specific prior written permission.
* 
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
* FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
* DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
* CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
* OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
     * @param tpf
     */
    public void update(float tpf) {
        //System.out.println("Time to communicate: " + tpf);
        CommunicationMessage peek = msgQueue.peek();
        if (peek != null) {
            CommunicationMessage poll = msgQueue.poll();
            updateCommunication(poll.getAuvName(), poll.getMsg(), poll.getCommunicationType());
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
        AUV sender = auv_manager.getMARSObject(auv_name);

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

        HashMap<String, AUV> auvs = auv_manager.getMARSObjects();

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
        HashMap<String, AUV> auvs = auv_manager.getMARSObjects();
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
