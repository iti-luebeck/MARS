/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
    
    public FPSTest(float initTime, float duration, String fileName){
        this.initTime = initTime;
        remainIT = initTime;
        this.duration = duration;
        remainDur = duration;
        this.fileName = fileName;
    }
    
    public void next(float tpf){
        if(remainIT > 0f){
            remainIT -= tpf;
        }else{
            if(remainDur > 0f){
                remainDur -= tpf;
                sumTPF += tpf;
                sumCalls += 1;
                if(tpf < minTPF){
                    minTPF = tpf;
                }
                if(tpf > maxTPF){
                    maxTPF = tpf;
                }
                
                if(remainDur <= 0){
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
    
    public String getText(){
        String ans = "\n ************************************************************************************ \n";
        ans += " ==================================================================================== \n";
        ans += " " + System.currentTimeMillis() +" Initial starting time: " + initTime +"s     Test duration: " + duration +"s \n";
        ans += " ==================================================================================== \n";
        ans += " Minimum time per frame(tpf) / maximum frames per second(fps):   " + minTPF + " / " + 1/minTPF + "\n";
        ans += " Maximum time per frame(tpf) / minimum frames per second(fps):   " + maxTPF + " / " + 1/maxTPF + "\n";
        ans += " Average time per frame(tpf) / average frames per second(fps):   " + sumTPF/sumCalls + " / " + 1/(sumTPF/sumCalls) + "\n";
        ans += " ==================================================================================== \n";
        ans += " ************************************************************************************ \n";
        return ans;
    }
}
