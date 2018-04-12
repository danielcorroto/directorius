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
 * Test del algoritmo de búsqueda a partir de la categoría
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class SearchAlgorithmByCategoryTest extends TestCase {
	/**
	 * Prueba sin contactos
	 */
	public void testEmptyContact() {
		SearchAlgorithm search = new SearchAlgorithmByCategory();
		Set<SimpleVCard> result = search.search(new ArrayList<>(), TestUtil.buildSet("a", "b"), null);

		assertEquals(0, result.size());
	}

	/**
	 * Prueba sin cadenas
	 */
	public void testEmptySearch() {
		Set<VCard> cards = new HashSet<>();
		cards.add(TestUtil.createVCardCategories("qwe", "cat1"));
		SearchAlgorithm search = new SearchAlgorithmByCategory();
		Set<SimpleVCard> result = search.search(cards, new HashSet<>(), null);

		assertEquals(0, result.size());
	}

	/**
	 * Prueba de búsqueda simple
	 */
	public void testSearchSimple() {
		Set<VCard> cards = new HashSet<>();
		cards.add(TestUtil.createVCardCategories("qwe rty", "abc"));
		cards.add(TestUtil.createVCardCategories("asd fgh", " aqwea"));
		cards.add(TestUtil.createVCardCategories("zxc vbn", "def", "qwe"));
		SearchAlgorithm search = new SearchAlgorithmByCategory();
		Set<SimpleVCard> result = search.search(cards, TestUtil.buildSet("qwe"), null);

		Set<String> names = TestUtil.getNamesFromSimpleVCard(result);

		assertEquals(2, result.size());
		assertTrue(names.contains("asd fgh"));
		assertTrue(names.contains("zxc vbn"));
	}

	/**
	 * Prueba de búsqueda con tilde y mayúsculas
	 */
	public void testSearchNormalize() {
		Set<VCard> cards = new HashSet<>();
		cards.add(TestUtil.createVCardCategories("qwe rty", "abc"));
		cards.add(TestUtil.createVCardCategories("asd fgh", " aqwèa"));
		cards.add(TestUtil.createVCardCategories("zxc vbn", "def", "qwe"));
		SearchAlgorithm search = new SearchAlgorithmByCategory();
		Set<SimpleVCard> result = search.search(cards, TestUtil.buildSet("qwÉ"), null);

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
		cards.add(TestUtil.createVCardCategories("qwer rty", "qwe"));
		cards.add(TestUtil.createVCardCategories("qwe fgh", "fdasf", "fasdqwer rtr"));
		cards.add(TestUtil.createVCardCategories("errte rqwet", "fsdar rtqwe"));
		SearchAlgorithm search = new SearchAlgorithmByCategory();
		Set<SimpleVCard> result = search.search(cards, TestUtil.buildSet("qwe", "r rt"), null);

		Set<String> names = TestUtil.getNamesFromSimpleVCard(result);

		assertEquals(2, result.size());
		assertTrue(names.contains("qwe fgh"));
		assertTrue(names.contains("errte rqwet"));
	}

}
