package mars.uwCommManager.benchmarking;

import java.text.DateFormat;
import java.util.Date;

/**
 * This logger does nothing but write messages to the console.
 * 
 * @author Jasper Schwinghammer
 * @version 3.0
 */
public class SConsoleLogger implements ISLogger {

	private int logLevel;

	/**
	 * Get a logger with a defined logLevel. The logLevel must be a constant
	 * value from IPedoLogger.
	 * 
	 * @param logLevel
	 *            Log level as integer
	 */
	public SConsoleLogger(int logLevel) {
		this.logLevel = logLevel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.pedobear.IPedoLogger#debug(java.lang.String)
	 */
	@Override
	public void debug(String input) {
		if (logLevel < LOG_LEVEL_ERROR) {
			System.out.println(getDateString() + "(debug)\t" + input);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.pedobear.IPedoLogger#error(java.lang.String)
	 */
	@Override
	public void error(String input) {
		System.out.println(getDateString() + "(ERROR)\t" + input);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.pedobear.IPedoLogger#info(java.lang.String)
	 */
	@Override
	public void info(String input) {
		if (logLevel < LOG_LEVEL_DEBUG) {
			System.out.println(getDateString() + "(info)\t" + input);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.pedobear.IPedoLogger#info(java.lang.String, java.lang.String)
	 */
	@Override
	public void info(String source, String input) {
		if (logLevel < LOG_LEVEL_DEBUG) {
			this.info(source + ": " + input);
		}
	}

	/**
	 * Get formated date string
	 * 
	 * @return formated date string
	 */
	private String getDateString() {
		return "[" + (DateFormat.getDateTimeInstance().format(new Date()))
				+ "] ";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.pedobear.IPedoLogger#shutdown()
	 */
	@Override
	public void shutdown() {

	}

}
