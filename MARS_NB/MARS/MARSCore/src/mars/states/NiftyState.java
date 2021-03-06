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
package mars.states;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.dynamic.PanelCreator;
import de.lessvoid.nifty.controls.dynamic.TextCreator;
import de.lessvoid.nifty.effects.EffectEventId;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.tools.SizeValue;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import mars.MARS_Main;
import mars.accumulators.Accumulator;
import mars.auv.AUV;
import mars.auv.AUV_Manager;

/**
 * The nifty app state for the popup/hover menues of AUVs.
 * 
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class NiftyState extends AbstractAppState implements ScreenController {

    private Nifty nifty;
    private NiftyJmeDisplay niftyDisplay;
    private Screen screen;
    private MARS_Main mars;
    private Node rootNode = new Node("NiftyState Root Node");
    private AssetManager assetManager;
    private AUV auv;
    private AUV_Manager auv_manager;

    /**
     * custom methods
     */
    public NiftyState() {
    }

    /**
     *
     */
    public void initNifty() {
        niftyDisplay = new NiftyJmeDisplay(assetManager,
                mars.getInputManager(),
                mars.getAudioRenderer(),
                mars.getGuiViewPort());
        nifty = niftyDisplay.getNifty();
        nifty.setIgnoreKeyboardEvents(true);
        nifty.setIgnoreMouseEvents(true);
                
        nifty.fromXml("nifty_energy_popup.xml", "hoverMenu");
        
        //hide main panel because niftyys effect/event system sucks!
        setHoverMenuForAUV(false);
        setSpeedMenu(false);
        setPopupMenu(true);

        //set logging to less spam
        Logger.getLogger("de.lessvoid.nifty").setLevel(Level.SEVERE);
        Logger.getLogger("NiftyInputEventHandlingLog").setLevel(Level.SEVERE);

        mars.getGuiViewPort().addProcessor(niftyDisplay);
    }

    /**
     *
     * @param auv
     * @param x
     * @param y
     */
    public void setHoverMenuForAUV(AUV auv, int x, int y) {
        // find old text
        this.auv = auv;

        Element niftyElement = nifty.getScreen("hoverMenu").findElementByName("hover");
        // swap old with new text
        if (niftyElement != null) {
            niftyElement.setConstraintX(new SizeValue(String.valueOf(x + 5)));
            niftyElement.setConstraintY(new SizeValue(String.valueOf(y + 10)));
            niftyElement.getParent().layoutElements();
            Element text = niftyElement.findElementByName("hover_left_text");
            if (text != null) {
                text.getRenderer(TextRenderer.class).setText(getAkkuForAUV());

                text.getRenderer(TextRenderer.class).setColor(new Color(1f - getAkkuValueForAUV(), getAkkuValueForAUV(), 0f, 1f));
                text.getParent().layoutElements();
            }
            text = niftyElement.findElementByName("hover_left_text_name");
            if (text != null) {
                text.getRenderer(TextRenderer.class).setText(auv.getName());

                text.getParent().layoutElements();
            }
            setHoverMenuForAUV(true);
        }
    }

    /**
     *
     * @param auv
     * @param x
     * @param y
     */
    public void setPopUpNameForAUV(AUV auv, int x, int y) {
        // find old text

        Screen hoverMenu = nifty.getScreen("hoverMenu");
        Element niftyElementLayer = hoverMenu.findElementByName("popup_background");
        Element findElementByName = niftyElementLayer.findElementByName("popup_" + auv.getName());
        if (findElementByName == null) {//not existing, so we have to create it here
            //create base panel
            PanelCreator createPanel = new PanelCreator();
            createPanel.setHeight("5%");
            createPanel.setWidth("15%");
            createPanel.setAlign("center");
            createPanel.setChildLayout("horizontal");
            createPanel.setBackgroundImage("energy_popup2.png");
            createPanel.setImageMode("resize:24,2,15,9,24,2,15,14,24,2,15,9");
            createPanel.setName("popup_" + auv.getName());
            createPanel.setId("popup_" + auv.getName());
            Element newPanel = createPanel.create(nifty, hoverMenu, niftyElementLayer);

            //create txt panel
            PanelCreator createPanel2 = new PanelCreator();
            createPanel2.setAlign("left");
            createPanel2.setVAlign("center");
            createPanel2.setBackgroundColor("#0000");
            createPanel2.setChildLayout("vertical");
            createPanel2.setName("popup_left_" + auv.getName());
            createPanel2.setId("popup_left_" + auv.getName());
            Element newPanel2 = createPanel2.create(nifty, hoverMenu, newPanel);

            //create txt
            TextCreator createText = new TextCreator(auv.getName());
            createText.setId("popup_left_text_" + auv.getName());
            createText.setText(auv.getName());
            createText.setWidth("100%");
            createText.setHeight("100%");
            createText.setWrap(true);
            createText.setFont("Interface/Fonts/Default.fnt");
            Element txt = createText.create(nifty, hoverMenu, newPanel2);

            newPanel.show();
            newPanel.layoutElements();
        }

        findElementByName = niftyElementLayer.findElementByName("popup_" + auv.getName());
        if (findElementByName != null) {//its existing, so we have to set the position
            findElementByName.setConstraintX(new SizeValue(String.valueOf(x + 5)));
            findElementByName.setConstraintY(new SizeValue(String.valueOf(y - 15)));
            findElementByName.getParent().layoutElements();
        }
    }

    /**
     *
     * @return
     */
    public float getAkkuValueForAUV() {
        if (auv != null) {
            Accumulator accumulator = auv.getAccumulator("main");
            if (accumulator != null) {
                return (Math.round(1f * (accumulator.getActualCurrent() / accumulator.getCapacity())));
            } else {
                return 0f;
            }
        } else {
            return 0f;
        }
    }

    /**
     *
     * @return
     */
    public String getAkkuForAUV() {
        if (auv != null) {
            String ret = "";
            HashMap<String, Accumulator> accumulators = auv.getAccumulators();
            for (Map.Entry<String, Accumulator> entry : accumulators.entrySet()) {
                String string = entry.getKey();
                Accumulator accumulator = entry.getValue();
                ret = ret + String.valueOf(Math.round(100f * (accumulator.getActualCurrent() / accumulator.getCapacity()))) + "%";
                break;//<- needs to be fixed for multiple accus
            }
            if (!accumulators.isEmpty()) {
                return ret;
            } else {
                return "NO ACCU!";
            }
        } else {
            return "NO AUV!";
        }
    }

    /**
     *
     * @param visible
     */
    public void setHoverMenuForAUV(boolean visible) {
        Element niftyElement = nifty.getScreen("hoverMenu").findElementByName("hover");
        if (niftyElement != null) {
            if (visible) {
                niftyElement.showWithoutEffects();
            } else {
                niftyElement.hideWithoutEffect();
            }
        }
    }

    /**
     *
     * @param visible
     */
    public void setSpeedMenu(boolean visible) {
        Element niftyElement = nifty.getScreen("hoverMenu").findElementByName("speed");
        if (niftyElement != null) {
            if (visible) {
                niftyElement.show();
                Element niftyElement2 = nifty.getScreen("hoverMenu").findElementByName("speed_background");
                if (niftyElement2 != null) {
                    niftyElement2.startEffect(EffectEventId.onCustom, null, "fadeIn");
                }
            } else {
                niftyElement.hide();
            }
        }
    }

    /**
     *
     * @param visible
     */
    public void setPopupMenu(boolean visible) {
        Element niftyElement3 = nifty.getScreen("hoverMenu").findElementByName("popup_background");
        if (niftyElement3 != null) {
            if (visible) {
                niftyElement3.show();
            } else {
                niftyElement3.hide();
            }
        }
    }

    /**
     *
     * @param auv
     * @param visible
     */
    public void setPopupMenu(AUV auv, boolean visible) {
        Element niftyElement3 = nifty.getScreen("hoverMenu").findElementByName("popup_" + auv.getName());
        if (niftyElement3 != null) {
            if (visible) {
                niftyElement3.show();
            } else {
                niftyElement3.hide();
            }
        }
    }

    /**
     *
     * @param visible
     */
    public void setSpeedUp(boolean visible) {

        Element niftyElement = nifty.getScreen("hoverMenu").findElementByName("speed");

        // swap old with new text
        if (niftyElement != null) {
            niftyElement.setConstraintX(new SizeValue(String.valueOf(0 + 5)));
            niftyElement.setConstraintY(new SizeValue(String.valueOf(0 + 10)));
            niftyElement.getParent().layoutElements();

            Element text = niftyElement.findElementByName("speed_left_text");
            if (text != null) {
                text.getRenderer(TextRenderer.class).setText(String.valueOf(mars.getSpeed()));

                text.getRenderer(TextRenderer.class).setColor(new Color(1f, 1f, 1f, 1f));
                text.getParent().layoutElements();
            }
            setSpeedMenu(true);
        }
    }

    /**
     *
     */
    public void show() {
        Element niftyElement = nifty.getScreen("hoverMenu").findElementByName("background");
        if (niftyElement != null) {
            niftyElement.show();
        }
        setHoverMenuForAUV(false);

        Element niftyElement2 = nifty.getScreen("hoverMenu").findElementByName("speed_background");
        if (niftyElement2 != null) {
            niftyElement2.show();
        }
        setSpeedMenu(false);

        Element niftyElement3 = nifty.getScreen("hoverMenu").findElementByName("popup_background");
        if (niftyElement3 != null) {
            niftyElement3.show();
        }
        setPopupMenu(true);
    }

    /**
     *
     * @return
     */
    public Node getRootNode() {
        return rootNode;
    }

    /**
     * Nifty GUI ScreenControl methods
     *
     * @param nifty
     * @param screen
     */
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
    }

    /**
     *
     */
    public void onStartScreen() {
    }

    /**
     *
     */
    public void onEndScreen() {
    }

    /**
     * jME3 AppState methods
     *
     * @param stateManager
     * @param app
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        if (!super.isInitialized()) {
            if (app instanceof MARS_Main) {
                mars = (MARS_Main) app;
                assetManager = mars.getAssetManager();
            } else {
                throw new RuntimeException("The passed application is not of type \"MARS_Main\"");
            }
            initNifty();
        }
        super.initialize(stateManager, app);
    }

    /**
     *
     */
    @Override
    public void cleanup() {
        super.cleanup();
        mars.getRootNode().detachChild(getRootNode());
    }

    /**
     *
     * @param tpf
     */
    @Override
    public void update(float tpf) {
        if (!super.isEnabled()) {
            return;
        }
        super.update(tpf);

        rootNode.updateLogicalState(tpf);
        rootNode.updateGeometricState();
    }

    /**
     *
     * @param stateManager
     */
    @Override
    public void stateAttached(AppStateManager stateManager) {
        super.stateAttached(stateManager);
    }

    /**
     *
     * @param stateManager
     */
    @Override
    public void stateDetached(AppStateManager stateManager) {
        super.stateDetached(stateManager);
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isEnabled() {
        return super.isEnabled();
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isInitialized() {
        return super.isInitialized();
    }

    /**
     *
     */
    @Override
    public void postRender() {
        if (!super.isEnabled()) {
            return;
        }
        super.postRender();
    }

    /**
     *
     * @param rm
     */
    @Override
    public void render(RenderManager rm) {
        if (!super.isEnabled()) {
            return;
        }
        super.render(rm);
    }

    /**
     *
     * @param enabled
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            rootNode.setCullHint(Spatial.CullHint.Always);
        } else {
            rootNode.setCullHint(Spatial.CullHint.Never);
        }
    }
}
