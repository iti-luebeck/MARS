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
package mars.actuators.cable;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.joints.SixDofJoint;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import mars.states.SimState;

/**
 * Node representation of a cable. The cable is build from a given number
 * of nodes. One node is a static anchor. One node is a kinematic anchor, that
 * can be dragged around. All nodes in between are physically connected with
 * 6DOF Joints. You can set different parameters of the nodes and joints to
 * create different "cable behaviour".
 * 
 * @author lasse hansen
 */
public class CableNode extends Node {
    
    private AssetManager assetManager;
    private PhysicsSpace space;
    private float length;
    private float detailedLength;
    private float diameter;
    private int sections;
    private Node kineticAnchor;
    //Vector3f posKineticAnchor;
    private Node staticAnchor;
    private Vector3f posStaticAnchor;
    private Vector3f tempPosStaticAnchor;
    private float kineticAnchorMass;
    private float staticAnchorMass = 0;
    private float mass;
    private Material material;
    
    /**
     * Constructor.
     * 
     * @param simState
     * @param length            length of the cable
     * @param detailedLength    the part of the cable that is described with a higher
     *                          amount of sections
     * @param diameter          the diamter of the cable
     * @param mass              the mass of the cable
     * @param sections          the number of sections
     * @param posStaticAnchor   the position of the static anchor node
     */
    public CableNode(SimState simState, float length, float detailedLength, float diameter, float mass, int sections, Vector3f posStaticAnchor){
        
        this.assetManager = simState.getAssetManager();
        this.space = simState.getBulletAppState().getPhysicsSpace();
        this.length = length;
        this.detailedLength = detailedLength;
        this.diameter = diameter;
        this.mass = mass;
        this.sections = sections;
        this.posStaticAnchor = posStaticAnchor;
        this.material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", ColorRGBA.Gray);
        
        //create Nodes
        kineticAnchor = createKineticAnchor();
        createNodesInBetween();
        staticAnchor = createStaticAnchor();
        
        //join Nodes
        joinNodes();
        
        //position static anchor
        staticAnchor.getControl(RigidBodyControl.class).setPhysicsLocation(posStaticAnchor);
        
        //set Geometry
        setGeometry();
        
        //set cable physics
        setCablePhysics();
        
        //add control
        addControl(new CableControl(this));
        
        //add cable components to the physics space
        space.addAll(this);
  
    } 

    /**
     * Creates a kinematic anchor node.
     * @return kinematic node
     */
    private Node createKineticAnchor() {
        Node node = createNode("KineticAnchor", new BoxCollisionShape(new Vector3f(getDiameter(), getDiameter(), getDiameter())), 0);
        //node.getControl(RigidBodyControl.class).setKinematic(true);
        attachChild(node);
        return node;
    }
    
    /**
     * Creates all nodes between the kinematic and static anchor.
     */
    private void createNodesInBetween() {
        int numberOfNodes = getSections()-1;
        System.out.println("numberOfNodes: "+numberOfNodes);
        int detail = (int) (numberOfNodes*0.5);
        System.out.println("detailNodes: "+detail);
        int rest = numberOfNodes-detail;
        System.out.println("restNodes: "+rest);
        float detailNodeMass = (getCableMass()*(getDetailedLength()/getLength()))/(detail+1);
        System.out.println("detailNodeMass: "+detailNodeMass);
        kineticAnchor.getControl(RigidBodyControl.class).setMass(detailNodeMass);
        float restNodeMass = (getCableMass()*((getLength()-getDetailedLength())/getLength()))/rest;
        System.out.println("restNodeMass: "+restNodeMass);
        
        //create nodes in the detailed part of the cable
        for (int i = 1; i <= detail; i++) {
            Node node = createNode("NodeInBetween", new BoxCollisionShape(new Vector3f(getDiameter(), getDiameter(), getDiameter())), detailNodeMass);
            node.getControl(RigidBodyControl.class).setPhysicsLocation(FastMath.interpolateLinear(i*((getDetailedLength()/detail)/getDetailedLength()), Vector3f.ZERO , Vector3f.UNIT_X.mult(getDetailedLength())));
            attachChild(node); 
        }
        
        //create nodes in the remaining part of the cable
        for (int i = 1; i <= rest; i++) {
            Node node = createNode("NodeInBetween", new BoxCollisionShape(new Vector3f(getDiameter(), getDiameter(), getDiameter())), restNodeMass);
            node.getControl(RigidBodyControl.class).setPhysicsLocation(FastMath.interpolateLinear(i*((getLength()/rest)/getLength()), Vector3f.UNIT_X.mult(getDetailedLength()), Vector3f.UNIT_X.mult(getLength()-(getLength()/rest))));
            if(i==rest){
                tempPosStaticAnchor = FastMath.interpolateLinear(i*((getLength()/rest)/getLength()), Vector3f.UNIT_X.mult(getDetailedLength()), Vector3f.UNIT_X.mult(getLength()-(getLength()/rest)));
            }
            attachChild(node); 
        }
    }
    
