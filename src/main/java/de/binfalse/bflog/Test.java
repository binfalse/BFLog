/**
 * 
 */
package de.binfalse.bflog;

import java.io.IOException;

/**
 * @author Martin Scharm
 *
 */
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		LOGGER.error("message", new IOException ("exceptionmsg"));
		LOGGER.error("test");
		LOGGER.setLogStackTrace(true);
		LOGGER.setLogToStdErr(false);
		LOGGER.setLogToStdOut(true);
		LOGGER.error("message", new IOException ("exceptionmsg"));
		LOGGER.error("test");

	}

}
