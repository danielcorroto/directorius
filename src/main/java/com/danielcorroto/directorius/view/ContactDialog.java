package com.danielcorroto.directorius.view;

import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;

import com.danielcorroto.directorius.controller.DisplayUtil;
import com.danielcorroto.directorius.controller.data.AddressInfo;
import com.danielcorroto.directorius.controller.data.EmailInfo;
import com.danielcorroto.directorius.controller.data.PhoneInfo;

import ezvcard.VCard;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ContactDialog extends Dialog<VCard> {
	/**
	 * Porcentajes para los elementos del panel principal
	 */
	private static final int[] PERSONALPANE_PERCENTAGE = new int[] { 25, 45, 30 };

	private static final int BUTTON_IMAGE_SIZE = 24;

	/**
	 * Caja de texto para informar del nombre
	 */
	private TextField nameTextField;
	/**
	 * Caja de texto para informar del apellido
	 */
	private TextField surnameTextField;
	/**
	 * Caja de texto para informar del nombre completo
	 */
	private TextField fullNameTextField;
	/**
	 * Combo para el año de nacimiento
	 */
	private ComboBox<String> comboBoxYear;
	/**
	 * Combo para el mes de nacimiento
	 */
	private ComboBox<String> comboBoxMonth;
	/**
	 * Combo para el día de nacimiento
	 */
	private ComboBox<String> comboBoxDay;
	/**
	 * Caja de texto para las notas
	 */
	private TextArea notesTextArea;
	/**
	 * Botón para borrar la foto cargada
	 */
	private Button photoClean;
	/**
	 * Botón para buscar y cargar foto
	 */
	private Button photoSearch;
	/**
	 * Contenedor de la foto
	 */
	private ImageView imageView;
	/**
	 * Botón para añadir categoría
	 */
	private Button addCategory;
	/**
	 * Botón para editar categoría
	 */
	private Button editCategory;
	/**
	 * Botón para eliminar categoría
	 */
	private Button removeCategory;
	/**
	 * Botón para añadir teléfono
	 */
	private Button addPhone;
	/**
	 * Botón para editar teléfono
	 */
	private Button editPhone;
	/**
	 * Botón para eliminar teléfono
	 */
	private Button removePhone;
	/**
	 * Botón para añadir email
	 */
	private Button addEmail;
	/**
	 * Botón para editar email
	 */
	private Button editEmail;
	/**
	 * Botón para eliminar email
	 */
	private Button removeEmail;
	/**
	 * Botón para añadir dirección
	 */
	private Button addAddress;
	/**
	 * Botón para editar dirección
	 */
	private Button editAddress;
	/**
	 * Botón para eliminar dirección
	 */
	private Button removeAddress;
	/**
	 * Contenedor de teléfonos
	 */
	private ListView<String> listViewCategory;
	/**
	 * Contenedor de teléfonos
	 */
	private ListView<PhoneInfo> listViewPhone;
	/**
	 * Contenedor de emails
	 */
	private ListView<EmailInfo> listViewEmail;
	/**
	 * Contenedor de direcciones
	 */
	private ListView<AddressInfo> listViewAddress;
	/**
	 * Botón para guardar el contacto y cerrar el diálogo
	 */
	private Button save;
	/**
	 * Tipo de botón de guardar el contacto y cerrar el diálogo
	 */
	private ButtonType saveButtonType;

	/**
	 * Para i18n
	 */
	private ResourceBundle rb;

	/**
	 * Constructor por defecto. Crea el panel de diálogo
	 */
	public ContactDialog() {
		super();
		rb = ResourceBundle.getBundle(Text.RESOURCE_BUNDLE, Locale.getDefault());
		build();
	}

	/**
	 * Construcción de la ventana de creación/edición de contactos
	 * 
	 */
	public void build() {
		// Crear gridPane
		GridPane gridPane = new GridPane();
		ColumnConstraints[] columnConstraints = new ColumnConstraints[PERSONALPANE_PERCENTAGE.length];
		for (int i = 0; i < PERSONALPANE_PERCENTAGE.length; i++) {
			columnConstraints[i] = new ColumnConstraints();
			columnConstraints[i].setPercentWidth(PERSONALPANE_PERCENTAGE[i]);
		}
		gridPane.getColumnConstraints().addAll(columnConstraints);
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		gridPane.setPadding(new Insets(25, 25, 25, 25));

		// Datos personales
		setLabel(gridPane, rb.getString(Text.I18N_CONTACT_NAME), 0);
		nameTextField = new TextField();
		gridPane.add(nameTextField, 1, 0);

		setLabel(gridPane, rb.getString(Text.I18N_CONTACT_SURNAME), 1);
		surnameTextField = new TextField();
		gridPane.add(surnameTextField, 1, 1);

		setLabel(gridPane, rb.getString(Text.I18N_CONTACT_FULLNAME), 2);
		fullNameTextField = new TextField();
		gridPane.add(fullNameTextField, 1, 2);

		setLabel(gridPane, rb.getString(Text.I18N_CONTACT_BIRTHDAY), 3);
		gridPane.add(buildBirthdayForm(), 1, 3);

		setLabel(gridPane, rb.getString(Text.I18N_CONTACT_NOTES), 4);
		notesTextArea = new TextArea();
		notesTextArea.setMaxHeight(100);
		gridPane.add(notesTextArea, 1, 4);

		// Fotografía
		imageView = new ImageView(new Image(MainWindow.class.getResourceAsStream(ResourcePath.IMG_LOGO)));
		gridPane.add(imageView, 2, 0);
		GridPane.setRowSpan(imageView, 4);
		GridPane.setHalignment(imageView, HPos.CENTER);

		HBox photoButtons = new HBox();
		photoButtons.setAlignment(Pos.CENTER);
		photoButtons.setSpacing(20);
		photoClean = new Button(rb.getString(Text.I18N_CONTACT_PHOTO_CLEAN));
		photoButtons.getChildren().add(photoClean);
		photoSearch = new Button(rb.getString(Text.I18N_CONTACT_PHOTO_SEARCH));
		photoButtons.getChildren().add(photoSearch);
		gridPane.add(photoButtons, 2, 4);

		// Categoría
		setLabel(gridPane, rb.getString(Text.I18N_CONTACT_CATEGORY), 5);
		addCategory = buildElementButton(ResourcePath.IMG_EDIT_CONTACT_CATEGORY_ADD, Text.I18N_CONTACT_CATEGORY_ADD);
		gridPane.add(addCategory, 0, 5);
		listViewCategory = new ListView<>();
		listViewCategory.setMaxHeight(100);
		listViewCategory.setCellFactory(createCategoryListViewCellFactory());
		gridPane.add(listViewCategory, 1, 5);

		HBox categoryButtons = new HBox();
		categoryButtons.setAlignment(Pos.CENTER);
		categoryButtons.setSpacing(20);
		editCategory = buildElementButton(ResourcePath.IMG_EDIT_CONTACT_EDIT, Text.I18N_CONTACT_CATEGORY_EDIT);
		editCategory.setDisable(true);
		categoryButtons.getChildren().add(editCategory);
		removeCategory = buildElementButton(ResourcePath.IMG_EDIT_CONTACT_REMOVE, Text.I18N_CONTACT_CATEGORY_REMOVE);
		removeCategory.setDisable(true);
		categoryButtons.getChildren().add(removeCategory);
		gridPane.add(categoryButtons, 2, 5);

		// Teléfono
		setLabel(gridPane, rb.getString(Text.I18N_CONTACT_PHONE), 6);
		addPhone = buildElementButton(ResourcePath.IMG_EDIT_CONTACT_PHONE_ADD, Text.I18N_CONTACT_PHONE_ADD);
		gridPane.add(addPhone, 0, 6);
		listViewPhone = new ListView<>();
		listViewPhone.setMaxHeight(100);
		listViewPhone.setCellFactory(createPhoneListViewCellFactory());
		gridPane.add(listViewPhone, 1, 6);

		HBox phoneButtons = new HBox();
		phoneButtons.setAlignment(Pos.CENTER);
		phoneButtons.setSpacing(20);
		editPhone = buildElementButton(ResourcePath.IMG_EDIT_CONTACT_EDIT, Text.I18N_CONTACT_PHONE_EDIT);
		editPhone.setDisable(true);
		phoneButtons.getChildren().add(editPhone);
		removePhone = buildElementButton(ResourcePath.IMG_EDIT_CONTACT_REMOVE, Text.I18N_CONTACT_PHONE_REMOVE);
		removePhone.setDisable(true);
		phoneButtons.getChildren().add(removePhone);
		gridPane.add(phoneButtons, 2, 6);

		// Email
		setLabel(gridPane, rb.getString(Text.I18N_CONTACT_EMAIL), 7);
		addEmail = buildElementButton(ResourcePath.IMG_EDIT_CONTACT_EMAIL_ADD, Text.I18N_CONTACT_EMAIL_ADD);
		gridPane.add(addEmail, 0, 7);
		listViewEmail = new ListView<>();
		listViewEmail.setMaxHeight(100);
		listViewEmail.setCellFactory(createEmailListViewCellFactory());
		gridPane.add(listViewEmail, 1, 7);

		HBox emailButtons = new HBox();
		emailButtons.setAlignment(Pos.CENTER);
		emailButtons.setSpacing(20);
		editEmail = buildElementButton(ResourcePath.IMG_EDIT_CONTACT_EDIT, Text.I18N_CONTACT_EMAIL_EDIT);
		editEmail.setDisable(true);
		emailButtons.getChildren().add(editEmail);
		removeEmail = buildElementButton(ResourcePath.IMG_EDIT_CONTACT_REMOVE, Text.I18N_CONTACT_EMAIL_REMOVE);
		removeEmail.setDisable(true);
		emailButtons.getChildren().add(removeEmail);
		gridPane.add(emailButtons, 2, 7);

		// Dirección
		setLabel(gridPane, rb.getString(Text.I18N_CONTACT_ADDRESS), 8);
		addAddress = buildElementButton(ResourcePath.IMG_EDIT_CONTACT_ADDRESS_ADD, Text.I18N_CONTACT_ADDRESS_ADD);
		gridPane.add(addAddress, 0, 8);
		listViewAddress = new ListView<>();
		listViewAddress.setMaxHeight(100);
		listViewAddress.setCellFactory(createAddressListViewCellFactory());
		gridPane.add(listViewAddress, 1, 8);

		HBox addressButtons = new HBox();
		addressButtons.setAlignment(Pos.CENTER);
		addressButtons.setSpacing(20);
		editAddress = buildElementButton(ResourcePath.IMG_EDIT_CONTACT_EDIT, Text.I18N_CONTACT_ADDRESS_EDIT);
		editAddress.setDisable(true);
		addressButtons.getChildren().add(editAddress);
		removeAddress = buildElementButton(ResourcePath.IMG_EDIT_CONTACT_REMOVE, Text.I18N_CONTACT_ADDRESS_REMOVE);
		removeAddress.setDisable(true);
		addressButtons.getChildren().add(removeAddress);
		gridPane.add(addressButtons, 2, 8);

		// Botones de mostrar/cerrar
		saveButtonType = new ButtonType(rb.getString(Text.I18N_CONTACT_SAVE), ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE, saveButtonType);
		save = (Button) this.getDialogPane().lookupButton(saveButtonType);
		save.setDisable(true);

		// Crear ventana
		VBox main = new VBox();
		main.getChildren().add(gridPane);

		// Crear scene y stage
		getDialogPane().setContent(main);

		// Setea propiedades
		Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
		double width = Screen.getPrimary().getBounds().getWidth();
		double height = Screen.getPrimary().getBounds().getHeight();
		this.getDialogPane().setMaxWidth(width / 2);
		this.getDialogPane().setMaxHeight(height - 100);
		Image logo = new Image(MainWindow.class.getResourceAsStream(ResourcePath.IMG_LOGO));
		stage.getIcons().add(logo);
	}

	/**
	 * Setea la etiqueta del formulario
	 * 
	 * @param gridPane
	 *            Grid donde ubicar la etiqueta
	 * @param text
	 *            Texto a incorporar en la etiqueta
	 * @param row
	 *            Fila del grid donde ubicar la etiqueta
	 */
	private void setLabel(GridPane gridPane, String text, int row) {
		Label label = new Label(text);
		gridPane.add(label, 0, row);
		GridPane.setHalignment(label, HPos.RIGHT);
	}

	/**
	 * Construye el formulario de cumpleaños
	 * 
	 * @return Contenedor de los combos
	 */
	private Node buildBirthdayForm() {
		ObservableList<String> comboBoxYearOptions = FXCollections.observableArrayList();
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		comboBoxYearOptions.add("");
		for (int i = currentYear; i > currentYear - 150; i--) {
			comboBoxYearOptions.add(String.valueOf(i));
		}
		comboBoxYear = new ComboBox<>(comboBoxYearOptions);

		ObservableList<String> comboBoxMonthOptions = FXCollections.observableArrayList();
		comboBoxMonthOptions.add("");
		for (int i = 1; i <= 12; i++) {
			comboBoxMonthOptions.add(String.valueOf(i));
		}
		comboBoxMonth = new ComboBox<>(comboBoxMonthOptions);

		ObservableList<String> comboBoxDayOptions = FXCollections.observableArrayList();
		comboBoxDayOptions.add("");
		for (int i = 1; i <= 31; i++) {
			comboBoxDayOptions.add(String.valueOf(i));
		}
		comboBoxDay = new ComboBox<>(comboBoxDayOptions);

		HBox result = new HBox();
		result.setSpacing(5);
		result.getChildren().add(comboBoxYear);
		result.getChildren().add(comboBoxMonth);
		result.getChildren().add(comboBoxDay);

		return result;
	}

	/**
	 * Construye el botón añadir elemento (teléfono/email/dirección)
	 * 
	 * @param resourcePathRuta
	 *            de la imagen del botón
	 * @param i18n
	 *            I18n tooltip del botón
	 * @return Botón añadir
	 */
	private Button buildElementButton(String resourcePath, String i18n) {
		ImageView addImage = new ImageView(new Image(MainWindow.class.getResourceAsStream(resourcePath)));
		addImage.setFitWidth(BUTTON_IMAGE_SIZE);
		addImage.setFitHeight(BUTTON_IMAGE_SIZE);
		Button button = new Button();
		button.setGraphic(addImage);
		button.setTooltip(new Tooltip(rb.getString(i18n)));
		return button;
	}

	/**
	 * Crea la visualización para el listview de categorías
	 * 
	 * @return Objeto que implementa la visualización del texto del listview de
	 *         categorías
	 */
	private Callback<ListView<String>, ListCell<String>> createCategoryListViewCellFactory() {
		Callback<ListView<String>, ListCell<String>> cellFactory = new Callback<ListView<String>, ListCell<String>>() {

			@Override
			public ListCell<String> call(ListView<String> param) {
				return new ListCell<String>() {
					@Override
					protected void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);

						if (empty || item == null) {
							setText(null);
						} else {
							setText(item);
						}
					}
				};
			}
		};

		return cellFactory;
	}

	/**
	 * Crea la visualización para el listview de teléfonos
	 * 
	 * @return Objeto que implementa la visualización del texto del listview de
	 *         teléfonos
	 */
	private Callback<ListView<PhoneInfo>, ListCell<PhoneInfo>> createPhoneListViewCellFactory() {
		Callback<ListView<PhoneInfo>, ListCell<PhoneInfo>> cellFactory = new Callback<ListView<PhoneInfo>, ListCell<PhoneInfo>>() {

			@Override
			public ListCell<PhoneInfo> call(ListView<PhoneInfo> param) {
				return new ListCell<PhoneInfo>() {
					@Override
					protected void updateItem(PhoneInfo item, boolean empty) {
						super.updateItem(item, empty);

						if (empty || item == null || item.getNumber() == null) {
							setText(null);
						} else {
							setText(DisplayUtil.getPhoneInfo(item, rb));
						}
					}
				};
			}
		};

		return cellFactory;
	}

	/**
	 * Crea la visualización para el listview de emails
	 * 
	 * @return Objeto que implementa la visualización del texto del listview de
	 *         emails
	 */
	private Callback<ListView<EmailInfo>, ListCell<EmailInfo>> createEmailListViewCellFactory() {
		Callback<ListView<EmailInfo>, ListCell<EmailInfo>> cellFactory = new Callback<ListView<EmailInfo>, ListCell<EmailInfo>>() {

			@Override
			public ListCell<EmailInfo> call(ListView<EmailInfo> param) {
				return new ListCell<EmailInfo>() {
					@Override
					protected void updateItem(EmailInfo item, boolean empty) {
						super.updateItem(item, empty);

						if (empty || item == null || item.getEmail() == null) {
							setText(null);
						} else {
							setText(DisplayUtil.getEmailInfo(item, rb));
						}
					}
				};
			}
		};

		return cellFactory;
	}

	/**
	 * Crea la visualización para el listview de direcciones
	 * 
	 * @return Objeto que implementa la visualización del texto del listview de
	 *         direcciones
	 */
	private Callback<ListView<AddressInfo>, ListCell<AddressInfo>> createAddressListViewCellFactory() {
		Callback<ListView<AddressInfo>, ListCell<AddressInfo>> cellFactory = new Callback<ListView<AddressInfo>, ListCell<AddressInfo>>() {

			@Override
			public ListCell<AddressInfo> call(ListView<AddressInfo> param) {
				return new ListCell<AddressInfo>() {
					@Override
					protected void updateItem(AddressInfo item, boolean empty) {
						super.updateItem(item, empty);

						if (empty || item == null) {
							setText(null);
						} else {
							setText(DisplayUtil.getAddressInfo(item, rb));
						}
					}
				};
			}
		};

		return cellFactory;
	}

	/**
	 * Obtiene el componente TextField con el nombre del contacto
	 * 
	 * @return Componente con el nombre
	 */
	public TextField getNameTextField() {
		return nameTextField;
	}

	/**
	 * Obtiene el componente TextField con el apellido del contacto
	 * 
	 * @return Componente con el apellido
	 */
	public TextField getSurnameTextField() {
		return surnameTextField;
	}

	/**
	 * Obtiene el componente TextField con el nombre completo del contacto
	 * 
	 * @return Componente con el nombre completo
	 */
	public TextField getFullNameTextField() {
		return fullNameTextField;
	}

	/**
	 * Obtiene el componente ComboBox con el año de nacimiento
	 * 
	 * @return Componente con el año de nacimiento
	 */
	public ComboBox<String> getComboBoxYear() {
		return comboBoxYear;
	}

	/**
	 * Obtiene el componente ComboBox con el mes de nacimiento
	 * 
	 * @return Componente con el mes de nacimiento
	 */
	public ComboBox<String> getComboBoxMonth() {
		return comboBoxMonth;
	}

	/**
	 * Obtiene el componente ComboBox con el día de nacimiento
	 * 
	 * @return Componente con el día de nacimiento
	 */
	public ComboBox<String> getComboBoxDay() {
		return comboBoxDay;
	}

	/**
	 * Obtiene el componente TextArea con las notas del contacto
	 * 
	 * @return Componente con las notas del contacto
	 */
	public TextArea getNotesTextArea() {
		return notesTextArea;
	}

	/**
	 * Obtiene el componente Button para limpiar la foto cargada
	 * 
	 * @return Componente para limpiar la foto cargada
	 */
	public Button getPhotoClean() {
		return photoClean;
	}

	/**
	 * Obtiene el componente Button para buscar una foto
	 * 
	 * @return Componente para buscar una foto
	 */
	public Button getPhotoSearch() {
		return photoSearch;
	}

	/**
	 * Obtiene el componente ImageView para mostrar la imagen
	 * 
	 * @return Componente para mostrar la imagen
	 */
	public ImageView getImageView() {
		return imageView;
	}

	/**
	 * Obtiene el componente Button para añadir una categoría
	 * 
	 * @return Componente para añadir una categoría
	 */
	public Button getAddCategory() {
		return addCategory;
	}

	/**
	 * Obtiene el componente Button para editar una categoría
	 * 
	 * @return Componente para editar una categoría
	 */
	public Button getEditCategory() {
		return editCategory;
	}

	/**
	 * Obtiene el componente Button para eliminar una categoría
	 * 
	 * @return Componente para eliminar una categoría
	 */
	public Button getRemoveCategory() {
		return removeCategory;
	}

	/**
	 * Obtiene el componente Button para añadir un teléfono
	 * 
	 * @return Componente para añadir un teléfono
	 */
	public Button getAddPhone() {
		return addPhone;
	}

	/**
	 * Obtiene el componente Button para editar un teléfono
	 * 
	 * @return Componente para editar un teléfono
	 */
	public Button getEditPhone() {
		return editPhone;
	}

	/**
	 * Obtiene el componente Button para eliminar un teléfono
	 * 
	 * @return Componente para eliminar un teléfono
	 */
	public Button getRemovePhone() {
		return removePhone;
	}

	/**
	 * Obtiene el componente Button para añadir un email
	 * 
	 * @return Componente para añadir un email
	 */
	public Button getAddEmail() {
		return addEmail;
	}

	/**
	 * Obtiene el componente Button para editar un email
	 * 
	 * @return Componente para editar un email
	 */
	public Button getEditEmail() {
		return editEmail;
	}

	/**
	 * Obtiene el componente Button para eliminar un email
	 * 
	 * @return Componente para eliminar un email
	 */
	public Button getRemoveEmail() {
		return removeEmail;
	}

	/**
	 * Obtiene el componente Button para añadir una dirección
	 * 
	 * @return Componente para añadir una dirección
	 */
	public Button getAddAddress() {
		return addAddress;
	}

	/**
	 * Obtiene el componente Button para editar una dirección
	 * 
	 * @return Componente para editar una dirección
	 */
	public Button getEditAddress() {
		return editAddress;
	}

	/**
	 * Obtiene el componente Button para eliminar una dirección
	 * 
	 * @return Componente para eliminar una dirección
	 */
	public Button getRemoveAddress() {
		return removeAddress;
	}

	/**
	 * Obtiene el componente ListView que contiene las categorías
	 * 
	 * @return Componente que contiene las categorías
	 */
	public ListView<String> getListViewCategory() {
		return listViewCategory;
	}

	/**
	 * Obtiene el componente ListView que contiene los teléfonos
	 * 
	 * @return Componente que contiene los teléfonos
	 */
	public ListView<PhoneInfo> getListViewPhone() {
		return listViewPhone;
	}

	/**
	 * Obtiene el componente ListView que contiene los emails
	 * 
	 * @return Componente que contiene los emails
	 */
	public ListView<EmailInfo> getListViewEmail() {
		return listViewEmail;
	}

	/**
	 * Obtiene el componente ListView que contiene las direcciones
	 * 
	 * @return Componente que contiene las direcciones
	 */
	public ListView<AddressInfo> getListViewAddress() {
		return listViewAddress;
	}

	/**
	 * Obtiene el componente Button para guardar un contacto
	 * 
	 * @return Componente para guardar contacto
	 */
	public Button getSave() {
		return save;
	}

	/**
	 * Obtiene el componente ButtonType para guardar un contacto
	 * 
	 * @return Componente del tipo de botón para guardar un contacto
	 */
	public ButtonType getSaveButtonType() {
		return saveButtonType;
	}

}