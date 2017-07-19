package p455w0rd.danknull.init;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author p455w0rd
 *
 */
public class ModLogger {
	private static Logger LOGGER = LogManager.getLogger(ModGlobals.NAME);
	public static String LOG_PREFIX = "==========[Start /dank/null %s]========";
	public static String LOG_SUFFIX = "==========[End /dank/null %s]==========";

	public static void warn(String msg) {
		LOGGER.warn(msg);
	}

	public static void error(String msg) {
		LOGGER.error(msg);
	}

	public static void infoBegin(String headerInfo) {
		String header = String.format(LOG_PREFIX, headerInfo);
		LOGGER.info(header);
	}

	public static void infoBegin(String headerInfo, String msg) {
		String header = String.format(LOG_PREFIX, headerInfo);
		LOGGER.info(header);
		LOGGER.info(msg);
	}

	public static void infoEnd(String footerInfo) {
		String footer = String.format(LOG_SUFFIX, footerInfo);
		LOGGER.info(footer);
	}

	public static void info(String msg) {
		LOGGER.info(msg);
	}

	public static void debug(String msg) {
		LOGGER.debug(msg);
	}
}