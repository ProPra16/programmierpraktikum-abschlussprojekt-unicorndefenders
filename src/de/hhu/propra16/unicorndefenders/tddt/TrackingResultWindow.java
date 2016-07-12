package de.hhu.propra16.unicorndefenders.tddt;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
      HBox hBox = new HBox();
      Analyser analyser = new Analyser(Tracking_List);
      try {
         analyser.toPieChart();
         hBox.getChildren().add(analyser.getChart());
      } catch (Exception e) {
         System.out.println("Fehler");
      }

      VBox vBox = new VBox();
      TextArea code = new TextArea();
      TextArea test = new TextArea();

      vBox.getChildren().add(code);
      vBox.getChildren().add(test);
      hBox.getChildren().add(vBox);

      root.getChildren().add(hBox);
      Scene scene = new Scene(root);
      setScene(scene);
   }


}
