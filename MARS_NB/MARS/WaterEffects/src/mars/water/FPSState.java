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
package mars.water;

import com.jme3.app.state.AbstractAppState;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Mandy-Jane Feldvoß, John Paul Jonte Einfacher FPS-Test zur
 * Ermittelung der durchschnittlichen FPS. Min- und max-FPS werden ebenfalls
 * bestimmt, jedoch nur über jeweiligen TPF-Wert.
 */
public class FPSState extends AbstractAppState {

    private float initTime; //Falls Zeit zur Initialisierung benötigt wird
    private float duration; //Dauer des Tests
    private float remainIT;
    private float remainDur;
    private float minTPF = 10; //Initialwert um zusätliche Abfrage zu sparen. Sollte nicht zum Problem werden ;)
    private float maxTPF = 0;
    private float sumTPF = 0;
    private int sumCalls = 0;
    private String fileName;
    private String info;
    private String title;
    private Callable setup = null;

    /**
     *
     * @param initTime
     * @param duration
     * @param fileName
     * @param title
     */
    public FPSState(float initTime, float duration, String fileName, String title) {
        this.initTime = initTime;
        this.duration = duration;
        this.fileName = fileName;
        this.title = title;

        remainIT = initTime;
        remainDur = duration;
    }

    /**
     *
     * @param info
     */
    public void addInfo(String info) { // Erlaubt das Hinzufügen weiterer Strings in die Ausgabedatei
        this.info += info + "\n";
    }

    /**
     *
     * @param setup
     */
    public void addSetup(Callable setup) {
        this.setup = setup;
    }

    /**
     *
     * @param tpf
     */
    @Override
    public void update(float tpf) { // Aus der update-Methode der SimpleApplication(oder vergleichbar) mit der time-per-frame aufrufen
        if (remainIT > 0f) {
            remainIT -= tpf;

            // call setup method
            if (remainIT <= 0) {
                Logger.getLogger(FPSState.class.getName()).log(Level.INFO, "Starting FPS Test \"" + title + "\"");

                try {
                    if (setup != null) {
                        setup.call();
                    }
                } catch (Exception ex) {
                    Logger.getLogger(FPSState.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (remainDur > 0f) {
            remainDur -= tpf;
            sumTPF += tpf;
            sumCalls += 1;

            if (tpf < minTPF) {
                minTPF = tpf;
            }
            if (tpf > maxTPF) {
                maxTPF = tpf;
            }

            /*if(tpf > 1f/60f){ // Schreibt Zeitpunkt und TPF/FPS ins Ausgabefile, falls 60FPS unterschritten werden
             addInfo("" + (duration-remainDur) + " " + tpf + " " + 1/tpf); 
             System.out.println(""+(duration-remainDur));
             }*/
            if (remainDur <= 0) {
                Logger.getLogger(FPSState.class.getName()).log(Level.INFO, "Stopping FPS Test \"" + title + "\"");

                try {
                    FileWriter writer = new FileWriter(fileName, true);
                    writer.write(getText());
                    writer.close();
                } catch (IOException ex) {
                    Logger.getLogger(FPSState.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     *
     * @return
     */
    public String getText() {
        String ans = "************************************************************************************\n";
        ans += "====================================================================================\n";
        ans += title + "\n";
        ans += System.currentTimeMillis() + " Starting time: " + initTime + "s     Duration: " + duration + "s \n";
        ans += "Info: " + info;
        ans += "====================================================================================\n";
        ans += "Min TPF, max FPS:   " + minTPF + ", " + 1 / minTPF + "\n";
        ans += "Max TPF, min FPS:   " + maxTPF + ", " + 1 / maxTPF + "\n";
        ans += "Avg TPF, avg FPS:   " + sumTPF / sumCalls + ", " + 1 / (sumTPF / sumCalls) + "\n";
        ans += "====================================================================================\n";
        ans += "************************************************************************************\n\n";
        return ans;
    }
}
