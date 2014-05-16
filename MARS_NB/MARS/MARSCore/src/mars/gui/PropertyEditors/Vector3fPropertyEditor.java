/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.gui.PropertyEditors;

import com.jme3.math.Vector3f;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.explorer.propertysheet.PropertyModel;

/**
 * This editor is used to edit positions and rotations in the property sheet. It
 * used an inplace editor which consist of three simple text fields to edit x, y
 * and z. The inplace editor is instance of VectorEditor.
 *
 * @author Christian Friedrich
 */
public class Vector3fPropertyEditor extends PropertyEditorSupport implements ExPropertyEditor, InplaceEditor.Factory {

    private InplaceEditor ed = null;

    @Override
    public String getAsText() {
        Vector3f v = (Vector3f) getValue();
        if (v == null) {
            return "Nothing set";
        }
        return "X: " + v.x + " Y: " + v.y + " Z: " + v.z;
    }

    /**
     *
     * @param env
     */
    @Override
    public void attachEnv(PropertyEnv env) {
        env.registerInplaceEditorFactory(this);
    }

    /**
     *
     * @return
     */
    @Override
    public InplaceEditor getInplaceEditor() {
        if (ed == null) {
            ed = new Inplace();
        }
        return ed;
    }

    private static class Inplace implements InplaceEditor {

        private final VectorEditor vectorEditor = new VectorEditor();
        private PropertyEditor editor = null;

        @Override
        public void connect(PropertyEditor propertyEditor, PropertyEnv env) {
            editor = propertyEditor;
            reset();
        }

        @Override
        public JComponent getComponent() {
            return vectorEditor;
        }

        @Override
        public void clear() {
            //avoid memory leaks:
            editor = null;
            model = null;
        }

        @Override
        public Object getValue() {
            return vectorEditor.getCoordinates();
        }

        @Override
        public void setValue(Object object) {
            vectorEditor.setCoordinates((Vector3f) (object));
        }

        @Override
        public boolean supportsTextEntry() {
            return true;
        }

        @Override
        public void reset() {
            vectorEditor.setCoordinates((Vector3f) (editor.getValue()));
        }

        @Override
        public KeyStroke[] getKeyStrokes() {
            return new KeyStroke[0];
        }

        @Override
        public PropertyEditor getPropertyEditor() {
            return editor;
        }

        @Override
        public PropertyModel getPropertyModel() {
            return model;
        }

        private PropertyModel model;

        @Override
        public void setPropertyModel(PropertyModel propertyModel) {
            this.model = propertyModel;
        }

        @Override
        public boolean isKnownComponent(Component component) {
            return component == vectorEditor || vectorEditor.isAncestorOf(component);
        }

        @Override
        public void addActionListener(ActionListener actionListener) {
            //do nothing - not needed for this component
        }

        @Override
        public void removeActionListener(ActionListener actionListener) {
            //do nothing - not needed for this component
        }

    }
}
