package de.hhu.propra16.unicorndefenders.tddt.config;

import de.hhu.propra16.unicorndefenders.tddt.files.File;
import de.hhu.propra16.unicorndefenders.tddt.files.FilesystemFile;
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
 * @author Pascal
 */
public class ConfigParser {

   /**
    * Eingabedatei.
    * Diese ist noch im Rohzustand, d.h. nur der Text.
    */
   private FilesystemFile file;

   /**
    * Aufgabenkatalog.
    * Diese Instanz enthaellt alle geparsten Aufgaben der Konfiguration.
    */
   private Catalog catalog;

   /**
    * Aufgabe, die derzeit aus den Daten der Konfiguration zusammengebaut wird.
    */
   private Exercise currentExercise;

   /**
    * Initialisiert den Parser mit der Konfigurationsdatei.
    *
    * @param file Eingabedatei
    *
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws IOException
    */
   public ConfigParser(FilesystemFile file)
           throws ParserConfigurationException, SAXException, IOException
   {
      this.file = file;
      this.catalog = new Catalog();
      this.currentExercise = new Exercise();

      factory = DocumentBuilderFactory.newInstance();
      builder = factory.newDocumentBuilder();
      document = builder.parse(file.getFileObject());
   }

   /**
    * Startet den Parser.
    * @throws Exception
    */
   public void parse() throws Exception {
      File source          = null;
      File test            = null;
      List<File> classes   = new ArrayList<>();

      NodeList list = document.getElementsByTagName("exercise");

      for (int exerciseIndex = 0; exerciseIndex < list.getLength(); exerciseIndex++) {
         // Aufgaben muessen im Tag 'exercises' > 'exercise' liegen.
         if (!inCorrectTag(list.item(exerciseIndex))) {
            continue;
         }

         currentExercise = new Exercise();

         Node curr = list.item(exerciseIndex).getAttributes().getNamedItem(TAG_NAME);
         if (curr == null)
            throw new Exception(ERR_MSG_MISSING_NAME_FOR_EXERCISE);

         currentExercise.setName(curr.getNodeValue());

         NodeList list2 = list.item(exerciseIndex).getChildNodes();

         for (int k = 0; k < list2.getLength(); k++) {
            Node n = list2.item(k);

            if (n.getNodeName().equals(TAG_DESCRIPTION)) {
               currentExercise.setDescription(n.getTextContent().trim());

            } else if (n.getNodeName().equals(TAG_CLASS_ROOT)) {
               NodeList classList = n.getChildNodes();
               for (int l = 0; l < classList.getLength(); l++) {
                  Node currentConfig = classList.item(l);

                  if (currentConfig.getNodeName().equals(TAG_CLASS_CHILD)) {
                     // @TODO Hier muss der Name der Klasse ausgelesen werden.
                     source = new File(
                             currentConfig.getAttributes().getNamedItem("name").getNodeValue(),
                             currentConfig.getTextContent().trim());

                     currentExercise.addClass(source);
                  }
               }

            } else if (n.getNodeName().equals("tests")) {
               NodeList classList = n.getChildNodes();
               for (int l = 0; l < classList.getLength(); l++) {
                  Node currentConfig = classList.item(l);

                  if (currentConfig.getNodeName().equals("test")) {
                     // @TODO Hier muss der Name der Klasse ausgelesen werden.
                     test = new File(
                             currentConfig.getAttributes().getNamedItem("name").getNodeValue(),
                             currentConfig.getTextContent().trim());

                     currentExercise.addTest(test);
                  }
               }
            }
            else if (n.getNodeName().equals(TAG_CONFIG)) {
               NodeList configList = n.getChildNodes();
               parseExcersiceConfig(configList);
            }
         }

         catalog.addExercise(currentExercise);
      }
   }

   /**
    * Parst den config-Block einer Aufgabe
    *
    * @param configList
    * @throws Exception
    */
   private void parseExcersiceConfig(NodeList configList) throws Exception {
      for (int l = 0; l < configList.getLength(); l++) {
         Node currentConfig = configList.item(l);

         if (currentConfig.getNodeName().equals(SUBTAG_BABYSTEPS)) {

            // Pruefen, ob alle verpflichtenden Einstellungen vorhanden sind.
            Node valueNode = currentConfig.getAttributes().getNamedItem("enable");
            Node timeNode  = currentConfig.getAttributes().getNamedItem("time");

            if (valueNode == null)
               throw new Exception(ERR_MSG_MISSING_ENABLE_TAG);

            if (valueNode.getNodeValue().toLowerCase().equals("true")) {
               currentExercise.enableBabysteps();

               // Eingeschaltetes Babysteps muss eine Zeit bekommen
               if (timeNode == null)
                  throw new Exception(ERR_MSG_MISSING_TIME_FOR_BABYSTEPS);

               currentExercise.setBabystepsMaxTimeInSeconds(Integer.parseInt(timeNode.getNodeValue()));
            }

         }
         else if (currentConfig.getNodeName().equals(SUBTAG_TIMETTRACKING)) {
            if (currentConfig.getAttributes().getNamedItem("enable").getNodeValue().toLowerCase().equals("true")) {
               currentExercise.enableTracking();
            }
         }
      }
   }

   /**
    * Liefert die geparsten Konfigurationsdaten
    * @return
    */
   public Catalog getCatalog() {
      return catalog;
   }

   /**
    * Testet, ob jeder 'exercise'-Tag auch im Obertag 'exercises' liegt.
    * Dieser muss dann der Root-Tag sein.
    *
    * @return
    */
   private boolean inCorrectTag(Node toCheck) {
      /*Node n = toCheck.getParentNode();
      while (n != null) {
         System.out.println(n.getNodeName());
         n = n.getParentNode();
      }
      */
      return toCheck.getParentNode().getNodeName().equals("exercises");
   }


   /**
    * Objekte, die von der XML-Bibliothek benoetigt werden.
    */
   private DocumentBuilderFactory factory;
   private DocumentBuilder builder;
   private Document document;

   /**
    * Vorgeschriebene Tagnamen
    */
   private final String TAG_DESCRIPTION       = "description";
   private final String TAG_NAME              = "name";
   private final String TAG_CLASS_ROOT        = "classes";
   private final String TAG_CLASS_CHILD       = "class";
   private final String TAG_CONFIG            = "config";

   private final String SUBTAG_BABYSTEPS      = "babysteps";
   private final String SUBTAG_TIMETTRACKING  = "timetracking";

   /**
    * Fehlermeldungen
    */
   private final String ERR_MSG_MISSING_TIME_FOR_BABYSTEPS = "Keine Zeitangabe für Babysteps angegeben.";
   private final String ERR_MSG_MISSING_ENABLE_TAG         = "Fehlende enable Angabe gefunden.";
   private final String ERR_MSG_MISSING_NAME_FOR_EXERCISE  = "Fehlender Name für eine Aufgabe.";
}
