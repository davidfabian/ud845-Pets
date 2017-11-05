package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by d on 10/31/2017.
 */

public class PetCursorAdapter extends CursorAdapter {

    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    //inflates a new view for a new item. Does not attach content
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    //populate the current view with data from cursor
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.pet_name);
        TextView breedTextView = (TextView) view.findViewById(R.id.pet_breed);

        String nameText = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        String breedText = cursor.getString(cursor.getColumnIndexOrThrow("breed"));

        nameTextView.setText(nameText);
        if (!TextUtils.isEmpty(breedText)) {
            breedTextView.setText(breedText);
        } else {
            breedTextView.setText("unknown breed");
        }
    }
}
