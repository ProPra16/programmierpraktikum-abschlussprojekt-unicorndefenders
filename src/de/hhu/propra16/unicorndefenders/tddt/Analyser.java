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
import static de.hhu.propra16.unicorndefenders.tddt.Cycle.REFACTOR;

import de.hhu.propra16.unicorndefenders.tddt.files.File;
import javafx.scene.text.Text;

/*
  * @author Sebastian
  *
  * In diesem Objekt wird sowohl das angezeigte Chart,
  * als auch die angezeigten Textfelder gespeichert.
  *
 */

public class Analyser {

   public Group chart= new Group();
   private ArrayList<TrackPoint> trackPoints;
   public TextArea code = new TextArea();
   public TextArea test = new TextArea();
   public String temp_code="";
   public String temp_test="";

   public Group getChart() {
      return this.chart;
   }


   public Analyser(ArrayList<TrackPoint> trackPoints) {
      this.trackPoints = trackPoints;
   }

   /*
   * Die Methode toPieChart() ändert das angezeigte Diagramm
   * zu einem Kreisdiagramm
    */

   public void toPieChart() throws Exception {
      long gesamtzeit = 0;
      int j = 0;
      for (TrackPoint i : this.trackPoints) {
         gesamtzeit += i.getTime();
      }

      this.chart.getChildren().remove(0, this.chart.getChildren().size());
      int temp = 0;
      for (TrackPoint i : this.trackPoints) {
         //Bildet die Dauer einer Phase ab
         Text text = new Text(""+temp);
         text.setX(100+90*Math.cos(2*Math.PI*temp / gesamtzeit));
         text.setY(100-90*Math.sin(2*Math.PI*temp/gesamtzeit));
         this.chart.getChildren().add(text);

         //Erstellt ein Kreissegment
         Arc arc = new Arc();
         arc.setCenterX(100.0);
         arc.setCenterY(100.0);
         arc.setRadiusX(75.0);
         arc.setRadiusY(75.0);
         arc.setStartAngle(360 * temp / gesamtzeit);
         arc.setLength(360 * i.getTime() / gesamtzeit);
         arc.setType(ArcType.ROUND);
         arc.setFill(getColor(i));

         //Trennt zwei Phasen durch eine Linie voneinander
         Line line = new Line();
         line.setStartX(100);
         line.setStartY(100);
         line.setEndX(100+75*Math.cos(2*Math.PI*temp / gesamtzeit));
         line.setEndY(100-75*Math.sin(2*Math.PI*temp/gesamtzeit));
         line.setFill(Color.BLACK);
         this.chart.getChildren().add(line);

         /*
         * Wenn auf die Phase geklickt wird,
         * wird der Inhalt derTextfelder durch den Code/Test
         * der jeweiligen Phase überschrieben
         *
         * Diese TextFelder werden im TrackingResultwindow geladen
         */

         arc.setOnMouseClicked(event -> {
            if (event.getEventType().equals(MouseEvent.MOUSE_CLICKED)) {
               this.temp_code=i.getCode().getContent();
               this.temp_test=i.getTest().getContent();
               this.code.setText(i.getCode().getContent());
               this.test.setText(i.getTest().getContent());
            }
         });

         /*
         * Wenn der Mauscursor in die Phase eintritt, wird der
         * Inhalt des Textfeldes temporär geändert
          */

         arc.setOnMouseEntered(event -> {

            if(event.getEventType().equals(MouseEvent.MOUSE_ENTERED)) {
               this.temp_code=this.code.getText();
               this.temp_test=this.test.getText();
               this.code.setText(i.getCode().getContent());
               this.test.setText(i.getTest().getContent());
            }

         });

         /*
         * Wenn der Mauscursor die Phase verlässt,
         * wird der Inhalt des Textfeldes auf den
         * ursprünglichen Zustand zurückgesetzt.
          */

         arc.setOnMouseExited(event -> {

            if(event.getEventType().equals(MouseEvent.MOUSE_EXITED)) {
               this.code.setText(this.temp_code);
               this.test.setText(this.temp_test);
            }
         });
         this.chart.getChildren().add(arc);

         temp += i.getTime();

         if(temp==gesamtzeit) {
            Text text2 = new Text(""+gesamtzeit);
            text2.setX(180);
            text2.setY(112);
            this.chart.getChildren().add(text2);
         }

         j++;
      }

   }

   /*
   * Die Methode toBar() ändert das angezeigte Diagramm
    * zu einer Leiste, in der alle Phasen angezeigt werden.
    */

