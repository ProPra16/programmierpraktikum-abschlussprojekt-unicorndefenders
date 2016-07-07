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
   HBox tabManager;
   @FXML
   TableView<MenuEntry> taskMenu;
   ObservableList<MenuEntry> taskMenuData = FXCollections.observableArrayList();



   static Cycle cycle;

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

                  babyStepsHandling();       // ggf. BabySteps
                  babyStepsAbbruch();        // ggf. Abbruch von BabySteps

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




   }

   public void compileCode(){
      try {
         File myCode = new File("MyDet", codeArea.getText());
         File myTest = new File("MyDetTest", testArea.getText());
         CompilerManager compilerManager = new CompilerManager(myCode, myTest, cycle);
         compilerManager.run();
         if (compilerManager.wasCompilerSuccessfull()) {
            System.out.println("Success");

            // wenn erfolgreiche Kompilierung, dann Abbruch des Timers für BabySteps
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
         if(isBabyStepsEnabled){    // BabySteps durchführen und dann ggf. abbrechen
            babyStepsHandling();
            babyStepsAbbruch();
         }


      }
      else if (cycle == GREEN){
         codeArea.setStyle("-fx-border-color: #088A08;");
         testArea.setStyle("-fx-border-color: #DF0101;");
         codeArea.setEditable(true);
         testArea.setEditable(false);
         cycle = RED;
         if(isBabyStepsEnabled){    // BabySteps durchführen und dann ggf. abbrechen
            babyStepsHandling();
            babyStepsAbbruch();
         }
      }
   }


   static String puffer="";                    // zum Zwischenspeichern des Inhalts von testArea bzw. codeArea

   // falls erfolgreich kompiliert wird, soll der Timer aus der count()-Methode in BabyStepsConfig.java gestoppt werden
   static boolean successfullCompiling=false;

   // falls der Timer 0:00 erreicht hat, auf true gesetzt; dann Abbbruch des Threads in BabyStepsAbbruch()
   static boolean finished=false;

   static boolean refactoring=false;  // falls zwischenzeitlich in REFACTOR gewechselt, Abbruch der anderen Threads (alle außer dem Main-Thread)

   @FXML
   Label babyStepsTimer;              // Timer-Label in der FXML-Datei

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
