/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars;

import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;

/**
 * Use this interface if you want to move sensors/actuators with servos for
 * example
 *
 * @author Thomas Tosik
 */
public interface Moveable {

    /**
     *
     * @param alpha
     */
    public void updateRotation(float alpha);

    /**
     *
     * @param world_rotation_axis_points
     */
    public void setLocalRotationAxisPoints(Matrix3f world_rotation_axis_points);

    /**
     *
     * @param translation_axis
     * @param new_realative_position
     */
    public void updateTranslation(Vector3f translation_axis, Vector3f new_realative_position);

    /**
     *
     * @return
     */
    public String getSlaveName();
}
