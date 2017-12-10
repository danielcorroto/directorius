package com.danielcorroto.directorius.controller.data;

import java.util.ResourceBundle;

/**
 * Muestra la información de teléfono/email/dirección en una línea
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class DisplayUtil {
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
		if (info.getStreet() != null && !info.getStreet().trim().isEmpty()) {
			builder.append(info.getStreet().trim());
		}
		if (info.getLocality() != null && !info.getLocality().trim().isEmpty()) {
			if (builder.length() != 0) {
				builder.append(", ");
			}
			builder.append(info.getLocality().trim());
		}
		if (info.getRegion() != null && !info.getRegion().trim().isEmpty()) {
			if (builder.length() != 0) {
				builder.append(", ");
			}
			builder.append(info.getRegion().trim());
		}
		if (info.getPostalCode() != null && !info.getPostalCode().trim().isEmpty()) {
			if (builder.length() != 0) {
				builder.append(", ");
			}
			builder.append(info.getPostalCode().trim());
		}
		if (info.getCountry() != null && !info.getCountry().trim().isEmpty()) {
			if (builder.length() != 0) {
				builder.append(", ");
			}
			builder.append(info.getCountry().trim());
		}

		builder.append(" (" + rb.getString(info.getType().getI18n()) + ")");
		if (info.getTag() != null && !info.getTag().isEmpty()) {
			builder.append(" - " + info.getTag());
		}
		return builder.toString();
	}
}
