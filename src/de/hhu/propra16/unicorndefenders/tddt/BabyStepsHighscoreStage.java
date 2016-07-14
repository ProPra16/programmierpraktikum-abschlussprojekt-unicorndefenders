package de.hhu.propra16.unicorndefenders.tddt;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.List;

/**
 * Klasse zum Azeigen der Highscores in einer eigenen Stage
 *
 * @author Eyyüp
 */

public class BabyStepsHighscoreStage extends Stage {

    public BabyStepsHighscoreStage(List<String> list) {

        super();

        Group root = new Group();

        Pane pane = new Pane();     // Pane zum Platzieren einer TextArea darin

        TextArea highscores = new TextArea();   // TextArea für die Highscores

        for(int i=0; i<list.size(); i++){

            // füge die Highscores der TextArea hinzu
            if(i<list.size()-1) highscores.appendText(list.get(i)+"\n\n");
            else highscores.appendText(list.get(i));
        }


        pane.getChildren().add(highscores);
        root.getChildren().add(pane);
        Scene scene = new Scene(root);
        setScene(scene);

    }
}

