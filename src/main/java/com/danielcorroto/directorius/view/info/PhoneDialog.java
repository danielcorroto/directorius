package com.danielcorroto.directorius.view.info;

import java.io.InputStream;
import java.util.Collections;

import com.danielcorroto.directorius.controller.data.PhoneInfo;
import com.danielcorroto.directorius.controller.type.PhoneTypeEnum;
import com.danielcorroto.directorius.model.comparator.PhoneTypeComparator;
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
 * Gestiona la creación de la ventana de creación/edición de números de teléfono
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class PhoneDialog extends AbstractDialog<PhoneInfo> {
	/**
	 * Campo del número de teléfono
	 */
	private TextField numberTextField;
	/**
	 * Campo del tipo de teléfono
	 */
	private ComboBox<PhoneTypeEnum> typeComboBox;
	/**
	 * Campo de la etiqueta del teléfono
	 */
	private TextField tagTextField;

	/**
	 * Constructor por defecto de la ventana
	 */
	public PhoneDialog() {
		super(false);
	}

	/**
	 * Constructor con parámetros de la ventana
	 * 
	 * @param info
	 *            Información del teléfono
	 */
	public PhoneDialog(PhoneInfo info) {
		super(true);
		numberTextField.setText(getValue(info.getNumber()));
		typeComboBox.getSelectionModel().select(info.getType());
		tagTextField.setText(getValue(info.getTag()));
	}

	/**
	 * Construye el ComboBox de los tipos
	 * 
	 * @return ComboBox de los tipos
	 */
	private ComboBox<PhoneTypeEnum> buildTypeComboBox() {
		ObservableList<PhoneTypeEnum> observableList = FXCollections.observableArrayList();
		for (PhoneTypeEnum type : PhoneTypeEnum.values()) {
			observableList.add(type);
		}
		Collections.sort(observableList, new PhoneTypeComparator(getResourceBundle()));
		ComboBox<PhoneTypeEnum> result = new ComboBox<>(observableList);

		result.setButtonCell(createPhoneTypeComboBoxCellFactory().call(null));
		result.setCellFactory(createPhoneTypeComboBoxCellFactory());
		result.setValue(PhoneTypeEnum.getDefault());

		return result;
	}

	/**
	 * Crea la visualización para el combobox
	 * 
	 * @return Objeto que implementa la visualización del texto del combobox
	 */
	private Callback<ListView<PhoneTypeEnum>, ListCell<PhoneTypeEnum>> createPhoneTypeComboBoxCellFactory() {
		Callback<ListView<PhoneTypeEnum>, ListCell<PhoneTypeEnum>> cellFactory = new Callback<ListView<PhoneTypeEnum>, ListCell<PhoneTypeEnum>>() {
			@Override
			public ListCell<PhoneTypeEnum> call(ListView<PhoneTypeEnum> l) {
				return new ListCell<PhoneTypeEnum>() {
					@Override
					protected void updateItem(PhoneTypeEnum item, boolean empty) {
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
			return Text.I18N_CONTACT_PHONE_EDIT;
		} else {
			return Text.I18N_CONTACT_PHONE_ADD;
		}
	}

	@Override
	protected InputStream getGraphicStream() {
		return PhoneDialog.class.getResourceAsStream(ResourcePath.IMG_EDIT_CONTACT_PHONE_ADD);
	}

	@Override
	protected void buildGrid(GridPane grid) {
		numberTextField = new TextField();
		numberTextField.setPromptText(getResourceBundle().getString(Text.I18N_CONTACT_PHONE_NUMBER));
		typeComboBox = buildTypeComboBox();
		tagTextField = new TextField();
		tagTextField.setPromptText(getResourceBundle().getString(Text.I18N_CONTACT_PHONE_TAG));

		buildRowGridPane(grid, Text.I18N_CONTACT_PHONE_NUMBER, numberTextField, 0);
		buildRowGridPane(grid, Text.I18N_CONTACT_PHONE_TYPE, typeComboBox, 1);
		buildRowGridPane(grid, Text.I18N_CONTACT_PHONE_TAG, tagTextField, 2);
	}

	@Override
	protected void autoEnableSave() {
		Node saveButton = this.getDialogPane().lookupButton(getSaveButtonType());
		numberTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			saveButton.setDisable(newValue.trim().isEmpty());
		});
	}

	@Override
	protected void autoFocus() {
		Platform.runLater(() -> numberTextField.requestFocus());
	}

	@Override
	protected Callback<ButtonType, PhoneInfo> buildResultConverter() {
		Callback<ButtonType, PhoneInfo> result = new Callback<ButtonType, PhoneInfo>() {

			@Override
			public PhoneInfo call(ButtonType dialogButton) {
				if (dialogButton == getSaveButtonType()) {
					return new PhoneInfo(getTextFromTextField(numberTextField), typeComboBox.getSelectionModel().getSelectedItem(), getTextFromTextField(tagTextField));
				}
				return null;
			}
		};
		return result;
	}
}
