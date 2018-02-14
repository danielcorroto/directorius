package com.danielcorroto.directorius.view;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.danielcorroto.directorius.controller.DisplayUtil;

import ezvcard.VCard;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * Gestiona la creación de la ventana de mostrar cumpleaños
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class BirthdayDialog extends Dialog<VCard> {

	/**
	 * Para i18n
	 */
	private ResourceBundle rb;

	/**
	 * Constructor por defecto
	 * 
	 * @param stat
	 *            Datos estadísticos
	 * @param i18nTitle
	 *            Titulo en i18n
	 */
	public BirthdayDialog(List<VCard> cards, String i18nTitle) {
		super();
		rb = ResourceBundle.getBundle(Text.RESOURCE_BUNDLE, Locale.getDefault());

		// Título
		this.setTitle(rb.getString(i18nTitle));

		// Lista
		ListView<VCard> listView = new ListView<>();
		listView.setCellFactory(createContactListViewCellFactory());
		this.getDialogPane().setContent(listView);

		// Rellenar la lista
		ObservableList<VCard> data = FXCollections.observableArrayList(cards);
		listView.setItems(data);

		// Botones de mostrar/cerrar
		ButtonType showButtonType = new ButtonType(rb.getString(Text.I18N_BIRTHDAY_SHOW), ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE, showButtonType);

		// Habilitar/Deshabilitar botón de mostrar
		Node saveButton = this.getDialogPane().lookupButton(showButtonType);
		saveButton.setDisable(true);

		// Habilitar botón mostrar
		listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<VCard>() {

			@Override
			public void changed(ObservableValue<? extends VCard> observable, VCard oldValue, VCard newValue) {
				saveButton.setDisable(false);
			}
		});

		// Doble click cierra
		listView.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
					setResultConverter(new Callback<ButtonType, VCard>() {

						@Override
						public VCard call(ButtonType param) {
							return listView.getSelectionModel().getSelectedItem();
						}
					});
					close();
				}

			}
		});

		// Convierte la selección en el objeto correspondiente
		this.setResultConverter(new Callback<ButtonType, VCard>() {

			@Override
			public VCard call(ButtonType param) {
				if (param == showButtonType) {
					return listView.getSelectionModel().getSelectedItem();
				}
				return null;
			}
		});

		// Setea propiedades
		Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
		double width = Screen.getPrimary().getBounds().getWidth();
		double height = Screen.getPrimary().getBounds().getHeight();
		this.getDialogPane().setMinWidth(width / 2);
		this.getDialogPane().setMinHeight(height - 100);
		Image logo = new Image(MainWindow.class.getResourceAsStream(ResourcePath.IMG_LOGO));
		stage.getIcons().add(logo);
	}

	/**
	 * Crea la visualización para el listview
	 * 
	 * @return Objeto que implementa la visualización del texto del listview
	 */
	private Callback<ListView<VCard>, ListCell<VCard>> createContactListViewCellFactory() {
		Callback<ListView<VCard>, ListCell<VCard>> cellFactory = new Callback<ListView<VCard>, ListCell<VCard>>() {

			@Override
			public ListCell<VCard> call(ListView<VCard> param) {
				return new ListCell<VCard>() {
					@Override
					protected void updateItem(VCard item, boolean empty) {
						super.updateItem(item, empty);

						if (empty || item == null || item.getFormattedName() == null) {
							setText(null);
						} else {
							setText(item.getFormattedName().getValue() + System.getProperty("line.separator") + DisplayUtil.buildBirthday(item, true, rb));
						}
					}
				};
			}
		};

		return cellFactory;
	}

}
