package com.danielcorroto.directorius.controller.data;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import ezvcard.VCard;
import ezvcard.property.Address;
import ezvcard.property.Email;
import ezvcard.property.Telephone;

/**
 * Información de las estadísticas
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class Statistics {
	/**
	 * Cuenta de todos los contactos
	 */
	private int allContacts = 0;
	/**
	 * Cuenta de todos los contactos con fecha de cumpleaños completa
	 */
	private int allContactsBirthday = 0;
	/**
	 * Cuenta de todos los contactos con fotografía
	 */
	private int allContactsPhoto = 0;
	/**
	 * Cuenta de todos los contactos con algún teléfono
	 */
	private int allContactsPhone = 0;
	/**
	 * Cuenta de todos los contactos con algún email
	 */
	private int allContactsEmail = 0;
	/**
	 * Cuenta de todos los contactos con alguna dirección
	 */
	private int allContactsAddress = 0;
	/**
	 * Cuenta de todos los contactos por categoría
	 */
	private Map<String, Integer> categoryMap = new TreeMap<>();
	/**
	 * Todos los teléfonos (únicos)
	 */
	private Set<String> phoneSet = new HashSet<>();
	/**
	 * Todos los emails (únicos)
	 */
	private Set<String> emailSet = new HashSet<>();
	/**
	 * Todas las direcciones (únicas)
	 */
	private Set<Address> addressSet = new HashSet<>();

	/**
	 * Crea las estadísticas con la información del contacto
	 * 
	 * @param vcard
	 *            Información del contacto
	 */
	public void addContact(VCard vcard) {
		allContacts++;
		if (vcard.getBirthday() != null && vcard.getBirthday().getDate() != null) {
			allContactsBirthday++;
		}
		if (vcard.getCategories() != null && !vcard.getCategories().getValues().isEmpty()) {
			for (String category : vcard.getCategories().getValues()) {
				Integer count = categoryMap.get(category);
				if (count == null) {
					count = 0;
				}
				categoryMap.put(category, count + 1);
			}
		}
		if (vcard.getPhotos() != null && !vcard.getPhotos().isEmpty()) {
			allContactsPhoto++;
		}

		if (vcard.getTelephoneNumbers() != null && !vcard.getTelephoneNumbers().isEmpty()) {
			allContactsPhone++;
			for (Telephone phone : vcard.getTelephoneNumbers()) {
				phoneSet.add(phone.getText().replace(" ", ""));
			}
		}
		if (vcard.getEmails() != null && !vcard.getEmails().isEmpty()) {
			allContactsEmail++;
			for (Email email : vcard.getEmails()) {
				emailSet.add(email.getValue().replace(" ", ""));
			}
		}
		if (vcard.getAddresses() != null && !vcard.getAddresses().isEmpty()) {
			allContactsAddress++;
			for (Address address : vcard.getAddresses()) {
				addressSet.add(address);
			}
		}
	}

	/**
	 * Obtiene la cantidad total de contactos
	 * 
	 * @return Total de contactos
	 */
	public int getAllContacts() {
		return allContacts;
	}

	/**
	 * Obtiene la cantidad total de contactos con fechas de nacimiento completas
	 * 
	 * @return Total de contactos con fechas de nacimiento completas
	 */
	public int getAllContactsBirthday() {
		return allContactsBirthday;
	}

	/**
	 * Obtiene la cantidad de contactos con fotografía
	 * 
	 * @return Total de contactos con fotografía
	 */
	public int getAllContactsPhoto() {
		return allContactsPhoto;
	}

	/**
	 * Obtiene la cantidad de contactos con teléfono
	 * 
	 * @return Total de contactos con teléfono
	 */
	public int getAllContactsPhone() {
		return allContactsPhone;
	}

	/**
	 * Obtiene la cantidad de contactos con email
	 * 
	 * @return Total de contactos con email
	 */
	public int getAllContactsEmail() {
		return allContactsEmail;
	}

	/**
	 * Obtiene la cantidad de contactos con dirección
	 * 
	 * @return Total de contactos con dirección
	 */
	public int getAllContactsAddress() {
		return allContactsAddress;
	}

	/**
	 * Obtiene la cantidad de contactos por categoría
	 * 
	 * @return Mapa con el total de contactos por categoría. Key: categoría,
	 *         Value: cantidad de contactos
	 */
	public Map<String, Integer> getAllContactsCategoryMap() {
		return categoryMap;
	}

	/**
	 * Obtiene la cantidad de teléfonos únicos
	 * 
	 * @return Total de teléfonos únicos
	 */
	public int getUniquePhones() {
		return phoneSet.size();
	}

	/**
	 * Obtiene la cantidad de emails únicos
	 * 
	 * @return Total de emails únicos
	 */
	public int getUniqueEmails() {
		return emailSet.size();
	}

	/**
	 * Obtiene la cantidad de direcciones únicas
	 * 
	 * @return Total de direcciones únicas
	 */
	public int getUniqueAddresses() {
		return addressSet.size();
	}

}