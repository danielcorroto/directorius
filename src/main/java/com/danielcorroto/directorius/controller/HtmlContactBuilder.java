package com.danielcorroto.directorius.controller;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import com.danielcorroto.directorius.controller.type.AddressTypeEnum;
import com.danielcorroto.directorius.controller.type.EmailTypeEnum;
import com.danielcorroto.directorius.controller.type.PhoneTypeEnum;
import com.danielcorroto.directorius.model.CustomParameter;
import com.danielcorroto.directorius.model.log.Logger;
import com.danielcorroto.directorius.view.ResourcePath;
import com.danielcorroto.directorius.view.Text;

import ezvcard.VCard;
import ezvcard.parameter.AddressType;
import ezvcard.parameter.EmailType;
import ezvcard.parameter.TelephoneType;
import ezvcard.property.Address;
import ezvcard.property.Email;
import ezvcard.property.Telephone;

/**
 * Constructor del HTML para mostrar la información del contacto
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class HtmlContactBuilder {
	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger.getLogger(HtmlContactBuilder.class);
	/**
	 * Conjunto de colores para la imagen por defecto
	 */
	private static final String[] COLOR = new String[] { "0", "5", "9", "c" };
	/**
	 * Salto de línea HTML
	 */
	private static final String NEW_LINE = "<br/>";

	/**
	 * Construye el código HTML de la información del contacto
	 * 
	 * @param vcard
	 *            Información del contacto
	 * @param photoDir
	 *            Directorio donde se ubican las fotografías
	 * @return Código HTML
	 */
	public static String build(VCard vcard, String photoDir) {
		String html = loadFileFromResource(ResourcePath.HTML);
		ResourceBundle rb = ResourceBundle.getBundle(Text.RESOURCE_BUNDLE, Locale.getDefault());

		// CSS
		html = html.replace(HtmlTemplate.CSS_BOOTSTRAP, MainWindowController.class.getResource(ResourcePath.CSS_BOOTSTRAP).toString());
		html = html.replace(HtmlTemplate.CSS_VCARD, MainWindowController.class.getResource(ResourcePath.CSS_VCARD).toString());

		// Foto
		html = html.replace(HtmlTemplate.SECTION_PHOTO, buildPhoto(vcard, photoDir));

		// Personal
		html = html.replace(HtmlTemplate.FULL_NAME, vcard.getFormattedName().getValue());
		html = html.replace(HtmlTemplate.BIRTHDAY, DisplayUtil.buildBirthday(vcard, false, rb));
		html = html.replace(HtmlTemplate.NOTES, buildNotes(vcard));
		html = html.replace(HtmlTemplate.GROUP_CATEGORY, buildGroupCategory(vcard));

		// Teléfono
		html = html.replace(HtmlTemplate.GROUP_PHONE, buildGroupPhone(vcard, rb));

		// Correo
		html = html.replace(HtmlTemplate.GROUP_EMAIL, buildGroupEmail(vcard, rb));

		// Dirección
		html = html.replace(HtmlTemplate.GROUP_ADDRESS, buildGroupAddress(vcard, rb));

		// Fecha de última edición
		html = html.replace(HtmlTemplate.LAST_EDITION, buildLastEdition(vcard, rb));

		return html;
	}

	/**
	 * Genera el código HTML con la dirección del contacto o una imagen SVG con
	 * las iniciales
	 * 
	 * @param vcard
	 *            Información del contacto
	 * @return Ruta de la foto o imagen SVG
	 */
	private static String buildPhoto(VCard vcard, String photoDir) {
		String result = null;

		// Obtiene la foto, si está asignada y existe el fichero
		if (vcard.getPhotos() != null && !vcard.getPhotos().isEmpty()) {
			String path = photoDir + vcard.getPhotos().get(0).getUrl();
			File f = new File(path);
			if (f.exists() && !f.isDirectory()) {
				// Se añade query parameter para que no lea la imagen de cache
				// si ha sido modificada
				result = f.toURI().toString() + "?" + vcard.getRevision().getValue();
			}
		}

		// Genera una imagen con las iniciales si no ha encontrado la foto
		if (result == null) {
			result = loadFileFromResource(ResourcePath.HTML_SECTION_PHOTO_DEFAULT);
			result = result.replace(HtmlTemplate.PHOTO_COLOR, createColor(vcard));
			StringBuilder initial = new StringBuilder();
			if (vcard.getStructuredName() != null && vcard.getStructuredName().getGiven() != null && !vcard.getStructuredName().getGiven().isEmpty()) {
				initial.append(vcard.getStructuredName().getGiven().substring(0, 1));
			}
			if (vcard.getStructuredName() != null && vcard.getStructuredName().getFamily() != null && !vcard.getStructuredName().getFamily().isEmpty()) {
				initial.append(vcard.getStructuredName().getFamily().substring(0, 1));
			}
			result = result.replace(HtmlTemplate.PHOTO_INITIAL, initial);
		}

		return result;
	}

	/**
	 * Genera el código HTML con las notas del contacto
	 * 
	 * @param vcard
	 *            Información del contacto
	 * @return Código HTML de las notas del contacto
	 */
	private static String buildNotes(VCard vcard) {
		if (vcard.getNotes() == null || vcard.getNotes().isEmpty() || vcard.getNotes().get(0) == null || vcard.getNotes().get(0).getValue() == null) {
			return "";
		}
		String result = vcard.getNotes().get(0).getValue();
		result = result.replaceAll("(\r\n|\n)", NEW_LINE);
		return result;
	}

	/**
	 * Genera el código HTML con las categorías del contacto
	 * 
	 * @param vcard
	 *            Información del contacto
	 * @return Código HTML de las categorías del contacto
	 */
	private static String buildGroupCategory(VCard vcard) {
		if (vcard.getCategories() == null || vcard.getCategories().getValues() == null || vcard.getCategories().getValues().isEmpty()) {
			return "";
		}

		String result = loadFileFromResource(ResourcePath.HTML_GROUP_CATEGORY);
		String template = loadFileFromResource(ResourcePath.HTML_SECTION_CATEGORY);

		StringBuilder categories = new StringBuilder();
		for (String category : vcard.getCategories().getValues()) {
			categories.append(template.replace(HtmlTemplate.CATEGORY, category));
		}
		result = result.replace(HtmlTemplate.SECTION_CATEGORY, categories);

		return result;
	}

	/**
	 * Genera el código HTML con los teléfonos del contacto
	 * 
	 * @param vcard
	 *            Información del contacto
	 * @param rb
	 *            Recurso para i18n
	 * @return Código HTML de los teléfonos del contacto
	 */
	private static String buildGroupPhone(VCard vcard, ResourceBundle rb) {
		if (vcard.getTelephoneNumbers() == null || vcard.getTelephoneNumbers().isEmpty()) {
			return "";
		}

		String result = loadFileFromResource(ResourcePath.HTML_GROUP_PHONE);
		String template = loadFileFromResource(ResourcePath.HTML_SECTION_PHONE);

		StringBuilder phones = new StringBuilder();
		for (Telephone phone : vcard.getTelephoneNumbers()) {
			String phoneTemplate = new String(template);
			phoneTemplate = phoneTemplate.replace(HtmlTemplate.PHONE, phone.getText());
			if (phone.getParameter(CustomParameter.TELEPHONE_TAG) != null) {
				phoneTemplate = phoneTemplate.replace(HtmlTemplate.PHONE_TEXT, phone.getParameter(CustomParameter.TELEPHONE_TAG));
			} else {
				phoneTemplate = phoneTemplate.replace(HtmlTemplate.PHONE_TEXT, "");
			}
			if (phone.getTypes() != null && phone.getTypes().get(0) != null && phone.getTypes().get(0).getValue() != null) {
				TelephoneType type = phone.getTypes().get(0);
				phoneTemplate = phoneTemplate.replace(HtmlTemplate.PHONE_TYPE, rb.getString(PhoneTypeEnum.getI18n(type)));
			} else {
				phoneTemplate = phoneTemplate.replace(HtmlTemplate.PHONE_TYPE, "");
			}
			phones.append(phoneTemplate);
		}
		result = result.replace(HtmlTemplate.SECTION_PHONE, phones);

		return result;
	}

	/**
	 * Genera el código HTML con los correos del contacto
	 * 
	 * @param vcard
	 *            Información del contacto
	 * @param rb
	 *            Recurso para i18n
	 * @return Código HTML de los correos del contacto
	 */
	private static String buildGroupEmail(VCard vcard, ResourceBundle rb) {
		if (vcard.getEmails() == null || vcard.getEmails().isEmpty()) {
			return "";
		}

		String result = loadFileFromResource(ResourcePath.HTML_GROUP_EMAIL);
		String template = loadFileFromResource(ResourcePath.HTML_SECTION_EMAIL);

		StringBuilder emails = new StringBuilder();
		for (Email email : vcard.getEmails()) {
			String emailTemplate = new String(template);
			emailTemplate = emailTemplate.replace(HtmlTemplate.EMAIL, email.getValue());
			if (email.getParameter(CustomParameter.EMAIL_TAG) != null) {
				emailTemplate = emailTemplate.replace(HtmlTemplate.EMAIL_TEXT, email.getParameter(CustomParameter.EMAIL_TAG));
			} else {
				emailTemplate = emailTemplate.replace(HtmlTemplate.EMAIL_TEXT, "");
			}
			if (email.getTypes() != null && email.getTypes().get(0) != null && email.getTypes().get(0).getValue() != null) {
				EmailType type = email.getTypes().get(0);
				emailTemplate = emailTemplate.replace(HtmlTemplate.EMAIL_TYPE, rb.getString(EmailTypeEnum.getI18n(type)));
			} else {
				emailTemplate = emailTemplate.replace(HtmlTemplate.EMAIL_TYPE, "");
			}
			emails.append(emailTemplate);
		}
		result = result.replace(HtmlTemplate.SECTION_EMAIL, emails);

		return result;
	}

	/**
	 * Genera el código HTML con las direcciones del contacto
	 * 
	 * @param vcard
	 *            Información del contacto
	 * @param rb
	 *            Recurso para i18n
	 * @return Código HTML de las direcciones del contacto
	 */
	private static String buildGroupAddress(VCard vcard, ResourceBundle rb) {
		if (vcard.getAddresses() == null || vcard.getAddresses().isEmpty()) {
			return "";
		}

		String result = loadFileFromResource(ResourcePath.HTML_GROUP_ADDRESS);
		String template = loadFileFromResource(ResourcePath.HTML_SECTION_ADDRESS);

		StringBuilder addresses = new StringBuilder();
		for (Address address : vcard.getAddresses()) {
			String addressTemplate = new String(template);
			addressTemplate = addressTemplate.replace(HtmlTemplate.ADDRESS, buildAddress(address));
			if (address.getParameter(CustomParameter.ADDRESS_TAG) != null) {
				addressTemplate = addressTemplate.replace(HtmlTemplate.ADDRESS_TEXT, address.getParameter(CustomParameter.ADDRESS_TAG));
			} else {
				addressTemplate = addressTemplate.replace(HtmlTemplate.ADDRESS_TEXT, "");
			}
			if (address.getTypes() != null && address.getTypes().get(0) != null && address.getTypes().get(0).getValue() != null) {
				AddressType type = address.getTypes().get(0);
				addressTemplate = addressTemplate.replace(HtmlTemplate.ADDRESS_TYPE, rb.getString(AddressTypeEnum.getI18n(type)));
			} else {
				addressTemplate = addressTemplate.replace(HtmlTemplate.ADDRESS_TYPE, "");
			}
			addresses.append(addressTemplate);
		}
		result = result.replace(HtmlTemplate.SECTION_ADDRESS, addresses);

		return result;
	}

	/**
	 * Genera el código HTML con una dirección
	 * 
	 * @param address
	 *            Información de la dirección
	 * @return Código HTML de una dirección del contacto
	 */
	private static String buildAddress(Address address) {
		StringBuilder sb = new StringBuilder();
		if (address.getStreetAddress() != null) {
			sb.append(address.getStreetAddress()).append(NEW_LINE);
		}
		if (address.getPostalCode() != null) {
			sb.append(address.getPostalCode());
			if (address.getLocality() != null) {
				sb.append(", ");
			} else {
				sb.append(NEW_LINE);
			}
		}
		if (address.getLocality() != null) {
			sb.append(address.getLocality()).append(NEW_LINE);
		}
		if (address.getRegion() != null) {
			sb.append(address.getRegion()).append(NEW_LINE);
		}
		if (address.getCountry() != null) {
			sb.append(address.getCountry());
		}

		return sb.toString();
	}

	/**
	 * Genera el código HTML con la fecha de última edición
	 * 
	 * @param vcard
	 *            Información del contacto
	 * @param rb
	 *            Recurso para i18n
	 * @return Código HTML de la fecha de última edición del contacto
	 */
	private static String buildLastEdition(VCard vcard, ResourceBundle rb) {
		String date = DisplayUtil.buildDate(vcard.getRevision().getValue(), rb);
		String lastEditionPattern = rb.getString(Text.I18N_CONTACT_LASTEDITION);
		return MessageFormat.format(lastEditionPattern, date);
	}

	/**
	 * Calcula el color a mostrar en la foto en caso de no existir
	 * 
	 * @param vcard
	 *            Información del contacto
	 * @return Color en hexadecimal. Por ejemplo "a0f0f0"
	 */
	private static String createColor(VCard vcard) {
		int value = Math.abs(vcard.getUid().hashCode()) % (COLOR.length * COLOR.length * COLOR.length);
		int r = value / COLOR.length / COLOR.length;
		int g = value / COLOR.length % COLOR.length;
		int b = value % COLOR.length;
		String hexColor = COLOR[r] + COLOR[g] + COLOR[b];
		return "#" + hexColor;
	}

	/**
	 * Lee el fichero recurso indicado
	 * 
	 * @param resourcePath
	 *            Ruta del recurso
	 * @return Contenido del fichero
	 */
	private static String loadFileFromResource(String resourcePath) {
		try {
			InputStream is = HtmlContactBuilder.class.getResourceAsStream(resourcePath);

			final int bufferSize = 8192;
			final char[] buffer = new char[bufferSize];
			final StringBuilder out = new StringBuilder();
			Reader in = new InputStreamReader(is, "UTF-8");
			for (;;) {
				int rsz = in.read(buffer, 0, buffer.length);
				if (rsz < 0)
					break;
				out.append(buffer, 0, rsz);
			}
			return out.toString();
		} catch (Exception e) {
			LOGGER.severe("Error al cargar el fichero del recurso " + resourcePath, e);
		}

		return "";
	}

}
