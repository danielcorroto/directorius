package com.danielcorroto.directorius.view;

import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Gestiona la creación de la ventana de creación/edición de contactos
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class ContactWindow {
	/**
	 * Porcentajes para los elementos del panel principal
	 */
	private static final int[] PERSONALPANE_PERCENTAGE = new int[] { 25, 45, 30 };

	/**
	 * Stage de la ventana
	 */
	private Stage stage;
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
	 * Combo para la lista de categorías
	 */
	private ComboBox<String> categoryCombo;
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
	 * Botón para cerrar el diálogo
	 */
	private Button cancel;
	/**
	 * Botón para guardar el contacto y cerrar el diálogo
	 */
	private Button save;

	/**
	 * Para i18n
	 */
	private ResourceBundle rb;

	public ContactWindow() {
		super();
		rb = ResourceBundle.getBundle(Text.RESOURCE_BUNDLE, Locale.getDefault());
	}

	/**
	 * Construcción de la ventana de creación/edición de contactos
	 * 
	 * @param stage
	 *            Stage de la ventana
	 */
	public void build(Stage stage) {
		this.stage = stage;

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

		setLabel(gridPane, rb.getString(Text.I18N_INFORMATION_PERSONAL_NAME), 0);
		nameTextField = new TextField();
		gridPane.add(nameTextField, 1, 0);

		setLabel(gridPane, rb.getString(Text.I18N_INFORMATION_PERSONAL_SURNAME), 1);
		surnameTextField = new TextField();
		gridPane.add(surnameTextField, 1, 1);

		setLabel(gridPane, rb.getString(Text.I18N_INFORMATION_PERSONAL_FULLNAME), 2);
		fullNameTextField = new TextField();
		gridPane.add(fullNameTextField, 1, 2);

		setLabel(gridPane, rb.getString(Text.I18N_INFORMATION_PERSONAL_BIRTHDAY), 3);
		gridPane.add(buildBirthdayForm(), 1, 3);

		setLabel(gridPane, rb.getString(Text.I18N_INFORMATION_PERSONAL_CATEGORIES), 4);
		categoryCombo = new ComboBox<>();
		categoryCombo.setEditable(true);
		gridPane.add(categoryCombo, 1, 4);

		setLabel(gridPane, rb.getString(Text.I18N_INFORMATION_PERSONAL_NOTES), 5);
		notesTextArea = new TextArea();
		notesTextArea.setMaxHeight(100);
		gridPane.add(notesTextArea, 1, 5);

		imageView = new ImageView(new Image(MainWindow.class.getResourceAsStream(ResourcePath.IMG_LOGO)));
		gridPane.add(imageView, 2, 0);
		GridPane.setRowSpan(imageView, 5);
		GridPane.setHalignment(imageView, HPos.CENTER);

		HBox photoButtons = new HBox();
		photoButtons.setAlignment(Pos.CENTER);
		photoButtons.setSpacing(20);
		photoClean = new Button(rb.getString(Text.I18N_EDITCONTACT_PHOTO_CLEAN));
		photoButtons.getChildren().add(photoClean);
		photoSearch = new Button(rb.getString(Text.I18N_EDITCONTACT_PHOTO_SEARCH));
		photoButtons.getChildren().add(photoSearch);
		gridPane.add(photoButtons, 2, 5);

		// Botones de cancelar y guardar
		HBox buttons = new HBox();
		buttons.setAlignment(Pos.CENTER);
		buttons.setSpacing(100);
		cancel = new Button(rb.getString(Text.I18N_EDITCONTACT_CANCEL));
		buttons.getChildren().add(cancel);
		save = new Button(rb.getString(Text.I18N_EDITCONTACT_SAVE));
		buttons.getChildren().add(save);

		// Crear ventana
		VBox main = new VBox();
		main.getChildren().add(gridPane);
		main.getChildren().add(buttons);

		// Crear scene y stage
		Scene scene = new Scene(main);
		stage.setScene(scene);

		// Setea propiedades
		double width = Screen.getPrimary().getBounds().getWidth();
		double height = Screen.getPrimary().getBounds().getHeight();
		stage.setWidth(width / 2);
		stage.setHeight(height - 100);
		stage.setTitle(rb.getString(Text.I18N_MENU_CONTACT_ADD));
		Image logo = new Image(MainWindow.class.getResourceAsStream(ResourcePath.IMG_LOGO));
		stage.getIcons().add(logo);
		stage.initModality(Modality.APPLICATION_MODAL);
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
	 * Obtiene el componente ComboBox con la categoría seleccionada
	 * 
	 * @return Componente con la categoría seleccionada
	 */
	public ComboBox<String> getCategoryCombo() {
		return categoryCombo;
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
	 * Obtiene el componente Button para cancelar la edición
	 * 
	 * @return Componente para cancelar la edición
	 */
	public Button getCancel() {
		return cancel;
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
	 * Obtiene el componente Stage
	 * 
	 * @return Componente base de la ventana
	 */
	public Stage getStage() {
		return stage;
	}

	/**
	 * Obtiene el componente Button para cerrar la ventana
	 * 
	 * @return Componente para cerrar la ventana
	 */
	public void close() {
		stage.close();
	}

}
