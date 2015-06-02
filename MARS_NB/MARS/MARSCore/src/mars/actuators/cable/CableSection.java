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

import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Cylinder;

/**
 * Geometry for a cable section. Use one instance of it for every section
 * of a cable to visualize it.
 * 
 * @author lasse hansen
 */
public class CableSection extends Geometry{
    
    /**
     * Constructor. 
     * @param start     the start point of the cable section
     * @param end       the end point of the cable section
     * @param diameter  the diameter of the cable
     * @param material  the material the cable consists of
     */
    public CableSection(Vector3f start, Vector3f end, float diameter, Material material) {
        super("CableSection");
        
        //let the geometry hang a little over 
        float overhang = 0.05f;
 
        //Choose a cylinder geometry to represent a cable section
        Cylinder cylinder = new Cylinder(10, 10, diameter, start.distance(end)+overhang);
        this.mesh=cylinder;
 
        //align the geometry along the cable
        setLocalTranslation(FastMath.interpolateLinear(.5f, start, end));
        lookAt(end, Vector3f.UNIT_Y);
        
        setMaterial(material); 
    }
    
}