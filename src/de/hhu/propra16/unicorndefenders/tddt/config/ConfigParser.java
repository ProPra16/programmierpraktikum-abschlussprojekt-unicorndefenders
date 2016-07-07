package de.hhu.propra16.unicorndefenders.tddt.config;

import de.hhu.propra16.unicorndefenders.tddt.files.File;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser fuer die Konfigurationsdatei.
 *
 * Folgende Fehler werden abgefangen (ConfigParserException):
 *    - Ungleiche Anzahl von Klassen und Tests innerhalb einer Aufgabe.
 *    - Fehlender Name einer Aufgabe.
 *    - Falsche Wurzel
 *    - Fehlende 'enable' Angabe bei den Erweiterungen
 *    - Babystepszeit, die keine Zahl ist. (Wird auch als ConfigParserException geworfen)
 *
 * @author Pascal
 */
public class ConfigParser {

   /**
    * Konfigurationsdatei.
    * Diese Instanz enthaellt alle geparsten Information aus der Konfiguration.
    */
   private Catalog catalog;

   /**
    * Initialisiert den Parser mit der Konfigurationsdatei.
    *
    * @param file Eingabedatei
    *
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws IOException
    */
   public ConfigParser(ReallyExistingFile file)
           throws ParserConfigurationException, SAXException, IOException
   {
      this.catalog = new Catalog();

      factory = DocumentBuilderFactory.newInstance();
      builder = factory.newDocumentBuilder();
      document = builder.parse(file.getFileObject());
   }

   /**
    * Startet den Parser.
    *
    * @throws ConfigParserException
    */
   public void parse() throws ConfigParserException {
      File source          = null;
      File test            = null;
      List<File> classes   = new ArrayList<>();

      // Wurzel muss 'exercises' sein
      Node documentParentNode = document.getFirstChild();
      if (! documentParentNode.getNodeName().equals(NODE_EXERCISES)) {
         throw new ConfigParserException(ERR_MSG_WRONG_ROOT, null);
      }

      // Jede Aufgabe ist ein direkter Nachfolger der Wurzel
      NodeList rootChildren = documentParentNode.getChildNodes();
      for (int i=0; i<rootChildren.getLength(); i++) {
         Node child = rootChildren.item(i);

         if (child.getNodeType() == Node.ELEMENT_NODE && rootChildren.item(i).getNodeName().equals(NODE_EXERCISE)) {
            Exercise exercise = parseExercise(child);

            // Jede Klasse muss genau einen Test haben
            if (exercise.getClassTemplate().size() != exercise.getTestTemplate().size()) {
               throw new ConfigParserException(ERR_MSG_DIFFERENT_NBR_OF_TESTS_AND_CLASSES, exercise);
            }

            catalog.addExercise(exercise);
         }

      }
   }

   /**
    * Parst einen Knoten, d.h. eine Aufgabe
    *
    * @param exerciseNode
    */
   private Exercise parseExercise(Node exerciseNode) throws ConfigParserException {
      Exercise exercise = new Exercise();
      NodeList children = exerciseNode.getChildNodes();

      // Jede Aufgabe muss einen Namen haben
      Node nameAttribute = exerciseNode.getAttributes().getNamedItem(ATTRIBUTE_NAME);
      if (nameAttribute == null)
         throw new ConfigParserException(ERR_MSG_MISSING_NAME, null);

      exercise.setName(nameAttribute.getNodeValue());


      for (int l=0; l<children.getLength(); l++) {
         String nodeName = children.item(l).getNodeName();

         // Beschreibung der Aufgabe
         if (nodeName.equals(NODE_DESCRIPTION)) {
            exercise.setDescription(children.item(l).getTextContent().trim());
         }

         // Liste aller Klassen, die zur Verfuegung gestellt werden
         if (nodeName.equals(NODE_CLASSES)) {
            exercise = parseCodeNode(children.item(l), exercise, CodeType.SOURCE);
         }

         // Liste aller Test, die zur Verfuegung gestellt werden
         if (nodeName.equals(NODE_TESTS)) {
            exercise = parseCodeNode(children.item(l), exercise, CodeType.TEST);
         }

         // Konfigurationen
         if (nodeName.equals(NODE_CONFIG)) {
            exercise = parseConfig(children.item(l), exercise);
         }
      }

      return exercise;
   }

