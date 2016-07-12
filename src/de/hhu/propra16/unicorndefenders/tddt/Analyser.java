package de.hhu.propra16.unicorndefenders.tddt;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import java.util.ArrayList;

import static de.hhu.propra16.unicorndefenders.tddt.Cycle.GREEN;
import static de.hhu.propra16.unicorndefenders.tddt.Cycle.RED;
import de.hhu.propra16.unicorndefenders.tddt.files.File;
import javafx.scene.shape.Shape;

/*
   @author Sebastian
 */

public class Analyser {

   private Pane chart;
   private ArrayList<TrackPoint> trackPoints;
   private File code;
   private File test;

   public Pane getChart() {
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
      Pane canvas = new Pane();
      int temp = 0;
      for (TrackPoint i : this.trackPoints) {

         //Shape arc = new Arc(50, 50, 300, 300, 360 * temp / gesamtzeit, 10);
         Arc arc = new Arc();
         arc.setCenterX(100.0);
         arc.setCenterY(100.0);
         arc.setRadiusX(75.0);
         arc.setRadiusY(75.0);
         arc.setStartAngle(360 * temp / gesamtzeit);
         arc.setLength(360 * (i.getTime()-temp) / gesamtzeit);
         arc.setType(ArcType.ROUND);
         arc.setFill(getColor(i));
         canvas.getChildren().add(arc);
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

   private void changeFiles(TrackPoint trackpoint) {
      this.code=trackpoint.getCode();
      this.test=trackpoint.getTest();
   }
}
