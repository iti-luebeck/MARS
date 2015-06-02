/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
