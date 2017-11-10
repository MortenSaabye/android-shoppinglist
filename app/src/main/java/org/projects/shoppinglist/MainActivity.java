package org.projects.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> bag;
    private ItemAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Needed to get the toolbar to work on older versions
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(savedInstanceState != null) {
            bag = savedInstanceState.getStringArrayList("list");
        } else {
            bag = new ArrayList<>();
        }

        adapter = new ItemAdapter(this, bag);
        //getting our listiew - you can check the ID in the xml to see that it
        //is indeed specified as "list"
        ListView listView = (ListView) findViewById(R.id.list);

        //setting the adapter on the listview
        listView.setAdapter(adapter);

        // These items are not part of the listView
        final EditText newItemField = (EditText) findViewById(R.id.itemName);
        final EditText quantityField = (EditText) findViewById(R.id.quantity);
        Button addButton = (Button) findViewById(R.id.addButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newItem = newItemField.getText().toString();
                String quantity = quantityField.getText().toString();
                bag.add(newItem + " " + quantity);
                newItemField.setText("");
                quantityField.setText("");
                //The next line is needed in order to say to the ListView
                //that the data has changed - we have added stuff now!
                adapter.notifyDataSetChanged();
            }
        });

        //Delete selected button and feature
        Button deleteSelectedButton = (Button) findViewById(R.id.deleteSelected);
        deleteSelectedButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("select Activity", adapter.getSelectedItems() + "");
                Log.d("select activity"," size: "+adapter.getSelectedItems().size());
                Log.d("select activity","bag size: "+bag.size());
                for(Integer index : adapter.getSelectedItems()) {
                    Log.d("select activity","index: "+index.toString());
                    bag.remove(index.intValue());
                }
                adapter.notifyDataSetChanged();
            }
        });

        //add some stuff to the list so we have something
        // to show on app startup

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==1) //the code means we came back from settings
        {
            //I can can these methods like this, because they are static
            String name = MyPreferenceFragment.getName(this);
            String message = "Welcome, "+name;
            Toast toast = Toast.makeText(this,message,Toast.LENGTH_LONG);
            toast.show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id) {
            case R.id.action_settings :
                Intent intent = new Intent(this,SettingsActivity.class);
                startActivityForResult(intent,1);
                return true;
            case R.id.action_clear_all :
                this.clearItems();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("list", bag);
    }

    public void clearItems() {
        bag.clear();
        adapter.notifyDataSetChanged();
    }

}
