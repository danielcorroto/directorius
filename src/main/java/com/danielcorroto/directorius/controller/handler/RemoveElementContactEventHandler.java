package com.danielcorroto.directorius.controller.handler;

import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;

/**
 * Maneja el evento de eliminar un elemento de la lista de selección
 * (categoría/teléfono/email/dirección)
 * 
 * @author Daniel Corroto Quirós
 *
 * @param <E>
 *            Tipo de evento
 * @param <T>
 *            Tipo de información
 */
public abstract class RemoveElementContactEventHandler<E extends ActionEvent, T> implements EventHandler<E> {
	private ListView<T> listView;
	private String title;

	/**
	 * 
	 * @param listView
	 *            ListView sobre la que se opera
	 * @param title
	 *            Título de la ventana
	 */
	public RemoveElementContactEventHandler(ListView<T> listView, String title) {
		this.listView = listView;
		this.title = title;
	}

	@Override
	public void handle(E event) {
		// Comprueba elemento seleccionado
		T info = listView.getSelectionModel().getSelectedItem();
		if (info == null) {
			return;
		}

		// Muestra alerta
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(infoToText(info));
		alert.setContentText(null);

		// Elimina el elemento de la lista
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			int index = listView.getSelectionModel().getSelectedIndex();
			listView.getItems().remove(index);
		}
	}

	public abstract String infoToText(T info);

}
