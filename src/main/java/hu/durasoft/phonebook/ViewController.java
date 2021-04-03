package hu.durasoft.phonebook;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.apache.commons.validator.routines.EmailValidator;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ViewController implements Initializable {

    private static final String MENU_CONTACTS = "Contacts";
    private static final String MENU_LIST = "List";
    private static final String MENU_EXPORT = "Export";
    private static final String MENU_EXIT = "Exit";

    private final DB db = new DB();
    private final ObservableList<Person> data = FXCollections.observableArrayList();

    @FXML
    AnchorPane anchorPane;
    @FXML
    SplitPane mainSplit;
    @FXML
    TableView<Person> table;
    @FXML
    TextField inputLastName;
    @FXML
    TextField inputFirstName;
    @FXML
    TextField inputEmail;
    @FXML
    Button addNewContactButton;
    @FXML
    StackPane menuPane;
    @FXML
    Pane contactPane;
    @FXML
    Pane exportPane;
    @FXML
    TextField inputExport;
    @FXML
    Button savePDF;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setTableData();
        setMenuData();
    }

    @FXML
    private void addContact() {
        String email = inputEmail.getText();
        if (EmailValidator.getInstance().isValid(email)) {
            Person person = new Person(inputLastName.getText(), inputFirstName.getText(), email);
            data.add(person);
            db.addContact(person);
            inputLastName.clear();
            inputFirstName.clear();
            inputEmail.clear();
        } else {
            alert("Wrong e-mail address");
        }
    }

    @FXML
    private void exportList() {
        String fileName = inputExport.getText().trim();
        if (!"".equals(fileName)) {
            PdfGeneration pdf = new PdfGeneration();
            pdf.pdfGeneration(fileName, data);
        } else {
            alert("No file name");
        }
    }

    private void setTableData() {
        table.getColumns().addAll(setLastName(), setFirstName(), setEmail(), setDelete());
        data.addAll(db.getAllContacts());
        table.setItems(data);
    }

    private TableColumn<Person, String> setLastName() {
        TableColumn<Person, String> lastNameCol = getColumn("Lastname", 130, "lastName");
        lastNameCol.setOnEditCommit(
                t -> {
                    Person person = t.getTableView().getItems().get(t.getTablePosition().getRow());
                    person.setLastName(t.getNewValue());
                    db.updateContact(person);
                }
        );
        return lastNameCol;
    }

    private TableColumn<Person, String> setFirstName() {
        TableColumn<Person, String> firstNameCol = getColumn("Firstname", 130, "firstName");
        firstNameCol.setOnEditCommit(
                t -> {
                    Person person = t.getTableView().getItems().get(t.getTablePosition().getRow());
                    person.setFirstName(t.getNewValue());
                    db.updateContact(person);
                }
        );
        return firstNameCol;
    }

    private TableColumn<Person, String> setEmail() {
        TableColumn<Person, String> emailCol = getColumn("E-mail address", 250, "email");
        emailCol.setOnEditCommit(
                t -> {
                    Person person = t.getTableView().getItems().get(t.getTablePosition().getRow());
                    person.setEmail(t.getNewValue());
                    db.updateContact(person);
                }
        );
        return emailCol;
    }

    private TableColumn<Person, String> setDelete() {
        TableColumn<Person, String> deleteCol = new TableColumn<>("Delete");
        deleteCol.setCellFactory(param -> getDeleteCell());
        return deleteCol;
    }

    private TableCell<Person, String> getDeleteCell() {
        return new TableCell<>() {
            final Button button = new Button("Delete");

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    button.setOnAction((ActionEvent event) -> {
                        Person person = getTableView().getItems().get(getIndex());
                        data.remove(person);
                        db.removeContact(person);
                    });
                    setGraphic(button);
                }
                setText(null);
            }
        };
    }

    private void setMenuData() {
        TreeItem<String> treeItemRoot = new TreeItem<>("Menu");
        TreeView<String> treeView = new TreeView<>(treeItemRoot);
        treeView.setShowRoot(false);

        TreeItem<String> contactsItem = new TreeItem<>(MENU_CONTACTS);
        TreeItem<String> exitItem = new TreeItem<>(MENU_EXIT);
        contactsItem.setExpanded(true);

        Node contactsNode = new ImageView(new Image(getClass().getResourceAsStream("contacts.png")));
        Node exportNode = new ImageView(new Image(getClass().getResourceAsStream("export.png")));
        TreeItem<String> listItem = new TreeItem<>(MENU_LIST, contactsNode);
        TreeItem<String> printItem = new TreeItem<>(MENU_EXPORT, exportNode);

        contactsItem.getChildren().addAll(listItem, printItem);
        treeItemRoot.getChildren().addAll(contactsItem, exitItem);

        menuPane.getChildren().add(treeView);

        treeView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            String selectedMenu = newValue.getValue();
            if (null != selectedMenu) {
                switch (selectedMenu) {
                    case MENU_CONTACTS:
                        contactPane.setVisible(true);
                        exportPane.setVisible(false);
                        try {
                            newValue.setExpanded(true);
                        } catch (Exception ex) {
                            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, "Something went wrong", ex);
                        }
                        break;
                    case MENU_LIST:
                        contactPane.setVisible(true);
                        exportPane.setVisible(false);
                        break;
                    case MENU_EXPORT:
                        contactPane.setVisible(false);
                        exportPane.setVisible(true);
                        break;
                    case MENU_EXIT:
                        System.exit(0);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void alert(String text) {
        mainSplit.setDisable(true);
        mainSplit.setOpacity(0.4);
        Label label = new Label(text);
        Button alertButton = new Button("OK");
        VBox vBox = new VBox(label, alertButton);
        vBox.setAlignment(Pos.CENTER);

        alertButton.setOnAction(actionEvent -> {
            mainSplit.setDisable(false);
            mainSplit.setOpacity(1);
            vBox.setVisible(false);
        });

        anchorPane.getChildren().add(vBox);
        AnchorPane.setTopAnchor(vBox, 300.0);
        AnchorPane.setLeftAnchor(vBox, 400.0);
    }

    private TableColumn<Person, String> getColumn(String title, int minWidth, String tableColumnName) {
        TableColumn<Person, String> colName = new TableColumn<>(title);
        colName.setMinWidth(minWidth);
        colName.setCellFactory(TextFieldTableCell.forTableColumn());
        colName.setCellValueFactory(new PropertyValueFactory<>(tableColumnName));
        return colName;
    }

}
