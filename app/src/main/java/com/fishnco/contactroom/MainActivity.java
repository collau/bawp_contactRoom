package com.fishnco.contactroom;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.fishnco.contactroom.adapter.RecyclerViewAdapter;
import com.fishnco.contactroom.model.Contact;
import com.fishnco.contactroom.model.ContactViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnContactClickListener {

    private static final int NEW_CONTACT_ACTIVITY_REQUEST_CODE = 1;
    private static final String TAG = "Clicked";
    private ContactViewModel contactViewModel;
    private TextView textView;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private LiveData<List<Contact>> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Any other form of model provider will lead to an error
        contactViewModel = new ViewModelProvider.AndroidViewModelFactory(MainActivity.this.getApplication())
                .create(ContactViewModel.class);

        //Listens for all the changes
        contactViewModel.getAllContacts().observe(this, new Observer<List<Contact>>() {
            @Override
            public void onChanged(List<Contact> contacts) {

                // Everytime something changes, to run this.
                recyclerViewAdapter = new RecyclerViewAdapter(contacts, MainActivity.this, MainActivity.this);
                recyclerView.setAdapter(recyclerViewAdapter);

//                StringBuilder builder = new StringBuilder();
//                for (Contact contact: contacts)
//                {
//                    builder.append(" - ").append(contact.getName()).append(" ").append(contact.getOccupation());
//                    Log.d("MainActivity", "onCreate: " + contact.getName());
//                    Log.d("MainActivity", "onCreate: " + contact.getId());
//                    Log.d("MainActivity", "onCreate: " + contact.getOccupation());
//                }

            }
        });

        FloatingActionButton buttonAdd = findViewById(R.id.button_addContact);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewContact.class);
                startActivityForResult(intent, NEW_CONTACT_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_CONTACT_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            assert data != null;
            Contact contact = new Contact(data.getStringExtra(NewContact.NAME_REPLY), data.getStringExtra(NewContact.OCCUPATION_REPLY));
            ContactViewModel.insert(contact);

            Log.d("MainActivity", "onActivityResult: " + data.getStringExtra(NewContact.NAME_REPLY));
            Log.d("MainActivity", "onActivityResult: " + data.getStringExtra(NewContact.OCCUPATION_REPLY));
        }
    }

    @Override
    public void onContactClick(int position) {
        Log.d(TAG, "onContactClick: " + position);
        Contact contact = Objects.requireNonNull(contactViewModel.allContacts.getValue()).get(position);
        Log.d(TAG, "onContactClick: " + contact.getName());
    }
}