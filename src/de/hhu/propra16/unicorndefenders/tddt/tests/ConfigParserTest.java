package de.hhu.propra16.unicorndefenders.tddt.tests;

import de.hhu.propra16.unicorndefenders.tddt.config.ConfigParser;
import de.hhu.propra16.unicorndefenders.tddt.config.ConfigParserException;
import de.hhu.propra16.unicorndefenders.tddt.config.DummyFile;
import de.hhu.propra16.unicorndefenders.tddt.config.Exercise;
import org.junit.Test;

import static org.junit.Assert.*;


public class ConfigParserTest {

   @Test(expected = ConfigParserException.class)
   public void wrongRoot() throws Exception {
      DummyFile file = new DummyFile(textWrongRoot);
      ConfigParser parser = new ConfigParser(file);
      parser.parse();
   }

   @Test(expected = ConfigParserException.class)
   public void missingExerciseName() throws Exception {
      DummyFile file = new DummyFile(textMissingExerciseName);
      ConfigParser parser = new ConfigParser(file);
      parser.parse();
   }

   @Test(expected = ConfigParserException.class)
   public void moreClassesThanTests() throws Exception {
      DummyFile file = new DummyFile(textMoreClassesThanTests);
      ConfigParser parser = new ConfigParser(file);
      parser.parse();
   }

   @Test(expected = ConfigParserException.class)
   public void moreTestsThanClasses() throws Exception {
      DummyFile file = new DummyFile(textMoreTestsThanClasses);
      ConfigParser parser = new ConfigParser(file);
      parser.parse();
   }

   @Test(expected = ConfigParserException.class)
   public void missingEnableValue() throws Exception {
      DummyFile file = new DummyFile(textMissingEnable);
      ConfigParser parser = new ConfigParser(file);
      parser.parse();
   }

   @Test
   public void correct() throws Exception {
      DummyFile file = new DummyFile(textCorrectContent);
      ConfigParser parser = new ConfigParser(file);
      parser.parse();

      assertEquals(1, parser.getCatalog().getExercises().size());
      Exercise exercise = parser.getCatalog().getExercises().get(0);

      assertEquals("Eine Testbeschreibung", exercise.getDescription());
      assertEquals("Testaufgabe", exercise.getName());

      assertEquals(1, exercise.getClassTemplate().size());
      assertEquals(1, exercise.getTestTemplate().size());

      assertEquals(true, exercise.isBabystepsEnabled());
      assertEquals(120, exercise.getBabystepsMaxTimeInSeconds());
      assertEquals(false, exercise.isTrackingEnabled());

   }



   private String textWrongRoot = "<exercisesssss>\n" +
           "   <exercise name=\"Testaufgabe\">\n" +
           "   </exercise>\n" +
           "</exercisesssss>";

   private String textMissingExerciseName = "<exercises>\n" +
           "   <exercise>\n" +
           "   </exercise>\n" +
           "</exercises>";

   private String textMoreClassesThanTests = "<exercises>\n" +
           "   <exercise name=\"Testaufgabe\">\n" +
           "      <description>Eine Testbeschreibung</description>\n" +
           "      <classes>\n" +
           "         <class name=\"Tesklasse\">\n" +
           "            public class Testklasse {}\n" +
           "         </class>\n" +
           "         <class name=\"Tesklasse2\">\n" +
           "            public class Testklasse2 {}\n" +
           "         </class>\n" +
           "      </classes>\n" +
           "      \n" +
           "      <tests>\n" +
           "      </tests>\n" +
           "\n" +
           "      <config>\n" +
           "         <babysteps enable=\"False\" time=\"120\"/>\n" +
           "         <timetracking enable=\"True\" />\n" +
           "      </config>      \n" +
           "   </exercise>\n" +
           "</exercises>";

   private String textMoreTestsThanClasses = "<exercises>\n" +
           "   <exercise name=\"Testaufgabe\">\n" +
           "      <description>Eine Testbeschreibung</description>\n" +
           "      <classes>\n" +
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

   private String textCorrectContent = "<exercises>\n" +
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
           "         <babysteps enable=\"True\" time=\"120\"/>\n" +
           "         <timetracking enable=\"false\" />\n" +
           "      </config>      \n" +
           "   </exercise>\n" +
           "</exercises>";

   private String textMissingEnable = "<exercises>\n" +
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
           "         <babysteps  time=\"120\"/>\n" +
           "         <timetracking enable=\"false\" />\n" +
           "      </config>      \n" +
           "   </exercise>\n" +
           "</exercises>";
}
