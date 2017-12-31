package com.danielcorroto.directorius.controller.handler;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

/**
 * Listener que habilita los nodos indicados
 * 
 * @author Daniel Corroto Quirós
 *
 * @param <T>
 *            Tipo de elemento modificado
 */
public class ElementEnablerChangeListener<T> implements ChangeListener<T> {
	/**
	 * Nodos que se habilitan
	 */
	private Node[] nodes;

	/**
	 * Constructor del manejador del evento
	 * 
	 * @param nodes
	 *            Nodos que se van a habilitar
	 */
	public ElementEnablerChangeListener(Node... nodes) {
		this.nodes = nodes;
	}

	@Override
	public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
		// Si se ha seleccionado algo se habilitan los nodos
		if (newValue != null) {
			for (Node node : nodes) {
				node.setDisable(false);
			}
		}
	}

}
