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
package mars.FishSim.food;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;
import mars.FishSim.FishSim;

/**
 *
 * @author Mandy Feldvoß
 */
public class FoodSource extends Node implements IFoodSource {

    FishSim sim;
    String name;
    float size;
    ArrayList<FoodSourceMap> foreignMaps = new ArrayList<FoodSourceMap>();

    /**
     *
     * @param sim Simulation
     * @param size Size of the foodsource
     * @param localTrans Position of the foodsource
     */
    public FoodSource(FishSim sim, float size, Vector3f localTrans) {
        this.sim = sim;
        this.size = size;
        setLocalTranslation(localTrans);
        sim.getRootNode().attachChild(this);
    }

    /**
     *
     * @param name Name of the foodsource
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return Name of the foodsource
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Delete foodsource
     */
    public void delete() {
        for (int i = 0; i < foreignMaps.size(); i++) {
            foreignMaps.get(i).remove(this);
        }
        sim.getRootNode().detachChild(this);
    }

    /**
     *
     * @param foreignMap foodsourcemap which this foodsource belongs to
     */
    @Override
    public void addToMap(FoodSourceMap foreignMap) {
        foreignMaps.add(foreignMap);
    }

    @Override
    public Vector3f getNearestLocation(Vector3f location) {
        Vector3f radiusVec = location.subtract(getLocalTranslation()).normalize().mult(size / 1000f);
        if (getLocalTranslation().distance(location) > getLocalTranslation().distance(getLocalTranslation().add(radiusVec))) {
            return getLocalTranslation().add(radiusVec);
        } else {
            return new Vector3f(location);
        }
    }

    /**
     *
     * @param location Location of the fish
     * @param amount The amount that can be eaten by a fish
     * @return Saturation which is granted to the fish
     */
    @Override
    public float feed(Vector3f location, float amount) {
        size -= amount;
        if (size <= 0) {
            sim.removeFoodSource(this);
        }
        return 1 + amount;
    }
}
