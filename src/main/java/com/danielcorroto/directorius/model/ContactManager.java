package com.danielcorroto.directorius.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.property.Uid;

/**
 * Gestión de los contactos, búsqueda, persistencia, etc
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class ContactManager {
	/**
	 * Fichero que contiene el fichero para cargar la agenda por defecto
	 */
	private static final String CONFIG_FILE = "config.info";
	/**
	 * Clave de la propiedad de la ruta fichero que contiene la agenda
	 */
	private static final String PROPERTIES_FILE = "file";

	/**
	 * Ruta del fichero VCard
	 */
	private File file;

	/**
	 * Mapa de Contactos. Key: UID. Value: VCard
	 */
	private Map<Uid, VCard> vcardMap;

	/**
	 * Colección sencilla de contactos ordenados por nombre
	 */
	private Map<Uid, SimpleVCard> simpleVcardMap;

	/**
	 * Constructor vacío. Utilizar {@link #loadFile(String)} para cargar datos
	 * del fichero
	 */
	private ContactManager() {
	}

	/**
	 * Crea un nuevo fichero para almacenar los datos
	 * 
	 * @param path
	 *            Ruta donde se va a almacenar el fichero
	 * @throws IOException
	 */
	public ContactManager(String path) throws IOException {
		file = new File(path);
		init();
		saveFile();
	}

	/**
	 * Inicia las variables del manager (si no están vacías)
	 */
	private void init() {
		if (vcardMap == null || !vcardMap.isEmpty() || simpleVcardMap == null || !simpleVcardMap.isEmpty()) {
			vcardMap = new HashMap<>();
			simpleVcardMap = new HashMap<>();
		}
	}

	/**
	 * Añade un contacto y almacena el fichero
	 * 
	 * @param vcard
	 *            Infomación del contacto
	 * @throws IOException
	 */
	public void createContact(VCard vcard) throws IOException {
		VCard copy = new VCard(vcard);
		vcardMap.put(copy.getUid(), copy);
		simpleVcardMap.put(copy.getUid(), new SimpleVCard(copy));

		saveFile();
	}

	/**
	 * Obtiene un contacto a partir de su identificador
	 * 
	 * @param id
	 *            Identificador del contacto
	 * @return Contacto encontrado o null
	 */
	public VCard readContact(Uid id) {
		return vcardMap.get(id);
	}

	/**
	 * Actualiza los datos de un contacto. Si no existía, lo crea
	 * 
	 * @param vcard
	 *            Información del contacto
	 * @throws IOException
	 */
	public void updateContact(VCard vcard) throws IOException {
		VCard previous = vcardMap.get(vcard.getUid());
		if (previous != null) {
			deleteContact(vcard.getUid());
		}
		createContact(vcard);
	}

	/**
	 * Elimina un contacto a partir de su identificador
	 * 
	 * @param id
	 *            Identificador del contacto
	 * @throws IOException
	 */
	public void deleteContact(Uid id) throws IOException {
		vcardMap.remove(id);
		simpleVcardMap.remove(id);

		saveFile();
	}

	/**
	 * Obtiene la información sencilla de todos los contactos
	 * 
	 * @return Información sencilla (nombre+uid) de todos los contactos
	 */
	public Set<SimpleVCard> getAllSimpleVCard() {
		Set<SimpleVCard> set = new TreeSet<>(new FullNameSimpleVCardComparator());
		set.addAll(simpleVcardMap.values());
		return set;
	}

	/**
	 * Busca el fichero de configuración e intenta cargar el fichero por defecto
	 * 
	 * @return ContactManager con la información cargada o null si no se ha
	 *         podido cargar
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static ContactManager autoLoadFile() throws FileNotFoundException, IOException {
		// Comprueba existencia del fichero de configuración
		String currentPath = System.getProperty("user.dir");
		File configFile = new File(currentPath + File.separator + CONFIG_FILE);
		if (!configFile.exists()) {
			return null;
		}

		// Carga los datos del fichero de configuración
		Properties properties = new Properties();
		InputStream is = new FileInputStream(configFile);
		properties.load(is);
		is.close();
		String filePath = properties.getProperty(PROPERTIES_FILE);
		if (filePath == null) {
			return null;
		}
		
		return loadFile(filePath);
	}

	/**
	 * Crea una instancia del administrador de contactos y carga el fichero
	 * VCard en memoria
	 * 
	 * @param path
	 *            Ruta absoluta del fichero
	 * @return Instancia del administrador de contactos con los datos cargados
	 * @throws IOException
	 */
	public static ContactManager loadFile(String path) throws IOException {
		ContactManager cm = new ContactManager();
		cm.file = new File(path);
		List<VCard> vcards = Ezvcard.parse(cm.file).all();

		cm.init();

		for (VCard vcard : vcards) {
			cm.createContact(vcard);
		}
		return cm;
	}

	/**
	 * Guarda los datos de los contactos en el fichero cargado
	 * 
	 * @throws IOException
	 */
	private void saveFile() throws IOException {
		Ezvcard.write(vcardMap.values()).go(file);
	}
}
