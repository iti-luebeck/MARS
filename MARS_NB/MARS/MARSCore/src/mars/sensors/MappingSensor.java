/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.sensors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * The base class for all mapping sensors. Mapping sensors use an image file as
 * an map. The position of the auv, repsectively of the sensor, is used to get
 * the data of a specific position in the image file. See pollution or flow for
 * an example.
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({FlowMeter.class, PollutionMeter.class})
public class MappingSensor {

}
