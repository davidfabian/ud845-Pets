/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDbHelper;

import java.util.Random;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {
    private String LOG_TAG = getClass().getName();
    private PetDbHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        PetDbHelper mHelper = new PetDbHelper(this);
        displayDatabaseInfo();


    }

    //returns a simple cursor from database containing all pet names and corresponding breeds.
    private Cursor nameAndBreedCursor() {
        String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED
        };
        return getContentResolver().query(
                PetEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);

    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {
        //find basic listview to inflate
        ListView petList = (ListView) findViewById(R.id.list_view_pet);

        //set up empty view
        View emptyView = findViewById(R.id.empty_view);
        petList.setEmptyView(emptyView);

        //get adapter to fill listview with content
        PetCursorAdapter petAdapter = new PetCursorAdapter(this, nameAndBreedCursor());
        //attach adapter to listview
        petList.setAdapter(petAdapter);



    }

    private void insertPet() {
        Random rand = new Random();
        int randomValue = rand.nextInt(51);
        ContentValues contentvalues = new ContentValues();

        contentvalues.put(PetContract.PetEntry.COLUMN_PET_NAME, "Dummy" + randomValue);
        contentvalues.put(PetContract.PetEntry.COLUMN_PET_GENDER, PetContract.PetEntry.GENDER_MALE);
        contentvalues.put(PetContract.PetEntry.COLUMN_PET_BREED, "RandomBreed" + randomValue);
        contentvalues.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, randomValue);

        Uri returnUri = getContentResolver().insert(PetEntry.CONTENT_URI, contentvalues);

    }

    private void deleteAll() {
        mHelper = new PetDbHelper(this);
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(PetEntry.TABLE_NAME, null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                deleteAll();
                displayDatabaseInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);


    }

    //to update the display of main activity after editing an entry
    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }
}