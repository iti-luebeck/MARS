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
     * @param file
     * @return  
     */
    public static String saveSimObject(SimObject simob, File file){
        try {
            JAXBContext context = JAXBContext.newInstance( SimObject.class );
            Marshaller m = context.createMarshaller();
            m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            File simfile = new File(file,simob.getName() + ".xml" );
            if(simfile.canWrite()){
                m.marshal( simob, simfile );
            }else{
                return "Can't write File: " + simfile.getAbsolutePath() + " . No Write Access";
            }
        } catch (JAXBException ex) {
            Logger.getLogger(XML_JAXB_ConfigReaderWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * 
     * @param simobs
     * @param file 
     * @return  
     */
    public static String saveSimObjects(ArrayList simobs, File file){
        Iterator iter = simobs.iterator();
        while(iter.hasNext() ) {
            SimObject simob = (SimObject)iter.next();
            String failure = saveSimObject(simob, file);
            if(failure != null){
                return failure;
            }
        }
        return null;
    }
    
    /**
     * 
     * @param simobs
     * @param file
     * @return
     */
    public static String saveSimObjects(HashMap<String,SimObject> simobs, File file){
        for ( String elem : simobs.keySet() ){
            SimObject simob = (SimObject)simobs.get(elem);
            String failure = saveSimObject(simob, file);    
            if(failure != null){
                return failure;
            }
        }
        return null;
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
     * @param file
     * @return  
     */
    public static String saveAUV(BasicAUV auv, File file){
        try {
            JAXBContext context = JAXBContext.newInstance( BasicAUV.class );
            Marshaller m = context.createMarshaller();
            m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            //File file = new File( "./config/default/auvs/" + auv.getName() + ".xml" );
            File auvfile = new File(file,auv.getName() + ".xml" );
            if(auvfile.canWrite()){
                m.marshal( auv, auvfile );
            }else{
                return "Can't write File: " + auvfile.getAbsolutePath() + " . No Write Access";
            }
        } catch (JAXBException ex) {
            Logger.getLogger(XML_JAXB_ConfigReaderWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * 
     * @param auvs
     * @param file 
     * @return  
     */
    public static String saveAUVs(ArrayList auvs, File file){
        Iterator iter = auvs.iterator();
        while(iter.hasNext() ) {
            BasicAUV auv = (BasicAUV)iter.next();
            String failure = saveAUV(auv, file);
            if(failure != null){
                return failure;
            }
        }
        return null;
    }
    
    /**
     * 
     * @param auvs
     * @param file
     * @return
     */
    public static String saveAUVs(HashMap<String,AUV> auvs, File file){
        for ( String elem : auvs.keySet() ){
            if(auvs.get(elem) instanceof BasicAUV){
                BasicAUV auv = (BasicAUV)auvs.get(elem);
                String failure =  saveAUV(auv, file); 
                if(failure != null){
                    return failure;
                }
            }
        }
        return null;
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
                UnmarshallListener ll = new UnmarshallListener();
                u.setListener(ll);
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
     * @param file  
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
    
    /**
     * 
     * @param file
     * @param mars_settings
     * @param auvManager
     * @param simObjectManager
     * @param keys
     * @param penv
     * @return
     */
    public static String saveConfiguration(File file, MARS_Settings mars_settings, AUV_Manager auvManager, SimObjectManager simObjectManager, KeyConfig keys, PhysicalEnvironment penv){
        //create dirs
        if(file.canWrite()){
            file.mkdir();
        }else{
            return "Can't create Directory: " + file.getAbsolutePath() + " . No Write Access";
        }
        //auv dir
        File auvFile = new File(file, "auvs");
        if(auvFile.canWrite()){
            auvFile.mkdir(); 
        }else{
            return "Can't create Directory: " + auvFile.getAbsolutePath() + " . No Write Access";
        }
 
        //simob dir
        File simobFile = new File(file, "simobjects");
        if(simobFile.canWrite()){
            simobFile.mkdir(); 
        }else{
            return "Can't create Directory: " + simobFile.getAbsolutePath() + " . No Write Access";
        }

        File settingsFile = new File(file, "Settings.xml");
        if(settingsFile.canWrite()){
            saveMARS_Settings(mars_settings, settingsFile);
        }else{
            return "Can't write File: " + settingsFile.getAbsolutePath() + " . No Write Access";
        }

        File penvFile = new File(file, "PhysicalEnvironment.xml");
        if(penvFile.canWrite()){
            savePhysicalEnvironment(penv, penvFile);
        }else{
            return "Can't write File: " + penvFile.getAbsolutePath() + " . No Write Access";
        }
        
        File keysFile = new File(file, "KeyConfig.xml");
        if(keysFile.canWrite()){
            saveKeyConfig(keys, keysFile);
        }else{
            return "Can't write File: " + keysFile.getAbsolutePath() + " . No Write Access";
        }
                
        String failure = saveSimObjects(simObjectManager.getSimObjects(), simobFile);
        if(failure != null){
            return failure;
        }
        
        failure = saveAUVs(auvManager.getAUVs(),auvFile);
        if(failure != null){
            return failure;
        }
        
        return null;
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
     * @param file  
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
     * @param keyconfig
     * @param file  
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
