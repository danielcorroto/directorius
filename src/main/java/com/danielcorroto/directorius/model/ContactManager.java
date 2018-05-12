package com.danielcorroto.directorius.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.danielcorroto.directorius.controller.data.Statistics;
import com.danielcorroto.directorius.model.comparator.FullNameSimpleVCardComparator;
import com.danielcorroto.directorius.model.comparator.FullNameVCardComparator;
import com.danielcorroto.directorius.model.log.Logger;
import com.danielcorroto.directorius.model.searchalgorithm.SearchAlgorithm;

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
	 * Logger
	 */
	private static final Logger LOGGER = Logger.getLogger(ContactManager.class);
	/**
	 * Fichero que contiene el fichero para cargar la agenda por defecto
	 */
	private static final String CONFIG_FILE = "config.info";
	/**
	 * Clave de la propiedad de la ruta fichero que contiene la agenda
	 */
	private static final String PROPERTIES_FILE = "file";
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
	private SortedSet<String> categoriesSet;

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
			deleteContact(vcard.getUid(), false);
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
		deleteContact(id, true);
	}

	/**
	 * Elimina un contacto a partir de su identificador
	 * 
	 * @param id
	 *            Identificador del contacto
	 * @param removePhoto
	 *            Si hay que borrar la foto. false si va a ser para actualizar
	 *            el contacto.
	 * @throws IOException
	 */
	private void deleteContact(Uid id, boolean removePhoto) throws IOException {
		if (removePhoto) {
			removePhotoFile(vcardMap.get(id));
		}
		vcardMap.remove(id);
		simpleVcardMap.remove(id);

		saveFile();
	}

	/**
	 * Obtiene la información sencilla de todos los contactos
	 * 
	 * @return Información sencilla (nombre+uid) de todos los contactos
	 */
	public SortedSet<SimpleVCard> getAllSimpleVCard() {
		SortedSet<SimpleVCard> set = new TreeSet<>(new FullNameSimpleVCardComparator());
		set.addAll(simpleVcardMap.values());
		return set;
	}

	/**
	 * Realiza una búsqueda en los contactos
	 * 
	 * @param filter Filtro a partir del cual se debe realizar la búsqueda
	 * @return Lista de contactos encontrados
	 */
	public SortedSet<SimpleVCard> search(SearchFilter filter) {
		SearchAlgorithm searchAlgorithm = filter.getType().getSearchAlgorithm();
		Set<SimpleVCard> result = searchAlgorithm.search(vcardMap.values(), filter);
		SortedSet<SimpleVCard> sorted = new TreeSet<>(new FullNameSimpleVCardComparator());
		sorted.addAll(result);
		return sorted;
	}

	public SortedSet<String> getCategories() {
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
		File configFile = getConfigFile();
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

		return loadFile(filePath, false);
	}

	/**
	 * Genera la ruta del fichero de configuración
	 * 
	 * @return Fichero de configuración
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 */
	private static File getConfigFile() throws MalformedURLException, URISyntaxException {
		String currentPath = ContactManager.class.getProtectionDomain().getCodeSource().getLocation().toString();
		if (currentPath.lastIndexOf('/') + 1 < currentPath.length()) {
			currentPath = currentPath.substring(0, currentPath.lastIndexOf('/') + 1);
		}
		String filename = currentPath + CONFIG_FILE;
		File configFile = new File(new URL(filename).toURI());
		return configFile;
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
	 * @return Nombre del fichero con extensión o null si es el mismo que ya
	 *         existía
	 * @throws IOException
	 */
	public String savePhotoFile(File source, String destPath) throws IOException {
		// FileChannel sourceChannel = null;
		// FileChannel destChannel = null;
		// FileInputStream fis = null;
		// FileOutputStream fos = null;
		String result = null;

		String extension = source.getName().substring(source.getName().lastIndexOf('.'));
		result = destPath + extension;
		String photoPath = getPhotoDir();
		String fullDestPath = photoPath + result;
		File dest = new File(fullDestPath);

		// Intenta crear el directorio de fotografía
		File photoDir = new File(photoPath);
		if (!photoDir.exists()) {
			if (!photoDir.mkdirs()) {
				LOGGER.warning("No se ha podido crear el directorio " + photoPath);
				return null;
			}
		}

		// Si los ficheros son el mismo, salir
		if (source.equals(dest)) {
			return null;
		}

		try (FileInputStream fis = new FileInputStream(source);
				FileChannel sourceChannel = fis.getChannel();
				FileOutputStream fos = new FileOutputStream(dest);
				FileChannel destChannel = fos.getChannel()) {
			destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
		}

		return result;
	}

	/**
	 * Elimina la foto indicada
	 * 
	 * @param filename
	 *            Nombre del fichero (nombre + extensión)
	 * @throws IOException
	 */
	public void removePhotoFile(String filename) throws IOException {
		String fullDestPath = getPhotoDir() + filename;
		boolean deleted = new File(fullDestPath).delete();
		if (!deleted) {
			LOGGER.info("No se ha podido borrar " + filename);
		}
	}

	/**
	 * Elimina el fichero de la fotografía del contacto indicado (si tiene)
	 * 
	 * @param card
	 *            Información del contacto
	 */
	private void removePhotoFile(VCard card) {
		if (card.getPhotos() == null || card.getPhotos().isEmpty()) {
			return;
		}

		String path = getPhotoDir() + card.getPhotos().get(0).getUrl();
		File file = new File(path);
		file.delete();
	}

	/**
	 * Crea una instancia del administrador de contactos y carga el fichero
	 * VCard en memoria. Sobreescribe la información de configuración
	 * 
	 * @param path
	 *            Ruta absoluta del fichero
	 * @return Instancia del administrador de contactos con los datos cargados
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static ContactManager loadFile(String path) throws IOException, URISyntaxException {
		return loadFile(path, true);
	}

	/**
	 * Crea una instancia del administrador de contactos y carga el fichero
	 * VCard en memoria
	 * 
	 * @param path
	 *            Ruta absoluta del fichero
	 * @param writeConfig
	 *            Indica si se sobreescribe la información de configuración
	 * @return Instancia del administrador de contactos con los datos cargados
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private static ContactManager loadFile(String path, boolean writeConfig) throws IOException, URISyntaxException {
		if (writeConfig) {
			File configFile = getConfigFile();
			OutputStream out = new FileOutputStream(configFile);

			Properties properties = new Properties();
			properties.setProperty(PROPERTIES_FILE, path);

			properties.store(out, "");
			out.close();
		}
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

	/**
	 * Construye y rellena las estadísticas
	 * 
	 * @return Estadísticas de los contactos
	 */
	public Statistics getStatistic() {
		Statistics statistic = new Statistics();
		for (VCard vcard : vcardMap.values()) {
			statistic.addContact(vcard);
		}

		return statistic;
	}

	/**
	 * Obtiene los contactos que cumplen años entre las fechas indicas (ambas
	 * inclusive) ordenadas por fecha y nombre completo
	 * 
	 * @param start
	 *            Fecha inicial a comprobar
	 * @param end
	 *            Fecha final a comprobar
	 * @return Contactos que cumplen años
	 */
	public List<VCard> getBirthday(Date start, Date end) {
		Map<Date, Set<VCard>> map = new TreeMap<>();

		// Setea la fecha de inicio y fin con la hora 00:00:00
		start = setTimeZero(start);
		end = setTimeZero(end);

		// Año de la fecha de inicio de búsqueda
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(start);
		int year = startCalendar.get(Calendar.YEAR);

		for (VCard card : vcardMap.values()) {
			if (card.getBirthday() == null) {
				continue;
			}

			// Obtiene fecha de nacimiento
			Calendar bdayCalendar = getBirthday(card);
			if (bdayCalendar == null) {
				continue;
			}

			// Obtiene la fecha (año) del siguiente cumpleaños desde la fecha de
			// inicio
			bdayCalendar.set(Calendar.YEAR, year);
			if (bdayCalendar.getTime().before(start)) {
				bdayCalendar.set(Calendar.YEAR, year + 1);
			}

			// Comprueba la fecha
			Date bday = bdayCalendar.getTime();
			bday = setTimeZero(bday);
			if ((start.before(bday) || start.equals(bday)) && (end.after(bday) || end.equals(bday))) {
				Set<VCard> cardSet = map.get(bday);
				if (cardSet == null) {
					cardSet = new TreeSet<>(new FullNameVCardComparator());
					map.put(bday, cardSet);
				}
				cardSet.add(new VCard(card));
			}
		}

		return buildBirthdayResult(map);
	}

	/**
	 * Obtiene la fecha de nacimiento.
	 * 
	 * @param card
	 *            Información del contacto
	 * @return Fecha de nacimiento. Si no se informa del año se obtiene la fecha
	 *         de cumpleaños del año presente. Si no se informa día o mes se
	 *         devuelve null
	 */
	private Calendar getBirthday(VCard card) {
		Calendar bdayCalendar = Calendar.getInstance();
		if (card.getBirthday().getDate() != null) {
			bdayCalendar.setTime(card.getBirthday().getDate());
		} else if (card.getBirthday().getPartialDate() != null) {
			// La fecha parcial es válida si hay día y mes
			if (card.getBirthday().getPartialDate().getMonth() == null || card.getBirthday().getPartialDate().getDate() == null) {
				bdayCalendar = null;
			} else {
				bdayCalendar.set(Calendar.MONTH, card.getBirthday().getPartialDate().getMonth() - 1);
				bdayCalendar.set(Calendar.DATE, card.getBirthday().getPartialDate().getDate());
			}
		} else {
			bdayCalendar = null;
		}

		return bdayCalendar;
	}

	/**
	 * Construye la lista ordenada de contactos según fecha de cumpleaños y
	 * nombre
	 * 
	 * @param map
	 *            Mapa con las fechas y contactos de los cumpleaños
	 * @return Lista ordenada de contactos según fecha de cumpleaños y nombre
	 */
	private List<VCard> buildBirthdayResult(Map<Date, Set<VCard>> map) {
		List<VCard> result = new ArrayList<>();
		for (Entry<Date, Set<VCard>> entry : map.entrySet()) {
			for (VCard card : entry.getValue()) {
				result.add(card);
			}
		}

		return result;
	}

	/**
	 * Setea la hora de la fecha a 00:00:00 del huso horario correspondiente
	 * 
	 * @param date
	 *            Fecha a la que se le va a asignar la hora 00:00:00
	 * @return Fecha con 00:00:00
	 */
	private Date setTimeZero(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}
}
