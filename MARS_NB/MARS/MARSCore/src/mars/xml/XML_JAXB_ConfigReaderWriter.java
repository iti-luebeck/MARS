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
package mars.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import mars.Helper.Helper;
import mars.KeyConfig;
import mars.MARS_Settings;
import mars.PhysicalEnvironment;
import mars.PhysicalExchange.AUVObject;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.accumulators.Accumulator;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.auv.BasicAUV;
import mars.simobjects.SimObject;
import mars.simobjects.SimObjectManager;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Lookup;

/**
 * This Class is responsible for reading and writing java objects to an
 * configName file as an xml file
 *
 * @author Thomas Tosik
 */
public class XML_JAXB_ConfigReaderWriter {

    private String configName = "default";

    /**
     *
     */
    public XML_JAXB_ConfigReaderWriter() {
    }

    /**
     *
     * @param config
     */
    public XML_JAXB_ConfigReaderWriter(String config) {
        this.configName = config;
    }

    /**
     *
     * @param configName
     */
    public void setConfigName(String configName) {
        this.configName = configName;
    }

    /**
     *
     * @return
     */
    public String getConfigName() {
        return configName;
    }

    /**
     *
     * @return
     */
    public ArrayList<SimObject> loadSimObjects() {
        ArrayList<SimObject> arrlist = new ArrayList<SimObject>();
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String s) {
                return s.toLowerCase().endsWith(".xml");
            }
        };
        File dir = InstalledFileLocator.getDefault().locate("config/" + getConfigName() + "/" + "simobjects", "mars.core", false);
        if (dir.isDirectory()) {
            File[] files = dir.listFiles(filter);
            for (int i = 0; i < files.length; i++) {
                //Get filename of file or directory
                //System.out.println(files[i].getName());
                arrlist.add(loadSimObject(files[i]));
            }
            return arrlist;
        } else {
            return arrlist;
        }
    }

    /**
     *
     * @param file
     * @return
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    public SimObject loadSimObject(File file) {
        try {
            if (file.exists()) {
                //we have to find new classes from modules/plugins(NBP) and add to them to the jaxbcontext so they can be marshalled
                Lookup bag = Lookup.getDefault();
                // the bag of objects
                // A query that looks up instances extending "MyClass"...
                Lookup.Template<SimObject> pattern = new Lookup.Template(SimObject.class);
                // The result of the query
                Lookup.Result<SimObject> result = bag.lookup(pattern);
                Set<Class<? extends SimObject>> allClasses = result.allClasses();
                Class[] toArray = allClasses.toArray(new Class[0]);
                Class[] append = Helper.prepend(toArray, SimObject.class);

                JAXBContext context = JAXBContext.newInstance(append);
                Unmarshaller u = context.createUnmarshaller();
                UnmarshallListener ll = new UnmarshallListener();
                u.setListener(ll);
                SimObject simob = (SimObject) u.unmarshal(file);
                return simob;
            } else {
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
     @SuppressWarnings({"unchecked","rawtypes"})
    public SimObject loadSimObject(String name) {
        try {
            File file = InstalledFileLocator.getDefault().locate("config/" + getConfigName() + "/simobjects/" + name + ".xml", "mars.core", false);
            if (file.exists()) {
                //we have to find new classes from modules/plugins(NBP) and add to them to the jaxbcontext so they can be marshalled
                Lookup bag = Lookup.getDefault();
                // the bag of objects
                // A query that looks up instances extending "MyClass"...
                Lookup.Template<SimObject> pattern = new Lookup.Template(SimObject.class);
                // The result of the query
                Lookup.Result<SimObject> result = bag.lookup(pattern);
                Set<Class<? extends SimObject>> allClasses = result.allClasses();
                Class[] toArray = allClasses.toArray(new Class[0]);
                Class[] append = Helper.prepend(toArray, SimObject.class);

                JAXBContext context = JAXBContext.newInstance(append);
                Unmarshaller u = context.createUnmarshaller();
                UnmarshallListener ll = new UnmarshallListener();
                u.setListener(ll);
                SimObject simob = (SimObject) u.unmarshal(file);
                return simob;
            } else {
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
    public static String saveSimObject(SimObject simob, File file) {
        try {
            JAXBContext context = JAXBContext.newInstance(SimObject.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            File simfile = new File(file, simob.getName() + ".xml");
            simfile.setWritable(true);
            Path toPath = simfile.toPath();
            try {
                BufferedWriter newBufferedWriter = Files.newBufferedWriter(toPath, StandardCharsets.UTF_8);
                m.marshal(simob, newBufferedWriter);
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
    public static String saveSimObjects(ArrayList<SimObject> simobs, File file) {
        Iterator<SimObject> iter = simobs.iterator();
        while (iter.hasNext()) {
            SimObject simob = iter.next();
            String failure = saveSimObject(simob, file);
            if (failure != null) {
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
    public static String saveSimObjects(HashMap<String, SimObject> simobs, File file) {
        for (String elem : simobs.keySet()) {
            SimObject simob = simobs.get(elem);
            String failure = saveSimObject(simob, file);
            if (failure != null) {
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
    public static String clearSimObjects(HashMap<String, SimObject> simobs, File file) {
        File[] listFiles = file.listFiles();
        for (File oldFile : listFiles) {
            String name = oldFile.getName().substring(0, oldFile.getName().lastIndexOf("."));
            boolean containsKey = simobs.containsKey(name);
            if (!containsKey) {
                try {
                    Files.deleteIfExists(oldFile.toPath());
                } catch (IOException ex) {
                    return "Can't delete File: " + oldFile.getAbsolutePath();
                }
            }
        }
        return null;
    }

    /**
     *
     * @return
     */
    public ArrayList<AUV> loadAUVs() {
        ArrayList<AUV> arrlist = new ArrayList<AUV>();
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String s) {
                return s.toLowerCase().endsWith(".xml");
            }
        };
        File dir = InstalledFileLocator.getDefault().locate("config/" + getConfigName() + "/auvs", "mars.core", false);
        if (dir.isDirectory()) {
            File[] files = dir.listFiles(filter);
            for (File file : files) {
                //Get filename of file or directory
                //System.out.println(files[i].getName());
                BasicAUV loadAUV = loadAUV(file);
                //if(loadAUV != null){
                    arrlist.add(loadAUV);
                //}
            }
            return arrlist;
        } else {
            return arrlist;
        }
    }

    /**
     *
     * @param file
     * @return
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    public BasicAUV loadAUV(File file) {
        try {
            if (file.exists()) {
                JAXBContext context = JAXBContext.newInstance(getJAXBContextClasses());
                Unmarshaller u = context.createUnmarshaller();
                UnmarshallListener ll = new UnmarshallListener();
                u.setListener(ll);
                BasicAUV auv = (BasicAUV) u.unmarshal(file);
                return auv;
            } else {
                return null;
            }
        } catch (JAXBException ex) {
            Logger.getLogger(XML_JAXB_ConfigReaderWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    @SuppressWarnings({"unchecked","rawtypes"})
    private static Class[] getJAXBContextClasses(){
        //we have to find new classes from modules/plugins(NBP) and add to them to the jaxbcontext so they can be marshalled
        Lookup bag = Lookup.getDefault();
        // the bag of objects
        // A query that looks up instances extending "MyClass"...
        Lookup.Template<BasicAUV> pattern = new Lookup.Template(BasicAUV.class);
        // The result of the query
        Lookup.Result<BasicAUV> result = bag.lookup(pattern);
        Set<Class<? extends BasicAUV>> allClasses = result.allClasses();
        Class[] toArray = allClasses.toArray(new Class[0]);

        Class[] append = Helper.prepend(toArray, BasicAUV.class);

        Lookup.Template<PhysicalExchanger> pattern2 = new Lookup.Template(PhysicalExchanger.class);
        // The result of the query
        Lookup.Result<PhysicalExchanger> result2 = bag.lookup(pattern2);
        Set<Class<? extends PhysicalExchanger>> allClasses2 = result2.allClasses();
        Class[] toArray2 = allClasses2.toArray(new Class[0]);

        Class[] append2 = Helper.prepend(toArray2, PhysicalExchanger.class);
        Class[] append4 = Helper.prepend(append2, Accumulator.class);

        Class[] append3 = Helper.concatClassArrays(append, append4);
        return append3;
    }

    /**
     *
     * @param name
     * @return
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    @Deprecated
    public BasicAUV loadAUV(String name) {
        try {
            File file = InstalledFileLocator.getDefault().locate("config/" + getConfigName() + "/auvs/" + name + ".xml", "mars.core", false);
            if (file.exists()) {
                //we have to find new classes from modules/plugins(NBP) and add to them to the jaxbcontext so they can be marshalled
                Lookup bag = Lookup.getDefault();
                // the bag of objects
                // A query that looks up instances extending "MyClass"...
                Lookup.Template<BasicAUV> pattern = new Lookup.Template(BasicAUV.class);
                // The result of the query
                Lookup.Result<BasicAUV> result = bag.lookup(pattern);
                Set<Class<? extends BasicAUV>> allClasses = result.allClasses();
                Class[] toArray = allClasses.toArray(new Class[0]);
                Class[] append = Helper.prepend(toArray, BasicAUV.class);

                JAXBContext context = JAXBContext.newInstance(append);
                Unmarshaller u = context.createUnmarshaller();
                UnmarshallListener ll = new UnmarshallListener();
                u.setListener(ll);
                BasicAUV auv = (BasicAUV) u.unmarshal(file);
                return auv;
            } else {
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
    public static String saveAUV(BasicAUV auv, File file) {
        try {
            JAXBContext context = JAXBContext.newInstance(getJAXBContextClasses());
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            File auvfile = new File(file, auv.getName() + ".xml");
            auvfile.setWritable(true);
            Path toPath = auvfile.toPath();
            try {
                BufferedWriter newBufferedWriter = Files.newBufferedWriter(toPath, StandardCharsets.UTF_8);
                m.marshal(auv, newBufferedWriter);
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
    public static String saveAUVs(ArrayList<AUV> auvs, File file) {
        Iterator<AUV>  iter = auvs.iterator();
        while (iter.hasNext()) {
            BasicAUV auv = (BasicAUV) iter.next();
            String failure = saveAUV(auv, file);
            if (failure != null) {
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
    public static String saveAUVs(HashMap<String, AUV> auvs, File file) {
        for (String elem : auvs.keySet()) {
            if (auvs.get(elem) instanceof BasicAUV) {
                BasicAUV auv = (BasicAUV) auvs.get(elem);
                String failure = saveAUV(auv, file);
                if (failure != null) {
                    return failure;
                }
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
    public static String clearAUVs(HashMap<String, AUV> auvs, File file) {
        File[] listFiles = file.listFiles();
        for (File oldFile : listFiles) {
            String name = oldFile.getName().substring(0, oldFile.getName().lastIndexOf("."));
            boolean containsKey = auvs.containsKey(name);
            if (!containsKey) {
                try {
                    Files.deleteIfExists(oldFile.toPath());
                } catch (IOException ex) {
                    return "Can't delete File: " + oldFile.getAbsolutePath();
                }
            }
        }
        return null;
    }

    /**
     *
     * @return
     */
    public MARS_Settings loadMARS_Settings() {
        try {
            File file = InstalledFileLocator.getDefault().locate("config/" + getConfigName() + "/" + "Settings" + ".xml", "mars.core", false);
            if (file.exists()) {
                JAXBContext context = JAXBContext.newInstance(MARS_Settings.class);
                Unmarshaller u = context.createUnmarshaller();
                UnmarshallListener ll = new UnmarshallListener();
                u.setListener(ll);
                MARS_Settings mars_settings = (MARS_Settings) u.unmarshal(file);
                return mars_settings;
            } else {
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
     * @return
     */
    public static String saveMARS_Settings(MARS_Settings mars_settings, File file) {
        try {
            JAXBContext context = JAXBContext.newInstance(MARS_Settings.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            file.setWritable(true);
            Path toPath = file.toPath();
            try {
                BufferedWriter newBufferedWriter = Files.newBufferedWriter(toPath, StandardCharsets.UTF_8);
                m.marshal(mars_settings, newBufferedWriter);
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
    public ConfigManager loadConfigManager() {
        try {
            File file = InstalledFileLocator.getDefault().locate("config/" + "Config" + ".xml", "mars.core", false);
            if (file.exists()) {
                JAXBContext context = JAXBContext.newInstance(ConfigManager.class);
                Unmarshaller u = context.createUnmarshaller();
                UnmarshallListener ll = new UnmarshallListener();
                u.setListener(ll);
                ConfigManager config = (ConfigManager) u.unmarshal(file);
                return config;
            } else {
                return null;
            }
        } catch (JAXBException ex) {
            Logger.getLogger(XML_JAXB_ConfigReaderWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     *
     * @param config
     * @param file
     * @return
     */
    public static String saveConfigManager(ConfigManager config, File file) {
        try {
            JAXBContext context = JAXBContext.newInstance(ConfigManager.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            file.setWritable(true);
            Path toPath = file.toPath();
            try {
                BufferedWriter newBufferedWriter = Files.newBufferedWriter(toPath, StandardCharsets.UTF_8);
                m.marshal(config, newBufferedWriter);
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
    public static String saveConfiguration(File file, MARS_Settings mars_settings, AUV_Manager auvManager, SimObjectManager simObjectManager, KeyConfig keys, PhysicalEnvironment penv) {
        //create dirs
        Path toPath = file.toPath();
        if (!Files.exists(toPath)) {
            try {
                Files.createDirectory(toPath);
            } catch (IOException ex) {
                return "Can't create Directory: " + file.getAbsolutePath();
            } catch (SecurityException ex) {
                return "Can't create Directory: " + file.getAbsolutePath() + " . No Write Access";
            }
        } else if (!Files.isWritable(toPath)) {
            return "Can't create Directory: " + file.getAbsolutePath() + " . No Write Access";
        }

        //auv dir
        File auvFile = new File(file, "auvs");
        Path auvPath = auvFile.toPath();
        if (!Files.exists(auvPath)) {
            try {
                Files.createDirectory(auvPath);
            } catch (IOException ex) {
                return "Can't create Directory: " + auvFile.getAbsolutePath();
            } catch (SecurityException ex) {
                return "Can't create Directory: " + auvFile.getAbsolutePath() + " . No Write Access";
            }
        } else if (!Files.isWritable(auvPath)) {
            return "Can't create Directory: " + auvFile.getAbsolutePath() + " . No Write Access";
        }

        //simob dir
        File simobFile = new File(file, "simobjects");
        Path simobPath = simobFile.toPath();
        if (!Files.exists(simobPath)) {
            try {
                Files.createDirectory(simobPath);
            } catch (IOException ex) {
                return "Can't create Directory: " + simobFile.getAbsolutePath();
            } catch (SecurityException ex) {
                return "Can't create Directory: " + simobFile.getAbsolutePath() + " . No Write Access";
            }
        } else if (!Files.isWritable(auvPath)) {
            return "Can't create Directory: " + simobFile.getAbsolutePath() + " . No Write Access";
        }

        File settingsFile = new File(file, "Settings.xml");
        String failure = saveMARS_Settings(mars_settings, settingsFile);
        if (failure != null) {
            return failure;
        }

        File penvFile = new File(file, "PhysicalEnvironment.xml");
        penvFile.setWritable(true);
        failure = savePhysicalEnvironment(penv, penvFile);
        if (failure != null) {
            return failure;
        }

        File keysFile = new File(file, "KeyConfig.xml");
        keysFile.setWritable(true);
        failure = saveKeyConfig(keys, keysFile);
        if (failure != null) {
            return failure;
        }

        //clear the old simobs
        failure = clearSimObjects(simObjectManager.getMARSObjects(), simobFile);
        if (failure != null) {
            return failure;
        }
        failure = saveSimObjects(simObjectManager.getMARSObjects(), simobFile);
        if (failure != null) {
            return failure;
        }

        //clear the old auvs
        failure = clearAUVs(auvManager.getMARSObjects(), auvFile);
        if (failure != null) {
            return failure;
        }
        //save the auvs
        failure = saveAUVs(auvManager.getMARSObjects(), auvFile);
        if (failure != null) {
            return failure;
        }

        return null;
    }

    /**
     *
     * @return
     */
    public PhysicalEnvironment loadPhysicalEnvironment() {
        try {
            File file = InstalledFileLocator.getDefault().locate("config/" + getConfigName() + "/" + "PhysicalEnvironment" + ".xml", "mars.core", false);
            if (file.exists()) {
                JAXBContext context = JAXBContext.newInstance(PhysicalEnvironment.class);
                Unmarshaller u = context.createUnmarshaller();
                UnmarshallListener ll = new UnmarshallListener();
                u.setListener(ll);
                PhysicalEnvironment pe = (PhysicalEnvironment) u.unmarshal(file);
                return pe;
            } else {
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
     * @return
     */
    public static String savePhysicalEnvironment(PhysicalEnvironment pe, File file) {
        try {
            JAXBContext context = JAXBContext.newInstance(PhysicalEnvironment.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            file.setWritable(true);
            Path toPath = file.toPath();
            try {
                BufferedWriter newBufferedWriter = Files.newBufferedWriter(toPath, StandardCharsets.UTF_8);
                m.marshal(pe, newBufferedWriter);
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
    public KeyConfig loadKeyConfig() {
        try {
            File file = InstalledFileLocator.getDefault().locate("config/" + getConfigName() + "/" + "KeyConfig" + ".xml", "mars.core", false);
            if (file.exists()) {
                JAXBContext context = JAXBContext.newInstance(KeyConfig.class);
                Unmarshaller u = context.createUnmarshaller();
                UnmarshallListener ll = new UnmarshallListener();
                u.setListener(ll);
                KeyConfig keyconfig = (KeyConfig) u.unmarshal(file);
                return keyconfig;
            } else {
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
     * @return
     */
    public static String saveKeyConfig(KeyConfig keyconfig, File file) {
        try {
            JAXBContext context = JAXBContext.newInstance(KeyConfig.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            file.setWritable(true);
            Path toPath = file.toPath();
            try {
                BufferedWriter newBufferedWriter = Files.newBufferedWriter(toPath, StandardCharsets.UTF_8);
                m.marshal(keyconfig, newBufferedWriter);
                newBufferedWriter.flush();
            } catch (IOException ex) {
                return "Can't write File: " + file.getAbsolutePath() + " . No Write Access";
            }
        } catch (JAXBException ex) {
            Logger.getLogger(XML_JAXB_ConfigReaderWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
