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

public class AnimalListAdapter extends ArrayAdapter<Animal_Field> {

    private Context mContext;
    int mResource;

    public AnimalListAdapter(Context context, int resource, ArrayList<Animal_Field> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = getItem(position).getName();
        String number = getItem(position).getNumber();

        Animal_Field animal_field = new Animal_Field(name, number);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tv_name = convertView.findViewById(R.id.animals_info_name);
        TextView tv_number = convertView.findViewById(R.id.animals_info_number);

        tv_name.setText(name);
        tv_number.setText(number);
        return convertView;
    }
}
