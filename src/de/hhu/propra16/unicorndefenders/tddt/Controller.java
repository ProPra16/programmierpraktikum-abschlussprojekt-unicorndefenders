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

import javafx.application.Platform;


public class Controller implements Initializable {

   @FXML
   TextArea codeArea;
   @FXML
   TextArea testArea;
   @FXML
   TextArea compilerMessages;
   @FXML
   Pane showRefactor;
   @FXML
   Pane showBackToRed;
   @FXML
   Label status;
   @FXML
   TableView<MenuEntry> taskMenu;
   ObservableList<MenuEntry> taskMenuData = FXCollections.observableArrayList();

   static String puffer="";                    // zum Zwischenspeichern des Inhalts von testArea bzw. codeArea für BabySteps

   // falls erfolgreich kompiliert wird, soll der Timer aus der count()-Methode in BabyStepsConfig.java gestoppt werden
   static boolean successfullCompiling=false;

   // falls der Timer 0:00 erreicht hat, auf true gesetzt; dann Abbbruch des Threads in BabyStepsAbbruch()
   static boolean finished=false;

   static boolean refactoring=false;  // falls zwischenzeitlich in REFACTOR gewechselt, Abbruch der anderen Threads (alle außer dem Main-Thread) bei BabySteps

   @FXML
   Label babyStepsTimer;              // Timer-Label in der FXML-Datei


   static Cycle cycle;

   // Fuer BabySteps
   static boolean isBabyStepsEnabled;
   static int babyStepsTimeinSeconds;

   static CompilerManager compilerManager;

   static List<Integer> timeList;

   static boolean userPickedTask = false;  // Damit Startbildschirm nicht zum arbeiten verwendet wird



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


                  codeArea.setText(codeList.get(0).getContent());
                  testArea.setText(testList.get(0).getContent());

