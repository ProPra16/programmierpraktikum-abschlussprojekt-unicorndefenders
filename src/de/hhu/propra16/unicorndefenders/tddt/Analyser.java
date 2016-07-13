package de.hhu.propra16.unicorndefenders.tddt;

import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextArea;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
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
   public TextArea code = new TextArea();
   public TextArea test = new TextArea();
   public String temp_code="";
   public String temp_test="";

   public Pane getChart() {
      return this.chart;
   }


   public Analyser(ArrayList<TrackPoint> trackPoints) {
      this.trackPoints = trackPoints;
   }

   public void toPieChart() throws Exception {
      long gesamtzeit = 0;
      int j = 0;
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
         arc.setLength(360 * (i.getTime() - temp) / gesamtzeit);
         arc.setType(ArcType.ROUND);
         arc.setFill(getColor(i));

         arc.setOnMouseClicked(event -> {
            if (event.getEventType().equals(MouseEvent.MOUSE_CLICKED)) {
               this.temp_code=i.getCode().getContent();
               this.temp_test=i.getTest().getContent();
               this.code.setText(i.getCode().getContent());
               this.test.setText(i.getTest().getContent());
            }
         });

         arc.setOnMouseEntered(event -> {

            if(event.getEventType().equals(MouseEvent.MOUSE_ENTERED)) {
               this.temp_code=this.code.getText();
               this.temp_test=this.test.getText();
               this.code.setText(i.getCode().getContent());
               this.test.setText(i.getTest().getContent());
            }

         });

         arc.setOnMouseExited(event -> {

            if(event.getEventType().equals(MouseEvent.MOUSE_EXITED)) {
               this.code.setText(this.temp_code);
               this.test.setText(this.temp_test);
            }
         });
         canvas.getChildren().add(arc);
         temp += i.getTime();
         j++;

      }

      root.getChildren().add(canvas);
      this.chart = canvas;
   }


   public Color getColor(TrackPoint trackpoint) {
      if (trackpoint.getCycle() == RED) return Color.RED;
      if (trackpoint.getCycle() == GREEN) return Color.GREEN;
      return Color.BLACK;
   }


}
