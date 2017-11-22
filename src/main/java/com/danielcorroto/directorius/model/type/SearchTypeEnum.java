package com.danielcorroto.directorius.model.type;

import com.danielcorroto.directorius.view.Text;

/**
 * Tipos de búsqueda que se pueden realizar sobre la lista de contactos
 * 
 * @author Daniel Corroto Quirós
 *
 */
public enum SearchTypeEnum {
	/**
	 * Búsqueda sobre todos los campos
	 */
	ALL(Text.I18N_TOOLBAR_SEARCHTYPE_ALL),
	/**
	 * Búsqueda a partir de la categoría/tag
	 */
	CATEGORY(Text.I18N_TOOLBAR_SEARCHTYPE_CATEGORY),
	/**
	 * Búsqueda a partir del nombre
	 */
	NAME(Text.I18N_TOOLBAR_SEARCHTYPE_NAME),
	/**
	 * Búsqueda a partir del número de teléfono
	 */
	PHONE(Text.I18N_TOOLBAR_SEARCHTYPE_PHONE),;

	/**
	 * Cadena para realizar i18n del tipo de búsqueda
	 */
	private String i18n;

	/**
	 * Constructor por defecto
	 * 
	 * @param i18n
	 *            Cadena para realizar i18n del tipo de búsqueda
	 */
	private SearchTypeEnum(String i18n) {
		this.i18n = i18n;
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
	 * Indica el tipo de búsqueda por defecto
	 * 
	 * @return Tipo de búsqueda por defecto
	 */
	public static final SearchTypeEnum getDefault() {
		return NAME;
	}

}
