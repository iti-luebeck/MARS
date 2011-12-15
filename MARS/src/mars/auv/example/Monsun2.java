/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.auv.example;

import com.jme3.math.Vector3f;
import javax.xml.bind.annotation.XmlRootElement;
import mars.SimState;
import mars.auv.BasicAUV;

/**
 *
 * @author Thomsa Tosik
 */
@XmlRootElement
public class Monsun2 extends BasicAUV{
    
    /**
     * 
     * @param simstate 
     */
    public Monsun2(SimState simstate){
        super(simstate);
    }

    /**
     *
     */
    public Monsun2(){
        super();
    }

    @Override
    protected Vector3f updateMyForces(){
        return new Vector3f(0f,0f,0f);
    }
    
    @Override
    protected Vector3f updateMyTorque(){
        return new Vector3f(0f,0f,0f);
    }
}
