package com.danielcorroto.directorius.controller.type;

import com.danielcorroto.directorius.view.Text;

import ezvcard.parameter.TelephoneType;

/**
 * Tipo de teléfono
 * 
 * @author Daniel Corroto Quirós
 *
 */
public enum PhoneTypeEnum {
	/**
	 * Teléfono móvil
	 */
	CELL(Text.I18N_INFORMATION_PHONE_TYPE_CELL, TelephoneType.CELL),
	/**
	 * Teléfono fax
	 */
	FAX(Text.I18N_INFORMATION_PHONE_TYPE_FAX, TelephoneType.FAX),
	/**
	 * Teléfono personal
	 */
	HOME(Text.I18N_INFORMATION_PHONE_TYPE_HOME, TelephoneType.HOME),
	/**
	 * Teléfono paginación
	 */
	PAGER(Text.I18N_INFORMATION_PHONE_TYPE_PAGER, TelephoneType.PAGER),
	/**
	 * Teléfono sms
	 */
	TEXT(Text.I18N_INFORMATION_PHONE_TYPE_TEXT, TelephoneType.TEXT),
	/**
	 * Teléfono de texto
	 */
	TEXTPHONE(Text.I18N_INFORMATION_PHONE_TYPE_TEXTPHONE, TelephoneType.TEXTPHONE),
	/**
	 * Teléfono vídeo
	 */
	VIDEO(Text.I18N_INFORMATION_PHONE_TYPE_VICEO, TelephoneType.VIDEO),
	/**
	 * Teléfono voz
	 */
	VOICE(Text.I18N_INFORMATION_PHONE_TYPE_VOICE, TelephoneType.VOICE),
	/**
	 * Teléfono de trabajo
	 */
	WORK(Text.I18N_INFORMATION_PHONE_TYPE_WORK, TelephoneType.WORK),;

	/**
	 * Cadena para realizar i18n del tipo de búsqueda
	 */
	private String i18n;
	/**
	 * VCard TYPE
	 */
	private TelephoneType type;

	/**
	 * Constructor por defecto
	 * 
	 * @param i18n
	 *            Cadena para realizar i18n del tipo de búsqueda
	 * @param type
	 *            Tipo de teléfono
	 */
	private PhoneTypeEnum(String i18n, TelephoneType type) {
		this.i18n = i18n;
		this.type = type;
	}

	/**
	 * Indica el tipo de teléfono por defecto
	 * 
	 * @return Tipo de teléfono por defecto
	 */
	public static PhoneTypeEnum getDefault() {
		return PhoneTypeEnum.HOME;
	}

	/**
	 * Obtiene cadena para realizar i18n
	 * 
	 * @return Cadena para realizar i18n
	 */
	public String getI18n() {
		return i18n;
	}

	/**
	 * Obtiene el tipo VCard
	 * 
	 * @return Tipo VCard
	 */
	public TelephoneType getTelephoneType() {
		return type;
	}

	/**
	 * Obtiene cadena para realizar i18n
	 * 
	 * @param type
	 *            Tipo de teléfono
	 * 
	 * @return Cadena para realizar i18n
	 */
	public static String getI18n(TelephoneType type) {
		for (PhoneTypeEnum element : values()) {
			if (element.type.equals(type)) {
				return element.i18n;
			}
		}
		return "";
	}
}