    /**
     * Create a static anchor node.
     * @return 
     */
    private Node createStaticAnchor() {
        Node node = createNode("StaticAnchor", new BoxCollisionShape(new Vector3f(getDiameter(), getDiameter(), getDiameter())), staticAnchorMass);
        node.getControl(RigidBodyControl.class).setPhysicsLocation(tempPosStaticAnchor);
        attachChild(node);
        return node;
    }
    
    /**
     * Join all nodes. Uses 6DOF Joints to link the single nodes including
     * kinematic and static anchor.
     */
    private void joinNodes(){
        Node currNode;
        Node nextNode;
        SixDofJoint joint;
        
        for (int i = 1; i < getSections(); i++) {
            currNode = (Node)getChildren().get(i-1);
            Vector3f currNodeCoor = currNode.getControl(RigidBodyControl.class).getPhysicsLocation();
            nextNode = (Node)getChildren().get(i);
            Vector3f nextNodeCoor = nextNode.getControl(RigidBodyControl.class).getPhysicsLocation();
            joint = createJoint(currNode, nextNode, Vector3f.ZERO, Vector3f.UNIT_X.mult(-(currNodeCoor.distance(nextNodeCoor))));
        }
        
        currNode = (Node)getChildren().get(getSections()-1);
        nextNode = (Node)getChildren().get(getSections());
        joint = createJoint(currNode, nextNode, Vector3f.ZERO, Vector3f.ZERO);
    }
    
    /**
     * Creat a node object. All nodes belong to COLLISION_GROUP_02.
     * All nodes can collide with objects from COLLISIONGROUP_01 to
     * COLLISIONGROUP_04.
     * 
     * @param name      the name of the node    
     * @param shape     the shape of the node
     * @param mass      the mass of the node
     * @return a node with the given properties
     */
    private Node createNode(String name, CollisionShape shape, float mass) {
        Node node = new Node(name);
        RigidBodyControl control = new RigidBodyControl(shape,mass);
        node.addControl(control);
        /*control.setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_02);
        control.setCollideWithGroups(PhysicsCollisionObject.COLLISION_GROUP_01);
        control.setCollideWithGroups(PhysicsCollisionObject.COLLISION_GROUP_02);
        control.setCollideWithGroups(PhysicsCollisionObject.COLLISION_GROUP_03);
        control.setCollideWithGroups(PhysicsCollisionObject.COLLISION_GROUP_04);*/
        control.setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_08);
        control.setCollideWithGroups(PhysicsCollisionObject.COLLISION_GROUP_09);;
        
        //control is never deactivated, nodes are always moveable
        control.setSleepingThresholds(0f, 0f);
        
