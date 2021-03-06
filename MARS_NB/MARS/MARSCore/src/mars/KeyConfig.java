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
package mars;

import java.util.HashMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.xml.HashMapAdapter;
import com.jme3.input.KeyNames;
import java.util.Map.Entry;
import javax.swing.tree.TreePath;
import javax.xml.bind.annotation.XmlElement;

/**
 * This class stores the mapping between the keyboard input and an action that
 * you want to perform with a class that extends the KEYS interface.
 *
 * @author Thomas Tosik
 */
@XmlRootElement(name = "KeyConfig")
@XmlAccessorType(XmlAccessType.NONE)
public class KeyConfig{

    @XmlJavaTypeAdapter(HashMapAdapter.class)
    private HashMap<String, String> keys;

    @XmlElement
    private String auv_key_focus = "";

    /**
     *
     */
    public KeyConfig() {
    }

    /**
     *
     * @return
     */
    public HashMap<String, String> getKeys() {
        return keys;
    }

    /**
     *
     */
    public void initAfterJAXB() {

    }

    /**
     *
     * @param target
     * @param hashmapname
     */
    public void updateState(String target, String hashmapname) {
        if (target.equals("enabled") && hashmapname.equals("Axis")) {

        }
    }

    /**
     *
     * @param path
     */
    public void updateState(TreePath path) {
        if (path.getPathComponent(0).equals(this)) {//make sure we want to change auv params
            if (path.getParentPath().getLastPathComponent().toString().equals("Settings")) {
                updateState(path.getLastPathComponent().toString(), "");
            } else {
                updateState(path.getLastPathComponent().toString(), path.getParentPath().getLastPathComponent().toString());
            }
        }
    }

    /**
     *
     */
    public void createKeys() {
        KeyNames keynames = new KeyNames();
        keys = new HashMap<String, String>();
        for (int i = 0; i < 223; i++) {
            keys.put("#" + i + " " + keynames.getName(i), "");
        }
    }

    private int getKeyNumber(String keyname) {
        return Integer.valueOf(keyname.substring(1, keyname.indexOf(" ")));
    }

    /**
     *
     * @param mapping
     * @return
     */
    public int getKeyNumberForMapping(String mapping) {
        for (Entry<String, String> entry : keys.entrySet()) {
            if (mapping.equals(entry.getValue())) {
                return getKeyNumber(entry.getKey());
            }
        }
        return 255;
    }

    /**
     *
     * @return
     */
    public String getAuv_key_focus() {
        return auv_key_focus;
    }

    /**
     *
     * @param auv_key_focus
     */
    public void setAuv_key_focus(String auv_key_focus) {
        this.auv_key_focus = auv_key_focus;
    }
}
