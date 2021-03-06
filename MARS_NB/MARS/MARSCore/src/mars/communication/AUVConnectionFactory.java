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
package mars.communication;

import java.util.logging.Level;
import java.util.logging.Logger;
import mars.auv.AUV;
import mars.communication.rosimpl.AUVConnectionRosImpl;
import mars.communication.tcpimpl.AUVConnectionTcpImpl;
import mars.core.ConnectionSettingsPanel;
import mars.sensors.Sensor;

/**
 *
 * @author Fabian Busse
 */
public class AUVConnectionFactory {

    public static AUVConnection createNewConnection(AUV auv, String param, ConnectionSettingsPanel panel, boolean gzipCompression) {

        AUVConnection conn;
        String connectionType = auv.getAuv_param().getConnectionType();

        if (connectionType.equals(AUVConnectionType.ROS.toString())) {
            conn = new AUVConnectionRosImpl(auv, panel);
            String masterUri = "http://" + auv.getMARS_Settings().getROSMasterip() + ":" + auv.getMARS_Settings().getROSMasterport() + "/";
            conn.connect(masterUri, auv.getMARS_Settings().getROSLocalip());
        } else if (connectionType.equals(AUVConnectionType.TCP.toString())) {
            conn = new AUVConnectionTcpImpl(auv, gzipCompression);
            conn.connect(param);
        } else {
            Logger.getLogger(AUVConnectionFactory.class.getName()).log(Level.INFO, "[" + auv.getName() + "] Unsupported Connection Type: " + auv.getAuv_param().getConnectionType(), "");
            return null;
        }

        auv.setAuvConnection(conn);

        // Add event listeners for the AUVObjectEvent from the sensors
        // so they can publish via their new connection
        for (String sensorName : auv.getSensors().keySet()) {

            Sensor sensor = auv.getSensors().get(sensorName);
            sensor.addAUVObjectListener(conn);
        }

        return conn;
    }
}
