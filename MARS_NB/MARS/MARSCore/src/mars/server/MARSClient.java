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
