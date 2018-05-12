package com.danielcorroto.directorius.model;

import com.danielcorroto.directorius.model.type.SearchTypeEnum;

/**
 * Datos a partir de los que se puede filtrar la búsqueda
 * 
 * @author Daniel Corroto Quirós
 */
public class SearchFilter {
	/**
	 * Categoría buscada
	 */
	private String category;
	/**
	 * Texto buscado
	 */
	private String text;
	/**
	 * Tipo de búsqueda
	 */
	private SearchTypeEnum type;

	/**
	 * 
	 * @param category
	 *            Categoría buscada
	 * @param text
	 *            Texto buscado
	 * @param type
	 *            Tipo de búsqueda
	 */
	public SearchFilter(String category, String text, SearchTypeEnum type) {
		super();
		this.category = category;
		this.text = text;
		this.type = type;
	}

	/**
	 * Devuelve la categoría buscada
	 * 
	 * @return Categoría buscada
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * Establece la categoría buscada
	 * 
	 * @param category
	 *            Categoría buscada
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * Devuelve el texto buscado
	 * 
	 * @return Texto buscado
	 */
	public String getText() {
		return text;
	}

	/**
	 * Establece el texto buscado
	 * 
	 * @param text
	 *            Texto buscado
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Devuelve el tipo de búsqueda
	 * 
	 * @return Tipo de búsqueda
	 */
	public SearchTypeEnum getType() {
		return type;
	}

	/**
	 * Establece el tipo de búsqueda
	 * 
	 * @param type
	 *            Tipo de búsqueda
	 */
	public void setType(SearchTypeEnum type) {
		this.type = type;
	}

}
