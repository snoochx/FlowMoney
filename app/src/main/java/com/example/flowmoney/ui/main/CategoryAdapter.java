package com.example.flowmoney.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.flowmoney.R;
import com.example.flowmoney.data.CategoryType;

public class CategoryAdapter extends ArrayAdapter<CategoryType> {

    private final LayoutInflater inflater;

    public CategoryAdapter(@NonNull Context context) {
        super(context, 0, CategoryType.values());
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    private View createView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_category, parent, false);
        }

        CategoryType category = getItem(position);
        ImageView icon = convertView.findViewById(R.id.ivCategoryIcon);
        TextView title = convertView.findViewById(R.id.tvCategoryTitle);

        if (category != null) {
            icon.setImageResource(category.iconRes);
            title.setText(category.title);
        }

        return convertView;
    }
}
