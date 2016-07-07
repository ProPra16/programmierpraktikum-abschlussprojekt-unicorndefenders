package de.hhu.propra16.unicorndefenders.tddt.config;

/**
 * Ausnahme des ConfigParsers.
 *
 * @author Pascal
 */
public class ConfigParserException extends Exception {

   /**
    * Die Aufgabe, bei der der Parser scheiterte.
    *
    * Diese Variable wird bisher nur dafür verwendet um den Namen der Aufgabe, bei der der
    * Parser abbrach in die Fehlermeldung schreiben zu koennen. Sie darf auch null sein,
    * wenn bereits Fehler aufgetreten sind, bevor ein Name gelesen werden konnte.
    */
   protected Exercise exercise;

   /**
    * Die Fehlermeldung, die dem Aufrufer mit getMessage zurueckgeliefert wird.
    */
   protected String message;

   ConfigParserException(String message, Exercise exercise) {
      super();
      this.exercise = exercise;

      // Aufgabenname als Zusatzinformation an die Benachrichtigung anhaengen.
      this.message = message;
      if (exercise != null) {
         this.message += " (Aufgetreten in '" + exercise.getName() + "')";
      }
   }

   @Override
   public String getMessage() {
      return message;
   }

   @Override
   public String toString() {
      return message;
   }
}
