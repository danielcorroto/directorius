package com.danielcorroto.directorius.model.comparator;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * Comparador para ordenar Strng según el idioma
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class StringLocaleComparator implements Comparator<String> {

	@Override
	public int compare(String arg0, String arg1) {
		if (arg0 == null && arg1 == null) {
			return 0;
		} else if (arg0 == null && arg1 != null) {
			return -1;
		} else if (arg0 != null && arg1 == null) {
			return 1;
		} else {
			Collator collator = Collator.getInstance(Locale.getDefault());
			return collator.compare(arg0.toLowerCase(), arg1.toLowerCase());
		}
	}

}
