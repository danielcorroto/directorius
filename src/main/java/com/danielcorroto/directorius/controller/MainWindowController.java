package com.danielcorroto.directorius.controller;

import java.util.Collection;

import com.danielcorroto.directorius.model.ContactManager;
import com.danielcorroto.directorius.model.SimpleVCard;
import com.danielcorroto.directorius.view.MainWindow;

import javafx.application.Application;
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

	public static void main(String[] args) {
		launch(args);
	}
}
