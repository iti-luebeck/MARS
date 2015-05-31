package mars.uwCommManager.benchmarking;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 *
 * @author jaspe_000
 */
public class TablePrinter extends java.io.PrintWriter {
    
    int lineCounter = 1;

    public TablePrinter(String fileName) throws FileNotFoundException{
       super((OutputStream) new FileOutputStream(fileName, false), true);
    }
    
    public void init(String headline1, String headline2) {
        println(headline1 +";"+headline2);
    }
    
    public void addValue(String value) {
        println(lineCounter++ + ";" + value);
    }
    
    public void addKeyValuePair(String key, String value) {
        println(key+";"+value);
        System.out.println(key+";"+value);
    }
    
    
    
    
    
    
    
}
