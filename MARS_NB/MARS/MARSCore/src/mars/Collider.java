/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars;

import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResults;
import com.jme3.scene.Spatial;
import com.jme3.util.SafeArrayList;

/**
 * This class can be used when you need to have nodes collidable, and check for
 * them, but cant add them to a second node. Sice The ScenGraph doesn't allow
 * multiple parents
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
