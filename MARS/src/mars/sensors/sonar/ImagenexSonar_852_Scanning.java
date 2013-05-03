/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.sensors.sonar;

import com.jme3.scene.Node;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import mars.Helper.Helper;
import mars.PhysicalEnvironment;
import mars.states.SimState;
import mars.hardware.Imaginex;
import mars.ros.MARSNodeMain;
import org.jboss.netty.buffer.ChannelBuffers;
import org.ros.message.Time;
import org.ros.node.topic.Publisher;

/**
 * This is the Imaginex Sonar class. It's the sonar used in the AUV HANSE.
 * Since the Imaginex sonars need some header information to be sent we put them in front of the basic sonar data.
 * @author Thomas Tosik
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class ImagenexSonar_852_Scanning extends Sonar{

    private int SonarReturnDataHeaderLength = 12;//265

    ///ROS stuff
    /**
     * 
     */
    protected Publisher<hanse_msgs.ScanningSonar> publisher = null;
    /**
     * 
     */
    protected hanse_msgs.ScanningSonar fl;
    /**
     * 
     */
    protected std_msgs.Header header; 
    
    /**
     * 
     */
    public ImagenexSonar_852_Scanning(){
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
        return super.getReturnDataLength()+SonarReturnDataHeaderLength+1;//+1 is the termination byte
    }

    @Override
    protected byte[] encapsulateWithHeaderTail(byte[] sondat){
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
    public byte[] getData(){
        return encapsulateWithHeaderTail(super.getData());
    }

    /**
     * 
     * @return
     */
    @Override
    public byte[] getRawData(){
        return super.getData();
    }

    @Override
    protected float calculateAverageNoiseFunction(float x){
        return 14.22898616f*((float)Math.pow(1.03339750f, (float)Math.abs(x)) );
    }

    @Override
    protected float calculateStandardDeviationNoiseFunction(float x){
        return 7.50837174f*((float)Math.pow(1.02266704f, (float)Math.abs(x)) );
    }
    
        /**
     * 
     * @param ros_node
     * @param auv_name
     */
    @Override
    public void initROS(MARSNodeMain ros_node, String auv_name) {
        super.initROS(ros_node, auv_name);
        publisher = ros_node.newPublisher(auv_name + "/" + this.getPhysicalExchangerName(),hanse_msgs.ScanningSonar._TYPE);  
        fl = this.mars_node.getMessageFactory().newFromType(hanse_msgs.ScanningSonar._TYPE);
        header = this.mars_node.getMessageFactory().newFromType(std_msgs.Header._TYPE);
        this.rosinit = true;
    }
    
    /**
     * 
     */
    @Override
    public void publish() {
        super.publish();
        header.setSeq(rosSequenceNumber++);
        header.setFrameId(this.getRos_frame_id());
        header.setStamp(Time.fromMillis(System.currentTimeMillis()));
        fl.setHeader(header);
        
        byte[] sonData = getRawData();
        float lastHeadPosition = getLastHeadPosition();
        this.mars.getView().initRayBasedData(sonData,lastHeadPosition,this);
        fl.setEchoData(ChannelBuffers.copiedBuffer(ByteOrder.LITTLE_ENDIAN,sonData));
        fl.setHeadPosition(lastHeadPosition);
        fl.setStartGain((byte)getScanning_gain());
        fl.setRange((byte)getMaxRange());
        
        if( publisher != null ){
            publisher.publish(fl);
        }
    }
}
