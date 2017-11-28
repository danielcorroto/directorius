package com.danielcorroto.directorius.model.searchalgorithm;

import java.text.Normalizer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.danielcorroto.directorius.model.SimpleVCard;

import ezvcard.VCard;

/**
 * Interfaz de algoritmo para realizar búsqueda sobre un contacto
 * 
 * @author Daniel Corroto Quirós
 *
 */
public abstract class SearchAlgorithm {
	/**
	 * Realiza la búsqueda sobre la lista de contactos
	 * 
	 * @param vcards
	 *            Lista de contactos
	 * @param searchTexts
	 *            Colección de cadenas que tienen que validar para que sea
	 *            válido el contacto
	 * @return Colección de contactos sencillos que cumplen las condiciones de
	 *         búsqueda. Vacío, nunca null, si no se encuentra nada
	 */
	public Set<SimpleVCard> search(Collection<VCard> vcards, Collection<String> searchTexts) {
		if (vcards == null || searchTexts == null || vcards.isEmpty() || searchTexts.isEmpty()) {
			return new HashSet<>();
		}

		Set<SimpleVCard> result = new HashSet<>();

		for (VCard vcard : vcards) {
			boolean match = true;
			Iterator<String> iterator = searchTexts.iterator();
			while (match && iterator.hasNext()) {
				String text = iterator.next();
				if (!matchSearch(vcard, text)) {
					match = false;
				}
			}
			if (match) {
				result.add(new SimpleVCard(vcard));
			}
		}

		return result;
	}

	/**
	 * Comprueba si la cadena contiene otra subcadena ambas normalizadas y case
	 * insensitive
	 * 
	 * @param str
	 *            Cadena
	 * @param substring
	 *            Subcadena buscada
	 * @return Si str contiene substring
	 */
	protected boolean contains(String str, String substring) {
		if (str == null || substring == null) {
			return false;
		}
		int index = normalizeAndLowercase(str).indexOf(normalizeAndLowercase(substring));
		return index >= 0;
	}

	/**
	 * Normaliza (quita tildes) y convierte a minúsculas
	 * 
	 * @param str
	 *            Cadena de entrada
	 * @return Cadena sin tildes y en minúsculas
	 */
	private String normalizeAndLowercase(String str) {
		String normalized = Normalizer.normalize(str, Normalizer.Form.NFD);
		String ascii = normalized.replaceAll("[^\\p{ASCII}]", "").toLowerCase();
		return ascii;
	}

	/**
	 * Indica si el contacto valida la búsqueda
	 * 
	 * @param vcard
	 *            Información del contacto
	 * @param text
	 *            Texto a buscar
	 * @return Si valida la búsqueda
	 */
	protected abstract boolean matchSearch(VCard vcard, String text);
}
