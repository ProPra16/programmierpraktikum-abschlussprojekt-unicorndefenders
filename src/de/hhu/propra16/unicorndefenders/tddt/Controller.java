package de.hhu.propra16.unicorndefenders.tddt;

import de.hhu.propra16.unicorndefenders.tddt.config.Catalog;
import de.hhu.propra16.unicorndefenders.tddt.config.ConfigParser;
import de.hhu.propra16.unicorndefenders.tddt.config.ConfigParserException;
import de.hhu.propra16.unicorndefenders.tddt.config.Exercise;
import de.hhu.propra16.unicorndefenders.tddt.files.*;
import de.hhu.propra16.unicorndefenders.tddt.files.File;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import org.xml.sax.SAXException;
import vk.core.api.CompileError;
import vk.core.api.TestFailure;
import vk.core.api.TestResult;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
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
   Button babystepsHighscore;       // Button für die Highscore-Option von BabySteps
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

   public static long sec;          // für die Methode count; falls keine Klassenvariable benutzt werden würde, Probleme in Lambda-Expressions
   public static boolean stopThread=false;   // zum Stoppen vorheriger Threads genutzter Boolean-Wert

   // zur Überprüfung des Erreichens der 3.GREEN-Phase
   static int highscoreziel=0;

   public static StringProperty sp = new SimpleStringProperty(null);   // StringProperty zur Aktualisierung der ablaufenden Zeit

   private String configFilePath;


   private void initMenu() {
      // initialer Menuaufbau
      try{
         // Parsen
         //FilesystemFile catalogFile = new FilesystemFile("test.xml");
         FilesystemFile catalogFile = new FilesystemFile(configFilePath);
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
      } catch (ParserConfigurationException | SAXException | ConfigParserException e) {
         Alert alert = new Alert(Alert.AlertType.ERROR);
         alert.setTitle("Schade");
         alert.setHeaderText("Der Katalog entspricht nicht dem Format :(");
         alert.setContentText(e.getMessage());
         alert.showAndWait();
         chooseFile();
      } catch (FileNotFoundException e ){
         Alert alert = new Alert(Alert.AlertType.ERROR);
         alert.setTitle("Schade");
         alert.setContentText("Der Katalog wurde nicht gefunden :(");
         System.out.println(e.getMessage());
         alert.showAndWait();
      }  catch (IOException e) {
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



                  // wenn die BabySteps-Funnktion eingeschaltet ist, lasse die Highscore-Option zu
                  // lasse ansonsten keinen Knopfdruck zu
                  if(isBabyStepsEnabled){

                     // für jede Aufgabe ist einzeln zu prüfen, ob das Highscoreziel (3mal Phase grün erreicht, also 2 zyklen vollendet) erreicht wurde
                     // nur dann wird ein Highscore-Eintrag erzeugt
                     // bei Wahl einer neuen Aufgabe deshalb auf 0 gesetzt
                     highscoreziel=0;
                     babystepsHighscore.setDisable(false);
                     BabyStepsHighscore.createFile(selectedExcercise.getName());
                     BabyStepsHighscore.aufgabe=selectedExcercise.getName();
                  }
                  else{
                     babystepsHighscore.setDisable(true);
                  }



                  List<File> codeList = selectedExcercise.getClassTemplate();
                  List<File> testList = selectedExcercise.getTestTemplate();

                  // Aufgabenstellung als Kommentar oben ins Textfeld schreiben
                  String  codeAreaText = "";
                  if(!selectedExcercise.getDescription().isEmpty()) {
                     codeAreaText = "/*" + selectedExcercise.getDescription() + "*/\n\n";
                  }
                  codeArea.setText(codeAreaText+codeList.get(0).getContent());

                  testArea.setText(testList.get(0).getContent());

                  status.setText("RED");
                  testArea.setEditable(true);
                  codeArea.setEditable(false);
                  codeArea.setStyle("-fx-border-color: #A4A4A4;");
                  testArea.setStyle("-fx-border-color: #DF0101;");
                  compile.setDisable(false);
                  next.setDisable(false);
                  tracking.setDisable(false);
                  babystepsHighscore.setDisable(false);

                  // neue Aufgabe angefangen, also werden die Zeiten der alten Aufgabe geloescht
                  trackList = new ArrayList<TrackPoint>();

                  StoppUhr.starten();

                  babyStepsHandling();       // ggf. BabySteps

               }

            }
            taskMenu.getSelectionModel().clearSelection();

         }
      });
   }

   /**
    * Fordert den Benutzer dazu auf eine Konfigurationsdatei zu waehlen.
    */
   private void chooseFile() {
      java.io.File file = null;

      // Solange den Dialog anzeigen, bis eine Datei gewaehlt wurde
      do {
         // Basierend auf https://docs.oracle.com/javase/8/javafx/api/javafx/stage/FileChooser.html
         FileChooser fileChooser = new FileChooser();
         fileChooser.setTitle("Wählen Sie einen Aufgabenkatalog");
         fileChooser.getExtensionFilters().addAll(
                 new FileChooser.ExtensionFilter("XML-Dateien", "*.xml"));

         file = fileChooser.showOpenDialog(codeArea.getScene().getWindow());
      } while (file == null);
      configFilePath = file.getAbsolutePath();

      // Menu anhand der gewaehlten Konfiguration aufbauen
      initMenu();
   }

   /*
    * Wird bei Programmaufruf ausgefuehrt:
    * - Lässt Katalog parsen und baut Auswahlmenuleiste mit entsprechenden Aufgaben
    */
   @Override
   public void initialize(URL location, ResourceBundle resources) {
      // Erste Phase: Test editieren
      cycle = RED;

      // Fordere zur Eingabe der Konfigurationsdatei auf, wenn das Fenster
      // fertig initialisiert ist.
      Platform.runLater(this::chooseFile);
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

      // beende zuvor laufende Threads, um zeitliche Verzögerungen zu beseitigen
      stopThread=true;     //schafft probleme!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

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
               File code = new File("Code", codeArea.getText());
               File test = new File("Test", testArea.getText());
               trackList.add(new TrackPoint(StoppUhr.zeit(), GREEN, code, test));

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
               File code = new File("Code", codeArea.getText());
               File test = new File("Test", testArea.getText());
               trackList.add(new TrackPoint(StoppUhr.zeit(), GREEN, code, test));
               refactoring=false;
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
            stopThread=true;
            continuecount(babyStepsTimer);
         }
      } else if (cycle == RED) {
         if (compilerManager.wasTestSuccessfull() || !compilerManager.wasCompilerSuccessfull()) {
            StoppUhr.beenden();
            File code = new File("Code", codeArea.getText());
            File test = new File("Test", testArea.getText());
            trackList.add(new TrackPoint(StoppUhr.zeit(), RED, code, test));
            highscoreziel++;  //bei jedem erfolgreichen Wechsel in die GREEN-Phase inkrementiert
            initGreenMode();
         } else {
            String msg = compilerMessages.getText();
            msg = msg + "Es muss genau ein Test fehlschlagen, um in Phase GREEN zu wechseln:\n";
            compilerMessages.setText(msg);
            showFailedTests();
            babyStepsHandling();

         }
      }


   }

   /*
    * Schreibt die aktuell fehlgeschlagenen Teste in das untere TextArea
    */
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
         File code = new File("Code", codeArea.getText());
         File test = new File("Test", testArea.getText());
         trackList.add(new TrackPoint(StoppUhr.zeit(), REFACTOR, code, test));
         refactoring=false;      ///
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
      refactoring=false;


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
      if(isBabyStepsEnabled) babyStepsHandling();

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
      }
      backToRed.setDisable(true);



   }

   /*
    * Oeffnet neues Fenster mit entsprechenden Tracking-Diagrammen
    */
   public void startTracking(Event event) {
      TrackingResultWindow trackResult = new TrackingResultWindow(trackList);
      trackResult.show();
   }





   // ----------------     AB HIER: Methoden speziell für BABYSTEPS || Aufgabengebiet: Eyyuep



   // wenn drei mal die GREEN-Phase erreicht wurde, gehe in die Highscore-Stage über, sonst passiert nichts

   public void showHighscore(Event event) {
      if(highscoreziel==3) {
         BabyStepsHighscore.handling();
         BabyStepsHighscoreStage highscores = new BabyStepsHighscoreStage(BabyStepsHighscore.highscorestrings);
         highscores.setTitle("BabySteps-TopFive zu " + BabyStepsHighscore.aufgabe);
         highscores.show();
         highscoreziel = 0;  // danach nicht mehr möglich
      }
   }


   public void babyStepsHandling() {  // führt BabySteps aus

      stopThread=true;

      try{
         Thread.sleep(1000);
      }
      catch(InterruptedException e){
         e.printStackTrace();
      }

      if (isBabyStepsEnabled && cycle != REFACTOR) {   // BabySteps nur, wenn eingeschaltet und nicht in der Refactoring-Phase

         if (cycle == RED) {                                 // alten Inhalt des Test-Editors speichern
            puffer = testArea.getText();
         }
         else if (cycle == GREEN) {                               // alten Inhalt des Code-Editors speichern
            puffer = codeArea.getText();
         }


         stopThread=false;
         sec = babyStepsTimeinSeconds;           // Anzahl Sekunden setzen
         init(babyStepsTimeinSeconds, babyStepsTimer); // Anfangswert des Labels setzen
         count(babyStepsTimer);                  // Timer runterzählen


      } else {                                                            // wenn babySteps=nein oder Refactoring-Phase
         init(babyStepsTimeinSeconds, babyStepsTimer); // damit das Label an StringProperty sp gebunden wird
         sp.setValue("");                              // sp auf leer gesetzt
      }
   }

   public static void init(long sec, Label label) {          // erzeugt Zeit-Label in Abhängigkeit von der konfigurierten Dauer

      long minutes=sec/60;    //Minutenanzahl

      long seconds=sec%60;    //Sekundenanzahl

      if(seconds>9)  sp.setValue(minutes+":"+seconds);   //Setze den Anfangswert der StringProperty
      else  sp.setValue(minutes+":0"+seconds);

      label.textProperty().bind(sp);     // binde die StringProperty an das Label

   }


   public void count(Label label){   // zählt die Zeit des Timer-Labels runter

      BabyStepsStoppUhr.starten();     // Zeitmessung für die Highscores

      Thread t = new Thread(() -> {         // damit parallel Aktionen möglich sind, neuen Thread erstellen


         while (!label.getText().equals("0:01")) {  // bis 0:00 erreicht wird, wird die Zeit runtergezählt
            try{

               // wenn eine Kompilierung erfolreich war, dann Schleife abbrechen
               if(successfullCompiling){
                  successfullCompiling=false;
                  break;
               }

               if(stopThread) break;
               Thread.sleep(1000);
               sec=sec-1;                               // runterzählen der Zeit
               if(stopThread) break;

               // Berechne die aktuelle Zeit im gewünschten Format und setze den Wert der StringProperty
               long minutes=sec/60;
               long seconds= sec%60;
               if(seconds>=10) {
                  Platform.runLater( () -> sp.setValue(minutes + ":" + seconds)); // Platform.runLater behebt das Problem, dass Updates der GUI nicht
               }                                                                  // auf einem Nicht-FX Application Thread ausgeführt werden können
               else{
                  Platform.runLater( () -> sp.setValue(minutes + ":0" + seconds));
               }

               // wenn man zwischenzeitlich in die REFACTOR-Phase gewechselt hat, soll der Timer gestoppt werden
               // refactoring-Variable wird im Controller auf true gesetzt
               if(refactoring){
                  refactoring=false;
                  break;
               }
            }
            catch (InterruptedException e) {
               e.printStackTrace();
            }

         }

         if(babyStepsTimer.getText().equals("0:01")) {
            if (cycle == RED) {
               sec = babyStepsTimeinSeconds;
               testArea.setText(puffer);
               Platform.runLater(() ->initGreenMode());        // Test-Editor-Inhalt zurücksetzen, da Zeit abgelaufen
            } else if (cycle == GREEN) {
               sec = babyStepsTimeinSeconds;
               codeArea.setText(puffer);
               Platform.runLater(() -> initRedMode());        // Test-Editor-Inhalt zurücksetzen, da Zeit abgelaufen
            }
         }

         sec = babyStepsTimeinSeconds;
         BabyStepsStoppUhr.beenden();  //Zeitmessung für die Highscores

      });

      t.start();      // Starte den Thread t

   }

   // diese Methode soll das Weiterrunterzählen nach Tätigung des Next-Buttons gewährleisten

   public void continuecount(Label label){

      String labeltext=label.getText();

      int seconds=0;

      // Umwandlung des Strings in Sekunden
      if(labeltext.length()==5){

         String min=""+labeltext.charAt(0);
         min=min+labeltext.charAt(1);
         int minutes=Integer.parseInt(min);
         String sec=""+labeltext.charAt(3);
         int secs=0;

         if(sec.equals("0")){
            String temp=""+ labeltext.charAt(4);
            secs=Integer.parseInt(temp);
         }
         else{
            sec=sec+labeltext.charAt(4);
            secs=Integer.parseInt(sec);
         }

         seconds=minutes*60+secs;
      }
      else{

         String min=""+labeltext.charAt(0);
         int minutes=Integer.parseInt(min);
         String sec=""+labeltext.charAt(2);
         int secs=0;

         if(sec.equals("0")){
            String temp=""+ labeltext.charAt(3);
            secs=Integer.parseInt(temp);
         }
         else{
            sec=sec+labeltext.charAt(3);
            secs=Integer.parseInt(sec);
         }

         seconds=minutes*60+secs;
      }

      stopThread=true;

      try{
         Thread.sleep(1000);
      }
      catch(InterruptedException e){
         e.printStackTrace();
      }

      if (isBabyStepsEnabled && cycle != REFACTOR) {   // BabySteps nur, wenn eingeschaltet und nicht in der Refactoring-Phase

         if (cycle == RED) {                                 // alten Inhalt des Test-Editors speichern
            puffer = testArea.getText();
         }
         else if (cycle == GREEN) {                               // alten Inhalt des Code-Editors speichern
            puffer = codeArea.getText();
         }


         stopThread=false;
         sec = seconds;           // Anzahl Sekunden setzen
         init(sec, babyStepsTimer); // Anfangswert des Labels setzen
         count(babyStepsTimer);                  // Timer runterzählen


      } else {                                                            // wenn babySteps=nein oder Refactoring-Phase
         init(babyStepsTimeinSeconds, babyStepsTimer); // damit das Label an StringProperty sp gebunden wird
         sp.setValue("");                              // sp auf leer gesetzt
      }

   }


}
