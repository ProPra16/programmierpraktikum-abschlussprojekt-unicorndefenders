import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;


public class BabyStepsConfig{

    public static  StringProperty sp = new SimpleStringProperty(null);

    public static Label init() {
        Label label = new Label();
        sp.setValue("2:00");
        label.textProperty().bind(sp);
        return label;
    }

    public static void start(Label label){
        Thread t = new Thread(() -> {
            long milliseconds=120000;
            while (!label.getText().equals("0:01")) {

                try {
                    Thread.sleep(1000);
                    milliseconds=milliseconds-1000;
                    long minutes=milliseconds/1000/60;
                    long seconds= (milliseconds/1000)%60;
                    if(seconds>=10) {
                        Platform.runLater( () -> sp.setValue(minutes + ":" + seconds)); //behebt Problem, dass Updates nicht auf dem FX Application Thread ausgeführt werden
                    }
                    else{
                        Platform.runLater( () -> sp.setValue(minutes + ":0" + seconds));
                    }
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();

    }



    /* im Controller: if(label.getText().equals("0:00")) stop();
    public static void stop(){
        // ggf. wieder auf sp.setValue("2:00")
        // hier Code zum Löschen des Tests/Codes und Wechsel zum vornagegangenen Zustand
    } */

}







