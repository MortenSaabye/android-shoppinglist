package org.projects.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity implements ClearAllDialogFragment.OnPositiveListener {
    private ArrayList<Product> bag;
    private ItemAdapter adapter;
    private DatabaseReference mDatabase;
    private String uid;
    private FirebaseAuth mAuth;
    private TextView greeting;


    private int spinnerQuantity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        uid = getIntent().getStringExtra("userId");
        greeting = findViewById(R.id.greeting_text);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        if(savedInstanceState != null) {
            bag = savedInstanceState.getParcelableArrayList("list");
        } else {
            bag = new ArrayList<>();
        }
        adapter = new ItemAdapter(this, bag);
        adapter.setUid(this.uid);
        ListView listView = findViewById(R.id.list);
        //setting the adapter on the listview
        listView.setAdapter(adapter);




        //Needed to get the toolbar to work on older versions
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Spinner quantitySpinner = (Spinner) findViewById(R.id.quantity_spinner);
        ArrayAdapter<CharSequence> quantityAdapter = ArrayAdapter.createFromResource(this, R.array.quantities, R.layout.support_simple_spinner_dropdown_item);
        quantitySpinner.setAdapter(quantityAdapter);
        setupSpinnerListener(quantitySpinner);


        // These items are not part of the listView
        final EditText newItemField = (EditText) findViewById(R.id.itemName);
        final EditText quantityField = (EditText) findViewById(R.id.quantity);
        final View parent = findViewById(R.id.layout_root);
        Button addButton = (Button) findViewById(R.id.addButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newItem = newItemField.getText().toString();
                int newQuantity;
                if (!quantityField.getText().toString().isEmpty()) {
                    newQuantity = Integer.parseInt(quantityField.getText().toString());
                } else {
                    newQuantity = spinnerQuantity;
                }
                Product product = new Product(newItem, newQuantity);
                product.setUid(mDatabase.push().getKey());

                mDatabase.child("products").child(product.getUid()).setValue(product);

                newItemField.setText("");
                quantityField.setText("");

            }
        });

        //Delete selected button and feature
        Button deleteSelectedButton = (Button) findViewById(R.id.deleteSelected);
        deleteSelectedButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int numItems = adapter.getSelectedBoxes().size();
                  final ArrayList<Product> productsBackup = new ArrayList<>(bag);
                ArrayList<Product> itemsToDelete = new ArrayList<>();

                for(Map.Entry<Integer,CheckBox> box : adapter.getSelectedBoxes().entrySet()) {
                    Product product = bag.get(box.getKey().intValue());
                    itemsToDelete.add(product);
                }

                for(Product product : itemsToDelete) {
                    mDatabase.child("products").child(product.getUid()).removeValue();
                }
                adapter.clearSelectedItems();

                Snackbar snackbar = Snackbar
                        .make(parent, "Deleted " + numItems + " items.", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //This code will ONLY be executed in case that
                                //the user has hit the UNDO button
                                bag.clear();
                                for(Product product : productsBackup){
                                    mDatabase.child("products").child(product.getUid()).setValue(product);
                                }

                                Snackbar snackbar = Snackbar.make(parent, "Old products restored!", Snackbar.LENGTH_SHORT);

                                //Show the user we have restored the name - but here
                                //on this snackbar there is NO UNDO - so no SetAction method is called
                                //if you wanted, you could include a REDO on the second action button
                                //for instance.
                                snackbar.show();
                                adapter.clearSelectedItems();
                            }
                        });

                snackbar.show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //listener for the list of products
        mDatabase.child("products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bag.clear();
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    Product product = noteDataSnapshot.getValue(Product.class);
                    bag.add(product);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Listener for name of the user
        mDatabase.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    greeting.setText(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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
            mDatabase.child("name").setValue(name);
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
            case R.id.action_log_out:
                this.logOut();
                return true;
            case R.id.action_share:
                this.shareList();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("list", bag);
    }

    public void onPositiveClicked() {
        //Do your update stuff here to the listview
        //and the bag etc
        //just to show how to get arguments from the bag.

        mDatabase.removeValue(); //here you can do stuff with the bag and
        adapter.clearSelectedItems();
        //adapter etc.
        Toast toast = Toast.makeText(this,
                "Cleared all items", Toast.LENGTH_LONG);
        toast.show();
    }

    private void setupSpinnerListener(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            //The AdapterView<?> type means that this can be any type,
            //so we can use both AdapterView<String> or any other
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                //So this code is called when ever the spinner is clicked
                spinnerQuantity = position + 1;
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO you would normally do something here
                // for instace setting the selected item to "null"
                // or something.
            }
        });
    }

    public void clearItems() {
        ClearAllDialog dialog = new ClearAllDialog();
        dialog.show(getSupportFragmentManager(), "ClearAllDialog");
    }

    public static class  ClearAllDialog extends ClearAllDialogFragment {
        @Override
        protected void negativeClick() {
            //Here we override the method and can now do something
            Toast toast = Toast.makeText(getActivity(),
                    "Tragedy avoided", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void logOut()
    {
        mAuth.signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void shareList()
    {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i<adapter.getCount();i++)
        {
            Product p = adapter.getItem(i);
            result.append(p.toString()).append("\n");
        }

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, result.toString());
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
    }
}
