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

public class Calendar_Event_Adapter extends ArrayAdapter<Calendar_Event_obj> {
    private Context mContext;
    int mResource;

    public Calendar_Event_Adapter(Context context, int resource, ArrayList<Calendar_Event_obj> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String Event_obj_Task = getItem(position).getEvent_obj_Task();
        String Event_obj_Field = getItem(position).getEvent_obj_Field();
        String Event_obj_key = getItem(position).getEvent_obj_key();

        Calendar_Event_obj calendar_event_obj = new Calendar_Event_obj(Event_obj_Task,Event_obj_Field, Event_obj_key);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);

        TextView tv_field = convertView.findViewById(R.id.animals_todo_adapter_DateDown_tv);
        TextView tv_task = convertView.findViewById(R.id.animals_todo_adapter_Task_tv);
        TextView tv_fieldName = convertView.findViewById(R.id.animals_todo_adapter_DateUp_tv);

        tv_fieldName.setText("Field:");
        tv_field.setText(Event_obj_Field);
        tv_task.setText(Event_obj_key);

        return convertView;


    }
}
