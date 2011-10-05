/*
 * MARSApp.java
 */

package mars;

import mars.gui.MARSView;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import com.jme3.system.JmeSystem;
import java.awt.Canvas;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import mars.xml.XMLConfigReaderWriter;

/**
 * The main class of the application.
 * @author Thomas Tosik
 */
public class MARSApp extends SingleFrameApplication {

    private static MARS_Main app;//com.jme3.app.Application app;
    private static JmeCanvasContext context;
    private static Canvas canvas;
    private static MARSView view;
    private static JFrame frame;
    private static MARS_Main simpleApp;
    private static int resolution_height = 480;
    private static int resolution_width = 640;
    private static int framelimit = 60;

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        view = new MARSView(this);
        show(view);
        frame = view.getFrame();
        assignIcon();
        String appClass = "mars.MARS_Main";
        createCanvas(appClass);
        view.addCanvas(canvas);
        frame.pack();
        startApp();
    }
    
    private void assignIcon() {
        Image img = Toolkit.getDefaultToolkit().getImage("./Assets/Images/mars_logo_12c.png");
        frame.setIconImage(img);
    } 

    /**
     * 
     * @param appClass
     */
    public static void createCanvas(String appClass){
        try {
             XMLConfigReaderWriter xmll = new XMLConfigReaderWriter();
             resolution_height = xmll.getResolutionHeight();
             resolution_width = xmll.getResolutionWidth();
             framelimit = xmll.getFrameLimit();
        } catch (Exception ex) {
            Logger.getLogger(MARS_Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        AppSettings settings = new AppSettings(true);
        settings.setWidth(resolution_width);
        settings.setHeight(resolution_height);
        settings.setFrameRate(framelimit);
        view.setCanvasPanel(settings.getWidth(),settings.getHeight());

        //JmeSystem.setLowPermissions(true);
        app = new MARS_Main();

        view.setSimAUV(app);

        app.setPauseOnLostFocus(false);
        app.setSettings(settings);
        app.createCanvas();

        context = (JmeCanvasContext) app.getContext();
        context.setSystemListener(app);
        canvas = context.getCanvas();
        canvas.setSize(settings.getWidth(), settings.getHeight());
    }

    /**
     *
     */
    public static void startApp(){
        app.startCanvas();
        app.enqueue(new Callable<Void>(){
            public Void call(){
                if (app instanceof MARS_Main){
                    simpleApp = (MARS_Main) app;
                    simpleApp.getFlyByCamera().setDragToRotate(true);
                    simpleApp.setView(view);
                }
                return null;
            }
        });
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     * @param root
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of MARSApp
     */
    public static MARSApp getApplication() {
        return Application.getInstance(MARSApp.class);
    }

    /**
     * Main method launching the application.
     * @param args
     */
    public static void main(String[] args) {
        launch(MARSApp.class, args);
    }
}
