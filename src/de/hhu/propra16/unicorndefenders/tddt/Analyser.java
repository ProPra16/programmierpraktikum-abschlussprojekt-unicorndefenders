package de.hhu.propra16.unicorndefenders.tddt;

import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import java.util.ArrayList;

import static de.hhu.propra16.unicorndefenders.tddt.Cycle.GREEN;
import static de.hhu.propra16.unicorndefenders.tddt.Cycle.RED;

/*
   @author Sebastian
 */

public class Analyser {

   private Canvas chart;
   private ArrayList<TrackPoint> trackPoints;

   public Canvas getChart() {
      return this.chart;
   }

   public Analyser(ArrayList<TrackPoint> trackPoints){
      this.trackPoints=trackPoints;
   }

   public void toPieChart() throws Exception {
      long gesamtzeit = 0;
      int j =0;
      for (TrackPoint i : this.trackPoints) {
         gesamtzeit += i.getTime();
      }
      Group root = new Group();
      Canvas canvas = new Canvas(400, 400);
      int temp = 0;
      for (TrackPoint i : this.trackPoints) {
         canvas.getGraphicsContext2D().setFill(getColor(i));
         canvas.getGraphicsContext2D().fillArc(50, 50, 300, 300, 360 * temp / gesamtzeit, 360 * i.getTime() / gesamtzeit, ArcType.ROUND);
         temp += i.getTime();
         j++;
      }
      root.getChildren().add(canvas);
      this.chart = canvas;
   }




   public Color getColor(TrackPoint trackpoint) {
      if (trackpoint.getCycle()==RED) return Color.RED;
      if (trackpoint.getCycle()==GREEN) return Color.GREEN;
      return Color.BLACK;
   }


}
