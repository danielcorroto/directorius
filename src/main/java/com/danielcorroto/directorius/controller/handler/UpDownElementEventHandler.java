package com.danielcorroto.directorius.controller.handler;

import java.util.List;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ListView;

/**
 * Maneja el evento de subir o bajar elementos de la lista
 * 
 * @author Daniel Corroto Quirós
 *
 * @param <E>
 *            Tipo de evento
 * @param <T>
 *            Tipo de elemento de la lista
 */
public class UpDownElementEventHandler<E extends Event, T> implements EventHandler<E> {
	/**
	 * ListView sobre la que se opera
	 */
	private ListView<T> listView;
	/**
	 * Si se sube la posición del elemento (true) o se baja (false)
	 */
	private boolean up;

	/**
	 * Constructor del manejador de evento
	 * 
	 * @param listView
	 *            ListView sobre la que se opera
	 * @param up
	 *            Si se sube la posición del elemento (true) o se baja (false)
	 */
	public UpDownElementEventHandler(ListView<T> listView, boolean up) {
		this.listView = listView;
		this.up = up;
	}

	@Override
	public void handle(E event) {
		int index = listView.getSelectionModel().getSelectedIndex();
		// Control de tope superior (si sube) o inferior (si baja)
		if (index == 0 && up) {
			return;
		}

		// Cantidad de posiciones a subir o bajar
		int delta;
		if (up) {
			delta = -1;
		} else {
			delta = 1;
		}

		// Intercambia posiciones
		List<T> categoryList = listView.getItems();
		T element = categoryList.get(index);
		T previous = categoryList.get(index + delta);
		categoryList.set(index + delta, element);
		categoryList.set(index, previous);

		// Selecciona el elemento movido
		listView.getSelectionModel().select(index + delta);
	}

}
