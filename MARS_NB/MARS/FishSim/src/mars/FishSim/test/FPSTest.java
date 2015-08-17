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
package mars.FishSim.test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Acer
 */
public class FPSTest {

    private float initTime;
    private float duration;
    private float remainIT;
    private float remainDur;
    private float minTPF = 10;
    private float maxTPF = 0;
    private float sumTPF = 0;
    private int sumCalls = 0;
    private String fileName;

    public FPSTest(float initTime, float duration, String fileName) {
        this.initTime = initTime;
        remainIT = initTime;
        this.duration = duration;
        remainDur = duration;
        this.fileName = fileName;
    }

    public void next(float tpf) {
        if (remainIT > 0f) {
            remainIT -= tpf;
        } else {
            if (remainDur > 0f) {
                remainDur -= tpf;
                sumTPF += tpf;
                sumCalls += 1;
                if (tpf < minTPF) {
                    minTPF = tpf;
                }
                if (tpf > maxTPF) {
                    maxTPF = tpf;
                }

                if (remainDur <= 0) {
                    try {
                        FileWriter writer = new FileWriter(fileName, true);
                        writer.write(getText());
                        writer.close();
                    } catch (IOException ex) {
                        Logger.getLogger(FPSTest.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    public String getText() {
        String ans = "\n ************************************************************************************ \n";
        ans += " ==================================================================================== \n";
        ans += " " + System.currentTimeMillis() + " Initial starting time: " + initTime + "s     Test duration: " + duration + "s \n";
        ans += " ==================================================================================== \n";
        ans += " Minimum time per frame(tpf) / maximum frames per second(fps):   " + minTPF + " / " + 1 / minTPF + "\n";
        ans += " Maximum time per frame(tpf) / minimum frames per second(fps):   " + maxTPF + " / " + 1 / maxTPF + "\n";
        ans += " Average time per frame(tpf) / average frames per second(fps):   " + sumTPF / sumCalls + " / " + 1 / (sumTPF / sumCalls) + "\n";
        ans += " ==================================================================================== \n";
        ans += " ************************************************************************************ \n";
        return ans;
    }
}
