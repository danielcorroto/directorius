package com.danielcorroto.directorius.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import com.danielcorroto.directorius.controller.data.AddressInfo;
import com.danielcorroto.directorius.controller.data.EmailInfo;
import com.danielcorroto.directorius.controller.data.PhoneInfo;
import com.danielcorroto.directorius.controller.type.AddressTypeEnum;
import com.danielcorroto.directorius.controller.type.EmailTypeEnum;
import com.danielcorroto.directorius.controller.type.PhoneTypeEnum;
import com.danielcorroto.directorius.model.ContactManager;
import com.danielcorroto.directorius.model.CustomParameter;
import com.danielcorroto.directorius.model.log.Logger;
import com.danielcorroto.directorius.view.ContactWindow;
import com.danielcorroto.directorius.view.Text;
import com.danielcorroto.directorius.view.info.AddressDialogWindow;
import com.danielcorroto.directorius.view.info.CategoryDialogWindow;
import com.danielcorroto.directorius.view.info.EmailDialogWindow;
import com.danielcorroto.directorius.view.info.PhoneDialogWindow;

import ezvcard.VCard;
import ezvcard.VCardVersion;
import ezvcard.parameter.AddressType;
import ezvcard.parameter.EmailType;
import ezvcard.parameter.ImageType;
import ezvcard.parameter.TelephoneType;
import ezvcard.property.Address;
import ezvcard.property.Birthday;
import ezvcard.property.Categories;
import ezvcard.property.Email;
import ezvcard.property.Note;
import ezvcard.property.Photo;
import ezvcard.property.StructuredName;
import ezvcard.property.Telephone;
import ezvcard.property.Uid;
import ezvcard.util.PartialDate;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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
	 * Logger
	 */
	private static final Logger LOGGER = Logger.getLogger(ContactWindowController.class);
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
	 * Constructor con parámetros. Inicia i18n
	 * 
	 * @param manager
	 *            Gestión de contactos
	 * @param vcard
	 *            Información del contacto editado o null si es nuevo
	 */
	public ContactWindowController(ContactManager manager, VCard vcard) {
		super();
		this.manager = manager;
		this.vcard = vcard;
		rb = ResourceBundle.getBundle(Text.RESOURCE_BUNDLE, Locale.getDefault());
	}

	@Override
	public void start(Stage stage) throws Exception {
		window = new ContactWindow();
		window.build(stage);

		if (vcard != null) {
			loadVCard();
		}

		mainFunctions();
		fullNameAutoloadFunction();
		photoFunctions();
		addElementFunctions();
		editElementFunctions();
		removeElementFunctions();

		stage.showAndWait();
	}

	/**
	 * Carga la información del contacto en el formulario
	 */
	private void loadVCard() {
		// Nombre
		if (vcard.getStructuredName() != null) {
			if (vcard.getStructuredName().getGiven() != null) {
				window.getNameTextField().setText(vcard.getStructuredName().getGiven());
			}
			if (vcard.getStructuredName().getFamily() != null) {
				window.getSurnameTextField().setText(vcard.getStructuredName().getFamily());
			}
		}
		String fullName = "";
		if (vcard.getFormattedName() != null) {
			fullName = vcard.getFormattedName().getValue();
			window.getFullNameTextField().setText(fullName);
		}

		// Fecha
		if (vcard.getBirthday() != null) {
			Integer year = null;
			Integer month = null;
			Integer day = null;

			// Fecha completa
			if (vcard.getBirthday().getDate() != null) {
				Calendar c = Calendar.getInstance();
				c.setTime(vcard.getBirthday().getDate());
				year = c.get(Calendar.YEAR);
				month = c.get(Calendar.MONTH) + 1;
				day = c.get(Calendar.DATE);
			}

			// Fecha parcial
			if (vcard.getBirthday().getPartialDate() != null) {
				year = vcard.getBirthday().getPartialDate().getYear();
				month = vcard.getBirthday().getPartialDate().getMonth();
				day = vcard.getBirthday().getPartialDate().getDate();
			}

			// Seteo de elementos
			if (year != null) {
				window.getComboBoxYear().getSelectionModel().select(String.valueOf(year));
			}
			if (month != null) {
				window.getComboBoxMonth().getSelectionModel().select(String.valueOf(month));
			}
			if (day != null) {
				window.getComboBoxDay().getSelectionModel().select(String.valueOf(day));
			}
		}

		// Categorías
		if (vcard.getCategories() != null && !vcard.getCategories().getValues().isEmpty()) {
			for (String category : vcard.getCategories().getValues()) {
				addCategoryElement(category);
			}
		}

		// Notas
		if (vcard.getNotes() != null && !vcard.getNotes().isEmpty()) {
			window.getNotesTextArea().setText(vcard.getNotes().get(0).getValue());
		}

		// Foto
		if (vcard.getPhotos() != null && !vcard.getPhotos().isEmpty()) {
			String url = vcard.getPhotos().get(0).getUrl();
			imageFile = new File(manager.getPhotoDir() + url);
			try {
				loadImage();
			} catch (IOException e) {
				imageFile = null;
				LOGGER.severe("No se ha podido cargar la foto de " + vcard.getUid(), e);
			}
		}

		// Teléfonos
		if (vcard.getTelephoneNumbers() != null && !vcard.getTelephoneNumbers().isEmpty()) {
			for (Telephone phone : vcard.getTelephoneNumbers()) {
				TelephoneType type = phone.getTypes().get(0);
				PhoneInfo info = new PhoneInfo(phone.getText(), PhoneTypeEnum.findByPhoneType(type), phone.getParameter(CustomParameter.EMAIL_TAG));
				addPhoneElement(info);
			}
		}

		// Emails
		if (vcard.getEmails() != null && !vcard.getEmails().isEmpty()) {
			for (Email email : vcard.getEmails()) {
				EmailType type = email.getTypes().get(0);
				EmailInfo info = new EmailInfo(email.getValue(), EmailTypeEnum.findByEmailType(type), email.getParameter(CustomParameter.EMAIL_TAG));
				addEmailElement(info);
			}
		}

		// Direcciones
		if (vcard.getAddresses() != null && !vcard.getAddresses().isEmpty()) {
			for (Address address : vcard.getAddresses()) {
				AddressType type = address.getTypes().get(0);
				AddressInfo info = new AddressInfo(address.getStreetAddress(), address.getLocality(), address.getRegion(), address.getPostalCode(), address.getCountry(),
						AddressTypeEnum.findByAddressType(type), address.getParameter(CustomParameter.ADDRESS_TAG));
				addAddressElement(info);
			}
		}

		// Valores de la ventana
		window.setTitle(rb.getString(Text.I18N_MENU_CONTACT_EDIT) + ": " + fullName);
		window.getSave().setDisable(false);
	}

	/**
	 * Setea el valor de nombre completo a partir del valor de nombre y
	 * apellidos
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
				fileChooser.setTitle(rb.getString(Text.I18N_CONTACT_PHOTO_SEARCH));
				imageFile = fileChooser.showOpenDialog(window.getStage());
				try {
					loadImage();
				} catch (IOException e) {
					imageFile = null;
					LOGGER.severe("Fichero no encontrado " + imageFile.getAbsolutePath(), e);
				}
			}
		});
	}

	/**
	 * Carga la imagen en el elemento ImageView. El fichero es el de la variable
	 * imageFile
	 * 
	 * @throws IOException
	 */
	private void loadImage() throws IOException {
		InputStream is = new FileInputStream(imageFile);
		Image image = new Image(is);
		window.getImageView().setImage(image);

		window.getImageView().setImage(image);
		window.getImageView().maxHeight(200 - 10);
		int yoursize = 200;
		if (window.getImageView().maxHeight(200 - 10) > yoursize) {
			window.getImageView().setFitHeight(200 - 10);
		}
		window.getImageView().setPreserveRatio(true);
		window.getImageView().setSmooth(true);
		window.getImageView().setCache(true);

		is.close();
	}

	/**
	 * Setea la funcionalidad de los botones de añadir teléfono/email/dirección
	 */
	private void addElementFunctions() {
		// Añadir categoría
		window.getAddCategory().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				CategoryDialogWindow dialog = new CategoryDialogWindow(manager.getCategories());
				Optional<String> result = dialog.showAndWait();

				result.ifPresent(new Consumer<String>() {

					@Override
					public void accept(String t) {
						addCategoryElement(t);
					}
				});
			}
		});

		// Añadir teléfono
		window.getAddPhone().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				PhoneDialogWindow dialog = new PhoneDialogWindow();
				Optional<PhoneInfo> result = dialog.showAndWait();

				result.ifPresent(new Consumer<PhoneInfo>() {

					@Override
					public void accept(PhoneInfo t) {
						addPhoneElement(t);
					}
				});
			}
		});

		// Añadir email
		window.getAddEmail().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				EmailDialogWindow dialog = new EmailDialogWindow();
				Optional<EmailInfo> result = dialog.showAndWait();

				result.ifPresent(new Consumer<EmailInfo>() {

					@Override
					public void accept(EmailInfo t) {
						addEmailElement(t);
					}
				});
			}
		});

		// Añadir dirección
		window.getAddAddress().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				AddressDialogWindow dialog = new AddressDialogWindow();
				Optional<AddressInfo> result = dialog.showAndWait();

				result.ifPresent(new Consumer<AddressInfo>() {

					@Override
					public void accept(AddressInfo t) {
						addAddressElement(t);
					}
				});
			}
		});
	}

	/**
	 * Setea la funcionalidad de los botones de editar teléfono/email/dirección,
	 * los habilita cuando hay un elemento seleccionado y setea la la acción de
	 * doble click sobre cada uno de esos elementos
	 */
	private void editElementFunctions() {
		// Editar categoría
		window.getListViewCategory().getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue != null) {
					window.getEditCategory().setDisable(false);
				}
			}
		});
		window.getEditCategory().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				showEditCategoryDialog();
			}
		});
		window.getListViewCategory().setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
					showEditCategoryDialog();
				}

			}
		});

		// Editar teléfono
		window.getListViewPhone().getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PhoneInfo>() {

			@Override
			public void changed(ObservableValue<? extends PhoneInfo> observable, PhoneInfo oldValue, PhoneInfo newValue) {
				if (newValue != null) {
					window.getEditPhone().setDisable(false);
				}
			}
		});
		window.getEditPhone().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				showEditPhoneDialog();
			}
		});
		window.getListViewPhone().setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
					showEditPhoneDialog();
				}

			}
		});

		// Editar email
		window.getListViewEmail().getSelectionModel().selectedItemProperty().addListener(new ChangeListener<EmailInfo>() {

			@Override
			public void changed(ObservableValue<? extends EmailInfo> observable, EmailInfo oldValue, EmailInfo newValue) {
				if (newValue != null) {
					window.getEditEmail().setDisable(false);
				}
			}
		});
		window.getEditEmail().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				showEditEmailDialog();
			}
		});
		window.getListViewEmail().setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
					showEditEmailDialog();
				}

			}
		});

		// Editar dirección
		window.getListViewAddress().getSelectionModel().selectedItemProperty().addListener(new ChangeListener<AddressInfo>() {

			@Override
			public void changed(ObservableValue<? extends AddressInfo> observable, AddressInfo oldValue, AddressInfo newValue) {
				if (newValue != null) {
					window.getEditAddress().setDisable(false);
				}
			}
		});
		window.getEditAddress().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				showEditAddressDialog();
			}
		});
		window.getListViewAddress().setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
					showEditAddressDialog();
				}

			}
		});
	}

	/**
	 * Muestra el diálogo de edición de la categoría a partir del elemento
	 * seleccionado
	 */
	private void showEditCategoryDialog() {
		String info = window.getListViewCategory().getSelectionModel().getSelectedItem();
		if (info == null) {
			return;
		}

		CategoryDialogWindow dialog = new CategoryDialogWindow(info, manager.getCategories());
		Optional<String> result = dialog.showAndWait();

		result.ifPresent(new Consumer<String>() {

			@Override
			public void accept(String t) {
				int index = window.getListViewCategory().getSelectionModel().getSelectedIndex();
				window.getListViewCategory().getItems().set(index, t);
			}
		});
	}

	/**
	 * Muestra el diálogo de edición del teléfono a partir del elemento
	 * seleccionado
	 */
	private void showEditPhoneDialog() {
		PhoneInfo info = window.getListViewPhone().getSelectionModel().getSelectedItem();
		if (info == null) {
			return;
		}

		PhoneDialogWindow dialog = new PhoneDialogWindow(info);
		Optional<PhoneInfo> result = dialog.showAndWait();

		result.ifPresent(new Consumer<PhoneInfo>() {

			@Override
			public void accept(PhoneInfo t) {
				int index = window.getListViewPhone().getSelectionModel().getSelectedIndex();
				window.getListViewPhone().getItems().set(index, t);
			}
		});
	}

	/**
	 * Muestra el diálogo de edición del email a partir del elemento
	 * seleccionado
	 */
	private void showEditEmailDialog() {
		EmailInfo info = window.getListViewEmail().getSelectionModel().getSelectedItem();
		if (info == null) {
			return;
		}

		EmailDialogWindow dialog = new EmailDialogWindow(info);
		Optional<EmailInfo> result = dialog.showAndWait();

		result.ifPresent(new Consumer<EmailInfo>() {

			@Override
			public void accept(EmailInfo t) {
				int index = window.getListViewEmail().getSelectionModel().getSelectedIndex();
				window.getListViewEmail().getItems().set(index, t);
			}
		});
	}

	/**
	 * Muestra el diálogo de edición de la dirección a partir del elemento
	 * seleccionado
	 */
	private void showEditAddressDialog() {
		AddressInfo info = window.getListViewAddress().getSelectionModel().getSelectedItem();
		if (info == null) {
			return;
		}

		AddressDialogWindow dialog = new AddressDialogWindow(info);
		Optional<AddressInfo> result = dialog.showAndWait();

		result.ifPresent(new Consumer<AddressInfo>() {

			@Override
			public void accept(AddressInfo t) {
				int index = window.getListViewAddress().getSelectionModel().getSelectedIndex();
				window.getListViewAddress().getItems().set(index, t);
			}
		});
	}

	/**
	 * Setea la funcionalidad de los botones de eliminar
	 * teléfono/email/dirección y los habilita cuando hay un elemento
	 * seleccionado
	 */
	private void removeElementFunctions() {
		// Eliminar categoría
		window.getListViewCategory().getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue != null) {
					window.getRemoveCategory().setDisable(false);
				}
			}
		});
		window.getRemoveCategory().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				String info = window.getListViewCategory().getSelectionModel().getSelectedItem();
				if (info == null) {
					return;
				}

				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle(rb.getString(Text.I18N_CONTACT_CATEGORY_REMOVE));
				alert.setHeaderText(info);
				alert.setContentText(null);

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK) {
					int index = window.getListViewCategory().getSelectionModel().getSelectedIndex();
					window.getListViewCategory().getItems().remove(index);
				}
			}
		});

		// Eliminar teléfono
		window.getListViewPhone().getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PhoneInfo>() {

			@Override
			public void changed(ObservableValue<? extends PhoneInfo> observable, PhoneInfo oldValue, PhoneInfo newValue) {
				if (newValue != null) {
					window.getRemovePhone().setDisable(false);
				}
			}
		});
		window.getRemovePhone().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				PhoneInfo info = window.getListViewPhone().getSelectionModel().getSelectedItem();
				if (info == null) {
					return;
				}

				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle(rb.getString(Text.I18N_CONTACT_PHONE_REMOVE));
				alert.setHeaderText(DisplayUtil.getPhoneInfo(info, rb));
				alert.setContentText(null);

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK) {
					int index = window.getListViewPhone().getSelectionModel().getSelectedIndex();
					window.getListViewPhone().getItems().remove(index);
				}
			}
		});

		// Eliminar email
		window.getListViewEmail().getSelectionModel().selectedItemProperty().addListener(new ChangeListener<EmailInfo>() {

			@Override
			public void changed(ObservableValue<? extends EmailInfo> observable, EmailInfo oldValue, EmailInfo newValue) {
				if (newValue != null) {
					window.getRemoveEmail().setDisable(false);
				}
			}
		});
		window.getRemoveEmail().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				EmailInfo info = window.getListViewEmail().getSelectionModel().getSelectedItem();
				if (info == null) {
					return;
				}

				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle(rb.getString(Text.I18N_CONTACT_EMAIL_REMOVE));
				alert.setHeaderText(DisplayUtil.getEmailInfo(info, rb));
				alert.setContentText(null);

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK) {
					int index = window.getListViewEmail().getSelectionModel().getSelectedIndex();
					window.getListViewEmail().getItems().remove(index);
				}
			}
		});

		// Eliminar dirección
		window.getListViewAddress().getSelectionModel().selectedItemProperty().addListener(new ChangeListener<AddressInfo>() {

			@Override
			public void changed(ObservableValue<? extends AddressInfo> observable, AddressInfo oldValue, AddressInfo newValue) {
				if (newValue != null) {
					window.getRemoveAddress().setDisable(false);
				}
			}
		});
		window.getRemoveAddress().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				AddressInfo info = window.getListViewAddress().getSelectionModel().getSelectedItem();
				if (info == null) {
					return;
				}

				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle(rb.getString(Text.I18N_CONTACT_ADDRESS_REMOVE));
				alert.setHeaderText(DisplayUtil.getAddressInfo(info, rb));
				alert.setContentText(null);

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK) {
					int index = window.getListViewAddress().getSelectionModel().getSelectedIndex();
					window.getListViewAddress().getItems().remove(index);
				}
			}
		});
	}

	/**
	 * Setea la funcionalidad de los botones cancelar, guardar y pulsar ESC y
	 * autoactivado de botón guardar
	 */
	private void mainFunctions() {
		// Autoactivado del botón guardar
		window.getFullNameTextField().textProperty().addListener((observable, oldValue, newValue) -> {
			window.getSave().setDisable(newValue.trim().isEmpty());
		});

		// Pulsar ESC
		window.getStage().addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ESCAPE) {
					window.close();
				}
			}
		});

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

				// Fecha
				String year = window.getComboBoxYear().getSelectionModel().getSelectedItem();
				String month = window.getComboBoxMonth().getSelectionModel().getSelectedItem();
				String day = window.getComboBoxDay().getSelectionModel().getSelectedItem();

				if (year != null && !year.isEmpty() && month != null && !month.isEmpty() && day != null && !day.isEmpty()) {
					// Fecha completa
					Calendar c = Calendar.getInstance();
					c.set(Calendar.YEAR, Integer.parseInt(year));
					c.set(Calendar.MONTH, Integer.parseInt(month) - 1);
					c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
					Birthday bday = new Birthday(c.getTime());
					vcard.setBirthday(bday);
				} else if ((year != null && !year.isEmpty()) || (month != null && !month.isEmpty()) || (day != null && !day.isEmpty())) {
					// Fecha parcial
					PartialDate.Builder b = PartialDate.builder();
					if (year != null && !year.isEmpty()) {
						b.year(Integer.parseInt(year));
					}
					if (month != null && !month.isEmpty()) {
						b.month(Integer.parseInt(month));
					}
					if (day != null && !day.isEmpty()) {
						b.date(Integer.parseInt(day));
					}

					Birthday bday = new Birthday(b.build());
					vcard.setBirthday(bday);
				}

				// Notas
				if (!window.getNotesTextArea().getText().isEmpty()) {
					vcard.getNotes().clear();
					vcard.getNotes().add(new Note(window.getNotesTextArea().getText().trim()));
				}

				// Foto
				if (imageFile != null) {
					try {
						String fileName = vcard.getUid().getValue().replaceAll("urn:uuid:", "");
						fileName = manager.savePhotoFile(imageFile, fileName);
						if (fileName != null) {
							// Si se incluye una nueva foto
							if (vcard.getPhotos() != null && !vcard.getPhotos().isEmpty()) {
								String previousUrl = vcard.getPhotos().get(0).getUrl();
								if (!previousUrl.equals(fileName)) {
									// Se elimina la foto antigua si se ha
									// cambiado la extensión. Si no se ha
									// cambiado la extensión ya ha sido
									// sobreescrita
									manager.removePhotoFile(previousUrl);
								}
								vcard.getPhotos().clear();
							}
							Photo photo = new Photo(fileName, getImageTypeFromFileName(fileName));
							vcard.addPhoto(photo);
						}
					} catch (IOException e) {
						LOGGER.severe("No se ha podido guardar la foto de " + vcard.getUid(), e);
					}
				} else {
					// No se ha seleccionado foto
					if (vcard.getPhotos() != null) {
						String previousUrl = vcard.getPhotos().get(0).getUrl();
						try {
							manager.removePhotoFile(previousUrl);
						} catch (IOException e) {
							LOGGER.severe("No se ha podido eliminar la foto de " + vcard.getUid(), e);
						}
						vcard.getPhotos().clear();
					}
				}

				// Categorías
				if (vcard.getCategoriesList() != null && !vcard.getCategoriesList().isEmpty()) {
					vcard.getCategoriesList().clear();
				}
				if (!window.getListViewCategory().getItems().isEmpty()) {
					Categories categories = new Categories();
					vcard.setCategories(categories);
					for (String category : window.getListViewCategory().getItems()) {
						categories.getValues().add(category);
					}
				}

				// Teléfono
				if (vcard.getTelephoneNumbers() != null && !vcard.getTelephoneNumbers().isEmpty()) {
					vcard.getTelephoneNumbers().clear();
				}
				for (PhoneInfo phone : window.getListViewPhone().getItems()) {
					Telephone phoneCard = new Telephone(phone.getNumber().trim());
					phoneCard.getTypes().add(phone.getType().getTelephoneType());
					if (phone.getTag() != null && !phone.getTag().trim().isEmpty()) {
						phoneCard.addParameter(CustomParameter.TELEPHONE_TAG, phone.getTag().trim());
					}
					vcard.addTelephoneNumber(phoneCard);
				}

				// Email
				if (vcard.getEmails() != null && !vcard.getEmails().isEmpty()) {
					vcard.getEmails().clear();
				}
				for (EmailInfo email : window.getListViewEmail().getItems()) {
					Email emailCard = new Email(email.getEmail().trim());
					emailCard.getTypes().add(email.getType().getEmailType());
					if (email.getTag() != null && !email.getTag().trim().isEmpty()) {
						emailCard.addParameter(CustomParameter.EMAIL_TAG, email.getTag().trim());
					}
					vcard.addEmail(emailCard);
				}

				// Dirección
				if (vcard.getAddresses() != null && !vcard.getAddresses().isEmpty()) {
					vcard.getAddresses().clear();
				}
				for (AddressInfo address : window.getListViewAddress().getItems()) {
					Address addressCard = new Address();
					addressCard.setStreetAddress(address.getStreet());
					addressCard.setLocality(address.getLocality());
					addressCard.setRegion(address.getRegion());
					addressCard.setPostalCode(address.getPostalCode());
					addressCard.setCountry(address.getCountry());
					addressCard.getTypes().add(address.getType().getAddressType());
					if (address.getTag() != null && !address.getTag().trim().isEmpty()) {
						addressCard.addParameter(CustomParameter.ADDRESS_TAG, address.getTag().trim());
					}
					vcard.addAddress(addressCard);
				}

				// Fecha de edición
				vcard.setRevision(new Date());

				// Guardar
				if (newContact) {
					try {
						manager.createContact(vcard);
					} catch (IOException e) {
						LOGGER.severe("No se ha podido guardar el contacto " + vcard.getFormattedName().getValue(), e);
					}
				} else {
					try {
						manager.updateContact(vcard);
					} catch (IOException e) {
						LOGGER.severe("No se ha podido actualizar el contacto " + vcard.getUid(), e);
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

	/**
	 * Añade una categoría a la lista
	 * 
	 * @param info
	 *            Información de la categoría
	 */
	private void addCategoryElement(String info) {
		window.getListViewCategory().getItems().add(info);
	}

	/**
	 * Añade un teléfono a la lista
	 * 
	 * @param info
	 *            Información del teléfono
	 */
	private void addPhoneElement(PhoneInfo info) {
		window.getListViewPhone().getItems().add(info);
	}

	/**
	 * Añade un email a la lista
	 * 
	 * @param info
	 *            Información del email
	 */
	private void addEmailElement(EmailInfo info) {
		window.getListViewEmail().getItems().add(info);
	}

	/**
	 * Añade una dirección a la lista
	 * 
	 * @param info
	 *            Información de la dirección
	 */
	private void addAddressElement(AddressInfo info) {
		window.getListViewAddress().getItems().add(info);
	}

}
