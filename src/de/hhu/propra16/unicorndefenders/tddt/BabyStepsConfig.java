import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;


public class BabyStepsConfig{

   public static  StringProperty sp = new SimpleStringProperty(null);   // StringProperty zur Aktualisierung der ablaufenden Zeit
   public static long sec;

   public static Label init(long sec) {          // erzeugt Zeit-Label in Abhängigkeit von der konfigurierten Dauer

      Label label = new Label();
                                            // berechne den Anfangswert des Labels
      long minutes=sec/60;
      long seconds=sec%60;

      if(seconds>9) sp.setValue(minutes+":"+seconds);
      else sp.setValue(minutes+":0"+seconds);

      label.textProperty().bind(sp);     // binde die StringProperty an das Label
      return label;
   }


   public static void count(Label label){   // zählt die Zeit runter

      Thread t = new Thread(() -> {         // damit parallel Aktionen möglich sind, neuen Thread erstellen


         while (!label.getText().equals("0:01")) {  // bis 0:00 erreicht wird, wird die Zeit runtergezählt

            try{

               Thread.sleep(1000);                  // halte den Thread eine Sekunde lang an und reduziere die Zeit dann um eine Sekunde

               sec=sec-1;
               long minutes=sec/60;
               long seconds= sec%60;

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






