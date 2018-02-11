package com.danielcorroto.directorius.view.info;

import java.io.InputStream;
import java.util.Locale;
import java.util.ResourceBundle;

import com.danielcorroto.directorius.view.MainWindow;
import com.danielcorroto.directorius.view.ResourcePath;
import com.danielcorroto.directorius.view.Text;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;

public abstract class AbstractDialogWindow<T> extends Dialog<T> {
	/**
	 * Gap horizontal en el grid
	 */
	private static final int GRID_HGAP = 10;
	/**
	 * Grap vertical en el grid
	 */
	private static final int GRID_VGAP = 10;

	/**
	 * Tipo de botón Guardar
	 */
	private ButtonType saveButtonType;

	/**
	 * Para i18n
	 */
	private ResourceBundle rb;

	/**
	 * Constructor por defecto de la ventana
	 * 
	 * @param edit
	 *            Indica si es una edición de contacto (true) o creación (false)
	 */
	public AbstractDialogWindow(boolean edit) {
		super();
		rb = ResourceBundle.getBundle(Text.RESOURCE_BUNDLE, Locale.getDefault());

		// Create the custom this.
		this.setTitle(rb.getString(getI18NTitle(edit)));

		// Establece el icono
		this.setGraphic(new ImageView(new Image(getGraphicStream())));

		// Botones de guardar/cancelar
		saveButtonType = new ButtonType(rb.getString(Text.I18N_CONTACT_SAVE), ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, saveButtonType);

		// Crea los campos
		GridPane grid = new GridPane();
		grid.setHgap(GRID_HGAP);
		grid.setVgap(GRID_VGAP);
		grid.setPadding(new Insets(20, 150, 10, 10));

		buildGrid(grid);
		this.getDialogPane().setContent(grid);

		// Habilitar/Deshabilitar botón de guardar
		Node saveButton = this.getDialogPane().lookupButton(saveButtonType);
		saveButton.setDisable(true);

		// Habilita botón guardar si el número no es vacío
		autoEnableSave();

		// Foco en el campo
		autoFocus();

		// Convierte la selección en el objeto correspondiente
		this.setResultConverter(buildResultConverter());

		// Setea propiedades
		Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(MainWindow.class.getResourceAsStream(ResourcePath.IMG_LOGO)));
	}

	/**
	 * Construye una fila del GridPane
	 * 
	 * @param grid
	 *            Objeto GridPane
	 * @param i18nLabel
	 *            Cadena para i18n del título
	 * @param element
	 *            Elemento (cuadro de texto, combo, etc)
	 * @param row
	 *            Fila de ubicación
	 */
	protected void buildRowGridPane(GridPane grid, String i18nLabel, Node element, int row) {
		Label label = new Label(rb.getString(i18nLabel));
		grid.add(label, 0, row);
		GridPane.setHalignment(label, HPos.RIGHT);
		grid.add(element, 1, row);
	}

	/**
	 * Obtiene el componente ButtonType del botón de guardar
	 * 
	 * @return Componente del tipo de botón guardar
	 */
	protected ButtonType getSaveButtonType() {
		return saveButtonType;
	}

	/**
	 * Obtiene el recurso para i18n
	 * 
	 * @return Recurso para multilenguaje
	 */
	protected ResourceBundle getResourceBundle() {
		return rb;
	}

	/**
	 * Obtiene el texto sin espacios al principio o final o null. Evita
	 * NullPointerException
	 * 
	 * @param str
	 *            Cadena original
	 * @return Cadena sin espacios al principio o final o null
	 */
	protected String getValue(String str) {
		if (str == null) {
			return null;
		} else {
			return str.trim();
		}

	}

	/**
	 * Obtiene el texto de un TextField o null si el objeto es nulo o el texto
	 * es nulo. Evita NullPointerException
	 * 
	 * @param field
	 *            Campo
	 * @return Cadena del campo sin espacios al principio o final o null
	 */
	protected String getTextFromTextField(TextField field) {
		if (field == null) {
			return null;
		} else if (field.getText() == null) {
			return null;
		} else {
			return field.getText().trim();
		}

	}

	/**
	 * Obtiene cadena para realizar i18n del título
	 * 
	 * @param edit
	 *            Indica si es una edición (true) o creación (false)
	 * @return Cadena para realizar i18n del título
	 */
	protected abstract String getI18NTitle(boolean edit);

	/**
	 * Obtiene InputStream para el icono principal de la ventana de diálogo
	 * 
	 * @return InputStream del icono principal
	 */
	protected abstract InputStream getGraphicStream();

	/**
	 * Construye el grid del formulario
	 * 
	 * @param grid
	 *            Grid base del formulario
	 */
	protected abstract void buildGrid(GridPane grid);

	/**
	 * Crea función para activar/desactivar el botón de guardado
	 */
	protected abstract void autoEnableSave();

	/**
	 * Genera la ubicación por defecto del foco
	 */
	protected abstract void autoFocus();

	/**
	 * Callback para construir el objeto resultado
	 * 
	 * @return Callback que construye el resultado
	 */
	protected abstract Callback<ButtonType, T> buildResultConverter();
}
