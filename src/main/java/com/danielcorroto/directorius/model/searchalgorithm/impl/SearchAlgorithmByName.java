package com.danielcorroto.directorius.model.searchalgorithm.impl;

import com.danielcorroto.directorius.model.searchalgorithm.SearchAlgorithm;

import ezvcard.VCard;

/**
 * Realiza la búsqueda sobre el nombre de los contactos
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class SearchAlgorithmByName extends SearchAlgorithm {

	@Override
	protected boolean matchSearch(VCard vcard, String text) {
		return vcard.getFormattedName() != null && vcard.getFormattedName().getValue() != null && contains(vcard.getFormattedName().getValue(), text);
	}

}
