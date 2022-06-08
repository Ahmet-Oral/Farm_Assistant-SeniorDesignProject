package com.example.seniordesignproject;

public class Animal_Todo_obj {
    private String Task;
    private String Date;
    private String Todo_obj_key;

    public Animal_Todo_obj(String Task, String Date, String Todo_obj_key){
        this.Date = Date;
        this.Task = Task;
        this.Todo_obj_key = Todo_obj_key;
    }

    public String getTodo_obj_key() {
        return Todo_obj_key;
    }

    public void setTodo_obj_key(String key_Todo) {
        Todo_obj_key = key_Todo;
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
