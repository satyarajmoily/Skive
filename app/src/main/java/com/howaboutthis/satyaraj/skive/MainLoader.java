package com.howaboutthis.satyaraj.skive;

import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.howaboutthis.satyaraj.skive.subjectprovider.SqlDatabase;
import com.howaboutthis.satyaraj.skive.subjectprovider.SubjectContract;
import com.howaboutthis.satyaraj.skive.subjectprovider.TimetableContract;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainLoader extends AsyncTaskLoader {

    private SQLiteDatabase mDatabase;
    private int loader;
    private int totalClasses;


    MainLoader(Context context, SQLiteDatabase database, int loader, int totalClasses) {
        super(context);
        this.mDatabase = database;
        this.loader = loader;
        this.totalClasses = totalClasses;

    }

    @Override
    protected void onStartLoading(){
        forceLoad();
    }

    @Override
    public Object loadInBackground() {
        if (loader == 0) {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);

            SqlDatabase database = new SqlDatabase(mDatabase);
            String dayOfTheWeek = null;
            String previousDay = null;

            switch (day) {
                case Calendar.SUNDAY:
                    dayOfTheWeek = TimetableContract.TimetableEntry.TABLE_SUNDAY;
                    previousDay = TimetableContract.TimetableEntry.TABLE_SATURDAY;
                    break;

                case Calendar.MONDAY:
                    dayOfTheWeek = TimetableContract.TimetableEntry.TABLE_MONDAY;
                    previousDay = TimetableContract.TimetableEntry.TABLE_SUNDAY;
                    break;

                case Calendar.TUESDAY:
                    dayOfTheWeek = TimetableContract.TimetableEntry.TABLE_TUESDAY;
                    previousDay = TimetableContract.TimetableEntry.TABLE_MONDAY;
                    break;

                case Calendar.WEDNESDAY:
                    dayOfTheWeek = TimetableContract.TimetableEntry.TABLE_WEDNESDAY;
                    previousDay = TimetableContract.TimetableEntry.TABLE_TUESDAY;
                    break;
                case Calendar.THURSDAY:
                    dayOfTheWeek = TimetableContract.TimetableEntry.TABLE_THURSDAY;
                    previousDay = TimetableContract.TimetableEntry.TABLE_WEDNESDAY;
                    break;
                case Calendar.FRIDAY:
                    dayOfTheWeek = TimetableContract.TimetableEntry.TABLE_FRIDAY;
                    previousDay = TimetableContract.TimetableEntry.TABLE_THURSDAY;
                    break;
                case Calendar.SATURDAY:
                    dayOfTheWeek = TimetableContract.TimetableEntry.TABLE_SATURDAY;
                    previousDay = TimetableContract.TimetableEntry.TABLE_FRIDAY;
                    break;
            }

            ContentValues contentValues = new ContentValues();
            contentValues.put(TimetableContract.TimetableEntry.STATE,0);
            contentValues.put(TimetableContract.TimetableEntry.BUTTON, "");
            database.update(previousDay,contentValues,null,null);

            String QUERY = database.query(dayOfTheWeek, "mainactivity", null);
            Cursor c = database.db.rawQuery(QUERY, null);
            MainListItem mlistItem;
            List<MainListItem> subjectList = new ArrayList<>();


            if (c.moveToFirst()) {
                while (!c.isAfterLast()) {
                    mlistItem = new MainListItem(c.getString(c.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME)), c.getFloat(c.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_PERCENTAGE)),c.getInt(c.getColumnIndex(TimetableContract.TimetableEntry.STATE)),c.getString(c.getColumnIndex(TimetableContract.TimetableEntry.BUTTON)),dayOfTheWeek);
                    subjectList.add(mlistItem);
                    c.moveToNext();
                }
            }

            c.close();

            return subjectList;
        }
        else if(loader == 1){

            SqlDatabase db = new SqlDatabase(mDatabase);

            String QUERY = db.query(null,"getpercentage",null);

            Cursor c = mDatabase.rawQuery(QUERY,null);

            float percentage ;
            float expectedPercentage;
            int totalAttened;
            int classes;
            int count;
            c.moveToFirst();
            percentage = (c.getFloat(c.getColumnIndex("AVG("+ SubjectContract.SubjectEntry.COLUMN_PERCENTAGE+")")));
            totalAttened =(c.getInt(c.getColumnIndex("SUM("+ SubjectContract.SubjectEntry.COLUMN_CLASSES_ATTENDED+")")));
            classes = (c.getInt(c.getColumnIndex("SUM("+ SubjectContract.SubjectEntry.COLUMN_TOTAL_CLASSES+")")));
            count = (c.getInt(c.getColumnIndex("COUNT("+ SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME+")")));
            c.close();

            int totalClassesTogether = count * totalClasses;

            int classesBunked = classes - totalAttened ;

            int classesLeft = totalClassesTogether - classesBunked;

            expectedPercentage = ((float)classesLeft/(float)totalClassesTogether)*100;

            if(Double.isNaN(expectedPercentage)){
                expectedPercentage = 0.0f;
            }

            return new MainActivity.PercentageValue(percentage,expectedPercentage);
        }
        return null;
    }
}
