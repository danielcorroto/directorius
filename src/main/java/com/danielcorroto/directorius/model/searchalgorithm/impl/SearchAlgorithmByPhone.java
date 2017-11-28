package com.danielcorroto.directorius.model.searchalgorithm.impl;

import com.danielcorroto.directorius.model.searchalgorithm.SearchAlgorithm;

import ezvcard.VCard;
import ezvcard.property.Telephone;

/**
 * Realiza la búsqueda sobre el número de teléfono (solo dígitos) de los
 * contactos
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class SearchAlgorithmByPhone extends SearchAlgorithm {

	@Override
	protected boolean matchSearch(VCard vcard, String text) {
		if (vcard.getTelephoneNumbers() == null) {
			return false;
		}
		for (Telephone telephone : vcard.getTelephoneNumbers()) {
			String cleanText = cleanTelephoneNumber(text);
			String cleanNumber = cleanTelephoneNumber(telephone.getText());
			if (cleanNumber.indexOf(cleanText) >= 0) {
				return true;
			}

		}
		return false;
	}

	/**
	 * Elimina todos los caracteres que no son un dígito
	 * 
	 * @param telephoneNumber
	 *            Número de teléfono con no dígitos: espacios, guiones, etc
	 * @return Número de teléfono solo con dígitos
	 */
	private String cleanTelephoneNumber(String telephoneNumber) {
		String clean = telephoneNumber.replaceAll("[^0-9]+", "");
		return clean;
	}
}
