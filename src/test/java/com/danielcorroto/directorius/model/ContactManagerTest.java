package com.danielcorroto.directorius.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.Set;

import com.danielcorroto.directorius.TestUtil;
import com.danielcorroto.directorius.model.type.SearchTypeEnum;

import ezvcard.VCard;
import junit.framework.TestCase;

/**
 * Test del administrador de contactos
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class ContactManagerTest extends TestCase {
	/**
	 * Prueba la creación de un nuevo fichero
	 * 
	 * @throws IOException
	 */
	public void testNew() throws IOException {
		new ContactManager(createTempFile().getAbsolutePath());
	}

	/**
	 * Prueba la creación y lectura
	 * 
	 * @throws IOException
	 */
	public void testCreateRead() throws IOException {
		ContactManager cm = new ContactManager(createTempFile().getAbsolutePath());
		VCard vcard = TestUtil.createVCard("Test");
		cm.createContact(vcard);
		VCard readed = cm.readContact(vcard.getUid());

		assertEquals(vcard.getUid(), readed.getUid());
		assertEquals(vcard.getFormattedName().getValue(), readed.getFormattedName().getValue());
		assertEquals(1, cm.getAllSimpleVCard().size());
	}

	/**
	 * Prueba la actualización y lectura
	 * 
	 * @throws IOException
	 */
	public void testUpdateRead() throws IOException {
		ContactManager cm = new ContactManager(createTempFile().getAbsolutePath());
		VCard vcard = TestUtil.createVCard("Test");
		cm.createContact(vcard);
		vcard.setFormattedName("Otro");
		cm.updateContact(vcard);
		VCard readed = cm.readContact(vcard.getUid());

		assertEquals(vcard.getUid(), readed.getUid());
		assertEquals(vcard.getFormattedName().getValue(), readed.getFormattedName().getValue());
		assertEquals(1, cm.getAllSimpleVCard().size());
	}

	/**
	 * Prueba la modificación del contacto pero no la actualización en el
	 * administrador
	 * 
	 * @throws IOException
	 */
	public void testNotUpdateRead() throws IOException {
		ContactManager cm = new ContactManager(createTempFile().getAbsolutePath());
		VCard vcard = TestUtil.createVCard("Test");
		cm.createContact(vcard);
		vcard.setFormattedName("Otro");
		VCard readed = cm.readContact(vcard.getUid());

		assertEquals(vcard.getUid(), readed.getUid());
		assertFalse(vcard.getFormattedName().getValue().equals(readed.getFormattedName().getValue()));
		assertEquals(1, cm.getAllSimpleVCard().size());
	}

	/**
	 * Prueba el borrado y lectura
	 * 
	 * @throws IOException
	 */
	public void testDeleteRead() throws IOException {
		ContactManager cm = new ContactManager(createTempFile().getAbsolutePath());
		VCard vcard = TestUtil.createVCard("Test");
		cm.createContact(vcard);
		cm.deleteContact(vcard.getUid());
		VCard readed = cm.readContact(vcard.getUid());

		assertNull(readed);
		assertEquals(0, cm.getAllSimpleVCard().size());
	}

	/**
	 * Prueba la carga de fichero
	 * 
	 * @throws IOException
	 */
	public void testLoad() throws IOException {
		File file = createTempFile();
		ContactManager cmOriginal = new ContactManager(file.getAbsolutePath());
		ContactManager cmReaded = ContactManager.loadFile(file.getAbsolutePath());

		assertEquals(cmOriginal.getAllSimpleVCard().size(), cmReaded.getAllSimpleVCard().size());
	}

	/**
	 * Prueba la creación, carga de fichero y lectura
	 * 
	 * @throws IOException
	 */
	public void testCreateLoadRead() throws IOException {
		File file = createTempFile();
		VCard vcard = TestUtil.createVCard("Test");
		ContactManager cmOriginal = new ContactManager(file.getAbsolutePath());
		cmOriginal.createContact(vcard);

		ContactManager cmReaded = ContactManager.loadFile(file.getAbsolutePath());
		VCard readed = cmReaded.readContact(vcard.getUid());

		assertEquals(vcard.getUid(), readed.getUid());
		assertEquals(vcard.getFormattedName().getValue(), readed.getFormattedName().getValue());
		assertEquals(1, cmReaded.getAllSimpleVCard().size());
	}

	/**
	 * Prueba la creación, actualización, carga de fichero y lectura
	 * 
	 * @throws IOException
	 */
	public void testCreateUpdateLoadRead() throws IOException {
		File file = createTempFile();
		ContactManager cmOriginal = new ContactManager(file.getAbsolutePath());
		VCard vcard = TestUtil.createVCard("Test");
		cmOriginal.createContact(vcard);
		vcard.setFormattedName("Otro");
		cmOriginal.updateContact(vcard);

		ContactManager cmReaded = ContactManager.loadFile(file.getAbsolutePath());
		VCard readed = cmReaded.readContact(vcard.getUid());

		assertEquals(vcard.getUid(), readed.getUid());
		assertEquals(vcard.getFormattedName().getValue(), readed.getFormattedName().getValue());
		assertEquals(1, cmReaded.getAllSimpleVCard().size());
	}

	/**
	 * Prueba la creación, borrado, carga del fichero y lectura
	 * 
	 * @throws IOException
	 */
	public void testCreateDeleteLoadRead() throws IOException {
		VCard vcard = TestUtil.createVCard("Test");
		File file = createTempFile();
		ContactManager cmOriginal = new ContactManager(file.getAbsolutePath());
		cmOriginal.createContact(vcard);
		cmOriginal.deleteContact(vcard.getUid());

		ContactManager cmReaded = ContactManager.loadFile(file.getAbsolutePath());
		VCard readed = cmReaded.readContact(vcard.getUid());

		assertNull(readed);
		assertEquals(0, cmReaded.getAllSimpleVCard().size());
	}

	/**
	 * Prueba las operaciones de crear / leer / actualizar / borrar y para cada
	 * una de ellas carga el fichero
	 * 
	 * @throws IOException
	 */
	public void testMultiRead() throws IOException {
		// Creacion
		VCard vcard = TestUtil.createVCard("Test");
		File file = createTempFile();
		ContactManager cmOriginal = new ContactManager(file.getAbsolutePath());
		cmOriginal.createContact(vcard);
		ContactManager cmReaded;
		VCard readed;

		// Carga y lectura
		cmReaded = ContactManager.loadFile(file.getAbsolutePath());
		readed = cmReaded.readContact(vcard.getUid());

		assertEquals(vcard.getUid(), readed.getUid());
		assertEquals(vcard.getFormattedName().getValue(), readed.getFormattedName().getValue());
		assertEquals(1, cmReaded.getAllSimpleVCard().size());

		// Actualizacion, carga y lectura
		vcard.setFormattedName("Otro");
		cmOriginal.updateContact(vcard);
		cmReaded = ContactManager.loadFile(file.getAbsolutePath());
		readed = cmReaded.readContact(vcard.getUid());

		assertEquals(vcard.getUid(), readed.getUid());
		assertEquals(vcard.getFormattedName().getValue(), readed.getFormattedName().getValue());
		assertEquals(1, cmReaded.getAllSimpleVCard().size());

		// Borrado, carga y lectura
		cmOriginal.deleteContact(vcard.getUid());
		cmReaded = ContactManager.loadFile(file.getAbsolutePath());
		readed = cmReaded.readContact(vcard.getUid());

		assertNull(readed);
		assertEquals(0, cmReaded.getAllSimpleVCard().size());
	}

	/**
	 * Prueba la lectura de los registros SimpleVCard
	 * 
	 * @throws IOException
	 */
	public void testCreateGetAll() throws IOException {
		ContactManager cm = new ContactManager(createTempFile().getAbsolutePath());
		VCard vcard1 = TestUtil.createVCard("Test");
		cm.createContact(vcard1);
		VCard vcard2 = TestUtil.createVCard("Otro");
		cm.createContact(vcard2);

		Set<SimpleVCard> all = cm.getAllSimpleVCard();
		Iterator<SimpleVCard> iterator = all.iterator();
		assertEquals(2, all.size());
		assertEquals(vcard2.getFormattedName().getValue(), iterator.next().getFormattedName().getValue());
		assertEquals(vcard1.getFormattedName().getValue(), iterator.next().getFormattedName().getValue());
	}

	/**
	 * Prueba la creación, la carga del fichero y lectura de los registros
	 * SimpleVCard
	 * 
	 * @throws IOException
	 */
	public void testCreateLoadGetAll() throws IOException {
		File file = createTempFile();
		ContactManager cmOriginal = new ContactManager(file.getAbsolutePath());
		VCard vcard1 = TestUtil.createVCard("Test");
		cmOriginal.createContact(vcard1);
		VCard vcard2 = TestUtil.createVCard("Otro");
		cmOriginal.createContact(vcard2);

		ContactManager cmReaded = ContactManager.loadFile(file.getAbsolutePath());
		Set<SimpleVCard> all = cmReaded.getAllSimpleVCard();
		Iterator<SimpleVCard> iterator = all.iterator();
		assertEquals(2, all.size());
		assertEquals(vcard2.getFormattedName().getValue(), iterator.next().getFormattedName().getValue());
		assertEquals(vcard1.getFormattedName().getValue(), iterator.next().getFormattedName().getValue());
	}

	/**
	 * Prueba la creación, la carga del fichero, actualización y lectura de los
	 * registros SimpleVCard
	 * 
	 * @throws IOException
	 */
	public void testCreateLoadUpdateGetAll() throws IOException {
		File file = createTempFile();
		ContactManager cmOriginal = new ContactManager(file.getAbsolutePath());
		VCard vcard1 = TestUtil.createVCard("Test");
		cmOriginal.createContact(vcard1);
		VCard vcard2 = TestUtil.createVCard("Otro");
		cmOriginal.createContact(vcard2);

		ContactManager cmReaded = ContactManager.loadFile(file.getAbsolutePath());
		vcard2.setFormattedName("ZZZ");
		cmReaded.updateContact(vcard2);
		Set<SimpleVCard> all = cmReaded.getAllSimpleVCard();
		Iterator<SimpleVCard> iterator = all.iterator();
		assertEquals(2, all.size());
		assertEquals(vcard1.getFormattedName().getValue(), iterator.next().getFormattedName().getValue());
		assertEquals(vcard2.getFormattedName().getValue(), iterator.next().getFormattedName().getValue());
	}

	/**
	 * Carga automática del fichero de propiedades inexistente
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public void testAutoLoadFileNull() throws FileNotFoundException, IOException, URISyntaxException {
		File configFile = getConfigFile();
		configFile.delete();

		ContactManager manager;

		manager = ContactManager.autoLoadFile();
		assertNull(manager);
	}

	/**
	 * Carga automática del fichero de propiedades existente pero el fichero
	 * vcard no existe
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public void testAutoLoadFileNotExist() throws FileNotFoundException, IOException, URISyntaxException {
		File configFile = getConfigFile();
		configFile.delete();

		ContactManager manager = null;

		File tempFile = createTempFile();
		tempFile.delete();

		try (BufferedWriter writer = Files.newBufferedWriter(configFile.toPath())) {
			writer.write("file=" + tempFile.getAbsolutePath().replaceAll("\\\\", "\\\\\\\\"));
			writer.close();
		}

		try {
			manager = ContactManager.autoLoadFile();
			fail();
		} catch (FileNotFoundException e) {
			boolean deleted = configFile.delete();
			assertTrue(deleted);
			assertNull(manager);
		}
	}

	/**
	 * Carga automática del fichero de propiedades existente pero el fichero
	 * vcard no existe
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public void testAutoLoadFile() throws FileNotFoundException, IOException, URISyntaxException {
		File configFile = getConfigFile();

		ContactManager manager = null;

		File tempFile = createTempFile();

		try (BufferedWriter writer = Files.newBufferedWriter(configFile.toPath())) {
			writer.write("file=" + tempFile.getAbsolutePath().replaceAll("\\\\", "\\\\\\\\"));
			writer.close();
		}

		manager = ContactManager.autoLoadFile();
		boolean deleted = configFile.delete();
		assertNotNull(manager);
		assertTrue(deleted);
	}

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
		// Creacion
		File file = createTempFile();
		ContactManager cm = new ContactManager(file.getAbsolutePath());

		String text = "qwerty";
		Method m = ContactManager.class.getMethod("splitSearchText", String.class);
		m.setAccessible(true);
		@SuppressWarnings("unchecked")
		Set<String> res = (Set<String>) m.invoke(cm, text);

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
		// Creacion
		File file = createTempFile();
		ContactManager cm = new ContactManager(file.getAbsolutePath());

		String text = "\"qwerty poiuy\"";
		Method m = ContactManager.class.getMethod("splitSearchText", String.class);
		m.setAccessible(true);
		@SuppressWarnings("unchecked")
		Set<String> res = (Set<String>) m.invoke(cm, text);

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
		// Creacion
		File file = createTempFile();
		ContactManager cm = new ContactManager(file.getAbsolutePath());

		String text = "asdf \"qwerty poiuy\" \"zxc vbn\" ";
		Method m = ContactManager.class.getMethod("splitSearchText", String.class);
		m.setAccessible(true);
		@SuppressWarnings("unchecked")
		Set<String> res = (Set<String>) m.invoke(cm, text);

		assertEquals(3, res.size());
		assertTrue(res.contains("asdf"));
		assertTrue(res.contains("qwerty poiuy"));
		assertTrue(res.contains("zxc vbn"));
	}

	/**
	 * Prueba la búsqueda de contactos por todos los campos
	 * 
	 * @throws IOException
	 */
	public void testSearchByAll() throws IOException {
		ContactManager cm = new ContactManager(createTempFile().getAbsolutePath());
		VCard vcard1 = TestUtil.createVCardAll("test", "rqewrqw", null, null, null, null, null, null);
		cm.createContact(vcard1);
		VCard vcard2 = TestUtil.createVCardAll("Otros", "fdsa a", null, null, null, null, null, null);
		cm.createContact(vcard2);
		VCard vcard3 = TestUtil.createVCardAll("MáS", "rqwer", null, null, null, new String[] { "asr@u.net" }, null, null);
		cm.createContact(vcard3);

		Set<SimpleVCard> result = cm.search("U", SearchTypeEnum.ALL);

		Iterator<SimpleVCard> iterator = result.iterator();
		assertEquals(1, result.size());
		assertEquals(vcard3.getFormattedName().getValue(), iterator.next().getFormattedName().getValue());
	}

	/**
	 * Prueba la búsqueda de contactos por categoría
	 * 
	 * @throws IOException
	 */
	public void testSearchByCategory() throws IOException {
		ContactManager cm = new ContactManager(createTempFile().getAbsolutePath());
		VCard vcard1 = TestUtil.createVCardCategories("test", "rqewrqw");
		cm.createContact(vcard1);
		VCard vcard2 = TestUtil.createVCardCategories("Otros", "fdsa a", "bncv");
		cm.createContact(vcard2);
		VCard vcard3 = TestUtil.createVCardCategories("MáS", "rqwer");
		cm.createContact(vcard3);

		Set<SimpleVCard> result = cm.search("S", SearchTypeEnum.CATEGORY);

		Iterator<SimpleVCard> iterator = result.iterator();
		assertEquals(1, result.size());
		assertEquals(vcard2.getFormattedName().getValue(), iterator.next().getFormattedName().getValue());
	}

	/**
	 * Prueba la búsqueda de contactos por nombre
	 * 
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public void testSearchByName() throws IOException {
		ContactManager cm = new ContactManager(createTempFile().getAbsolutePath());
		VCard vcard1 = TestUtil.createVCard("test");
		cm.createContact(vcard1);
		VCard vcard2 = TestUtil.createVCard("Otro");
		cm.createContact(vcard2);
		VCard vcard3 = TestUtil.createVCard("MáS");
		cm.createContact(vcard3);

		Set<SimpleVCard> result = cm.search("S", SearchTypeEnum.NAME);

		Iterator<SimpleVCard> iterator = result.iterator();
		assertEquals(2, result.size());
		assertEquals(vcard3.getFormattedName().getValue(), iterator.next().getFormattedName().getValue());
		assertEquals(vcard1.getFormattedName().getValue(), iterator.next().getFormattedName().getValue());
	}

	/**
	 * Prueba la búsqueda de contactos por teléfono
	 * 
	 * @throws IOException
	 */
	public void testSearchByPhone() throws IOException {
		ContactManager cm = new ContactManager(createTempFile().getAbsolutePath());
		VCard vcard1 = TestUtil.createVCardPhones("test 95 2", "046 868");
		cm.createContact(vcard1);
		VCard vcard2 = TestUtil.createVCardPhones("Otros 95 2", "7168", "7681 816");
		cm.createContact(vcard2);
		VCard vcard3 = TestUtil.createVCardPhones("MáS 95 2", "689 5214");
		cm.createContact(vcard3);

		Set<SimpleVCard> result = cm.search("95 2", SearchTypeEnum.PHONE);

		Iterator<SimpleVCard> iterator = result.iterator();
		assertEquals(1, result.size());
		assertEquals(vcard3.getFormattedName().getValue(), iterator.next().getFormattedName().getValue());
	}

	/**
	 * Prueba el listado de categorías
	 * 
	 * @throws IOException
	 */
	public void testGetCategories() throws IOException {
		ContactManager cm = new ContactManager(createTempFile().getAbsolutePath());
		VCard vcard1 = TestUtil.createVCardCategories("abc", "aa", "bb", "cc");
		cm.createContact(vcard1);
		VCard vcard2 = TestUtil.createVCardCategories("abc", "bb", "dd", "ff");
		cm.createContact(vcard2);
		VCard vcard3 = TestUtil.createVCardCategories("abc", "bb", "ee", "hh");
		cm.createContact(vcard3);
		VCard vcard4 = TestUtil.createVCard("abc");
		cm.createContact(vcard4);

		assertEquals(7, cm.getCategories().size());
		assertTrue(cm.getCategories().contains("aa"));
		assertTrue(cm.getCategories().contains("bb"));
		assertTrue(cm.getCategories().contains("cc"));
		assertTrue(cm.getCategories().contains("dd"));
		assertTrue(cm.getCategories().contains("ee"));
		assertTrue(cm.getCategories().contains("ff"));
		assertTrue(cm.getCategories().contains("hh"));
	}

	/**
	 * Crea un fichero temporal
	 * 
	 * @return Descriptor del fichero temporal
	 * @throws IOException
	 */
	private File createTempFile() throws IOException {
		File temp = File.createTempFile("directorius", ".tmp");
		return temp;
	}

	/**
	 * Obtiene el fichero de configuración
	 * 
	 * @return Fichero de configuración
	 * @throws MalformedURLException
	 * @throws URISyntaxException
	 */
	private File getConfigFile() throws MalformedURLException, URISyntaxException {
		String currentPath = ContactManager.class.getProtectionDomain().getCodeSource().getLocation().toString();
		if (currentPath.lastIndexOf('/') + 1 < currentPath.length()) {
			currentPath = currentPath.substring(0, currentPath.lastIndexOf('/') + 1);
		}
		String filename = currentPath + "config.info";
		File configFile = new File(new URL(filename).toURI());
		return configFile;
	}
}
