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

import java.util.HashMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * This class is responsible for managing/loading different simulator
 * configuration.
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@XmlRootElement(name = "Config")
@XmlAccessorType(XmlAccessType.NONE)
public class ConfigManager {

    @XmlJavaTypeAdapter(HashMapAdapter.class)
    private HashMap<String, Object> config;

    /**
     *
     */
    public ConfigManager() {
    }

    /**
     *
     */
    public void initAfterJAXB() {

    }

    /**
     *
     * @return
     */
    public String getConfigName() {
        return (String) config.get("defaultConfig");
    }

    /**
     *
     * @param defaultConfig
     */
    public void setConfigName(String defaultConfig) {
        config.put("defaultConfig", defaultConfig);
    }

    /**
     *
     * @return
     */
    public boolean isAutoEnabled() {
        return (Boolean) config.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setAutoEnabled(boolean enabled) {
        config.put("enabled", enabled);
    }
}
