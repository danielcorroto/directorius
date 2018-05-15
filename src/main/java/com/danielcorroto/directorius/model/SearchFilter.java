package com.danielcorroto.directorius.model;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.danielcorroto.directorius.model.type.SearchTypeEnum;

/**
 * Datos a partir de los que se puede filtrar la búsqueda
 * 
 * @author Daniel Corroto Quirós
 */
public class SearchFilter {
	/**
	 * Patrón para cortar una cadena a partir del espacio pero sin incluir las
	 * comillas
	 */
	private static final Pattern SEARCH_PATTERN = Pattern.compile("\"([^\"]*)\"|(\\S+)"); // Pattern.compile("([^\"]\\S*|\".+?\")\\s*");

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
	 * Separa en cadenas la cadena separada por espacios pero agrupando cadenas
	 * rodeadas por comillas dobles. Por ejemplo:
	 * <ul>
	 * <li>qwerty -> [querty]
	 * <li>qwerty "asdf ñlkj" -> [qwerty, asdf ñlkj]
	 * <li>qwerty "asdf ñlkj" "zxcv mnb" -> [qwerty, asdf ñlkj, zxcv mnb]
	 * </ul>
	 * 
	 * @return Cadenas separadas
	 */
	public Set<String> getSplittedText() {
		Set<String> res = new HashSet<>();

		Matcher m = SEARCH_PATTERN.matcher(text);
		while (m.find()) {
			if (m.group(1) != null) {
				res.add(m.group(1));
			} else {
				res.add(m.group(2).replace("\"", ""));
			}
		}

		return res;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SearchFilter [category=");
		builder.append(category);
		builder.append(", text=");
		builder.append(text);
		builder.append(", type=");
		builder.append(type);
		builder.append("]");
		return builder.toString();
	}

}
