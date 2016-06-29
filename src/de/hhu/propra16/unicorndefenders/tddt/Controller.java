package de.hhu.propra16.unicorndefenders.tddt;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    TextArea codeArea;
    @FXML
    TextArea testArea;
    @FXML
    TableView<MenuEntry> taskMenu;
    ObservableList<MenuEntry> taskMenuData = FXCollections.observableArrayList();



    // Dummy-Methoden


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        taskMenuData.add(new MenuEntry("A1", "Ja"));
        taskMenuData.add(new MenuEntry("A2", "Nein"));
        taskMenu.setItems(taskMenuData);

        taskMenu.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY){
                    MenuEntry selectedEntry = taskMenu.getSelectionModel().getSelectedItem();
                    System.out.println(selectedEntry.getTaskTitle());
                }
            }
        });




    }

    public void loadCatalog(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Test");
        alert.setContentText("erfolgreich");
        alert.showAndWait();
    }

    public void next(){
        testArea.setText("YAAAAY");
        testArea.setStyle("-fx-border-color: #088A08;");
        codeArea.setStyle("-fx-border-color: #DF0101;");
        testArea.setEditable(true);
        codeArea.setEditable(false);
    }



}
