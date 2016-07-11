package de.hhu.propra16.unicorndefenders.tddt;

import de.hhu.propra16.unicorndefenders.tddt.config.Catalog;
import de.hhu.propra16.unicorndefenders.tddt.config.ConfigParser;
import de.hhu.propra16.unicorndefenders.tddt.config.Exercise;
import de.hhu.propra16.unicorndefenders.tddt.files.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.xml.sax.SAXException;
import vk.core.api.CompileError;
import vk.core.api.TestFailure;
import vk.core.api.TestResult;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static de.hhu.propra16.unicorndefenders.tddt.Cycle.GREEN;
import static de.hhu.propra16.unicorndefenders.tddt.Cycle.RED;
import static de.hhu.propra16.unicorndefenders.tddt.Cycle.REFACTOR;

/**
 * Interaktion zwischen grafischer Oberflaeche und Nutzer
 * Steuerung des Programmflusses
 *
 * @author Alessandra
 */
public class Controller implements Initializable {

   @FXML
   TextArea codeArea;
   @FXML
   TextArea testArea;
   @FXML
   TextArea compilerMessages;
   @FXML
   Button backToRed;
   @FXML
   Button refactor;
   @FXML
   Label status;
   @FXML
   Button compile;
   @FXML
   Button next;
   @FXML
   Button tracking;
   @FXML
   TableView<MenuEntry> taskMenu;
   ObservableList<MenuEntry> taskMenuData = FXCollections.observableArrayList();

   static Cycle cycle;        // aktueller Zyklus

   static CompilerManager compilerManager;      // Infovermittler bzgl. Tests und Compiler zwischen den Methoden

   static ArrayList<TrackPoint> trackList = new ArrayList<>();

   static File codeBuffer;   // speichert Code, für den Fall, dass User von GREEN nach RED zurueckspringen will





   /*
    *   Variablen fuer BabySteps
    */

   @FXML
   Label babyStepsTimer;              // Timer-Label in der FXML-Datei

   static boolean isBabyStepsEnabled;
   static int babyStepsTimeinSeconds;

   // falls der Timer 0:00 erreicht hat, auf true gesetzt; dann Abbbruch des Threads in BabyStepsAbbruch()
   static boolean finished=false;

   static boolean refactoring=false;  // falls zwischenzeitlich in REFACTOR gewechselt, Abbruch der anderen Threads (alle außer dem Main-Thread) bei BabySteps

   static String puffer="";                    // zum Zwischenspeichern des Inhalts von testArea bzw. codeArea für BabySteps

   // falls erfolgreich kompiliert wird, soll der Timer aus der count()-Methode in BabyStepsConfig.java gestoppt werden
   static boolean successfullCompiling=false;



   /*
    * Wird bei Programmaufruf ausgefuehrt:
    * - Lässt Katalog parsen und baut Auswahlmenuleiste mit entsprechenden Aufgaben
    */
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

                  status.setText("RED");
                  testArea.setEditable(true);
                  codeArea.setEditable(false);
                  testArea.setStyle("-fx-border-color: #DF0101;");
                  compile.setDisable(false);
                  next.setDisable(false);
                  tracking.setDisable(false);

                  // neue Aufgabe angefangen, also werden die Zeiten der alten Aufgabe geloescht
                  trackList = new ArrayList<TrackPoint>();

                  StoppUhr.starten();

                  babyStepsHandling();       // ggf. BabySteps
                  babyStepsAbbruch();        // ggf. Abbruch von BabySteps

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
         File myCode = new File(ClassNameExtractor.getClassName(codeArea.getText()), codeArea.getText());
         File myTest = new File(ClassNameExtractor.getClassName(testArea.getText()), testArea.getText());

         // Neuer Compiler-Manager fuer das Kompilieren
         compilerManager = new CompilerManager(myCode, myTest, cycle);
         compilerManager.run();

