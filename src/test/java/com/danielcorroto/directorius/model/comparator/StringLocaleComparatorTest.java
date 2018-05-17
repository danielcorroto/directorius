package com.danielcorroto.directorius.model.comparator;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.TestCase;

/**
 * Test del comparador
 * 
 * @author Daniel Corroto Quirós
 */
public class StringLocaleComparatorTest extends TestCase {

	/**
	 * Prueba la ordenación sin elemntos
	 */
	public void testEmpty() {
		Collection<String> vcards = createData();

		assertTrue(vcards.isEmpty());
	}

	/**
	 * Prueba la ordenación donde alguno de los elementos tienen valor null
	 */
	public void testSomeNull() {
		Collection<String> vcards = createData(null, "Ceres");
		Iterator<String> iterator = vcards.iterator();

		assertNull(iterator.next());
		assertEquals("Ceres", iterator.next());
	}

	/**
	 * Prueba la ordenación con todos los datos
	 */
	public void testData() {
		Collection<String> vcards = createData("Ceres", "Pallas", "Juno", "Vesta", "Astraea", "Hebe");
		Iterator<String> iterator = vcards.iterator();

		assertEquals("Astraea", iterator.next());
		assertEquals("Ceres", iterator.next());
		assertEquals("Hebe", iterator.next());
		assertEquals("Juno", iterator.next());
		assertEquals("Pallas", iterator.next());
		assertEquals("Vesta", iterator.next());
	}

	/**
	 * Prueba la ordenación con todos los datos sin tener en cuenta mayúsculas y
	 * minúsculas
	 */
	public void testDataCaseInsensitive() {
		Collection<String> vcards = createData("caris", "caras", "Caros", "Cares");
		Iterator<String> iterator = vcards.iterator();

		assertEquals("caras", iterator.next());
		assertEquals("Cares", iterator.next());
		assertEquals("caris", iterator.next());
		assertEquals("Caros", iterator.next());
	}

	/**
	 * Prueba la ordenación con datos con tilde
	 */
	public void testDiacritic() {
		Collection<String> vcards = createData("Oan", "Ábe", "Ace", "Áde", "Oañ", "Ábá", "Na", "Ábí", "Ña", "Acá", "Ací", "Oao", "Abò", "Àbu");
		Iterator<String> iterator = vcards.iterator();

		assertEquals("Ábá", iterator.next());
		assertEquals("Ábe", iterator.next());
		assertEquals("Ábí", iterator.next());
		assertEquals("Abò", iterator.next());
		assertEquals("Àbu", iterator.next());
		assertEquals("Acá", iterator.next());
		assertEquals("Ace", iterator.next());
		assertEquals("Ací", iterator.next());
		assertEquals("Áde", iterator.next());
		assertEquals("Na", iterator.next());
		assertEquals("Ña", iterator.next());
		assertEquals("Oan", iterator.next());
		assertEquals("Oañ", iterator.next());
		assertEquals("Oao", iterator.next());
	}

	/**
	 * Crea un set de String ordenado a partir de las cadenas
	 * 
	 * @param strings
	 *            Cadenas utilizadas para ordenar
	 * @return Conjunto de cadenas ordenadas
	 */
	private Set<String> createData(String... strings) {
		SortedSet<String> set = new TreeSet<>(new StringLocaleComparator());
		for (String string : strings) {
			set.add(string);
		}
		return set;
	}

}
