import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;


public class BabyStepsConfig{

   public static  StringProperty sp = new SimpleStringProperty(null);   // StringProperty zur Aktualisierung der ablaufenden Zeit

   public static int chosen_time=2;         // gewählte Zeit, Default-Wert= 2 min

      public static Label init() {          // erzeugt Label je nach Wahl der Zeit

         Label label = new Label();
                                            // setze den Anfangswert des Labels
         if(chosen_time==2) sp.setValue("2:00");
         if(chosen_time==3) sp.setValue("3:00");
         if(chosen_time==4) sp.setValue("4:00");

         label.textProperty().bind(sp);     // binde die StringProperty an das Label
         return label;
      }

   public static void start(Label label){   // zählt die Zeit runter

      Thread t = new Thread(() -> {         // damit parallel Aktionen möglich sind, neuen Thread erstellen

         long milliseconds=120000;

         if(chosen_time==2) milliseconds=120000;    // je nach gewählter Dauer wird diese in Millisekunden umgerechnet
         if(chosen_time==3) milliseconds=180000;
         if(chosen_time==4) milliseconds=240000;

         while (!label.getText().equals("0:01")) {  // bis 0:00 erreicht wird, wird die Zeit runtergezählt

            try{

               Thread.sleep(1000);                  // halte den Thread eine Sekunde lang an und reduziere die Zeit dann um eine Sekunde

               milliseconds=milliseconds-1000;
               long minutes=milliseconds/1000/60;
               long seconds= (milliseconds/1000)%60;

               if(seconds>=10) {
                  Platform.runLater( () -> sp.setValue(minutes + ":" + seconds)); // behebt das Problem, dass Updates der GUI
               }                                                                  // nicht auf dem FX Application Thread ausgeführt werden können
               else{
                  Platform.runLater( () -> sp.setValue(minutes + ":0" + seconds));
               }
            }
            catch (InterruptedException e) {
               e.printStackTrace();
            }

         }

      });

        t.start();      // Starte den Thread t

   }

}

/* im Controller: if(label.getText().equals("0:00")) stop();
    public static void stop(){
        // ggf. wieder auf sp.setValue("2:00")
        // hier Code zum Löschen des Tests/Codes und Wechsel zum vornagegangenen Zustand
    }

    wenn auf babysteps geklickt wird, fenster fragt nach min
    je nach dem 2,3,4 min

    dort in einer stop methode die tests/code löschen
    textarea leeren



    */





