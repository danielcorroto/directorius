package com.danielcorroto.directorius.model.searchalgorithm.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.danielcorroto.directorius.TestUtil;
import com.danielcorroto.directorius.model.SimpleVCard;
import com.danielcorroto.directorius.model.searchalgorithm.SearchAlgorithm;

import ezvcard.VCard;
import ezvcard.VCardVersion;
import ezvcard.property.Categories;
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
		Set<SimpleVCard> result = search.search(new ArrayList<>(), TestUtil.buildSet("a", "b"));

		assertEquals(0, result.size());
	}

	/**
	 * Prueba sin cadenas
	 */
	public void testEmptySearch() {
		Set<VCard> cards = new HashSet<>();
		cards.add(createVCard("qwe", "cat1"));
		SearchAlgorithm search = new SearchAlgorithmByCategory();
		Set<SimpleVCard> result = search.search(cards, new HashSet<>());

		assertEquals(0, result.size());
	}

	/**
	 * Prueba de búsqueda simple
	 */
	public void testSearchSimple() {
		Set<VCard> cards = new HashSet<>();
		cards.add(createVCard("qwe rty", "abc"));
		cards.add(createVCard("asd fgh", " aqwea"));
		cards.add(createVCard("zxc vbn", "def", "qwe"));
		SearchAlgorithm search = new SearchAlgorithmByCategory();
		Set<SimpleVCard> result = search.search(cards, TestUtil.buildSet("qwe"));

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
		cards.add(createVCard("qwe rty", "abc"));
		cards.add(createVCard("asd fgh", " aqwèa"));
		cards.add(createVCard("zxc vbn", "def", "qwe"));
		SearchAlgorithm search = new SearchAlgorithmByCategory();
		Set<SimpleVCard> result = search.search(cards, TestUtil.buildSet("qwÉ"));

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
		cards.add(createVCard("qwer rty", "qwe"));
		cards.add(createVCard("qwe fgh", "fdasf", "fasdqwer rtr"));
		cards.add(createVCard("errte rqwet", "fsdar rtqwe"));
		SearchAlgorithm search = new SearchAlgorithmByCategory();
		Set<SimpleVCard> result = search.search(cards, TestUtil.buildSet("qwe", "r rt"));

		Set<String> names = TestUtil.getNamesFromSimpleVCard(result);

		assertEquals(2, result.size());
		assertTrue(names.contains("qwe fgh"));
		assertTrue(names.contains("errte rqwet"));
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
	private VCard createVCard(String name, String... categs) {
		VCard vcard = new VCard(VCardVersion.V4_0);
		vcard.setFormattedName(name);

		if (categs.length > 0) {
			Categories categories = new Categories();
			vcard.setCategories(categories);
			for (String categ : categs) {
				categories.getValues().add(categ);
			}
		}

		return vcard;
	}
}
