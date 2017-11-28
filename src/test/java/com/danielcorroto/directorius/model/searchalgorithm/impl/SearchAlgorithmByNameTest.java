package com.danielcorroto.directorius.model.searchalgorithm.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.danielcorroto.directorius.TestUtil;
import com.danielcorroto.directorius.model.SimpleVCard;
import com.danielcorroto.directorius.model.searchalgorithm.SearchAlgorithm;

import ezvcard.VCard;
import ezvcard.VCardVersion;
import junit.framework.TestCase;

/**
 * Test del algoritmo de búsqueda a partir del nombre
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class SearchAlgorithmByNameTest extends TestCase {
	/**
	 * Prueba sin contactos
	 */
	public void testEmptyContact() {
		SearchAlgorithm search = new SearchAlgorithmByName();
		Set<SimpleVCard> result = search.search(new ArrayList<>(), TestUtil.buildSet("a", "b"));

		assertEquals(0, result.size());
	}

	/**
	 * Prueba sin cadenas
	 */
	public void testEmptySearch() {
		Set<VCard> cards = new HashSet<>();
		cards.add(createVCard("qwe"));
		SearchAlgorithm search = new SearchAlgorithmByName();
		Set<SimpleVCard> result = search.search(cards, new HashSet<>());

		assertEquals(0, result.size());
	}

	/**
	 * Prueba de búsqueda simple
	 */
	public void testSearchSimple() {
		Set<VCard> cards = new HashSet<>();
		cards.add(createVCard("qwe rty"));
		cards.add(createVCard("asd fgh"));
		cards.add(createVCard("zxc vbn"));
		SearchAlgorithm search = new SearchAlgorithmByName();
		Set<SimpleVCard> result = search.search(cards, TestUtil.buildSet("qwe"));

		assertEquals(1, result.size());
		assertEquals("qwe rty", result.iterator().next().getFormattedName().getValue());
	}

	/**
	 * Prueba de búsqueda con tilde y mayúsculas
	 */
	public void testSearchNormalize() {
		Set<VCard> cards = new HashSet<>();
		cards.add(createVCard("qwè rty"));
		cards.add(createVCard("asd fgh"));
		cards.add(createVCard("zxc vbn"));
		SearchAlgorithm search = new SearchAlgorithmByName();
		Set<SimpleVCard> result = search.search(cards, TestUtil.buildSet("qwÉ"));

		assertEquals(1, result.size());
		assertEquals("qwè rty", result.iterator().next().getFormattedName().getValue());
	}

	/**
	 * Prueba de búsqueda con varias cadenas de búsqueda
	 */
	public void testSearchMultiple() {
		Set<VCard> cards = new HashSet<>();
		cards.add(createVCard("qwer rty"));
		cards.add(createVCard("qwe fgh"));
		cards.add(createVCard("er rte rqwet"));
		SearchAlgorithm search = new SearchAlgorithmByName();
		Set<SimpleVCard> result = search.search(cards, TestUtil.buildSet("qwe", "r rt"));

		Set<String> names = TestUtil.getNamesFromSimpleVCard(result);

		assertEquals(2, result.size());
		assertTrue(names.contains("qwer rty"));
		assertTrue(names.contains("er rte rqwet"));
	}

	/**
	 * Construye una VCard v 4.0 con el nombre
	 * 
	 * @param name
	 *            Nombre asignado
	 * @return Objeto VCard con el nombre indicado
	 */
	private VCard createVCard(String name) {
		VCard vcard = new VCard(VCardVersion.V4_0);
		vcard.setFormattedName(name);
		return vcard;
	}
}
