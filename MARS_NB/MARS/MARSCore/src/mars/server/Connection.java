/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.server;

import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import mars.auv.AUV_Manager;
import mars.auv.BasicAUV;
import mars.MARS_Main;
import mars.actuators.Thruster;
import mars.auv.CommunicationManager;
import mars.sensors.Accelerometer;
import mars.sensors.Compass;
import mars.sensors.Gyroscope;
import mars.sensors.sonar.ImagenexSonar_852_Echo;
import mars.sensors.sonar.ImagenexSonar_852_Scanning;
import mars.sensors.PingDetector;
import mars.sensors.PressureSensor;
import mars.sensors.TemperatureSensor;
import mars.sensors.UnderwaterModem;
import mars.sensors.VideoCamera;

/**
 *
 * @author Thomas Tosik
 */
public class Connection extends Thread {

    private Socket sockConnected;
    private MARS_Main mars;
    private BasicAUV auv;
    private AUV_Manager auv_manager;
    private CommunicationManager com_manager;

    private int OutputStreamSize = 1228100;

    private MARS_OutputStream out;

    //Commands
    private static final String cmd_hello = "HELLO__________";
    private static final String cmd_hello_data = "SIMAUV CLIENT";
    private static final String cmd_auvid = "AUV_ID_________";
    private static final String cmd_badauvid = "AUV_NOT_EXIST__";
    private static final String cmd_stop = "STOPPED________";
    private static final String cmd_temp = "Temp__________";
    private static final String cmd_depth = "Depth__________";
    private static final String cmd_thruster = "Thruster_______";
    private static final String cmd_acceleration = "Acceleration___";
    private static final String cmd_gyroscope = "Gyroscope______";
    private static final String cmd_camera = "Camera_________";
    private static final String cmd_sonar = "Sonar360_______";
    private static final String cmd_sonar2 = "SonarEcho______";
    private static final String cmd_angles = "Angles_________";
    private static final String cmd_imu = "IMU____________";
    private static final String cmd_pinger = "PINGER_________";
    private static final String cmd_comm = "COMM___________";

    /**
     * 
     * @param sockConnected
     * @param mars 
     * @param auv_manager
     * @param com_manager  
     */
    public Connection(Socket sockConnected,MARS_Main mars, AUV_Manager auv_manager, CommunicationManager com_manager) {
        //set the logging
        /*
        try {
            // Create an appending file handler
            boolean append = true;
            FileHandler handler = new FileHandler(this.getClass().getName() + ".log", append);
            // Add to the desired logger
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.addHandler(handler);
        } catch (IOException e) { }
        */
        this.mars = mars;
        this.auv_manager = auv_manager;
        this.com_manager = com_manager;
        this.sockConnected = sockConnected;
    }

    @Override
    public void run() {
        try{
            Scanner in  = new Scanner( sockConnected.getInputStream() );
            out = new MARS_OutputStream(sockConnected.getOutputStream(),OutputStreamSize);
            for( ;; ) {
                //take the input
                String input_from_client = "";
                try{
                    input_from_client = in.nextLine();
                } catch (java.util.NoSuchElementException ex){
                    Logger.getLogger(mars.server.MARS_Server.class.getName()).log(Level.SEVERE, "NO INPUT", "");
                    break;
                }
                System.out.println(input_from_client);

                //parse the input
                boolean stop_connection = parse_input(input_from_client,out);
                if(stop_connection){
                     break;
                }
            }
            sockConnected.close();
            Logger.getLogger(mars.server.MARS_Server.class.getName()).log(Level.INFO, "Connection " + sockConnected + " closed...", "");
        }catch( IOException e ) {
            Logger.getLogger(mars.server.MARS_Server.class.getName()).log(Level.SEVERE, e.toString(), "");
        }
    }
    
    /**
     * 
     */
    public synchronized void testlock(){
        System.out.println("LOCK" + getName() + "!!!!!!!!!!!!!!!!!");
        sendString(cmd_temp,"test","000");
    }
    
