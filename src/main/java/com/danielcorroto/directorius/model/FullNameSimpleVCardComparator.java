package com.danielcorroto.directorius.model;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * Comparador para ordenar VCard según su nombre completo
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class FullNameSimpleVCardComparator implements Comparator<SimpleVCard> {

	@Override
	public int compare(SimpleVCard arg0, SimpleVCard arg1) {
		String name0 = null;
		String name1 = null;
		if (arg0.getFormattedName() != null) {
			name0 = arg0.getFormattedName().getValue().toLowerCase();
		}
		if (arg1.getFormattedName() != null) {
			name1 = arg1.getFormattedName().getValue().toLowerCase();
		}

		if (name0 == null && name1 == null) {
			return 0;
		} else if (name0 == null && name1 != null) {
			return -1;
		} else if (name0 != null && name1 == null) {
			return 1;
		} else {
			Collator collator = Collator.getInstance(Locale.getDefault());
			return collator.compare(name0, name1);
		}
	}

}
