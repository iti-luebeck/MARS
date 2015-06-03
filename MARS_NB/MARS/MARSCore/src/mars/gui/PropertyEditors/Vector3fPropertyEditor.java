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
