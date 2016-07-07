package de.hhu.propra16.unicorndefenders.tddt;

import de.hhu.propra16.unicorndefenders.tddt.config.Exercise;
import javafx.beans.property.SimpleStringProperty;

/**
 * Listeneintraege des Aufgabenauswahlmenus
 *
 * Habe mich bzgl. der Methodik an:
 * http://docs.oracle.com/javafx/2/ui_controls/table-view.htm
 * orientiert
 *
 * @author Alessandra
 */
public class MenuEntry {

   private final SimpleStringProperty taskTitle = new SimpleStringProperty("");
   private final SimpleStringProperty babySteps = new SimpleStringProperty("");

   // Speichere die entsprechende Exercise zwischen, um bei Click Events leichter reagieren zu können
   private Exercise exercise;


   public MenuEntry(){ this("","", null);
   }

   public MenuEntry(String title, String bs, Exercise exercise){
      taskTitle.set(title);
      babySteps.set(bs);
      this.exercise= exercise;
   }

   public Exercise getExercise() {
      return exercise;
   }


   /*
    * Methoden unverwendet; werden aber benoetigt, damit TableView die Klasse verwenden kann
    */

   public String getTaskTitle() {
        return taskTitle.get();
    }

   public String getBabySteps() {
        return babySteps.get();
    }

   public void setBabySteps(String bs) {
        babySteps.set(bs);
    }

   public void setTaskTitle(String title) {
        taskTitle.set(title);
    }


}
