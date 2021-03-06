package com.danielcorroto.directorius.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import com.danielcorroto.directorius.controller.data.AddressInfo;
import com.danielcorroto.directorius.controller.data.EmailInfo;
import com.danielcorroto.directorius.controller.data.PhoneInfo;
import com.danielcorroto.directorius.controller.handler.ElementEnablerChangeListener;
import com.danielcorroto.directorius.controller.handler.RemoveElementContactEventHandler;
import com.danielcorroto.directorius.controller.handler.UpDownElementEventHandler;
import com.danielcorroto.directorius.controller.type.AddressTypeEnum;
import com.danielcorroto.directorius.controller.type.EmailTypeEnum;
import com.danielcorroto.directorius.controller.type.PhoneTypeEnum;
import com.danielcorroto.directorius.model.ContactManager;
import com.danielcorroto.directorius.model.CustomParameter;
import com.danielcorroto.directorius.model.Utils;
import com.danielcorroto.directorius.model.log.Logger;
import com.danielcorroto.directorius.view.AlertExceptionDialog;
import com.danielcorroto.directorius.view.ContactDialog;
import com.danielcorroto.directorius.view.Text;
import com.danielcorroto.directorius.view.info.AddressDialog;
import com.danielcorroto.directorius.view.info.CategoryDialog;
import com.danielcorroto.directorius.view.info.EmailDialog;
import com.danielcorroto.directorius.view.info.PhoneDialog;

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
import ezvcard.property.Nickname;
import ezvcard.property.Note;
import ezvcard.property.Photo;
import ezvcard.property.StructuredName;
import ezvcard.property.Telephone;
import ezvcard.property.Uid;
import ezvcard.util.PartialDate;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.util.Callback;

