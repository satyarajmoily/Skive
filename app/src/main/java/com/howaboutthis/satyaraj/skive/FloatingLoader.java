package com.howaboutthis.satyaraj.skive;


import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.howaboutthis.satyaraj.skive.subjectprovider.SqlDatabase;
import com.howaboutthis.satyaraj.skive.subjectprovider.SubjectContract;

import java.util.ArrayList;

public class FloatingLoader extends AsyncTaskLoader<ArrayList<String>> {

    private SQLiteDatabase mDatabase;

    FloatingLoader(Context context, SQLiteDatabase database) {
        super(context);
        this.mDatabase = database;
    }

    @Override
    protected void onStartLoading(){
        forceLoad();
    }

    @Override
    public ArrayList<String> loadInBackground() {

        SqlDatabase db = new SqlDatabase(mDatabase);
        ArrayList<String> values = new ArrayList<>();
        String QUERY = db.query(null, "floatingbutton",null);

        Cursor c = mDatabase.rawQuery(QUERY, null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                values.add( c.getString( c.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME)) );
                c.moveToNext();
            }
        }

        c.close();

        return values;
    }
}
