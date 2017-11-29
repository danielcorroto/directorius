package com.danielcorroto.directorius;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.danielcorroto.directorius.model.CustomParameter;
import com.danielcorroto.directorius.model.SimpleVCard;

import ezvcard.VCard;
import ezvcard.VCardVersion;
import ezvcard.property.Address;
import ezvcard.property.Categories;
import ezvcard.property.Email;
import ezvcard.property.Note;
import ezvcard.property.Telephone;
import ezvcard.property.Uid;

/**
 * Utilidades para los test
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class TestUtil {

	/**
	 * Construye un set de cadenas a partir de la entrada
	 * 
	 * @param elements
	 *            Cadenas
	 * @return Set de cadenas
	 */
	public static Set<String> buildSet(String... elements) {
		Set<String> result = new HashSet<>();
		for (String element : elements) {
			result.add(element);
		}
		return result;
	}

	/**
	 * Crea una colección de nombres a partir de una lista de contactos sencilla
	 * 
	 * @param cards
	 *            Colección de contactos
	 * @return Set de nombres
	 */
	public static Set<String> getNamesFromSimpleVCard(Collection<SimpleVCard> cards) {
		Set<String> names = new HashSet<>();
		for (SimpleVCard simple : cards) {
			names.add(simple.getFormattedName().getValue());
		}
		return names;
	}

	/**
	 * Construye una VCard v 4.0 con el nombre
	 * 
	 * @param name
	 *            Nombre asignado
	 * @return Objeto VCard con el nombre indicado
	 */
	public static VCard createVCard(String name) {
		VCard vcard = new VCard(VCardVersion.V4_0);
		vcard.setFormattedName(name);
		vcard.setUid(Uid.random());
		return vcard;
	}

	/**
	 * Construye una VCard v 4.0 con el nombre y categorías
	 * 
	 * @param name
	 *            Nombre asignado
	 * @param categs
	 *            Lista de categorias
	 * @return Objeto VCard con el nombre y categorías indicadas
	 */
	public static VCard createVCardCategories(String name, String... categs) {
		VCard vcard = new VCard(VCardVersion.V4_0);
		vcard.setFormattedName(name);
		vcard.setUid(Uid.random());

		if (categs.length > 0) {
			Categories categories = new Categories();
			vcard.setCategories(categories);
			for (String categ : categs) {
				categories.getValues().add(categ);
			}
		}

		return vcard;
	}

	/**
	 * Construye una VCard v 4.0 con el nombre y teléfonos
	 * 
	 * @param name
	 *            Nombre asignado
	 * @param phones
	 *            Lista de teléfonos
	 * @return Objeto VCard con el nombre y teléfonos indicados
	 */
	public static VCard createVCardPhones(String name, String... phones) {
		VCard vcard = new VCard(VCardVersion.V4_0);
		vcard.setFormattedName(name);
		vcard.setUid(Uid.random());

		if (phones.length > 0) {
			for (String phone : phones) {
				Telephone telephone = new Telephone(phone);
				vcard.getTelephoneNumbers().add(telephone);
			}
		}

		return vcard;
	}

	/**
	 * Construye una VCard v 4.0 con el nombre y otros datos
	 * 
	 * @param name
	 *            Nombre asignado
	 * @param note
	 *            Nota asignada
	 * @param categs
	 *            Lista de categorías
	 * @param phones
	 *            Lista de teléfonos
	 * @param phoneTags
	 *            Lista de tags de teléfonos
	 * @param mails
	 *            Lista de correos
	 * @param mailTags
	 *            Lista de tags de correos
	 * @param addresses
	 *            Lista de direcciones: "Calle|Ciudad|Región|CP|País"
	 * @return Objeto VCard con el nombre y resto de información indicada
	 */
	public static VCard createVCardAll(String name, String note, String[] categs, String[] phones, String[] phoneTags, String[] mails, String[] mailTags, String[] addresses) {
		VCard vcard = new VCard(VCardVersion.V4_0);
		vcard.setFormattedName(name);
		vcard.setUid(Uid.random());

		if (note != null) {
			vcard.getNotes().add(new Note(note));
		}

		if (categs != null) {
			Categories cats = new Categories();
			vcard.setCategories(cats);
			for (String categ : categs) {
				vcard.getCategories().getValues().add(categ);
			}
		}

		if (phones != null) {
			for (int i = 0; i < phones.length; i++) {
				Telephone telephone = new Telephone(phones[i]);
				if (phoneTags != null && i < phoneTags.length && phoneTags[i] != null) {
					telephone.addParameter(CustomParameter.TELEPHONE_TAG, phoneTags[i]);
				}
				vcard.addTelephoneNumber(telephone);
			}
		}

		if (mails != null) {
			for (int i = 0; i < mails.length; i++) {
				Email email = new Email(mails[i]);
				if (mailTags != null && i < mailTags.length && mailTags[i] != null) {
					email.addParameter(CustomParameter.EMAIL_TAG, mailTags[i]);
				}
				vcard.addEmail(email);
			}
		}

		if (addresses != null) {
			for (String address : addresses) {
				String[] splitted = address.split("\\|");
				Address addr = new Address();
				addr.setStreetAddress(splitted[0]);
				addr.setLocality(splitted[1]);
				addr.setRegion(splitted[2]);
				addr.setPostalCode(splitted[3]);
				addr.setCountry(splitted[4]);
				vcard.addAddress(addr);
			}
		}
		return vcard;
	}
}