/**
 * Gestiona la creación y edición de contactos
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class ContactDialogController {
	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger.getLogger(ContactDialogController.class);
	/**
	 * Clase de la vista
	 */
	private ContactDialog window;
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
	public ContactDialogController(ContactManager manager, VCard vcard) {
		super();
		this.manager = manager;
		this.vcard = vcard;
		rb = Utils.getResourceBundle();
	}

	public void start() throws Exception {
		window = new ContactDialog();

		if (vcard != null) {
			loadVCard();
		}

		mainFunctions();
		fullNameAutoloadFunction();
		photoFunctions();
		selectElementFunctions();
		addElementFunctions();
		editElementFunctions();
		removeElementFunctions();
		upElementFunctions();
		downElementFunctions();
		
		// Foco en el campo inicial
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				window.getNameTextField().requestFocus();
			}
		});
		

		Optional<VCard> result = window.showAndWait();

		result.ifPresent(new Consumer<VCard>() {

			@Override
			public void accept(VCard t) {
				vcard = t;
			}
		});

	}

	/**
	 * Carga la información del contacto en el formulario
	 */
	private void loadVCard() {
		String fullName = loadVCardName();
		loadVCardBirthday();
		loadVCardCategory();
		loadVCardNote();
		loadVCardPhoto();
		loadVCardPhone();
		loadVCardEmail();
		loadVCardAddress();

		// Valores de la ventana
		window.setTitle(rb.getString(Text.I18N_MENU_CONTACT_EDIT) + ": " + fullName);
		window.getSave().setDisable(false);
	}

	/**
	 * Carga la información del nombre y apodo del contacto en el formulario
	 * 
	 * @return Nombre completo del contacto
	 */
	private String loadVCardName() {

		if (vcard.getStructuredName() != null) {
			if (vcard.getStructuredName().getGiven() != null) {
				window.getNameTextField().setText(vcard.getStructuredName().getGiven());
			}
			if (vcard.getStructuredName().getFamily() != null) {
				window.getSurnameTextField().setText(vcard.getStructuredName().getFamily());
			}
		}
		if (vcard.getNickname() != null && vcard.getNickname().getValues() != null) {
			window.getNicknameTextField().setText(vcard.getNickname().getValues().get(0));
		}
		String fullName = "";
		if (vcard.getFormattedName() != null) {
			fullName = vcard.getFormattedName().getValue();
			window.getFullNameTextField().setText(fullName);
		}
		return fullName;
	}

	/**
	 * Carga la información del cumpleaños del contacto en el formulario
	 */
	private void loadVCardBirthday() {
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
	}

	/**
	 * Carga la información de la categoría del contacto en el formulario
	 */
	private void loadVCardCategory() {
		if (vcard.getCategories() != null && !vcard.getCategories().getValues().isEmpty()) {
			for (String category : vcard.getCategories().getValues()) {
				addCategoryElement(category);
			}
		}
	}

	/**
	 * Carga la información de la nota del contacto en el formulario
	 */
	private void loadVCardNote() {
		if (vcard.getNotes() != null && !vcard.getNotes().isEmpty()) {
			window.getNotesTextArea().setText(vcard.getNotes().get(0).getValue());
		}
	}

	/**
	 * Carga la información de la foto del contacto en el formulario
	 */
	private void loadVCardPhoto() {
		if (vcard.getPhotos() != null && !vcard.getPhotos().isEmpty()) {
			String url = vcard.getPhotos().get(0).getUrl();
			imageFile = new File(manager.getPhotoDir() + url);
			try {
				loadImage();
			} catch (IOException e) {
				LOGGER.severe("No se ha podido cargar la foto de " + vcard.getUid(), e);
				new AlertExceptionDialog(e).showAndWait();
				imageFile = null;
			}
		}
	}

	/**
	 * Carga la información del teléfono del contacto en el formulario
	 */
	private void loadVCardPhone() {
		if (vcard.getTelephoneNumbers() != null && !vcard.getTelephoneNumbers().isEmpty()) {
			for (Telephone phone : vcard.getTelephoneNumbers()) {
				TelephoneType type = phone.getTypes().get(0);
				PhoneInfo info = new PhoneInfo(phone.getText(), PhoneTypeEnum.findByPhoneType(type), phone.getParameter(CustomParameter.EMAIL_TAG));
				addPhoneElement(info);
			}
		}
	}

	/**
	 * Carga la información del email del contacto en el formulario
	 */
	private void loadVCardEmail() {
		if (vcard.getEmails() != null && !vcard.getEmails().isEmpty()) {
			for (Email email : vcard.getEmails()) {
				EmailType type = email.getTypes().get(0);
				EmailInfo info = new EmailInfo(email.getValue(), EmailTypeEnum.findByEmailType(type), email.getParameter(CustomParameter.EMAIL_TAG));
				addEmailElement(info);
			}
		}
	}

	/**
	 * Carga la información de la dirección del contacto en el formulario
	 */
	private void loadVCardAddress() {
		if (vcard.getAddresses() != null && !vcard.getAddresses().isEmpty()) {
			for (Address address : vcard.getAddresses()) {
				AddressType type = address.getTypes().get(0);
				AddressInfo info = new AddressInfo(address.getStreetAddress(), address.getLocality(), address.getRegion(), address.getPostalCode(), address.getCountry(),
						AddressTypeEnum.findByAddressType(type), address.getParameter(CustomParameter.ADDRESS_TAG));
				addAddressElement(info);
			}
		}
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
				imageFile = fileChooser.showOpenDialog(window.getDialogPane().getScene().getWindow());
				try {
					loadImage();
				} catch (IOException e) {
					LOGGER.severe("Fichero no encontrado " + imageFile.getAbsolutePath(), e);
					new AlertExceptionDialog(e).showAndWait();
					imageFile = null;
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
		window.setPhoto(image);
		is.close();
	}

	/**
	 * Setea la funcionalidad de los elementos seleccionados
	 */
	private void selectElementFunctions() {
		// Selección de categoría
		List<Node> categoryNodes = new ArrayList<>();
		categoryNodes.add(window.getListViewCategory());
		categoryNodes.add(window.getEditCategory());
		categoryNodes.add(window.getRemoveCategory());
		categoryNodes.add(window.getUpCategory());
		categoryNodes.add(window.getDownCategory());
		window.getListViewCategory().getSelectionModel().selectedItemProperty()
				.addListener(new ElementEnablerChangeListener<>(categoryNodes.toArray(new Node[categoryNodes.size()])));

		// Selección de teléfono
		List<Node> phoneNodes = new ArrayList<>();
		phoneNodes.add(window.getListViewPhone());
		phoneNodes.add(window.getEditPhone());
		phoneNodes.add(window.getRemovePhone());
		phoneNodes.add(window.getUpPhone());
		phoneNodes.add(window.getDownPhone());
		window.getListViewPhone().getSelectionModel().selectedItemProperty().addListener(new ElementEnablerChangeListener<>(phoneNodes.toArray(new Node[phoneNodes.size()])));

		// Selección de email
		List<Node> emailNodes = new ArrayList<>();
		emailNodes.add(window.getListViewEmail());
		emailNodes.add(window.getEditEmail());
		emailNodes.add(window.getRemoveEmail());
		emailNodes.add(window.getUpEmail());
		emailNodes.add(window.getDownEmail());
		window.getListViewEmail().getSelectionModel().selectedItemProperty().addListener(new ElementEnablerChangeListener<>(emailNodes.toArray(new Node[emailNodes.size()])));

		// Selección de dirección
		List<Node> addressNodes = new ArrayList<>();
		addressNodes.add(window.getListViewAddress());
		addressNodes.add(window.getEditAddress());
		addressNodes.add(window.getRemoveAddress());
		addressNodes.add(window.getUpAddress());
		addressNodes.add(window.getDownAddress());
		window.getListViewAddress().getSelectionModel().selectedItemProperty().addListener(new ElementEnablerChangeListener<>(addressNodes.toArray(new Node[addressNodes.size()])));
	}

	/**
	 * Setea la funcionalidad de los botones de añadir teléfono/email/dirección
	 */
	private void addElementFunctions() {
		// Añadir categoría
		window.getAddCategory().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				CategoryDialog dialog = new CategoryDialog(manager.getCategories());
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
				PhoneDialog dialog = new PhoneDialog();
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
				EmailDialog dialog = new EmailDialog();
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
				AddressDialog dialog = new AddressDialog();
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

		CategoryDialog dialog = new CategoryDialog(info, manager.getCategories());
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

		PhoneDialog dialog = new PhoneDialog(info);
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

		EmailDialog dialog = new EmailDialog(info);
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

		AddressDialog dialog = new AddressDialog(info);
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
		window.getRemoveCategory()
				.setOnAction(new RemoveElementContactEventHandler<ActionEvent, String>(window.getListViewCategory(), rb.getString(Text.I18N_CONTACT_CATEGORY_REMOVE)) {

					@Override
					public String infoToText(String info) {
						return info;
					}
				});

		// Eliminar teléfono
		window.getRemovePhone().setOnAction(new RemoveElementContactEventHandler<ActionEvent, PhoneInfo>(window.getListViewPhone(), rb.getString(Text.I18N_CONTACT_PHONE_REMOVE)) {

			@Override
			public String infoToText(PhoneInfo info) {
				return DisplayUtil.getPhoneInfo(info, rb);
			}
		});

		// Eliminar email
		window.getRemoveEmail().setOnAction(new RemoveElementContactEventHandler<ActionEvent, EmailInfo>(window.getListViewEmail(), rb.getString(Text.I18N_CONTACT_EMAIL_REMOVE)) {

			@Override
			public String infoToText(EmailInfo info) {
				return DisplayUtil.getEmailInfo(info, rb);
			}
		});

		// Eliminar dirección
		window.getRemoveAddress()
				.setOnAction(new RemoveElementContactEventHandler<ActionEvent, AddressInfo>(window.getListViewAddress(), rb.getString(Text.I18N_CONTACT_ADDRESS_REMOVE)) {

					@Override
					public String infoToText(AddressInfo info) {
						return DisplayUtil.getAddressInfo(info, rb);
					}
				});
	}

	/**
	 * Setea la funcionalidad de los botones de subir posición de
	 * teléfono/email/dirección y los habilita cuando hay un elemento
	 * seleccionado
	 */
	private void upElementFunctions() {
		// Subir categoría
		window.getUpCategory().setOnAction(new UpDownElementEventHandler<>(window.getListViewCategory(), true));

		// Subir teléfono
		window.getUpPhone().setOnAction(new UpDownElementEventHandler<>(window.getListViewPhone(), true));

		// Subir email
		window.getUpEmail().setOnAction(new UpDownElementEventHandler<>(window.getListViewEmail(), true));

		// Subir dirección
		window.getUpAddress().setOnAction(new UpDownElementEventHandler<>(window.getListViewAddress(), true));
	}

	/**
	 * Setea la funcionalidad de los botones de bajar posición de
	 * teléfono/email/dirección y los habilita cuando hay un elemento
	 * seleccionado
	 */
	private void downElementFunctions() {
		// Bajar categoría
		window.getDownCategory().setOnAction(new UpDownElementEventHandler<>(window.getListViewCategory(), false));

		// Bajar teléfono
		window.getDownPhone().setOnAction(new UpDownElementEventHandler<>(window.getListViewPhone(), false));

		// Bajar email
		window.getDownEmail().setOnAction(new UpDownElementEventHandler<>(window.getListViewEmail(), false));

		// Bajar dirección
		window.getDownAddress().setOnAction(new UpDownElementEventHandler<>(window.getListViewAddress(), false));
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

		window.setResultConverter(new Callback<ButtonType, VCard>() {

			@Override
			public VCard call(ButtonType param) {
				if (param == window.getSaveButtonType()) {
					save();
					return vcard;
				}
				return null;
			}
		});

	}

	/**
	 * Guarda la información introducida creando o actualizando el contacto
	 */
	private void save() {
		boolean newContact = false;
		if (vcard == null) {
			vcard = new VCard(VCardVersion.V4_0);
			vcard.setUid(Uid.random());
			newContact = true;
		}

		// Almacenamiento de la información
		saveName();
		saveBirthday();
		saveNotes();
		savePhoto();
		saveCagetory();
		savePhone();
		saveEmail();
		saveAddress();

		// Fecha de edición
		vcard.setRevision(new Date());

		// Guardar
		if (newContact) {
			try {
				manager.createContact(vcard);
			} catch (IOException e) {
				LOGGER.severe("No se ha podido guardar el contacto " + vcard.getFormattedName().getValue(), e);
				new AlertExceptionDialog(e).showAndWait();
			}
		} else {
			try {
				manager.updateContact(vcard);
			} catch (IOException e) {
				LOGGER.severe("No se ha podido actualizar el contacto " + vcard.getUid(), e);
				new AlertExceptionDialog(e).showAndWait();
			}
		}

	}

	/**
	 * Almacena la información del nombre
	 */
	private void saveName() {
		vcard.setFormattedName(window.getFullNameTextField().getText().trim());
		StructuredName sn = new StructuredName();
		sn.setGiven(window.getNameTextField().getText().trim());
		sn.setFamily(window.getSurnameTextField().getText().trim());
		Nickname nicknames = null;
		if (! window.getNicknameTextField().getText().trim().isEmpty()) {
			nicknames = new Nickname();
			nicknames.getValues().add(window.getNicknameTextField().getText());
		}
		vcard.setNickname(nicknames);
		vcard.setStructuredName(sn);
	}

	/**
	 * Almacena la información de cumpleaños
	 */
	private void saveBirthday() {
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
		} else if (!Utils.isBlank(year) || !Utils.isBlank(month) || !Utils.isBlank(day)) {
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
	}

	/**
	 * Almacena la información de las notas
	 */
	private void saveNotes() {
		if (!window.getNotesTextArea().getText().isEmpty()) {
			vcard.getNotes().clear();
			vcard.getNotes().add(new Note(window.getNotesTextArea().getText().trim()));
		}
	}

	/**
	 * Almacena la información de la foto
	 */
	private void savePhoto() {
		if (imageFile != null) {
			try {
				String fileName = vcard.getUid().getValue().replaceAll("urn:uuid:", "");
				fileName = manager.savePhotoFile(imageFile, fileName);
				if (fileName == null) {
					return;
				}
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
			} catch (IOException e) {
				LOGGER.severe("No se ha podido guardar la foto de " + vcard.getUid(), e);
				new AlertExceptionDialog(e).showAndWait();
			}
		} else {
			// No se ha seleccionado foto
			cleanPhoto();
		}
	}

	/**
	 * Elimina la foto del directorio y de la información de contacto
	 */
	private void cleanPhoto() {
		if (vcard.getPhotos() != null && !vcard.getPhotos().isEmpty()) {
			String previousUrl = vcard.getPhotos().get(0).getUrl();
			try {
				manager.removePhotoFile(previousUrl);
			} catch (IOException e) {
				LOGGER.severe("No se ha podido eliminar la foto de " + vcard.getUid(), e);
				new AlertExceptionDialog(e).showAndWait();
			}
			vcard.getPhotos().clear();
		}
	}

	/**
	 * Almacena la información de la categoría
	 */
	private void saveCagetory() {
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
	}

	/**
	 * Almacena la información del teléfono
	 */
	private void savePhone() {
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
	}

	/**
	 * Almacena la información del email
	 */
	private void saveEmail() {
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
	}

	/**
	 * Almacena la información de la dirección
	 */
	private void saveAddress() {
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

	/**
	 * Obtiene la información del contacto creado / modificado o null si no se
	 * ha creado ninguno
	 * 
	 * @return Información del contacto
	 */
	public VCard getVcard() {
		return vcard;
	}

}
