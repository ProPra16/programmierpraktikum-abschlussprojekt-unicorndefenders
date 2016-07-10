import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

/*
   @author Sebastian
 */

import java.util.List;

public class Analyser {

   public static Canvas analyse(List<Integer> Zeiten) throws Exception {
      Color[] colors = {Color.BLUE, Color.GREEN, Color.DARKGRAY, Color.ORANGE, Color.RED, Color.DARKORANGE, Color.YELLOW, Color.AQUAMARINE, Color.DARKGOLDENROD, Color.LIME};
      int gesamtzeit = 0;
      int j =0;
      for (int i : Zeiten) {
         gesamtzeit += i;
      }
      Group root = new Group();
      Canvas canvas = new Canvas(400, 400);
      int temp = 0;
      for (int i : Zeiten) {
         canvas.getGraphicsContext2D().setFill(colors[j%10]);
         canvas.getGraphicsContext2D().fillArc(50, 50, 300, 300, 360 * temp / gesamtzeit, 360 * i / gesamtzeit, ArcType.ROUND);
         temp += i;
         j++;
      }
      root.getChildren().add(canvas);
      return canvas;
   }

}
