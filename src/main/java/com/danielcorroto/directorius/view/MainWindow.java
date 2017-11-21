package com.danielcorroto.directorius.view;

import java.util.Locale;
import java.util.ResourceBundle;

import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCombination.Modifier;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Gestiona la creación de la ventana principal
 * 
 * @author Daniel Corroto Quirós
 *
 */
@SuppressWarnings("restriction")
public class MainWindow {
	/**
	 * Tamaño (ancho y alto) de la imagen en el menú
	 */
	private static final int MENU_IMAGE_SIZE = 20;
	/**
	 * Para i18n
	 */
	private ResourceBundle rb;

	/**
	 * Constructor por defecto. Inicia i18n
	 */
	public MainWindow() {
		super();
		rb = ResourceBundle.getBundle(Text.RESOURCE_BUNDLE, Locale.getDefault());
	}

	/**
	 * Construcción de la ventana principal
	 * 
	 * @param stage
	 *            Stage de la ventana
	 */
	public void build(Stage stage) {
		// Crear ventana principal
		VBox main = new VBox();
		main.getChildren().add(createMenuBar());

		// Crear scene y stage
		Scene scene = new Scene(main);
		stage.setScene(scene);

		// Setea propiedades
		stage.setMaximized(true);
		stage.setTitle(Text.APP_NAME);
		Image logo = new Image(MainWindow.class.getResourceAsStream(Path.RESOURCE_IMG + Path.IMG_LOGO));
		stage.getIcons().add(logo);
		stage.show();
	}

	/**
	 * Crea el objeto menu bar de la interfaz.
	 * 
	 * <pre>
	 * MenuBar
	 * |- Menu 1
	 * | |- MenuItem 1.A
	 * | |- MenuItem 1.B
	 * |- Menu 2
	 *   |- MenuItem 2.C
	 *   |- MenuItem 2.D
	 * </pre>
	 * 
	 * @return
	 */
	private MenuBar createMenuBar() {
		MenuBar menuBar = new MenuBar();

		// Crea menú File
		MenuItem menuFileNew = createMenuItem(Text.I18N_MENU_FILE_NEW, Path.IMG_MENU_FILE_NEW, KeyCode.N, KeyCombination.CONTROL_DOWN);
		MenuItem menuFileOpen = createMenuItem(Text.I18N_MENU_FILE_OPEN, Path.IMG_MENU_FILE_OPEN, KeyCode.O, KeyCombination.CONTROL_DOWN);
		MenuItem menuFileExit = createMenuItem(Text.I18N_MENU_FILE_EXIT, Path.IMG_MENU_FILE_EXIT, null);
		Menu menuFile = new Menu(rb.getString(Text.I18N_MENU_FILE));
		menuFile.getItems().addAll(menuFileNew, menuFileOpen, new SeparatorMenuItem(), menuFileExit);
		menuBar.getMenus().addAll(menuFile);

		// Crea menú Contact
		MenuItem menuContactAdd = createMenuItem(Text.I18N_MENU_CONTACT_ADD, Path.IMG_MENU_CONTACT_ADD, KeyCode.A, KeyCombination.CONTROL_DOWN);
		MenuItem menuContactEdit = createMenuItem(Text.I18N_MENU_CONTACT_EDIT, Path.IMG_MENU_CONTACT_EDIT, KeyCode.E, KeyCombination.CONTROL_DOWN);
		MenuItem menuContactRemove = createMenuItem(Text.I18N_MENU_CONTACT_REMOVE, Path.IMG_MENU_CONTACT_REMOVE, KeyCode.R, KeyCombination.CONTROL_DOWN);
		Menu menuContact = new Menu(rb.getString(Text.I18N_MENU_CONTACT));
		menuContact.getItems().addAll(menuContactAdd, menuContactEdit, menuContactRemove);
		menuBar.getMenus().addAll(menuContact);

		// Crea menú Birthday
		MenuItem menuBirthdayToday = createMenuItem(Text.I18N_MENU_BIRTHDAY_TODAY, Path.IMG_MENU_BIRTHDAY_TODAY, null);
		MenuItem menuBirthdayWithinWeek = createMenuItem(Text.I18N_MENU_BIRTHDAY_WITHINWEEK, Path.IMG_MENU_BIRTHDAY_WITHINWEEK, KeyCode.B, KeyCombination.CONTROL_DOWN);
		MenuItem menuBirthdayWithinMonth = createMenuItem(Text.I18N_MENU_BIRTHDAY_WITHINMONTH, Path.IMG_MENU_BIRTHDAY_WITHINMONTH, null);
		MenuItem menuBirthdayThisWeek = createMenuItem(Text.I18N_MENU_BIRTHDAY_THISWEEK, Path.IMG_MENU_BIRTHDAY_THISWEEK, null);
		MenuItem menuBirthdayThisMonth = createMenuItem(Text.I18N_MENU_BIRTHDAY_THISMONTH, Path.IMG_MENU_BIRTHDAY_THISMONTH, null);
		Menu menuBirthday = new Menu(rb.getString(Text.I18N_MENU_BIRTHDAY));
		menuBirthday.getItems().addAll(menuBirthdayToday, new SeparatorMenuItem(), menuBirthdayWithinWeek, menuBirthdayWithinMonth, new SeparatorMenuItem(), menuBirthdayThisWeek,
				menuBirthdayThisMonth);
		menuBar.getMenus().addAll(menuBirthday);

		// Crea menú Help
		MenuItem menuHelpAbout = createMenuItem(Text.I18N_MENU_HELP_ABOUT, Path.IMG_MENU_HELP_ABOUT, KeyCode.F1);
		Menu menuHelp = new Menu(rb.getString(Text.I18N_MENU_HELP));
		menuHelp.getItems().addAll(menuHelpAbout);
		menuBar.getMenus().addAll(menuHelp);

		return menuBar;
	}

	/**
	 * Crea un nuevo objeto MenuItem para agregar a Menu
	 * 
	 * @param i18n
	 *            Cadena para aplicar el ResourceBundle
	 * @param pathImage
	 *            Ruta de la imagen dentro de Path.RESOURCE_IMG
	 * @param keyCode
	 *            Tecla para el acceso directo. Por ejemplo KeyCode.E
	 * @param modifiers
	 *            Modificadores para el acceso directo. Por ejemplo
	 *            KeyCombination.CONTROL_DOWN
	 * @return Objeto MenuItem
	 */
	private MenuItem createMenuItem(String i18n, String pathImage, KeyCode keyCode, Modifier... modifiers) {
		MenuItem item = new MenuItem(rb.getString(i18n));

		if (pathImage != null) {
			ImageView iview = new ImageView(new Image(MainWindow.class.getResourceAsStream(Path.RESOURCE_IMG + pathImage)));
			iview.setFitWidth(MENU_IMAGE_SIZE);
			iview.setFitHeight(MENU_IMAGE_SIZE);
			item.setGraphic(iview);
		}
		if (keyCode != null && modifiers != null) {
			item.setAccelerator(new KeyCodeCombination(keyCode, modifiers));
		}

		return item;
	}
}
