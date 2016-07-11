package de.hhu.propra16.unicorndefenders.tddt.files;

import de.hhu.propra16.unicorndefenders.tddt.Cycle;
import vk.core.api.CompilationUnit;
import vk.core.api.TestFailure;
import vk.core.api.TestResult;

import java.util.Collection;

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
    * Gibt alle Test zurueck, die fehlgeschlagen sind.
    *
    * @return Fehlgeschlagene Tests
    */
   public Collection<TestFailure> getTestFailures() {
      return testResult.getTestFailures();
   }

   /**
    * Prueft, ob die fuer einen Entwicklungsstatus korrekte Anzahl Tests fehlgeschlagen ist.
    *
    * @param cycle Entwicklungszyklus
    * @return Ist die korrekte Anzahl Tests in Abhaengigkeit vom Entwicklungszyklus fehlgeschlagen?
    */
   public boolean isValidNumberOfFailedTests(Cycle cycle) {
      if (cycle == Cycle.RED) {
         return testResult.getNumberOfFailedTests() == 1;
      }
      else if (cycle == Cycle.GREEN || cycle == Cycle.REFACTOR) {
         return testResult.getNumberOfFailedTests() == 0;
      }

      return false;
   }
}
