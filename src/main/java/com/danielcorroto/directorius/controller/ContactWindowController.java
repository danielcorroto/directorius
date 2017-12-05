package com.danielcorroto.directorius.controller;

import java.io.IOException;
import java.util.Date;
import java.util.Set;

import com.danielcorroto.directorius.model.ContactManager;
import com.danielcorroto.directorius.view.ContactWindow;

import ezvcard.VCard;
import ezvcard.VCardVersion;
import ezvcard.property.Categories;
import ezvcard.property.Note;
import ezvcard.property.StructuredName;
import ezvcard.property.Uid;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

/**
 * Gestiona la creación y edición de contactos
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class ContactWindowController extends Application {
	/**
	 * Clase de la vista
	 */
	private ContactWindow window;
	/**
	 * Gestión de contactos
	 */
	private ContactManager manager;

	/**
	 * Información del contacto
	 */
	private VCard vcard;

	/**
	 * Constructor por defecto. Inicia i18n
	 * 
	 * @param manager
	 *            Gestión de contactos
	 * @param vcard
	 *            Información del contacto editado o null si es nuevo
	 */
	public ContactWindowController(ContactManager manager, VCard vcard) {
		super();
		this.manager = manager;
	}

	@Override
	public void start(Stage stage) throws Exception {
		window = new ContactWindow();
		window.build(stage);

		loadCategories();

		cancelFunction();
		saveFunction();

		stage.showAndWait();
	}

	private void loadCategories() {
		Set<String> categories = manager.getCategories();
		categories.add("");
		ObservableList<String> elementList = FXCollections.observableArrayList(categories);
		window.getCategoryCombo().setItems(elementList);
	}

	private void cancelFunction() {
		window.getCancel().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				window.close();
			}
		});
	}

	private void saveFunction() {
		window.getSave().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				if (vcard == null) {
					vcard = new VCard(VCardVersion.V4_0);
				}

				// Nombre
				vcard.setFormattedName(window.getFullNameTextField().getText().trim());
				StructuredName sn = new StructuredName();
				sn.setGiven(window.getNameTextField().getText().trim());
				sn.setFamily(window.getSurnameTextField().getText().trim());
				vcard.setStructuredName(sn);

				// Categorías
				if (window.getCategoryCombo().getSelectionModel() != null && window.getCategoryCombo().getSelectionModel().getSelectedItem() != null
						&& !window.getCategoryCombo().getSelectionModel().getSelectedItem().isEmpty()) {
					if (vcard.getCategories() == null) {
						vcard.setCategories(new Categories());
					}
					if (vcard.getCategories().getValues() != null && !vcard.getCategories().getValues().isEmpty()) {
						vcard.getCategories().getValues().clear();
					}
					vcard.getCategories().getValues().add(window.getCategoryCombo().getSelectionModel().getSelectedItem());
				}

				// Notas
				if (!window.getNotesTextArea().getText().isEmpty()) {
					vcard.getNotes().clear();
					vcard.getNotes().add(new Note(window.getNotesTextArea().getText().trim()));
				}

				// Fecha de edición
				vcard.setRevision(new Date());

				// Guardar
				if (vcard.getUid() == null) {
					vcard.setUid(Uid.random());
					try {
						manager.createContact(vcard);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					try {
						manager.updateContact(vcard);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				window.close();
			}
		});
	}

}
