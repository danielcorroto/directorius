package com.danielcorroto.directorius.controller;

import java.text.DateFormatSymbols;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import com.danielcorroto.directorius.controller.data.AddressInfo;
import com.danielcorroto.directorius.controller.data.EmailInfo;
import com.danielcorroto.directorius.controller.data.PhoneInfo;
import com.danielcorroto.directorius.model.Utils;
import com.danielcorroto.directorius.view.Text;

import ezvcard.VCard;

/**
 * Utilidades para mostrar cadenas a partir de i18n ResourceBundle
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class DisplayUtil {
	/**
	 * Milisegundos de medio año (366 * 86400 * 1000 / 2)
	 */
	private static final long HALF_YEAR_MILLIS = 15811200000L;

	/**
	 * Campo de fecha desconocido
	 */
	private static final String UNKNOWN_DATE_DATA = "??";

	/**
	 * Muestra la información del teléfono en 1 línea
	 * 
	 * @param info
	 *            Información del teléfono
	 * @param rb
	 *            Para i18n
	 * @return Cadena del teléfono
	 */
	public static String getPhoneInfo(PhoneInfo info, ResourceBundle rb) {
		StringBuilder builder = new StringBuilder();
		builder.append(info.getNumber());
		builder.append(" (");
		builder.append(rb.getString(info.getType().getI18n()));
		builder.append(")");
		if (info.getTag() != null && !info.getTag().isEmpty()) {
			builder.append(" - ");
			builder.append(info.getTag());
		}
		return builder.toString();
	}

	/**
	 * Muestra la información del email en 1 línea
	 * 
	 * @param info
	 *            Información del email
	 * @param rb
	 *            Para i18n
	 * @return Cadena del email
	 */
	public static String getEmailInfo(EmailInfo info, ResourceBundle rb) {
		StringBuilder builder = new StringBuilder();
		builder.append(info.getEmail());
		builder.append(" (");
		builder.append(rb.getString(info.getType().getI18n()));
		builder.append(")");
		if (info.getTag() != null && !info.getTag().isEmpty()) {
			builder.append(" - ");
			builder.append(info.getTag());
		}
		return builder.toString();
	}

	/**
	 * Muestra la información de la dirección en 1 línea
	 * 
	 * @param info
	 *            Información de la dirección teléfono
	 * @param rb
	 *            Para i18n
	 * @return Cadena de la dirección
	 */
	public static String getAddressInfo(AddressInfo info, ResourceBundle rb) {
		StringBuilder builder = new StringBuilder();
		if (!Utils.isBlank(info.getStreet())) {
			builder.append(info.getStreet().trim());
		}
		if (!Utils.isBlank(info.getLocality())) {
			if (builder.length() != 0) {
				builder.append(", ");
			}
			builder.append(info.getLocality().trim());
		}
		if (!Utils.isBlank(info.getRegion())) {
			if (builder.length() != 0) {
				builder.append(", ");
			}
			builder.append(info.getRegion().trim());
		}
		if (!Utils.isBlank(info.getPostalCode())) {
			if (builder.length() != 0) {
				builder.append(", ");
			}
			builder.append(info.getPostalCode().trim());
		}
		if (!Utils.isBlank(info.getCountry())) {
			if (builder.length() != 0) {
				builder.append(", ");
			}
			builder.append(info.getCountry().trim());
		}

		builder.append(" (" + rb.getString(info.getType().getI18n()) + ")");
		if (!Utils.isBlank(info.getTag())) {
			builder.append(" - " + info.getTag());
		}
		return builder.toString();
	}

	/**
	 * Genera una cadena con la fecha de nacimiento y edad del contacto teniendo
	 * en cuenta la fecha actual y el recurso de i18n. Por ejemplo: "17 de
	 * diciembre de 1987 ( 30 años )"
	 * 
	 * @param vcard
	 *            Información del contacto
	 * @param nextBday
	 *            Indica si hay que calcular la edad para el próximo cumpleaños
	 * @param rb
	 *            Recurso para i18n
	 * @return Código HTML de la fecha de nacimiento del contacto
	 */
	public static String buildBirthday(VCard vcard, boolean nextBday, ResourceBundle rb) {
		if (vcard.getBirthday() == null) {
			return "";
		}

		Integer year = null;
		Integer month = null;
		Integer day = null;
		Integer age = null;

		// Fecha completa
		if (vcard.getBirthday().getDate() != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(vcard.getBirthday().getDate());
			year = c.get(Calendar.YEAR);
			month = c.get(Calendar.MONTH) + 1;
			day = c.get(Calendar.DATE);

			age = getAgeFrom(year, month, day, nextBday);
		}

		// Fecha parcial
		if (vcard.getBirthday().getPartialDate() != null) {
			year = vcard.getBirthday().getPartialDate().getYear();
			month = vcard.getBirthday().getPartialDate().getMonth();
			day = vcard.getBirthday().getPartialDate().getDate();
		}

		String datePattern = rb.getString(Text.I18N_CONTACT_DATE_FORMAT);
		String result = formatDate(datePattern, year, month, day);
		if (age != null) {
			String agePattern = rb.getString(Text.I18N_CONTACT_AGE_FORMAT);
			result += " ( " + MessageFormat.format(agePattern, age) + " )";
		}
		return result;
	}

	/**
	 * Genera una cadena con la fecha indicada teniendo en cuenta el recurso de
	 * i18n. Por ejemplo: "17 de diciembre de 1987"
	 * 
	 * @param date
	 *            Fecha a convertir en cadena
	 * @param rb
	 *            Recurso para i18n
	 * @return Fecha convertida en texto
	 */
	public static String buildDate(Date date, ResourceBundle rb) {
		if (date == null) {
			return "";
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DATE);
		String datePattern = rb.getString(Text.I18N_CONTACT_DATE_FORMAT);
		return formatDate(datePattern, year, month, day);
	}

	/**
	 * Calcula la edad a partir de una fecha
	 * 
	 * @param year
	 *            Año de nacimiento
	 * @param month
	 *            Mes de nacimiento (1-12)
	 * @param day
	 *            Día de nacimiento (1-31)
	 * @param nextBday
	 *            Indica si hay que calcular la edad para el próximo cumpleaños
	 *            si cae próximo
	 * @return Edad
	 */
	private static int getAgeFrom(int year, int month, int day, boolean nextBday) {
		int age = 0;
		Calendar dob = Calendar.getInstance();
		dob.set(Calendar.YEAR, year);
		dob.set(Calendar.MONTH, month - 1);
		dob.set(Calendar.DATE, day);
		Calendar now = Calendar.getInstance();
		if (dob.after(now)) {
			throw new IllegalArgumentException("Fecha de nacimiento en el futuro");
		}
		int year1 = now.get(Calendar.YEAR);
		int year2 = dob.get(Calendar.YEAR);
		age = year1 - year2;

		if (nextBday) {
			// Comprueba si hay que sumar un año porque el cumpleaños caiga en
			// el
			// próximo año
			Calendar currentDob = Calendar.getInstance();
			currentDob.set(Calendar.MONTH, month - 1);
			currentDob.set(Calendar.DATE, day);

			if (Math.abs(currentDob.getTime().getTime() - now.getTime().getTime()) > HALF_YEAR_MILLIS) {
				age++;
			}
		}

		return age;
	}

	/**
	 * Formatea una fecha en formato texto según el idioma
	 * 
	 * @param datePattern
	 *            Patrón para formatear. {0} es año, {1} es mes y {2} es día
	 * @param year
	 *            Año de nacimiento
	 * @param month
	 *            Mes de nacimiento (1-12)
	 * @param day
	 *            Día de nacimiento (1-31)
	 * @return Fecha en formato texto
	 */
	private static String formatDate(String datePattern, Integer year, Integer month, Integer day) {
		String syear = year != null ? year.toString() : UNKNOWN_DATE_DATA;
		String smonth = month != null ? DateFormatSymbols.getInstance().getMonths()[month - 1] : UNKNOWN_DATE_DATA;
		String sday = day != null ? day.toString() : UNKNOWN_DATE_DATA;
		String result = MessageFormat.format(datePattern, syear, smonth, sday);
		return result;
	}
}
