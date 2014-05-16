/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.recorder;

import com.jme3.math.Vector3f;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.xml.Vector3fAdapter;

/**
 * A Simple storing class for recordings(JAXB)
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"time", "position", "rotation"})
public class Record implements Comparable<Record>{
    
    /**
     * 
     */
    @XmlElement
    @XmlJavaTypeAdapter(Vector3fAdapter.class)
    public Vector3f position;
    
    /**
     *
     */
    @XmlElement
    @XmlJavaTypeAdapter(Vector3fAdapter.class)
    public Vector3f rotation;
    
    /**
     *
     */
    @XmlElement
    public float time;

    /**
     *
     */
    public Record() {
    }
    
    /**
     *
     * @param time
     * @param postion
     * @param rotation
     */
    public Record(float time, Vector3f postion, Vector3f rotation) {
        this.time = time;
        this.position = postion;
        this.rotation = rotation;
    }

    /**
     *
     * @return
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     *
     * @return
     */
    public Vector3f getRotation() {
        return rotation;
    }

    /**
     *
     * @return
     */
    public float getTime() {
        return time;
    }

    /**
     *
     * @param position
     */
    public void setPosition(Vector3f position) {
        this.position = position;
    }

    /**
     *
     * @param rotation
     */
    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    /**
     *
     * @param time
     */
    public void setTime(float time) {
        this.time = time;
    }
    
    @Override
    public int compareTo(Record record) {
        if(this.getTime() > record.getTime()){
            return 1;
        }else if(this.getTime() < record.getTime()){
            return -1;
        }else{
            return 0;
        }
    }
}
