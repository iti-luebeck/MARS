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
package mars.xml;

/**
 * A special HashMapEntry so we can store easily the unit used by a physical
 * thing. For example temperatur = C°.
 *
 * @author Thomas Tosik
 */
public class HashMapEntry {

    private String unit = "";

    private Object value;

    /**
     *
     */
    public HashMapEntry() {
        unit = "";
    }

    /**
     *
     * @param unit
     * @param value
     */
    public HashMapEntry(String unit, Object value) {
        this.unit = unit;
        this.value = value;
    }

    /**
     *
     * @return
     */
    public String getUnit() {
        if (unit != null) {
            return unit;
        } else {
            return "";
        }
    }

    /**
     *
     * @return
     */
    public Object getValue() {
        return value;
    }

    /**
     *
     * @param unit
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     *
     * @param value
     */
    public void setValue(Object value) {
        this.value = value;
    }
}