   public void toBar() throws Exception {
      long gesamtzeit = 0;
      int j = 0;
      int length=500;
      for (TrackPoint i : this.trackPoints) {
         gesamtzeit += i.getTime();
      }
      this.chart.getChildren().add(new Rectangle());
      this.chart.getChildren().remove(0, this.chart.getChildren().size());
      int temp = 0;
      for (TrackPoint i : this.trackPoints) {
         //Bildet die Dauer einer Phase ab
         Text text = new Text(""+temp);
         text.setX(50);
         text.setY(length*temp/gesamtzeit);
         this.chart.getChildren().add(text);

         //Erstellt ein Rechteck
         Rectangle rectangle = new Rectangle();
         rectangle.setFill(getColor(i));
         rectangle.setX(25);
         rectangle.setY(length*temp/gesamtzeit);
         rectangle.setHeight(length*i.getTime()/gesamtzeit);
         rectangle.setWidth(20);

         //Trennt zwei Phasen durch eine Linie voneinander
         Line line = new Line();
         line.setStartX(25);
         line.setStartY(length*temp/gesamtzeit);
         line.setEndX(45);
         line.setEndY(length*temp/gesamtzeit);
         line.setFill(Color.BLACK);
         this.chart.getChildren().add(line);

         /*
         * Wenn auf die Phase geklickt wird,
         * wird der Inhalt derTextfelder durch den Code/Test
         * der jeweiligen Phase überschrieben
         *
         * Diese TextFelder werden im TrackingResultwindow geladen
         */

         rectangle.setOnMouseClicked(event -> {
            if (event.getEventType().equals(MouseEvent.MOUSE_CLICKED)) {
               this.temp_code=i.getCode().getContent();
               this.temp_test=i.getTest().getContent();
               this.code.setText(i.getCode().getContent());
               this.test.setText(i.getTest().getContent());
            }
         });

         /*
         * Wenn der Mauscursor in die Phase eintritt, wird der
         * Inhalt des Textfeldes temporär geändert
          */

         rectangle.setOnMouseEntered(event -> {

            if(event.getEventType().equals(MouseEvent.MOUSE_ENTERED)) {
               this.temp_code=this.code.getText();
               this.temp_test=this.test.getText();
               this.code.setText(i.getCode().getContent());
               this.test.setText(i.getTest().getContent());
            }

         });

         /*
         * Wenn der Mauscursor die Phase verlässt,
         * wird der Inhalt des Textfeldes auf den
         * ursprünglichen Zustand zurückgesetzt.
          */

         rectangle.setOnMouseExited(event -> {

            if(event.getEventType().equals(MouseEvent.MOUSE_EXITED)) {
               this.code.setText(this.temp_code);
               this.test.setText(this.temp_test);
            }
         });

         this.chart.getChildren().add(rectangle);

         temp += i.getTime();
         if(temp==gesamtzeit) {
            Text text2 = new Text(""+gesamtzeit);
            text2.setX(50);
            text2.setY(length);
            this.chart.getChildren().add(text2);
         }

         j++;
      }
   }

