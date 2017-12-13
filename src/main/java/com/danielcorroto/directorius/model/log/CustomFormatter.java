package com.danielcorroto.directorius.model.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Formateador del logger de la cadena que se imprime
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class CustomFormatter extends Formatter {
	/**
	 * Patrón de la fecha
	 */
	private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
	/**
	 * Objeto para establecer la fecha
	 */
	private Date date = new Date();
	/**
	 * Formateador de la fecha
	 */
	private SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
	/**
	 * Salto de línea
	 */
	private String lineSeparator = java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction("line.separator"));

	@Override
	public String format(LogRecord record) {
		StringBuffer sb = new StringBuffer();
		sb.append(sdf.format(date)).append(" ");
		sb.append(record.getLevel()).append(" ");
		sb.append(record.getLoggerName()).append(" ");
		sb.append(record.getMessage());
		sb.append(lineSeparator);
		if (record.getThrown() != null) {
			try {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				record.getThrown().printStackTrace(pw);
				pw.close();
				sb.append(sw.toString());
			} catch (Exception ex) {
			}
		}
		return sb.toString();
	}

}
