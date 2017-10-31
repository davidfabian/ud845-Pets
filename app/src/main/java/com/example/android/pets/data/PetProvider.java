package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.android.pets.R;


/**
 * {@link ContentProvider} for Pets app.
 */
public class PetProvider extends ContentProvider {

    public static final int PETS = 100;
    public static final int PET_ID = 101;
    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);

        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PET_ID);
    }

    private PetDbHelper mDbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new PetDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                cursor = db.query(
                        PetContract.PetEntry.TABLE_NAME,                     // The table to query
                        projection,                               // The columns to return
                        selection,                                // The columns for the WHERE clause
                        selectionArgs,                            // The values for the WHERE clause
                        null,                                     // don't group the rows
                        null,                                     // don't filter by row groups
                        null                                 // The sort order
                );
                break;
            case PET_ID:
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(
                        uri.getLastPathSegment(),                     // The table to query
                        projection,                               // The columns to return
                        selection,                                // The columns for the WHERE clause
                        selectionArgs,                            // The values for the WHERE clause
                        null,                                     // don't group the rows
                        null,                                     // don't filter by row groups
                        null                                 // The sort order
                );
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI" + uri);
        }
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertPet(Uri uri, ContentValues values) throws IllegalArgumentException {

        // Check that the name is not null
        String name = values.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);
        String breed = values.getAsString(PetContract.PetEntry.COLUMN_PET_BREED);
        String weight = values.getAsString(PetContract.PetEntry.COLUMN_PET_WEIGHT);
        if (name.length() == 0 || breed.length() == 0 || weight.length() == 0) {
            Toast toast = Toast.makeText(getContext(), getContext().getString(R.string.adding_pet_error), Toast.LENGTH_SHORT);
            toast.show();
            return uri;
        }

        //create intermittent database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // Insert the new row, returning the primary key value of the new row

        long newRowId = db.insert(PetContract.PetEntry.TABLE_NAME, null, values);
        if (newRowId == -1) {
            Toast toast = Toast.makeText(getContext(), getContext().getString(R.string.adding_pet_error), Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Toast toast = Toast.makeText(getContext(), getContext().getString(R.string.adding_pet_successful) + " " + Long.toString(newRowId), Toast.LENGTH_SHORT);
            toast.show();
        }
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, newRowId);

    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {

        // Check that the name is not null
        String name = contentValues.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);
        String breed = contentValues.getAsString(PetContract.PetEntry.COLUMN_PET_BREED);
        String weight = contentValues.getAsString(PetContract.PetEntry.COLUMN_PET_WEIGHT);
        if (name.length() == 0 || breed.length() == 0 || weight.length() == 0 || contentValues.size() == 0) {
            Toast toast = Toast.makeText(getContext(), getContext().getString(R.string.adding_pet_error), Toast.LENGTH_SHORT);
            toast.show();
            return 0;
        }

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case PET_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        //create intermittent database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        //update and return the number of rows updated
        return db.update(PetContract.PetEntry.TABLE_NAME, values, selection, selectionArgs);
    }


    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                // Delete all rows that match the selection and selection args
                return database.delete(PetContract.PetEntry.TABLE_NAME, selection, selectionArgs);
            case PET_ID:
                // Delete a single row given by the ID in the URI
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return database.delete(PetContract.PetEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }
    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return PetContract.PetEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetContract.PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}