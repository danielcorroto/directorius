package com.danielcorroto.directorius.model;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import ezvcard.VCard;
import ezvcard.VCardVersion;
import ezvcard.property.Uid;
import junit.framework.TestCase;

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
		VCard vcard = createVCard("Test");
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
		VCard vcard = createVCard("Test");
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
		VCard vcard = createVCard("Test");
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
		VCard vcard = createVCard("Test");
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
		VCard vcard = createVCard("Test");
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
		VCard vcard = createVCard("Test");
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
		VCard vcard = createVCard("Test");
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
		VCard vcard = createVCard("Test");
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
		VCard vcard1 = createVCard("Test");
		cm.createContact(vcard1);
		VCard vcard2 = createVCard("Otro");
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
		VCard vcard1 = createVCard("Test");
		cmOriginal.createContact(vcard1);
		VCard vcard2 = createVCard("Otro");
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
	 * @throws IOException 
	 */
	public void testCreateLoadUpdateGetAll() throws IOException {
		File file = createTempFile();
		ContactManager cmOriginal = new ContactManager(file.getAbsolutePath());
		VCard vcard1 = createVCard("Test");
		cmOriginal.createContact(vcard1);
		VCard vcard2 = createVCard("Otro");
		cmOriginal.createContact(vcard2);

		ContactManager cmReaded = ContactManager.loadFile(file.getAbsolutePath());
		vcard2.setFormattedName("ZZZ");
		cmReaded.updateContact(vcard2);
		Set<SimpleVCard> all = cmReaded.getAllSimpleVCard();
		Iterator<SimpleVCard> iterator = all.iterator();
		assertEquals(2, all.size());
		assertEquals(vcard1.getFormattedName().getValue(), iterator.next().getFormattedName().getValue());
		assertEquals(vcard2.getFormattedName().getValue(), iterator.next().getFormattedName().getValue());
		// TODO
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
	 * Crea un contacto con el nombre indicado y un Uid aleatorio
	 * 
	 * @param name
	 *            Nombre del contacto.
	 * @return Contacto VCard
	 */
	private VCard createVCard(String name) {
		VCard vcard = new VCard(VCardVersion.V4_0);
		vcard.setFormattedName(name);
		vcard.setUid(Uid.random());
		return vcard;
	}
}
