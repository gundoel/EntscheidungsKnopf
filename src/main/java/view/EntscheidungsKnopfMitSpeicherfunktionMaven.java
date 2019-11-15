package view;

import io.CsvStrategy;
import io.IOStrategy;
import io.XmlStrategy;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.AppData;
import model.Decision;

import java.io.File;
import java.util.Optional;

/**
 * notes:
 * File decisions.txt must be in resources directory of project or passed as program argument
 * Buttons are only enabled, if interaction is allowed (e. g. decision must be selected in table
 * in order to activate delete button, data must be changed in order to activate save button,
 * textfield must be filled in order to acitivate add button etc.)
 * If data was changed and user did not save changes, a confirmation dialog is opened, when app is closed
 *
 * @author Simon Rizzi, rizzisim@students.zhaw.ch
 * @version 4.0
 * TODO in saveAndExitDialog fist button is always focused. requestFocus etc. do not work -> could change button order
 * TODO UI design (main app and confirmation dialog) could be better
 * TODO io change needs refactoring. wrong and redundant logic
 */

public class EntscheidungsKnopfMitSpeicherfunktionMaven extends Application {
    private TextArea msgBox = new TextArea();
    private TableView decisionTable = new TableView();
    private AppData appData = new AppData();
    // store current stage, otherwise many methods need stage parameter
    private Stage currentStage = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        currentStage = primaryStage;
        primaryStage.setTitle("Entscheidungsknopf");
        Label msgBoxLabel = new Label("Meldungen:");
        msgBox.getStyleClass().add("msgbox");
        msgBox.textProperty().bind(appData.getMsg());

        // https://blog.axxg.de/javafx-stage-dialog-beenden-mit-abfrage/
        primaryStage.setOnCloseRequest(
                windowEvent -> {
                    if (!appData.getDecisionsSaved().getValue()) {
                        // prevents primaryStage from closing
                        // https://stackoverflow.com/questions/23160573/javafx-stage-setoncloserequest-without-function
                        windowEvent.consume();
                        showSaveAndExitDialog();
                    }
                });
        VBox main = new VBox();
        main.setSpacing(5);
        main.setPadding(new Insets(5, 5, 5, 5));

        // Display decisions in table and provide buttons to change list and save to text file
        HBox mainBox = new HBox();
        mainBox.getStyleClass().add("mainbox");
        mainBox.setSpacing(5);

        TextField decisionToAddTextField = new TextField();
        decisionToAddTextField.getStyleClass().add("addtextfield");
        decisionToAddTextField.setPromptText("Entscheidung hinzufügen");
        decisionToAddTextField.setOnMouseClicked(e -> decisionToAddTextField.selectAll());

        VBox topBox = new VBox();
        topBox.setSpacing(5);
        topBox.getStyleClass().add("rightbox");

        HBox controlButtonBox = new HBox();
        controlButtonBox.setSpacing(5);
        controlButtonBox.getStyleClass().add("controlbuttonbox");

        Button addButton = new Button("Hinzufügen");
        addButton.getStyleClass().add("appButton");
        //disable button when textfield is empty
        addButton.disableProperty().bind(
                Bindings.isEmpty(decisionToAddTextField.textProperty())
        );
        // change decisionsSaved if new decision was added (negate return value true)
        addButton.setOnAction(event -> appData.setDecisionsSaved(!appData.handleAddAction(decisionToAddTextField.getText())));

        HBox decisionButtonBox = new HBox();
        decisionButtonBox.setSpacing(5);
        decisionButtonBox.getStyleClass().add("decisionbuttonbox");

        Button decisionButton = new Button("Klick mich");
        decisionButton.getStyleClass().add("decisionButton");
        decisionButtonBox.getChildren().add(decisionButton);
        controlButtonBox.getChildren().addAll(decisionToAddTextField, addButton, getDeleteButton());

        mainBox.getChildren().addAll(topBox);
        topBox.getChildren().addAll(decisionButtonBox, controlButtonBox);
        main.getChildren().addAll(getMainMenuBar(), mainBox, getDecisionTable(), msgBoxLabel, msgBox);

