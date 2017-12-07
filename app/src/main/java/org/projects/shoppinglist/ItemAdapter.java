package org.projects.shoppinglist;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by MortenSaabye on 22/09/2017.
 */

public class ItemAdapter extends ArrayAdapter<Product> {
    private ArrayList<Product> shoppingList;
    private DatabaseReference mDatabase;

    public void setUid(String uid) {
        this.uid = uid;
    }

    private String uid;
    private Context context;
    private Map<Integer,CheckBox> checkBoxes = new HashMap<>();
    public Map<Integer,CheckBox> getSelectedBoxes() {
        return checkBoxes;
    }
    public void clearSelectedItems(){
        for(Map.Entry<Integer,CheckBox> box :checkBoxes.entrySet()) {
            box.getValue().setChecked(false);
        }
        checkBoxes.clear();
    }
    public Context getContext() {
        return context;
    }

    public ItemAdapter(@NonNull Context context, ArrayList<Product> shoppingList) {
        super(context, 0,  shoppingList);
        this.shoppingList = shoppingList;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Product item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.shopping_item, parent, false);
        }

        TextView itemName = (TextView) convertView.findViewById(R.id.itemName);
        itemName.setText(item.toString());


        Button deleteBtn = (Button) convertView.findViewById(R.id.deleteBtn);
        final ItemAdapter adapter = this;
        this.mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(this.uid).child("products");
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = shoppingList.get(position).getUid();
                mDatabase.child(id).removeValue();
                adapter.clearSelectedItems();
            }
        });

        final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shoppingList.get(position);
                if (checkBoxes.get(position) != null) {
                    checkBoxes.remove(position);
                } else {
                    checkBoxes.put(position, checkBox);
                }
            }
        });

        return convertView;
    }
}
