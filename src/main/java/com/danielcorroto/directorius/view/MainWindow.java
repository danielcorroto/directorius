package com.danielcorroto.directorius.view;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.SortedSet;

import com.danielcorroto.directorius.controller.HtmlContactBuilder;
import com.danielcorroto.directorius.model.SearchFilter;
import com.danielcorroto.directorius.model.SimpleVCard;
import com.danielcorroto.directorius.model.Utils;
import com.danielcorroto.directorius.model.type.SearchTypeEnum;

import ezvcard.VCard;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
	private static final int[] TOOLBAR_PERCENTAGES = new int[] { 20, 55, 15, 10 };
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
	 * Toolbar
	 */
	private ToolBar toolbar;
	/**
	 * Botón añadir contacto
	 */
	private Button addContactButton;
	/**
	 * Combo para realizar una búsqueda por categorías
	 */
	private ComboBox<String> searchCategoryComboBox;
	/**
	 * Caja de texto para realizar búsqueda de contactos
	 */
	private TextField searchTextField;
	/**
	 * Combo para el tipo de búsqueda
	 */
	private ComboBox<SearchTypeEnum> searchTypeComboBox;
	/**
	 * Muestra la cantidad de contactos mostrados
	 */
	private Label contactsSizeLabel;
	/**
	 * Vista de la información del contacto
	 */
	private WebView webView;
	/**
	 * Lista de todos los items del menú
	 */
	private MenuItems menuItems;
	/**
	 * Menú de contactos
	 */
	private Menu menuContact;
	/**
	 * Menú de cumpleaños
	 */
	private Menu menuBirthday;

	/**
	 * Para i18n
	 */
	private ResourceBundle rb;

	/**
	 * Constructor por defecto. Inicia i18n
	 */
	public MainWindow() {
		super();
		rb = Utils.getResourceBundle();
	}

	/**
	 * Construcción de la ventana principal
	 * 
	 * @param stage
	 *            Stage de la ventana
	 */
	public void build(Stage stage) {
		// Crear Panel
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
		VBox.setVgrow(gridPane, Priority.ALWAYS);

		// Lista de contactos
		listView = createListView();
		gridPane.add(listView, 0, 0);
		GridPane.setVgrow(listView, Priority.ALWAYS);

		// Visualización de contactos
		WebView webView = createWebView();
		gridPane.add(webView, 1, 0);
		GridPane.setVgrow(webView, Priority.ALWAYS);
		GridPane.setRowSpan(webView, 2);

		// Cantidad de contactos
		contactsSizeLabel = new Label("0 contactos");
		contactsSizeLabel.setAlignment(Pos.CENTER_RIGHT);
		gridPane.add(contactsSizeLabel, 0, 1);
		GridPane.setHalignment(contactsSizeLabel, HPos.CENTER);
		GridPane.setMargin(contactsSizeLabel, TOOLBAR_MARGIN);

		// Crear ventana principal
		VBox main = new VBox();
		main.getChildren().add(createMenuBar());
		main.getChildren().add(createToolbar());
		main.getChildren().add(gridPane);
		setDisable(true);

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
	 * Establece la lista de contactos y el contador de contactos
	 * 
	 * @param contacts
	 *            Colección ordenada de contactos
	 */
	public void establishContacts(SortedSet<SimpleVCard> contacts) {
		ObservableList<SimpleVCard> elementList = FXCollections.observableArrayList(contacts);
		listView.setItems(elementList);

		String contactsSize = MessageFormat.format(rb.getString(Text.I18N_CONTACTS_SIZE), contacts.size());
		contactsSizeLabel.setText(contactsSize);
	}

	/**
	 * Busca la información completa del contacto, genera la página de
	 * información y la muestra
	 * 
	 * @param VCard
	 *            Información del contacto
	 * @param photoDir
	 *            Directorio de fotografías
	 */
	public void establishContactInfo(VCard vcard, String photoDir) {
		String html = HtmlContactBuilder.build(vcard, photoDir);

		webView.getEngine().loadContent(html);
		webView.getEngine().setJavaScriptEnabled(true);
	}

	/**
	 * Selecciona en la lista de contactos el indicado
	 * 
	 * @param vcard
	 *            Contacto a seleccionar en la lista
	 */
	public void selectContactInList(VCard vcard) {
		for (SimpleVCard simpleVCard : listView.getItems()) {
			if (simpleVCard.getUid().equals(vcard.getUid())) {
				listView.getSelectionModel().select(simpleVCard);
				listView.scrollTo(simpleVCard);
			}
		}
	}

	/**
	 * Carga los datos en la lista de categorías del buscador
	 * 
	 * @param categories
	 *            Lista de categorías (no ordenadas)
	 */
	public void loadCategorySearchList(SortedSet<String> categories) {
		// Esteblece los valores
		ObservableList<String> searchCategoryOptions = FXCollections.observableArrayList();
		searchCategoryOptions.add("");
		searchCategoryOptions.addAll(categories);
		searchCategoryComboBox.setItems(searchCategoryOptions);
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
	 * Obtiene el contacto seleccionado del componente ListView
	 * 
	 * @return Contacto seleccionado
	 */
	public SimpleVCard getListViewSelectedItem() {
		return listView.getSelectionModel().getSelectedItem();
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
	 * Obtiene el filtro de búsqueda a partir de los valores de la ventana
	 * @return Filtro de búsqueda
	 */
	public SearchFilter getSearchFilter() {
		String category = searchCategoryComboBox.getValue();
		String text = searchTextField.getText();
		SearchTypeEnum type = searchTypeComboBox.getValue();
		
		return new SearchFilter(category, text, type);
	}

	/**
	 * Obtiene el componente ComboBox para realizar búsquedas
	 * 
	 * @return Componente para realizar búsquedas
	 */
	public ComboBox<String> getSearchCategoryComboBox() {
		return searchCategoryComboBox;
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
		MenuItem menuContactStatistics = createMenuItem(Text.I18N_MENU_CONTACT_STATISTICS, ResourcePath.IMG_MENU_CONTACT_STATISTICS, null);
		menuContact = new Menu(rb.getString(Text.I18N_MENU_CONTACT));
		menuContact.getItems().addAll(menuContactAdd, menuContactEdit, menuContactRemove, new SeparatorMenuItem(), menuContactStatistics);
		menuBar.getMenus().addAll(menuContact);
		menuItems.setContactAdd(menuContactAdd);
		menuItems.setContactEdit(menuContactEdit);
		menuItems.setContactRemove(menuContactRemove);
		menuItems.setContactStatistics(menuContactStatistics);

		// Crea menú Birthday
		MenuItem menuBirthdayToday = createMenuItem(Text.I18N_MENU_BIRTHDAY_TODAY, ResourcePath.IMG_MENU_BIRTHDAY_TODAY, null);
		MenuItem menuBirthdayWithinWeek = createMenuItem(Text.I18N_MENU_BIRTHDAY_WITHINWEEK, ResourcePath.IMG_MENU_BIRTHDAY_WITHINWEEK, KeyCode.B, KeyCombination.CONTROL_DOWN);
		MenuItem menuBirthdayWithinMonth = createMenuItem(Text.I18N_MENU_BIRTHDAY_WITHINMONTH, ResourcePath.IMG_MENU_BIRTHDAY_WITHINMONTH, null);
		MenuItem menuBirthdayThisWeek = createMenuItem(Text.I18N_MENU_BIRTHDAY_THISWEEK, ResourcePath.IMG_MENU_BIRTHDAY_THISWEEK, null);
		MenuItem menuBirthdayThisMonth = createMenuItem(Text.I18N_MENU_BIRTHDAY_THISMONTH, ResourcePath.IMG_MENU_BIRTHDAY_THISMONTH, null);
		menuBirthday = new Menu(rb.getString(Text.I18N_MENU_BIRTHDAY));
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
		toolbar = new ToolBar(gridPane);

		// Combo categorías
		searchCategoryComboBox = new ComboBox<>();
		searchCategoryComboBox.setMaxWidth(Double.POSITIVE_INFINITY);
		searchCategoryComboBox.setTooltip(new Tooltip(rb.getString(Text.I18N_TOOLBAR_CATEGORYSEARCH_TOOLTIP)));
		gridPane.add(searchCategoryComboBox, 0, 0);
		GridPane.setMargin(searchCategoryComboBox, TOOLBAR_MARGIN);
		GridPane.setHalignment(searchCategoryComboBox, HPos.CENTER);
		HBox.setHgrow(searchCategoryComboBox, Priority.ALWAYS);

		// Buscador
		searchTextField = new TextField();
		searchTextField.setPromptText(rb.getString(Text.I18N_TOOLBAR_SEARCH));
		searchTextField.setTooltip(new Tooltip(rb.getString(Text.I18N_TOOLBAR_SEARCH)));
		gridPane.add(searchTextField, 1, 0);
		GridPane.setMargin(searchTextField, TOOLBAR_MARGIN);
		GridPane.setHalignment(searchTextField, HPos.LEFT);
		HBox.setHgrow(searchTextField, Priority.ALWAYS);

		// Combo tipo de búsqueda
		ObservableList<SearchTypeEnum> searchTypeOptions = FXCollections.observableArrayList();
		for (SearchTypeEnum searchType : SearchTypeEnum.values()) {
			searchTypeOptions.add(searchType);
		}
		searchTypeComboBox = new ComboBox<>(searchTypeOptions);
		searchTypeComboBox.setMaxWidth(Double.POSITIVE_INFINITY);
		searchTypeComboBox.setButtonCell(createSearchTypeComboBoxCellFactory().call(null));
		searchTypeComboBox.setCellFactory(createSearchTypeComboBoxCellFactory());
		searchTypeComboBox.setValue(SearchTypeEnum.getDefault());
		searchTypeComboBox.setTooltip(new Tooltip(rb.getString(Text.I18N_TOOLBAR_SEARCHTYPE_TOOLTIP)));
		gridPane.add(searchTypeComboBox, 2, 0);
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
		gridPane.add(addContactButton, 3, 0);
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
		return listView;
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

	/**
	 * Deshabilita los elementos de la ventana según estén cargados los
	 * contactos
	 * 
	 * @param value
	 *            True deshabilita elementos y False habilita elementos
	 */
	public void setDisable(boolean value) {
		// Deshabilita menú
		menuContact.setDisable(value);
		menuItems.getContactAdd().setDisable(value);
		menuItems.getContactEdit().setDisable(value);
		menuItems.getContactRemove().setDisable(value);
		menuItems.getContactStatistics().setDisable(value);
		menuBirthday.setDisable(value);
		menuItems.getBirthdayThisMonth().setDisable(value);
		menuItems.getBirthdayThisWeek().setDisable(value);
		menuItems.getBirthdayToday().setDisable(value);
		menuItems.getBirthdayWithinMonth().setDisable(value);
		menuItems.getBirthdayWithinWeek().setDisable(value);

		// Deshabilita toolbar
		toolbar.setDisable(value);

		// Deshabilita elementos principales
		listView.setDisable(value);
		contactsSizeLabel.setDisable(value);
		webView.setDisable(value);
	}
}
