package com.danielcorroto.directorius.view;

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

	@Override
	public void start(Stage primaryStage) throws Exception {
		Properties properties = new Properties();
		properties.load(MainWindow.class.getResourceAsStream(PROPERTIES_FILE));
		String version = properties.getProperty(PROPERTY_VERSION);
		ResourceBundle rb = ResourceBundle.getBundle(Text.RESOURCE_BUNDLE, Locale.getDefault());
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setHeaderText(null);
		alert.setContentText(Text.APP_NAME + " " + version);

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

		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.showAndWait();
	}

}
