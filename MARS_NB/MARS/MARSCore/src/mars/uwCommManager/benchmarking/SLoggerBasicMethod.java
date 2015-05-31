/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.benchmarking;

import java.util.Properties;

/**
 * @version 0.1
 * @author Jasper Schwinghammer
 */
public class SLoggerBasicMethod {
     /**
     * log the basic system-informations for debugging purpose
     * @since 0.1
     */
    public static void LogSystemInformation(ISLogger debugLog, final String projectName, final String Version) {
	Properties systemProps = System.getProperties();
	debugLog.info("Java RE Version: "
		+ systemProps.getProperty("java.version"));
	debugLog.info("Java RE Vendor: "
		+ systemProps.getProperty("java.vendor"));
	debugLog.info("Java installation directory: "
		+ systemProps.getProperty("java.home"));
	debugLog.info("Java VM Version: "
		+ systemProps.getProperty("java.vm.version"));
	debugLog.info("OS Name: " + systemProps.getProperty("os.name"));
	debugLog.info("OS Version: " + systemProps.getProperty("os.version"));
        debugLog.info("OS Architecture: "+ systemProps.getProperty("os.arch"));
	debugLog.info("Running as User: "
		+ systemProps.getProperty("user.name"));
	//debugLog.info("user home dir " + systemProps.getProperty("user.home"));
	//debugLog.info("user current working dir: "
		//+ systemProps.getProperty("user.dir"));
	debugLog.info(projectName + " Version " + Version);
    }
    
}
