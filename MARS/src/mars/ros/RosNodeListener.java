/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.ros;

import java.util.EventListener;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public interface RosNodeListener extends EventListener
{
  public void fireEvent( RosNodeEvent e );
}    

