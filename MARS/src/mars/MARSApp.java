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
import com.jme3.system.awt.AwtPanel;
import com.jme3.system.awt.AwtPanelsContext;
import com.jme3.system.awt.PaintMode;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import mars.xml.XML_JAXB_ConfigReaderWriter;

/**
 * The main class of the application.
 * @author Thomas Tosik
 */
public class MARSApp extends SingleFrameApplication {

    private static MARS_Main app;//com.jme3.app.Application app;
    private static JmeCanvasContext context;
    private static Canvas canvas;
    private static AwtPanel sim_panel, map_panel;
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
        //createCanvas(appClass);
        //view.addCanvas(canvas);
        //view.addAWTMainPanel(sim_panel);
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
            MARS_Settings mars_settings = XML_JAXB_ConfigReaderWriter.loadMARS_Settings();
            mars_settings.init();
            resolution_height = mars_settings.getResolution_Height();
            resolution_width = mars_settings.getResolution_Width();
            framelimit = mars_settings.getFrameLimit();
        } catch (Exception ex) {
            Logger.getLogger(MARS_Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        AppSettings settings = new AppSettings(true);
        settings.setWidth(resolution_width);
        settings.setHeight(resolution_height);
        settings.setFrameRate(framelimit);
        //settings.setCustomRenderer(app);
        settings.setCustomRenderer(AwtPanelsContext.class);
        view.setCanvasPanel(settings.getWidth(),settings.getHeight());

        //JmeSystem.setLowPermissions(true);
        app = new MARS_Main();

        view.setSimAUV(app);

        app.setPauseOnLostFocus(false);
        app.setSettings(settings);
        app.start();
        
        /*Thread t = new Thread(new Runnable() {
 
            @Override
            public void run() {
                //TestPssmShadow2 t = new TestPssmShadow2();//This would be your jME app extending SimpleApplication
                app.start();
            }
        });
        t.start();*/
        
        /*app.createCanvas();

        context = (JmeCanvasContext) app.getContext();
        context.setSystemListener(app);
        canvas = context.getCanvas();
        canvas.setSize(settings.getWidth(), settings.getHeight());*/
       /* SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                final AwtPanelsContext ctx = (AwtPanelsContext) app.getContext();
                sim_panel = ctx.createPanel(PaintMode.Accelerated);
                sim_panel.setPreferredSize(new Dimension(640, 480));
                ctx.setInputSource(sim_panel);
                
                map_panel = ctx.createPanel(PaintMode.Accelerated);
                map_panel.setPreferredSize(new Dimension(256, 256));
                
                view.addAWTMainPanel(sim_panel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });*/
    }

    /**
     *
     */
    public static void startApp(){
        //app.startCanvas();
/*        app.start();
        app.enqueue(new Callable<Void>(){
            public Void call(){
                if (app instanceof MARS_Main){
                    simpleApp = (MARS_Main) app;
                    simpleApp.getFlyByCamera().setDragToRotate(true);
                    simpleApp.setView(view);
                }
                return null;
            }
        });*/
        Thread t = new Thread(new Runnable() {
 
            @Override
            public void run() {
                        try {
                            MARS_Settings mars_settings = XML_JAXB_ConfigReaderWriter.loadMARS_Settings();
                            mars_settings.init();
                            resolution_height = mars_settings.getResolution_Height();
                            resolution_width = mars_settings.getResolution_Width();
                            framelimit = mars_settings.getFrameLimit();
                        } catch (Exception ex) {
                            Logger.getLogger(MARS_Main.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        AppSettings settings = new AppSettings(true);
                        settings.setWidth(resolution_width);
                        settings.setHeight(resolution_height);
                        settings.setFrameRate(framelimit);
                        //settings.setCustomRenderer(app);
                        settings.setCustomRenderer(AwtPanelsContext.class);
                        view.setCanvasPanel(settings.getWidth(),settings.getHeight());

                        //JmeSystem.setLowPermissions(true);
                        app = new MARS_Main();

                        view.setSimAUV(app);

                        app.setPauseOnLostFocus(false);
                        app.setShowSettings(false);
                        app.setSettings(settings);
                        app.setView(view);
                        app.start();
                        
                        /*app.enqueue(new Callable<Void>(){
                        public Void call(){
                                app.getFlyByCamera().setDragToRotate(true);
                                app.test();
                                //sim_panel.attachTo(true, app.getViewPort());
                                //app.setView(view);
                                return null;
                            }
                        });*/
 
                        SwingUtilities.invokeLater(new Runnable(){
                            public void run(){
                                final AwtPanelsContext ctx = (AwtPanelsContext) app.getContext();

                                sim_panel = ctx.createPanel(PaintMode.Accelerated);
                                sim_panel.setPreferredSize(new Dimension(640, 480));
                                sim_panel.setMinimumSize(new Dimension(640, 480));
                                sim_panel.transferFocus();
                                ctx.setInputSource(sim_panel);
                                map_panel = ctx.createPanel(PaintMode.Accelerated);
                                map_panel.setPreferredSize(new Dimension(256, 256));
                                map_panel.setMinimumSize(new Dimension(256, 256));

                                view.addAWTMainPanel(sim_panel);
                                view.addAWTMapPanel(map_panel);
                                app.enqueue(new Callable<Void>(){
                                public Void call(){
                                        app.getViewPort().setClearFlags(true, true, true);
                                        app.getFlyByCamera().setDragToRotate(true);
                                        sim_panel.attachTo(true, app.getViewPort());
                                        map_panel.attachTo(false, app.getMapViewPort());
                                        return null;
                                    }
                                });
                                frame.pack();
                                frame.setLocationRelativeTo(null);
                                frame.setVisible(true);
                                frame.validate();
                                frame.repaint();
                            }
                        });
                }
        });
        t.start();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.pack();
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
