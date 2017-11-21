package com.danielcorroto.directorius.controller;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Gestión la ventana principal
 * 
 * @author Daniel Corroto Quirós
 *
 */
@SuppressWarnings("restriction")
public class MainWindowController extends Application {
	public MainWindowController() {
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		StackPane root = new StackPane();
		Scene scene = new Scene(root, 300, 250);
		primaryStage.setScene(scene);
		primaryStage.setMaximized(true);
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}
}
