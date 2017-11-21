package com.danielcorroto.directorius.controller;

import com.danielcorroto.directorius.view.MainWindow;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Gestión la ventana principal
 * 
 * @author Daniel Corroto Quirós
 *
 */
@SuppressWarnings("restriction")
public class MainWindowController extends Application {
	/**
	 * Clase de la vista
	 */
	private MainWindow window;

	@Override
	public void start(Stage primaryStage) throws Exception {
		window = new MainWindow();
		window.build(primaryStage);

	}

	public static void main(String[] args) {
		launch(args);
	}
}
