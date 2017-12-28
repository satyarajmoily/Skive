package com.howaboutthis.satyaraj.skive;


import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.howaboutthis.satyaraj.skive.subjectprovider.SqlDatabase;
import com.howaboutthis.satyaraj.skive.subjectprovider.SubjectContract;

import java.util.ArrayList;
import java.util.List;

public class EditLoader extends AsyncTaskLoader<List<EditListItem>> {

    private SQLiteDatabase mDatabase;

    EditLoader(Context context, SQLiteDatabase database) {
        super(context);
        this.mDatabase = database;
    }

    @Override
    protected void onStartLoading(){
        forceLoad();
    }

    @Override
    public List<EditListItem> loadInBackground() {

        EditListItem mlistItem;
        List<EditListItem> subjectList = new ArrayList<>();
        SqlDatabase db = new SqlDatabase(mDatabase);
        String QUERY = db.query(null,"editactivity",null);
        Cursor c = mDatabase.rawQuery(QUERY,null);


        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                mlistItem = new EditListItem(c.getString( c.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME)), c.getFloat( c.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_PERCENTAGE)),c.getInt( c.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_CLASSES_ATTENDED)),c.getInt( c.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_TOTAL_CLASSES)));
                subjectList.add(mlistItem);
                c.moveToNext();
            }
        }

        c.close();

        return subjectList ;
    }
    }