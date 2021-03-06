package com.danielcorroto.directorius.view.info;

import java.io.InputStream;
import java.util.Collections;

import com.danielcorroto.directorius.controller.data.AddressInfo;
import com.danielcorroto.directorius.controller.type.AddressTypeEnum;
import com.danielcorroto.directorius.model.Utils;
import com.danielcorroto.directorius.model.comparator.AddressTypeComparator;
import com.danielcorroto.directorius.view.ResourcePath;
import com.danielcorroto.directorius.view.Text;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
 * Gestiona la creación de la ventana de creación/edición de direcciones
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class AddressDialog extends AbstractDialog<AddressInfo> {
	/**
	 * Campo de la calle
	 */
	private TextField streetTextField;
	/**
	 * Campo de la localidad
	 */
	private TextField localityTextField;
	/**
	 * Campo de la región
	 */
	private TextField regionTextField;
	/**
	 * Campo del código postal
	 */
	private TextField postalCodeTextField;
	/**
	 * Campo del país
	 */
	private TextField countryTextField;
	/**
	 * Campo del tipo de dirección
	 */
	private ComboBox<AddressTypeEnum> typeComboBox;
	/**
	 * Campo de la etiqueta de la dirección
	 */
	private TextField tagTextField;

	/**
	 * Constructor por defecto de la ventana
	 */
	public AddressDialog() {
		super(false);
	}

	/**
	 * Constructor con parámetros de la ventana
	 * 
	 * @param info
	 *            Información de la dirección
	 */
	public AddressDialog(AddressInfo info) {
		super(true);
		streetTextField.setText(getValue(info.getStreet()));
		localityTextField.setText(getValue(info.getLocality()));
		regionTextField.setText(getValue(info.getRegion()));
		postalCodeTextField.setText(getValue(info.getPostalCode()));
		countryTextField.setText(getValue(info.getCountry()));
		typeComboBox.getSelectionModel().select(info.getType());
		tagTextField.setText(getValue(info.getTag()));
	}

	/**
	 * Construye el ComboBox de los tipos
	 * 
	 * @return ComboBox de los tipos
	 */
	private ComboBox<AddressTypeEnum> buildTypeComboBox() {
		ObservableList<AddressTypeEnum> observableList = FXCollections.observableArrayList();
		for (AddressTypeEnum type : AddressTypeEnum.values()) {
			observableList.add(type);
		}
		Collections.sort(observableList, new AddressTypeComparator(getResourceBundle()));
		ComboBox<AddressTypeEnum> result = new ComboBox<>(observableList);

		result.setButtonCell(createAddressTypeComboBoxCellFactory().call(null));
		result.setCellFactory(createAddressTypeComboBoxCellFactory());
		result.setValue(AddressTypeEnum.getDefault());

		return result;
	}

	/**
	 * Crea la visualización para el combobox
	 * 
	 * @return Objeto que implementa la visualización del texto del combobox
	 */
	private Callback<ListView<AddressTypeEnum>, ListCell<AddressTypeEnum>> createAddressTypeComboBoxCellFactory() {
		Callback<ListView<AddressTypeEnum>, ListCell<AddressTypeEnum>> cellFactory = new Callback<ListView<AddressTypeEnum>, ListCell<AddressTypeEnum>>() {
			@Override
			public ListCell<AddressTypeEnum> call(ListView<AddressTypeEnum> l) {
				return new ListCell<AddressTypeEnum>() {
					@Override
					protected void updateItem(AddressTypeEnum item, boolean empty) {
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
			return Text.I18N_CONTACT_ADDRESS_EDIT;
		} else {
			return Text.I18N_CONTACT_ADDRESS_ADD;
		}
	}

	@Override
	protected InputStream getGraphicStream() {
		return AddressDialog.class.getResourceAsStream(ResourcePath.IMG_EDIT_CONTACT_ADDRESS_ADD);
	}

	@Override
	protected void buildGrid(GridPane grid) {
		streetTextField = new TextField();
		streetTextField.setPromptText(getResourceBundle().getString(Text.I18N_CONTACT_ADDRESS_STREET));
		localityTextField = new TextField();
		localityTextField.setPromptText(getResourceBundle().getString(Text.I18N_CONTACT_ADDRESS_LOCALITY));
		regionTextField = new TextField();
		regionTextField.setPromptText(getResourceBundle().getString(Text.I18N_CONTACT_ADDRESS_REGION));
		postalCodeTextField = new TextField();
		postalCodeTextField.setPromptText(getResourceBundle().getString(Text.I18N_CONTACT_ADDRESS_POSTALCODE));
		countryTextField = new TextField();
		countryTextField.setPromptText(getResourceBundle().getString(Text.I18N_CONTACT_ADDRESS_COUNTRY));
		typeComboBox = buildTypeComboBox();
		tagTextField = new TextField();
		tagTextField.setPromptText(getResourceBundle().getString(Text.I18N_CONTACT_PHONE_TAG));

		buildRowGridPane(grid, Text.I18N_CONTACT_ADDRESS_STREET, streetTextField, 0);
		buildRowGridPane(grid, Text.I18N_CONTACT_ADDRESS_LOCALITY, localityTextField, 1);
		buildRowGridPane(grid, Text.I18N_CONTACT_ADDRESS_REGION, regionTextField, 2);
		buildRowGridPane(grid, Text.I18N_CONTACT_ADDRESS_POSTALCODE, postalCodeTextField, 3);
		buildRowGridPane(grid, Text.I18N_CONTACT_ADDRESS_COUNTRY, countryTextField, 4);
		buildRowGridPane(grid, Text.I18N_CONTACT_ADDRESS_TYPE, typeComboBox, 5);
		buildRowGridPane(grid, Text.I18N_CONTACT_ADDRESS_TAG, tagTextField, 6);
	}

	@Override
	protected void autoEnableSave() {
		streetTextField.textProperty().addListener(buildTextChangeListener());
		localityTextField.textProperty().addListener(buildTextChangeListener());
		regionTextField.textProperty().addListener(buildTextChangeListener());
		postalCodeTextField.textProperty().addListener(buildTextChangeListener());
		countryTextField.textProperty().addListener(buildTextChangeListener());
	}

	/**
	 * Construye el listener del texto para habilitar el botón de guardar cuando
	 * alguno de los campos de la dirección no está vacío o deshabilitarlo en
	 * caso contrario
	 * 
	 * @return Listener que habilita/deshabilita el botón de guardar
	 */
	private ChangeListener<String> buildTextChangeListener() {
		Node saveButton = this.getDialogPane().lookupButton(getSaveButtonType());

		return new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!Utils.isBlank(streetTextField.getText())) {
					saveButton.setDisable(false);
					return;
				}
				if (!Utils.isBlank(localityTextField.getText())) {
					saveButton.setDisable(false);
					return;
				}
				if (!Utils.isBlank(regionTextField.getText())) {
					saveButton.setDisable(false);
					return;
				}
				if (!Utils.isBlank(postalCodeTextField.getText())) {
					saveButton.setDisable(false);
					return;
				}
				if (!Utils.isBlank(countryTextField.getText())) {
					saveButton.setDisable(false);
					return;
				}

				saveButton.setDisable(true);
			}
		};
	}

	@Override
	protected void autoFocus() {
		Platform.runLater(() -> streetTextField.requestFocus());
	}

	@Override
	protected Callback<ButtonType, AddressInfo> buildResultConverter() {
		Callback<ButtonType, AddressInfo> result = new Callback<ButtonType, AddressInfo>() {

			@Override
			public AddressInfo call(ButtonType dialogButton) {
				if (dialogButton == getSaveButtonType()) {
					return new AddressInfo(getTextFromTextField(streetTextField), getTextFromTextField(localityTextField), getTextFromTextField(regionTextField),
							getTextFromTextField(postalCodeTextField), getTextFromTextField(countryTextField), typeComboBox.getSelectionModel().getSelectedItem(),
							getTextFromTextField(tagTextField));
				}
				return null;
			}
		};
		return result;
	}

}
