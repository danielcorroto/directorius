package com.danielcorroto.directorius.model.type;

import com.danielcorroto.directorius.model.searchalgorithm.SearchAlgorithm;
import com.danielcorroto.directorius.model.searchalgorithm.SearchAlgorithmFactory;
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
	ALL(Text.I18N_TOOLBAR_SEARCHTYPE_ALL, SearchAlgorithmFactory.buildSearchAlgorithmByAll()),
	/**
	 * Búsqueda a partir de la categoría/tag
	 */
	CATEGORY(Text.I18N_TOOLBAR_SEARCHTYPE_CATEGORY, SearchAlgorithmFactory.buildSearchAlgorithmByCategory()),
	/**
	 * Búsqueda a partir del nombre
	 */
	NAME(Text.I18N_TOOLBAR_SEARCHTYPE_NAME, SearchAlgorithmFactory.buildSearchAlgorithmByName()),
	/**
	 * Búsqueda a partir del número de teléfono
	 */
	PHONE(Text.I18N_TOOLBAR_SEARCHTYPE_PHONE, SearchAlgorithmFactory.buildSearchAlgorithmByPhone()),;

	/**
	 * Cadena para realizar i18n del tipo de búsqueda
	 */
	private String i18n;
	/**
	 * Algoritmo de búsqueda
	 */
	private SearchAlgorithm searchAlgorithm;

	/**
	 * Constructor por defecto
	 * 
	 * @param i18n
	 *            Cadena para realizar i18n del tipo de búsqueda
	 * @param searchAlgorithm
	 *            Algoritmo que realiza búsquedas sobre un conjunto de contactos
	 */
	private SearchTypeEnum(String i18n, SearchAlgorithm searchAlgorithm) {
		this.i18n = i18n;
		this.searchAlgorithm = searchAlgorithm;
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
	 * Obtiene el algoritmo encargado de realizar la búsqueda
	 * 
	 * @return Objeto que contiene la lógica de búsqueda para el tipo indicado
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public SearchAlgorithm getSearchAlgorithm() {
		return searchAlgorithm;
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
