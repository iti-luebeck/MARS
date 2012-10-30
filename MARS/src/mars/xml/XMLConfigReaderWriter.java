/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.xml;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.*;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import mars.PhysicalEnvironment;
import mars.PhysicalExchanger;
import mars.MARS_Settings;
import mars.MARS_Main;
import mars.states.SimState;
import mars.actuators.Actuator;
import mars.actuators.BrushlessThruster;
import mars.simobjects.SimObject;
import mars.actuators.SeaBotixThruster;
import mars.auv.AUV;
import mars.auv.AUV_Parameters;
import mars.auv.BasicAUV;
import mars.auv.example.Hanse;
import mars.auv.example.Monsun2;
import mars.sensors.Accelerometer;
import mars.sensors.Compass;
import mars.sensors.Gyroscope;
import mars.sensors.sonar.ImagenexSonar_852_Echo;
import mars.sensors.sonar.ImagenexSonar_852_Scanning;
import mars.sensors.InfraRedSensor;
import mars.sensors.PingDetector;
import mars.sensors.PressureSensor;
import mars.sensors.Sensor;
import mars.sensors.sonar.Sonar;
import mars.sensors.TemperatureSensor;
import mars.sensors.UnderwaterModem;
import mars.sensors.VideoCamera;

/**
 * With this class we read and parse the congfig xml file. We can extract Objects that SimAUV needs.
 * Like an AUV or Settings.
 * @author Thomas Tosik
 * @deprecated 
 */
@Deprecated
public class XMLConfigReaderWriter
{

    private SimState simstate;
    private MARS_Main mars;
    private Document document;

    /**
     *
     * @param simstate 
     * @throws Exception
     */
    public XMLConfigReaderWriter(SimState simstate) throws Exception{
       //set the logging
       try {
            // Create an appending file handler
            boolean append = true;
            FileHandler handler = new FileHandler(XMLConfigReaderWriter.class.getName() + ".log", append);
            // Add to the desired logger
            Logger logger = Logger.getLogger(XMLConfigReaderWriter.class.getName());
            logger.addHandler(handler);
        } catch (IOException e) { }

        Logger.getLogger(XMLConfigReaderWriter.class.getName()).log(Level.INFO, "Creating XMLConfigReader...", "");

        this.simstate = simstate;
        this.mars = simstate.getMARS();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        document = builder.parse( new File("auv_config.xml") );

        Logger.getLogger(XMLConfigReaderWriter.class.getName()).log(Level.INFO, "Parsing auv_config.xml...", "");
    }

    /**
     *
     * @throws Exception
     */
    public XMLConfigReaderWriter() throws Exception{
       //set the logging
       try {
            // Create an appending file handler
            boolean append = true;
            FileHandler handler = new FileHandler(XMLConfigReaderWriter.class.getName() + ".log", append);
            // Add to the desired logger
            Logger logger = Logger.getLogger(XMLConfigReaderWriter.class.getName());
            logger.addHandler(handler);
        } catch (IOException e) { }

        Logger.getLogger(XMLConfigReaderWriter.class.getName()).log(Level.INFO, "Creating XMLConfigReader...", "");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        document = builder.parse( new File("auv_config.xml") );

        Logger.getLogger(XMLConfigReaderWriter.class.getName()).log(Level.INFO, "Parsing auv_config.xml...", "");
    }

    /**
     * This method writes a DOM document to a file
     * @param doc
     * @param filename
     */
    public static void writeXmlFile(Document doc, String filename) {
        try {
        // Prepare the DOM document for writing
        Source source = new DOMSource(doc);

        // Prepare the output file
        File file = new File(filename);
        Result result = new StreamResult(file);

        // Write the DOM document to the file
        Transformer xformer = TransformerFactory.newInstance().newTransformer();
        xformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
        } catch (TransformerException e) {
        }
    }

    /**
     * This method writes the DOM document to a file
     * @param filename
     */
    public void writeXmlFile(String filename) {
        try {
        // Prepare the DOM document for writing
        Source source = new DOMSource(getDocument());

        // Prepare the output file
        File file = new File(filename);
        Result result = new StreamResult(file);

        // Write the DOM document to the file
        Transformer xformer = TransformerFactory.newInstance().newTransformer();
        xformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
        } catch (TransformerException e) {
        }
    }

        /**
     * This method writes the DOM document to a file
         * @param file
     */
    public void writeXmlFile(File file) {
        try {
        // Prepare the DOM document for writing
        Source source = new DOMSource(getDocument());

        // Prepare the output file
        Result result = new StreamResult(file);

        // Write the DOM document to the file
        Transformer xformer = TransformerFactory.newInstance().newTransformer();
        xformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
        } catch (TransformerException e) {
        }
    }