        Scene scene = new Scene(main, 600, 400);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        try {
            decisionButton.setOnAction(actionEvent -> {
                if (appData.getRandomDecision() != null) {
                    decisionButton.setText(appData.getRandomDecision());
                }
            });
        } catch (Exception e) {
            appData.setMsg(e.getMessage());
            e.printStackTrace();
        }
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private TableView getDecisionTable() {
        decisionTable.setEditable(true);
        decisionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        decisionTable.getStyleClass().add("decisionTable");
        try {
            TableColumn uuidColumn = new TableColumn<Decision, String>("ID");
            TableColumn createdColumn = new TableColumn<Decision, String>("Erstellt");
            TableColumn createdUserColumn = new TableColumn<Decision, String>("Ersteller");
            TableColumn decisionColumn = new TableColumn<Decision, String>("Entscheidung");
            uuidColumn.setCellValueFactory(new PropertyValueFactory<Decision, String>("uuid"));
            createdColumn.setCellValueFactory(new PropertyValueFactory<Decision, String>("created"));
            createdUserColumn.setCellValueFactory(new PropertyValueFactory<Decision, String>("createdUser"));
            decisionColumn.setCellValueFactory(new PropertyValueFactory<Decision, String>("decisionText"));
            decisionTable.setItems(appData.getDecisionList());
            decisionTable.getColumns().addAll(uuidColumn, createdColumn, createdUserColumn, decisionColumn);
        } catch (Exception e) {
            appData.setMsg("Entscheidungen konnten nicht geladen werden.");
        }
        return decisionTable;
    }

    Button getDeleteButton() {
        Button deleteButton = new Button("Löschen");
        // change decisionsSaved if decision was removed (negate return value true)
        deleteButton.setOnAction(event -> {
            appData.setDecisionsSaved(
                    (!appData.handleDeleteAction((Decision) decisionTable.getSelectionModel().getSelectedItem()))
            );
        });
        deleteButton.getStyleClass().add("appButton");
        // disable button when no item is selected
        deleteButton.disableProperty().bind(
                Bindings.isEmpty(decisionTable.getSelectionModel().getSelectedItems())
        );
        return deleteButton;
    }

