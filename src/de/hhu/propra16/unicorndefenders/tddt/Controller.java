package de.hhu.propra16.unicorndefenders.tddt;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

public class Controller {

    @FXML
    TextArea codeArea;
    @FXML
    TextArea testArea;


    // Dummy-Methoden

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

    public void click(){

    }


}
