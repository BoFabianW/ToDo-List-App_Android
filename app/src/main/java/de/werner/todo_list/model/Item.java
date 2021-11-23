package de.werner.todo_list.model;

public class Item {

    private long id;
    private String titel;
    private boolean isDone;

    // Konstruktor.
    public Item(String titel) {
        this.titel = titel;
        this.isDone = false;
    }

    // #################### GETTER UND SETTER #####################

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
