package com.example.seniordesignproject;

public class Calendar_Event_obj {
    private String Event_obj_Task;
    private String Event_obj_Field;
    private String Event_obj_key;

    public Calendar_Event_obj(String Event_obj_Task, String Event_obj_Field, String Event_obj_key){
        this.Event_obj_Field = Event_obj_Field;
        this.Event_obj_Task = Event_obj_Task;
        this.Event_obj_key = Event_obj_key;
    }

    public String getEvent_obj_Task() {
        return Event_obj_Task;
    }

    public void setEvent_obj_Task(String event_obj_Task) {
        Event_obj_Task = event_obj_Task;
    }

    public String getEvent_obj_Field() {
        return Event_obj_Field;
    }

    public void setEvent_obj_Field(String event_obj_Field) {
        Event_obj_Field = event_obj_Field;
    }

    public String getEvent_obj_key() {
        return Event_obj_key;
    }

    public void setEvent_obj_key(String event_obj_key) {
        Event_obj_key = event_obj_key;
    }
}
