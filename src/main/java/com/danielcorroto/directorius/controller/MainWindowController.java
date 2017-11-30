package com.danielcorroto.directorius.controller;

import java.util.Collection;
import java.util.Set;

import com.danielcorroto.directorius.model.ContactManager;
import com.danielcorroto.directorius.model.SimpleVCard;
import com.danielcorroto.directorius.model.type.SearchTypeEnum;
import com.danielcorroto.directorius.view.MainWindow;

import ezvcard.VCard;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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

		listViewFunction();
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
	 * Busca la información completa del contacto, genera la página de
	 * información y la muestra
	 * 
	 * @param simpleVCard
	 *            Información sencilal del contacto
	 */
	public void setWebViewInfo(SimpleVCard simpleVCard) {
		VCard vcard = manager.readContact(simpleVCard.getUid());
		String html = HtmlContactBuilder.build(vcard, manager.getPhotoDir());

		window.getWebView().getEngine().loadContent(html);
		window.getWebView().getEngine().setJavaScriptEnabled(true);
	}

	/**
	 * Setea la funcionalidad de la lista de contactos
	 */
	private void listViewFunction() {
		window.getListView().setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				SimpleVCard element = window.getListView().getSelectionModel().getSelectedItem();
				if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 1) {
					setWebViewInfo(element);
				}
			}
		});
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
				SearchTypeEnum type = window.getSearchTypeComboBox().getValue();
				list = manager.search(newValue.trim(), type);
				setListViewItems(list);
			}
		});
	}

	public static void main(String[] args) {
		launch(args);
	}
}
