package com.danielcorroto.directorius.view;

import java.util.Locale;
import java.util.ResourceBundle;

import com.danielcorroto.directorius.model.SimpleVCard;
import com.danielcorroto.directorius.model.type.SearchTypeEnum;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCombination.Modifier;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Callback;

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
	 * Tamaño (ancho y alto) de la imagen en la toolbar
	 */
	private static final int TOOLBAR_IMAGE_SIZE = 32;
	/**
	 * Porcentajes para los elementos de la toolbar
	 */
	private static final int[] TOOLBAR_PERCENTAGES = new int[] { 70, 20, 10 };
	/**
	 * Margen para los elementos de la toolbar
	 */
	private static final Insets TOOLBAR_MARGIN = new Insets(0, 20, 0, 20);
	/**
	 * Porcentajes para los elementos del panel principal
	 */
	private static final int[] MAINPANE_PERCENTAGE = new int[] { 30, 70 };
	/**
	 * Ancho mínimo para los elementos el panel principal
	 */
	private static final Integer[] MAINPANE_MINWIDTH = new Integer[] { 350, null };

	/**
	 * Vista de la lista de los contactos
	 */
	private ListView<SimpleVCard> listView;
	/**
	 * Botón añadir contacto
	 */
	private Button addContactButton;
	/**
	 * Caja de texto para realizar búsqueda de contactos
	 */
	private TextField searchTextField;
	/**
	 * Combo para el tipo de búsqueda
	 */
	private ComboBox<SearchTypeEnum> searchTypeComboBox;
	/**
	 * Vista de la información del contacto
	 */
	private WebView webView;
	/**
	 * Lista de todos los items del menú
	 */
	private MenuItems menuItems;

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
		// Crear borderpane
		GridPane gridPane = new GridPane();
		ColumnConstraints[] columnConstraints = new ColumnConstraints[MAINPANE_PERCENTAGE.length];
		for (int i = 0; i < MAINPANE_PERCENTAGE.length; i++) {
			columnConstraints[i] = new ColumnConstraints();
			columnConstraints[i].setPercentWidth(MAINPANE_PERCENTAGE[i]);
			if (MAINPANE_MINWIDTH[i] != null) {
				columnConstraints[i].setMinWidth(MAINPANE_MINWIDTH[i]);
			}
		}
		gridPane.getColumnConstraints().addAll(columnConstraints);

		listView = createListView();
		gridPane.add(listView, 0, 0);
		GridPane.setVgrow(listView, Priority.ALWAYS);
		WebView webView = createWebView();
		gridPane.add(webView, 1, 0);
		GridPane.setVgrow(webView, Priority.ALWAYS);
		VBox.setVgrow(gridPane, Priority.ALWAYS);

		// Crear ventana principal
		VBox main = new VBox();
		main.getChildren().add(createMenuBar());
		main.getChildren().add(createToolbar());
		main.getChildren().add(gridPane);

		// Crear scene y stage
		Scene scene = new Scene(main);
		stage.setScene(scene);

		// Setea propiedades
		stage.setMaximized(true);
		stage.setTitle(Text.APP_NAME);
		Image logo = new Image(MainWindow.class.getResourceAsStream(ResourcePath.IMG_LOGO));
		stage.getIcons().add(logo);
		stage.show();
	}

	/**
	 * Obtiene el componente ListView para listar contactos
	 * 
	 * @return Componente de listar contactos
	 */
	public ListView<SimpleVCard> getListView() {
		return listView;
	}

	/**
	 * Obtiene el componente Button para añadir un contacto
	 * 
	 * @return Componente para añadir contacto
	 */
	public Button getAddContactButton() {
		return addContactButton;
	}

	/**
	 * Obtiene el componente TextField para realizar búsquedas
	 * 
	 * @return Componente para realizar búsquedas
	 */
	public TextField getSearchTextField() {
		return searchTextField;
	}

	/**
	 * Obtiene el componente ComboBox para el tipo de búsqueda
	 * 
	 * @return Componente para el tipo de búsqueda
	 */
	public ComboBox<SearchTypeEnum> getSearchTypeComboBox() {
		return searchTypeComboBox;
	}

	/**
	 * Obtiene el componente WebView para mostrar la información del contacto
	 * 
	 * @return Componente para mostrar información del contacto
	 */
	public WebView getWebView() {
		return webView;
	}

	/**
	 * Obtiene los componentes MenuItem
	 * 
	 * @return Componente que contiene todos los items del menú
	 */
	public MenuItems getMenuItems() {
		return menuItems;
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
	 * @return Objeto MenuBar
	 */
	private MenuBar createMenuBar() {
		MenuBar menuBar = new MenuBar();
		menuItems = new MenuItems();

		// Crea menú File
		MenuItem menuFileNew = createMenuItem(Text.I18N_MENU_FILE_NEW, ResourcePath.IMG_MENU_FILE_NEW, KeyCode.N, KeyCombination.CONTROL_DOWN);
		MenuItem menuFileOpen = createMenuItem(Text.I18N_MENU_FILE_OPEN, ResourcePath.IMG_MENU_FILE_OPEN, KeyCode.O, KeyCombination.CONTROL_DOWN);
		MenuItem menuFileExit = createMenuItem(Text.I18N_MENU_FILE_EXIT, ResourcePath.IMG_MENU_FILE_EXIT, null);
		Menu menuFile = new Menu(rb.getString(Text.I18N_MENU_FILE));
		menuFile.getItems().addAll(menuFileNew, menuFileOpen, new SeparatorMenuItem(), menuFileExit);
		menuBar.getMenus().addAll(menuFile);
		menuItems.setFileNew(menuFileNew);
		menuItems.setFileOpen(menuFileOpen);
		menuItems.setFileExit(menuFileExit);

		// Crea menú Contact
		MenuItem menuContactAdd = createMenuItem(Text.I18N_MENU_CONTACT_ADD, ResourcePath.IMG_MENU_CONTACT_ADD, KeyCode.A, KeyCombination.CONTROL_DOWN);
		MenuItem menuContactEdit = createMenuItem(Text.I18N_MENU_CONTACT_EDIT, ResourcePath.IMG_MENU_CONTACT_EDIT, KeyCode.E, KeyCombination.CONTROL_DOWN);
		MenuItem menuContactRemove = createMenuItem(Text.I18N_MENU_CONTACT_REMOVE, ResourcePath.IMG_MENU_CONTACT_REMOVE, KeyCode.R, KeyCombination.CONTROL_DOWN);
		Menu menuContact = new Menu(rb.getString(Text.I18N_MENU_CONTACT));
		menuContact.getItems().addAll(menuContactAdd, menuContactEdit, menuContactRemove);
		menuBar.getMenus().addAll(menuContact);
		menuItems.setContactAdd(menuContactAdd);
		menuItems.setContactEdit(menuContactEdit);
		menuItems.setContactRemove(menuContactRemove);

		// Crea menú Birthday
		MenuItem menuBirthdayToday = createMenuItem(Text.I18N_MENU_BIRTHDAY_TODAY, ResourcePath.IMG_MENU_BIRTHDAY_TODAY, null);
		MenuItem menuBirthdayWithinWeek = createMenuItem(Text.I18N_MENU_BIRTHDAY_WITHINWEEK, ResourcePath.IMG_MENU_BIRTHDAY_WITHINWEEK, KeyCode.B, KeyCombination.CONTROL_DOWN);
		MenuItem menuBirthdayWithinMonth = createMenuItem(Text.I18N_MENU_BIRTHDAY_WITHINMONTH, ResourcePath.IMG_MENU_BIRTHDAY_WITHINMONTH, null);
		MenuItem menuBirthdayThisWeek = createMenuItem(Text.I18N_MENU_BIRTHDAY_THISWEEK, ResourcePath.IMG_MENU_BIRTHDAY_THISWEEK, null);
		MenuItem menuBirthdayThisMonth = createMenuItem(Text.I18N_MENU_BIRTHDAY_THISMONTH, ResourcePath.IMG_MENU_BIRTHDAY_THISMONTH, null);
		Menu menuBirthday = new Menu(rb.getString(Text.I18N_MENU_BIRTHDAY));
		menuBirthday.getItems().addAll(menuBirthdayToday, new SeparatorMenuItem(), menuBirthdayWithinWeek, menuBirthdayWithinMonth, new SeparatorMenuItem(), menuBirthdayThisWeek,
				menuBirthdayThisMonth);
		menuBar.getMenus().addAll(menuBirthday);
		menuItems.setBirthdayToday(menuBirthdayToday);
		menuItems.setBirthdayWithinWeek(menuBirthdayWithinWeek);
		menuItems.setBirthdayWithinMonth(menuBirthdayWithinMonth);
		menuItems.setBirthdayThisWeek(menuBirthdayThisWeek);
		menuItems.setBirthdayThisMonth(menuBirthdayThisMonth);

		// Crea menú Help
		MenuItem menuHelpAbout = createMenuItem(Text.I18N_MENU_HELP_ABOUT, ResourcePath.IMG_MENU_HELP_ABOUT, KeyCode.F1);
		Menu menuHelp = new Menu(rb.getString(Text.I18N_MENU_HELP));
		menuHelp.getItems().addAll(menuHelpAbout);
		menuBar.getMenus().addAll(menuHelp);
		menuItems.setHelpAbout(menuHelpAbout);
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
			ImageView iview = new ImageView(new Image(MainWindow.class.getResourceAsStream(pathImage)));
			iview.setFitWidth(MENU_IMAGE_SIZE);
			iview.setFitHeight(MENU_IMAGE_SIZE);
			item.setGraphic(iview);
		}
		if (keyCode != null && modifiers != null) {
			item.setAccelerator(new KeyCodeCombination(keyCode, modifiers));
		}

		return item;
	}

	/**
	 * Crea el objeto toolbar de la interfaz
	 * 
	 * @return Objeto ToolBar
	 */
	private ToolBar createToolbar() {
		// Grid
		GridPane gridPane = new GridPane();
		ColumnConstraints[] columnConstraints = new ColumnConstraints[TOOLBAR_PERCENTAGES.length];
		for (int i = 0; i < TOOLBAR_PERCENTAGES.length; i++) {
			columnConstraints[i] = new ColumnConstraints();
			columnConstraints[i].setPercentWidth(TOOLBAR_PERCENTAGES[i]);
		}
		gridPane.getColumnConstraints().addAll(columnConstraints);
		HBox.setHgrow(gridPane, Priority.ALWAYS);
		ToolBar toolbar = new ToolBar(gridPane);

		// Buscador
		searchTextField = new TextField();
		searchTextField.setPromptText(rb.getString(Text.I18N_TOOLBAR_SEARCH));
		searchTextField.setTooltip(new Tooltip(rb.getString(Text.I18N_TOOLBAR_SEARCH)));
		gridPane.add(searchTextField, 0, 0);
		GridPane.setMargin(searchTextField, TOOLBAR_MARGIN);
		GridPane.setHalignment(searchTextField, HPos.CENTER);
		HBox.setHgrow(searchTextField, Priority.ALWAYS);

		// Combo tipo de búsqueda
		ObservableList<SearchTypeEnum> searchTypeOptions = FXCollections.observableArrayList();
		for (SearchTypeEnum searchType : SearchTypeEnum.values()) {
			searchTypeOptions.add(searchType);
		}
		searchTypeComboBox = new ComboBox<>(searchTypeOptions);
		searchTypeComboBox.setButtonCell(createSearchTypeComboBoxCellFactory().call(null));
		searchTypeComboBox.setCellFactory(createSearchTypeComboBoxCellFactory());
		searchTypeComboBox.setValue(SearchTypeEnum.getDefault());
		searchTypeComboBox.setTooltip(new Tooltip(rb.getString(Text.I18N_TOOLBAR_SEARCHTYPE_TOOLTIP)));
		gridPane.add(searchTypeComboBox, 1, 0);
		GridPane.setMargin(searchTypeComboBox, TOOLBAR_MARGIN);
		GridPane.setHalignment(searchTypeComboBox, HPos.CENTER);
		HBox.setHgrow(searchTypeComboBox, Priority.ALWAYS);

		// Botón añadir contacto
		addContactButton = new Button();
		ImageView addButtonImage = new ImageView(new Image(MainWindow.class.getResourceAsStream(ResourcePath.IMG_MENU_CONTACT_ADD)));
		addButtonImage.setFitWidth(TOOLBAR_IMAGE_SIZE);
		addButtonImage.setFitHeight(TOOLBAR_IMAGE_SIZE);
		addContactButton.setGraphic(addButtonImage);
		addContactButton.setTooltip(new Tooltip(rb.getString(Text.I18N_TOOLBAR_CONTACTADD_TOOLTIP)));
		gridPane.add(addContactButton, 2, 0);
		GridPane.setMargin(addContactButton, TOOLBAR_MARGIN);
		GridPane.setHalignment(addContactButton, HPos.RIGHT);
		HBox.setHgrow(addContactButton, Priority.ALWAYS);

		return toolbar;
	}

	/**
	 * Crea la visualización para el combobox
	 * 
	 * @return Objeto que implementa la visualización del texto del combobox
	 */
	private Callback<ListView<SearchTypeEnum>, ListCell<SearchTypeEnum>> createSearchTypeComboBoxCellFactory() {
		Callback<ListView<SearchTypeEnum>, ListCell<SearchTypeEnum>> cellFactory = new Callback<ListView<SearchTypeEnum>, ListCell<SearchTypeEnum>>() {
			@Override
			public ListCell<SearchTypeEnum> call(ListView<SearchTypeEnum> l) {
				return new ListCell<SearchTypeEnum>() {
					@Override
					protected void updateItem(SearchTypeEnum item, boolean empty) {
						super.updateItem(item, empty);
						if (item == null || empty) {
							setGraphic(null);
						} else {
							setText(rb.getString(item.getI18n()));
						}
					}
				};
			}
		};

		return cellFactory;
	}

	/**
	 * Crea el objeto listview de la interfaz
	 * 
	 * @return Objeto ListView
	 */
	private ListView<SimpleVCard> createListView() {
		ListView<SimpleVCard> listView = new ListView<>();
		listView.setCellFactory(createContactListViewCellFactory());

		return listView;
	}

	/**
	 * Crea la visualización para el listview
	 * 
	 * @return Objeto que implementa la visualización del texto del listview
	 */
	private Callback<ListView<SimpleVCard>, ListCell<SimpleVCard>> createContactListViewCellFactory() {
		Callback<ListView<SimpleVCard>, ListCell<SimpleVCard>> cellFactory = new Callback<ListView<SimpleVCard>, ListCell<SimpleVCard>>() {

			@Override
			public ListCell<SimpleVCard> call(ListView<SimpleVCard> param) {
				return new ListCell<SimpleVCard>() {
					@Override
					protected void updateItem(SimpleVCard item, boolean empty) {
						super.updateItem(item, empty);

						if (empty || item == null || item.getFormattedName() == null) {
							setText(null);
						} else {
							setText(item.getFormattedName().getValue());
						}
					}
				};
			}
		};

		return cellFactory;
	}

	/**
	 * Crea la visualización para el webview
	 * 
	 * @return Objeto que implementa la visualización del listview
	 */
	private WebView createWebView() {
		webView = new WebView();
		return webView;
	}
}
