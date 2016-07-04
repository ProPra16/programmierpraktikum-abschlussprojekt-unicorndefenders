package de.hhu.propra16.unicorndefenders.tddt;

import de.hhu.propra16.unicorndefenders.tddt.config.Catalog;
import de.hhu.propra16.unicorndefenders.tddt.config.ConfigParser;
import de.hhu.propra16.unicorndefenders.tddt.config.Exercise;
import de.hhu.propra16.unicorndefenders.tddt.files.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import org.xml.sax.SAXException;
import vk.core.api.CompileError;

import javax.xml.parsers.ParserConfigurationException;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import static de.hhu.propra16.unicorndefenders.tddt.Cycle.GREEN;
import static de.hhu.propra16.unicorndefenders.tddt.Cycle.RED;
import javafx.application.Platform;

public class Controller implements Initializable {

   @FXML
   TextArea codeArea;
   @FXML
   TextArea testArea;
   @FXML
   TextArea compilerMessages;
   @FXML
   HBox tabManager;
   @FXML
   TableView<MenuEntry> taskMenu;
   ObservableList<MenuEntry> taskMenuData = FXCollections.observableArrayList();



   Cycle cycle;

   // Fuer BabySteps
   static boolean isBabyStepsEnabled;
   static int babyStepsTimeinSeconds;



   // Dummy-Methoden


   @Override
   public void initialize(URL location, ResourceBundle resources) {

      cycle = RED;

      try{
         FilesystemFile catalogFile = new FilesystemFile("test.xml");
         ConfigParser configParser= new ConfigParser(catalogFile);
         configParser.parse();
         Catalog catalog = configParser.getCatalog();
         List<Exercise> exercises = catalog.getExercises();


         for(Exercise e : exercises){
            if(e.isBabystepsEnabled())
               taskMenuData.add(new MenuEntry(e.getName(), "Ja", e));
            else
               taskMenuData.add(new MenuEntry(e.getName(), "Nein", e));
         }
      }catch (FileNotFoundException e){
         Alert alert = new Alert(Alert.AlertType.ERROR);
         alert.setTitle("Schade");
         alert.setContentText("Der Katalog wurde nicht gefunden :(");
         alert.showAndWait();
      } catch (SAXException e) {
         e.printStackTrace();
      } catch (ParserConfigurationException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      } catch (Exception e) {
         e.printStackTrace();
      }

      taskMenu.setItems(taskMenuData);



      taskMenu.setOnMousePressed(new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent event) {
            if (event.getButton() == MouseButton.PRIMARY){
               MenuEntry selectedEntry = taskMenu.getSelectionModel().getSelectedItem();
              // Menuevent.getSource();
               if(selectedEntry != null) {
                  System.out.println(selectedEntry.getTaskTitle());


                  Exercise selectedExcercise = selectedEntry.getExercise();

                  isBabyStepsEnabled = selectedExcercise.isBabystepsEnabled();
                  babyStepsTimeinSeconds = selectedExcercise.getBabystepsMaxTimeInSeconds();

                  List<File> codeList = selectedExcercise.getClassTemplate();
                  List<File> testList = selectedExcercise.getTestTemplate();
                  tabManager.getChildren().clear();

                  for(int i = 0; i < codeList.size(); i++){
                     TabButton button = new TabButton(Integer.toString(i+1), codeList.get(i), testList.get(i));
                     button.setOnMousePressed(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                           if(event.getButton()== MouseButton.PRIMARY){
                              TabButton clickedButton = (TabButton) event.getSource();
                              codeArea.setText(clickedButton.getCode().getContent());
                              testArea.setText(clickedButton.getTest().getContent());


                           }
                        }
                     });
                     tabManager.getChildren().add(button);
                     codeArea.setText(codeList.get(0).getContent());
                     testArea.setText(testList.get(0).getContent());
                  }

               }
            }
            taskMenu.getSelectionModel().clearSelection();
         }

      });

   babyStepsHandling();


   }

   public void compileCode(){
      try {
         File myCode = new File("MyDet", codeArea.getText());
         File myTest = new File("MyDetTest", testArea.getText());
         CompilerManager compilerManager = new CompilerManager(myCode, myTest, cycle);
         compilerManager.run();
         if (compilerManager.wasCompilerSuccessfull()) {
            System.out.println("Success");
             successfullCompiling=true;
         } else {
            System.out.println("NOP");
            String message = "";
            Collection<CompileError> errorsCode = compilerManager.getSourceFile().getCompilerErrors();
            Collection<CompileError> errorsTest = compilerManager.getTestFile().getCompilerErrors();
            message = message+"Compile-Errors - Code:\n";
            if(errorsCode != null) {
               for (CompileError cmpErr : errorsCode) {
                  System.out.println(cmpErr.getMessage());
                  message = message + cmpErr.getMessage() + "\n";
               }
            }
            message = message+"Compile-Errors - Test:\n";
            if(errorsTest != null) {
               for (CompileError cmpErr : errorsTest) {
                  System.out.println(cmpErr.getMessage());
                  message = message + cmpErr.getMessage() + "\n";
               }
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
         babyStepsHandling();
      }
      else if (cycle == GREEN){
         codeArea.setStyle("-fx-border-color: #088A08;");
         testArea.setStyle("-fx-border-color: #DF0101;");
         codeArea.setEditable(true);
         testArea.setEditable(false);
         cycle = RED;
         babyStepsHandling();
      }
   }


    static String puffer="";
    static boolean successfullCompiling=false;
    @FXML
    static Label babyStepsTimer;

    public void babyStepsHandling(){


        if(isBabyStepsEnabled&&cycle!=REFACTOR) {          // BabySteps nur, wenn eingeschaltet und nicht in der Refactoring-Phase
            if(cycle==RED){                                 // alten Zustand des Test-Editors speichern
                puffer=testArea.getText();
            }
            if(cycle==GREEN){                               // alten Zustand des Code-Editors speichern
                puffer=codeArea.getText();
            }

            babyStepsTimer = BabyStepsConfig.init(babyStepsTimeinSeconds); // Anfangswert des Labels setzen
            BabyStepsConfig.sec=babyStepsTimeinSeconds;           // Anzahl Sekunden setzen
            BabyStepsConfig.count(babyStepsTimer);                         // Timer runterzählen

            Thread t = new Thread(() -> {         // damit parallel Aktionen möglich sind, neuen Thread erstellen
                while (true) {

                    if(successfullCompiling){   // wenn Kompilierung erfolreich war, dann Schleife abbrechen
                       successfullCompiling=false;
                       break;
                    }


                    if (!babyStepsTimer.getText().equals("0:00")) {
                        if(cycle == RED){
                            Platform.runLater( () -> testArea.setText(puffer)); // Test-Editor-Inhalt zurücksetzen
                            babyStepsHandling();                                // falls Zeit abgelaufen, wird die Zeit erneut runtergezählt

                        }
                        if(cycle == GREEN){
                            Platform.runLater( () -> codeArea.setText(puffer));          // Code-Editor-Inhalt zurücksetzen
                            babyStepsHandling();                                         // falls Zeit abgelaufen, wird die Zeit erneut runtergezählt

                        }
                        break;   // Schleife abbrechen, sobald die Zeit abgelaufen ist
                    }
                }

            });

            t.start();
        }
        else {
            babyStepsTimer.setText("");   // wenn man in der Refactoring-Phase ist oder BabySteps ausgeschaltet ist, dann leeres Label
        }
    }

}
