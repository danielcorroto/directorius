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
		Set<SimpleVCard> result = search.search(new ArrayList<>(), TestUtil.buildFilter("a b", null));

		assertEquals(0, result.size());
	}

	/**
	 * Prueba sin cadneas
	 */
	public void testEmptySearch() {
		Set<VCard> cards = new HashSet<>();
		cards.add(TestUtil.createVCardAll("qwe", null, null, null, null, null, null, null));
		SearchAlgorithm search = new SearchAlgorithmByAll();
		Set<SimpleVCard> result = search.search(cards, TestUtil.buildFilter("", null));

		assertEquals(1, result.size());
		Set<String> names = TestUtil.getNamesFromSimpleVCard(result);
		assertTrue(names.contains("qwe"));
	}

	/**
	 * Prueba de búsqueda simple
	 */
	public void testSearchSimple() {
		Set<VCard> cards = new HashSet<>();
		cards.add(TestUtil.createVCardAll("qwe", null, null, null, null, null, null, null));
		cards.add(TestUtil.createVCardAll("rty", null, null, null, null, null, null, null));
		cards.add(TestUtil.createVCardAll("uio", "fdaqwe fd", null, null, null, null, null, null));
		cards.add(TestUtil.createVCardAll("asd", "fdsfsf ss", null, null, null, null, null, null));
		cards.add(TestUtil.createVCardAll("fgh", null, new String[] { "fasd qwe" }, null, null, null, null, null));
		cards.add(TestUtil.createVCardAll("jkl", null, new String[] { "fdsa fds" }, null, null, null, null, null));
		cards.add(TestUtil.createVCardAll("zxc", null, null, new String[] { "5454 54 3 4" }, null, null, null, null));
		cards.add(TestUtil.createVCardAll("vbn", null, null, new String[] { "8756 876 3" }, null, null, null, null));
		cards.add(TestUtil.createVCardAll("qw", null, null, new String[] { "1" }, new String[] { "dfdsqwef" }, null, null, null));
		cards.add(TestUtil.createVCardAll("er", null, null, new String[] { "1" }, new String[] { "hgfhdgjj" }, null, null, null));
		cards.add(TestUtil.createVCardAll("ty", null, null, null, null, new String[] { "asd@qwe.a" }, null, null));
		cards.add(TestUtil.createVCardAll("ui", null, null, null, null, new String[] { "fd@cas.ne" }, null, null));
		cards.add(TestUtil.createVCardAll("op", null, null, null, null, new String[] { "" }, new String[] { "qwe fdas" }, null));
		cards.add(TestUtil.createVCardAll("as", null, null, null, null, new String[] { "" }, new String[] { "hghgd st" }, null));
		cards.add(TestUtil.createVCardAll("df", null, null, null, null, null, null, new String[] { "qwe|ci|re|cp|pa" }));
		cards.add(TestUtil.createVCardAll("gh", null, null, null, null, null, null, new String[] { "ca|rqwe|re|cp|pa" }));
		cards.add(TestUtil.createVCardAll("jk", null, null, null, null, null, null, new String[] { "ca|ci|qwer|cp|pa" }));
		cards.add(TestUtil.createVCardAll("zx", null, null, null, null, null, null, new String[] { "ca|ci|re|tqwer|pa" }));
		cards.add(TestUtil.createVCardAll("cv", null, null, null, null, null, null, new String[] { "ca|ci|re|cp|tqwer" }));
		cards.add(TestUtil.createVCardAll("bn", null, null, null, null, null, null, new String[] { "ca|ci|re|cp|pa" }));
		SearchAlgorithm search = new SearchAlgorithmByAll();
		Set<SimpleVCard> result = search.search(cards, TestUtil.buildFilter("qwe", null));

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
		cards.add(TestUtil.createVCardAll("qwè", null, null, null, null, null, null, null));
		cards.add(TestUtil.createVCardAll("rty", null, null, null, null, null, null, null));
		cards.add(TestUtil.createVCardAll("uio", "fdaqwË fd", null, null, null, null, null, null));
		cards.add(TestUtil.createVCardAll("asd", "fdsfsf ss", null, null, null, null, null, null));
		cards.add(TestUtil.createVCardAll("fgh", null, new String[] { "fasd qwÈ" }, null, null, null, null, null));
		cards.add(TestUtil.createVCardAll("jkl", null, new String[] { "fdsa fds" }, null, null, null, null, null));
		cards.add(TestUtil.createVCardAll("zxc", null, null, new String[] { "5454 54 3 4" }, null, null, null, null));
		cards.add(TestUtil.createVCardAll("vbn", null, null, new String[] { "8756 876 3" }, null, null, null, null));
		cards.add(TestUtil.createVCardAll("qw", null, null, new String[] { "1" }, new String[] { "dfdsqwef" }, null, null, null));
		cards.add(TestUtil.createVCardAll("er", null, null, new String[] { "1" }, new String[] { "hgfhdgjj" }, null, null, null));
		cards.add(TestUtil.createVCardAll("ty", null, null, null, null, new String[] { "asd@qwë.a" }, null, null));
		cards.add(TestUtil.createVCardAll("ui", null, null, null, null, new String[] { "fd@cas.ne" }, null, null));
		cards.add(TestUtil.createVCardAll("op", null, null, null, null, new String[] { "" }, new String[] { "qwè fdas" }, null));
		cards.add(TestUtil.createVCardAll("as", null, null, null, null, new String[] { "" }, new String[] { "hghgd st" }, null));
		cards.add(TestUtil.createVCardAll("df", null, null, null, null, null, null, new String[] { "qwe|ci|re|cp|pa" }));
		cards.add(TestUtil.createVCardAll("gh", null, null, null, null, null, null, new String[] { "ca|rqwÉ|re|cp|pa" }));
		cards.add(TestUtil.createVCardAll("jk", null, null, null, null, null, null, new String[] { "ca|ci|qwËr|cp|pa" }));
		cards.add(TestUtil.createVCardAll("zx", null, null, null, null, null, null, new String[] { "ca|ci|re|tqwér|pa" }));
		cards.add(TestUtil.createVCardAll("cv", null, null, null, null, null, null, new String[] { "ca|ci|re|cp|tqwer" }));
		cards.add(TestUtil.createVCardAll("bn", null, null, null, null, null, null, new String[] { "ca|ci|re|cp|pa" }));
		SearchAlgorithm search = new SearchAlgorithmByAll();
		Set<SimpleVCard> result = search.search(cards, TestUtil.buildFilter("qwé", null));

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
		cards.add(TestUtil.createVCardAll("qwe", "ar rt", null, null, null, null, null, null));
		cards.add(TestUtil.createVCardAll("rty", null, null, null, null, null, null, null));
		cards.add(TestUtil.createVCardAll("uio", "fdaqwe fd", null, null, null, null, null, null));
		cards.add(TestUtil.createVCardAll("asd", "fdsfsf ss", null, null, null, null, null, null));
		cards.add(TestUtil.createVCardAll("fgh", "asr rt", new String[] { "fasd qwe" }, null, null, null, null, null));
		cards.add(TestUtil.createVCardAll("jkl", null, new String[] { "fdsa fds" }, null, null, null, null, null));
		cards.add(TestUtil.createVCardAll("zxc", null, null, new String[] { "5454 54 3 4" }, null, null, null, null));
		cards.add(TestUtil.createVCardAll("vbn", null, null, new String[] { "8756 876 3" }, null, null, null, null));
		cards.add(TestUtil.createVCardAll("qw", null, null, new String[] { "1" }, new String[] { "dfdsqwef" }, null, null, null));
		cards.add(TestUtil.createVCardAll("er", null, null, new String[] { "1" }, new String[] { "hgfhdgjj" }, null, null, null));
		cards.add(TestUtil.createVCardAll("ty", "r rt", null, null, null, new String[] { "asd@qwe.a" }, null, null));
		cards.add(TestUtil.createVCardAll("ui", null, null, null, null, new String[] { "fd@cas.ne" }, null, null));
		cards.add(TestUtil.createVCardAll("op", null, null, null, null, new String[] { "" }, new String[] { "qwe fdas" }, null));
		cards.add(TestUtil.createVCardAll("as", null, null, null, null, new String[] { "" }, new String[] { "hghgd st" }, null));
		cards.add(TestUtil.createVCardAll("df", null, null, null, null, null, null, new String[] { "qwe|ci|r rt|cp|pa" }));
		cards.add(TestUtil.createVCardAll("gh", null, null, null, null, null, null, new String[] { "ca|rqwe|re|cp|pa" }));
		cards.add(TestUtil.createVCardAll("jk", null, new String[] { "fdsr rtds" }, null, null, null, null, new String[] { "ca|ci|qwer|cp|pa" }));
		cards.add(TestUtil.createVCardAll("zx", null, null, null, null, null, null, new String[] { "ca|ci|re|tqwer|pa" }));
		cards.add(TestUtil.createVCardAll("cv", null, new String[] { "r rtds" }, null, null, null, null, new String[] { "ca|ci|re|cp|tqwer" }));
		cards.add(TestUtil.createVCardAll("bn", null, null, null, null, null, null, new String[] { "ca|ci|re|cp|pa" }));
		SearchAlgorithm search = new SearchAlgorithmByAll();
		Set<SimpleVCard> result = search.search(cards, TestUtil.buildFilter("qwe \"r rt\"", null));

		Set<String> names = TestUtil.getNamesFromSimpleVCard(result);

		assertEquals(6, result.size());
		assertTrue(names.contains("qwe"));
		assertTrue(names.contains("fgh"));
		assertTrue(names.contains("ty"));
		assertTrue(names.contains("df"));
		assertTrue(names.contains("jk"));
		assertTrue(names.contains("cv"));
	}

	public void testSearchWithEmptyCategory() {
		Set<VCard> cards = new HashSet<>();
		cards.add(TestUtil.createVCardAll("qwe", "ar rt", null, null, null, null, null, null));
		cards.add(TestUtil.createVCardAll("rty", null, null, null, null, null, null, null));
		cards.add(TestUtil.createVCardAll("uio", "fdaqwe fd", null, null, null, null, null, null));
		cards.add(TestUtil.createVCardAll("asd", "fdsfsf ss", null, null, null, null, null, null));
		cards.add(TestUtil.createVCardAll("fgh", "asr rt", new String[] { "fasd qwe" }, null, null, null, null, null));
		cards.add(TestUtil.createVCardAll("jkl", null, new String[] { "fdsa fds" }, null, null, null, null, null));
		cards.add(TestUtil.createVCardAll("zxc", null, null, new String[] { "5454 54 3 4" }, null, null, null, null));
		cards.add(TestUtil.createVCardAll("vbn", null, null, new String[] { "8756 876 3" }, null, null, null, null));
		cards.add(TestUtil.createVCardAll("qw", null, null, new String[] { "1" }, new String[] { "dfdsqwef" }, null, null, null));
		cards.add(TestUtil.createVCardAll("er", null, null, new String[] { "1" }, new String[] { "hgfhdgjj" }, null, null, null));
		cards.add(TestUtil.createVCardAll("ty", "r rt", new String[] {}, null, null, new String[] { "asd@qwe.a" }, null, null));
		cards.add(TestUtil.createVCardAll("ui", null, null, null, null, new String[] { "fd@cas.ne" }, null, null));
		cards.add(TestUtil.createVCardAll("op", null, null, null, null, new String[] { "" }, new String[] { "qwe fdas" }, null));
		cards.add(TestUtil.createVCardAll("as", null, null, null, null, new String[] { "" }, new String[] { "hghgd st" }, null));
		cards.add(TestUtil.createVCardAll("df", null, null, null, null, null, null, new String[] { "qwe|ci|r rt|cp|pa" }));
		cards.add(TestUtil.createVCardAll("gh", null, null, null, null, null, null, new String[] { "ca|rqwe|re|cp|pa" }));
		cards.add(TestUtil.createVCardAll("jk", null, new String[] { "fdsr rtds" }, null, null, null, null, new String[] { "ca|ci|qwer|cp|pa" }));
		cards.add(TestUtil.createVCardAll("zx", null, null, null, null, null, null, new String[] { "ca|ci|re|tqwer|pa" }));
		cards.add(TestUtil.createVCardAll("cv", null, new String[] { "r rtds" }, null, null, null, null, new String[] { "ca|ci|re|cp|tqwer" }));
		cards.add(TestUtil.createVCardAll("bn", null, null, null, null, null, null, new String[] { "ca|ci|re|cp|pa" }));
		SearchAlgorithm search = new SearchAlgorithmByAll();
		Set<SimpleVCard> result = search.search(cards, TestUtil.buildFilter("qwe \"r rt\"", ""));

		Set<String> names = TestUtil.getNamesFromSimpleVCard(result);

		assertEquals(6, result.size());
		assertTrue(names.contains("qwe"));
		assertTrue(names.contains("fgh"));
		assertTrue(names.contains("ty"));
		assertTrue(names.contains("df"));
		assertTrue(names.contains("jk"));
		assertTrue(names.contains("cv"));
	}

	public void testSearchWithCategory() {
		Set<VCard> cards = new HashSet<>();
		cards.add(TestUtil.createVCardAll("qwe", "ar rt", null, null, null, null, null, null));
		cards.add(TestUtil.createVCardAll("rty", null, null, null, null, null, null, null));
		cards.add(TestUtil.createVCardAll("uio", "fdaqwe fd", null, null, null, null, null, null));
		cards.add(TestUtil.createVCardAll("asd", "fdsfsf ss", null, null, null, null, null, null));
		cards.add(TestUtil.createVCardAll("fgh", "asr rt", new String[] { "fasd qwe", "aaaa" }, null, null, null, null, null));
		cards.add(TestUtil.createVCardAll("jkl", null, new String[] { "fdsa fds" }, null, null, null, null, null));
		cards.add(TestUtil.createVCardAll("zxc", null, null, new String[] { "5454 54 3 4" }, null, null, null, null));
		cards.add(TestUtil.createVCardAll("vbn", null, null, new String[] { "8756 876 3" }, null, null, null, null));
		cards.add(TestUtil.createVCardAll("qw", null, null, new String[] { "1" }, new String[] { "dfdsqwef" }, null, null, null));
		cards.add(TestUtil.createVCardAll("er", null, null, new String[] { "1" }, new String[] { "hgfhdgjj" }, null, null, null));
		cards.add(TestUtil.createVCardAll("ty", "r rt", new String[] {}, null, null, new String[] { "asd@qwe.a" }, null, null));
		cards.add(TestUtil.createVCardAll("ui", null, null, null, null, new String[] { "fd@cas.ne" }, null, null));
		cards.add(TestUtil.createVCardAll("op", null, null, null, null, new String[] { "" }, new String[] { "qwe fdas" }, null));
		cards.add(TestUtil.createVCardAll("as", null, null, null, null, new String[] { "" }, new String[] { "hghgd st" }, null));
		cards.add(TestUtil.createVCardAll("df", null, new String[] { "aaa" }, null, null, null, null, new String[] { "qwe|ci|r rt|cp|pa" }));
		cards.add(TestUtil.createVCardAll("gh", null, null, null, null, null, null, new String[] { "ca|rqwe|re|cp|pa" }));
		cards.add(TestUtil.createVCardAll("jk", null, new String[] { "fdsr rtds" }, null, null, null, null, new String[] { "ca|ci|qwer|cp|pa" }));
		cards.add(TestUtil.createVCardAll("zx", null, null, null, null, null, null, new String[] { "ca|ci|re|tqwer|pa" }));
		cards.add(TestUtil.createVCardAll("cv", null, new String[] { "r rtdaaas" }, null, null, null, null, new String[] { "ca|ci|re|cp|tqwer" }));
		cards.add(TestUtil.createVCardAll("bn", null, null, null, null, null, null, new String[] { "ca|ci|re|cp|pa" }));
		SearchAlgorithm search = new SearchAlgorithmByAll();
		Set<SimpleVCard> result = search.search(cards, TestUtil.buildFilter("qwe \"r rt\"", "aaa"));

		Set<String> names = TestUtil.getNamesFromSimpleVCard(result);

		assertEquals(3, result.size());
		assertTrue(names.contains("fgh"));
		assertTrue(names.contains("df"));
		assertTrue(names.contains("cv"));
	}
}
