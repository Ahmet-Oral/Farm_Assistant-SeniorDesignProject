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

public class Animal_Feature_Adapter extends ArrayAdapter<Animal_Feature> {
    private Context mContext;
    int mResource;


    public Animal_Feature_Adapter(Context context, int resource, ArrayList<Animal_Feature> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String feature = getItem(position).getFeature();
        String value = getItem(position).getValue();

        Animal_Feature animal_feature = new Animal_Feature(feature, value);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tv_feature = convertView.findViewById(R.id.animal_feature_adapter_feature);
        TextView tv_value = convertView.findViewById(R.id.animal_feature_adapter_value);

        tv_feature.setText(feature);
        tv_value.setText(value);
        return convertView;
    }
}
