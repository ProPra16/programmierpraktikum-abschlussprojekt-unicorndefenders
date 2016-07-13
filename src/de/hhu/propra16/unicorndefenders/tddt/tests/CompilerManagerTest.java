package de.hhu.propra16.unicorndefenders.tddt.tests;

import de.hhu.propra16.unicorndefenders.tddt.Cycle;
import de.hhu.propra16.unicorndefenders.tddt.files.CompilerManager;
import de.hhu.propra16.unicorndefenders.tddt.files.File;
import org.junit.Test;
import vk.core.api.TestFailure;

import java.util.Collection;

import static org.junit.Assert.*;

public class CompilerManagerTest {

   /**************
    *
    * Zyklus: RED
    *
    *************/

   @Test
   public void oneTestFailedInRed() {
      assertEquals(true, runOneTestFailed(Cycle.RED));
   }

   @Test
   public void zeroTestsFailedInRed() {
      // Es muessen genau ein Test fehlschlagen
      assertEquals(false, runZeroTestsFailed(Cycle.RED));
   }


   /**************
    *
    * Zyklus: GREEN
    *
    *************/

   @Test
   public void oneTestFailedInGreen(){
      // Es muessen alle Tests laufen
      assertEquals(false, runOneTestFailed(Cycle.GREEN));
   }

   @Test
   public void zeroTestsFailedInGreen() {
      assertEquals(true, runZeroTestsFailed(Cycle.GREEN));
   }


   /**************
    *
    * Zyklus: REFACTOR
    *
    *************/
   @Test
   public void oneTestFailedInRefactor(){
      // Es muessen alle Tests laufen
      assertEquals(false, runOneTestFailed(Cycle.REFACTOR));
   }

   @Test
   public void zeroTestsFailedInRefactor(){
      // Es muessen alle Tests laufen
      assertEquals(true, runZeroTestsFailed(Cycle.REFACTOR));
   }


   /**
    * Der Compiler fand einen Fehler.
    * In diesem Fall muss der CompilerManager, obwohl die korrekte Anzahl Tests fehlschlagen wuerde,
    * false zurueckgegeben.
    */

   public void syntaxErrorInRed(){
      File code = new File("FooBar", incorrectCode);
      File test = new File("FooBarTest", oneTestFailed);

      CompilerManager manager = new CompilerManager(code, test, Cycle.RED);
      assertEquals(false, manager.run());
   }

   public void syntaxErrorInGreen(){
      File code = new File("FooBar", incorrectCode);
      File test = new File("FooBarTest", zeroTestFailed);

      CompilerManager manager = new CompilerManager(code, test, Cycle.GREEN);
      assertEquals(false, manager.run());
   }

   public void syntaxErrorInRefactor(){
      File code = new File("FooBar", incorrectCode);
      File test = new File("FooBarTest", zeroTestFailed);

      CompilerManager manager = new CompilerManager(code, test, Cycle.REFACTOR);
      assertEquals(false, manager.run());
   }


   /**
    * Fuehrt Tests aus, wobei genau einer fehlschlaegt.
    *
    * @param cycle Aktueller Zyklus
    * @return Ergebnis, das der CompilerManager liefert
    */
   private boolean runOneTestFailed(Cycle cycle) {
      File code = new File("FooBar", correctCode);
      File test = new File("FooBarTest", oneTestFailed);

      CompilerManager manager = new CompilerManager(code, test, cycle);
      return manager.run();
   }

   /**
    * Fuehrt Tests aus, wobei keiner fehlschlaegt.
    *
    * @param cycle Aktueller Zyklus
    * @return Ergebnis, das der CompilerManager liefert
    */
   private boolean runZeroTestsFailed(Cycle cycle) {
      File code = new File("FooBar", correctCode);
      File test = new File("FooBarTest", zeroTestFailed);

      CompilerManager manager = new CompilerManager(code, test, cycle);

      return manager.run();
   }


   /**
    * Dieser Code wird fehlerfrei uebersetzt.
    */
   String correctCode = "public class FooBar { public static int test() { return 1; } }";

   /**
    * Dieser String enthaelt einen Syntaxfehler
    */
   String incorrectCode = "public class FooBar public static int test() { return 1; } }";

   /**
    * Zwei Tests, wobei einer fehlschlaegt.
    */
   String oneTestFailed = "import static org.junit.Assert.*;\n" +
           "import org.junit.Test;\n" +
           "\n" +
           "public class FooBarTest { \n" +
           "   @Test\n" +
           "   public void ersterTest() {\n" +
           "      assertEquals(2, FooBar.test());\n" +
           "   } \n" +
           "   \n" +
           "   @Test\n" +
           "   public void zweiterTest() {\n" +
           "      assertEquals(1, FooBar.test());\n" +
           "   } \n" +
           "}";

   /**
    * Zwei Tests, wobei keiner fehlschlaegt.
    */
   String zeroTestFailed = "import static org.junit.Assert.*;\n" +
           "import org.junit.Test;\n" +
           "\n" +
           "public class FooBarTest { \n" +
           "   @Test\n" +
           "   public void ersterTest() {\n" +
           "      assertEquals(1, FooBar.test());\n" +
           "   } \n" +
           "   \n" +
           "   @Test\n" +
           "   public void zweiterTest() {\n" +
           "      assertEquals(1, FooBar.test());\n" +
           "   } \n" +
           "}";
}
