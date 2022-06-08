package com.example.seniordesignproject;

public class Animal_Todo_obj {
    private String Task;
    private String Date;

    public  Animal_Todo_obj(String Task, String Date){
        this.Date = Date;
        this.Task = Task;
    }

    public String getTask() {
        return Task;
    }

    public void setTask(String task) {
        Task = task;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
