/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.sensors.sonar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import mars.Helper.Helper;
import mars.hardware.Imaginex;

/**
 * This is the Tritech Sonar class. It is the sonar used in the AUV SMART-E.
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class TriTech extends Sonar {

    private int SonarReturnDataHeaderLength = 12;//265

    /**
     *
     */
    public TriTech() {
        super();
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

    @Override
    protected float calculateAverageNoiseFunction(float x) {
        return 14.22898616f * ((float) Math.pow(1.03339750f, (float) Math.abs(x)));
    }

    @Override
    protected float calculateStandardDeviationNoiseFunction(float x) {
        return 7.50837174f * ((float) Math.pow(1.02266704f, (float) Math.abs(x)));
    }
}
