package com.danielcorroto.directorius.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import com.danielcorroto.directorius.model.ContactManager;
import com.danielcorroto.directorius.view.ContactWindow;
import com.danielcorroto.directorius.view.Text;

import ezvcard.VCard;
import ezvcard.VCardVersion;
import ezvcard.parameter.ImageType;
import ezvcard.property.Categories;
import ezvcard.property.Note;
import ezvcard.property.Photo;
import ezvcard.property.StructuredName;
import ezvcard.property.Uid;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
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
	 * Fichero con la imagen
	 */
	private File imageFile = null;

	/**
	 * Para i18n
	 */
	private ResourceBundle rb;

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
		rb = ResourceBundle.getBundle(Text.RESOURCE_BUNDLE, Locale.getDefault());
	}

	@Override
	public void start(Stage stage) throws Exception {
		window = new ContactWindow();
		window.build(stage);

		loadCategories();

		mainFunctions();
		fullNameAutoloadFunction();
		photoFunctions();

		stage.showAndWait();
	}
	
	/**
	 * Setea el valor de nombre completo a partir del valor de nombre y apellidos
	 */
	private void fullNameAutoloadFunction() {
		window.getFullNameTextField().focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (!window.getFullNameTextField().getText().trim().isEmpty()) {
					return;
				}
				
				String name = window.getNameTextField().getText().trim();
				String surname = window.getSurnameTextField().getText().trim();
				window.getFullNameTextField().setText(name + " " + surname);
			}
		});
	}

	/**
	 * Carga las categorías en el combo de categorías
	 */
	private void loadCategories() {
		Set<String> categories = manager.getCategories();
		categories.add("");
		ObservableList<String> elementList = FXCollections.observableArrayList(categories);
		window.getCategoryCombo().setItems(elementList);
	}

	/**
	 * Setea la funcionalidad de los botones limpiar y buscar foto
	 */
	private void photoFunctions() {
		// Limpiar
		window.getPhotoClean().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				window.getImageView().setImage(null);
				imageFile = null;
			}
		});

		// Buscar
		window.getPhotoSearch().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle(rb.getString(Text.I18N_EDITCONTACT_PHOTO_SEARCH));
				imageFile = fileChooser.showOpenDialog(window.getStage());
				InputStream is;
				try {
					is = new FileInputStream(imageFile);
					Image image = new Image(is);
					window.getImageView().setImage(image);

					window.getImageView().setImage(image);
					window.getImageView().maxHeight(200 - 10);
					int yoursize = 200;
					if (window.getImageView().maxHeight(200 - 10) > yoursize) {
						window.getImageView().setFitHeight(200 - 10);
						System.out.println("Fix Size : 200 ");
					}
					window.getImageView().setPreserveRatio(true);
					window.getImageView().setSmooth(true);
					window.getImageView().setCache(true);

				} catch (FileNotFoundException e) {
					imageFile = null;
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Setea la funcionalidad de los botones cancelar y guardar
	 */
	private void mainFunctions() {
		// Cancelar
		window.getCancel().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				window.close();
			}
		});

		// Guardar
		window.getSave().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				boolean newContact = false;
				if (vcard == null) {
					vcard = new VCard(VCardVersion.V4_0);
					vcard.setUid(Uid.random());
					newContact = true;
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

				// Foto
				if (imageFile != null) {
					try {
						if (vcard.getPhotos() != null && !vcard.getPhotos().isEmpty()) {
							vcard.getPhotos().clear();
						}
						String fileName = vcard.getUid().getValue().replaceAll("urn:uuid:", "");
						fileName = manager.savePhotoFile(imageFile, fileName);
						Photo photo = new Photo(fileName, getImageTypeFromFileName(fileName));
						vcard.addPhoto(photo);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				// Fecha de edición
				vcard.setRevision(new Date());

				// Guardar
				if (newContact) {
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

	/**
	 * Devuelve el tipo de imagen (gif, jpg, png) a partir del nombre del
	 * fichero
	 * 
	 * @param fileName
	 *            Nombre del fichero con extensión
	 * @return Tipo de imagen o null si no es válida
	 */
	private ImageType getImageTypeFromFileName(String fileName) {
		String lower = fileName.toLowerCase();
		if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
			return ImageType.JPEG;
		}

		if (lower.endsWith(".png")) {
			return ImageType.PNG;
		}

		if (lower.endsWith(".gif")) {
			return ImageType.GIF;
		}

		return null;
	}

}
