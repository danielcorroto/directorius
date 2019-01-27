package com.danielcorroto.directorius.model;

import java.util.Collection;
import java.util.Locale;
import java.util.ResourceBundle;

import com.danielcorroto.directorius.view.Text;
import com.danielcorroto.directorius.view.UTF8Control;

/**
 * Utilidades
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class Utils {

	/**
	 * Indica si la cadena es vacía o null o solo tiene blancos. Copiado de
	 * org.apache.commons.lang3.StringUtils
	 * 
	 * @param s
	 *            Si la cadena es vacía o null o solo tiene blancos
	 */
	public static boolean isBlank(final CharSequence cs) {
		int strLen;
		if (cs == null || (strLen = cs.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if (Character.isWhitespace(cs.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Indica si la colección está vacía o es null
	 * 
	 * @param collection
	 *            Colección de elementos
	 * @return Si la colección está vacía o es null
	 */
	public static boolean isEmpty(final Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	/**
	 * Obtiene el recurso ResourceBundle para i18n
	 * 
	 * @return Objeto para gestionar i18n
	 */
	public static ResourceBundle getResourceBundle() {
		return ResourceBundle.getBundle(Text.RESOURCE_BUNDLE, Locale.getDefault(), new UTF8Control());
	}
}
