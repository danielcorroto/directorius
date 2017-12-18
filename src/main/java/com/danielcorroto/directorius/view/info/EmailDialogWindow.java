package com.danielcorroto.directorius.view.info;

import java.io.InputStream;

import com.danielcorroto.directorius.controller.data.EmailInfo;
import com.danielcorroto.directorius.controller.type.EmailTypeEnum;
import com.danielcorroto.directorius.view.ResourcePath;
import com.danielcorroto.directorius.view.Text;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

/**
 * Gestiona la creación de la ventana de creación/edición de emails
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class EmailDialogWindow extends AbstractDialogWindow<EmailInfo> {
	/**
	 * Campo del email
	 */
	private TextField emailTextField;
	/**
	 * Campo del tipo de email
	 */
	private ComboBox<EmailTypeEnum> typeComboBox;
	/**
	 * Campo de la etiqueta del email
	 */
	private TextField tagTextField;

	/**
	 * Constructor por defecto de la ventana
	 */
	public EmailDialogWindow() {
		super(false);
	}

	/**
	 * Constructor con parámetros de la ventana
	 * 
	 * @param info
	 *            Información del email
	 */
	public EmailDialogWindow(EmailInfo info) {
		super(true);
		emailTextField.setText(info.getEmail().trim());
		typeComboBox.getSelectionModel().select(info.getType());
		tagTextField.setText(info.getTag());
	}

	/**
	 * Construye el ComboBox de los tipos
	 * 
	 * @return ComboBox de los tipos
	 */
	private ComboBox<EmailTypeEnum> buildTypeComboBox() {
		ObservableList<EmailTypeEnum> observableList = FXCollections.observableArrayList();
		for (EmailTypeEnum type : EmailTypeEnum.values()) {
			observableList.add(type);
		}
		ComboBox<EmailTypeEnum> result = new ComboBox<>(observableList);

		result.setButtonCell(createEmailTypeComboBoxCellFactory().call(null));
		result.setCellFactory(createEmailTypeComboBoxCellFactory());
		result.setValue(EmailTypeEnum.getDefault());

		return result;
	}

	/**
	 * Crea la visualización para el combobox
	 * 
	 * @return Objeto que implementa la visualización del texto del combobox
	 */
	private Callback<ListView<EmailTypeEnum>, ListCell<EmailTypeEnum>> createEmailTypeComboBoxCellFactory() {
		Callback<ListView<EmailTypeEnum>, ListCell<EmailTypeEnum>> cellFactory = new Callback<ListView<EmailTypeEnum>, ListCell<EmailTypeEnum>>() {
			@Override
			public ListCell<EmailTypeEnum> call(ListView<EmailTypeEnum> l) {
				return new ListCell<EmailTypeEnum>() {
					@Override
					protected void updateItem(EmailTypeEnum item, boolean empty) {
						super.updateItem(item, empty);
						if (item == null || empty) {
							setGraphic(null);
						} else {
							setText(getResourceBundle().getString(item.getI18n()));
						}
					}
				};
			}
		};

		return cellFactory;
	}

	@Override
	protected String getI18NTitle(boolean edit) {
		if (edit) {
			return Text.I18N_CONTACT_EMAIL_EDIT;
		} else {
			return Text.I18N_CONTACT_EMAIL_ADD;
		}
	}

	@Override
	protected InputStream getGraphicStream() {
		return EmailDialogWindow.class.getResourceAsStream(ResourcePath.IMG_EDIT_CONTACT_EMAIL_ADD);
	}

	@Override
	protected void buildGrid(GridPane grid) {
		emailTextField = new TextField();
		emailTextField.setPromptText(getResourceBundle().getString(Text.I18N_CONTACT_EMAIL_EMAIL));
		typeComboBox = buildTypeComboBox();
		tagTextField = new TextField();
		tagTextField.setPromptText(getResourceBundle().getString(Text.I18N_CONTACT_EMAIL_TAG));

		buildRowGridPane(grid, Text.I18N_CONTACT_EMAIL_EMAIL, emailTextField, 0);
		buildRowGridPane(grid, Text.I18N_CONTACT_EMAIL_TYPE, typeComboBox, 1);
		buildRowGridPane(grid, Text.I18N_CONTACT_EMAIL_TAG, tagTextField, 2);
	}

	@Override
	protected void autoEnableSave() {
		Node saveButton = this.getDialogPane().lookupButton(getSaveButtonType());
		emailTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			saveButton.setDisable(newValue.trim().isEmpty());
		});
	}

	@Override
	protected void autoFocus() {
		Platform.runLater(() -> emailTextField.requestFocus());
	}

	@Override
	protected Callback<ButtonType, EmailInfo> buildResultConverter() {
		Callback<ButtonType, EmailInfo> result = new Callback<ButtonType, EmailInfo>() {

			@Override
			public EmailInfo call(ButtonType dialogButton) {
				if (dialogButton == getSaveButtonType()) {
					return new EmailInfo(emailTextField.getText().trim(), typeComboBox.getSelectionModel().getSelectedItem(), tagTextField.getText().trim());
				}
				return null;
			}
		};
		return result;
	}
}
