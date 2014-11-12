/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.actuators.SpecialManipulators;

import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import com.rits.cloning.Cloner;
import java.util.HashMap;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.actuators.Actuator;
import mars.states.SimState;

/**
 * Not finished. Launches a small torpedo shaped object.
 * 
 * @author Thomas Tosik
 */
public class TorpedoLauncher extends Actuator {

    //motor
    private Geometry TorpedoLauncherStart;
    private Geometry TorpedoLauncherEnd;

    private Sphere bullet;
    private SphereCollisionShape bulletCollisionShape;
    Material mat2;

    /**
     *
     */
    public TorpedoLauncher() {
        super();
    }

    /**
     *
     * @param simstate
     * @param MassCenterGeom
     */
    public TorpedoLauncher(SimState simstate, Geometry MassCenterGeom) {
        super(simstate, MassCenterGeom);
    }

    /**
     *
     * @param simstate
     */
    public TorpedoLauncher(SimState simstate) {
        super(simstate);
    }

    /**
     *
     * @param canon
     */
    public TorpedoLauncher(TorpedoLauncher torpedo) {
        super(torpedo);
        HashMap<String, String> actionsOriginal = torpedo.getAllActions();
        Cloner cloner = new Cloner();
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        TorpedoLauncher actuator = new TorpedoLauncher(this);
        actuator.initAfterJAXB();
        return actuator;
    }

    /**
     *
     * @param pe
     */
    @Override
    public void copyValuesFromPhysicalExchanger(PhysicalExchanger pe) {
        super.copyValuesFromPhysicalExchanger(pe);
        if (pe instanceof Canon) {
            HashMap<String, String> actionOriginal = ((Canon) pe).getAllActions();
            Cloner cloner = new Cloner();
        }
    }

    /**
     * DON'T CALL THIS METHOD! In this method all the initialiasing for the
     * motor will be done and it will be attached to the physicsNode.
     */
    @Override
    public void init(Node auv_node) {
        super.init(auv_node);

    }

    @Override
    public void update() {

    }

    /**
     * Create a round geometry and apply a force to it.
     */
    public void shoot() {

    }

    /**
     *
     * @param tpf
     */
    @Override
    public void update(float tpf) {

    }

    @Override
    public void reset() {

    }
}
