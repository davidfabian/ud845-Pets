package com.example.android.pets.data;

import android.provider.BaseColumns;

/**
 * Created by d on 10/2/2017.
 * contract class do define the database used in the pets sehelter app
 */
//contract for the database
public final class PetContract {
    //dummy constructor
    private PetContract() {
    }

    //class for pets table
    public final class PetEntry implements BaseColumns {
        //table name defined
        public static final String TABLE_NAME = "pets";
        //fields in pets table
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PET_NAME = "name";
        public static final String COLUMN_PET_GENDER = "gender";
        public static final String COLUMN_PET_BREED = "breed";
        public static final String COLUMN_PET_WEIGHT = "weight";

        //helper constants for gender
        public static final int GENDER_MALE = 1;
        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_FEMALE = 2;

    }
}
