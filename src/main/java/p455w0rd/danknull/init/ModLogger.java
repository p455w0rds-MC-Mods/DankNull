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

	public static void warn(final String msg) {
		LOGGER.warn(msg);
	}

	public static void error(final String msg) {
		LOGGER.error(msg);
	}

	public static void infoBegin(final String headerInfo) {
		final String header = String.format(LOG_PREFIX, headerInfo);
		LOGGER.info(header);
	}

	public static void infoBegin(final String headerInfo, final String msg) {
		final String header = String.format(LOG_PREFIX, headerInfo);
		LOGGER.info(header);
		LOGGER.info(msg);
	}

	public static void infoEnd(final String footerInfo) {
		final String footer = String.format(LOG_SUFFIX, footerInfo);
		LOGGER.info(footer);
	}

	public static void info(final String msg) {
		LOGGER.info(msg);
	}

	public static void debug(final String msg) {
		LOGGER.debug(msg);
	}

	public static void debug(final String msg, final Object... format) {
		LOGGER.debug(String.format(msg, format));
	}
}