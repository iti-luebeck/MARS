/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.ros;

/**
 *
 * @author Thomas Tosik
 * @deprecated Should be replaced be the new MARSClient interface
 */
@Deprecated
public interface ROS_Publisher {

    /**
     *
     */
    public void publishUpdate();

    /**
     *
     */
    public void publish();
}