        return node;
    }
    
    /**
     * Creates a joint and links to nodes thereby. 
     * @param A         the first node to link
     * @param B         the second node to link
     * @param pivotA    local translation of the joint connection point in node A 
     * @param pivotB    local translation of the joint connection point in node B
     * @return 
     */
    private SixDofJoint createJoint(Node A, Node B, Vector3f pivotA, Vector3f pivotB){
        SixDofJoint joint = new SixDofJoint(A.getControl(RigidBodyControl.class), B.getControl(RigidBodyControl.class), pivotA, pivotB, false);
        joint.setLinearLowerLimit(Vector3f.ZERO);
        joint.setLinearUpperLimit(Vector3f.ZERO);
        joint.setAngularLowerLimit(Vector3f.ZERO);
        joint.setAngularUpperLimit(Vector3f.ZERO);
        return joint;
    }
    
    /**
     * Update the Geometry of the cable. Used for smooth Visualization. Old
     * Geometries are detached from the cable and new ones aligned and attached.
     */
    public void updateGeometry(){
        Vector3f currNode;
        Vector3f nextNode;
        for (int i = 1; i < getSections(); i++) {
            currNode = getChildren().get(i-1).getControl(RigidBodyControl.class).getPhysicsLocation();
            nextNode = getChildren().get(i).getControl(RigidBodyControl.class).getPhysicsLocation();
            detachChildNamed("CableSection");
            Geometry cableSection = new CableSection(worldToLocal(currNode,null),worldToLocal(nextNode,null), getDiameter(), material);
            attachChild(cableSection);
        }
    }

    /**
     * Set the initial Geometry of the cable.
     */
    private void setGeometry() {
        Vector3f currNode;
        Vector3f nextNode;
        for (int i = 1; i < getSections(); i++) {
            currNode = getChildren().get(i-1).getControl(RigidBodyControl.class).getPhysicsLocation();
            nextNode = getChildren().get(i).getControl(RigidBodyControl.class).getPhysicsLocation();
            Geometry cableSection = new CableSection(worldToLocal(currNode,null),worldToLocal(nextNode,null) , getDiameter(), material);
            attachChild(cableSection);
        }
    }
    
    
    /**
     * Set the physics of the cable. Here you can set different parameters of
     * the joints and nodes to create different "cable behaviour".
     */
    private void setCablePhysics() {
        for (int i = 0; i < getChildren().size(); i++) {
            try {
                Node currNode = (Node) getChildren().get(i);
                RigidBodyControl currControl = currNode.getControl(RigidBodyControl.class);
                currControl.setDamping(0.99f, 0.99f);
            } catch (Exception e) {
            }
        }
    }

    /**
     * @return the length
     */
    public float getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(float length) {
        this.length = length;
    }

    /**
     * @return the detailedLength
     */
    public float getDetailedLength() {
        return detailedLength;
    }

    /**
     * @param detailedLength the detailedLength to set
     */
    public void setDetailedLength(float detailedLength) {
        this.detailedLength = detailedLength;
    }

    /**
     * @return the sections
     */
    public int getSections() {
        return sections;
    }

    /**
     * @param sections the sections to set
     */
    public void setSections(int sections) {
        this.sections = sections;
    }

    /**
     * @return the posStaticAnchor
     */
    public Vector3f getPosStaticAnchor() {
        return posStaticAnchor;
    }

    /**
     * @param posStaticAnchor the posStaticAnchor to set
     */
    public void setPosStaticAnchor(Vector3f posStaticAnchor) {
        this.posStaticAnchor = posStaticAnchor;
    }

    /**
     * @return the diameter
     */
    public float getDiameter() {
        return diameter;
    }

    /**
     * @param diameter the diameter to set
     */
    public void setDiameter(float diameter) {
        this.diameter = diameter;
    }

    /**
     * @return the kineticAnchor
     */
    public Node getKineticAnchor() {
        return kineticAnchor;
    }

    /**
     * @return the cableMass
     */
    public float getCableMass() {
        return mass;
    }

    /**
     * @param cableMass the cableMass to set
     */
    public void setCableMass(float cableMass) {
        this.mass = cableMass;
    }
}