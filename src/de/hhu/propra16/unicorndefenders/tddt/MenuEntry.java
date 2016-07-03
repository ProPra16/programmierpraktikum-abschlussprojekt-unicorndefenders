package de.hhu.propra16.unicorndefenders.tddt;

import de.hhu.propra16.unicorndefenders.tddt.config.Exercise;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by Alessa on 26.06.16.
 */
public class MenuEntry {

    private final SimpleStringProperty taskTitle = new SimpleStringProperty("");
    private final SimpleStringProperty babySteps = new SimpleStringProperty("");
    private Exercise exercise;


    public MenuEntry(){ this("","", null);
    }

    public MenuEntry(String title, String bs, Exercise exercise){
        taskTitle.set(title);
        babySteps.set(bs);
        this.exercise= exercise;
    }

    public String getTaskTitle() {
        return taskTitle.get();
    }

    public String getBabySteps() {
        return babySteps.get();
    }

    public void setBabySteps(String bs) {
        babySteps.set(bs);
    }

    public void setTaskTitle(String title) {
        taskTitle.set(title);
    }

    public Exercise getExercise() {
        return exercise;
    }
}
