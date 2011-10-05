/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.sensors;

import com.jme3.scene.Node;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import mars.Helper.Helper;
import mars.PhysicalEnvironment;
import mars.SimState;
import mars.hardware.Imaginex;

/**
 * This is the Imaginex Sonar class. It's the sonar used in the AUV HANSE.
 * Since the Imaginex sonars need some header information to be sent we put them in front of the basic sonar data.
 * @author Thomas Tosik
 */
public class ImagenexSonar_852_Scanning extends Sonar{

    private int SonarReturnDataHeaderLength = 12;//265

    /**
     * 
     * @param simauv
     * @param detectable
     * @param pe
     */
    public ImagenexSonar_852_Scanning(SimState simstate, Node detectable,PhysicalEnvironment pe) {
        super(simstate,detectable,pe);
        //set the logging
        try {
            // Create an appending file handler
            boolean append = true;
            FileHandler handler = new FileHandler(this.getClass().getName() + ".log", append);
            // Add to the desired logger
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.addHandler(handler);
        } catch (IOException e) { }
    }

    /**
     *
     * @param simauv
     * @param detectable
     */
    public ImagenexSonar_852_Scanning(SimState simstate, Node detectable) {
        super(simstate,detectable);
        //set the logging
        try {
            // Create an appending file handler
            boolean append = true;
            FileHandler handler = new FileHandler(this.getClass().getName() + ".log", append);
            // Add to the desired logger
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.addHandler(handler);
        } catch (IOException e) { }
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
        return super.getSonarReturnDataLength()+SonarReturnDataHeaderLength+1;//+1 is the termination byte
    }

    @Override
    protected byte[] encapsulateWithHeaderTail(byte[] sondat){
        byte[] header = new byte[SonarReturnDataHeaderLength];
        byte[] end = new byte[1];

        //calculate the sonar head postion
        int head_position = Imaginex.getHeading(getLastHeadPosition());

        //build the header for the imaginex sonar
        byte[] imaginex_bytes = Imaginex.imaginexByteConverterHead(head_position);
        byte[] imaginex_bytes_2 = Imaginex.imaginexByteConverterDataLength(super.getSonarReturnDataLength());
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
    public byte[] getSonarData(){
        return encapsulateWithHeaderTail(super.getSonarData());
    }


    @Override
    protected float calculateAverageNoiseFunction(float x){
        return 14.22898616f*((float)Math.pow(1.03339750f, (float)Math.abs(x)) );
    }

    @Override
    protected float calculateStandardDeviationNoiseFunction(float x){
        return 7.50837174f*((float)Math.pow(1.02266704f, (float)Math.abs(x)) );
    }
}
