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
package mars.sensors;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import mars.PhysicalEnvironment;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.auv.AUV;
import mars.events.AUVObjectEvent;
import mars.geodesy.Ellipsoid;
import mars.geodesy.GeodeticCalculator;
import mars.geodesy.GlobalPosition;
import mars.states.SimState;

/**
 * A GPS sensor. Translates MARS world coordinates to GPS. Needs a reference point.
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class GPSReceiver extends Sensor {

    @XmlElement(name = "Positionmeter")
    Positionmeter pos = new Positionmeter();

    private Geometry GPSReceiverGeom;

    private GeodeticCalculator geoCalc = new GeodeticCalculator();
    private Ellipsoid reference = Ellipsoid.WGS84;

    /**
     *
     */
    public GPSReceiver() {
        super();
    }

    /**
     *
     * @param simstate
     * @param pe
     */
    public GPSReceiver(SimState simstate, PhysicalEnvironment pe) {
        super(simstate);
        this.pe = pe;
        pos.setPhysicalEnvironment(pe);
        pos.setSimState(simState);
    }

    /**
     *
     * @param simstate
     */
    public GPSReceiver(SimState simstate) {
        super(simstate);
        pos.setSimState(simState);
    }

    /**
     *
     * @param sensor
     */
    public GPSReceiver(GPSReceiver sensor) {
        super(sensor);
        pos = (Positionmeter) sensor.getPositionMeter().copy();
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        GPSReceiver sensor = new GPSReceiver(this);
        sensor.initAfterJAXB();
        return sensor;
    }

    /**
     *
     */
    @Override
    public void init(Node auv_node) {
        super.init(auv_node);
        pos.init(auv_node);

        Sphere sphere7 = new Sphere(8, 8, 0.04f);
        GPSReceiverGeom = new Geometry("PressureStart", sphere7);
        Material mark_mat7 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat7.setColor("Color", ColorRGBA.White);
        GPSReceiverGeom.setMaterial(mark_mat7);
        GPSReceiverGeom.updateGeometricState();
        PhysicalExchanger_Node.attachChild(GPSReceiverGeom);
        PhysicalExchanger_Node.setLocalTranslation(getReferencePointWorld());
        rootNode.attachChild(PhysicalExchanger_Node);
    }

    /**
     *
     * @param tpf
     */
    @Override
    public void update(float tpf) {
        pos.update(tpf);
    }

    /**
     *
     */
    @Override
    public void reset() {
        pos.reset();
    }

    /**
     *
     * @return
     */
    public Positionmeter getPositionMeter() {
        return pos;
    }

    /**
     *
     * @return
     */
    public Vector3f getReferencePointGPS() {
        return (Vector3f) variables.get("ReferencePointGPS");
    }

    /**
     *
     * @param ReferencePointGPS
     */
    public void setReferencePointGPS(Vector3f ReferencePointGPS) {
        variables.put("ReferencePointGPS", ReferencePointGPS);
    }

    /**
     *
     * @return
     */
    public Vector3f getReferencePointWorld() {
        return (Vector3f) variables.get("ReferencePointWorld");
    }

    /**
     *
     * @param ReferencePointWorld
     */
    public void setReferencePointWorld(Vector3f ReferencePointWorld) {
        variables.put("ReferencePointWorld", ReferencePointWorld);
    }

    /**
     *
     * @return
     */
    public Float getLatitudeFactor() {
        return (Float) variables.get("LatitudeFactor");
    }

    /**
     *
     * @param LatitudeFactor
     */
    public void setLatitudeFactor(Float LatitudeFactor) {
        variables.put("LatitudeFactor", LatitudeFactor);
    }

    @Override
    public void setPhysicalEnvironment(PhysicalEnvironment pe) {
        super.setPhysicalEnvironment(pe);
        pos.setPhysicalEnvironment(pe);
    }

    /**
     *
     * @param simState
     */
    @Override
    public void setSimState(SimState simState) {
        super.setSimState(simState);
        pos.setSimState(simState);
    }

    @Override
    public void setPhysicsControl(RigidBodyControl physics_control) {
        super.setPhysicsControl(physics_control);
        pos.setPhysicsControl(physics_control);
    }

    /**
     *
     * @param visible
     */
    @Override
    public void setNodeVisibility(boolean visible) {
        super.setNodeVisibility(visible);
        pos.setNodeVisibility(visible);
    }

    /**
     *
     * @param name
     */
    @Override
    public void setName(String name) {
        super.setName(name);
        pos.setName(name + "_positionmeter");
    }

    /**
     *
     * @param enabled
     */
    @Override
    public void setEnabled(Boolean enabled) {
        super.setEnabled(enabled);
        pos.setEnabled(enabled);
    }

    @Override
    public void setAuv(AUV auv) {
        super.setAuv(auv);
        pos.setAuv(auv);
    }

    public Vector3f getGPS() {
        GlobalPosition pointA = new GlobalPosition(getReferencePointGPS().z, getReferencePointGPS().x, 0.0); // Point A

        GlobalPosition userPos = new GlobalPosition(getReferencePointGPS().z + 0.01f, getReferencePointGPS().x, 0.0); // Point B
        GlobalPosition userPos2 = new GlobalPosition(getReferencePointGPS().z, getReferencePointGPS().x + 0.01f, 0.0); // Point B

        double distanceLat = geoCalc.calculateGeodeticCurve(reference, userPos, pointA).getEllipsoidalDistance(); // Distance between Point A and Point B
        double distanceLon = geoCalc.calculateGeodeticCurve(reference, userPos2, pointA).getEllipsoidalDistance(); // Distance between Point A and Point B

        Vector3f diffPosition = pos.getWorldPosition().subtract(getReferencePointWorld());
        double metLat = (1d / distanceLat) * (Math.abs(pointA.getLatitude() - userPos.getLatitude()));
        double latitude = diffPosition.z * metLat;

        double metLon = (1d / distanceLon) * (Math.abs(pointA.getLongitude() - userPos2.getLongitude()));
        double longitude = diffPosition.x * metLon;

        float altitudeRet = pos.getPositionY();
        float latitudeRet = ((getReferencePointGPS().z) - (float) latitude);
        float longitudeRet = ((getReferencePointGPS().x) + (float) longitude);

        return new Vector3f(altitudeRet, latitudeRet, longitudeRet);
    }

    @Override
    public void publishData() {
        super.publishData();
        AUVObjectEvent auvEvent = new AUVObjectEvent(this, getGPS(), System.currentTimeMillis());
        notifyAdvertisementAUVObject(auvEvent);
    }
}
