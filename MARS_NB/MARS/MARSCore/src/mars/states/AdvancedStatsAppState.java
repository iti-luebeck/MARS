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
import com.jme3.app.StatsAppState;
import com.jme3.app.state.AppStateManager;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.openide.util.Exceptions;

/**
 * An AppState to save the frame stats to a csv file.
 * 
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class AdvancedStatsAppState extends StatsAppState{

    protected Application app;
    protected boolean showFps = true;
    private final ArrayList<Integer> fpsList = new ArrayList<Integer>();
    //Delimiter used in CSV file
    private static final String NEW_LINE_SEPARATOR = "\n";
    //CSV file header
    private static final Object [] FILE_HEADER = {"sec","fps"};
    private boolean statsSaveStart = false;
    private float seconds = 0f;

    public AdvancedStatsAppState() {
        super();
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = app;
    }

    
    @Override
    public void update(float tpf) {
        if (showFps) {
            secondCounter += app.getTimer().getTimePerFrame();
            frameCounter ++;
            if (secondCounter >= 1.0f) {
                int fps = (int) (frameCounter / secondCounter);
                if(statsSaveStart){
                    seconds += secondCounter;
                    fpsList.add(fps);
                    if(seconds >= 121f){
                        saveFPSToCSV();
                        seconds = 0f;
                        statsSaveStart = false;
                    }
                }
                fpsText.setText("Frames per second: " + fps);
                secondCounter = 0.0f;
                frameCounter = 0;
            }          
        }
    }

    public void setstatsSaveStart(boolean statsSaveStart) {
        this.statsSaveStart = statsSaveStart;
    }
 
    public void saveFPSToCSV(){
        int sec = 1;
        FileWriter fileWriter = null;
        try {
            CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
            fileWriter = new FileWriter("fpstest");
            //initialize CSVPrinter object
            CSVPrinter csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
            //Create CSV file header
            csvFilePrinter.printRecord(FILE_HEADER);
            for (Integer integer : fpsList) {
                List fpsvalue = new ArrayList();
                fpsvalue.add(sec++);
                fpsvalue.add(integer);
                csvFilePrinter.printRecord(fpsvalue);
            }
            fpsList.clear();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
    
}
