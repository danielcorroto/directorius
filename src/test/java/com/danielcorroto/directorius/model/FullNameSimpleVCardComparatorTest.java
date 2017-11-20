package com.danielcorroto.directorius.model;

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
 *
 */
public class FullNameSimpleVCardComparatorTest extends TestCase {

	/**
	 * Prueba la ordenación sin elemntos
	 */
	public void testEmpty() {
		Collection<SimpleVCard> vcards = createData();

		assertTrue(vcards.isEmpty());
	}

	/**
	 * Prueba la ordenación donde alguno de los elementos tienen valor null
	 */
	public void testSomeNull() {
		Collection<SimpleVCard> vcards = createData(null, "Ceres");
		Iterator<SimpleVCard> iterator = vcards.iterator();

		assertNull(iterator.next().getFormattedName());
		assertEquals("Ceres", iterator.next().getFormattedName().getValue());
	}

	/**
	 * Prueba la ordenación con todos los datos
	 */
	public void testData() {
		Collection<SimpleVCard> vcards = createData("Ceres", "Pallas", "Juno", "Vesta", "Astraea", "Hebe");
		Iterator<SimpleVCard> iterator = vcards.iterator();

		assertEquals("Astraea", iterator.next().getFormattedName().getValue());
		assertEquals("Ceres", iterator.next().getFormattedName().getValue());
		assertEquals("Hebe", iterator.next().getFormattedName().getValue());
		assertEquals("Juno", iterator.next().getFormattedName().getValue());
		assertEquals("Pallas", iterator.next().getFormattedName().getValue());
		assertEquals("Vesta", iterator.next().getFormattedName().getValue());
	}

	/**
	 * Prueba la ordenación con todos los datos sin tener en cuenta mayúsculas y
	 * minúsculas
	 */
	public void testDataCaseInsensitive() {
		Collection<SimpleVCard> vcards = createData("caris", "caras", "Caros", "Cares");
		Iterator<SimpleVCard> iterator = vcards.iterator();

		assertEquals("caras", iterator.next().getFormattedName().getValue());
		assertEquals("Cares", iterator.next().getFormattedName().getValue());
		assertEquals("caris", iterator.next().getFormattedName().getValue());
		assertEquals("Caros", iterator.next().getFormattedName().getValue());
	}

	/**
	 * Prueba la ordenación con datos con tilde
	 */
	public void testDiacritic() {
		Collection<SimpleVCard> vcards = createData("Oan", "Ábe", "Ace", "Áde", "Oañ", "Ábá", "Na", "Ábí", "Ña", "Acá", "Ací", "Oao", "Abò", "Àbu");
		Iterator<SimpleVCard> iterator = vcards.iterator();

		assertEquals("Ábá", iterator.next().getFormattedName().getValue());
		assertEquals("Ábe", iterator.next().getFormattedName().getValue());
		assertEquals("Ábí", iterator.next().getFormattedName().getValue());
		assertEquals("Abò", iterator.next().getFormattedName().getValue());
		assertEquals("Àbu", iterator.next().getFormattedName().getValue());
		assertEquals("Acá", iterator.next().getFormattedName().getValue());
		assertEquals("Ace", iterator.next().getFormattedName().getValue());
		assertEquals("Ací", iterator.next().getFormattedName().getValue());
		assertEquals("Áde", iterator.next().getFormattedName().getValue());
		assertEquals("Na", iterator.next().getFormattedName().getValue());
		assertEquals("Ña", iterator.next().getFormattedName().getValue());
		assertEquals("Oan", iterator.next().getFormattedName().getValue());
		assertEquals("Oañ", iterator.next().getFormattedName().getValue());
		assertEquals("Oao", iterator.next().getFormattedName().getValue());
	}

	/**
	 * Crea un set de VCard ordenado a partir de los nombres
	 * 
	 * @param names
	 *            Nombres utilizados para ordenar
	 * @return Conjunto de VCard ordenado por nombre
	 */
	private Set<SimpleVCard> createData(String... names) {
		SortedSet<SimpleVCard> set = new TreeSet<>(new FullNameSimpleVCardComparator());
		for (String name : names) {
			set.add(createVCard(name));
		}
		return set;
	}

	/**
	 * Crea un objeto VCard V4.0
	 * 
	 * @param name
	 *            Nombre utilizado como Formatted Name
	 * @return Objeto VCard con el nombre
	 */
	private SimpleVCard createVCard(String name) {
		SimpleVCard vcard = new SimpleVCard();
		vcard.setFormattedName(name);
		return vcard;
	}
}
