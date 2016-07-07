import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;
import java.util.List;

public class Analyser {

    public Canvas analyse(List<Integer> Zeiten) throws Exception{
        int gesamtzeit=0;
        for (int i: Zeiten) {
            gesamtzeit +=i;
        }
        Group root = new Group();
        Canvas canvas = new Canvas(400, 300);
        int temp =0;
        for (int i: Zeiten) {
            canvas.getGraphicsContext2D().setFill(Color.GREEN);
            canvas.getGraphicsContext2D().fillArc(200, 200, 100, 100, 360*temp/gesamtzeit, 360*i/gesamtzeit, ArcType.ROUND);
            temp+=i;
        }
        root.getChildren().add(canvas);
        return canvas;
    }

}
