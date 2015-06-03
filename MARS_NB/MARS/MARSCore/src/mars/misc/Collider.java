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
package mars.misc;

import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResults;
import com.jme3.scene.Spatial;
import com.jme3.util.SafeArrayList;

/**
 * This class can be used when you need collidable nodes, and check for
 * them, but cant add them to a second node. Sice The ScenGraph doesn't allow
 * multiple parents. Copy from JME Nodes(?).
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class Collider {

    /**
     *
     */
    protected SafeArrayList<Spatial> children = new SafeArrayList<Spatial>(Spatial.class);

    /**
     *
     */
    public Collider() {
    }

    /**
     *
     * @param child
     * @return
     */
    public int attachChild(Spatial child) {
        if (child == null) {
            throw new IllegalArgumentException("child cannot be null");
        }

        children.add(child);

        return children.size();
    }

    /**
     *
     * <code>detachAllChildren</code> removes all children attached to this
     * node.
     */
    public void detachAllChildren() {
        for (int i = children.size() - 1; i >= 0; i--) {
            detachChildAt(i);
        }
    }

    /**
     * <code>detachChild</code> removes a given child from the node's list. This
     * child will no longer be maintained.
     *
     * @param child the child to remove.
     * @return the index the child was at. -1 if the child was not in the list.
     */
    public int detachChild(Spatial child) {
        if (child == null) {
            throw new NullPointerException();
        }

        int index = children.indexOf(child);
        if (index != -1) {
            detachChildAt(index);
        }
        return index;
    }

    /**
     * <code>detachChild</code> removes a given child from the node's list. This
     * child will no longe be maintained. Only the first child with a matching
     * name is removed.
     *
     * @param childName the child to remove.
     * @return the index the child was at. -1 if the child was not in the list.
     */
    public int detachChildNamed(String childName) {
        if (childName == null) {
            throw new NullPointerException();
        }

        for (int x = 0, max = children.size(); x < max; x++) {
            Spatial child = children.get(x);
            if (childName.equals(child.getName())) {
                detachChildAt(x);
                return x;
            }
        }
        return -1;
    }

    /**
     *
     * <code>detachChildAt</code> removes a child at a given index. That child
     * is returned for saving purposes.
     *
     * @param index the index of the child to be removed.
     * @return the child at the supplied index.
     */
    public Spatial detachChildAt(int index) {
        Spatial child = children.remove(index);
        return child;
    }

    /**
     *
     * @param other
     * @param results
     * @return
     */
    public int collideWith(Collidable other, CollisionResults results) {
        int total = 0;
        for (Spatial child : children.getArray()) {
            total += child.collideWith(other, results);
        }
        return total;
    }
}
