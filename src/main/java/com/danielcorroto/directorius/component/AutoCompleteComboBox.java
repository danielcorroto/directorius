package com.danielcorroto.directorius.component;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;

/**
 * ComboBox que se autocompleta a partir de los datos seleccionables. Además UP
 * y DOWN se mueve al elemento anterior y posterior respecitvamente
 * 
 * @author Daniel Corroto Quirós
 *
 * @param <T>
 *            Tipo de elemento del ComboBox
 */
public class AutoCompleteComboBox<T> extends ComboBox<T> {
	/**
	 * Anterior valor antiguo del editor
	 */
	private String lastOldValue;

	/**
	 * Creates a default ComboBox instance with an empty items list and default
	 * selection model.
	 */
	public AutoCompleteComboBox() {
		super();
		addEventListeners();
	}

	/**
	 * Creates a default ComboBox instance with the provided items list and a
	 * default selection model.
	 * 
	 * @param items
	 *            Provided items
	 */
	public AutoCompleteComboBox(ObservableList<T> items) {
		super(items);
		addEventListeners();
	}

	private void addEventListeners() {
		// El texto ha cambiado
		getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
			// Se resetea el valor antiguo por la cadena no seleccionada en el
			// editor
			int caret = getEditor().getSelection().getStart();
			if (getEditor().getSelection() != null) {
				oldValue = oldValue.substring(0, caret);
			}

			// Si son textos iguales o si se ha borrado parte del texto, no se
			// hace nada
			if (oldValue.equals(newValue) || (newValue.length() < oldValue.length() && oldValue.startsWith(newValue))) {
				return;
			}

			// Evita que con 1 caracter siempre salga un texto con un
			// case-sensitivity concreto
			if (lastOldValue != null && lastOldValue.length() == 1 && newValue.toUpperCase().equals(lastOldValue.toUpperCase())) {
				return;
			}

			// Busca la primera coincidencia
			T first = findFirst(newValue);
			if (first == null) {
				return;
			}
			// Si el elegido es el mismo que el nuevo, no se hace nada (para
			// evitar recursión)
			if (first.toString().toUpperCase().equals(newValue.toUpperCase())) {
				return;
			}

			// Selecciona la primera opción que coincida y coloca el cursor
			getSelectionModel().select(first);
			getEditor().setText(first.toString());

			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					// Setea el cursor y la selección
					getEditor().positionCaret(caret + 1);
					getEditor().selectEnd();
				}
			});

			lastOldValue = oldValue;
		});

		// Se mueve al elemento anterior o posterior
		getEditor().setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.DOWN) {
				getSelectionModel().selectNext();
			} else if (event.getCode() == KeyCode.UP) {
				getSelectionModel().selectPrevious();
			}
		});
	}

	/**
	 * Busca el primer elemento que empiece por el parámetro pasado.
	 * Case-insensitive
	 * 
	 * @param newValue
	 *            Cadena a buscar
	 * @return Elemento encontrado o null
	 */
	private T findFirst(String newValue) {
		String toFind = newValue.toUpperCase();
		for (T item : getItems()) {
			if (item.toString().toUpperCase().startsWith(toFind)) {
				return item;
			}
		}

		return null;
	}
}
