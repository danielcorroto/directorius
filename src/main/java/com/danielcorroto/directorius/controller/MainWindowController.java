package com.danielcorroto.directorius.controller;

import java.util.Collection;
import java.util.Set;

import com.danielcorroto.directorius.model.ContactManager;
import com.danielcorroto.directorius.model.SimpleVCard;
import com.danielcorroto.directorius.model.type.SearchTypeEnum;
import com.danielcorroto.directorius.view.AboutWindow;
import com.danielcorroto.directorius.view.MainWindow;

import ezvcard.VCard;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

		menuItemFunction();
		listViewFunction();
		searchTextFieldFunction();
		searchTypeComboBoxFunction();

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
	private void setListViewItems(Collection<SimpleVCard> list) {
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
	private void setWebViewInfo(SimpleVCard simpleVCard) {
		VCard vcard = manager.readContact(simpleVCard.getUid());
		String html = HtmlContactBuilder.build(vcard, manager.getPhotoDir());

		window.getWebView().getEngine().loadContent(html);
		window.getWebView().getEngine().setJavaScriptEnabled(true);
	}

	/**
	 * Setea la funcionalidad de los items del menú
	 */
	private void menuItemFunction() {
		window.getMenuItems().getHelpAbout().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					new AboutWindow().start(new Stage());
				} catch (Exception e) {
					e.printStackTrace();
				}
				;
			}
		});
	}

	/**
	 * Setea la funcionalidad de la lista de contactos
	 */
	private void listViewFunction() {
		window.getListView().getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SimpleVCard>() {

			@Override
			public void changed(ObservableValue<? extends SimpleVCard> observable, SimpleVCard oldValue, SimpleVCard newValue) {
				setWebViewInfo(newValue);
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

	/**
	 * Setea la funcionalidad del combo de tipo de búsqueda: busca contactos y
	 * los muestre en el ListView
	 */
	private void searchTypeComboBoxFunction() {
		window.getSearchTypeComboBox().getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SearchTypeEnum>() {

			@Override
			public void changed(ObservableValue<? extends SearchTypeEnum> observable, SearchTypeEnum oldValue, SearchTypeEnum newValue) {
				String text = window.getSearchTextField().getText();
				// Si no hay texto que buscar el resultado siempre es la lista
				// completa
				if (text.trim().isEmpty()) {
					return;
				}

				Set<SimpleVCard> list = manager.search(text.trim(), newValue);
				setListViewItems(list);
			}
		});
	}

	public static void main(String[] args) {
		launch(args);
	}
}