   /**
    * Parst die Konfigurationen innerhalb des 'config'-Knoten.
    * Hier werden alle Einstellungen der Erweiterungen festgelegt.
    *
    * @param configNode
    * @param exercise
    * @return
    * @throws ConfigParserException
    */
   private Exercise parseConfig(Node configNode, Exercise exercise) throws ConfigParserException {
      NodeList children = configNode.getChildNodes();

      for (int l = 0; l < children.getLength(); l++) {
         Node currentConfig = children.item(l);

         if (currentConfig.getNodeName().equals(NODE_BABYSTEPS)) {

            // Pruefen, ob alle verpflichtenden Einstellungen vorhanden sind.
            Node valueNode = currentConfig.getAttributes().getNamedItem(ATTRIBUTE_ENABLE);
            Node timeNode  = currentConfig.getAttributes().getNamedItem(ATTRIBUTE_TIME);

            if (valueNode == null) {
               throw new ConfigParserException(ERR_MSG_MISSING_ENABLE, exercise);
            }

            if (valueNode.getNodeValue().toLowerCase().equals("true")) {
               exercise.enableBabysteps();

               // Eingeschaltetes Babysteps muss eine Zeit bekommen
               if (timeNode == null)
                  throw new ConfigParserException(ERR_MSG_MISSING_TIME, exercise);

               // Auch NumberFormatException soll als ConfigParserException geworfen werden
               try {
                  exercise.setBabystepsMaxTimeInSeconds(Integer.parseInt(timeNode.getNodeValue()));
               } catch (NumberFormatException ex) {
                  throw new ConfigParserException("Ungültige Zeit für Babysteps. (" + ex.getMessage() + ")", exercise);
               }

            }
         }
         else if (currentConfig.getNodeName().equals(NODE_TIMETRACKING)) {
            if (currentConfig.getAttributes().getNamedItem(ATTRIBUTE_ENABLE).getNodeValue().toLowerCase().equals("true")) {
               exercise.enableTracking();
            }
         }
      }

      return exercise;
   }

   /**
    * Parst Code-Knoten.
    * Dies sind die Knoten fuer Klassen und Tests.
    *
    * Einer der Uebergabeparameter ist die aktuell noch nicht fertig gelesene Aufgabe.
    * Diese wird in dieser Methode veraendert. Das modifizierte Objekt wird am Ende
    * wieder zurueckgegeben.
    *
    * @param node
    * @param exercise Die noch unfertig gelesene Aufgabe
    * @param codeType Type des Knotens (Code oder Tests)
    *
    * @return Die modifizierte Aufgabe
    */
   private Exercise parseCodeNode(Node node, Exercise exercise, CodeType codeType) {
      File file = null;
      NodeList classes = node.getChildNodes();

      for (int l = 0; l < classes.getLength(); l++) {
         Node currentConfig = classes.item(l);

         String name = (currentConfig.getAttributes() == null)
                 ? "Unbenannte Klasse"
                 : currentConfig.getAttributes().getNamedItem(ATTRIBUTE_NAME).getNodeValue();


         if (codeType == CodeType.SOURCE && currentConfig.getNodeName().equals(NODE_CLASS)) {
            file = new File(name, currentConfig.getTextContent().trim());
            exercise.addClass(file);
         }
         else if (codeType == CodeType.TEST && currentConfig.getNodeName().equals(NODE_TEST)) {
            file = new File(name, currentConfig.getTextContent().trim());
            exercise.addTest(file);
         }
      }

      return exercise;
   }

   /**
    * Liefert die geparsten Konfigurationsdaten
    * @return
    */
   public Catalog getCatalog() {
      return catalog;
   }

   /**
    * Objekte, die von der XML-Bibliothek benoetigt werden.
    */
   private DocumentBuilderFactory factory;
   private DocumentBuilder builder;
   private Document document;

   /**
    * Internes Enum zur Verwendung als Parameter in parseCodeNode.
    *
    * @author Pascal
    */
   private enum CodeType {
      SOURCE,
      TEST
   };


   private final String ERR_MSG_WRONG_ROOT = "Datei besitzt ein falsches Wurzelelement. (Erwartet: exercises)";
   private final String ERR_MSG_DIFFERENT_NBR_OF_TESTS_AND_CLASSES = "Unterschiedliche Anzahl von Tests und Klassen.";
   private final String ERR_MSG_MISSING_NAME = "Kein Name fuer Aufgabe vergeben.";
   private final String ERR_MSG_MISSING_ENABLE = "Fehlende enable Angabe.";
   private final String ERR_MSG_MISSING_TIME = "Keine Zeit für Babysteps angegeben.";

   private final String ATTRIBUTE_NAME = "name";
   private final String ATTRIBUTE_TIME = "time";
   private final String ATTRIBUTE_ENABLE = "enable";

   private final String NODE_EXERCISES = "exercises";
   private final String NODE_EXERCISE = "exercise";
   private final String NODE_DESCRIPTION = "description";
   private final String NODE_CONFIG = "config";
   private final String NODE_BABYSTEPS = "babysteps";
   private final String NODE_TIMETRACKING = "timetracking";
   private final String NODE_CLASSES = "classes";
   private final String NODE_CLASS = "class";
   private final String NODE_TESTS = "tests";
   private final String NODE_TEST = "test";

}
