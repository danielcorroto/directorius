package com.danielcorroto.directorius.model.searchalgorithm.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.danielcorroto.directorius.TestUtil;
import com.danielcorroto.directorius.model.SimpleVCard;
import com.danielcorroto.directorius.model.searchalgorithm.SearchAlgorithm;

import ezvcard.VCard;
import junit.framework.TestCase;

/**
 * Test del algoritmo de búsqueda a partir del teléfono
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class SearchAlgorithmByPhoneTest extends TestCase {
	/**
	 * Prueba sin contactos
	 */
	public void testEmptyContact() {
		SearchAlgorithm search = new SearchAlgorithmByPhone();
		Set<SimpleVCard> result = search.search(new ArrayList<>(), TestUtil.buildSet("910 4", "67 6"));

		assertEquals(0, result.size());
	}

	/**
	 * Prueba sin cadenas
	 */
	public void testEmptySearch() {
		Set<VCard> cards = new HashSet<>();
		cards.add(TestUtil.createVCardPhones("qwe", "915 9"));
		SearchAlgorithm search = new SearchAlgorithmByPhone();
		Set<SimpleVCard> result = search.search(cards, new HashSet<>());

		assertEquals(0, result.size());
	}

	/**
	 * Prueba de búsqueda simple
	 */
	public void testSearchSimple() {
		Set<VCard> cards = new HashSet<>();
		cards.add(TestUtil.createVCardPhones("qwe rty", "856"));
		cards.add(TestUtil.createVCardPhones("asd fgh", "9 10 9"));
		cards.add(TestUtil.createVCardPhones("zxc vbn", "45678", "910 4"));
		SearchAlgorithm search = new SearchAlgorithmByPhone();
		Set<SimpleVCard> result = search.search(cards, TestUtil.buildSet("91 0"));

		Set<String> names = TestUtil.getNamesFromSimpleVCard(result);

		assertEquals(2, result.size());
		assertTrue(names.contains("asd fgh"));
		assertTrue(names.contains("zxc vbn"));
	}

	/**
	 * Prueba de búsqueda con varias cadenas de búsqueda
	 */
	public void testSearchMultiple() {
		Set<VCard> cards = new HashSet<>();
		cards.add(TestUtil.createVCardPhones("qwe rty", "8856"));
		cards.add(TestUtil.createVCardPhones("asd fgh", "9 10 88"));
		cards.add(TestUtil.createVCardPhones("zxc vbn", "45678", "910 49 8 80"));
		SearchAlgorithm search = new SearchAlgorithmByPhone();
		Set<SimpleVCard> result = search.search(cards, TestUtil.buildSet("91 0", "88"));

		Set<String> names = TestUtil.getNamesFromSimpleVCard(result);

		assertEquals(2, result.size());
		assertTrue(names.contains("asd fgh"));
		assertTrue(names.contains("zxc vbn"));
	}

}
