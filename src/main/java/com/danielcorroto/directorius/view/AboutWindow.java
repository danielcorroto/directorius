package com.danielcorroto.directorius.view;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Vista del menú Acerca de...
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class AboutWindow extends Application {
	/**
	 * Fichero de propiedades generadas a partir del POM
	 */
	private static final String PROPERTIES_FILE = "/app.properties";
	/**
	 * Propiedad de la versión del proyecto
	 */
	private static final String PROPERTY_VERSION = "project.version";
	/**
	 * Propiedad de la fecha de compilación del proyecto
	 */
	private static final String PROPERTY_TIMESTAMP = "project.timestamp";
	/**
	 * Formato de entrada de la fecha de compilación del proyecto
	 */
	private static final String TIMESTAMP_INPUT_FORMAT = "yyyy-MM-dd'T'HH':'mm':'ss'Z'";
	/**
	 * Formato de salida de la fecha de compilación del proyecto
	 */
	private static final String TIMESTAMP_OUTPUT_FORMAT = "yyyy/MM/dd";

	@Override
	public void start(Stage primaryStage) throws Exception {
		ResourceBundle rb = ResourceBundle.getBundle(Text.RESOURCE_BUNDLE, Locale.getDefault());

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setHeaderText(null);
		alert.setContentText(buildContentText());

		// Gestión de Alert a través de DialogPane
		DialogPane pane = alert.getDialogPane();

		ObjectProperty<ButtonType> result = new SimpleObjectProperty<>();
		for (ButtonType type : pane.getButtonTypes()) {
			ButtonType resultValue = type;
			((Button) pane.lookupButton(type)).setOnAction(e -> {
				result.set(resultValue);
				pane.getScene().getWindow().hide();
			});
		}

		pane.getScene().setRoot(new Label());
		Scene scene = new Scene(pane);

		Stage dialog = new Stage();
		dialog.setScene(scene);
		dialog.setTitle(rb.getString(Text.I18N_MENU_HELP_ABOUT));
		Image logo = new Image(MainWindow.class.getResourceAsStream(ResourcePath.IMG_LOGO));
		dialog.getIcons().add(logo);

		scene.setOnKeyPressed(event -> {
			if (KeyCode.ESCAPE.equals(event.getCode())) {
				dialog.close();
			}
		});

		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.showAndWait();
	}

	/**
	 * Devuelve el texto que se muestra en la ventana
	 * 
	 * @return Texto a mostrar
	 * @throws IOException
	 * @throws ParseException
	 */
	private String buildContentText() throws IOException, ParseException {
		// Versión
		Properties properties = new Properties();
		properties.load(MainWindow.class.getResourceAsStream(PROPERTIES_FILE));
		String version = properties.getProperty(PROPERTY_VERSION);

		// Fecha
		String stringDateIn = properties.getProperty(PROPERTY_TIMESTAMP);
		SimpleDateFormat sdfIn = new SimpleDateFormat(TIMESTAMP_INPUT_FORMAT);
		Date date = sdfIn.parse(stringDateIn);
		SimpleDateFormat sdfOut = new SimpleDateFormat(TIMESTAMP_OUTPUT_FORMAT);
		String stringDateOut = sdfOut.format(date);

		return Text.APP_NAME + " " + version + System.getProperty("line.separator") + stringDateOut;
	}

}
