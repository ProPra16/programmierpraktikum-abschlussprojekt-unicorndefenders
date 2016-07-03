package de.hhu.propra16.unicorndefenders.tddt.files;

import de.hhu.propra16.unicorndefenders.tddt.Cycle;
import vk.core.api.CompilerFactory;
import vk.core.api.CompilerResult;
import vk.core.api.JavaStringCompiler;

/**
 * Zentrale Schnittstelle zwischen Compilerbibliothek und der TDDT-Anwendung.
 *
 * @author Pascal
 */
public class CompilerManager {

   /**
    * kompilierte Quellcode Datei.
    * Eine Instanz von Source wird auch erzeugt, sollte es Compilierungsfehler geben.
    * Dann enthaelt sie allerdings zusaetzlich die Liste aller Compilierungsfehler.
    */

   private Source sourceFile;

   /**
    * Kompilierter und ggf. ausgefuehrter TestCode.
    */
   private TestCode testFile;

   /**
    * Der Entwicklungsstatus in dem wir zu dem Zeitpunkt sind.
    * z.B. RED, GREEN, ...
    */
   private Cycle currentCircle;

   /**
    * Der Uebersetzungsvorgang beider Dateien ist erfolgreich verlaufen.
    */
   private boolean compilationComplete;

   /**
    * Der TestCode wurde mit einer korrekten Anzahl von Fehlschlaegen ausgefuehrt.
    * Der aktuelle Zustand wird aus der Instanzvariable currentCircle genommen.
    */
   private boolean testComplete;

   /**
    * Erzeugt einen neuen Compiler Manager.
    *
    * @param source Die Quellcode-Datei.
    * @param test Die Testcode-Datei.
    * @param currentCircle Der aktuelle Entwicklungszustand in dem wir uns befinden.
    */
   public CompilerManager(File source, File test, Cycle currentCircle) {
      sourceFile = new Source(source);
      testFile = new TestCode(test);

      this.currentCircle = currentCircle;
      testComplete = compilationComplete = false;
   }

   /**
    * Startet den Uebersetzungsvorgang fuer die beiden JAVA-Dateien und fuehrt
    * anschliessend die Tests aus.
    * Gibt zurueck, ob Compiler UND TestCode korrekt ausgefuehrt wurden.
    *
    * Diese Methode muss als erste Methode dieser Klasse nach dem Konstruktor aufgerufen werden.
    *
    * @return true, wenn der Uebersetzungsvorgang fehlerfrei verlief und die Anzahl der
    * fehlgeschlagenen Tests den Vorgaben des Entwicklungszustandes entspricht.
    * false sonst.
    */
   public boolean run() {
      JavaStringCompiler compiler = CompilerFactory.getCompiler(
              sourceFile.getCompilationUnit(),
              testFile.getCompilationUnit());

      compiler.compileAndRunTests();

      // Ein Compilerfehler ist aufgetreten
      CompilerResult compilerResult = compiler.getCompilerResult();
      if (compilerResult.hasCompileErrors()) {
         sourceFile.setCompilerErrors(compilerResult);
         testFile.setCompilerErrors(compilerResult);
         return false;
      }

      // Testen, ob die korrekte Anzahl Tests fehlschlugen
      testFile.setTestResult(compiler.getTestResult());
      if (! testFile.isValidNumberOfFailedTests(currentCircle)) {
         compilationComplete = true;
         return false;
      }

      // Alles ok
      testComplete = true;
      return true;
   }

   /**
    * Liefert die uebersetzte Quellcode-Datei inklusive evtl. Compilerfehler.
    *
    * @return Quellcode-Datei
    */
   public Source getSourceFile() {
      return sourceFile;
   }

   /**
    * Liefert die ausgefuehrte Testdatei inklusive deren Ergebnisse.
    *
    * @return Testdatei
    */
   public TestCode getTestFile() {
      return testFile;
   }

   /**
    * Gibt true, wenn der Compiler beide Dateien (Code und Test) fehlerfrei ausgefuehrt hat.
    *
    * @return War der Compiler erfolgreich?
    */
   public boolean wasCompilerSuccessfull() {
      return compilationComplete;
   }

   /**
    * Gibt true, wenn die korrekte Anzahl Tests fuer den aktuellen Enwicklungsstatus
    * fehlgeschlagen sind.
    *
    * @return ISt die richte Anzahl Tests fehlgeschlagen?
    */
   public boolean wasTestSuccessfull() {
      return testComplete;
   }
}