                  babyStepsHandling();       // ggf. BabySteps
                  babyStepsAbbruch();        // ggf. Abbruch von BabySteps

               }

            }
            taskMenu.getSelectionModel().clearSelection();
            status.setText("RED");
            testArea.setEditable(true);
            codeArea.setEditable(false);
            testArea.setStyle("-fx-border-color: #DF0101;");
            userPickedTask = true;
         }

      });




   }


   /*
    * Leitet das Kompilieren der Usereingaben ein und gibt im Fehlerfall entsprechende Meldungen in ein TextArea aus
    *
    */
   public void compileCode(){
      if(userPickedTask) {
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

               // wenn erfolgreiche Kompilierung, dann Abbruch des Timers für BabySteps
               successfullCompiling = true;

            } else {
               System.out.println("NOP");
               String message = compilerMessages.getText();
               Collection<CompileError> errorsCode = compilerManager.getSourceFile().getCompilerErrors();
               Collection<CompileError> errorsTest = compilerManager.getTestFile().getCompilerErrors();
               message = message + "Compile-Errors - Code:\n";
               if (errorsCode != null) {
                  for (CompileError cmpErr : errorsCode) {
                     System.out.println(cmpErr.getMessage());
                     message = message + cmpErr.getMessage() + "\n";
                  }
               }
               message = message + "Compile-Errors - Test:\n";
               if (errorsTest != null) {
                  for (CompileError cmpErr : errorsTest) {
                     System.out.println(cmpErr.getMessage());
                     message = message + cmpErr.getMessage() + "\n";
                  }
               }
               compilerMessages.setText(message);

            }
         } catch (Exception e) {
            System.out.println(e.getMessage());
         }
      }
   }


   /*
    * Ueberprueft, ob User die Phasen wechseln kann und gibt je nach Ergebnis entsprechende Meldungung aus und
    * initialisiert Uebergang
    *
    */
   public void next(){
      if (userPickedTask) {

         compileCode();

         // Je nach laufender Phase muss anders reagiert werden.
         if (cycle == GREEN) {
            if (compilerManager.wasTestSuccessfull()) {
               // Nach erfolgreicher gruenen Phase gibt es die Moeglichkeit, in Refactor zu wechseln
               // Frage dies ueber ein Alert ab
               Alert checkRefactor = new Alert(Alert.AlertType.CONFIRMATION);
               checkRefactor.setContentText("In den Modus REFACTOR wechseln?");
               boolean goToRefactor = false;
               Optional<ButtonType> pickedOption = checkRefactor.showAndWait();
               if (pickedOption.get() == ButtonType.OK) {
                  goToRefactor = true;
               }

               if (goToRefactor) {
                  cycle = REFACTOR;
                  status.setText("REFACTOR");
                  testArea.setEditable(true);
                  testArea.setStyle("-fx-border-color:#A4A4A4");
                  codeArea.setStyle("-fx-border-color:#A4A4A4");

                  // Mittels neuem Button in der Menuleiste oben kommt man wieder zurueck zur Phase RED
                  Button endRefactor = new Button("Refactor beenden");
                  showRefactor.getChildren().add(endRefactor);

                  endRefactor.setOnMousePressed(new EventHandler<MouseEvent>() {
                     @Override
                     public void handle(MouseEvent event) {
                        if (event.getButton() == MouseButton.PRIMARY) {

                           compileCode();

                           // Man darf nur wechseln, wenn alle Teste erfolgreich sind
                           if (compilerManager.wasTestSuccessfull()) {

                              testArea.setStyle("-fx-border-color: #DF0101;");
                              codeArea.setStyle("-fx-border-color: #A4A4A4;");
                              testArea.setEditable(true);
                              codeArea.setEditable(false);
                              cycle = RED;
                              status.setText("RED");
                              if (isBabyStepsEnabled) {    // BabySteps durchführen und dann ggf. abbrechen
                                 babyStepsHandling();
                                 babyStepsAbbruch();
                              }
                              showRefactor.getChildren().clear();
                           }
                        }
                     }
                  });

               } else {
                  testArea.setStyle("-fx-border-color: #DF0101;");
                  codeArea.setStyle("-fx-border-color: #A4A4A4;");
                  testArea.setEditable(true);
                  codeArea.setEditable(false);
                  cycle = RED;
                  status.setText("RED");
                  if (isBabyStepsEnabled) {    // BabySteps durchführen und dann ggf. abbrechen
                     babyStepsHandling();
                     babyStepsAbbruch();
                  }
               }
            } else {
               if (compilerManager.wasCompilerSuccessfull()) {
                  String msg = compilerMessages.getText();
                  msg = msg + "Fehler wurden noch nicht behoben :(\n ";
                  compilerMessages.setText(msg);
               } else {
                  String msg = compilerMessages.getText();
                  msg = msg + "Kompilieren fehlgeschlagen\n ";
                  compilerMessages.setText(msg);
               }
            }
         } else if (cycle == RED) {
            if (compilerManager.wasTestSuccessfull()) {
               codeArea.setStyle("-fx-border-color: #088A08;");
               testArea.setStyle("-fx-border-color: #A4A4A4;");
               codeArea.setEditable(true);
               testArea.setEditable(false);
               Button backToRed = new Button("BACK TO RED");
               createBackToRedButton();
               cycle = GREEN;
               status.setText("GREEN");
               if (isBabyStepsEnabled) {    // BabySteps durchführen und dann ggf. abbrechen
                  babyStepsHandling();
                  babyStepsAbbruch();
               }
            } else {
               if (compilerManager.wasCompilerSuccessfull()) {
                  String msg = compilerMessages.getText();
                  msg = msg + "Es muss genau ein Test fehlschlagen, um in Phase GREEN zu wechseln\n";
                  compilerMessages.setText(msg);
               } else {  // Wenn Code nicht kompiliert soll auch gewechselt werden
                  String msg = compilerMessages.getText();
                  msg = msg + "Kompilieren fehlgeschlagen\n ";
                  compilerMessages.setText(msg);
                  codeArea.setStyle("-fx-border-color: #088A08;");
                  testArea.setStyle("-fx-border-color: #A4A4A4;");
                  codeArea.setEditable(true);
                  testArea.setEditable(false);
                  createBackToRedButton();
                  cycle = GREEN;
                  status.setText("GREEN");
                  if (isBabyStepsEnabled) {    // BabySteps durchführen und dann ggf. abbrechen
                     babyStepsHandling();
                     babyStepsAbbruch();
                  }
               }
            }
         }
      }

   }


   public void createBackToRedButton(){
      Button backToRed = new Button("BACK TO RED");
      showBackToRed.getChildren().add(backToRed);
      backToRed.setOnMousePressed(new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent event) {
            if(event.getButton() == MouseButton.PRIMARY){
               testArea.setStyle("-fx-border-color: #DF0101;");
               codeArea.setStyle("-fx-border-color: #A4A4A4;");
               testArea.setEditable(true);
               codeArea.setEditable(false);
               showBackToRed.getChildren().clear();
               cycle = RED;
               status.setText("RED");
               if(isBabyStepsEnabled) {    // BabySteps durchführen und dann ggf. abbrechen
                  babyStepsHandling();
                  babyStepsAbbruch();
               }


            }
         }
      });
   }



   public void babyStepsHandling() {  // führt BabySteps aus

      // etwaigen vorigen Thread aus der count-Methode in BabyStepsConfig.java stoppen, damit dieser nicht weiterläuft
      // und nicht doppelt runtergezählt wird
      BabyStepsConfig.stopThread = true;

      if (isBabyStepsEnabled && cycle != REFACTOR) {   // BabySteps nur, wenn eingeschaltet und nicht in der Refactoring-Phase

         // damit stopThread im Thread der count()-Methode in BabyStepsConfig.java wirken kann, eine Sekunde warten,
         // da dort auch stets eine Sekunde gewartet wird; stopthread wird nämlich weiter unten wiederauf false gesetzt,
         // damit ein neuer Thread in count() starten kann
         try{
            Thread.sleep(1000);
         }
         catch (InterruptedException e) {
            e.printStackTrace();
         }

         if (cycle == RED) {                                 // alten Inhalt des Test-Editors speichern
            puffer = testArea.getText();
         }
         if (cycle == GREEN) {                               // alten Inhalt des Code-Editors speichern
            puffer = codeArea.getText();
         }

         BabyStepsConfig.init(babyStepsTimeinSeconds, babyStepsTimer); // Anfangswert des Labels setzen

         // weiter oben auf true gesetzt, damit ein eventuell noch laufender alter Thread abbricht
         // damit neuer Thread  in BabyStepsConfig.count() möglich, jetzt wieder auf false gesetzt
         BabyStepsConfig.stopThread = false;
         BabyStepsConfig.sec = babyStepsTimeinSeconds;           // Anzahl Sekunden setzen
         BabyStepsConfig.count(babyStepsTimer);                  // Timer runterzählen



      }
      else{                                                            // wenn babySteps=nein oder Refactoring-Phase
         BabyStepsConfig.init(babyStepsTimeinSeconds, babyStepsTimer); // damit das Label an StringProperty sp gebunden wird
         BabyStepsConfig.sp.setValue("");                              // sp auf leer gesetzt
      }

   }

   // Methode setzt den Zustand der TextAreas zurück, falls die Zeit abgelaufen ist d.h. falls der Timer aus
   // BabyStepsConfig.count() 0:00 erreicht
   public void babyStepsAbbruch(){

      Thread t = new Thread(() -> {             // weiterer Thread, damit parallel Aktionen möglich sind
         while (true) {
            if(babyStepsTimer.getText().equals("0:01")) {
               try {
                  // dann 0:00 im Thread von BabyStepsConfig.count() erreicht
                  // in der if-Bedingung wird bewusst nicht 0:00 verwendet, da der andere Thread früher startet
                  // und die Threads deshalb zeitlich versetzt laufen
                  Thread.sleep(1000);
               }
               catch(InterruptedException e){
                  e.printStackTrace();
               }
               if (cycle == RED) {

                  testArea.setText(puffer);        // Test-Editor-Inhalt zurücksetzen, da Zeit abgelaufen

               }
               if (cycle == GREEN) {

                  codeArea.setText(puffer);        // Code-Editor-Inhalt zurücksetzen, da Zeit abgelaufen

               }
               break;                              // wenn die Zeit abgelaufen ist, auch diese Schleife abbrechen
            }
            if (cycle==REFACTOR){      // wenn zwischenzeitlich REFACTOR gewählt wurde, alle Nicht-Main-Threads abbrechen
               refactoring=true;       // führt zum Abbruch des Threads in BabyStepsConfig.count()
               break;
            }

            // damit Thread nicht umsonst weiter läuft, Abbruch, sobald BabyStepsConfig.count() auch fertig
            // zB. im Falle einer erfolgreichen Kompilierung
            if(finished){
               finished=false;
               break;
            }

         }

         // wenn die Zeit abgelaufen ist, wird über next() in die Phase zuvor gewechselt
         // So kann man es machen, wechselt aber nur einmal:
         /*
         if(babyStepsTimer.getText().equals("0:00")){
            Platform.runLater( () ->next());
         }*/
      });

      t.start();  // Starte den Thread t

   }


}
