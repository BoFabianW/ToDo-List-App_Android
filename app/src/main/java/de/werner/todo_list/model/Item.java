package de.werner.todo_list.model;

public class Item {

    private String titel;
    private boolean isDone;

    // Konstruktor.
    public Item() {
        this.isDone = false;
    }

    // #################### GETTER UND SETTER #####################
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
