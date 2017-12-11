package com.danielcorroto.directorius.controller.type;

import com.danielcorroto.directorius.view.Text;

import ezvcard.parameter.AddressType;

/**
 * Tipo de dirección
 * 
 * @author Daniel Corroto Quirós
 *
 */
public enum AddressTypeEnum {
	/**
	 * Dirección personal
	 */
	HOME(Text.I18N_INFORMATION_ADDRESS_TYPE_HOME, AddressType.HOME),
	/**
	 * Dirección de trabajo
	 */
	WORK(Text.I18N_INFORMATION_ADDRESS_TYPE_WORK, AddressType.WORK),;

	/**
	 * Cadena para realizar i18n del tipo de búsqueda
	 */
	private String i18n;
	/**
	 * VCard TYPE
	 */
	private AddressType type;

	/**
	 * Constructor por defecto
	 * 
	 * @param i18n
	 *            Cadena para realizar i18n del tipo de búsqueda
	 * @param type
	 *            Tipo de dirección
	 */
	private AddressTypeEnum(String i18n, AddressType type) {
		this.i18n = i18n;
		this.type = type;
	}

	/**
	 * Indica el tipo de dirección por defecto
	 * 
	 * @return Tipo de dirección por defecto
	 */
	public static AddressTypeEnum getDefault() {
		return AddressTypeEnum.HOME;
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
	public AddressType getAddressType() {
		return type;
	}

	/**
	 * Obtiene el enumerado a partir del tipo de dirección
	 * 
	 * @param type
	 *            Tipo de dirección vcard
	 * @return Elemento del enumerado
	 */
	public static AddressTypeEnum findByAddressType(AddressType type) {
		for (AddressTypeEnum element : values()) {
			if (element.type.equals(type)) {
				return element;
			}
		}
		return null;
	}

	/**
	 * Obtiene cadena para realizar i18n
	 * 
	 * @param type
	 *            Tipo de dirección
	 * 
	 * @return Cadena para realizar i18n
	 */
	public static String getI18n(AddressType type) {
		for (AddressTypeEnum element : values()) {
			if (element.type.equals(type)) {
				return element.i18n;
			}
		}
		return "";
	}
}
