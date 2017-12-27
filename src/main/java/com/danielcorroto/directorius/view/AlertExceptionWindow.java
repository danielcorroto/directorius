package com.danielcorroto.directorius.view;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Alerta para mostrar la información de la excepción
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class AlertExceptionWindow extends Alert {
	/**
	 * Constructor por defecto
	 * 
	 * @param e
	 *            Excepción a mostrar
	 */
	public AlertExceptionWindow(Exception e) {
		super(AlertType.ERROR);
		build(e);
	}

	/**
	 * Construye la ventana
	 * 
	 * @param e
	 *            Excepción a mostrar
	 */
	private void build(Exception e) {
		ResourceBundle rb = ResourceBundle.getBundle(Text.RESOURCE_BUNDLE, Locale.getDefault());

		String exceptionString = rb.getString(Text.I18N_EXCEPTION_TITLE);
		setTitle(exceptionString);
		setHeaderText(exceptionString);

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String exceptionText = sw.toString();

		Label label = new Label(rb.getString(Text.I18N_EXCEPTION_STACKTRACE));

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);
		getDialogPane().setContent(expContent);

		double width = Screen.getPrimary().getBounds().getWidth();
		double height = Screen.getPrimary().getBounds().getHeight();
		getDialogPane().setMinWidth(width / 2);
		getDialogPane().setMinHeight(height - 300);
		setResizable(false);
		Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(MainWindow.class.getResourceAsStream(ResourcePath.IMG_LOGO)));
		initModality(Modality.APPLICATION_MODAL);
	}

}