    private ArrayList getAUVNodes(){
        ArrayList ret = new ArrayList();
        NodeList nl = document.getElementsByTagName("AUV");
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if(node.getNodeName().equals("AUV")){
                ret.add(node);
            }
        }
        return ret;
    }

    private Node getAUVNode(String auv_name){
        NodeList nl = document.getElementsByTagName("AUV");
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if(node.getNodeName().equals("AUV")){
                if(node.getAttributes().item(0).getTextContent().equals(auv_name)){
                    return node;
                }
            }
        }
        return null;
    }

    private Node addVector(Vector3f vector){
        Node vector_node = document.createElement("vector");
        Node vectorx_node = document.createElement("x");
        Node vectory_node = document.createElement("y");
        Node vectorz_node = document.createElement("z");
        vectorx_node.setTextContent(((Float)(vector.getX())).toString());
        vectory_node.setTextContent(((Float)(vector.getY())).toString());
        vectorz_node.setTextContent(((Float)(vector.getZ())).toString());
        vector_node.appendChild(vectorx_node);
        vector_node.appendChild(vectory_node);
        vector_node.appendChild(vectorz_node);
        return vector_node;
    }

    private Node addColor(ColorRGBA color){
        Node color_node = document.createElement("color");
        Vector3f color_vector = new Vector3f(color.getRed()*255f,color.getGreen()*255f,color.getBlue()*255f);
        color_node.appendChild(addVector(color_vector));
        return color_node;
    }

    private Node addHashMap(HashMap<String,Object> hashmap, String hashmap_name){
        Node hashmap_node = document.createElement(hashmap_name);
        for ( String elem : hashmap.keySet() ){
            Object obj = hashmap.get(elem);
            if(obj instanceof HashMap){
                hashmap_node.appendChild(addHashMap((HashMap<String,Object>)obj,elem));
            }else if(obj instanceof Vector3f){
                Node elem_node = document.createElement(elem);
                elem_node.appendChild(addVector((Vector3f)obj));
                hashmap_node.appendChild(elem_node);
            }else if(obj instanceof ColorRGBA){
                hashmap_node.appendChild(addColor((ColorRGBA)obj));
            }else if(obj instanceof Boolean){
                Node elem_node = document.createElement(elem);
                elem_node.setTextContent(((Boolean)obj).toString());
                hashmap_node.appendChild(elem_node);
            }else if(obj instanceof Float){
                Node elem_node = document.createElement(elem);
                elem_node.setTextContent(((Float)obj).toString());
                hashmap_node.appendChild(elem_node);
            }else if(obj instanceof Integer){
                Node elem_node = document.createElement(elem);
                elem_node.setTextContent(((Integer)obj).toString());
                hashmap_node.appendChild(elem_node);
            }else if(obj instanceof String){
                Node elem_node = document.createElement(elem);
                elem_node.setTextContent((String)obj);
                hashmap_node.appendChild(elem_node);
            }
        }
        return hashmap_node;
    }

    private Node addHashMapSensors(HashMap<String,Sensor> hashmap,String hashmap_name){
        Node node = document.createElement(hashmap_name);
        return node;
    }

    private Node addHashMapActuators(HashMap<String,Actuator> hashmap,String hashmap_name){
        Node node = document.createElement(hashmap_name);
        return node;
    }

    /**
     *
     * @param auv
     */
    public void addAUV(AUV auv){
        AUV_Parameters auv_param = auv.getAuv_param();
        HashMap<String,Sensor> sensors = auv.getSensors();
        HashMap<String,Actuator> actuators = auv.getActuators();
        Node rootNode = document.getDocumentElement();

        Node auv_node = document.createElement("AUV");
        NamedNodeMap auv_nodeAttributes = auv_node.getAttributes();
        Attr auv_name = document.createAttribute("name");
        auv_name.setValue(auv_param.getAuv_name());
        auv_nodeAttributes.setNamedItem(auv_name);

        HashMap<String,Object> vars = auv_param.getAllVariables();
        auv_node.appendChild(addHashMap(vars,"AUVParameters"));

        auv_node.appendChild(addHashMapSensors(sensors,"Sensors"));
        
        auv_node.appendChild(addHashMapActuators(actuators,"Actuators"));

        rootNode.appendChild(auv_node);
    }

    /**
     *
     * @param simobj
     */
    public void addSimObject(SimObject simobj){
        Node rootNode = document.getDocumentElement();

        HashMap<String,Object> vars = simobj.getAllVariables();
        Node simobj_node = addHashMap(vars,"Object");
        NamedNodeMap auv_nodeAttributes = simobj_node.getAttributes();
        Attr auv_name = document.createAttribute("name");
        auv_name.setValue(simobj.getName());
        auv_nodeAttributes.setNamedItem(auv_name);

        rootNode.appendChild(simobj_node);
    }

    /**
     *
     * @param auv
     */
    public void deleteAUV(AUV auv){
        deleteAUV(auv.getName());
    }

    /**
     *
     * @param auv_name
     */
    public void deleteAUV(String auv_name){
        Node rootNode = document.getDocumentElement();
        rootNode.removeChild(getAUVNode(auv_name));
    }

    /**
     *
     * @param simob
     */
    public void deleteSimObj(SimObject simob){
        deleteSimObj(simob.getName());
    }

    /**
     *
     * @param simob_name
     */
    public void deleteSimObj(String simob_name){
        Node rootNode = document.getDocumentElement();
        rootNode.removeChild(getSimObjectNode(simob_name));
    }

    private ArrayList getSimObjectNodes(){
        ArrayList ret = new ArrayList();
        NodeList nl = document.getElementsByTagName("Object");
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if(node.getNodeName().equals("Object")){
                ret.add(node);
            }
        }
        return ret;
    }

    private Node getSimObjectNode(String simob_name){
        NodeList nl = document.getElementsByTagName("Object");
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if(node.getNodeName().equals("Object")){
                if(node.getAttributes().item(0).getTextContent().equals(simob_name)){
                    return node;
                }
            }
        }
        return null;
    }

     private Node getAUVParametersNode(Node auv_node){
        NodeList nl = auv_node.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if(node.getNodeName().equals("AUVParameters")){
                return node;
            }
        }
        return null;
    }

    private Node getHashNodesNode(Node auv_node, String hashname){
        NodeList nl = auv_node.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if(node.getNodeName().equals(hashname)){
                return node;
            }
        }
        return null;
    }

    private Node getPhysicalEnvironmentNode(){
        NodeList nl = document.getElementsByTagName("PhysicalEnvironment");
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if(node.getNodeName().equals("PhysicalEnvironment")){
                return node;
            }
        }
        return null;
    }

    /**
     *
     * @param treepath
     * @param pathcount
     * @param node_obj
     */
    public void setPathElement(Object[] treepath, int pathcount, Object node_obj){
        Node node = document.getDocumentElement();
        for (int i = 1; i < pathcount-1; i++) {
            //System.out.println(treepath[i].toString());
            node = getHashNodesNode(node,treepath[i].toString());
        }
        if(node_obj instanceof Float){
            node.setTextContent(node_obj.toString());
        }else if(node_obj instanceof Integer){
            node.setTextContent(node_obj.toString());
        }else if(node_obj instanceof Boolean){
            node.setTextContent(node_obj.toString());
        }else if(node_obj instanceof String){
            node.setTextContent(node_obj.toString());
        }else if(node_obj instanceof Vector3f){
            setVector(node, (Vector3f)node_obj);
        }else if(node_obj instanceof ColorRGBA){
            setColor(node, (ColorRGBA)node_obj);
        }
    }

    /**
     * 
     * @param treepath
     * @param pathcount
     * @param node_obj
     */
    public void setPathElementPE(Object[] treepath, int pathcount, Object node_obj){
        Node penvnode = getPhysicalEnvironmentNode();
        Node node = penvnode;

        for (int i = 2; i < pathcount-1; i++) {
            node = getHashNodesNode(node,treepath[i].toString());
        }
        if(node_obj instanceof Float){
            node.setTextContent(node_obj.toString());
        }else if(node_obj instanceof Integer){
            node.setTextContent(node_obj.toString());
        }else if(node_obj instanceof Boolean){
            node.setTextContent(node_obj.toString());
        }else if(node_obj instanceof String){
            node.setTextContent(node_obj.toString());
        }else if(node_obj instanceof Vector3f){
            setVector(node, (Vector3f)node_obj);
        }else if(node_obj instanceof ColorRGBA){
            setColor(node, (ColorRGBA)node_obj);
        }
    }

    /**
     *
     * @param auv_name
     * @param treepath
     * @param pathcount
     * @param node_obj
     */
    public void setPathElementAUV(String auv_name, Object[] treepath, int pathcount, Object node_obj){
        Node auvvnode = getAUVNode(auv_name);
        Node auv_param = getAUVParametersNode(auvvnode);
        Node node = auv_param;

        for (int i = 4; i < pathcount-1; i++) {
            node = getHashNodesNode(node,treepath[i].toString());
        }
        if(node_obj instanceof Float){
            node.setTextContent(node_obj.toString());
        }else if(node_obj instanceof Integer){
            node.setTextContent(node_obj.toString());
        }else if(node_obj instanceof Boolean){
            node.setTextContent(node_obj.toString());
        }else if(node_obj instanceof String){
            node.setTextContent(node_obj.toString());
        }else if(node_obj instanceof Vector3f){
            setVector(node, (Vector3f)node_obj);
        }else if(node_obj instanceof ColorRGBA){
            setColor(node, (ColorRGBA)node_obj);
        }
    }

    /**
     *
     * @param simob_name
     * @param treepath
     * @param pathcount
     * @param node_obj
     */
    public void setPathElementSimObject(String simob_name, Object[] treepath, int pathcount, Object node_obj){
        Node auvvnode = getSimObjectNode(simob_name);
        Node node = auvvnode;

        for (int i = 3; i < pathcount-1; i++) {
            node = getHashNodesNode(node,treepath[i].toString());
        }
        if(node_obj instanceof Float){
            node.setTextContent(node_obj.toString());
        }else if(node_obj instanceof Integer){
            node.setTextContent(node_obj.toString());
        }else if(node_obj instanceof Boolean){
            node.setTextContent(node_obj.toString());
        }else if(node_obj instanceof String){
            node.setTextContent(node_obj.toString());
        }else if(node_obj instanceof Vector3f){
            setVector(node, (Vector3f)node_obj);
        }else if(node_obj instanceof ColorRGBA){
            setColor(node, (ColorRGBA)node_obj);
        }
    }

    /**
     *
     * @return
     */
    public Document getDocument() {
        return document;
    }

    /**
     * 
     * @param document
     */
    private void setDocument(Document document) {
        this.document = document;
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public MARS_Settings getSimAUVSettings() throws Exception{
        MARS_Settings settings = new MARS_Settings(this);

        Node RootNode = document.getDocumentElement();
        String root = RootNode.getNodeName();

        NodeList nl = document.getElementsByTagName("Settings");

        for (int i = 0; i < nl.getLength(); i++) {
            NodeList nl2 = nl.item(i).getChildNodes();
            for (int j = 0; j < nl2.getLength(); j++) {
                Node node = nl2.item(j);
                if(node.getNodeName().equals("Graphics")){
                    NodeList nl3 = node.getChildNodes();
                    for (int k = 0; k < nl3.getLength(); k++) {
                        Node GraphicsNode = nl3.item(k);
                        parseGraphics(GraphicsNode,settings);
                    }
                }else if(node.getNodeName().equals("Server")){
                    NodeList nl3 = node.getChildNodes();
                    for (int k = 0; k < nl3.getLength(); k++) {
                        Node ServerNode = nl3.item(k);
                        parseServer(ServerNode,settings);
                    }
                }else if(node.getNodeName().equals("Misc")){
                    NodeList nl3 = node.getChildNodes();
                    for (int k = 0; k < nl3.getLength(); k++) {
                        Node MiscNode = nl3.item(k);
                        parseMisc(MiscNode,settings);
                    }
                }else if(node.getNodeName().equals("Physics")){
                    NodeList nl3 = node.getChildNodes();
                    for (int k = 0; k < nl3.getLength(); k++) {
                        Node PhysicsNode = nl3.item(k);
                        parsePhysics(PhysicsNode,settings);
                    }
                }else if(node.getNodeName().equals("PhysicalEnvironment")){
                    parsePhysicalEnvironment(node,settings);
                }
            }
        }

        return settings;
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public int getFrameLimit() throws Exception{
        NodeList nl = document.getElementsByTagName("FrameLimit");
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if(node.getNodeName().equals("FrameLimit")){
                return Integer.valueOf(node.getTextContent().trim());
            }
        }
        return 480;
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public int getResolutionHeight() throws Exception{
        NodeList nl = document.getElementsByTagName("Resolution");
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if(node.getNodeName().equals("Resolution")){
                NodeList nl2 = node.getChildNodes();
                for (int j = 0; j < nl2.getLength(); j++) {
                    Node res_node = nl2.item(j);
                    if(res_node.getNodeName().equals("height")){
                        return Integer.valueOf(res_node.getTextContent().trim());
                    }
                }
            }
        }
        return 480;
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public int getResolutionWidth() throws Exception{
        NodeList nl = document.getElementsByTagName("Resolution");
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if(node.getNodeName().equals("Resolution")){
                NodeList nl2 = node.getChildNodes();
                for (int j = 0; j < nl2.getLength(); j++) {
                    Node res_node = nl2.item(j);
                    if(res_node.getNodeName().equals("width")){
                        return Integer.valueOf(res_node.getTextContent().trim());
                    }
                }
            }
        }
        return 640;
    }

    /**
     *
     * @param object_name
     * @return
     * @throws Exception
     */
    public SimObject getObject(String object_name) throws Exception
    {
        NodeList nl = document.getElementsByTagName("Object");
        SimObject simobject = new SimObject(this);

        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if(node.getNodeName().equals("Object") && node.getAttributes().item(0).getTextContent().equals(object_name)){
                simobject = parseObjects(node,mars);
                break;
            }
        }
        return simobject;
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public ArrayList getObjects() throws Exception
    {
        ArrayList simobjects = new ArrayList();

        NodeList nl = document.getElementsByTagName("Object");

        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if(node.getNodeName().equals("Object")){
                simobjects.add(parseObjects(node,mars));
            }
        }
        return simobjects;
    }

    private BasicAUV get_auv_type(AUV_Parameters auv_param){
        if(auv_param.getAuv_class().equals("Hanse")){
            Hanse auv_hanse = new Hanse();
            return auv_hanse;
        }else if(auv_param.getAuv_class().equals("Monsun2")){
            Monsun2 auv_monsun2 = new Monsun2();
            return auv_monsun2;
        }else if(auv_param.getAuv_class().equals("BasicAUV")){
            BasicAUV auv_basic = new BasicAUV();
            return auv_basic;
        }       
        return null;
    }
    
    /**
     *
     * @return
     * @throws Exception
     */
    public ArrayList getAuvs() throws Exception
    {
        ArrayList auvs = new ArrayList();

        Node RootNode = document.getDocumentElement();
        String root = RootNode.getNodeName();

        NodeList nl = document.getElementsByTagName("AUV");

        for (int i = 0; i < nl.getLength(); i++) {
            ArrayList sensors = new ArrayList();
            ArrayList actuators = new ArrayList();
            NodeList nl2 = nl.item(i).getChildNodes();
            AUV_Parameters auv_param = new AUV_Parameters(this);
            BasicAUV basic_auv = null;
            auv_param.setAuv_name(nl.item(i).getAttributes().getNamedItem("name").getTextContent());
            for (int j = 0; j < nl2.getLength(); j++) {
                Node node = nl2.item(j);
                if(node.getNodeName().equals("AUVParameters")){
                    parseAUVParameters(node,auv_param);
                }else if(node.getNodeName().equals("Sensors")){
                    sensors = parseSensors(node);
                }else if(node.getNodeName().equals("Actuators")){
                    actuators = parseActuators(node);
                }
            }
            basic_auv = get_auv_type(auv_param);
            basic_auv.setAuv_param(auv_param);
            basic_auv.setName(nl.item(i).getAttributes().getNamedItem("name").getTextContent());    
            //hanse.registerSensors(sensors);
            //hanse.registerActuators(actuators);
            basic_auv.registerPhysicalExchangers(sensors);
            basic_auv.registerPhysicalExchangers(actuators);
            auvs.add(basic_auv);
        }
        return auvs;
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public HashMap<String,AUV> getAuvsHash() throws Exception
    {
        HashMap<String,AUV> auvs = new HashMap<String, AUV>();

        Node RootNode = document.getDocumentElement();
        String root = RootNode.getNodeName();

        NodeList nl = document.getElementsByTagName("AUV");

        for (int i = 0; i < nl.getLength(); i++) {
            ArrayList sensors = new ArrayList();
            ArrayList actuators = new ArrayList();
            NodeList nl2 = nl.item(i).getChildNodes();
            AUV_Parameters auv_param = new AUV_Parameters(this);
            BasicAUV basic_auv = null;
            auv_param.setAuv_name(nl.item(i).getAttributes().getNamedItem("name").getTextContent());
            for (int j = 0; j < nl2.getLength(); j++) {
                Node node = nl2.item(j);
                if(node.getNodeName().equals("AUVParameters")){
                    parseAUVParameters(node,auv_param);
                }else if(node.getNodeName().equals("Sensors")){
                    sensors = parseSensors(node);
                }else if(node.getNodeName().equals("Actuators")){
                    actuators = parseActuators(node);
                }
            }
            basic_auv = get_auv_type(auv_param);
            basic_auv.setAuv_param(auv_param);
            basic_auv.setName(nl.item(i).getAttributes().getNamedItem("name").getTextContent());    
            //hanse.registerSensors(sensors);
            //hanse.registerActuators(actuators);
            basic_auv.registerPhysicalExchangers(sensors);
            basic_auv.registerPhysicalExchangers(actuators);
            auvs.put(basic_auv.getName(), basic_auv);
        }
        return auvs;
    }
    
    /**
     *
     * @param auv_name
     * @return
     * @throws Exception
     */
    public AUV getAuv(String auv_name) throws Exception
    {
        Node RootNode = document.getDocumentElement();
        String root = RootNode.getNodeName();

        NodeList nl = document.getElementsByTagName("AUV");
        BasicAUV basic_auv = null;
        
        for (int i = 0; i < nl.getLength(); i++) {
            if(nl.item(i).getAttributes().item(0).getTextContent().equals(auv_name)){
                ArrayList sensors = new ArrayList();
                ArrayList actuators = new ArrayList();
                NodeList nl2 = nl.item(i).getChildNodes();
                AUV_Parameters auv_param = new AUV_Parameters(this);
                for (int j = 0; j < nl2.getLength(); j++) {
                    Node node = nl2.item(j);
                    if(node.getNodeName().equals("AUVParameters")){
                        parseAUVParameters(node,auv_param);
                    }else if(node.getNodeName().equals("Sensors")){
                        sensors = parseSensors(node);
                    }else if(node.getNodeName().equals("Actuators")){
                        actuators = parseActuators(node);
                    }
                }
                basic_auv = get_auv_type(auv_param);
                basic_auv.setAuv_param(auv_param);
                basic_auv.setName(nl.item(i).getAttributes().getNamedItem("name").getTextContent());    
                //hanse.registerSensors(sensors);
                //hanse.registerActuators(actuators);
                basic_auv.registerPhysicalExchangers(sensors);
                basic_auv.registerPhysicalExchangers(actuators);
                break;
            }
        }
        return basic_auv;
    }

    private SimObject parseObjects(Node model_node, MARS_Main simauv){
        NodeList nl = model_node.getChildNodes();
        SimObject simob = new SimObject(this);
        simob.setName(model_node.getAttributes().item(0).getTextContent());
        for (int k = 0; k < nl.getLength(); k++) {
            Node ObjectsNode = nl.item(k);
            if(ObjectsNode.getNodeName().equals("enabled")){
                simob.setEnabled(Boolean.valueOf(ObjectsNode.getTextContent().trim()));
            }else if(ObjectsNode.getNodeName().equals("sonar_detectable")){
                simob.setSonar_detectable(Boolean.valueOf(ObjectsNode.getTextContent().trim()));
            }else if(ObjectsNode.getNodeName().equals("pinger")){
                simob.setPinger(Boolean.valueOf(ObjectsNode.getTextContent().trim()));
            }else if(ObjectsNode.getNodeName().equals("light")){
                simob.setLight(Boolean.valueOf(ObjectsNode.getTextContent().trim()));
            }else if(ObjectsNode.getNodeName().equals("color")){
                simob.setColor(getColor(ObjectsNode));
            }else if(ObjectsNode.getNodeName().equals("filepath")){
                simob.setFilepath(ObjectsNode.getTextContent().trim());
            }else if(ObjectsNode.getNodeName().equals("scale")){
                simob.setScale(Float.valueOf(ObjectsNode.getTextContent().trim()));
            }else if(ObjectsNode.getNodeName().equals("position")){
                simob.setPosition(getVector(ObjectsNode));
            }else if(ObjectsNode.getNodeName().equals("rotation")){
                simob.setRotation(getVector(ObjectsNode));
            }else if(ObjectsNode.getNodeName().equals("Collision")){
                parseCollisionSimOb(ObjectsNode,simob);
            }
        }
        return simob;
    }

    private void parseCollisionSimOb(Node node,SimObject simob){
        NodeList nl3 = node.getChildNodes();
        for (int k = 0; k < nl3.getLength(); k++) {
            Node ObjectsNode = nl3.item(k);
            if(ObjectsNode.getNodeName().equals("type")){
                simob.setType(Integer.valueOf(ObjectsNode.getTextContent().trim()));
            }else if(ObjectsNode.getNodeName().equals("dimensions")){
                simob.setDimensions(getVector(ObjectsNode));
            }else if(ObjectsNode.getNodeName().equals("position")){
                simob.setCollisionPosition(getVector(ObjectsNode));
            }else if(ObjectsNode.getNodeName().equals("debug_collision")){
                simob.setDebugCollision(Boolean.valueOf(ObjectsNode.getTextContent().trim()));
            }else if(ObjectsNode.getNodeName().equals("collidable")){
                simob.setCollidable(Boolean.valueOf(ObjectsNode.getTextContent().trim()));
            }
        }
    }

    private void parseGraphics(Node node, MARS_Settings settings){
        if(node.getNodeName().equals("Axis")){
            getSetupAxis(node,settings);
        }else if(node.getNodeName().equals("Fog")){
            getSetupFog(node,settings);
        }else if(node.getNodeName().equals("DepthOfField")){
            getSetupDepthOfField(node,settings);
        }else if(node.getNodeName().equals("WavesWater")){
            getSetupWavesWater(node,settings);
        }else if(node.getNodeName().equals("Water")){
            getSetupWater(node,settings);
        }else if(node.getNodeName().equals("PlaneWater")){
            getSetupPlaneWater(node,settings);
        }else if(node.getNodeName().equals("SkyBox")){
            getSetupSkyBox(node,settings);
        }else if(node.getNodeName().equals("SimpleSkyBox")){
            getSetupSimpleSkyBox(node,settings);
        }else if(node.getNodeName().equals("Terrain")){
            getSetupTerrain(node,settings);
        }else if(node.getNodeName().equals("Light")){
            getSetupLight(node,settings);
        }else if(node.getNodeName().equals("WireFrame")){
            getSetupWireFrame(node,settings);
        }else if(node.getNodeName().equals("CrossHairs")){
            getsetupCrossHairs(node,settings);
        }else if(node.getNodeName().equals("Resolution")){
            getResolution(node,settings);
        }else if(node.getNodeName().equals("FrameLimit")){
            settings.setFrameLimit(Integer.valueOf(node.getTextContent().trim()));
        }else if(node.getNodeName().equals("FPS")){
            settings.setFPS(Boolean.valueOf(node.getTextContent().trim()));
        }
    }

    private void parseServer(Node node, MARS_Settings settings){
        NodeList nl = node.getChildNodes();
        for (int k = 0; k < nl.getLength(); k++) {
            Node ServerNode = nl.item(k);
            if(node.getNodeName().equals("RAW")){
                parseRAWServer(ServerNode,settings);
            }else if(node.getNodeName().equals("ROS")){
                parseROSServer(ServerNode,settings);
            }
        }
    }
    
    private void parseRAWServer(Node node, MARS_Settings settings){
        if(node.getNodeName().equals("port")){
            settings.setRAW_Server_port(Integer.valueOf(node.getTextContent().trim()));
        }else if(node.getNodeName().equals("backlog")){
            settings.setRAW_Server_backlog(Integer.valueOf(node.getTextContent().trim()));
        }else if(node.getNodeName().equals("OutputStreamSize")){
            settings.setRAW_Server_OutputStreamSize(Integer.valueOf(node.getTextContent().trim()));
        }else if(node.getNodeName().equals("enabled")){
            settings.setRAW_Server_enabled(Boolean.valueOf(node.getTextContent().trim()));  
        }
    }
    
    private void parseROSServer(Node node, MARS_Settings settings){
        if(node.getNodeName().equals("masterport")){
            settings.setROS_Server_port(Integer.valueOf(node.getTextContent().trim()));
        }else if(node.getNodeName().equals("masterip")){
            settings.setROS_Master_IP(String.valueOf(node.getTextContent().trim()));
        }else if(node.getNodeName().equals("enabled")){
            settings.setROS_Server_enabled(Boolean.valueOf(node.getTextContent().trim()));  
        }
    }
    
    private void parseMisc(Node node, MARS_Settings settings){
        if(node.getNodeName().equals("setupCam")){
            getSetupCam(node,settings);
        }
    }

    private void parsePhysics(Node node, MARS_Settings settings){
        if(node.getNodeName().equals("framerate")){
            settings.setPhysicsFramerate(Integer.valueOf(node.getTextContent().trim()));
        }else if(node.getNodeName().equals("debug")){
            settings.setPhysicsDebug(Boolean.valueOf(node.getTextContent().trim()));
        }
    }

    private ArrayList parseSensors(Node node){
        ArrayList sensors = new ArrayList();
        NodeList nl3 = node.getChildNodes();
        for (int k = 0; k < nl3.getLength(); k++) {
            Node SensorsNode = nl3.item(k);
            if(SensorsNode.getNodeName().equals("PressureSensor")){
                PressureSensor press = getPressureSensor(SensorsNode);
                sensors.add(press);
            }else if(SensorsNode.getNodeName().equals("TemperatureSensor")){
                TemperatureSensor temp = getTemperatureSensor(SensorsNode);
                sensors.add(temp);
            }else if(SensorsNode.getNodeName().equals("Accelerometer")){
                Accelerometer acc = getAccelerometer(SensorsNode);
                sensors.add(acc);
            }else if(SensorsNode.getNodeName().equals("Gyroscope")){
                Gyroscope gyro = getGyroscope(SensorsNode);
                sensors.add(gyro);
            }else if(SensorsNode.getNodeName().equals("Compass")){
                Compass comp = getCompass(SensorsNode);
                sensors.add(comp);
            }else if(SensorsNode.getNodeName().equals("ImagenexSonar360")){
                ImagenexSonar_852_Scanning son = getImaginexSonarScanning(SensorsNode);
                sensors.add(son);
            }else if(SensorsNode.getNodeName().equals("ImagenexSonarEcho")){
                ImagenexSonar_852_Echo son = getImaginexSonarEcho(SensorsNode);
                sensors.add(son);
            }else if(SensorsNode.getNodeName().equals("VideoCamera")){
                VideoCamera vid = getVideoCamera(SensorsNode);
                sensors.add(vid);
            }else if(SensorsNode.getNodeName().equals("PingDetector")){
                PingDetector ping = getPingDetector(SensorsNode);
                sensors.add(ping);
            }else if(SensorsNode.getNodeName().equals("UnderwaterModem")){
                UnderwaterModem mod = getUnderwaterModem(SensorsNode);
                sensors.add(mod);
            }else if(SensorsNode.getNodeName().equals("InfraRedSensor")){
                InfraRedSensor infra = getInfraRedSensor(SensorsNode);
                sensors.add(infra);
            }
        }
        return sensors;
    }

    private ArrayList parseActuators(Node node){
        ArrayList actuators = new ArrayList();
        NodeList nl3 = node.getChildNodes();
        for (int k = 0; k < nl3.getLength(); k++) {
            Node ActuatorsNode = nl3.item(k);
            if(ActuatorsNode.getNodeName().equals("SeaBotixThruster")){
                SeaBotixThruster sea = getSeaBotixThruster(ActuatorsNode);
                actuators.add(sea);
            }else if(ActuatorsNode.getNodeName().equals("BrushlessThruster")){
                BrushlessThruster brush = getBrushlessThruster(ActuatorsNode);
                actuators.add(brush);
            }
        }
        return actuators;
    }

    private void parseModel(Node node,AUV_Parameters auv_param){
        NodeList nl3 = node.getChildNodes();
        for (int k = 0; k < nl3.getLength(); k++) {
            Node ModelNode = nl3.item(k);
            if(ModelNode.getNodeName().equals("filepath")){
                auv_param.setModelFilePath(ModelNode.getTextContent().trim());
            }else if(ModelNode.getNodeName().equals("scale")){
                auv_param.setModel_scale(Float.valueOf(ModelNode.getTextContent().trim()));
            }else if(ModelNode.getNodeName().equals("name")){
                auv_param.setModel_name(ModelNode.getTextContent().trim());
            }
        }
    }

    private void parseDebug(Node node,AUV_Parameters auv_param){
        NodeList nl3 = node.getChildNodes();
        for (int k = 0; k < nl3.getLength(); k++) {
            Node ModelNode = nl3.item(k);
            if(ModelNode.getNodeName().equals("buoyancy")){
                auv_param.setDebugBuoycancy(Boolean.valueOf(ModelNode.getTextContent().trim()));
            }else if(ModelNode.getNodeName().equals("drag")){
                auv_param.setDebugDrag(Boolean.valueOf(ModelNode.getTextContent().trim()));
            }else if(ModelNode.getNodeName().equals("physical_exchanger")){
                auv_param.setDebugPhysicalExchanger(Boolean.valueOf(ModelNode.getTextContent().trim()));
            }else if(ModelNode.getNodeName().equals("collision")){
                auv_param.setDebugCollision(Boolean.valueOf(ModelNode.getTextContent().trim()));
            }else if(ModelNode.getNodeName().equals("centers")){
                auv_param.setDebugCenters(Boolean.valueOf(ModelNode.getTextContent().trim()));
            }
        }
    }

    private void parseCollision(Node node,AUV_Parameters auv_param){
        NodeList nl3 = node.getChildNodes();
        for (int k = 0; k < nl3.getLength(); k++) {
            Node ModelNode = nl3.item(k);
            if(ModelNode.getNodeName().equals("type")){
                auv_param.setType(Integer.valueOf(ModelNode.getTextContent().trim()));
            }else if(ModelNode.getNodeName().equals("dimensions")){
                auv_param.setDimensions(getVector(ModelNode));
            }else if(ModelNode.getNodeName().equals("position")){
                auv_param.setCollisionPosition(getVector(ModelNode));
            }
        }
    }

    private void parseAUVParameters(Node node,AUV_Parameters auv_param){
        NodeList nl3 = node.getChildNodes();
        for (int k = 0; k < nl3.getLength(); k++) {
            Node AUVParametersNode = nl3.item(k);
            if(AUVParametersNode.getNodeName().equals("mass_auv")){
                auv_param.setMass(Float.valueOf(AUVParametersNode.getTextContent().trim()));
            }else if(AUVParametersNode.getNodeName().equals("enabled")){
                auv_param.setEnabled(Boolean.valueOf(AUVParametersNode.getTextContent().trim()));
            }else if(AUVParametersNode.getNodeName().equals("class")){
                auv_param.setAuv_class(String.valueOf(AUVParametersNode.getTextContent().trim()));
            }else if(AUVParametersNode.getNodeName().equals("drag_coefficient_linear")){
                auv_param.setDrag_coefficient_linear(Float.valueOf(AUVParametersNode.getTextContent().trim()));
            }else if(AUVParametersNode.getNodeName().equals("drag_coefficient_angular")){
                auv_param.setDrag_coefficient_angular(Float.valueOf(AUVParametersNode.getTextContent().trim()));
            }else if(AUVParametersNode.getNodeName().equals("centroid_center_distance")){
                auv_param.setCentroid_center_distance(getVector(AUVParametersNode));
            }else if(AUVParametersNode.getNodeName().equals("position")){
                auv_param.setPosition(getVector(AUVParametersNode));
            }else if(AUVParametersNode.getNodeName().equals("rotation")){
                auv_param.setRotation(getVector(AUVParametersNode));
            }else if(AUVParametersNode.getNodeName().equals("damping_linear")){;
                auv_param.setDamping_linear(Float.valueOf(AUVParametersNode.getTextContent().trim()));
            }else if(AUVParametersNode.getNodeName().equals("damping_angular")){
                auv_param.setDamping_angular(Float.valueOf(AUVParametersNode.getTextContent().trim()));
            }else if(AUVParametersNode.getNodeName().equals("damping_angular")){
                auv_param.setDamping_angular(Float.valueOf(AUVParametersNode.getTextContent().trim()));
            }else if(AUVParametersNode.getNodeName().equals("angular_factor")){
                auv_param.setAngular_factor(Float.valueOf(AUVParametersNode.getTextContent().trim()));
            }else if(AUVParametersNode.getNodeName().equals("linear_factor")){
                auv_param.setLinear_factor(getVector(AUVParametersNode));
            }else if(AUVParametersNode.getNodeName().equals("offCamera_height")){
                auv_param.setOffCamera_height(Integer.valueOf(AUVParametersNode.getTextContent().trim()));
            }else if(AUVParametersNode.getNodeName().equals("offCamera_width")){
                auv_param.setOffCamera_width(Integer.valueOf(AUVParametersNode.getTextContent().trim()));
            }else if(AUVParametersNode.getNodeName().equals("buoyancy_updaterate")){
                auv_param.setBuoyancy_updaterate(Integer.valueOf(AUVParametersNode.getTextContent().trim()));
            }else if(AUVParametersNode.getNodeName().equals("buoyancy_distance")){
                auv_param.setBuoyancy_distance(Float.valueOf(AUVParametersNode.getTextContent().trim()));
            }else if(AUVParametersNode.getNodeName().equals("buoyancy_scale")){
                auv_param.setBuoyancy_scale(Float.valueOf(AUVParametersNode.getTextContent().trim()));
            }else if(AUVParametersNode.getNodeName().equals("drag_updaterate")){
                auv_param.setDrag_updaterate(Integer.valueOf(AUVParametersNode.getTextContent().trim()));
            }else if(AUVParametersNode.getNodeName().equals("Waypoints")){
                getWaypoints(AUVParametersNode,auv_param);
            }else if(AUVParametersNode.getNodeName().equals("Model")){
                parseModel(AUVParametersNode,auv_param);
            }else if(AUVParametersNode.getNodeName().equals("Debug")){
                parseDebug(AUVParametersNode,auv_param);
            }else if(AUVParametersNode.getNodeName().equals("Collision")){
                parseCollision(AUVParametersNode,auv_param);
            }else if(AUVParametersNode.getNodeName().equals("physicalvalues_updaterate")){
                auv_param.setPhysicalvalues_updaterate(Float.valueOf(AUVParametersNode.getTextContent().trim()));
            }
        }
    }

    private void parsePhysicalEnvironment(Node node,MARS_Settings settings){
        PhysicalEnvironment pe = new PhysicalEnvironment(this);
        NodeList nl3 = node.getChildNodes();
        for (int k = 0; k < nl3.getLength(); k++) {
            Node PhysicalEnvironmentNode = nl3.item(k);
            if(PhysicalEnvironmentNode.getNodeName().equals("fluid_density")){
                pe.setFluid_density(Float.valueOf(PhysicalEnvironmentNode.getTextContent().trim()));
            }else if(PhysicalEnvironmentNode.getNodeName().equals("air_density")){
                pe.setAir_density(Float.valueOf(PhysicalEnvironmentNode.getTextContent().trim()));
            }else if(PhysicalEnvironmentNode.getNodeName().equals("fluid_temp")){
                pe.setFluid_temp(Float.valueOf(PhysicalEnvironmentNode.getTextContent().trim()));
            }else if(PhysicalEnvironmentNode.getNodeName().equals("air_temp")){
                pe.setAir_temp(Float.valueOf(PhysicalEnvironmentNode.getTextContent().trim()));
            }else if(PhysicalEnvironmentNode.getNodeName().equals("fluid_viscosity")){
                pe.setFluid_viscosity(Float.valueOf(PhysicalEnvironmentNode.getTextContent().trim()));
            }else if(PhysicalEnvironmentNode.getNodeName().equals("fluid_salinity")){
                pe.setFluid_salinity(Float.valueOf(PhysicalEnvironmentNode.getTextContent().trim()));
            }else if(PhysicalEnvironmentNode.getNodeName().equals("water_height")){
                pe.setWater_height(Float.valueOf(PhysicalEnvironmentNode.getTextContent().trim()));
            }else if(PhysicalEnvironmentNode.getNodeName().equals("pressure_water_height")){
                pe.setPressure_water_height(Float.valueOf(PhysicalEnvironmentNode.getTextContent().trim()));
            }else if(PhysicalEnvironmentNode.getNodeName().equals("gravitational_acceleration_vector")){
                pe.setGravitational_acceleration_vector(getVector(PhysicalEnvironmentNode));
            }else if(PhysicalEnvironmentNode.getNodeName().equals("magnetic_north")){
                pe.setMagnetic_north(getVector(PhysicalEnvironmentNode));
            }else if(PhysicalEnvironmentNode.getNodeName().equals("magnetic_east")){
                pe.setMagnetic_east(getVector(PhysicalEnvironmentNode));
            }else if(PhysicalEnvironmentNode.getNodeName().equals("magnetic_z")){
                pe.setMagnetic_z(getVector(PhysicalEnvironmentNode));
            }else if(PhysicalEnvironmentNode.getNodeName().equals("water_current")){
                pe.setWater_current(getVector(PhysicalEnvironmentNode));
            }
        }
        settings.setPhysical_environment(pe);
    }
    
    private Vector3f getVector(Node nodes){
        Vector3f ret = new Vector3f(0f,0f,0f);
        NodeList nodelist = nodes.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node node = nodelist.item(i);
            if(node.getNodeName().equals("vector")){
                NodeList n = node.getChildNodes();
                for (int k = 0; k < n.getLength(); k++) {
                    Node VectorNode = n.item(k);
                    if(VectorNode.getNodeName().equals("x")){
                        ret.x = Float.valueOf(VectorNode.getTextContent().trim());
                    }else if(VectorNode.getNodeName().equals("y")){
                        ret.y = Float.valueOf(VectorNode.getTextContent().trim());
                    }else if(VectorNode.getNodeName().equals("z")){
                        ret.z = Float.valueOf(VectorNode.getTextContent().trim());
                    }
                }
            }
        }
        return ret;
    }

    private void setVector(Node node, Vector3f vector){
        NodeList nodelist = node.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node nodevec = nodelist.item(i);
            if(nodevec.getNodeName().equals("vector")){
                NodeList n = nodevec.getChildNodes();
                for (int k = 0; k < n.getLength(); k++) {
                    Node VectorNode = n.item(k);
                    if(VectorNode.getNodeName().equals("x")){
                        VectorNode.setTextContent(String.valueOf(vector.x));
                    }else if(VectorNode.getNodeName().equals("y")){
                        VectorNode.setTextContent(String.valueOf(vector.y));
                    }else if(VectorNode.getNodeName().equals("z")){
                        VectorNode.setTextContent(String.valueOf(vector.z));
                    }
                }
            }
        }
    }

    private void setColor(Node node, ColorRGBA color){
        Vector3f col = new Vector3f(color.r*255f,color.b*255f,color.g*255f);
        setVector(node,col);
    }

    private ColorRGBA getColor(Node node){
        Vector3f vec = getVector(node);
        ColorRGBA color = new ColorRGBA(vec.x/255f,vec.y/255f,vec.z/255f,0f);
        return color;
    }

    private SeaBotixThruster getSeaBotixThruster(Node node){
        SeaBotixThruster sea = new SeaBotixThruster(simstate);
        NodeList nodelist = node.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node sensor_node = nodelist.item(i);
            if(sensor_node.getNodeName().equals("name")){
                sea.setPhysicalExchangerName(sensor_node.getTextContent().trim());
            }else if(sensor_node.getNodeName().equals("enabled")){
                sea.setEnabled(Boolean.valueOf(sensor_node.getTextContent().trim()));    
            }else if(sensor_node.getNodeName().equals("position")){
                sea.setMotorPosition(getVector(sensor_node));
            }else if(sensor_node.getNodeName().equals("MotorDirection")){
                sea.setMotorDirection(getVector(sensor_node));
            }else if(sensor_node.getNodeName().equals("noise")){
                getNoise(sensor_node,sea);
            }
        }
        return sea;
    }
    
    private BrushlessThruster getBrushlessThruster(Node node){
        BrushlessThruster sea = new BrushlessThruster(simstate);
        NodeList nodelist = node.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node sensor_node = nodelist.item(i);
            if(sensor_node.getNodeName().equals("name")){
                sea.setPhysicalExchangerName(sensor_node.getTextContent().trim());
            }else if(sensor_node.getNodeName().equals("enabled")){
                sea.setEnabled(Boolean.valueOf(sensor_node.getTextContent().trim()));    
            }else if(sensor_node.getNodeName().equals("position")){
                sea.setMotorPosition(getVector(sensor_node));
            }else if(sensor_node.getNodeName().equals("MotorDirection")){
                sea.setMotorDirection(getVector(sensor_node));
            }else if(sensor_node.getNodeName().equals("noise")){
                getNoise(sensor_node,sea);
            }
        }
        return sea;
    }

    private PingDetector getPingDetector(Node node){
        PingDetector ping = new PingDetector(simstate);
        NodeList nodelist = node.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node sensor_node = nodelist.item(i);
            if(sensor_node.getNodeName().equals("name")){
                ping.setPhysicalExchangerName(sensor_node.getTextContent().trim());
            }else if(sensor_node.getNodeName().equals("ros_publish_rate")){
                ping.setRos_publish_rate(Integer.valueOf(sensor_node.getTextContent().trim()));    
            }else if(sensor_node.getNodeName().equals("enabled")){
                ping.setEnabled(Boolean.valueOf(sensor_node.getTextContent().trim()));    
            }else if(sensor_node.getNodeName().equals("position")){
                ping.setPingStartVector(getVector(sensor_node));
            }else if(sensor_node.getNodeName().equals("PingerDirection")){
                ping.setPingDirectionVector(getVector(sensor_node));
            }else if(sensor_node.getNodeName().equals("detection_range")){
                ping.setDetection_range(Float.valueOf(sensor_node.getTextContent().trim()));
            }else if(sensor_node.getNodeName().equals("noise")){
                getNoise(sensor_node,ping);
            }
        }
        return ping;
    }

    private PressureSensor getPressureSensor(Node node){
        PressureSensor press = new PressureSensor(simstate);
        NodeList nodelist = node.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node sensor_node = nodelist.item(i);
            if(sensor_node.getNodeName().equals("name")){
                press.setPhysicalExchangerName(sensor_node.getTextContent().trim());    
            }
            else if(sensor_node.getNodeName().equals("ros_publish_rate")){
                press.setRos_publish_rate(Integer.valueOf(sensor_node.getTextContent().trim()));    
            }
            else if(sensor_node.getNodeName().equals("enabled")){
                press.setEnabled(Boolean.valueOf(sensor_node.getTextContent().trim()));    
            }else if(sensor_node.getNodeName().equals("position")){
                press.setPressureSensorStartVector(getVector(sensor_node));
            }else if(sensor_node.getNodeName().equals("noise")){
                getNoise(sensor_node,press);
            }
        }
        return press;
    }

    private TemperatureSensor getTemperatureSensor(Node node){
        TemperatureSensor temp = new TemperatureSensor(simstate);
        NodeList nodelist = node.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node sensor_node = nodelist.item(i);
            if(sensor_node.getNodeName().equals("name")){
                temp.setPhysicalExchangerName(sensor_node.getTextContent().trim());
            }else if(sensor_node.getNodeName().equals("ros_publish_rate")){
                temp.setRos_publish_rate(Integer.valueOf(sensor_node.getTextContent().trim()));    
            }else if(sensor_node.getNodeName().equals("enabled")){
                temp.setEnabled(Boolean.valueOf(sensor_node.getTextContent().trim()));    
            }else if(sensor_node.getNodeName().equals("position")){
                temp.setTemperatureSensorStartVector(getVector(sensor_node));
            }else if(sensor_node.getNodeName().equals("noise")){
                getNoise(sensor_node,temp);
            }
        }
        return temp;
    }

    private Accelerometer getAccelerometer(Node node){
        Accelerometer acc = new Accelerometer(simstate);
        NodeList nodelist = node.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node sensor_node = nodelist.item(i);
            if(sensor_node.getNodeName().equals("name")){
                acc.setPhysicalExchangerName(sensor_node.getTextContent().trim());
            }else if(sensor_node.getNodeName().equals("ros_publish_rate")){
                acc.setRos_publish_rate(Integer.valueOf(sensor_node.getTextContent().trim()));    
            }else if(sensor_node.getNodeName().equals("enabled")){
                acc.setEnabled(Boolean.valueOf(sensor_node.getTextContent().trim()));    
            }else if(sensor_node.getNodeName().equals("noise")){
                getNoise(sensor_node,acc);
            }
        }
        return acc;
    }

    private UnderwaterModem getUnderwaterModem(Node node){
        UnderwaterModem mod = new UnderwaterModem(simstate);
        NodeList nodelist = node.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node sensor_node = nodelist.item(i);
            if(sensor_node.getNodeName().equals("name")){
                mod.setPhysicalExchangerName(sensor_node.getTextContent().trim());
            }else if(sensor_node.getNodeName().equals("ros_publish_rate")){
                mod.setRos_publish_rate(Integer.valueOf(sensor_node.getTextContent().trim()));    
            }else if(sensor_node.getNodeName().equals("enabled")){
                mod.setEnabled(Boolean.valueOf(sensor_node.getTextContent().trim()));    
            }else if(sensor_node.getNodeName().equals("noise")){
                getNoise(sensor_node,mod);
            }
        }
        return mod;
    }
    
    private Gyroscope getGyroscope(Node node){
        Gyroscope gyro = new Gyroscope(simstate);
        NodeList nodelist = node.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node sensor_node = nodelist.item(i);
            if(sensor_node.getNodeName().equals("name")){
                gyro.setPhysicalExchangerName(sensor_node.getTextContent().trim());
            }else if(sensor_node.getNodeName().equals("ros_publish_rate")){
                gyro.setRos_publish_rate(Integer.valueOf(sensor_node.getTextContent().trim()));    
            }else if(sensor_node.getNodeName().equals("enabled")){
                gyro.setEnabled(Boolean.valueOf(sensor_node.getTextContent().trim()));    
            }else if(sensor_node.getNodeName().equals("noise")){
                getNoise(sensor_node,gyro);
            }
        }
        return gyro;
    }

    private Compass getCompass(Node node){
        Compass comp = new Compass(simstate);
        NodeList nodelist = node.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node sensor_node = nodelist.item(i);
            if(sensor_node.getNodeName().equals("name")){
                comp.setPhysicalExchangerName(sensor_node.getTextContent().trim());
            }else if(sensor_node.getNodeName().equals("ros_publish_rate")){
                comp.setRos_publish_rate(Integer.valueOf(sensor_node.getTextContent().trim()));    
            }else if(sensor_node.getNodeName().equals("enabled")){
                comp.setEnabled(Boolean.valueOf(sensor_node.getTextContent().trim()));    
            }else if(sensor_node.getNodeName().equals("position")){
                comp.setCompassStartVector(getVector(sensor_node));
            }else if(sensor_node.getNodeName().equals("CompassYawAxisVector")){
                comp.setCompassYawAxisVector(getVector(sensor_node));
            }else if(sensor_node.getNodeName().equals("CompassPitchAxisVector")){
                comp.setCompassPitchAxisVector(getVector(sensor_node));
            }else if(sensor_node.getNodeName().equals("CompassRollAxisVector")){
                comp.setCompassRollAxisVector(getVector(sensor_node));
            }else if(sensor_node.getNodeName().equals("noise")){
                getNoise(sensor_node,comp);
            }
        }
        return comp;
    }

    private InfraRedSensor getInfraRedSensor(Node node){
        InfraRedSensor infra = new InfraRedSensor(simstate,(com.jme3.scene.Node)mars.getRootNode().getChild("terrain"));
        NodeList nodelist = node.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node sensor_node = nodelist.item(i);
            if(sensor_node.getNodeName().equals("name")){
                infra.setPhysicalExchangerName(sensor_node.getTextContent().trim());
            }else if(sensor_node.getNodeName().equals("ros_publish_rate")){
                infra.setRos_publish_rate(Integer.valueOf(sensor_node.getTextContent().trim()));    
            }else if(sensor_node.getNodeName().equals("enabled")){
                infra.setEnabled(Boolean.valueOf(sensor_node.getTextContent().trim()));    
            }else if(sensor_node.getNodeName().equals("position")){
                infra.setPosition(getVector(sensor_node));
            }else if(sensor_node.getNodeName().equals("direction")){
                infra.setDirection(getVector(sensor_node));
            }else if(sensor_node.getNodeName().equals("MaxRange")){
                infra.setMaxRange(Float.valueOf(sensor_node.getTextContent().trim()));
            }else if(sensor_node.getNodeName().equals("MinRange")){
                infra.setMinRange(Float.valueOf(sensor_node.getTextContent().trim()));
            }else if(sensor_node.getNodeName().equals("noise")){
                getNoise(sensor_node,infra);
            }else if(sensor_node.getNodeName().equals("angular_damping")){
                getAngular_dampingI(sensor_node,infra);
            }else if(sensor_node.getNodeName().equals("length_damping")){
                getLength_dampingI(sensor_node,infra);
            }
        }
        return infra;
    }
        
    private ImagenexSonar_852_Scanning getImaginexSonarScanning(Node node){
        ImagenexSonar_852_Scanning son = new ImagenexSonar_852_Scanning(simstate,(com.jme3.scene.Node)mars.getRootNode().getChild("terrain"));
        NodeList nodelist = node.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node sensor_node = nodelist.item(i);
            if(sensor_node.getNodeName().equals("name")){
                son.setPhysicalExchangerName(sensor_node.getTextContent().trim());
            }else if(sensor_node.getNodeName().equals("ros_publish_rate")){
                son.setRos_publish_rate(Integer.valueOf(sensor_node.getTextContent().trim()));    
            }else if(sensor_node.getNodeName().equals("enabled")){
                son.setEnabled(Boolean.valueOf(sensor_node.getTextContent().trim()));    
            }else if(sensor_node.getNodeName().equals("position")){
                son.setSonarPosition(getVector(sensor_node));
            }else if(sensor_node.getNodeName().equals("SonarDirection")){
                son.setSonarDirection(getVector(sensor_node));
            }else if(sensor_node.getNodeName().equals("SonarUpDirection")){
                son.setSonarUpDirection(getVector(sensor_node));
            }else if(sensor_node.getNodeName().equals("SonarMaxRange")){
                son.setSonarMaxRange(Float.valueOf(sensor_node.getTextContent().trim()));
            }else if(sensor_node.getNodeName().equals("SonarMinRange")){
                son.setSonarMinRange(Float.valueOf(sensor_node.getTextContent().trim()));
            }else if(sensor_node.getNodeName().equals("SonarCone")){
                getSonarCone(sensor_node,son);
            }else if(sensor_node.getNodeName().equals("Scanning_resolution")){
                son.setScanning_resolution((float)((Float.valueOf(sensor_node.getTextContent().trim()))*(Math.PI/180f)));
            }else if(sensor_node.getNodeName().equals("ScanningGain")){
                son.setScanning_gain(Integer.valueOf(sensor_node.getTextContent().trim()));
            }else if(sensor_node.getNodeName().equals("Scanning")){
                son.setScanning(Boolean.valueOf(sensor_node.getTextContent().trim()));
            }else if(sensor_node.getNodeName().equals("noise")){
                getNoise(sensor_node,son);
            }else if(sensor_node.getNodeName().equals("Debug")){
                son.setDebug(Boolean.valueOf(sensor_node.getTextContent().trim()));
            }else if(sensor_node.getNodeName().equals("angular_damping")){
                getAngular_damping(sensor_node,son);
            }else if(sensor_node.getNodeName().equals("length_damping")){
                getLength_damping(sensor_node,son);
            }
        }
        return son;
    }

    private ImagenexSonar_852_Echo getImaginexSonarEcho(Node node){
        ImagenexSonar_852_Echo son = new ImagenexSonar_852_Echo(simstate,(com.jme3.scene.Node)mars.getRootNode().getChild("terrain"));
        NodeList nodelist = node.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node sensor_node = nodelist.item(i);
            if(sensor_node.getNodeName().equals("name")){
                son.setPhysicalExchangerName(sensor_node.getTextContent().trim());
            }else if(sensor_node.getNodeName().equals("ros_publish_rate")){
                son.setRos_publish_rate(Integer.valueOf(sensor_node.getTextContent().trim()));    
            }else if(sensor_node.getNodeName().equals("enabled")){
                son.setEnabled(Boolean.valueOf(sensor_node.getTextContent().trim()));    
            }else if(sensor_node.getNodeName().equals("position")){
                son.setSonarPosition(getVector(sensor_node));
            }else if(sensor_node.getNodeName().equals("SonarDirection")){
                son.setSonarDirection(getVector(sensor_node));
            }else if(sensor_node.getNodeName().equals("SonarUpDirection")){
                son.setSonarUpDirection(getVector(sensor_node));
            }else if(sensor_node.getNodeName().equals("SonarMaxRange")){
                son.setSonarMaxRange(Float.valueOf(sensor_node.getTextContent().trim()));
            }else if(sensor_node.getNodeName().equals("SonarMinRange")){
                son.setSonarMinRange(Float.valueOf(sensor_node.getTextContent().trim()));
            }else if(sensor_node.getNodeName().equals("SonarCone")){
                getSonarCone(sensor_node,son);
            }else if(sensor_node.getNodeName().equals("Scanning_resolution")){
                son.setScanning_resolution((float)((Float.valueOf(sensor_node.getTextContent().trim()))*(Math.PI/180f)));
            }else if(sensor_node.getNodeName().equals("ScanningGain")){
                son.setScanning_gain(Integer.valueOf(sensor_node.getTextContent().trim()));
            }else if(sensor_node.getNodeName().equals("Scanning")){
                son.setScanning(Boolean.valueOf(sensor_node.getTextContent().trim()));
            }else if(sensor_node.getNodeName().equals("noise")){
                getNoise(sensor_node,son);
            }else if(sensor_node.getNodeName().equals("Debug")){
                son.setDebug(Boolean.valueOf(sensor_node.getTextContent().trim()));
            }else if(sensor_node.getNodeName().equals("angular_damping")){
                getAngular_damping(sensor_node,son);
            }else if(sensor_node.getNodeName().equals("length_damping")){
                getLength_damping(sensor_node,son);
            }
        }
        return son;
    }

    private void getSonarCone(Node node, Sonar son){
        NodeList nodelist = node.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node sensor_node = nodelist.item(i);
            if(sensor_node.getNodeName().equals("SonarConeType")){
                son.setSonar_cone_type(Integer.valueOf(sensor_node.getTextContent().trim()));
            }else if(sensor_node.getNodeName().equals("beam_height")){
                son.setBeam_height((float)((Float.valueOf(sensor_node.getTextContent().trim()))*(Math.PI/180f)));
            }else if(sensor_node.getNodeName().equals("beam_width")){
                son.setBeam_width((float)((Float.valueOf(sensor_node.getTextContent().trim()))*(Math.PI/180f)));
            }else if(sensor_node.getNodeName().equals("beam_ray_height_resolution")){
                son.setBeam_ray_height_resolution(Integer.valueOf(sensor_node.getTextContent().trim()));
            }else if(sensor_node.getNodeName().equals("beam_ray_width_resolution")){
                son.setBeam_ray_width_resolution(Integer.valueOf(sensor_node.getTextContent().trim()));
            }
        }
    }

    private void getNoise(Node noise_nodes, PhysicalExchanger pe){
        NodeList nodelist = noise_nodes.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node noise_node = nodelist.item(i);
            if(noise_node.getNodeName().equals("type")){
                pe.setNoise_type(Integer.valueOf(noise_node.getTextContent().trim()));
            }else if(noise_node.getNodeName().equals("value")){
                pe.setNoise_value(Float.valueOf(noise_node.getTextContent().trim()));
            }
        }
    }

    private void getAngular_damping(Node node, Sonar son){
        NodeList nodelist = node.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node sensor_node = nodelist.item(i);
            if(sensor_node.getNodeName().equals("enabled")){
                son.setAngular_damping(Boolean.valueOf(sensor_node.getTextContent().trim()));
            }else if(sensor_node.getNodeName().equals("factor")){
                son.setAngular_factor(Float.valueOf(sensor_node.getTextContent().trim()));
            }
        }
    }

    private void getLength_damping(Node node, Sonar son){
        NodeList nodelist = node.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node sensor_node = nodelist.item(i);
            if(sensor_node.getNodeName().equals("enabled")){
                son.setLength_damping(Boolean.valueOf(sensor_node.getTextContent().trim()));
            }else if(sensor_node.getNodeName().equals("factor")){
                son.setLength_factor(Float.valueOf(sensor_node.getTextContent().trim()));
            }
        }
    }
    
        private void getAngular_dampingI(Node node, InfraRedSensor infra){
        NodeList nodelist = node.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node sensor_node = nodelist.item(i);
            if(sensor_node.getNodeName().equals("enabled")){
                infra.setAngular_damping(Boolean.valueOf(sensor_node.getTextContent().trim()));
            }else if(sensor_node.getNodeName().equals("factor")){
                infra.setAngular_factor(Float.valueOf(sensor_node.getTextContent().trim()));
            }
        }
    }

    private void getLength_dampingI(Node node, InfraRedSensor infra){
        NodeList nodelist = node.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node sensor_node = nodelist.item(i);
            if(sensor_node.getNodeName().equals("enabled")){
                infra.setLength_damping(Boolean.valueOf(sensor_node.getTextContent().trim()));
            }else if(sensor_node.getNodeName().equals("factor")){
                infra.setLength_factor(Float.valueOf(sensor_node.getTextContent().trim()));
            }
        }
    }

    private VideoCamera getVideoCamera(Node node){
        VideoCamera vid = new VideoCamera(simstate);
        NodeList nodelist = node.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node sensor_node = nodelist.item(i);
            if(sensor_node.getNodeName().equals("name")){
                vid.setPhysicalExchangerName(sensor_node.getTextContent().trim());
            }else if(sensor_node.getNodeName().equals("ros_publish_rate")){
                vid.setRos_publish_rate(Integer.valueOf(sensor_node.getTextContent().trim()));    
            }else if(sensor_node.getNodeName().equals("enabled")){
                vid.setEnabled(Boolean.valueOf(sensor_node.getTextContent().trim()));    
            }else if(sensor_node.getNodeName().equals("position")){
                vid.setCameraStartVector(getVector(sensor_node));
            }else if(sensor_node.getNodeName().equals("CameraDirection")){
                vid.setCameraDirection(getVector(sensor_node));
            }else if(sensor_node.getNodeName().equals("CameraTopDirection")){
                vid.setCameraTopDirection(getVector(sensor_node));
            }else if(sensor_node.getNodeName().equals("CameraHeight")){
                vid.setCameraHeight(Integer.valueOf(sensor_node.getTextContent().trim()));
            }else if(sensor_node.getNodeName().equals("CameraWidth")){
                vid.setCameraWidth(Integer.valueOf(sensor_node.getTextContent().trim()));
            }else if(sensor_node.getNodeName().equals("CameraAngle")){
                vid.setCameraAngle(Float.valueOf(sensor_node.getTextContent().trim()));
            }else if(sensor_node.getNodeName().equals("Debug")){
                vid.setDebug(Boolean.valueOf(sensor_node.getTextContent().trim()));
            }
        }
        return vid;
    }

    private void getSetupAxis(Node nodes,MARS_Settings settings){
        NodeList nodelist = nodes.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node setup_node = nodelist.item(i);
            if(setup_node.getNodeName().equals("enabled")){
                settings.setSetupAxis(Boolean.valueOf(setup_node.getTextContent().trim()));
            }
        }
    }

    private void getSetupFog(Node nodes,MARS_Settings settings){
        NodeList nodelist = nodes.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node setup_node = nodelist.item(i);
            if(setup_node.getNodeName().equals("enabled")){
                settings.setSetupFog(Boolean.valueOf(setup_node.getTextContent().trim()));
            }else if(setup_node.getNodeName().equals("color")){
                settings.setFogcolor(getColor(setup_node));
            }else if(setup_node.getNodeName().equals("Distance")){
                settings.setFogDistance(Float.valueOf(setup_node.getTextContent().trim()));
            }else if(setup_node.getNodeName().equals("FogDensity")){
                settings.setFogDensity(Float.valueOf(setup_node.getTextContent().trim()));
            }
        }
    }

    private void getSetupDepthOfField(Node nodes,MARS_Settings settings){
        NodeList nodelist = nodes.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node setup_node = nodelist.item(i);
            if(setup_node.getNodeName().equals("enabled")){
                settings.setSetupDepthOfField(Boolean.valueOf(setup_node.getTextContent().trim()));
            }else if(setup_node.getNodeName().equals("BlurScale")){
                settings.setBlurScale(Float.valueOf(setup_node.getTextContent().trim()));
            }else if(setup_node.getNodeName().equals("FocusRange")){
                settings.setFocusRange(Float.valueOf(setup_node.getTextContent().trim()));
            }else if(setup_node.getNodeName().equals("FocusDistance")){
                settings.setFocusDistance(Float.valueOf(setup_node.getTextContent().trim()));
            }
        }
    }

    private void getSetupWater(Node nodes,MARS_Settings settings){
        NodeList nodelist = nodes.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node setup_node = nodelist.item(i);
            if(setup_node.getNodeName().equals("enabled")){
                settings.setSetupWater(Boolean.valueOf(setup_node.getTextContent().trim()));
            }
        }
    }

    private void getSetupWavesWater(Node nodes,MARS_Settings settings){
        NodeList nodelist = nodes.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node setup_node = nodelist.item(i);
            if(setup_node.getNodeName().equals("enabled")){
                settings.setSetupWavesWater(Boolean.valueOf(setup_node.getTextContent().trim()));
            }
        }
    }

    private void getSetupPlaneWater(Node nodes,MARS_Settings settings){
        NodeList nodelist = nodes.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node setup_node = nodelist.item(i);
            if(setup_node.getNodeName().equals("enabled")){
                settings.setSetupPlainWater(Boolean.valueOf(setup_node.getTextContent().trim()));
            }else if(setup_node.getNodeName().equals("filepath")){
                settings.setPlanewaterfilepath(setup_node.getTextContent().trim());
            }
        }
    }

    private void getSetupSkyBox(Node nodes,MARS_Settings settings){
        NodeList nodelist = nodes.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node setup_node = nodelist.item(i);
            if(setup_node.getNodeName().equals("enabled")){
                settings.setSetupSkyBox(Boolean.valueOf(setup_node.getTextContent().trim()));
            }else if(setup_node.getNodeName().equals("filepath")){
                settings.setSkyboxfilepath(setup_node.getTextContent().trim());
            }
        }
    }

    private void getSetupSimpleSkyBox(Node nodes,MARS_Settings settings){
        NodeList nodelist = nodes.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node setup_node = nodelist.item(i);
            if(setup_node.getNodeName().equals("enabled")){
                settings.setSetupSimpleSkyBox(Boolean.valueOf(setup_node.getTextContent().trim()));
            }else if(setup_node.getNodeName().equals("color")){
                settings.setSimpleskycolor(getColor(setup_node));
            }
        }
    }

    private void getSetupTerrain(Node nodes,MARS_Settings settings){
        NodeList nodelist = nodes.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node setup_node = nodelist.item(i);
            if(setup_node.getNodeName().equals("enabled")){
                settings.setSetupTerrain(Boolean.valueOf(setup_node.getTextContent().trim()));
            }else if(setup_node.getNodeName().equals("filepath_heightmap")){
                settings.setTerrainfilepath_hm(setup_node.getTextContent().trim());
            }else if(setup_node.getNodeName().equals("filepath_colormap")){
                settings.setTerrainfilepath_cm(setup_node.getTextContent().trim());
            }else if(setup_node.getNodeName().equals("position")){
                settings.setTerrain_position(getVector(setup_node));
            }else if(setup_node.getNodeName().equals("tileHeigth")){
                settings.setTileHeigth(Float.valueOf(setup_node.getTextContent().trim()));
            }
        }
    }

    private void getSetupLight(Node nodes,MARS_Settings settings){
        NodeList nodelist = nodes.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node setup_node = nodelist.item(i);
            if(setup_node.getNodeName().equals("enabled")){
                settings.setSetupLight(Boolean.valueOf(setup_node.getTextContent().trim()));
            }else if(setup_node.getNodeName().equals("color")){
                settings.setLight_color(getColor(setup_node));
            }else if(setup_node.getNodeName().equals("direction")){
                settings.setLight_direction(getVector(setup_node));
            }
        }
    }

    private void getResolution(Node nodes,MARS_Settings settings){
        NodeList nodelist = nodes.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node setup_node = nodelist.item(i);
            if(setup_node.getNodeName().equals("width")){
                settings.setResolution_Width(Integer.valueOf(setup_node.getTextContent().trim()));
            }else if(setup_node.getNodeName().equals("height")){
                settings.setResolution_Height(Integer.valueOf(setup_node.getTextContent().trim()));
            }
        }
    }

    private void getSetupWireFrame(Node nodes,MARS_Settings settings){
        NodeList nodelist = nodes.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node setup_node = nodelist.item(i);
            if(setup_node.getNodeName().equals("enabled")){
                settings.setSetupWireFrame(Boolean.valueOf(setup_node.getTextContent().trim()));
            }else if(setup_node.getNodeName().equals("color")){
                settings.setWireframecolor(getColor(setup_node));
            }
        }
    }

    private void getsetupCrossHairs(Node nodes,MARS_Settings settings){
        NodeList nodelist = nodes.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node setup_node = nodelist.item(i);
            if(setup_node.getNodeName().equals("enabled")){
                settings.setSetupCrossHairs(Boolean.valueOf(setup_node.getTextContent().trim()));
            }
        }
    }

    private void getWaypoints(Node nodes,AUV_Parameters auv_param){
        NodeList nodelist = nodes.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node AUVParametersNode = nodelist.item(i);
            if(AUVParametersNode.getNodeName().equals("enabled")){
                auv_param.setWaypoints_enabled(Boolean.valueOf(AUVParametersNode.getTextContent().trim()));
            }else if(AUVParametersNode.getNodeName().equals("color")){
                auv_param.setWaypoints_color(getColor(AUVParametersNode));
            }else if(AUVParametersNode.getNodeName().equals("visiblity")){
                auv_param.setWaypoints_visible(Boolean.valueOf(AUVParametersNode.getTextContent().trim()));
            }else if(AUVParametersNode.getNodeName().equals("updaterate")){
                auv_param.setWaypoints_updaterate(Float.valueOf(AUVParametersNode.getTextContent().trim()));
            }else if(AUVParametersNode.getNodeName().equals("maxWaypoints")){
                auv_param.setMaxWaypoints(Integer.valueOf(AUVParametersNode.getTextContent().trim()));
            }
        }
    }

    private void getSetupCam(Node nodes,MARS_Settings settings){
        NodeList nodelist = nodes.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node setup_node = nodelist.item(i);
            if(setup_node.getNodeName().equals("flyCam")){
                getflyCam(setup_node,settings);
            }else if(setup_node.getNodeName().equals("chaseCam")){
                getchaseCam(setup_node,settings);
            }
        }
    }

    private void getflyCam(Node nodes,MARS_Settings settings){
        NodeList nodelist = nodes.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node setup_node = nodelist.item(i);
            if(setup_node.getNodeName().equals("MoveSpeed")){
                settings.setFlyCamMoveSpeed(Integer.valueOf(setup_node.getTextContent().trim()));
            }
        }
    }

    private void getchaseCam(Node nodes,MARS_Settings settings){
        NodeList nodelist = nodes.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node setup_node = nodelist.item(i);
        }
    }
}
