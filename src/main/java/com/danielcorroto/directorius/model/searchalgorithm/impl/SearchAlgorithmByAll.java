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
	 * Realiza la búsqueda sobre el nombre
	 * 
	 * @param vcard
	 *            Información del contacto
	 * @param text
	 *            Texto a buscar
	 * @return Si valida la búsqueda
	 */
	private boolean searchName(VCard vcard, String text) {
		return vcard.getFormattedName() != null && vcard.getFormattedName().getValue() != null && contains(vcard.getFormattedName().getValue(), text);
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
			if (note != null && note.getValue() != null && contains(note.getValue(), text)) {
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
			if (category != null && contains(category, text)) {
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
				if (telephone.getText() != null && contains(telephone.getText(), text)) {
					return true;
				}
				if (contains(telephone.getParameter(CustomParameter.TELEPHONE_TAG), text)) {
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
				if (email.getValue() != null && contains(email.getValue(), text)) {
					return true;
				}
				if (contains(email.getParameter(CustomParameter.EMAIL_TAG), text)) {
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
			if (address.getStreetAddress() != null && contains(address.getStreetAddress(), text)) {
				return true;
			} else if (address.getLocality() != null && contains(address.getLocality(), text)) {
				return true;
			} else if (address.getRegion() != null && contains(address.getRegion(), text)) {
				return true;
			} else if (address.getPostalCode() != null && contains(address.getPostalCode(), text)) {
				return true;
			} else if (address.getCountry() != null && contains(address.getCountry(), text)) {
				return true;
			}
		}
		return false;
	}

}
