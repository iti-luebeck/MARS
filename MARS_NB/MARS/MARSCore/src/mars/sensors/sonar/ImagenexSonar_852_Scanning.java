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
package mars.sensors.sonar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import mars.Helper.Helper;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.events.AUVObjectEvent;
import mars.hardware.Imaginex;
import mars.misc.LaserScannerData;
import mars.misc.SonarData;

/**
 * This is the Imaginex Sonar class. It is the sonar used in the AUV HANSE. Since the Imaginex sonars need some header information to be sent we put them in front of the basic sonar data.
 *
 * @author Thomas Tosik
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class ImagenexSonar_852_Scanning extends Sonar {

    private int SonarReturnDataHeaderLength = 12;//265

    /**
     *
     */
    public ImagenexSonar_852_Scanning() {
        super();
    }

    /**
     *
     * @param sonar
     */
    public ImagenexSonar_852_Scanning(ImagenexSonar_852_Scanning sonar) {
        super(sonar);
    }

    /**
     *
     * @return
     */
    public int getSonarReturnDataHeaderLength() {
        return SonarReturnDataHeaderLength;
    }

    /**
     *
     * @return
     */
    public int getSonarReturnDataTotalLength() {
        return super.getReturnDataLength() + SonarReturnDataHeaderLength + 1;//+1 is the termination byte
    }

    @Override
    protected byte[] encapsulateWithHeaderTail(byte[] sondat) {
        byte[] header = new byte[SonarReturnDataHeaderLength];
        byte[] end = new byte[1];

        //calculate the sonar head postion
        int head_position = Imaginex.getHeading(getLastHeadPosition());

        //build the header for the imaginex sonar
        byte[] imaginex_bytes = Imaginex.imaginexByteConverterHead(head_position);
        byte[] imaginex_bytes_2 = Imaginex.imaginexByteConverterDataLength(super.getReturnDataLength());
        header[5] = imaginex_bytes[0];
        header[6] = imaginex_bytes[1];
        header[7] = 50;
        header[10] = imaginex_bytes_2[0];
        header[11] = imaginex_bytes_2[1];
        header[0] = 'I';
        header[1] = 'M';
        header[2] = 'X';
        header[3] = Imaginex.sonar_head_id;
        header[4] = Imaginex.sonar_serial_status;
        end[0] = Imaginex.termination_byte;

        byte[] arr_ret = Helper.concatByteArrays(header, sondat);
        byte[] arr_ret2 = Helper.concatByteArrays(arr_ret, end);

        return arr_ret2;
    }

    @Override
    public byte[] getData() {
        return encapsulateWithHeaderTail(super.getData());
    }

    /**
     *
     * @return
     */
    @Override
    public byte[] getRawData() {
        return super.getData();
    }

    public SonarData getSonarData() {
        return new SonarData(getLastHeadPosition(), getMaxRange(), getScanningGain(), getRawData());
    }

    @Override
    protected float calculateAverageNoiseFunction(float x) {
        return 14.22898616f * ((float) Math.pow(1.03339750f, Math.abs(x)));
    }

    @Override
    protected float calculateStandardDeviationNoiseFunction(float x) {
        return 7.50837174f * ((float) Math.pow(1.02266704f, Math.abs(x)));
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        ImagenexSonar_852_Scanning sensor = new ImagenexSonar_852_Scanning(this);
        sensor.initAfterJAXB();
        return sensor;
    }

    @Override
    public void publishData() {
        super.publishData();
        AUVObjectEvent auvEvent = new AUVObjectEvent(this, getSonarData(), System.currentTimeMillis());
        notifyAdvertisementAUVObject(auvEvent);
    }
}
