package com.howaboutthis.satyaraj.skive;


import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.howaboutthis.satyaraj.skive.subjectprovider.SqlDatabase;
import com.howaboutthis.satyaraj.skive.subjectprovider.TimetableContract;

import java.util.ArrayList;
import java.util.Objects;

public class AddTimetableLoader extends AsyncTaskLoader<ArrayList<String>> {

    private SQLiteDatabase mDatabase;
    private String dayOfTheWeek;

    AddTimetableLoader(Context context, SQLiteDatabase database, String day) {
        super(context);
        this.mDatabase = database;
        this.dayOfTheWeek = day;
    }

    @Override
    protected void onStartLoading(){
        forceLoad();
    }

    @Override
    public ArrayList<String> loadInBackground() {


        SqlDatabase sqlDatabase = new SqlDatabase(mDatabase);

        if(Objects.equals(dayOfTheWeek, "Monday")){
            dayOfTheWeek = TimetableContract.TimetableEntry.TABLE_MONDAY;

        }else if (Objects.equals(dayOfTheWeek, "Tuesday")){
            dayOfTheWeek = TimetableContract.TimetableEntry.TABLE_TUESDAY;

        }else if (Objects.equals(dayOfTheWeek, "Wednesday")){
            dayOfTheWeek = TimetableContract.TimetableEntry.TABLE_WEDNESDAY;

        }else if (Objects.equals(dayOfTheWeek, "Thursday")){
            dayOfTheWeek = TimetableContract.TimetableEntry.TABLE_THURSDAY;

        }else if (Objects.equals(dayOfTheWeek, "Friday")){
            dayOfTheWeek = TimetableContract.TimetableEntry.TABLE_FRIDAY;

        }else if (Objects.equals(dayOfTheWeek, "Saturday")){
            dayOfTheWeek = TimetableContract.TimetableEntry.TABLE_SATURDAY;

        }else if (Objects.equals(dayOfTheWeek, "Sunday")){
            dayOfTheWeek = TimetableContract.TimetableEntry.TABLE_SUNDAY;

        }

        String QUERY = sqlDatabase.query(dayOfTheWeek,"addtimetable", null);
        Cursor c = mDatabase.rawQuery(QUERY,null);
        ArrayList<String> subjectList = new ArrayList<>();


        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                subjectList.add( c.getString( c.getColumnIndex(TimetableContract.TimetableEntry.COLUMN)) );
                c.moveToNext();
            }
        }
        c.close();

        return subjectList;
    }
}
