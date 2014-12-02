package mars.water;

import com.jme3.app.state.AbstractAppState;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Mandy-Jane Feldvoß, John Paul Jonte
 * Einfacher FPS-Test zur Ermittelung der durchschnittlichen FPS.
 * Min- und max-FPS werden ebenfalls bestimmt, jedoch nur über jeweiligen TPF-Wert.
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
        if (remainIT > 0f){
            remainIT -= tpf;
            
            // call setup method
            if (remainIT <= 0) {
                Logger.getLogger(FPSState.class.getName()).log(Level.INFO, "Starting FPS Test \"" + title + "\"");
                
                try {
                    if (setup != null) setup.call();
                } catch (Exception ex) {
                    Logger.getLogger(FPSState.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        else if (remainDur > 0f) {
            remainDur -= tpf;
            sumTPF += tpf;
            sumCalls += 1;

            if (tpf < minTPF) minTPF = tpf;
            if (tpf > maxTPF) maxTPF = tpf;
            
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
    public String getText(){
        String ans = "************************************************************************************\n";
        ans += "====================================================================================\n";
        ans += title + "\n";
        ans += System.currentTimeMillis() + " Starting time: " + initTime +"s     Duration: " + duration + "s \n";
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