package com.danielcorroto.directorius.model.searchalgorithm;

import com.danielcorroto.directorius.model.searchalgorithm.impl.SearchAlgorithmByAll;
import com.danielcorroto.directorius.model.searchalgorithm.impl.SearchAlgorithmByCategory;
import com.danielcorroto.directorius.model.searchalgorithm.impl.SearchAlgorithmByName;
import com.danielcorroto.directorius.model.searchalgorithm.impl.SearchAlgorithmByPhone;

/**
 * Factoría de algoritmos de búsqueda
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class SearchAlgorithmFactory {
	/**
	 * Crea una nueva instancia del algoritmo de búsqueda por todos los campos
	 * 
	 * @return Algoritmo de búsqueda por todos los campos
	 */
	public static SearchAlgorithm buildSearchAlgorithmByAll() {
		return new SearchAlgorithmByAll();
	}

	/**
	 * Crea una nueva instancia del algoritmo de búsqueda por categorías
	 * 
	 * @return Algoritmo de búsqueda por categorías
	 */
	public static SearchAlgorithm buildSearchAlgorithmByCategory() {
		return new SearchAlgorithmByCategory();
	}

	/**
	 * Crea una nueva instancia del algoritmo de búsqueda por nombre
	 * 
	 * @return Algoritmo de búsqueda por nombre
	 */
	public static SearchAlgorithm buildSearchAlgorithmByName() {
		return new SearchAlgorithmByName();
	}

	/**
	 * Crea una nueva instancia del algoritmo de búsqueda por teléfono
	 * 
	 * @return Algoritmo de búsqueda por teléfono
	 */
	public static SearchAlgorithm buildSearchAlgorithmByPhone() {
		return new SearchAlgorithmByPhone();
	}
}