   public void toBarChart() throws Exception {
      long maxzeit = 0;
      int k = 1;
      int length=450;
      ArrayList<ArrayList<TrackPoint>> phasen= new ArrayList<ArrayList<TrackPoint>>();
      ArrayList<TrackPoint> temp_list= new ArrayList<TrackPoint>();
      for(TrackPoint i:this.trackPoints) {
         if(i.getCycle()==RED) {
            phasen.add(temp_list);
            temp_list= new ArrayList<TrackPoint>();
            temp_list.add(i);
         }
         else {
            temp_list.add(i);
         }
      }
      phasen.add(temp_list);


      for(ArrayList<TrackPoint> i:phasen) {
         long temp_zeit=0;
         for(TrackPoint j:i) {
            temp_zeit+=j.getTime();
         }
         if(temp_zeit>maxzeit) maxzeit=temp_zeit;
      }

      this.chart.getChildren().add(new Rectangle());
      this.chart.getChildren().remove(0, this.chart.getChildren().size());

      Line y_achse = new Line();
      y_achse.setStartX(50);
      y_achse.setStartY(length+20);
      y_achse.setEndX(50);
      y_achse.setEndY(0);
      y_achse.setFill(Color.BLACK);
      y_achse.setStrokeWidth(2.5);
      this.chart.getChildren().add(y_achse);

      for (ArrayList<TrackPoint> j:phasen) {
         long temp_zeit=0;
         for (TrackPoint i : j) {
            int temp = 0;

            /*
            //Bildet die Dauer eines Zyklusses ab
            Text text = new Text("" + temp);
            text.setX(50);
            text.setY(length * temp / maxzeit);
            this.chart.getChildren().add(text);
            */
            //Erstellt ein Rechteck
            Rectangle rectangle = new Rectangle();
            rectangle.setFill(getColor(i));
            rectangle.setX(k*50);
            rectangle.setY(length-length * (i.getTime()+temp_zeit) / maxzeit);
            rectangle.setHeight(length * i.getTime() / maxzeit);
            rectangle.setWidth(30);


            //Trennt zwei Phasen durch eine Linie voneinander
            Line line = new Line();
            line.setStartX(k*50);
            line.setStartY(length-length * (i.getTime()+temp_zeit) / maxzeit);
            line.setEndX(k*50+30);
            line.setEndY(length-length * (i.getTime()+temp_zeit) / maxzeit);
            line.setFill(Color.BLACK);
            this.chart.getChildren().add(line);

         /*
         * Wenn auf die Phase geklickt wird,
         * wird der Inhalt derTextfelder durch den Code/Test
         * der jeweiligen Phase überschrieben
         *
         * Diese TextFelder werden im TrackingResultwindow geladen
         */

            rectangle.setOnMouseClicked(event -> {
               if (event.getEventType().equals(MouseEvent.MOUSE_CLICKED)) {
                  this.temp_code = i.getCode().getContent();
                  this.temp_test = i.getTest().getContent();
                  this.code.setText(i.getCode().getContent());
                  this.test.setText(i.getTest().getContent());
               }
            });

         /*
         * Wenn der Mauscursor in die Phase eintritt, wird der
         * Inhalt des Textfeldes temporär geändert
          */

            rectangle.setOnMouseEntered(event -> {

               if (event.getEventType().equals(MouseEvent.MOUSE_ENTERED)) {
                  this.temp_code = this.code.getText();
                  this.temp_test = this.test.getText();
                  this.code.setText(i.getCode().getContent());
                  this.test.setText(i.getTest().getContent());
               }

            });

         /*
         * Wenn der Mauscursor die Phase verlässt,
         * wird der Inhalt des Textfeldes auf den
         * ursprünglichen Zustand zurückgesetzt.
          */

            rectangle.setOnMouseExited(event -> {

               if (event.getEventType().equals(MouseEvent.MOUSE_EXITED)) {
                  this.code.setText(this.temp_code);
                  this.test.setText(this.temp_test);
               }
            });

            this.chart.getChildren().add(rectangle);
/*
            temp += i.getTime();
            if (temp == maxzeit) {
               Text text2 = new Text("" + maxzeit);
               text2.setX(50);
               text2.setY(length);
               this.chart.getChildren().add(text2);
            }
*/
            temp_zeit+=i.getTime();
         }
         Line x_achse_abschnitt = new Line();
         x_achse_abschnitt.setStartX(k*50+15);
         x_achse_abschnitt.setStartY(length+18);
         x_achse_abschnitt.setEndX(k*50+15);
         x_achse_abschnitt.setEndY(length+22);
         x_achse_abschnitt.setFill(Color.BLACK);
         x_achse_abschnitt.setStrokeWidth(1);
         this.chart.getChildren().add(x_achse_abschnitt);


         k++;
         /*
         *Zeichnet die x-Achse des KoordinatenSystems
          */
         Line x_achse = new Line();
         x_achse.setStartX((k-1)*50);
         x_achse.setStartY(length+20);
         x_achse.setEndX((k+1)*50);
         x_achse.setEndY(length+20);
         x_achse.setFill(Color.BLACK);
         x_achse.setStrokeWidth(2.5);
         this.chart.getChildren().add(x_achse);

         if(k>1) {
            Text text_phase = new Text("" + (k - 1));
            text_phase.setX(50 * k + 15);
            text_phase.setY(length + 50);
            this.chart.getChildren().add(text_phase);
         }
      }
   }






   /*
   * Gibt zu einer Phase die entsprechende Farbe als Color zurück
   */

   public Color getColor(TrackPoint trackpoint) {
      if (trackpoint.getCycle() == RED) return Color.RED;
      if (trackpoint.getCycle() == GREEN) return Color.GREEN;
      return Color.BLACK;
   }


}
