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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Gestión la ventana principal
 * 
 * @author Daniel Corroto Quirós
 *
 */
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
		addContactButtonFunction();
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
		window.getMenuItems().getContactAdd().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				loadContactWindow(null);
			}
		});
		
		window.getMenuItems().getContactEdit().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				if (window.getListView().getSelectionModel().getSelectedItem() == null) {
					return;
				}
				
				SimpleVCard simple = window.getListView().getSelectionModel().getSelectedItem();
				VCard vcard = manager.readContact(simple.getUid());
				loadContactWindow(vcard);
			}
		});

		window.getMenuItems().getHelpAbout().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					new AboutWindow().start(new Stage());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Carga la ventana de añadir/editar contacto y recarga la lista de
	 * contactos
	 * 
	 * @param vcard
	 *            Información del contacto a editar o null si es uno nuevo
	 */
	private void loadContactWindow(VCard vcard) {
		try {
			new ContactWindowController(manager, vcard).start(new Stage());

			// Carga la nueva lista
			String text = window.getSearchTextField().getText();
			SearchTypeEnum type = window.getSearchTypeComboBox().getValue();
			Set<SimpleVCard> list = manager.search(text.trim(), type);
			setListViewItems(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addContactButtonFunction() {
		window.getAddContactButton().setOnAction(window.getMenuItems().getContactAdd().getOnAction());
	}

	/**
	 * Setea la funcionalidad de la lista de contactos
	 */
	private void listViewFunction() {
		window.getListView().getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SimpleVCard>() {

			@Override
			public void changed(ObservableValue<? extends SimpleVCard> observable, SimpleVCard oldValue, SimpleVCard newValue) {
				if (newValue != null) {
					setWebViewInfo(newValue);
				}
			}
		});
		window.getListView().setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
					SimpleVCard simple = window.getListView().getSelectionModel().getSelectedItem();
					VCard vcard = manager.readContact(simple.getUid());
					loadContactWindow(vcard);
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
