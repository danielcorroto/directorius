package com.danielcorroto.directorius;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.danielcorroto.directorius.model.SimpleVCard;

/**
 * Utilidades para los test
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class TestUtil {

	/**
	 * Construye un set de cadenas a partir de la entrada
	 * 
	 * @param elements
	 *            Cadenas
	 * @return Set de cadenas
	 */
	public static Set<String> buildSet(String... elements) {
		Set<String> result = new HashSet<>();
		for (String element : elements) {
			result.add(element);
		}
		return result;
	}

	/**
	 * Crea una colección de nombres a partir de una lista de contactos sencilla
	 * 
	 * @param cards
	 *            Colección de contactos
	 * @return Set de nombres
	 */
	public static Set<String> getNamesFromSimpleVCard(Collection<SimpleVCard> cards) {
		Set<String> names = new HashSet<>();
		for (SimpleVCard simple : cards) {
			names.add(simple.getFormattedName().getValue());
		}
		return names;
	}
}
