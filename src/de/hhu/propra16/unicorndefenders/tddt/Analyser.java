import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;

import java.util.List;

public class Analyser {

   public Canvas analyse(List<Integer> Zeiten) throws Exception {
      Color[] colors = {Color.BLUE, Color.GREEN, Color.DARKGRAY, Color.ORANGE, Color.RED, Color.DARKORANGE, Color.YELLOW, Color.AQUAMARINE, Color.DARKGOLDENROD, Color.LIME};
      int gesamtzeit = 0;
      int j =0;
      for (int i : Zeiten) {
         gesamtzeit += i;
      }
      Group root = new Group();
      Canvas canvas = new Canvas(400, 300);
      int temp = 0;
      for (int i : Zeiten) {
         canvas.getGraphicsContext2D().setFill(colors[j%10]);
         canvas.getGraphicsContext2D().fillArc(200, 200, 100, 100, 360 * temp / gesamtzeit, 360 * i / gesamtzeit, ArcType.ROUND);
         temp += i;
         j++;
      }
      root.getChildren().add(canvas);
      return canvas;
   }

}
