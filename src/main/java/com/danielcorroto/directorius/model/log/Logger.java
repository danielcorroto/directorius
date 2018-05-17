package com.danielcorroto.directorius.model.log;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;

import com.danielcorroto.directorius.view.Text;

/**
 * Se encarga de generar el log. Abstracción de java.util.logging.Logger
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class Logger {
	private java.util.logging.Logger javaLogger;

	private static Map<Class<?>, Logger> loggers = new HashMap<>();
	private static Handler fileHandler = null;

	public static Logger getLogger(Class<?> clazz) {
		Logger logger = loggers.get(clazz);
		if (logger == null) {
			try {
				logger = new Logger(clazz);
				loggers.put(clazz, logger);
			} catch (SecurityException | IOException e) {
				e.printStackTrace();
			}
		}
		return logger;
	}

	/**
	 * Constructor privado
	 * 
	 * @throws IOException
	 * @throws SecurityException
	 */
	private Logger(Class<?> clazz) throws SecurityException, IOException {
		if (fileHandler == null) {
			// Crea el manejador
			String filename = getLogFilename(); 
			fileHandler = new FileHandler(filename, true);
			fileHandler.setFormatter(new CustomFormatter());
			
			// Redirige salida de error
			PrintStream ps = new PrintStream(filename);
			System.setErr(ps);
		}
		javaLogger = java.util.logging.Logger.getLogger(clazz.getName());
		javaLogger.addHandler(fileHandler);
	}

	/**
	 * Genera el fichero que servirá como Logger
	 * 
	 * @return Ruta absoluta del fichero de log
	 * @throws IOException
	 */
	private String getLogFilename() throws IOException {
		String filename = Text.APP_NAME + "_" + new Date().getTime() + "_";
		return File.createTempFile(filename, ".log").getAbsolutePath();
	}

	/**
	 * Genera la cadena de log
	 * 
	 * @param level
	 *            Nivel de log
	 * @param msg
	 *            Mensaje
	 * @param thrown
	 *            Excepción mostrada (o null si no hay)
	 */
	private void log(Level level, String msg, Throwable thrown) {
		javaLogger.log(level, msg, thrown);
	}

	/**
	 * Almacena log SEVERE del mensaje
	 * 
	 * @param msg
	 *            Mensaje de log
	 */
	public void severe(String msg) {
		log(Level.SEVERE, msg, null);
	}

	/**
	 * Almacena log SEVERE del mensaje
	 * 
	 * @param msg
	 *            Mensaje de log
	 * @param thrown
	 *            Excepción
	 */
	public void severe(String msg, Throwable thrown) {
		log(Level.SEVERE, msg, thrown);
	}

	/**
	 * Almacena log WARNING del mensaje
	 * 
	 * @param msg
	 *            Mensaje de log
	 */
	public void warning(String msg) {
		log(Level.WARNING, msg, null);
	}

	/**
	 * Almacena log WARNING del mensaje
	 * 
	 * @param msg
	 *            Mensaje de log
	 * @param thrown
	 *            Excepción
	 */
	public void warning(String msg, Throwable thrown) {
		log(Level.WARNING, msg, thrown);
	}

	/**
	 * Almacena log INFO del mensaje
	 * 
	 * @param msg
	 *            Mensaje de log
	 */
	public void info(String msg) {
		log(Level.INFO, msg, null);
	}

	/**
	 * Almacena log INFO del mensaje
	 * 
	 * @param msg
	 *            Mensaje de log
	 * @param thrown
	 *            Excepción
	 */
	public void info(String msg, Throwable thrown) {
		log(Level.INFO, msg, thrown);
	}

	/**
	 * Almacena log CONFIG del mensaje
	 * 
	 * @param msg
	 *            Mensaje de log
	 */
	public void config(String msg) {
		log(Level.CONFIG, msg, null);
	}

	/**
	 * Almacena log CONFIG del mensaje
	 * 
	 * @param msg
	 *            Mensaje de log
	 * @param thrown
	 *            Excepción
	 */
	public void config(String msg, Throwable thrown) {
		log(Level.CONFIG, msg, thrown);
	}

	/**
	 * Almacena log FINE del mensaje
	 * 
	 * @param msg
	 *            Mensaje de log
	 */
	public void fine(String msg) {
		log(Level.FINE, msg, null);
	}

	/**
	 * Almacena log FINE del mensaje
	 * 
	 * @param msg
	 *            Mensaje de log
	 * @param thrown
	 *            Excepción
	 */
	public void fine(String msg, Throwable thrown) {
		log(Level.FINE, msg, thrown);
	}

	/**
	 * Almacena log FINER del mensaje
	 * 
	 * @param msg
	 *            Mensaje de log
	 */
	public void finer(String msg) {
		log(Level.FINER, msg, null);
	}

	/**
	 * Almacena log FINER del mensaje
	 * 
	 * @param msg
	 *            Mensaje de log
	 * @param thrown
	 *            Excepción
	 */
	public void finer(String msg, Throwable thrown) {
		log(Level.FINER, msg, thrown);
	}

	/**
	 * Almacena log FINEST del mensaje
	 * 
	 * @param msg
	 *            Mensaje de log
	 */
	public void finest(String msg) {
		log(Level.FINEST, msg, null);
	}

	/**
	 * Almacena log FINEST del mensaje
	 * 
	 * @param msg
	 *            Mensaje de log
	 * @param thrown
	 *            Excepción
	 */
	public void finest(String msg, Throwable thrown) {
		log(Level.FINEST, msg, thrown);
	}
}
