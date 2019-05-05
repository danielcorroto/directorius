package com.danielcorroto.directorius.controller.data;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.danielcorroto.directorius.controller.DisplayUtil;
import com.danielcorroto.directorius.model.Utils;
import com.danielcorroto.directorius.model.comparator.StringLocaleComparator;

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
	 * Cuenta de todos los contactos por categoría. Key: categoría. Value:
	 * cantidad
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
	 * Mapeado de la cantidad de nombres iguales. Key: nombre. Value: cantidad
	 */
	private Map<String, Integer> nameMap = new TreeMap<>(new StringLocaleComparator());
	/**
	 * Mapeado del año de nacimiento. Key: año. Value: cantidad
	 */
	private Map<Integer, Integer> birthYearMap = new TreeMap<>();

	/**
	 * Mapeado del día y mes de nacimiento. Key: día del año (suponiendo
	 * bisiesto). Value: cantidad
	 */
	private Map<Integer, Integer> birthDayMap = new TreeMap<>();

	/**
	 * Crea las estadísticas con la información del contacto
	 * 
	 * @param vcard
	 *            Información del contacto
	 */
	public void addContact(VCard vcard) {
		// Total de contactos
		allContacts++;
		// Información de cumpleaños
		if (vcard.getBirthday() != null && vcard.getBirthday().getDate() != null) {
			allContactsBirthday++;
		}
		// Categorías
		if (vcard.getCategories() != null && !vcard.getCategories().getValues().isEmpty()) {
			mapCategories(vcard);
		}
		// Foto
		if (!Utils.isEmpty(vcard.getPhotos())) {
			allContactsPhoto++;
		}
		// Teléfonos
		if (!Utils.isEmpty(vcard.getTelephoneNumbers())) {
			allContactsPhone++;
			for (Telephone phone : vcard.getTelephoneNumbers()) {
				phoneSet.add(phone.getText().replace(" ", ""));
			}
		}
		// Correos
		if (!Utils.isEmpty(vcard.getEmails())) {
			allContactsEmail++;
			for (Email email : vcard.getEmails()) {
				emailSet.add(email.getValue().replace(" ", ""));
			}
		}
		// Direcciones
		if (!Utils.isEmpty(vcard.getAddresses())) {
			allContactsAddress++;
			for (Address address : vcard.getAddresses()) {
				addressSet.add(address);
			}
		}
		// Nombres
		if (vcard.getStructuredName() != null && vcard.getStructuredName().getGiven() != null) {
			String name = vcard.getStructuredName().getGiven();
			nameMap.computeIfAbsent(name, k -> nameMap.put(k, 0));
			int quantity = nameMap.get(name);
			nameMap.put(name, quantity + 1);
		}
		// Año / día+mes de nacimiento
		if (vcard.getBirthday() != null) {
			Integer year = null; // YYYY
			Integer month = null; // 1-12
			Integer day = null; // 1-31
			if (vcard.getBirthday().getPartialDate() != null) {
				year = vcard.getBirthday().getPartialDate().getYear();
				month = vcard.getBirthday().getPartialDate().getMonth();
				if (month != null) {
					month -= 1;
				}
				day = vcard.getBirthday().getPartialDate().getDate();
			} else if (vcard.getBirthday().getDate() != null) {
				Calendar c = Calendar.getInstance();
				c.setTime(vcard.getBirthday().getDate());
				year = c.get(Calendar.YEAR);
				month = c.get(Calendar.MONTH);
				day = c.get(Calendar.DAY_OF_MONTH);
			}

			// Año
			if (year != null) {
				int total = 0;
				if (birthYearMap.containsKey(year)) {
					total = birthYearMap.get(year);
				}
				birthYearMap.put(year, total + 1);
			}

			// Día + mes
			if (day != null && month != null) {
				Calendar c = Calendar.getInstance();
				c.set(Calendar.DAY_OF_MONTH, day);
				c.set(Calendar.MONTH, month);
				// Un año bisiesto cualquiera (por el 29-2)
				c.set(Calendar.YEAR, DisplayUtil.BASE_YEAR);
				Integer dayOfYear = c.get(Calendar.DAY_OF_YEAR);
				int total = 0;
				if (birthDayMap.containsKey(dayOfYear)) {
					total = birthDayMap.get(dayOfYear);
				}
				birthDayMap.put(dayOfYear, total + 1);
			}
		}
	}

	/**
	 * Mapea la información por categoría
	 * 
	 * @param vcard
	 *            Información del contacto
	 */
	private void mapCategories(VCard vcard) {
		for (String category : vcard.getCategories().getValues()) {
			Integer count = categoryMap.get(category);
			if (count == null) {
				count = 0;
			}
			categoryMap.put(category, count + 1);
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

	/**
	 * Obtiene un mapa con los nombres y su cantidad
	 * 
	 * @return Mapa de nombres y su cantidad
	 */
	public Map<String, Integer> getNameMap() {
		return nameMap;
	}

	/**
	 * Obtiene un mapa con los años de nacimiento y su cantidad
	 * 
	 * @return Mapa de año de nacimiento y su cantidad
	 */
	public Map<Integer, Integer> getBirthYearMap() {
		return birthYearMap;
	}

	/**
	 * Obtiene un mapa con el día-del año de nacimiento (suponiendo que fuera
	 * bisiesto) y su cantidad
	 * 
	 * @return Mapa de día del año de nacimiento y su cantidad
	 */
	public Map<Integer, Integer> getBirthDayMap() {
		return birthDayMap;
	}

}
