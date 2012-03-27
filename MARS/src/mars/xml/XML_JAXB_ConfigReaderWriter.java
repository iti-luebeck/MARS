/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.xml;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import mars.KeyConfig;
import mars.MARS_Settings;
import mars.PhysicalEnvironment;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.auv.BasicAUV;
import mars.simobjects.SimObject;
import mars.simobjects.SimObjectManager;

/**
 * This Class is responsible for reading and writing java objects to an config file
 * as an xml file
 * @author Thomas Tosik
 */
public class XML_JAXB_ConfigReaderWriter {
    /**
     * 
     * @return
     */
    public static ArrayList loadSimObjects(){
        ArrayList arrlist = new ArrayList();
        FilenameFilter filter = new FilenameFilter() {
                    public boolean accept(File dir, String s) {
                        return s.toLowerCase().endsWith( ".xml" );
                    }           
        };
        File dir = new File("./config/default/simobjects");
        
        if(dir.isDirectory()){
            File[] files = dir.listFiles(filter);
            for (int i=0; i<files.length; i++) {
                //Get filename of file or directory
                System.out.println(files[i].getName());
                arrlist.add(loadSimObject(files[i]));
            }
            return arrlist;
        }else{
            return arrlist;
        }
    }
    
    /**
     * 
     * @param file
     * @return
     */
    public static SimObject loadSimObject(File file){
        try {
            if(file.exists()){
                JAXBContext context = JAXBContext.newInstance( SimObject.class );
                Unmarshaller u = context.createUnmarshaller();
                //u.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
                UnmarshallListener ll = new UnmarshallListener();
                u.setListener(ll);
                SimObject simob = (SimObject)u.unmarshal( file );
                //System.out.println(simob.getName());
                return simob;
            }else{
                return null;
            }
        } catch (JAXBException ex) {
            Logger.getLogger(XML_JAXB_ConfigReaderWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * 
     * @param name
     * @return
     */
    public static SimObject loadSimObject(String name){
        try {
            File file = new File("./config/default/simobjects/" + name + ".xml");
            if(file.exists()){
                JAXBContext context = JAXBContext.newInstance( SimObject.class );
                Unmarshaller u = context.createUnmarshaller();
                //u.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
                UnmarshallListener ll = new UnmarshallListener();
                u.setListener(ll);
                SimObject simob = (SimObject)u.unmarshal( file );
                //System.out.println(simob.getName());
                return simob;
            }else{
                return null;
            }
        } catch (JAXBException ex) {
            Logger.getLogger(XML_JAXB_ConfigReaderWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * 
     * @param simob
     */
    public static void saveSimObject(SimObject simob, File file){
        try {
            JAXBContext context = JAXBContext.newInstance( SimObject.class );
            Marshaller m = context.createMarshaller();
            m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            File simfile = new File(file,simob.getName() + ".xml" );
            m.marshal( simob, simfile );
        } catch (JAXBException ex) {
            Logger.getLogger(XML_JAXB_ConfigReaderWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * 
     * @param simobs
     */
    public static void saveSimObjects(ArrayList simobs, File file){
        Iterator iter = simobs.iterator();
        while(iter.hasNext() ) {
            SimObject simob = (SimObject)iter.next();
            saveSimObject(simob, file);
        }
    }
    
    public static void saveSimObjects(HashMap<String,SimObject> simobs, File file){
        for ( String elem : simobs.keySet() ){
            SimObject simob = (SimObject)simobs.get(elem);
            saveSimObject(simob, file);    
        }
    }
    
    /**
     * 
     * @return
     */
    public static ArrayList loadAUVs(){
        ArrayList arrlist = new ArrayList();
        FilenameFilter filter = new FilenameFilter() {
                    public boolean accept(File dir, String s) {
                        return s.toLowerCase().endsWith( ".xml" );
                    }           
        };
        File dir = new File("./config/default/auvs");
        
        if(dir.isDirectory()){
            File[] files = dir.listFiles(filter);
            for (int i=0; i<files.length; i++) {
                //Get filename of file or directory
                System.out.println(files[i].getName());
                arrlist.add(loadAUV(files[i]));
            }
            return arrlist;
        }else{
            return arrlist;
        }
    }
    
    /**
     * 
     * @param file
     * @return
     */
    public static BasicAUV loadAUV(File file){
        try {
            if(file.exists()){
                JAXBContext context = JAXBContext.newInstance( BasicAUV.class );
                Unmarshaller u = context.createUnmarshaller();
                UnmarshallListener ll = new UnmarshallListener();
                u.setListener(ll);
                //u.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
                BasicAUV auv = (BasicAUV)u.unmarshal( file );
                //System.out.println(simob.getName());
                return auv;
            }else{
                return null;
            }
        } catch (JAXBException ex) {
            Logger.getLogger(XML_JAXB_ConfigReaderWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * 
     * @param name
     * @return
     */
    public static BasicAUV loadAUV(String name){
        try {
            File file = new File("./config/default/auvs/" + name + ".xml");
            if(file.exists()){
                JAXBContext context = JAXBContext.newInstance( BasicAUV.class );
                Unmarshaller u = context.createUnmarshaller();
                UnmarshallListener ll = new UnmarshallListener();
                u.setListener(ll);
                //u.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
                BasicAUV auv = (BasicAUV)u.unmarshal( file );
                //System.out.println(simob.getName());
                return auv;
            }else{
                return null;
            }
        } catch (JAXBException ex) {
            Logger.getLogger(XML_JAXB_ConfigReaderWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * 
     * @param auv
     */
    public static void saveAUV(BasicAUV auv, File file){
        try {
            JAXBContext context = JAXBContext.newInstance( BasicAUV.class );
            Marshaller m = context.createMarshaller();
            m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            //File file = new File( "./config/default/auvs/" + auv.getName() + ".xml" );
            File auvfile = new File(file,auv.getName() + ".xml" );
            m.marshal( auv, auvfile );
        } catch (JAXBException ex) {
            Logger.getLogger(XML_JAXB_ConfigReaderWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * 
     * @param auvs
     */
    public static void saveAUVs(ArrayList auvs, File file){
        Iterator iter = auvs.iterator();
        while(iter.hasNext() ) {
            BasicAUV auv = (BasicAUV)iter.next();
            saveAUV(auv, file);
        }
    }
    
    public static void saveAUVs(HashMap<String,AUV> auvs, File file){
        for ( String elem : auvs.keySet() ){
            if(auvs.get(elem) instanceof BasicAUV){
                BasicAUV auv = (BasicAUV)auvs.get(elem);
                saveAUV(auv, file);    
            }
        }
    }
    
    /**
     * 
     * @return
     */
    public static MARS_Settings loadMARS_Settings(){
        try {
            File file = new File("./config/default/" + "Settings" + ".xml");
            if(file.exists()){
                JAXBContext context = JAXBContext.newInstance( MARS_Settings.class );
                Unmarshaller u = context.createUnmarshaller();
                MARS_Settings mars_settings = (MARS_Settings)u.unmarshal( file );
                //System.out.println(simob.getName());
                return mars_settings;
            }else{
                return null;
            }
        } catch (JAXBException ex) {
            Logger.getLogger(XML_JAXB_ConfigReaderWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * 
     * @param mars_settings
     */
    public static void saveMARS_Settings(MARS_Settings mars_settings,File file){
        try {
            JAXBContext context = JAXBContext.newInstance( MARS_Settings.class );
            Marshaller m = context.createMarshaller();
            m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            //File file = new File( "./config/default/" + "Settings" + ".xml" );
            m.marshal( mars_settings, file );
        } catch (JAXBException ex) {
            Logger.getLogger(XML_JAXB_ConfigReaderWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void saveConfiguration(File file, MARS_Settings mars_settings, AUV_Manager auvManager, SimObjectManager simObjectManager, KeyConfig keys, PhysicalEnvironment penv){
        //create dirs
        file.mkdir();
        //auv dir
        File auvFile = new File(file, "auvs");
        auvFile.mkdir();    
        //simob dir
        File simobFile = new File(file, "simobjects");
        simobFile.mkdir();

        File settingsFile = new File(file, "Settings.xml");
        saveMARS_Settings(mars_settings, settingsFile);

        File penvFile = new File(file, "PhysicalEnvironment.xml");
        savePhysicalEnvironment(penv, penvFile);
        
        File keysFile = new File(file, "KeyConfig.xml");
        saveKeyConfig(keys, keysFile);
        
        saveSimObjects(simObjectManager.getSimObjects(), simobFile);
        
        saveAUVs(auvManager.getAUVs(),auvFile);
    }
    
        /**
     * 
     * @return
     */
    public static PhysicalEnvironment loadPhysicalEnvironment(){
        try {
            File file = new File("./config/default/" + "PhysicalEnvironment" + ".xml");
            if(file.exists()){
                JAXBContext context = JAXBContext.newInstance( PhysicalEnvironment.class );
                Unmarshaller u = context.createUnmarshaller();
                UnmarshallListener ll = new UnmarshallListener();
                u.setListener(ll);
                PhysicalEnvironment pe = (PhysicalEnvironment)u.unmarshal( file );
                //System.out.println(simob.getName());
                return pe;
            }else{
                return null;
            }
        } catch (JAXBException ex) {
            Logger.getLogger(XML_JAXB_ConfigReaderWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * 
     * @param pe 
     */
    public static void savePhysicalEnvironment(PhysicalEnvironment pe, File file){
        try {
            JAXBContext context = JAXBContext.newInstance( PhysicalEnvironment.class );
            Marshaller m = context.createMarshaller();
            m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            //File file = new File( "./config/default/" + "PhysicalEnvironment" + ".xml" );
            m.marshal( pe, file );
        } catch (JAXBException ex) {
            Logger.getLogger(XML_JAXB_ConfigReaderWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
        /**
     * 
     * @return
     */
    public static KeyConfig loadKeyConfig(){
        try {
            File file = new File("./config/default/" + "KeyConfig" + ".xml");
            if(file.exists()){
                JAXBContext context = JAXBContext.newInstance( KeyConfig.class );
                Unmarshaller u = context.createUnmarshaller();
                UnmarshallListener ll = new UnmarshallListener();
                u.setListener(ll);
                KeyConfig keyconfig = (KeyConfig)u.unmarshal( file );
                //System.out.println(simob.getName());
                return keyconfig;
            }else{
                return null;
            }
        } catch (JAXBException ex) {
            Logger.getLogger(XML_JAXB_ConfigReaderWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * 
     * @param mars_settings
     */
    public static void saveKeyConfig(KeyConfig keyconfig, File file){
        try {
            JAXBContext context = JAXBContext.newInstance( KeyConfig.class );
            Marshaller m = context.createMarshaller();
            m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            //File file = new File( "./config/default/" + "KeyConfig" + ".xml" );
            m.marshal( keyconfig, file );
        } catch (JAXBException ex) {
            Logger.getLogger(XML_JAXB_ConfigReaderWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
