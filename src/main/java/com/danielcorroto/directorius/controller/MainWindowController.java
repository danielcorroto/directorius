package com.danielcorroto.directorius.controller;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.SortedSet;

import com.danielcorroto.directorius.model.ContactManager;
import com.danielcorroto.directorius.model.SearchFilter;
import com.danielcorroto.directorius.model.SimpleVCard;
import com.danielcorroto.directorius.model.Utils;
import com.danielcorroto.directorius.model.log.Logger;
import com.danielcorroto.directorius.model.type.SearchTypeEnum;
import com.danielcorroto.directorius.view.AboutWindow;
import com.danielcorroto.directorius.view.AlertExceptionDialog;
import com.danielcorroto.directorius.view.BirthdayDialog;
import com.danielcorroto.directorius.view.MainWindow;
import com.danielcorroto.directorius.view.StatisticDialog;
import com.danielcorroto.directorius.view.Text;

import ezvcard.VCard;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * Gestión la ventana principal
 * 
 * @author Daniel Corroto Quirós
 *
 */
public class MainWindowController extends Application {
	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger.getLogger(MainWindowController.class);
	/**
	 * Clase de la vista
	 */
	private MainWindow window;
	/**
	 * Gestión de contactos
	 */
	private ContactManager manager;
	/**
	 * Stage de la ventana
	 */
	private Stage stage;

	/**
	 * Para i18n
	 */
	private ResourceBundle rb;

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			rb = Utils.getResourceBundle();

			stage = primaryStage;
			window = new MainWindow();
			window.build(primaryStage);

			setupMenuItemFunction();
			addContactButtonFunction();
			listViewFunction();
			searchFunctions();

			manager = ContactManager.autoLoadFile();
			if (manager != null) {
				loadManager();
			}

