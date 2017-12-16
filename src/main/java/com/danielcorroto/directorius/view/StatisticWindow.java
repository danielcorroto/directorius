package com.danielcorroto.directorius.view;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import com.danielcorroto.directorius.controller.data.Statistics;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * Gestiona la creación de la ventana de mostrar estadísticas
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class StatisticWindow extends Dialog<Void> {
	/**
	 * Porcentajes para los elementos del panel de estadísticas
	 */
	private static final double[] STATISTIC_PERCENTAGE = new double[] { 0.7, 0.1, 0.1 };

	/**
	 * Para i18n
	 */
	private ResourceBundle rb;

	/**
	 * Constructor por defecto
	 * 
	 * @param stat
	 *            Datos estadísticos
	 */
	public StatisticWindow(Statistics stat) {
		super();
		rb = ResourceBundle.getBundle(Text.RESOURCE_BUNDLE, Locale.getDefault());

		// Título
		this.setTitle(rb.getString(Text.I18N_MENU_CONTACT_STATISTICS));

		// Tabla
		TableView<Statistic> table = buildTable();
		this.getDialogPane().setContent(table);

		// Rellenar la tabla
		ObservableList<Statistic> data = FXCollections.observableArrayList();
		data.add(new Statistic(rb.getString(Text.I18N_STATISTIC_ITEM_TOTAL), stat.getAllContacts(), stat.getAllContacts()));
		data.add(new Statistic(rb.getString(Text.I18N_STATISTIC_ITEM_BIRTHDAY), stat.getAllContactsBirthday(), stat.getAllContacts()));
		data.add(new Statistic(rb.getString(Text.I18N_STATISTIC_ITEM_PHOTO), stat.getAllContactsPhoto(), stat.getAllContacts()));
		data.add(new Statistic(rb.getString(Text.I18N_STATISTIC_ITEM_PHONE), stat.getAllContactsPhone(), stat.getAllContacts()));
		data.add(new Statistic(rb.getString(Text.I18N_STATISTIC_ITEM_EMAIL), stat.getAllContactsEmail(), stat.getAllContacts()));
		data.add(new Statistic(rb.getString(Text.I18N_STATISTIC_ITEM_ADDRESS), stat.getAllContactsAddress(), stat.getAllContacts()));
		String itemCategory = rb.getString(Text.I18N_STATISTIC_ITEM_CATEGORY);
		for (Entry<String, Integer> entry : stat.getAllContactsCategoryMap().entrySet()) {
			data.add(new Statistic(MessageFormat.format(itemCategory, entry.getKey()), entry.getValue(), stat.getAllContacts()));
		}
		table.setItems(data);

		// Botones de guardar/cancelar
		this.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);

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
	 * Cosntruye la tabla
	 * 
	 * @return Tabla para las estadísticas
	 */
	private TableView<Statistic> buildTable() {
		TableView<Statistic> table = new TableView<>();

		// Definición de columnas
		TableColumn<Statistic, String> textCol = new TableColumn<>(rb.getString(Text.I18N_STATISTIC_TITLE_TEXT));
		TableColumn<Statistic, Number> totalCol = new TableColumn<>(rb.getString(Text.I18N_STATISTIC_TITLE_VALUE));
		TableColumn<Statistic, Number> percentageCol = new TableColumn<>(rb.getString(Text.I18N_STATISTIC_TITLE_PERCENTAGE));

		// Propiedades de las columnas
		textCol.prefWidthProperty().bind(table.widthProperty().multiply(STATISTIC_PERCENTAGE[0]));
		totalCol.prefWidthProperty().bind(table.widthProperty().multiply(STATISTIC_PERCENTAGE[1]));
		percentageCol.prefWidthProperty().bind(table.widthProperty().multiply(STATISTIC_PERCENTAGE[2]));

		// Visualización de las filas
		textCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Statistic,String>, ObservableValue<String>>() {
			
			@Override
			public ObservableValue<String> call(CellDataFeatures<Statistic, String> param) {
				return new SimpleStringProperty(param.getValue().text);
			}
		});
		totalCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Statistic,Number>, ObservableValue<Number>>() {

			@Override
			public ObservableValue<Number> call(CellDataFeatures<Statistic, Number> param) {
				return new SimpleIntegerProperty(param.getValue().total);
			}
		});
		percentageCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Statistic,Number>, ObservableValue<Number>>() {

			@Override
			public ObservableValue<Number> call(CellDataFeatures<Statistic, Number> param) {
				return new SimpleIntegerProperty(100 * param.getValue().total / param.getValue().max);
			}
		});
		
		// Seteo de columnas en tabla
		List<TableColumn<Statistic, ?>> tableColumns = new ArrayList<>();
		tableColumns.add(textCol);
		tableColumns.add(totalCol);
		tableColumns.add(percentageCol);
		ObservableList<TableColumn<Statistic, ?>> titleList = FXCollections.observableArrayList(tableColumns);

		table.getColumns().setAll(titleList);

		return table;
	}

	/**
	 * Clase para indicar cada fila de la tabla
	 * 
	 * @author Daniel Corroto Quirós
	 *
	 */
	private class Statistic {
		/**
		 * Texto descriptivo
		 */
		private String text;
		/**
		 * Cantidad total
		 */
		private int total;
		/**
		 * Porcentaje sobre el total
		 */
		private int max;

		/**
		 * Constructor con parámetros
		 * 
		 * @param text
		 *            Texto descriptivo
		 * @param total
		 *            Cantidad total
		 * @param max
		 *            Máxima cantidad (para calcular porcentaje)
		 */
		public Statistic(String text, int total, int max) {
			super();
			this.text = text;
			this.total = total;
			this.max = max;
		}

	}
}
