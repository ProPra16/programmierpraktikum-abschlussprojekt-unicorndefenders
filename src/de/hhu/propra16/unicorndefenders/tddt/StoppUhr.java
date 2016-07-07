package de.hhu.propra16.unicorndefenders.tddt;


/**
 * Klasse zur Zeitmessung für das Tracking
 *
 * @author Eyyüp
 */


public class StoppUhr{

   public static long timeStart=0;     // Startzeit
   public static long timeEnd=0;       // Endzeit

   public static void starten(){
      timeStart = System.currentTimeMillis();
   }  // misst Startzeit

   public static void beenden() {
      timeEnd = System.currentTimeMillis();
   }    // misst Endzeit

   public static long zeit(){             // misst die Zeit zwischen Start- und Endzeit
      long time=0;
      if(timeStart!=0&&timeEnd!=0){
         time=timeEnd-timeStart;
      }

      return time/1000;    //Ausgabe in Sekunden
   }
}
