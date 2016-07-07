package de.hhu.propra16.unicorndefenders.tddt;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;


public class BabyStepsConfig{

   public static  StringProperty sp = new SimpleStringProperty(null);   // StringProperty zur Aktualisierung der ablaufenden Zeit
   public static long sec;          // für die Methode count; falls keine Klassenvariable benutzt werden würde, Probleme in Lambda-Expressions
   public static boolean stopThread=false;                              // zum Stoppen vorheriger Threads genutzter Boolean-Wert


   public static void init(long sec, Label label) {          // erzeugt Zeit-Label in Abhängigkeit von der konfigurierten Dauer

      long minutes=sec/60;    //Minutenanzahl

      long seconds=sec%60;    //Sekundenanzahl

      if(seconds>9)  sp.setValue(minutes+":"+seconds);   //Setze den Anfangswert der StringProperty
      else  sp.setValue(minutes+":0"+seconds);

      label.textProperty().bind(sp);     // binde die StringProperty an das Label

   }


   public static void count(Label label){   // zählt die Zeit des Timer-Labels runter

      Thread t = new Thread(() -> {         // damit parallel Aktionen möglich sind, neuen Thread erstellen


         while (!label.getText().equals("0:01")) {  // bis 0:00 erreicht wird, wird die Zeit runtergezählt
            try{

               // wenn ein neuer Thread erstellt wird, alten Thread abbrechen über den im Controller gesetzten Wert stopThread
               // damit nicht doppelt runtergezählt wird
               if(stopThread) break;

               // wenn eine Kompilierung erfolreich war, dann Schleife abbrechen
               if(Controller.successfullCompiling){
                  Controller.successfullCompiling=false;
                  break;
               }

               // halte den Thread eine Sekunde lang an und reduziere die Zeit dann um eine Sekunde
               if(!stopThread)Thread.sleep(1000);
               if(stopThread) break;                    // um zeitliche Verzögerungen zu beseitigen, 2-mal diese Abfrage
               sec=sec-1;                               // runterzählen der Zeit

               // Berechne die aktuelle Zeit im gewünschten Format und setze den Wert der StringProperty
               long minutes=sec/60;
               long seconds= sec%60;
               if(seconds>=10) {
                  Platform.runLater( () -> sp.setValue(minutes + ":" + seconds)); // Platform.runLater behebt das Problem, dass Updates der GUI nicht
               }                                                                  // auf einem Nicht-FX Application Thread ausgeführt werden können
               else{
                  Platform.runLater( () -> sp.setValue(minutes + ":0" + seconds));
               }

               // wenn man zwischenzeitlich in die REFACTOR-Phase gewechselt hat, soll der Timer gestoppt werden
               // refactoring-Variable wird im Controller auf true gesetzt
               if(Controller.refactoring){
                  Controller.refactoring=false;
                  break;
               }
            }
            catch (InterruptedException e) {
               e.printStackTrace();
            }

         }

         // finished-Wert sagt aus, ob der Timer die Zeit bis 0:00 runtergezählt hat
         // denn wenn dies der Fall ist, wird auch ein Thread im Controller beendet
         Controller.finished=true;


      });

      t.start();      // Starte den Thread t

   }

}






