package de.hhu.propra16.unicorndefenders.tddt;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * Created by Sebastian on 04.07.2016.
 */
public class TrackingResultWindow extends Stage {


   public TrackingResultWindow(ArrayList<TrackPoint> Tracking_List) {
      super();

      setTitle("Tracking Result");
      Group root = new Group();
      try {
         root.getChildren().add(Analyser.analyse(Tracking_List));
      } catch (Exception e) {
         System.out.println("Fehler");
      }
      Scene scene = new Scene(root);
      setScene(scene);
   }


}
