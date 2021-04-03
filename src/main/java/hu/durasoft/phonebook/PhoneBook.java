package hu.durasoft.phonebook;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PhoneBook extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = new FXMLLoader(PhoneBook.class.getResource("View.fxml")).load();
        Scene scene = new Scene(root);
        stage.setTitle("Phonebook");
        stage.setWidth(800);
        stage.setHeight(680);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
