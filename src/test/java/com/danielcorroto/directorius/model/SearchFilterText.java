package com.danielcorroto.directorius.model;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import junit.framework.TestCase;

/**
 * Test la funcionalidad del particionado de cadenas en la clase de filtrado de
 * búsqueda
 * 
 * @author Daniel Corroto Quirós
 */
public class SearchFilterText extends TestCase {

	/**
	 * Prueba el troceado de una cadena simple
	 * 
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public void testSplitSearchTextSimple()
			throws NoSuchMethodException, SecurityException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		SearchFilter filter = new SearchFilter(null, "qwerty", null);
		Set<String> res = filter.getSplittedText();
		assertEquals(1, res.size());
		assertTrue(res.contains("qwerty"));
	}

	/**
	 * Prueba el troceado de una cadena con comillas
	 * 
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public void testSplitSearchTextQuoted()
			throws NoSuchMethodException, SecurityException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		SearchFilter filter = new SearchFilter(null, "\"qwerty poiuy\"", null);
		Set<String> res = filter.getSplittedText();
		assertEquals(1, res.size());
		assertTrue(res.contains("qwerty poiuy"));
	}

	/**
	 * Prueba el troceado de una cadena con y sin comillas
	 * 
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public void testSplitSearchTextMultiple()
			throws NoSuchMethodException, SecurityException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		SearchFilter filter = new SearchFilter(null, "asdf \"qwerty poiuy\" \"zxc vbn\" ", null);
		Set<String> res = filter.getSplittedText();
		assertEquals(3, res.size());
		assertTrue(res.contains("asdf"));
		assertTrue(res.contains("qwerty poiuy"));
		assertTrue(res.contains("zxc vbn"));
	}

}
