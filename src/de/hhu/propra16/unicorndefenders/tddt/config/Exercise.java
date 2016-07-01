package de.hhu.propra16.unicorndefenders.tddt.config;

import de.hhu.propra16.unicorndefenders.tddt.files.File;

import java.util.ArrayList;
import java.util.List;

/**
 * Eine Aufgabenstellung.
 *
 * @author Pascal
 */
public class Exercise {

   /**
    * Name der Aufgabe
    */
   private String name;

   /**
    * Beschreibungstext der Aufgabe.
    */
   private String description;

   /**
    * Liste aller Klassentemplates einer Aufgabe
    */
   /** @TODO Durch PhysicalFile ersetzen */
   private List<File> classTemplate;

   /**
    * Zeitlimit fuer Babysteps.
    */
   private int babystepsMaxTimeInSeconds;

   /**
    * Flag, ob Babysteps aktiviert ist oder nicht.
    */
   private boolean babystepsEnabled;

   /**
    * Flag, ob Tracking aktiviert ist oder nicht.
    */
   private boolean trackingEnabled;


   public Exercise() {
      this.classTemplate = new ArrayList<>();

      // Eine Aufgaben muss nicht zwingend eine Beschreibung besitzen.
      description = "";

      babystepsEnabled = false;
      babystepsMaxTimeInSeconds = 0;

      trackingEnabled = false;
   }


   public int getBabystepsMaxTimeInSeconds() {
      return babystepsMaxTimeInSeconds;
   }

   public void setBabystepsMaxTimeInSeconds(int babystepsMaxTimeInSeconds) {
      this.babystepsMaxTimeInSeconds = babystepsMaxTimeInSeconds;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getName() {
      return name;
   }

   public String getDescription() {
      return description;
   }

   public void enableBabysteps() {
      this.babystepsEnabled = true;
   }

   public void enableTracking() {
      this.trackingEnabled = true;
   }

   public boolean isTrackingEnabled() {
      return trackingEnabled;
   }

   public boolean isBabystepsEnabled() {
      return babystepsEnabled;
   }

   public List<File> getClassTemplate() {
      return classTemplate;
   }

   public void addClass(File classTemplate) {
      this.classTemplate.add(classTemplate);
   }
}
