package de.werner.todo_list.model;

import de.werner.todo_list.view.MainActivity;

public class Item {

    private String titel;
    private boolean isDone;

    public Item() {
        this.isDone = false;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
