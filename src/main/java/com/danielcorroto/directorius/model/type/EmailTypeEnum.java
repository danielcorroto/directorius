package com.danielcorroto.directorius.model.type;

import com.danielcorroto.directorius.view.Text;

import ezvcard.parameter.EmailType;

/**
 * Tipo de correo
 * 
 * @author Daniel Corroto Quirós
 *
 */
public enum EmailTypeEnum {
	/**
	 * Dirección personal
	 */
	HOME(Text.I18N_INFORMATION_EMAIL_TYPE_HOME, EmailType.HOME),
	/**
	 * Dirección de trabajo
	 */
	WORK(Text.I18N_INFORMATION_EMAIL_TYPE_WORK, EmailType.WORK),;

	/**
	 * Cadena para realizar i18n del tipo de búsqueda
	 */
	private String i18n;
	/**
	 * VCard TYPE
	 */
	private EmailType type;

	/**
	 * Constructor por defecto
	 * 
	 * @param i18n
	 *            Cadena para realizar i18n del tipo de búsqueda
	 * @param type
	 *            Tipo de dirección
	 */
	private EmailTypeEnum(String i18n, EmailType type) {
		this.i18n = i18n;
		this.type = type;
	}

	/**
	 * Obtiene cadena para realizar i18n
	 * 
	 * @param type
	 *            Tipo de dirección
	 * 
	 * @return Cadena para realizar i18n
	 */
	public static String getI18n(EmailType type) {
		for (EmailTypeEnum element : values()) {
			if (element.type.equals(type)) {
				return element.i18n;
			}
		}
		return "";
	}
}
