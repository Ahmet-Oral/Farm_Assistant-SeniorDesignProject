package com.example.seniordesignproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Animal_Todo_Adapter extends ArrayAdapter<Animal_Todo_obj> {
    private Context mContext;
    int mResource;


    public Animal_Todo_Adapter(Context context, int resource, ArrayList<Animal_Todo_obj> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String Date = getItem(position).getDate();
        String Task = getItem(position).getTask();
        String Todo_obj_key = getItem(position).getTodo_obj_key();

        Animal_Todo_obj animal_todo_obj = new Animal_Todo_obj(Date, Task, Todo_obj_key);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tv_date = convertView.findViewById(R.id.animals_todo_adapter_DateDown_tv);
        TextView tv_task = convertView.findViewById(R.id.animals_todo_adapter_Task_tv);
        TextView tv_dateUp = convertView.findViewById(R.id.animals_todo_adapter_DateUp_tv);

        tv_dateUp.setText("Date:");
        tv_date.setText(Date);
        tv_task.setText(Task);

        return convertView;

    }
}
