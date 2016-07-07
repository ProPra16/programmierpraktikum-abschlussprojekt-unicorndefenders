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
import javafx.scene.layout.Pane;
import org.xml.sax.SAXException;
import vk.core.api.CompileError;

import javax.xml.parsers.ParserConfigurationException;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static de.hhu.propra16.unicorndefenders.tddt.Cycle.GREEN;
import static de.hhu.propra16.unicorndefenders.tddt.Cycle.RED;
import static de.hhu.propra16.unicorndefenders.tddt.Cycle.REFACTOR;

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
   Pane showRefactor;
   @FXML
   TableView<MenuEntry> taskMenu;
   ObservableList<MenuEntry> taskMenuData = FXCollections.observableArrayList();



   Cycle cycle;

   // Fuer BabySteps
   boolean isBabyStepsEnabled;
   int babyStepsTimeinSeconds;

   CompilerManager compilerManager;

   List<Integer> timeList;



   // Dummy-Methoden


   @Override
   public void initialize(URL location, ResourceBundle resources) {

      // Erste Phase: Test editieren
      cycle = RED;

      // initialer Menuaufbau
      try{
         // Parsen
         FilesystemFile catalogFile = new FilesystemFile("test.xml");
         ConfigParser configParser= new ConfigParser(catalogFile);
         configParser.parse();

         // Einhaengen der Aufgaben aus dem Katalog
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


      // Klickhandler fuer das Menu
      taskMenu.setOnMousePressed(new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent event) {
            if (event.getButton() == MouseButton.PRIMARY){
               // Bestimme den ausgewaehlten Eintrag
               MenuEntry selectedEntry = taskMenu.getSelectionModel().getSelectedItem();

               //Es kann sein, dass User nicht auf existenten Eintrag klickt
               if(selectedEntry != null) {

                  Exercise selectedExcercise = selectedEntry.getExercise();

                  // Konfigurationsdaten lesen

                  isBabyStepsEnabled = selectedExcercise.isBabystepsEnabled();
                  babyStepsTimeinSeconds = selectedExcercise.getBabystepsMaxTimeInSeconds();


                  List<File> codeList = selectedExcercise.getClassTemplate();
                  List<File> testList = selectedExcercise.getTestTemplate();

                  // Alte Tabs sind nun redundant
                  tabManager.getChildren().clear();

                  // Stelle die Tabs zur Navigation innerhalb der jeweiligen Klassen der Aufgabe auf
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

                  }
                  // Zu Beginn soll die erste Klasse der Aufgabe angezeigt werden
                  codeArea.setText(codeList.get(0).getContent());
                  testArea.setText(testList.get(0).getContent());

               }
            }
            taskMenu.getSelectionModel().clearSelection();
         }

      });




   }


   /*
    * Leitet das Kompilieren der Usereingaben ein und gibt im Fehlerfall entsprechende Meldungen in ein TextArea aus
    *
    */
   public void compileCode(){
      try {
         // Baue zur Weiterleitung File-Instanzen
         File myCode = new File("MyDet", codeArea.getText());
         File myTest = new File("MyDetTest", testArea.getText());

         // Neuer Compiler-Manager fuer das Kompilieren
         compilerManager = new CompilerManager(myCode, myTest, cycle);
         compilerManager.run();

         // Im Fehlerfall werden die Compiler-Meldungen in das untere Feld geschrieben
         if (compilerManager.wasCompilerSuccessfull()) {
            System.out.println("Success");
         } else {
            System.out.println("NOP");
            String message = compilerMessages.getText();
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


   /*
    * Ueberprueft, ob User die Phasen wechseln kann und gibt je nach Ergebnis entsprechende Meldungung aus und
    * initialisiert Uebergang
    *
    */
   public void next(){
      compileCode();

      // Je nach laufender Phase muss anders reagiert werden.
      if(cycle == GREEN) {
         if(compilerManager.wasTestSuccessfull()) {
            // Nach erfolgreicher gruenen Phase gibt es die Moeglichkeit, in Refactor zu wechseln
            // Frage dies ueber ein Alert ab
            Alert checkRefactor = new Alert(Alert.AlertType.CONFIRMATION);
            checkRefactor.setContentText("In den Modus REFACTOR wechseln?");
            boolean goToRefactor = false;
            Optional<ButtonType> pickedOption = checkRefactor.showAndWait();
            if(pickedOption.get() == ButtonType.OK){
               goToRefactor = true;
            }

            if(goToRefactor){
               cycle = REFACTOR;
               testArea.setEditable(true);
               testArea.setStyle("-fx-border-color:#A4A4A4");
               codeArea.setStyle("-fx-border-color:#A4A4A4");

               // Mittels neuem Button in der Menuleiste oben kommt man wieder zurueck zur Phase RED
               Button endRefactor = new Button("Refactor beenden");
               showRefactor.getChildren().add(endRefactor);

               endRefactor.setOnMousePressed(new EventHandler<MouseEvent>() {
                  @Override
                  public void handle(MouseEvent event) {
                     if(event.getButton() == MouseButton.PRIMARY){

                        compileCode();

                        // Man darf nur wechseln, wenn alle Teste erfolgreich sind
                        if(compilerManager.wasTestSuccessfull()) {

                           testArea.setStyle("-fx-border-color: #DF0101;");
                           codeArea.setStyle("-fx-border-color: #A4A4A4;");
                           testArea.setEditable(true);
                           codeArea.setEditable(false);
                           cycle = RED;
                           showRefactor.getChildren().clear();
                        }
                     }
                  }
               });

            }else {
               testArea.setStyle("-fx-border-color: #DF0101;");
               codeArea.setStyle("-fx-border-color: #A4A4A4;");
               testArea.setEditable(true);
               codeArea.setEditable(false);
               cycle = RED;
            }
         }
         else{
            if (compilerManager.wasCompilerSuccessfull() ){
               String msg = compilerMessages.getText();
               msg = msg + "Fehler wurden noch nicht behoben :(\n ";
               compilerMessages.setText(msg);
            } else {
               String msg = compilerMessages.getText();
               msg = msg + "Kompilieren fehlgeschlagen\n ";
               compilerMessages.setText(msg);
            }
         }
      }
      else if (cycle == RED){
         if(compilerManager.wasTestSuccessfull()) {
            codeArea.setStyle("-fx-border-color: #088A08;");
            testArea.setStyle("-fx-border-color: #A4A4A4;");
            codeArea.setEditable(true);
            testArea.setEditable(false);
            cycle = GREEN;
         }
         else{
            if (compilerManager.wasCompilerSuccessfull()) {
               String msg = compilerMessages.getText();
               msg = msg + "Es muss genau ein Test fehlschlagen, um in Phase GREEN zu wechseln\n";
               compilerMessages.setText(msg);
            }else{
               String msg = compilerMessages.getText();
               msg = msg + "Kompilieren fehlgeschlagen\n ";
               compilerMessages.setText(msg);
            }
         }
      }
   }



}
