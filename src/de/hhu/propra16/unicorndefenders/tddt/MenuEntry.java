package de.hhu.propra16.unicorndefenders.tddt;

import javafx.beans.property.SimpleStringProperty;

/**
 * Created by Alessa on 26.06.16.
 */
public class MenuEntry {

    private final SimpleStringProperty taskTitle = new SimpleStringProperty("");
    private final SimpleStringProperty babySteps = new SimpleStringProperty("");


    public MenuEntry(){
        this("","");
    }

    public MenuEntry(String title, String bs){
        taskTitle.set(title);
        babySteps.set(bs);
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
}
