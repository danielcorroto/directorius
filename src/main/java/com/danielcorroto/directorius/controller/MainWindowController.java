package com.danielcorroto.directorius.controller;

import java.util.Collection;
import java.util.Set;

import com.danielcorroto.directorius.model.ContactManager;
import com.danielcorroto.directorius.model.SimpleVCard;
import com.danielcorroto.directorius.model.type.SearchTypeEnum;
import com.danielcorroto.directorius.view.MainWindow;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

/**
 * Gestión la ventana principal
 * 
 * @author Daniel Corroto Quirós
 *
 */
@SuppressWarnings("restriction")
public class MainWindowController extends Application {
	/**
	 * Clase de la vista
	 */
	private MainWindow window;
	/**
	 * Gestión de contactos
	 */
	private ContactManager manager;

	@Override
	public void start(Stage primaryStage) throws Exception {
		window = new MainWindow();
		window.build(primaryStage);
		searchTextFieldFunction();

		manager = ContactManager.autoLoadFile();
		if (manager != null) {
			setListViewItems(manager.getAllSimpleVCard());
		}
	}

	/**
	 * Setea la colección de contactos en la ListView
	 * 
	 * @param list
	 *            Colección de contactos a mostrar
	 */
	public void setListViewItems(Collection<SimpleVCard> list) {
		ObservableList<SimpleVCard> elementList = FXCollections.observableArrayList(list);
		window.getListView().setItems(elementList);
	}

	/**
	 * Setea la funcionalidad de la caja de búsqueda: busca contactos y los
	 * muestra en el ListView
	 */
	private void searchTextFieldFunction() {
		window.getSearchTextField().textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				Set<SimpleVCard> list;
				if (newValue.trim().isEmpty()) {
					list = manager.getAllSimpleVCard();
				} else {
					SearchTypeEnum type = window.getSearchTypeComboBox().getValue();
					list = manager.search(newValue.trim(), type);
				}
				setListViewItems(list);
			}
		});
	}

	public static void main(String[] args) {
		launch(args);
	}
}
