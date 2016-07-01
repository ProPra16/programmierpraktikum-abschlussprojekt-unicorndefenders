package de.hhu.propra16.unicorndefenders.tddt;

import de.hhu.propra16.unicorndefenders.tddt.files.CompilerManager;
import de.hhu.propra16.unicorndefenders.tddt.files.File;
import de.hhu.propra16.unicorndefenders.tddt.files.Source;
import de.hhu.propra16.unicorndefenders.tddt.files.TestCode;
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
import vk.core.api.CompileError;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

import static de.hhu.propra16.unicorndefenders.tddt.Cycle.GREEN;
import static de.hhu.propra16.unicorndefenders.tddt.Cycle.RED;

public class Controller implements Initializable {

   @FXML
   TextArea codeArea;
   @FXML
   TextArea testArea;
   @FXML
   TextArea compilerMessages;
   @FXML
   TableView<MenuEntry> taskMenu;
   ObservableList<MenuEntry> taskMenuData = FXCollections.observableArrayList();



   Cycle cycle;



   // Dummy-Methoden


   @Override
   public void initialize(URL location, ResourceBundle resources) {

      cycle = RED;

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

   public void compileCode(){
      try {
         File myCode = new File("MyDet", codeArea.getText());
         File myTest = new File("MyDetTest", testArea.getText());
         CompilerManager compilerManager = new CompilerManager(myCode, myTest, cycle);
         compilerManager.run();
         if (compilerManager.wasCompilerSuccessfull()) {
            System.out.println("Success");
         } else {
            System.out.println("NOP");
            String message = "";
            Collection<CompileError> errors = compilerManager.getSourceFile().getCompilerErrors();
            for (CompileError cmpErr : errors) {
               System.out.println(cmpErr.getMessage());
               message = message + cmpErr.getMessage() + "\n";
            }
            compilerMessages.setText(message);

         }
      } catch (Exception e){
         System.out.println(e.getMessage());
      }

   }

   public void loadCatalog(){
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Test");
      alert.setContentText("erfolgreich");
      alert.showAndWait();
   }

   public void next(){
      if(cycle == RED) {
         testArea.setStyle("-fx-border-color: #088A08;");
         codeArea.setStyle("-fx-border-color: #DF0101;");
         testArea.setEditable(true);
         codeArea.setEditable(false);
         cycle = GREEN;
      }
      else if (cycle == GREEN){
         codeArea.setStyle("-fx-border-color: #088A08;");
         testArea.setStyle("-fx-border-color: #DF0101;");
         codeArea.setEditable(true);
         testArea.setEditable(false);
         cycle = RED;
      }
   }



}
