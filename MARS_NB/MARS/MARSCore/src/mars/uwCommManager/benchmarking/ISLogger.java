package mars.uwCommManager.benchmarking;

/**
 * A basic logging tool.
 * 
 * Should do the FilenotFoundException handling on it's own and should throw
 * another own exception.
 * 
 * @author Jodder
 * @version 1.0
 */
public interface ISLogger{

	public static final int LOG_AND_CONSOLE = 5;
	public static final int LOG_ONLY = 6;
	public static final int CONSOLE_ONLY = 7;

	public static final int LOG_LEVEL_INFO = 10;
	public static final int LOG_LEVEL_DEBUG = 11;
	public static final int LOG_LEVEL_ERROR = 12;

	/**
	 * Print a Debug string
	 * 
	 * @param input
	 *            some words about the debug message
	 */
	public abstract void debug(String input);

	/**
	 * Print an Error string
	 * 
	 * @param input
	 *            some words about the error message
	 */
	public abstract void error(String input);

	/**
	 * Print an Info string
	 * 
	 * @param input
	 *            some words about the info message
	 */
	public abstract void info(String input);

	/**
	 * Info string with two parameters
	 * 
	 * @param source
	 *            Source of the log message
	 * @param input
	 *            log message
	 */
	public abstract void info(String source, String input);

	/**
	 * destroy the print writers if there are any
	 */
	public abstract void shutdown();

}