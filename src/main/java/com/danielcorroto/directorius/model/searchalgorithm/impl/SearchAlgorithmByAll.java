package com.danielcorroto.directorius.model.searchalgorithm.impl;

import com.danielcorroto.directorius.model.CustomParameter;
import com.danielcorroto.directorius.model.searchalgorithm.SearchAlgorithm;

import ezvcard.VCard;
import ezvcard.property.Address;
import ezvcard.property.Email;
import ezvcard.property.Note;
import ezvcard.property.Telephone;

/**
 * Realiza la búsqueda sobre la información de los contactos: nombre, notas,
 * categorías, teléfono, correo y dirección
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class SearchAlgorithmByAll extends SearchAlgorithm {

	@Override
	protected boolean matchSearch(VCard vcard, String text) {
		// Nombre
		if (vcard.getFormattedName() != null && vcard.getFormattedName().getValue() != null && contains(vcard.getFormattedName().getValue(), text)) {
			return true;
		}

		// Notas
		if (vcard.getNotes() != null) {
			for (Note note : vcard.getNotes()) {
				if (note != null && note.getValue() != null && contains(note.getValue(), text)) {
					return true;
				}
			}
		}

		// Categorías
		if (vcard.getCategories() != null) {
			for (String category : vcard.getCategories().getValues()) {
				if (category != null && contains(category, text)) {
					return true;
				}
			}
		}

		// Teléfono
		if (vcard.getTelephoneNumbers() != null) {
			for (Telephone telephone : vcard.getTelephoneNumbers()) {
				if (telephone != null) {
					if (telephone.getText() != null && contains(telephone.getText(), text)) {
						return true;
					}
					if (contains(telephone.getParameter(CustomParameter.TELEPHONE_TAG), text)) {
						return true;
					}
				}
			}
		}

		// Correo
		if (vcard.getEmails() != null) {
			for (Email email : vcard.getEmails()) {
				if (email != null) {
					if (email.getValue() != null && contains(email.getValue(), text)) {
						return true;
					}
					if (contains(email.getParameter(CustomParameter.EMAIL_TAG), text)) {
						return true;
					}
				}
			}
		}

		// Dirección
		if (vcard.getAddresses() != null) {
			for (Address address : vcard.getAddresses()) {
				if (address.getStreetAddress() != null && contains(address.getStreetAddress(), text)) {
					return true;
				}
				if (address.getLocality() != null && contains(address.getLocality(), text)) {
					return true;
				}
				if (address.getRegion() != null && contains(address.getRegion(), text)) {
					return true;
				}
				if (address.getPostalCode() != null && contains(address.getPostalCode(), text)) {
					return true;
				}
				if (address.getCountry() != null && contains(address.getCountry(), text)) {
					return true;
				}
			}
		}

		return false;
	}

}
