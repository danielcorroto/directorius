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
		if (searchName(vcard, text)) {
			return true;
		}

		// Notas
		if (searchNotes(vcard, text)) {
			return true;
		}

		// Categorías
		if (searchCategory(vcard, text)) {
			return true;
		}

		// Teléfono
		if (searchPhone(vcard, text)) {
			return true;
		}

		// Correo
		if (searchEmail(vcard, text)) {
			return true;
		}

		// Dirección
		return searchAddress(vcard, text);
	}

	/**
	 * Realiza la búsqueda sobre el nombre / apodo
	 * 
	 * @param vcard
	 *            Información del contacto
	 * @param text
	 *            Texto a buscar
	 * @return Si valida la búsqueda
	 */
	private boolean searchName(VCard vcard, String text) {
		// Nombre completo
		if (vcard.getFormattedName() != null && vcard.getFormattedName().getValue() != null && normalizeContains(vcard.getFormattedName().getValue(), text)) {
			return true;
		}

		// Solo nombre
		if (vcard.getStructuredName() != null && vcard.getStructuredName().getGiven() != null && normalizeContains(vcard.getStructuredName().getGiven(), text)) {
			return true;
		}

		// Solo apellido
		if (vcard.getStructuredName() != null && vcard.getStructuredName().getFamily() != null && normalizeContains(vcard.getStructuredName().getFamily(), text)) {
			return true;
		}

		// Apodo
		if (vcard.getNickname() != null && vcard.getNickname().getValues() != null) {
			for (String nickname : vcard.getNickname().getValues()) {
				if (normalizeContains(nickname, text)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Realiza la búsqueda sobre las notas
	 * 
	 * @param vcard
	 *            Información del contacto
	 * @param text
	 *            Texto a buscar
	 * @return Si valida la búsqueda
	 */
	private boolean searchNotes(VCard vcard, String text) {
		if (vcard.getNotes() == null) {
			return false;
		}
		for (Note note : vcard.getNotes()) {
			if (note != null && note.getValue() != null && normalizeContains(note.getValue(), text)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Realiza la búsqueda sobre las categorías
	 * 
	 * @param vcard
	 *            Información del contacto
	 * @param text
	 *            Texto a buscar
	 * @return Si valida la búsqueda
	 */
	private boolean searchCategory(VCard vcard, String text) {
		if (vcard.getCategories() == null) {
			return false;
		}
		for (String category : vcard.getCategories().getValues()) {
			if (category != null && normalizeContains(category, text)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Realiza la búsqueda sobre el teléfono
	 * 
	 * @param vcard
	 *            Información del contacto
	 * @param text
	 *            Texto a buscar
	 * @return Si valida la búsqueda
	 */
	private boolean searchPhone(VCard vcard, String text) {
		if (vcard.getTelephoneNumbers() == null) {
			return false;
		}
		for (Telephone telephone : vcard.getTelephoneNumbers()) {
			if (telephone != null) {
				if (telephone.getText() != null && normalizeContains(telephone.getText(), text)) {
					return true;
				}
				if (normalizeContains(telephone.getParameter(CustomParameter.TELEPHONE_TAG), text)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Realiza la búsqueda sobre el email
	 * 
	 * @param vcard
	 *            Información del contacto
	 * @param text
	 *            Texto a buscar
	 * @return Si valida la búsqueda
	 */
	private boolean searchEmail(VCard vcard, String text) {
		if (vcard.getEmails() == null) {
			return false;
		}
		for (Email email : vcard.getEmails()) {
			if (email != null) {
				if (email.getValue() != null && normalizeContains(email.getValue(), text)) {
					return true;
				}
				if (normalizeContains(email.getParameter(CustomParameter.EMAIL_TAG), text)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Realiza la búsqueda sobre la dirección
	 * 
	 * @param vcard
	 *            Información del contacto
	 * @param text
	 *            Texto a buscar
	 * @return Si valida la búsqueda
	 */
	private boolean searchAddress(VCard vcard, String text) {
		if (vcard.getAddresses() == null) {
			return false;
		}
		for (Address address : vcard.getAddresses()) {
			if (address.getStreetAddress() != null && normalizeContains(address.getStreetAddress(), text)) {
				return true;
			} else if (address.getLocality() != null && normalizeContains(address.getLocality(), text)) {
				return true;
			} else if (address.getRegion() != null && normalizeContains(address.getRegion(), text)) {
				return true;
			} else if (address.getPostalCode() != null && normalizeContains(address.getPostalCode(), text)) {
				return true;
			} else if (address.getCountry() != null && normalizeContains(address.getCountry(), text)) {
				return true;
			}
		}
		return false;
	}

}
