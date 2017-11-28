package com.danielcorroto.directorius.model.searchalgorithm.impl;

import com.danielcorroto.directorius.model.searchalgorithm.SearchAlgorithm;

import ezvcard.VCard;

/**
 * Realiza la búsqueda sobre las categorías de los contactos
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class SearchAlgorithmByCategory extends SearchAlgorithm {

	@Override
	protected boolean matchSearch(VCard vcard, String text) {
		if (vcard.getCategories() == null) {
			return false;
		}
		for (String category : vcard.getCategories().getValues()) {
			if (contains(category, text)) {
				return true;
			}
		}
		return false;
	}

}
