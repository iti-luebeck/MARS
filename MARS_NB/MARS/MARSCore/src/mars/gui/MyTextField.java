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
package mars.gui;

import javax.swing.JTextField;
import javax.swing.text.Document;

/**
 * A special text field which filters inputs.
 * 
 * @author Thomas Tosik
 */
public class MyTextField extends JTextField{
    String value;
    String hashmapname;
    Object obj;

    /**
     * Mimic all the constructors people expect with text fields
     */
    public MyTextField() {
        this("", 5);
    }

    /**
     *
     * @param text
     * @param value
     * @param obj
     * @param hashmapname 
     */
    public MyTextField(String text,String value, Object obj,String hashmapname) {
        this(text, 5);
        this.value = value;
        this.obj = obj;
        this.hashmapname = hashmapname;
    }

    /**
     *
     * @param columns
     */
    public MyTextField(int columns) {
        this("", columns);
    }

    /**
     *
     * @param doc
     * @param text
     * @param columns
     */
    public MyTextField(Document doc, String text, int columns){
        super(doc, text, columns);
        // Listen to our own action events so that we know when to stop editing.
    }

    /**
     *
     * @param text
     * @param columns
     */
    public MyTextField(String text, int columns) {
        super(text, columns);
        // Listen to our own action events so that we know when to stop editing.
    }

    /**
     *
     * @return
     */
    public String getHashMapName() {
        return hashmapname;
    }

    /**
     *
     * @return
     */
    public Object getObject() {
        return obj;
    }

    /**
     *
     * @param obj
     */
    public void setObject(Object obj) {
        this.obj = obj;
    }

    /**
     * 
     * @return
     */
    public String getValue() {
        return value;
    }
}
