package de.hhu.propra16.unicorndefenders.tddt.tests;

import de.hhu.propra16.unicorndefenders.tddt.config.ConfigParser;
import de.hhu.propra16.unicorndefenders.tddt.config.DummyFile;
import de.hhu.propra16.unicorndefenders.tddt.config.Exercise;
import org.junit.Test;

import static org.junit.Assert.*;


public class ConfigParserTest {

   /**
    * Diese Konfigurationsdatei ist gueltig.
    * @throws Exception
    */
   @Test
   public void correctExercise() throws Exception {
      String configText = "<exercises>\n" +
              "   <exercise name=\"Testaufgabe\">\n" +
              "      <description>Eine Testbeschreibung</description>\n" +
              "      <classes>\n" +
              "         <class name=\"Tesklasse\">\n" +
              "            public class Testklasse {}\n" +
              "         </class>\n" +
              "      </classes>\n" +
              "      \n" +
              "      <tests>\n" +
              "         <test name=\"TestklasseTest\">\n" +
              "            public class TestklasseTest {}\n" +
              "         </test>\n" +
              "      </tests>\n" +
              "\n" +
              "      <config>\n" +
              "         <babysteps enable=\"False\" time=\"120\"/>\n" +
              "         <timetracking enable=\"True\" />\n" +
              "      </config>      \n" +
              "   </exercise>\n" +
              "</exercises>";

      DummyFile file = new DummyFile(configText);
      ConfigParser parser = new ConfigParser(file);
      parser.parse();

      assertEquals(1, parser.getCatalog().getExercises().size());
      Exercise exercise = parser.getCatalog().getExercises().get(0);

      assertEquals(1, exercise.getClassTemplate().size());
      assertEquals(1, exercise.getTestTemplate().size());

      assertEquals("Testaufgabe", exercise.getName());
      assertEquals("Eine Testbeschreibung", exercise.getDescription());
      assertEquals("public class Testklasse {}", exercise.getClassTemplate().get(0).getContent());
      assertEquals("public class TestklasseTest {}", exercise.getTestTemplate().get(0).getContent());

      assertEquals(false, exercise.isBabystepsEnabled());
      assertEquals(true, exercise.isTrackingEnabled());
   }

   /**
    * Fehlende Konfigurationsangaben fuehren dazu, dass diese deaktiviert sind.
    * @throws Exception
    */
   @Test
   public void missingConfig() throws Exception {
      String configText = "<exercises>\n" +
              "   <exercise name=\"Testaufgabe\">\n" +
              "      <description>Eine Testbeschreibung</description>\n" +
              "      <classes>\n" +
              "         <class name=\"Tesklasse\">\n" +
              "            public class Testklasse {}\n" +
              "         </class>\n" +
              "      </classes>\n" +
              "      \n" +
              "      <tests>\n" +
              "         <test name=\"TestklasseTest\">\n" +
              "            public class TestklasseTest {}\n" +
              "         </test>\n" +
              "      </tests>\n" +
              "   </exercise>\n" +
              "</exercises>";

      DummyFile file = new DummyFile(configText);
      ConfigParser parser = new ConfigParser(file);
      parser.parse();


      assertEquals(1, parser.getCatalog().getExercises().size());

      Exercise exercise = parser.getCatalog().getExercises().get(0);

      assertEquals(false, exercise.isBabystepsEnabled());
      assertEquals(false, exercise.isTrackingEnabled());
   }

   /**
    * Falscher Wurzel-Tag
    */
   @Test
   public void invalidRoot() throws Exception {
      String configText = "<wrong> <exercise name=\"Test\"></exercise> </wrong>";

      DummyFile file = new DummyFile(configText);
      ConfigParser parser = new ConfigParser(file);
      parser.parse();

      assertEquals(0, parser.getCatalog().getExercises().size());
   }

   /**
    * Keine Zahl als Babystepszeit.
    */
   @Test(expected = NumberFormatException.class)
   public void invalidBabystepsTime() throws Exception {
      String configText = "<exercises>\n" +
              "   <exercise name=\"Testaufgabe\">\n" +
              "\n" +
              "      <config>\n" +
              "         <babysteps enable=\"true\" time=\"abc\"/>\n" +
              "         <timetracking enable=\"True\" />\n" +
              "      </config>      \n" +
              "   </exercise>\n" +
              "</exercises>";

      DummyFile file = new DummyFile(configText);
      ConfigParser parser = new ConfigParser(file);
      parser.parse();
   }
}
