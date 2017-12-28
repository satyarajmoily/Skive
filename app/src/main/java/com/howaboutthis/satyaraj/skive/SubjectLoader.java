package com.howaboutthis.satyaraj.skive;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.howaboutthis.satyaraj.skive.subjectprovider.SqlDatabase;
import com.howaboutthis.satyaraj.skive.subjectprovider.SubjectContract;

import java.util.ArrayList;

public class SubjectLoader extends AsyncTaskLoader<ArrayList<String>> {

    private SQLiteDatabase mDatabase;

    SubjectLoader(Context context, SQLiteDatabase database) {
        super(context);
        mDatabase = database;
    }

    @Override
    protected void onStartLoading(){
        forceLoad();
    }

    @Override
    public ArrayList<String> loadInBackground() {

        ArrayList<String> subjectList = new ArrayList<>();
        SqlDatabase db = new SqlDatabase(mDatabase);
        String QUERY = db.query(null,"subjectactivity",null);
        Cursor c = mDatabase.rawQuery(QUERY,null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                subjectList.add(c.getString( c.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME)));
                c.moveToNext();
            }
        }

        c.close();

        return subjectList ;
    }
}
