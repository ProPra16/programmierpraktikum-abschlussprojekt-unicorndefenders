package de.hhu.propra16.unicorndefenders.tddt.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Aufgabenkatalog.
 * Dies ist die Liste aller Aufgaben, die aus der Konfigurationsdatei gelesen wurde.
 *
 * @author Pascal
 */
public class Catalog {

   /**
    * Liste aller Aufgaben
    */
   private List<Exercise> exercises;

   public Catalog() {
      exercises = new ArrayList<>();
   }

   /**
    * Fuegt eine neue Aufgabe hinzu.
    *
    * @param newExercise Die Aufgabe, die hinzugefuegt werden soll.
    */
   public void addExercise(Exercise newExercise) {
      exercises.add(newExercise);
   }

   /**
    * Liefert den Aufgabenkatalog.
    *
    * @return Aufgabenkatalog.
    */
   public List<Exercise> getExercises() {
      return exercises;
   }
}
