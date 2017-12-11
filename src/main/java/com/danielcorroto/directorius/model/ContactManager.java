package com.danielcorroto.directorius.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.danielcorroto.directorius.model.searchalgorithm.SearchAlgorithm;
import com.danielcorroto.directorius.model.type.SearchTypeEnum;

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
	 * Patrón para cortar una cadena a partir del espacio pero sin incluir las
	 * comillas
	 */
	private static final Pattern searchPattern = Pattern.compile("\"([^\"]*)\"|(\\S+)"); // Pattern.compile("([^\"]\\S*|\".+?\")\\s*");
	/**
	 * Charset para la lectura/escritura de fichero
	 */
	private static final Charset CHARSET_DEFAULT = Charset.forName("UTF-8");
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
	 * Colección de categorías
	 */
	private Set<String> categoriesSet;

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
		if (vcardMap == null || !vcardMap.isEmpty() || simpleVcardMap == null || !simpleVcardMap.isEmpty() || categoriesSet == null || categoriesSet.isEmpty()) {
			vcardMap = new HashMap<>();
			simpleVcardMap = new HashMap<>();
			categoriesSet = new TreeSet<>();
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
		if (vcardMap.containsKey(vcard.getUid())) {
			vcard.setUid(Uid.random());
			return;
		}

		loadMemoryContact(vcard);
		saveFile();
	}

	/**
	 * Carga el contacto en memoria
	 * 
	 * @param vcard
	 *            Información del contacto
	 */
	private void loadMemoryContact(VCard vcard) {
		VCard copy = new VCard(vcard);
		vcardMap.put(copy.getUid(), copy);
		simpleVcardMap.put(copy.getUid(), new SimpleVCard(copy));
		if (vcard.getCategories() != null && vcard.getCategories().getValues() != null) {
			for (String category : vcard.getCategories().getValues()) {
				categoriesSet.add(category);
			}
		}
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
	 * Realiza una búsqueda en los contactos
	 * 
	 * @param text
	 *            Texto a buscar. Los literales pueden ir entre comillas dobles.
	 *            Busca los contatos que coinciden con todas las búsqudas
	 * @param type
	 *            Tipo de búsqueda: nombre, todo el texto, etc
	 * @return Lista de contactos encontrados
	 */
	public Set<SimpleVCard> search(String text, SearchTypeEnum type) {
		if (text == null || text.trim().isEmpty()) {
			return getAllSimpleVCard();
		}
		Set<String> searchTexts = splitSearchText(text);
		SearchAlgorithm searchAlgorithm = type.getSearchAlgorithm();
		Set<SimpleVCard> result = searchAlgorithm.search(vcardMap.values(), searchTexts);
		Set<SimpleVCard> sorted = new TreeSet<>(new FullNameSimpleVCardComparator());
		sorted.addAll(result);
		return sorted;
	}

	/**
	 * Separa en cadenas la cadena separada por espacios pero agrupando cadenas
	 * rodeadas por comillas dobles. Por ejemplo:
	 * <ul>
	 * <li>qwerty -> [querty]
	 * <li>qwerty "asdf ñlkj" -> [qwerty, asdf ñlkj]
	 * <li>qwerty "asdf ñlkj" "zxcv mnb" -> [qwerty, asdf ñlkj, zxcv mnb]
	 * </ul>
	 * 
	 * @param text
	 *            Texto a trocear
	 * @return
	 */
	public Set<String> splitSearchText(String text) {
		Set<String> res = new HashSet<>();

		Matcher m = searchPattern.matcher(text);
		while (m.find()) {
			if (m.group(1) != null) {
				res.add(m.group(1));
			} else {
				res.add(m.group(2).replace("\"", ""));
			}
		}

		return res;
	}

	public Set<String> getCategories() {
		return new TreeSet<>(categoriesSet);
	}

	/**
	 * Busca el fichero de configuración e intenta cargar el fichero por defecto
	 * 
	 * @return ContactManager con la información cargada o null si no se ha
	 *         podido cargar
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static ContactManager autoLoadFile() throws FileNotFoundException, IOException, URISyntaxException {
		// Comprueba existencia del fichero de configuración
		String currentPath = ContactManager.class.getProtectionDomain().getCodeSource().getLocation().toString();
		if (currentPath.lastIndexOf('/') + 1 < currentPath.length()) {
			currentPath = currentPath.substring(0, currentPath.lastIndexOf('/') + 1);
		}
		String filename = currentPath + CONFIG_FILE;
		File configFile = new File(new URL(filename).toURI());

		if (configFile == null || !configFile.exists()) {
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
	 * Obtiene el directorio de fotografías
	 * 
	 * @return Ruta absoluta del directorio de fotografías
	 */
	public String getPhotoDir() {
		String path = file.getAbsolutePath();
		return path.substring(0, path.lastIndexOf('.')) + ".photo" + File.separator;
	}

	/**
	 * Copia el fichero indicado en el directorio de fotografías con el nombre
	 * indicado
	 * 
	 * @param source
	 *            Fichero de origen
	 * @param destPath
	 *            Nombre del fichero (sin ruta ni extensión)
	 * @return Nombre del fichero con extensión
	 * @throws IOException
	 */
	public String savePhotoFile(File source, String destPath) throws IOException {
		FileChannel sourceChannel = null;
		FileChannel destChannel = null;
		FileInputStream fis = null;
		FileOutputStream fos = null;
		String result = null;

		String extension = source.getName().substring(source.getName().lastIndexOf('.'));
		result = destPath + extension;
		String fullDestPath = getPhotoDir() + result;
		File dest = new File(fullDestPath);
		
		// Si los ficheros son el mismo, salir
		if (source.equals(dest)) {
			return result;
		}
		
		try {
			fis = new FileInputStream(source);
			sourceChannel = fis.getChannel();

			fos = new FileOutputStream(dest);
			destChannel = fos.getChannel();

			destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
		} finally {
			fis.close();
			fos.close();
			sourceChannel.close();
			destChannel.close();
		}

		return result;
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
		FileInputStream fis = new FileInputStream(cm.file);
		List<VCard> vcards = Ezvcard.parse(new InputStreamReader(fis, CHARSET_DEFAULT)).all();
		fis.close();

		cm.init();

		for (VCard vcard : vcards) {
			cm.loadMemoryContact(vcard);
		}
		return cm;
	}

	/**
	 * Guarda los datos de los contactos en el fichero cargado
	 * 
	 * @throws IOException
	 */
	private void saveFile() throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		Ezvcard.write(vcardMap.values()).go(new OutputStreamWriter(fos, CHARSET_DEFAULT));
	}
}