    /**
     * 
     * @return
     */
    public boolean hasUnderwaterCommunication(){
        if(auv != null){
            return auv.hasSensorsOfClass(UnderwaterModem.class.getName());
        }else{
            return false;
        }
    }

    /**
     * 
     * @param cmd
     * @param physicalexchanger_name
     * @param data
     */
    public void sendString(String cmd, String physicalexchanger_name, String data){
        synchronized(out){
            try {
                byte[] send_string_length = Converter.convertIntToByteArray(cmd.length()*2+4+physicalexchanger_name.length()*2+4+data.length()*2+4);
                out.write(send_string_length);
                out.write(Converter.convertIntToUTF16ByteArray(cmd.length()));
                out.writeQT(cmd.getBytes("UTF-16"));
                out.write(Converter.convertIntToUTF16ByteArray(physicalexchanger_name.length()));
                out.writeQT(physicalexchanger_name.getBytes("UTF-16"));
                out.write(Converter.convertIntToUTF16ByteArray(data.length()));
                out.writeQT(data.getBytes("UTF-16"));
                out.flush();
            } catch (IOException ex) {
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void sendByteArray(String cmd, String physicalexchanger_name, byte[] data){
        synchronized(out){
            try {
                byte[] send_string_length = Converter.convertIntToByteArray(cmd.length()*2+4+physicalexchanger_name.length()*2+4+data.length+4);
                out.write(send_string_length);
                out.write(Converter.convertIntToUTF16ByteArray(cmd.length()));
                out.writeQT(cmd.getBytes("UTF-16"));
                out.write(Converter.convertIntToUTF16ByteArray(physicalexchanger_name.length()));
                out.writeQT(physicalexchanger_name.getBytes("UTF-16"));
                out.write(Converter.convertIntToByteArray(data.length));
                out.write(data);
                out.flush();
            } catch (IOException ex) {
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /*
     * We take a string that we get from the client and parse it.
     * After that we call the methods that are requested.
     */
    private boolean parse_input(String input_from_client,MARS_OutputStream out){
        //get the sensors data, write the actuator data
        if(input_from_client.startsWith("Sonar360")){
            String[] input = input_from_client.split(" ");
            String son_name = input[1];
            final ImagenexSonar_852_Scanning son = (ImagenexSonar_852_Scanning)auv.getSensor(son_name);

            if(son != null){
                //we have to use callables here because we change the some nodes in the rootNode in the
                //getMultiRaySonarData method. rotation and stuff. and because we are in a different thread
                //we must synchronize with the opengl trender thread
                Future fut = mars.enqueue(new Callable() {
                    public byte[] call() throws Exception {
                        return son.getData();
                    }
                });

                byte[] sondat = new byte[son.getSonarReturnDataTotalLength()];
                try {
                    sondat = (byte[]) fut.get();
                } catch (InterruptedException ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }
                sendByteArray(cmd_sonar,son_name,sondat);
            }else{
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE,"Son doesnt exist!", "");
            }
        }else if(input_from_client.startsWith("SonarEcho")){
            String[] input = input_from_client.split(" ");
            String son_name = input[1];
            final ImagenexSonar_852_Echo son = (ImagenexSonar_852_Echo)auv.getSensor(son_name);

            if(son != null){
                //we have to use callables here because we change the some nodes in the rootNode in the
                //getMultiRaySonarData method. rotation and stuff. and because we are in a different thread
                //we must synchronize with the opengl trender thread
                Future fut = mars.enqueue(new Callable() {
                    public byte[] call() throws Exception {
                        return son.getData();
                    }
                });

                byte[] sondat = new byte[son.getSonarReturnDataTotalLength()];
                try {
                    sondat = (byte[]) fut.get();
                } catch (InterruptedException ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }
                sendByteArray(cmd_sonar2,son_name,sondat);
            }else{
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE,"Son doesnt exist!", "");
            }
        }
        else if(input_from_client.startsWith("Depth")){
            String[] input = input_from_client.split(" ");
            String depth_name = input[1];
            PressureSensor press = (PressureSensor)auv.getSensor(depth_name);
            if(press != null){
                String ret = "" + press.getDepth();
                sendString(cmd_depth,depth_name,ret);
            }else{
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE,"Press doesnt exist!", "");
            }
        }
        else if (input_from_client.startsWith("Temp")) {
            String[] input = input_from_client.split(" ");
            String temp_name = input[1];
            TemperatureSensor temp = (TemperatureSensor)auv.getSensor(temp_name);
            if(temp != null){
                String ret = " " + temp.getTemperature();
                sendString(cmd_temp,temp_name,ret);
            }else{
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE,"Temp doesnt exist!", "");
            }
        }else if(input_from_client.startsWith("Thruster")){
            String[] ret = input_from_client.split(" ");
            int retint = Integer.valueOf(ret[2]).intValue();

            Thruster act = (Thruster)auv.getActuator(ret[1]);
            if(act != null){
                act.set_thruster_speed(retint);
                sendString(cmd_thruster,ret[1],String.valueOf(retint));
            }else{
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE,"Thruster with name \"" + ret[1] + "\" doesnt exist!", "");
            }
        }else if (input_from_client.startsWith("Comm")) {
            String[] input = input_from_client.split(" ");
            String comm_name = input[1];
            String comm_msg = input[2];
            //com_manager.putMsg(comm_msg);
            com_manager.putMsg(auv.getName(), comm_msg);
            //UnderwaterModem comm = (UnderwaterModem)auv.getSensor(comm_name);
            /*if(comm != null){
                String ret = " " + comm.getMessage();
                sendString(cmd_temp,comm_name,ret);
            }else{
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE,"Comm doesnt exist!", "");
            }*/
        }else if(input_from_client.startsWith("Angles")){
            String[] input = input_from_client.split(" ");
            String comp_name = input[1];
            Compass comp = (Compass)auv.getSensor(comp_name);
            if(comp != null){
                String ret = " " + comp.getYawDegree() + " " + comp.getPitchDegree() + " " + comp.getRollDegree();
                sendString(cmd_angles,comp_name,ret);
            }else{
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE,"Compass doesnt exist!", "");
            }
        }else if(input_from_client.startsWith("Pinger")){
            String[] input = input_from_client.split(" ");
            String ping_name = input[1];
            PingDetector ping = (PingDetector)auv.getSensor(ping_name);
            if(ping != null){
                String ret = " " + ping.getPingerAngleRadiant("pingpong");
                sendString(cmd_pinger,ping_name,ret);
            }else{
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE,"Compass doesnt exist!", "");
            }
        }else if(input_from_client.startsWith("Acceleration")){
            String[] input = input_from_client.split(" ");
            String acc_name = input[1];
            Accelerometer acc = (Accelerometer)auv.getSensor(acc_name);

            if(acc != null){
                String ret = " " + acc.getAcceleration().x + " " + acc.getAcceleration().z + " " + acc.getAcceleration().y;
                sendString(cmd_acceleration,acc_name,ret);
            }else{
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE,"Acc doesnt exist!", "");
            }
        }else if(input_from_client.startsWith("Gyroscope")){
            String[] input = input_from_client.split(" ");
            String gyro_name = input[1];
            Gyroscope gyro = (Gyroscope)auv.getSensor(gyro_name);

            if(gyro != null){
                String ret = " " + gyro.getAngularVelocity().x + " " + gyro.getAngularVelocity().z + " " + gyro.getAngularVelocity().y;
                sendString(cmd_gyroscope,gyro_name,ret);
            }else{
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE,"Gyro doesnt exist!", "");
            }
        }else if(input_from_client.startsWith("IMU")){
            Accelerometer acc = (Accelerometer)auv.getSensor("accelerometer");
            Gyroscope gyro = (Gyroscope)auv.getSensor("gyroscope");

            if(gyro != null && acc != null){
                String ret = " " + acc.getAcceleration().x + " " + acc.getAcceleration().z + " " + acc.getAcceleration().y + " " + gyro.getAngularVelocity().x + " " + gyro.getAngularVelocity().z + " " + gyro.getAngularVelocity().y;
                sendString(cmd_imu,"imu2000",ret);
            }else{
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE,"Gyro and Acc doesnt exist!", "");
            }
        }else if(input_from_client.startsWith("Camera")){
            byte[] ret = new byte[1228800];
            String[] input = input_from_client.split(" ");
            String cam_name = input[1];
            final VideoCamera vcam = (VideoCamera)auv.getSensor(cam_name);

            if(vcam != null){
                Future fut = mars.enqueue(new Callable() {
                    public byte[] call() throws Exception {
                        return vcam.getImage();
                    }
                });

                try {
                    ret = (byte[])fut.get();
                } catch (InterruptedException ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }
                sendByteArray(cmd_camera,cam_name,ret);
            }else{
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE,"Vcam doesnt exist!", "");
            }
        }else if(input_from_client.startsWith("HELLO")){
            try {
                byte[] send_string_length = Converter.convertIntToByteArray(cmd_hello.length()*2+cmd_hello_data.length()*2+8);
                out.write(send_string_length);
                out.write(Converter.convertIntToUTF16ByteArray(cmd_hello.length()));
                out.writeQT(cmd_hello.getBytes("UTF-16"));
                out.write(Converter.convertIntToUTF16ByteArray(cmd_hello_data.length()));
                out.writeQT(cmd_hello_data.getBytes("UTF-16"));
                out.flush();
            } catch (IOException ex) {
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            }

        }else if(input_from_client.startsWith("AUV_ID")){
            String[] ret = input_from_client.split(" ");
            auv = (BasicAUV)auv_manager.getAUV(ret[1]);
            if(auv != null){
                try {
                    byte[] send_string_length = Converter.convertIntToByteArray(cmd_auvid.length()*2+4+ret[1].getBytes().length*2+4);
                    out.write(send_string_length);
                    out.write(Converter.convertIntToUTF16ByteArray(cmd_auvid.length()));
                    out.writeQT(cmd_auvid.getBytes("UTF-16"));
                    out.write(Converter.convertIntToUTF16ByteArray(ret[1].getBytes().length));
                    out.writeQT(ret[1].getBytes("UTF-16"));
                    out.flush();
                } catch (IOException ex) {
                    Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
                try {
                    byte[] send_string_length = Converter.convertIntToByteArray(cmd_badauvid.length()*2+4);
                    out.write(send_string_length);
                    out.write(Converter.convertIntToUTF16ByteArray(cmd_badauvid.length()));
                    out.writeQT(cmd_badauvid.getBytes("UTF-16"));
                    out.flush();
                } catch (IOException ex) {
                    Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }else if(input_from_client.startsWith("STOP")){
            try {
                byte[] send_string_length = Converter.convertIntToByteArray(cmd_stop.length()*2+4);
                out.write(send_string_length);
                out.write(Converter.convertIntToUTF16ByteArray(cmd_stop.length()));
                out.writeQT(cmd_stop.getBytes("UTF-16"));
                out.flush();
            } catch (IOException ex) {
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            }
            return true;
        }else{// we got a request from client but dont know what to do with it
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, "Command from client not found", input_from_client);
        }
        return false;
    }

    /**
     * 
     * @param OUTPUT_STREAM_SIZE
     */
    public void setOUTPUT_STREAM_SIZE(int OUTPUT_STREAM_SIZE) {
        this.OutputStreamSize = OUTPUT_STREAM_SIZE;
    }

}