         String message = "";
         // Im Fehlerfall werden die Compiler-Meldungen in das untere Feld geschrieben
         if (compilerManager.wasCompilerSuccessfull()) {
            message = "Compiling: Successful! :)\n";

            // wenn erfolgreiche Kompilierung, dann Abbruch des Timers für BabySteps
            successfullCompiling = true;

         } else {

            message = "Compiling: Not so successful! :(\n";

            Collection<CompileError> errorsCode = compilerManager.getSourceFile().getCompilerErrors();
            Collection<CompileError> errorsTest = compilerManager.getTestFile().getCompilerErrors();

            if (errorsCode != null) {
               if(errorsCode.size() > 0)
                  message = message + "-------------------------Compile-Errors - Code:\n";
               for (CompileError cmpErr : errorsCode) {
                  message = message + cmpErr.toString() + "\n";
               }
            }

            if (errorsTest != null) {
               if(errorsTest.size() > 0)
                  message = message + "-------------------------Compile-Errors - Test:\n";
               for (CompileError cmpErr : errorsTest) {
                  message = message + cmpErr.toString() + "\n";
               }
            }

         }
         compilerMessages.setText(message);
      } catch (Exception e) {
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
               StoppUhr.beenden();
               trackList.add(new TrackPoint(StoppUhr.zeit(), GREEN));

               StoppUhr.starten();

               backToRed.setDisable(true);
               cycle = REFACTOR;
               status.setText("REFACTOR");
               testArea.setEditable(true);
               testArea.setStyle("-fx-border-color:#A4A4A4");
               codeArea.setStyle("-fx-border-color:#A4A4A4");

               // Button fuer neuen Phasenbeginn freigeben
               refactor.setDisable(false);
               next.setDisable(true);

            } else {
               StoppUhr.beenden();
               trackList.add(new TrackPoint(StoppUhr.zeit(), GREEN));
               initRedMode();
            }
         } else {
            if (compilerManager.wasCompilerSuccessfull()) {
               String msg = compilerMessages.getText();
               msg = msg + "Es gibt noch fehlschlagene Teste :(\nErst nach dem Beheben kann gewechselt werden:\n ";
               compilerMessages.setText(msg);
               showFailedTests();
            } else {
               String msg = compilerMessages.getText();
               msg = msg + "Kompilieren fehlgeschlagen\n ";
               compilerMessages.setText(msg);
            }
         }
      } else if (cycle == RED) {
         if (compilerManager.wasTestSuccessfull() || !compilerManager.wasCompilerSuccessfull()) {
            StoppUhr.beenden();
            trackList.add(new TrackPoint(StoppUhr.zeit(), RED));
            initGreenMode();
         } else {
            String msg = compilerMessages.getText();
            msg = msg + "Es muss genau ein Test fehlschlagen, um in Phase GREEN zu wechseln:\n";
            compilerMessages.setText(msg);
            showFailedTests();

         }
      }


   }

   public void showFailedTests(){
      String msg = compilerMessages.getText();
      Collection<TestFailure> failedTests= compilerManager.getTestFile().getTestFailures();
      if(failedTests.size() > 0) {
         msg = msg + "Fehlgeschlagene Teste:\n\n";
         for (TestFailure failure : failedTests)
            msg = msg + failure.getMethodName() + "\n" + failure.getMessage()+"\n\n";
      } else {
         msg = msg + "Keine fehlgeschlagenen Teste.\n";
      }
      compilerMessages.setText(msg);
   }


   /*
    * Wechsel von REFACTOR zu RED
    */
   public void endRefactor(){

      compileCode();

      // Man darf nur wechseln, wenn alle Teste erfolgreich sind
      if (compilerManager.wasTestSuccessfull()) {
         StoppUhr.beenden();
         trackList.add(new TrackPoint(StoppUhr.zeit(), REFACTOR));

         initRedMode();
         refactor.setDisable(true);
         next.setDisable(false);
      }else{
         if(compilerManager.wasCompilerSuccessfull()){
            String msg = "Es gibt noch Teste die fehlschlagen:\n\n";
            compilerMessages.setText(msg);
            showFailedTests();

         }
      }


   }

   /*
    *  Initialisierung von RED
    */
   public void initRedMode() {
      testArea.setStyle("-fx-border-color: #DF0101;");
      codeArea.setStyle("-fx-border-color: #A4A4A4;");
      testArea.setEditable(true);
      codeArea.setEditable(false);
      backToRed.setDisable(true);
      cycle = RED;
      status.setText("RED");
      StoppUhr.starten();
      if (isBabyStepsEnabled) {    // BabySteps durchführen und dann ggf. abbrechen
         babyStepsHandling();
         babyStepsAbbruch();
      }
   }

   /*
    * Initialisierung von GREEN
    */
   public void initGreenMode(){
      // Muessen alten Code speichern, falls zurueckgesprungen wird
      codeBuffer = new File("BufferFile", codeArea.getText());

      codeArea.setStyle("-fx-border-color: #088A08;");
      testArea.setStyle("-fx-border-color: #A4A4A4;");
      codeArea.setEditable(true);
      testArea.setEditable(false);
      backToRed.setDisable(false);
      cycle = GREEN;
      status.setText("GREEN");
      StoppUhr.starten();
      if (isBabyStepsEnabled) {    // BabySteps durchführen und dann ggf. abbrechen
         babyStepsHandling();
         babyStepsAbbruch();
      }
   }

   /*
    * Ruecksprung von GREEN nach RED
    */
   public void backToRed() {

      testArea.setStyle("-fx-border-color: #DF0101;");
      codeArea.setStyle("-fx-border-color: #A4A4A4;");
      testArea.setEditable(true);
      codeArea.setEditable(false);
      // Setze den Code zurueck auf den Stand vor Beginn der Phase GREEN
      codeArea.setText(codeBuffer.getContent());
      cycle = RED;
      status.setText("RED");
      StoppUhr.starten();
      if (isBabyStepsEnabled) {    // BabySteps durchführen und dann ggf. abbrechen
         babyStepsHandling();
         babyStepsAbbruch();
      }
      backToRed.setDisable(true);



   }

   /*
    * Oeffnet neues Fenster mit entsprechenden Tracking-Diagrammen
    */
   public void startTracking(Event event) {
   }



   // ----------------     AB HIER: Methoden speziell für BABYSTEPS || Aufgabengebiet: Eyyuep


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
                // ggf. BabyStepsConfig.sp.setValue("")
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
