package com.danielcorroto.directorius.view.info;

import java.io.InputStream;
import java.util.Collection;

import com.danielcorroto.directorius.view.ResourcePath;
import com.danielcorroto.directorius.view.Text;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

/**
 * Gestiona la creación de la ventana de creación/edición de categorías
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class CategoryDialogWindow extends AbstractDialogWindow<String> {
	/**
	 * Campo del email
	 */
	private ComboBox<String> categoryComboBox;

	/**
	 * Constructor por defecto de la ventana
	 */
	public CategoryDialogWindow(Collection<String> categories) {
		super(false);
		ObservableList<String> elementList = FXCollections.observableArrayList(categories);
		categoryComboBox.setItems(elementList);
	}

	/**
	 * Constructor con parámetros de la ventana
	 * 
	 * @param category
	 *            Información de la categoría
	 */
	public CategoryDialogWindow(String category, Collection<String> categories) {
		super(true);
		ObservableList<String> elementList = FXCollections.observableArrayList(categories);
		categoryComboBox.setItems(elementList);
		categoryComboBox.getSelectionModel().select(category);
	}

	@Override
	protected String getI18NTitle(boolean edit) {
		if (edit) {
			return Text.I18N_CONTACT_CATEGORY_EDIT;
		} else {
			return Text.I18N_CONTACT_CATEGORY_ADD;
		}
	}

	@Override
	protected InputStream getGraphicStream() {
		return EmailDialogWindow.class.getResourceAsStream(ResourcePath.IMG_EDIT_CONTACT_CATEGORY_ADD);
	}

	@Override
	protected void buildGrid(GridPane grid) {
		categoryComboBox = new ComboBox<>();
		categoryComboBox.setEditable(true);
		buildRowGridPane(grid, Text.I18N_CONTACT_CATEGORY_CATEGORY, categoryComboBox, 0);
	}

	@Override
	protected void autoEnableSave() {
		Node saveButton = this.getDialogPane().lookupButton(getSaveButtonType());
		categoryComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			saveButton.setDisable(newValue.trim().isEmpty());
		});
	}

	@Override
	protected void autoFocus() {
		Platform.runLater(() -> categoryComboBox.requestFocus());
	}

	@Override
	protected Callback<ButtonType, String> buildResultConverter() {
		Callback<ButtonType, String> result = new Callback<ButtonType, String>() {

			@Override
			public String call(ButtonType dialogButton) {
				if (dialogButton == getSaveButtonType()) {
					return new String(categoryComboBox.getSelectionModel().getSelectedItem().trim());
				}
				return null;
			}
		};
		return result;
	}

}
