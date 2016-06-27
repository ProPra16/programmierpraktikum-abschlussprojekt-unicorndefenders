package de.hhu.propra16.unicorndefenders.tddt.files;

import de.hhu.propra16.unicorndefenders.tddt.Cycle;
import vk.core.api.CompilationUnit;
import vk.core.api.TestResult;

/**
 * Datei fuer jUnit Tests.
 *
 * @author Pascal
 */
public class TestCode extends Source {

   /**
    * Ergebnis eines Tests
    */
   private TestResult testResult;

   /**
    * Konstruktor.
    *
    * @param file
    */
   public TestCode(File file) {
      super(file);

      // Muss ueberschrieben werden, da Tests mit true initialisiert werden muessen.
      compilationUnit = new CompilationUnit(logicalName, content, true);
   }

   /**
    *
    * @param testResult Testergebnisse
    */
   public void setTestResult(TestResult testResult) {
      this.testResult = testResult;
   }

   /**
    * Prueft, ob die fuer einen Entswicjlungsstatus korrekte Anzahl Tests fehlgeschlagen is.
    *
    * @param cycle
    * @return
    */
   public boolean isValidNumberOfFailedTests(Cycle cycle) {
      if (cycle == Cycle.RED) {
         return testResult.getNumberOfFailedTests() == 1;
      }
      else if (cycle == Cycle.GREEN) {
         return testResult.getNumberOfFailedTests() == 0;
      }

      return false;
   }
}