    private MenuBar getMainMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.getStyleClass().add("menuBar");
        menuBar.getMenus().addAll(getFileMenu(), getIOStrategyMenu());
        return menuBar;
    }

    private Menu getFileMenu() {
        Menu fileMenu = new Menu("Datei");
        fileMenu.getItems().addAll(getSaveMenuItem(), getSaveAsMenuItem(), getLoadDecisionsMenuItem());
        return fileMenu;
    }

    private MenuItem getSaveMenuItem() {
        MenuItem saveMenuItem = new MenuItem("Speichern");
        saveMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        // disable menu item when no data was changed
        saveMenuItem.disableProperty().bind(appData.getDecisionsSaved());
        saveMenuItem.setOnAction(actionEvent -> appData.handleSaveAction(getCurrentStage()));
        return saveMenuItem;
    }

    private MenuItem getSaveAsMenuItem() {
        MenuItem saveAsMenuItem = new MenuItem("Speichern unter");
        saveAsMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
        saveAsMenuItem.setOnAction(actionEvent -> appData.handleSaveAsAction(getCurrentStage()));
        return saveAsMenuItem;
    }

    private MenuItem getLoadDecisionsMenuItem() {
        MenuItem loadDecisionsMenuItem = new MenuItem("Entscheidungen laden");
        loadDecisionsMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        loadDecisionsMenuItem.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(appData.getIoStrategy().getFileExtensionFilter());
            fileChooser.setTitle("Entscheidungsdatei öffnen");
            File file = fileChooser.showOpenDialog(getCurrentStage());
            final Label fileLabel = new Label();
            if (file != null) {
                fileLabel.setText(file.getPath());
                appData.setFilepath(file.getAbsolutePath());
                try {
                    appData.fillDecisionList();
                    appData.setMsg("Entscheidungen von " + appData.getFilepath() + " geladen.");
                } catch (Exception e) {
                    appData.setFilepath(null);
                    appData.setMsg(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        return loadDecisionsMenuItem;
    }

    private Menu getIOStrategyMenu() {
        Menu menu = new Menu("Datenverwaltung");
        ToggleGroup ioStrategySelection = new ToggleGroup();
        RadioMenuItem xmlMenuItem = getXMLIOStrategyMenuItem();
        xmlMenuItem.setToggleGroup(ioStrategySelection);
        RadioMenuItem csvMenuItem = getCSVIOStrategyMenuItem();
        csvMenuItem.setToggleGroup(ioStrategySelection);
        // mark as selected at startup
        xmlMenuItem.setSelected(isXmlStrategySelected());
        csvMenuItem.setSelected(!isXmlStrategySelected());
        menu.getItems().addAll(xmlMenuItem, csvMenuItem);
        return menu;
    }

    //TODO ugly
    private boolean isXmlStrategySelected() {
        if(appData.getIoStrategy() instanceof XmlStrategy) {
            return true;
        }
        else {
            return false;
        }
    }

    // todo could be done better
    private RadioMenuItem getXMLIOStrategyMenuItem() {
        RadioMenuItem xmlMenuItem = new RadioMenuItem("XML");
        xmlMenuItem.setOnAction(actionEvent -> {
            // data must be saved with current io strategy
            IOStrategy newStrategy = new XmlStrategy();
            IOStrategy oldStrategy = appData.getIoStrategy();
            //resaving needed, filepath points to wrong file
            if(appData.getDecisionList().size() > 0) {
                appData.setDecisionsSaved(false);
            }
            if(!isIOStrategyChangeValid(newStrategy)) {
                xmlMenuItem.setSelected(!isXmlStrategySelected());
                appData.setIOStrategy(oldStrategy);
            }
            else {
                appData.setIOStrategy(newStrategy);
            }
        });
        return xmlMenuItem;
    }

    //TODO could be done better
    private RadioMenuItem getCSVIOStrategyMenuItem() {
        RadioMenuItem csvMenuItem = new RadioMenuItem("CSV");
        csvMenuItem.setOnAction(actionEvent -> {
            // data must be saved with current io strategy
            IOStrategy newStrategy = new CsvStrategy();
            IOStrategy oldStrategy = appData.getIoStrategy();
            //resaving needed, filepath points to wrong file
            if(appData.getDecisionList().size() > 0) {
                appData.setDecisionsSaved(false);
            }
            if(!isIOStrategyChangeValid(newStrategy)) {
                csvMenuItem.setSelected(!isXmlStrategySelected());
                appData.setIOStrategy(oldStrategy);
            }
            else{
                appData.setIOStrategy(newStrategy);
            }
        });
        return csvMenuItem;
    }

    //TODO could be done better
    private boolean isIOStrategyChangeValid(IOStrategy newStrategy) {
        if(!appData.getDecisionsSaved().getValue()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Ungespeicherte Änderung");
            alert.setHeaderText("Ungespeicherte Änderung");
            alert.setContentText("Speicherstrategie geändert. Alte Datei speichern\n" +
                    "und neue Datei anlegen?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                appData.handleSaveAction(getCurrentStage());
                // only return true, if user created new file
                appData.setIOStrategy(newStrategy); //set strategy to save file with right format //TODO ugly
                return appData.handleSaveAsAction(getCurrentStage());
            } else {
                return false;
            }
        }
        return true;
    }

    private void showSaveAndExitDialog() {
        final Stage dialog = new Stage();
        dialog.setTitle("Ungespeicherte Änderungen");
        dialog.initModality(Modality.APPLICATION_MODAL);
        //dialog.initOwner(parent);
        dialog.setOnCloseRequest(
                windowEvent -> {
                    dialog.close();
                }
        );
        Label label = new Label("Änderungen nicht gespeichert. Trotzdem beenden?");

        Button exitButton = new Button("Beenden");
        exitButton.getStyleClass().add("appButton");
        exitButton.setOnAction(actionEvent -> {
            System.exit(0);
        });

        Button saveAndExitButton = new Button("Speichern und beenden");
        saveAndExitButton.getStyleClass().add("appButton");
        //TODO not working -> focus always on fist button
        //saveAndExitButton.setDefaultButton(true);
        // saveAndExitButton.requestFocus();
        saveAndExitButton.setOnAction(actionEvent -> {
            appData.handleSaveAction(getCurrentStage());
            if (appData.getDecisionsSaved().getValue()) {
                System.exit(0);
            } else {
                dialog.close();
            }
        });

        Button cancelButton = new Button("Abbrechen");
        cancelButton.getStyleClass().add("appButton");
        cancelButton.setOnAction(actionEvent -> dialog.close());
        HBox hbox = new HBox();
        hbox.getStyleClass().add("hbox_center");
        hbox.setSpacing(10);
        hbox.getChildren().add(exitButton);
        hbox.getChildren().add(cancelButton);
        hbox.getChildren().add(saveAndExitButton);

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(5, 5, 5, 5));
        hbox.getStyleClass().add("vbox_center");
        vbox.setSpacing(10);
        vbox.getChildren().add(label);
        vbox.getChildren().add(hbox);

        Scene scene = new Scene(vbox);
        dialog.setScene(scene);
        dialog.show();
    }

    public Stage getCurrentStage() {
        return currentStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}