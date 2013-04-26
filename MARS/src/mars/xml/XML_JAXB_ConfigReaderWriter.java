/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.xml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
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
import mars.recorder.Recording;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.auv.BasicAUV;
import mars.simobjects.SimObject;
import mars.simobjects.SimObjectManager;

/**
 * This Class is responsible for reading and writing java objects to an configName file
 * as an xml file
 * @author Thomas Tosik
 */
public class XML_JAXB_ConfigReaderWriter {

    private String configName = "default";
    
    public XML_JAXB_ConfigReaderWriter() {
    }
    
    public XML_JAXB_ConfigReaderWriter(String config) {
        this.configName = config;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getConfigName() {
        return configName;
    }
    
    /**
     * 
     * @return
     */
    public ArrayList loadSimObjects(){
        ArrayList arrlist = new ArrayList();
        FilenameFilter filter = new FilenameFilter() {
                    public boolean accept(File dir, String s) {
                        return s.toLowerCase().endsWith( ".xml" );
                    }           
        };
        File dir = new File("./config/" + getConfigName() + "/simobjects");
        
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
    public SimObject loadSimObject(File file){
        try {
            if(file.exists()){
                JAXBContext context = JAXBContext.newInstance( SimObject.class );
                Unmarshaller u = context.createUnmarshaller();
                UnmarshallListener ll = new UnmarshallListener();
                u.setListener(ll);
                SimObject simob = (SimObject)u.unmarshal( file );
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
    public SimObject loadSimObject(String name){
        try {
            File file = new File("./config/" + getConfigName() + "/simobjects/" + name + ".xml");
            if(file.exists()){
                JAXBContext context = JAXBContext.newInstance( SimObject.class );
                Unmarshaller u = context.createUnmarshaller();
                UnmarshallListener ll = new UnmarshallListener();
                u.setListener(ll);
                SimObject simob = (SimObject)u.unmarshal( file );
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
            simfile.setWritable(true);
            Path toPath = simfile.toPath();
            try {
                BufferedWriter newBufferedWriter = Files.newBufferedWriter(toPath, Charset.defaultCharset());
                m.marshal( simob, newBufferedWriter );
                newBufferedWriter.flush();
            } catch (IOException ex) {
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
    public ArrayList loadAUVs(){
        ArrayList arrlist = new ArrayList();
        FilenameFilter filter = new FilenameFilter() {
                    public boolean accept(File dir, String s) {
                        return s.toLowerCase().endsWith( ".xml" );
                    }           
        };
        File dir = new File("./config/" + getConfigName() + "/auvs");
        
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
    public BasicAUV loadAUV(File file){
        try {
            if(file.exists()){
                JAXBContext context = JAXBContext.newInstance( BasicAUV.class );
                Unmarshaller u = context.createUnmarshaller();
                UnmarshallListener ll = new UnmarshallListener();
                u.setListener(ll);
                BasicAUV auv = (BasicAUV)u.unmarshal( file );
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
    public BasicAUV loadAUV(String name){
        try {
            File file = new File("./config/" + getConfigName() + "/auvs/" + name + ".xml");
            if(file.exists()){
                JAXBContext context = JAXBContext.newInstance( BasicAUV.class );
                Unmarshaller u = context.createUnmarshaller();
                UnmarshallListener ll = new UnmarshallListener();
                u.setListener(ll);
                BasicAUV auv = (BasicAUV)u.unmarshal( file );
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
            File auvfile = new File(file,auv.getName() + ".xml" );
            auvfile.setWritable(true);
            Path toPath = auvfile.toPath();
            try {
                BufferedWriter newBufferedWriter = Files.newBufferedWriter(toPath, Charset.defaultCharset());
                m.marshal( auv, newBufferedWriter );
                newBufferedWriter.flush();
            } catch (IOException ex) {
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
    public MARS_Settings loadMARS_Settings(){
        try {
            File file = new File("./config/" + getConfigName() + "/" + "Settings" + ".xml");
            if(file.exists()){
                JAXBContext context = JAXBContext.newInstance( MARS_Settings.class );
                Unmarshaller u = context.createUnmarshaller();
                UnmarshallListener ll = new UnmarshallListener();
                u.setListener(ll);
                MARS_Settings mars_settings = (MARS_Settings)u.unmarshal( file );
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
    public static String saveMARS_Settings(MARS_Settings mars_settings,File file){
        try {
            JAXBContext context = JAXBContext.newInstance( MARS_Settings.class );
            Marshaller m = context.createMarshaller();
            m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            file.setWritable(true);
            Path toPath = file.toPath();
            try {
                BufferedWriter newBufferedWriter = Files.newBufferedWriter(toPath, Charset.defaultCharset());
                m.marshal( mars_settings, newBufferedWriter );
                newBufferedWriter.flush();
            } catch (IOException ex) {
                return "Can't write File: " + file.getAbsolutePath() + " . No Write Access";
            }
        } catch (JAXBException ex) {
            Logger.getLogger(XML_JAXB_ConfigReaderWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
        Path toPath = file.toPath();
        if(!Files.exists(toPath)){
            try {
                Files.createDirectory(toPath);
            } catch (IOException ex) {
                return "Can't create Directory: " + file.getAbsolutePath();
            } catch (SecurityException ex){
                return "Can't create Directory: " + file.getAbsolutePath() + " . No Write Access";
            }
        }else if(!Files.isWritable(toPath)){
            return "Can't create Directory: " + file.getAbsolutePath() + " . No Write Access";
        }
        
        //auv dir
        File auvFile = new File(file, "auvs");
        Path auvPath = auvFile.toPath();
        if(!Files.exists(auvPath)){
            try {
                Files.createDirectory(auvPath);
            } catch (IOException ex) {
                return "Can't create Directory: " + auvFile.getAbsolutePath();
            } catch (SecurityException ex){
                return "Can't create Directory: " + auvFile.getAbsolutePath() + " . No Write Access";
            }
        }else if(!Files.isWritable(auvPath)){
            return "Can't create Directory: " + auvFile.getAbsolutePath() + " . No Write Access";
        }
        
        //cleanup auv directory
        try {
            EnumSet<FileVisitOption> opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
            Files.walkFileTree(auvPath, opts, 1, new XMLFileWalker());//ignore anything deeper
        } catch (IOException ex) {
            Logger.getLogger(XML_JAXB_ConfigReaderWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
 
        //simob dir
        File simobFile = new File(file, "simobjects");
        Path simobPath = simobFile.toPath();
        if(!Files.exists(simobPath)){
            try {
                Files.createDirectory(simobPath);
            } catch (IOException ex) {
                return "Can't create Directory: " + simobFile.getAbsolutePath();
            } catch (SecurityException ex){
                return "Can't create Directory: " + simobFile.getAbsolutePath() + " . No Write Access";
            }
        }else if(!Files.isWritable(auvPath)){
            return "Can't create Directory: " + simobFile.getAbsolutePath() + " . No Write Access";
        }
        
        File settingsFile = new File(file, "Settings.xml");
        String failure = saveMARS_Settings(mars_settings, settingsFile);
        if(failure != null){
            return failure;
        }

        File penvFile = new File(file, "PhysicalEnvironment.xml");
        penvFile.setWritable(true);
        failure = savePhysicalEnvironment(penv, penvFile);
        if(failure != null){
            return failure;
        }
        
        File keysFile = new File(file, "KeyConfig.xml");
        keysFile.setWritable(true);
        failure = saveKeyConfig(keys, keysFile);
        if(failure != null){
            return failure;
        }
                
        failure = saveSimObjects(simObjectManager.getSimObjects(), simobFile);
        if(failure != null){
            return failure;
        }
        
        //clear the folder first
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
    public PhysicalEnvironment loadPhysicalEnvironment(){
        try {
            File file = new File("./config/" + getConfigName() + "/" + "PhysicalEnvironment" + ".xml");
            if(file.exists()){
                JAXBContext context = JAXBContext.newInstance( PhysicalEnvironment.class );
                Unmarshaller u = context.createUnmarshaller();
                UnmarshallListener ll = new UnmarshallListener();
                u.setListener(ll);
                PhysicalEnvironment pe = (PhysicalEnvironment)u.unmarshal( file );
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
    public static String savePhysicalEnvironment(PhysicalEnvironment pe, File file){
        try {
            JAXBContext context = JAXBContext.newInstance( PhysicalEnvironment.class );
            Marshaller m = context.createMarshaller();
            m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            file.setWritable(true);
            Path toPath = file.toPath();
            try {
                BufferedWriter newBufferedWriter = Files.newBufferedWriter(toPath, Charset.defaultCharset());
                m.marshal( pe, newBufferedWriter );
                newBufferedWriter.flush();
            } catch (IOException ex) {
                return "Can't write File: " + file.getAbsolutePath() + " . No Write Access";
            }
        } catch (JAXBException ex) {
            Logger.getLogger(XML_JAXB_ConfigReaderWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
        /**
     * 
     * @return
     */
    public KeyConfig loadKeyConfig(){
        try {
            File file = new File("./config/" + getConfigName() + "/" + "KeyConfig" + ".xml");
            if(file.exists()){
                JAXBContext context = JAXBContext.newInstance( KeyConfig.class );
                Unmarshaller u = context.createUnmarshaller();
                UnmarshallListener ll = new UnmarshallListener();
                u.setListener(ll);
                KeyConfig keyconfig = (KeyConfig)u.unmarshal( file );
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
    public static String saveKeyConfig(KeyConfig keyconfig, File file){
        try {
            JAXBContext context = JAXBContext.newInstance( KeyConfig.class );
            Marshaller m = context.createMarshaller();
            m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            file.setWritable(true);
            Path toPath = file.toPath();
            try {
                BufferedWriter newBufferedWriter = Files.newBufferedWriter(toPath, Charset.defaultCharset());
                m.marshal( keyconfig, newBufferedWriter );
                newBufferedWriter.flush();
            } catch (IOException ex) {
                return "Can't write File: " + file.getAbsolutePath() + " . No Write Access";
            }
        } catch (JAXBException ex) {
            Logger.getLogger(XML_JAXB_ConfigReaderWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public String saveRecording(Recording rec, File file){
        try {
            JAXBContext context = JAXBContext.newInstance( Recording.class );
            Marshaller m = context.createMarshaller();
            m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            File recfile = new File("./config/" + getConfigName() + "/recording/" + "recorder" + ".xml");
            //File recfile = new File(file,"./config/" + getConfigName() + "/recording/" + "recorder" + ".xml");
            recfile.setWritable(true);
            Path toPath = recfile.toPath();
            try {
                BufferedWriter newBufferedWriter = Files.newBufferedWriter(toPath, Charset.defaultCharset());
                m.marshal( rec, newBufferedWriter );
                newBufferedWriter.flush();
            } catch (IOException ex) {
                return "Can't write File: " + recfile.getAbsolutePath() + " . No Write Access";
            }
        } catch (JAXBException ex) {
            Logger.getLogger(XML_JAXB_ConfigReaderWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public Recording loadRecording(File file){
        try {
            File file2 = new File("./config/" + getConfigName() + "/recording/" + "recorder" + ".xml");
            if(file2.exists()){
                JAXBContext context = JAXBContext.newInstance( Recording.class );
                Unmarshaller u = context.createUnmarshaller();
                UnmarshallListener ll = new UnmarshallListener();
                u.setListener(ll);
                Recording recorder = (Recording)u.unmarshal( file2 );
                return recorder;
            }else{
                return null;
            }
        } catch (JAXBException ex) {
            Logger.getLogger(XML_JAXB_ConfigReaderWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
