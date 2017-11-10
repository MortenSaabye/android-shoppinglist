package org.projects.shoppinglist;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by MortenSaabye on 22/09/2017.
 */

public class ItemAdapter extends ArrayAdapter<String> {
    private ArrayList<String> shoppingList;
    private ArrayList<Integer> selectedItems = new ArrayList<>();

    public ArrayList<Integer> getSelectedItems() {
        return selectedItems;
    }
    public ItemAdapter(@NonNull Context context, ArrayList<String> shoppingList) {
        super(context, 0,  shoppingList);
        this.shoppingList = shoppingList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final String item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.shopping_item, parent, false);
        }

        TextView itemName = (TextView) convertView.findViewById(R.id.itemName);
        itemName.setText(item);

        Button deleteBtn = (Button) convertView.findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shoppingList.remove(shoppingList.get(position));
                notifyDataSetChanged();
            }
        });

        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer selectedInteger = new Integer(position);
                if (selectedItems.contains(selectedInteger)) {
                    selectedItems.remove(position);
                } else {
                    selectedItems.add(position);
                }
                Log.d("select", "" + position);

            }
        });


        return convertView;
    }

}
