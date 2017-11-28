package com.danielcorroto.directorius.model.searchalgorithm.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.danielcorroto.directorius.TestUtil;
import com.danielcorroto.directorius.model.CustomParameter;
import com.danielcorroto.directorius.model.SimpleVCard;
import com.danielcorroto.directorius.model.searchalgorithm.SearchAlgorithm;

import ezvcard.VCard;
import ezvcard.VCardVersion;
import ezvcard.property.Address;
import ezvcard.property.Categories;
import ezvcard.property.Email;
import ezvcard.property.Note;
import ezvcard.property.Telephone;
import junit.framework.TestCase;

/**
 * Test del algoritmo de búsqueda a partir de todos los campos
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class SearchAlgorithmByAllTest extends TestCase {
	/**
	 * Prueba sin contactos
	 */
	public void testEmptyContact() {
		SearchAlgorithm search = new SearchAlgorithmByAll();
		Set<SimpleVCard> result = search.search(new ArrayList<>(), TestUtil.buildSet("a", "b"));

		assertEquals(0, result.size());
	}

	/**
	 * Prueba sin cadneas
	 */
	public void testEmptySearch() {
		Set<VCard> cards = new HashSet<>();
		cards.add(createVCard("qwe", null, null, null, null, null, null, null));
		SearchAlgorithm search = new SearchAlgorithmByAll();
		Set<SimpleVCard> result = search.search(cards, new HashSet<>());

		assertEquals(0, result.size());
	}

	/**
	 * Prueba de búsqueda simple
	 */
	public void testSearchSimple() {
		Set<VCard> cards = new HashSet<>();
		cards.add(createVCard("qwe", null, null, null, null, null, null, null));
		cards.add(createVCard("rty", null, null, null, null, null, null, null));
		cards.add(createVCard("uio", "fdaqwe fd", null, null, null, null, null, null));
		cards.add(createVCard("asd", "fdsfsf ss", null, null, null, null, null, null));
		cards.add(createVCard("fgh", null, new String[] { "fasd qwe" }, null, null, null, null, null));
		cards.add(createVCard("jkl", null, new String[] { "fdsa fds" }, null, null, null, null, null));
		cards.add(createVCard("zxc", null, null, new String[] { "5454 54 3 4" }, null, null, null, null));
		cards.add(createVCard("vbn", null, null, new String[] { "8756 876 3" }, null, null, null, null));
		cards.add(createVCard("qw", null, null, new String[] { "1" }, new String[] { "dfdsqwef" }, null, null, null));
		cards.add(createVCard("er", null, null, new String[] { "1" }, new String[] { "hgfhdgjj" }, null, null, null));
		cards.add(createVCard("ty", null, null, null, null, new String[] { "asd@qwe.a" }, null, null));
		cards.add(createVCard("ui", null, null, null, null, new String[] { "fd@cas.ne" }, null, null));
		cards.add(createVCard("op", null, null, null, null, new String[] { "" }, new String[] { "qwe fdas" }, null));
		cards.add(createVCard("as", null, null, null, null, new String[] { "" }, new String[] { "hghgd st" }, null));
		cards.add(createVCard("df", null, null, null, null, null, null, new String[] { "qwe|ci|re|cp|pa" }));
		cards.add(createVCard("gh", null, null, null, null, null, null, new String[] { "ca|rqwe|re|cp|pa" }));
		cards.add(createVCard("jk", null, null, null, null, null, null, new String[] { "ca|ci|qwer|cp|pa" }));
		cards.add(createVCard("zx", null, null, null, null, null, null, new String[] { "ca|ci|re|tqwer|pa" }));
		cards.add(createVCard("cv", null, null, null, null, null, null, new String[] { "ca|ci|re|cp|tqwer" }));
		cards.add(createVCard("bn", null, null, null, null, null, null, new String[] { "ca|ci|re|cp|pa" }));
		SearchAlgorithm search = new SearchAlgorithmByAll();
		Set<SimpleVCard> result = search.search(cards, TestUtil.buildSet("qwe"));

		Set<String> names = TestUtil.getNamesFromSimpleVCard(result);

		assertEquals(11, result.size());
		assertTrue(names.contains("qwe"));
		assertTrue(names.contains("uio"));
		assertTrue(names.contains("fgh"));
		assertTrue(names.contains("qw"));
		assertTrue(names.contains("ty"));
		assertTrue(names.contains("op"));
		assertTrue(names.contains("df"));
		assertTrue(names.contains("gh"));
		assertTrue(names.contains("jk"));
		assertTrue(names.contains("zx"));
		assertTrue(names.contains("cv"));
	}

	/**
	 * Prueba de búsqueda con tilde y mayúsculas
	 */
	public void testSearchNormalize() {
		Set<VCard> cards = new HashSet<>();
		cards.add(createVCard("qwè", null, null, null, null, null, null, null));
		cards.add(createVCard("rty", null, null, null, null, null, null, null));
		cards.add(createVCard("uio", "fdaqwË fd", null, null, null, null, null, null));
		cards.add(createVCard("asd", "fdsfsf ss", null, null, null, null, null, null));
		cards.add(createVCard("fgh", null, new String[] { "fasd qwÈ" }, null, null, null, null, null));
		cards.add(createVCard("jkl", null, new String[] { "fdsa fds" }, null, null, null, null, null));
		cards.add(createVCard("zxc", null, null, new String[] { "5454 54 3 4" }, null, null, null, null));
		cards.add(createVCard("vbn", null, null, new String[] { "8756 876 3" }, null, null, null, null));
		cards.add(createVCard("qw", null, null, new String[] { "1" }, new String[] { "dfdsqwef" }, null, null, null));
		cards.add(createVCard("er", null, null, new String[] { "1" }, new String[] { "hgfhdgjj" }, null, null, null));
		cards.add(createVCard("ty", null, null, null, null, new String[] { "asd@qwë.a" }, null, null));
		cards.add(createVCard("ui", null, null, null, null, new String[] { "fd@cas.ne" }, null, null));
		cards.add(createVCard("op", null, null, null, null, new String[] { "" }, new String[] { "qwè fdas" }, null));
		cards.add(createVCard("as", null, null, null, null, new String[] { "" }, new String[] { "hghgd st" }, null));
		cards.add(createVCard("df", null, null, null, null, null, null, new String[] { "qwe|ci|re|cp|pa" }));
		cards.add(createVCard("gh", null, null, null, null, null, null, new String[] { "ca|rqwÉ|re|cp|pa" }));
		cards.add(createVCard("jk", null, null, null, null, null, null, new String[] { "ca|ci|qwËr|cp|pa" }));
		cards.add(createVCard("zx", null, null, null, null, null, null, new String[] { "ca|ci|re|tqwér|pa" }));
		cards.add(createVCard("cv", null, null, null, null, null, null, new String[] { "ca|ci|re|cp|tqwer" }));
		cards.add(createVCard("bn", null, null, null, null, null, null, new String[] { "ca|ci|re|cp|pa" }));
		SearchAlgorithm search = new SearchAlgorithmByAll();
		Set<SimpleVCard> result = search.search(cards, TestUtil.buildSet("qwé"));

		Set<String> names = TestUtil.getNamesFromSimpleVCard(result);

		assertEquals(11, result.size());
		assertTrue(names.contains("qwè"));
		assertTrue(names.contains("uio"));
		assertTrue(names.contains("fgh"));
		assertTrue(names.contains("qw"));
		assertTrue(names.contains("ty"));
		assertTrue(names.contains("op"));
		assertTrue(names.contains("df"));
		assertTrue(names.contains("gh"));
		assertTrue(names.contains("jk"));
		assertTrue(names.contains("zx"));
		assertTrue(names.contains("cv"));
	}

	/**
	 * Prueba de búsqueda con varias cadenas de búsqueda
	 */
	public void testSearchMultiple() {
		Set<VCard> cards = new HashSet<>();
		cards.add(createVCard("qwe", "ar rt", null, null, null, null, null, null));
		cards.add(createVCard("rty", null, null, null, null, null, null, null));
		cards.add(createVCard("uio", "fdaqwe fd", null, null, null, null, null, null));
		cards.add(createVCard("asd", "fdsfsf ss", null, null, null, null, null, null));
		cards.add(createVCard("fgh", "asr rt", new String[] { "fasd qwe" }, null, null, null, null, null));
		cards.add(createVCard("jkl", null, new String[] { "fdsa fds" }, null, null, null, null, null));
		cards.add(createVCard("zxc", null, null, new String[] { "5454 54 3 4" }, null, null, null, null));
		cards.add(createVCard("vbn", null, null, new String[] { "8756 876 3" }, null, null, null, null));
		cards.add(createVCard("qw", null, null, new String[] { "1" }, new String[] { "dfdsqwef" }, null, null, null));
		cards.add(createVCard("er", null, null, new String[] { "1" }, new String[] { "hgfhdgjj" }, null, null, null));
		cards.add(createVCard("ty", "r rt", null, null, null, new String[] { "asd@qwe.a" }, null, null));
		cards.add(createVCard("ui", null, null, null, null, new String[] { "fd@cas.ne" }, null, null));
		cards.add(createVCard("op", null, null, null, null, new String[] { "" }, new String[] { "qwe fdas" }, null));
		cards.add(createVCard("as", null, null, null, null, new String[] { "" }, new String[] { "hghgd st" }, null));
		cards.add(createVCard("df", null, null, null, null, null, null, new String[] { "qwe|ci|r rt|cp|pa" }));
		cards.add(createVCard("gh", null, null, null, null, null, null, new String[] { "ca|rqwe|re|cp|pa" }));
		cards.add(createVCard("jk", null, new String[] { "fdsr rtds" }, null, null, null, null, new String[] { "ca|ci|qwer|cp|pa" }));
		cards.add(createVCard("zx", null, null, null, null, null, null, new String[] { "ca|ci|re|tqwer|pa" }));
		cards.add(createVCard("cv", null, new String[] { "r rtds" }, null, null, null, null, new String[] { "ca|ci|re|cp|tqwer" }));
		cards.add(createVCard("bn", null, null, null, null, null, null, new String[] { "ca|ci|re|cp|pa" }));
		SearchAlgorithm search = new SearchAlgorithmByAll();
		Set<SimpleVCard> result = search.search(cards, TestUtil.buildSet("qwe", "r rt"));

		Set<String> names = TestUtil.getNamesFromSimpleVCard(result);

		assertEquals(6, result.size());
		assertTrue(names.contains("qwe"));
		assertTrue(names.contains("fgh"));
		assertTrue(names.contains("ty"));
		assertTrue(names.contains("df"));
		assertTrue(names.contains("jk"));
		assertTrue(names.contains("cv"));
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
	private VCard createVCard(String name, String note, String[] categs, String[] phones, String[] phoneTags, String[] mails, String[] mailTags, String[] addresses) {
		VCard vcard = new VCard(VCardVersion.V4_0);
		vcard.setFormattedName(name);

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
