package mars.uwCommManager.benchmarking;

import java.io.*;
import java.util.*;
import java.text.DateFormat;

/**
 * A basic logging tool.
 * 
 * Should do the FilenotFoundException handling on it's own and should throw
 * another own exception.
 * 
 * @author Jodder
 * @version 1.0
 */
public final class SLogger extends java.io.PrintWriter implements
		ISLogger {

	private int mode;
	private int loggingLevel;

	/**
	 * Creates a logger and leaves it open to append to a existing logfiles.
	 * 
	 * @since 1.0
	 * @param fileName
	 *            String with the Filename for the Logger
	 * @param append
	 *            boolean weather to append to the file
	 * @param mode
	 *            the loggingmode must be a constant value from IPedoLogger
	 * @param loggingLevel
	 *            loggin level
	 * @throws FileNotFoundException
	 *             die exception DIE!
	 * 
	 */
	private SLogger(String fileName, boolean append, int mode,
			int loggingLevel) throws FileNotFoundException {
		super((OutputStream) new FileOutputStream(fileName, append), true);
		this.mode = mode;
		this.loggingLevel = loggingLevel;
		info("Logger initialized");
	}

	/**
	 * Does the same as the other constructor, but doesn't append
	 * 
	 * @param fileName
	 *            File name
	 * @param mode
	 *            Logging mode
	 * @param loggingLevel
	 *            Logging level
	 * @throws FileNotFoundException
	 *             Thrown, if file could not found
	 */
	private SLogger(String fileName, int mode, int loggingLevel)
			throws FileNotFoundException {
		super((OutputStream) new FileOutputStream(fileName), false);
		this.mode = mode;
		this.loggingLevel = loggingLevel;
		info("Logger initialized");
		// System.out.println("["+(DateFormat.getDateTimeInstance().format(new
		// Date()))+"]"+ " (info)\t Logger initialized" );
		// println("["+(DateFormat.getDateTimeInstance().format(new
		// Date()))+"]"+ " (info)\t Logger initialized" );
	}

	/**
	 * New logger. For there is not-trivial operation in the Logger this is not
	 * done in the constructor. This method can append
	 * 
	 * @since 1.0
	 * @param filename
	 *            the filename for the logger
	 * @param append
	 *            bool wether it should be appended
	 * @param loggingLevel
	 *            the loggingLevel from IPedoLogger
	 * @param mode
	 *            of the logger
	 * @return the new logger
	 * @throws FileNotFoundException
	 *             Thrown, if file not found
	 * 
	 */
	public static ISLogger getNewLogger(String filename, boolean append,
			int mode, int loggingLevel) throws FileNotFoundException {
		String temp;
		if (filename.isEmpty()) {
			temp = "untitled";
		} else {
			temp = filename;
		}
		return (new SLogger(temp + ".pbl", append, mode, loggingLevel));
	}

	/**
	 * New logger without appending. For there is not-trivial operation in the
	 * Logger this is not done in the constructor.
	 * 
	 * @since 1.0
	 * @param filename
	 *            the filename for the logger
	 * @param loggingLevel
	 *            the loggingLevel from IPedoLogger
	 * @param mode
	 *            Logging mode
	 * @return the new logger
	 * @throws FileNotFoundException
	 *             Thrown, if file not found
	 * 
	 */
	public static ISLogger getNewLogger(String filename, int mode,
			int loggingLevel) throws FileNotFoundException {
		String temp;
		if (filename == "") {
			temp = "untitled";
		} else {
			temp = filename;
		}
		return (new SLogger(temp + ".pbl", mode, loggingLevel));
	}

	/* (non-Javadoc)
	 * @see core.pedobear.IPedoLogger#debug(java.lang.String)
	 */
	@Override
	public void debug(String input) {
		if (loggingLevel < LOG_LEVEL_ERROR) {
			if (mode != LOG_ONLY) {
				System.out.println(getDateString() + "(debug)\t" + input);
			}
			println(getDateString() + "(debug)\t" + input);
		}
	}

	/* (non-Javadoc)
	 * @see core.pedobear.IPedoLogger#error(java.lang.String)
	 */
	@Override
	public void error(String input) {
		if (mode != LOG_ONLY) {
			System.out.println(getDateString() + "(ERROR)\t" + input);
		}
		println(getDateString() + "(ERROR)\t" + input);
	}

	/* (non-Javadoc)
	 * @see core.pedobear.IPedoLogger#info(java.lang.String)
	 */
	@Override
	public void info(String input) {
		if (loggingLevel < LOG_LEVEL_DEBUG) {
			if (mode != LOG_ONLY) {
				System.out.println(getDateString() + "(info)\t" + input);
			}
			println(getDateString() + "(info)\t" + input);
		}
	}

	/* (non-Javadoc)
	 * @see core.pedobear.IPedoLogger#info(java.lang.String, java.lang.String)
	 */
	@Override
	public void info(String source, String input) {
		if (loggingLevel < LOG_LEVEL_DEBUG) {
			this.info(source + ": " + input);
		}
	}

	private String getDateString() {
		return "[" + (DateFormat.getDateTimeInstance().format(new Date()))
				+ "] ";
	}

	/* (non-Javadoc)
	 * @see core.pedobear.IPedoLogger#shutdown()
	 */
	@Override
	public void shutdown() {

		close();
	}
}
