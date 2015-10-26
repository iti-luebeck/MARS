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
package mars.PhysicalExchange;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import mars.MARS_Main;
import mars.MARS_Settings;
import mars.auv.AUV_Manager;

/**
 * The main class responsible for publish/sending data of the sensors/actuators
 * from auvs.
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class PhysicalExchangerPublisher implements Runnable {

    private static final long sleeptime = 2;

    private MARS_Main mars;
    private AUV_Manager auv_manager;
    private MARS_Settings marsSettings;

    //rosjava stuff
    private boolean running = true;

    /**
     *
     * @param mars
     * @param auv_manager
     * @param marsSettings
     */
    public PhysicalExchangerPublisher(MARS_Main mars, AUV_Manager auv_manager, MARS_Settings marsSettings) {
        //set the logging
        try {

            Logger.getLogger(this.getClass().getName()).setLevel(Level.parse(marsSettings.getLoggingLevel()));

            if(marsSettings.getLoggingFileWrite()){
                // Create an appending file handler
                boolean append = true;
                FileHandler handler = new FileHandler(this.getClass().getName() + ".log", append);
                handler.setLevel(Level.parse(marsSettings.getLoggingLevel()));
                // Add to the desired logger
                Logger logger = Logger.getLogger(this.getClass().getName());
                logger.addHandler(handler);
            }

            if(!marsSettings.getLoggingEnabled()){
                Logger.getLogger(this.getClass().getName()).setLevel(Level.OFF);
            }

        } catch (IOException e) {
        }

        this.mars = mars;
        this.auv_manager = auv_manager;
        this.marsSettings = marsSettings;
    }

    /**
     *
     */
    public void shutdown() {
        running = false;
    }

    @Override
    public void run() {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "ROS Server running...", "");
        try {
            while (running) {
                Future<Void> fut = mars.enqueue(new Callable<Void>() {
                    public Void call() throws Exception {
                        //if(marsSettings.isROS_Server_publish()){
                        auv_manager.publishSensorsOfAUVs();
                        auv_manager.publishActuatorsOfAUVs();
                        //}
                        return null;
                    }
                });
                Thread.sleep(sleeptime);
            }
        } catch (Exception e) {
        }
    }
}