			window.getSearchTextField().requestFocus();
		} catch (Exception e) {
			LOGGER.severe("Error en la aplicación principal", e);
			new AlertExceptionDialog(e).showAndWait();
		}
	}

	/**
	 * Carga los datos del manager en la lista de contactos y cumpleaños.
	 * Habilita la ventana
	 */
	private void loadManager() {
		// Habilita ventana
		window.setDisable(false);
		// Carga datos
		window.establishContacts(manager.getAllSimpleVCard());
		window.loadCategorySearchList(manager.getCategories());
		// Carga ventana de cumpleaños
		Calendar end = Calendar.getInstance();
		end.add(Calendar.DATE, 7);
		List<VCard> cards = manager.getBirthday(new Date(), end.getTime());
		if (cards != null && !cards.isEmpty()) {
			Optional<VCard> card = new BirthdayDialog(cards, Text.I18N_MENU_BIRTHDAY_WITHINWEEK).showAndWait();
			if (card.isPresent()) {
				window.establishContactInfo(card.get(), manager.getPhotoDir());
				window.selectContactInList(card.get());
			}
		}
	}
	
	/**
	 * Setea la funcionalidad de los menús
	 */
	private void setupMenuItemFunction() {
		setupMenuItemFileFunction();
		setupMenuItemContactFunction();
		setupMenuItemBirthdayFunction();
		setupMenuItemHelpFunction();
	}

	/**
	 * Setea la funcionalidad de los items del menú Archivo
	 */
	private void setupMenuItemFileFunction() {
		// Nuevo fichero
		window.getMenuItems().getFileNew().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				File f = createFileChooser(true);

				try {
					if (f == null) {
						return;
					}
					f.delete();
					if (!f.createNewFile()) {
						return;
					}
					manager = ContactManager.loadFile(f.getAbsolutePath());
					if (manager != null) {
						loadManager();
					}
				} catch (Exception e) {
					LOGGER.severe("Error al cargar el fichero " + f, e);
					new AlertExceptionDialog(e).showAndWait();
				}
			}
		});
		// Cargar fichero
		window.getMenuItems().getFileOpen().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				File f = createFileChooser(false);

				if (f == null) {
					return;
				}
				try {
					manager = ContactManager.loadFile(f.getAbsolutePath());
					if (manager != null) {
						loadManager();
					}
				} catch (Exception e) {
					LOGGER.severe("Error al cargar el fichero " + f, e);
					new AlertExceptionDialog(e).showAndWait();
				}
			}
		});
		// Salir
		window.getMenuItems().getFileExit().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Platform.exit();
			}
		});
	}

	/**
	 * Crea el selector de fichero para Nuevo directorio y Abrir directorio
	 * 
	 * @param create
	 *            Indica si es de creación de directorio (true) o de apertura
	 *            (false)
	 * @return Fichero seleccionado o null
	 */
	private File createFileChooser(boolean create) {
		String title;
		if (create) {
			title = rb.getString(Text.I18N_MENU_FILE_NEW);
		} else {
			title = rb.getString(Text.I18N_MENU_FILE_OPEN);
		}
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(title);
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(rb.getString(Text.I18N_FILE_OPEN_VCF), "*.vcf"));
		File f;
		if (create) {
			f = fileChooser.showSaveDialog(stage);
		} else {
			f = fileChooser.showOpenDialog(stage);
		}
		return f;
	}

	/**
	 * Setea la funcionalidad de los items del menú Contactos
	 */
	private void setupMenuItemContactFunction() {
		// Añadir contacto
		window.getMenuItems().getContactAdd().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				loadContactWindow(null);
			}
		});
		// Editar contacto
		window.getMenuItems().getContactEdit().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				if (window.getListViewSelectedItem() == null) {
					return;
				}

				SimpleVCard simplevcard = window.getListViewSelectedItem();
				loadContactWindow(simplevcard);
			}
		});
		// Eliminar contacto
		window.getMenuItems().getContactRemove().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				SimpleVCard simpleCard = window.getListViewSelectedItem();
				removeContact(simpleCard);
			}
		});
		// Estadísticas de contacto
		window.getMenuItems().getContactStatistics().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					new StatisticDialog(manager.getStatistic()).showAndWait();
				} catch (Exception e) {
					LOGGER.severe("Error al abrir la ventana Estadísticas", e);
					new AlertExceptionDialog(e).showAndWait();
				}
			}
		});
	}

	/**
	 * Setea la funcionalidad de los items del menú Cumpleaños
	 */
	private void setupMenuItemBirthdayFunction() {
		// Cumpleaños hoy
		window.getMenuItems().getBirthdayToday().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				List<VCard> cards = manager.getBirthday(new Date(), new Date());
				Optional<VCard> card = new BirthdayDialog(cards, Text.I18N_MENU_BIRTHDAY_TODAY).showAndWait();
				if (card.isPresent()) {
					window.establishContactInfo(card.get(), manager.getPhotoDir());
					window.selectContactInList(card.get());
				}
			}
		});

		// Cumpleaños dentro de una semana
		window.getMenuItems().getBirthdayWithinWeek().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Calendar end = Calendar.getInstance();
				end.add(Calendar.DATE, 7);
				List<VCard> cards = manager.getBirthday(new Date(), end.getTime());
				Optional<VCard> card = new BirthdayDialog(cards, Text.I18N_MENU_BIRTHDAY_WITHINWEEK).showAndWait();
				if (card.isPresent()) {
					window.establishContactInfo(card.get(), manager.getPhotoDir());
					window.selectContactInList(card.get());
				}
			}
		});

		// Cumpleaños dentro de un mes
		window.getMenuItems().getBirthdayWithinMonth().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Calendar end = Calendar.getInstance();
				end.add(Calendar.MONTH, 1);
				List<VCard> cards = manager.getBirthday(new Date(), end.getTime());
				Optional<VCard> card = new BirthdayDialog(cards, Text.I18N_MENU_BIRTHDAY_WITHINMONTH).showAndWait();
				if (card.isPresent()) {
					window.establishContactInfo(card.get(), manager.getPhotoDir());
					window.selectContactInList(card.get());
				}
			}
		});

		// Cumpleaños esta semana
		window.getMenuItems().getBirthdayThisWeek().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Calendar start = Calendar.getInstance();
				start.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
				Calendar end = Calendar.getInstance();
				end.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
				List<VCard> cards = manager.getBirthday(start.getTime(), end.getTime());
				Optional<VCard> card = new BirthdayDialog(cards, Text.I18N_MENU_BIRTHDAY_THISWEEK).showAndWait();
				if (card.isPresent()) {
					window.establishContactInfo(card.get(), manager.getPhotoDir());
					window.selectContactInList(card.get());
				}
			}
		});

		// Cumpleaños este mes
		window.getMenuItems().getBirthdayThisMonth().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Calendar start = Calendar.getInstance();
				start.set(Calendar.DATE, 1);
				Calendar end = Calendar.getInstance();
				end.set(Calendar.DATE, end.getActualMaximum(Calendar.DATE));
				List<VCard> cards = manager.getBirthday(start.getTime(), end.getTime());
				Optional<VCard> card = new BirthdayDialog(cards, Text.I18N_MENU_BIRTHDAY_THISMONTH).showAndWait();
				if (card.isPresent()) {
					window.establishContactInfo(card.get(), manager.getPhotoDir());
					window.selectContactInList(card.get());
				}
			}
		});
	}

	/**
	 * Setea la funcionalidad de los items del menú Ayuda
	 */
	private void setupMenuItemHelpFunction() {
		window.getMenuItems().getHelpAbout().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					new AboutWindow().start(new Stage());
				} catch (Exception e) {
					LOGGER.severe("Error al abrir la ventana Acerca de...", e);
					new AlertExceptionDialog(e).showAndWait();
				}
			}
		});
	}

	/**
	 * Carga la ventana de añadir/editar contacto y recarga la lista de
	 * contactos
	 * 
	 * @param simplevcard
	 *            Información del contacto a editar o null si es uno nuevo
	 */
	private void loadContactWindow(SimpleVCard simplevcard) {
		try {
			VCard vcard = null;
			if (simplevcard != null) {
				vcard = manager.readContact(simplevcard.getUid());
			}
			ContactDialogController cwc = new ContactDialogController(manager, vcard);
			cwc.start();

			// Carga la nueva lista
			vcard = cwc.getVcard();
			if (vcard != null) {
				window.loadCategorySearchList(manager.getCategories());
				reloadContactListView();
				window.selectContactInList(vcard);
			}
		} catch (Exception e) {
			LOGGER.severe("Error al abrir la ventana Añadir/Editar contacto...", e);
			new AlertExceptionDialog(e).showAndWait();
		}
	}
	
	/**
	 * Recarga la lista de contactos a partir de los parámetros de los elementos
	 * de la búsqueda
	 */
	private void reloadContactListView() {
		SearchFilter filter = window.getSearchFilter();
		SortedSet<SimpleVCard> list = manager.search(filter);
		window.establishContacts(list);
	}

	/**
	 * Setea la funcionalidad del botón añadir contacto
	 */
	private void addContactButtonFunction() {
		window.getAddContactButton().setOnAction(window.getMenuItems().getContactAdd().getOnAction());
	}

	/**
	 * Setea la funcionalidad de la lista de contactos
	 */
	private void listViewFunction() {
		window.getListView().getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SimpleVCard>() {

			@Override
			public void changed(ObservableValue<? extends SimpleVCard> observable, SimpleVCard oldValue, SimpleVCard newValue) {
				if (newValue != null) {
					VCard vcard = manager.readContact(newValue.getUid());
					window.establishContactInfo(vcard, manager.getPhotoDir());
				}
			}
		});
		window.getListView().setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
					SimpleVCard simplevcard = window.getListViewSelectedItem();
					if (simplevcard == null) {
						return;
					}
					loadContactWindow(simplevcard);
				}

			}
		});
		window.getListView().setCellFactory(createContactListViewCellFactory());
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
						final ListCell<SimpleVCard> cell = this;

						if (empty || item == null || item.getFormattedName() == null) {
							setText(null);
						} else {
							setText(item.getFormattedName().getValue());
						}

						// Menú contextual
						final ContextMenu contextMenu = new ContextMenu();
						MenuItem edit = new MenuItem(rb.getString(Text.I18N_MENU_CONTACT_EDIT));
						MenuItem remove = new MenuItem(rb.getString(Text.I18N_MENU_CONTACT_REMOVE));
						contextMenu.getItems().addAll(edit, remove);
						edit.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								SimpleVCard simplevcad = cell.getItem();
								loadContactWindow(simplevcad);
							}
						});
						remove.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								SimpleVCard simpleCard = cell.getItem();
								removeContact(simpleCard);
							}
						});
						this.setContextMenu(contextMenu);

					}
				};
			}
		};

		return cellFactory;
	}

	/**
	 * Muestra el cuadro de diálogo de eliminar contacto y realiza la función de
	 * eliminarlo
	 * 
	 * @param simpleCard
	 *            Información sencilla del contacto
	 */
	private void removeContact(SimpleVCard simpleCard) {
		if (simpleCard == null) {
			return;
		}
		VCard card = manager.readContact(simpleCard.getUid());

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(rb.getString(Text.I18N_MENU_CONTACT_REMOVE));
		alert.setHeaderText(card.getFormattedName().getValue());
		alert.setContentText(null);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			try {
				manager.deleteContact(card.getUid());
			} catch (IOException e) {
				LOGGER.severe("Error al borrar contacto " + card.getFormattedName().getValue(), e);
				new AlertExceptionDialog(e).showAndWait();
			}
			window.loadCategorySearchList(manager.getCategories());
			reloadContactListView();
		}
	}

	private void searchFunctions() {
		searchCategoryComboBoxFunction();
		searchTextFieldFunction();
		searchTypeComboBoxFunction();
	}

	/**
	 * Setea la funcionalidad del combo de categoría de búsqueda: busca
	 * contactos y los muestre en el ListView
	 */
	private void searchCategoryComboBoxFunction() {
		window.getSearchCategoryComboBox().getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				SearchFilter filter = window.getSearchFilter();
				SortedSet<SimpleVCard> list = manager.search(filter);
				window.establishContacts(list);
			}
		});
	}

	/**
	 * Setea la funcionalidad de la caja de búsqueda: busca contactos y los
	 * muestra en el ListView
	 */
	private void searchTextFieldFunction() {
		window.getSearchTextField().textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				SearchFilter filter = window.getSearchFilter();
				SortedSet<SimpleVCard> list = manager.search(filter);
				window.establishContacts(list);
			}
		});
	}

	/**
	 * Setea la funcionalidad del combo de tipo de búsqueda: busca contactos y
	 * los muestre en el ListView
	 */
	private void searchTypeComboBoxFunction() {
		window.getSearchTypeComboBox().getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SearchTypeEnum>() {

			@Override
			public void changed(ObservableValue<? extends SearchTypeEnum> observable, SearchTypeEnum oldValue, SearchTypeEnum newValue) {
				String text = window.getSearchTextField().getText();
				// Si no hay texto que buscar el resultado siempre es la lista
				// completa
				if (text.trim().isEmpty()) {
					return;
				}

				SearchFilter filter = window.getSearchFilter();
				SortedSet<SimpleVCard> list = manager.search(filter);
				window.establishContacts(list);
			}
		});
	}

	public static void main(String[] args) {
		launch(args);
	}
}
