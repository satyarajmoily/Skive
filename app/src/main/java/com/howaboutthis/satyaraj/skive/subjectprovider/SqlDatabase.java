package com.howaboutthis.satyaraj.skive.subjectprovider;

import android.content.ContentValues;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Objects;

public class SqlDatabase {


    private static final String LOG_TAG = SqlDatabase.class.getSimpleName();
    public  SQLiteDatabase db;

    public SqlDatabase(SQLiteDatabase db){

       this.db = db;
    }

    public void create(){


        String SQL_CREATE_SUBJECT_TABLE = "CREATE TABLE " + SubjectContract.SubjectEntry.TABLE_NAME + "("
                + SubjectContract.SubjectEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME + " TEXT NOT NULL, "
                + SubjectContract.SubjectEntry.COLUMN_CLASSES_ATTENDED + " INTEGER, "
                + SubjectContract.SubjectEntry.COLUMN_TOTAL_CLASSES + " INTEGER, "
                + SubjectContract.SubjectEntry.COLUMN_PERCENTAGE +" INTEGER " + ")";

        String SQL_CREATE_MONDAY_TABLE = "CREATE TABLE " + TimetableContract.TimetableEntry.TABLE_MONDAY + "("
                + TimetableContract.TimetableEntry._ID + " INTEGER PRIMARY KEY , "
                + TimetableContract.TimetableEntry.COLUMN + " TEXT , "
                + TimetableContract.TimetableEntry.STATE + " INTEGER , "
                + TimetableContract.TimetableEntry.BUTTON + " TEXT " + ")";

        String SQL_CREATE_TUESDAY_TABLE = "CREATE TABLE " + TimetableContract.TimetableEntry.TABLE_TUESDAY + "("
                + TimetableContract.TimetableEntry._ID + " INTEGER PRIMARY KEY , "
                + TimetableContract.TimetableEntry.COLUMN + " TEXT , "
                + TimetableContract.TimetableEntry.STATE + " INTEGER , "
                + TimetableContract.TimetableEntry.BUTTON + " TEXT " +")";

        String SQL_CREATE_WEDNESDAY_TABLE = "CREATE TABLE " + TimetableContract.TimetableEntry.TABLE_WEDNESDAY + "("
                + TimetableContract.TimetableEntry._ID + " INTEGER PRIMARY KEY , "
                + TimetableContract.TimetableEntry.COLUMN + " TEXT , "
                + TimetableContract.TimetableEntry.STATE + " INTEGER , "
                + TimetableContract.TimetableEntry.BUTTON + " TEXT " +")";

        String SQL_CREATE_THURSDAY_TABLE = "CREATE TABLE " + TimetableContract.TimetableEntry.TABLE_THURSDAY + "("
                + TimetableContract.TimetableEntry._ID + " INTEGER PRIMARY KEY , "
                + TimetableContract.TimetableEntry.COLUMN + " TEXT , "
                + TimetableContract.TimetableEntry.STATE+ " INTEGER ,"
                + TimetableContract.TimetableEntry.BUTTON + " TEXT " + ")";

        String SQL_CREATE_FRIDAY_TABLE = "CREATE TABLE " + TimetableContract.TimetableEntry.TABLE_FRIDAY + "("
                + TimetableContract.TimetableEntry._ID + " INTEGER PRIMARY KEY , "
                + TimetableContract.TimetableEntry.COLUMN + " TEXT , "
                + TimetableContract.TimetableEntry.STATE+ " INTEGER , "
                + TimetableContract.TimetableEntry.BUTTON + " TEXT " + ")";

        String SQL_CREATE_SATURDAY_TABLE = "CREATE TABLE " + TimetableContract.TimetableEntry.TABLE_SATURDAY + "("
                + TimetableContract.TimetableEntry._ID + " INTEGER PRIMARY KEY , "
                + TimetableContract.TimetableEntry.COLUMN + " TEXT , "
                + TimetableContract.TimetableEntry.STATE+ " INTEGER , "
                + TimetableContract.TimetableEntry.BUTTON + " TEXT " + ")";

        String SQL_CREATE_SUNDAY_TABLE = "CREATE TABLE " + TimetableContract.TimetableEntry.TABLE_SUNDAY + "("
                + TimetableContract.TimetableEntry._ID + " INTEGER PRIMARY KEY , "
                + TimetableContract.TimetableEntry.COLUMN + " TEXT , "
                + TimetableContract.TimetableEntry.STATE+ " INTEGER , "
                + TimetableContract.TimetableEntry.BUTTON + " TEXT " + ")";


        db.execSQL(SQL_CREATE_FRIDAY_TABLE);
        db.execSQL(SQL_CREATE_MONDAY_TABLE);
        db.execSQL(SQL_CREATE_WEDNESDAY_TABLE);
        db.execSQL(SQL_CREATE_THURSDAY_TABLE);
        db.execSQL(SQL_CREATE_TUESDAY_TABLE);
        db.execSQL(SQL_CREATE_SATURDAY_TABLE);
        db.execSQL(SQL_CREATE_SUNDAY_TABLE);
        db.execSQL(SQL_CREATE_SUBJECT_TABLE);

    }

    public void insert(String tableName, ContentValues contentValues){

         long rowsInserted;
          rowsInserted = db.insert(tableName, null, contentValues);

        if (rowsInserted == -1){
            Log.e(LOG_TAG, "Failed to insert row " );
        }

    }

    public void update(String table, ContentValues contentValues, String selection, String[] selectionArgs){

        int rowsUpdated ;

        rowsUpdated = db.update(table, contentValues, selection, selectionArgs);

        if (rowsUpdated == 0)
            Log.e(LOG_TAG, "Failed to update");

    }

    public String resetID (String table, int position){
        return "UPDATE "+ table + " SET " + TimetableContract.TimetableEntry._ID + " = "
                + TimetableContract.TimetableEntry._ID + " - 1 WHERE " + TimetableContract.TimetableEntry._ID
                + " > " + position;
    }

    public String query(String tableName,  String whichCLass,String value) {

        String QUERY = null;
        if (Objects.equals(whichCLass, "addtimetable")) {
            QUERY = "SELECT " + TimetableContract.TimetableEntry.COLUMN + " FROM " + tableName ;

        } else if (Objects.equals(whichCLass, "subjectactivity")) {

            QUERY = "SELECT " + SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME + " FROM "
                    + SubjectContract.SubjectEntry.TABLE_NAME;

        }else if (Objects.equals(whichCLass, "floatingbutton")) {

            QUERY = "SELECT " + SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME + " FROM "
                    + SubjectContract.SubjectEntry.TABLE_NAME + " WHERE "
                    + SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME + " IS NOT NULL";

        }
        else if (Objects.equals(whichCLass, "mainactivity")) {

            QUERY = "SELECT " + SubjectContract.SubjectEntry.TABLE_NAME + "." + SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME + ","
                    + SubjectContract.SubjectEntry.TABLE_NAME + "." + SubjectContract.SubjectEntry.COLUMN_PERCENTAGE + ","
                    + TimetableContract.TimetableEntry.STATE + ","
                    + TimetableContract.TimetableEntry.BUTTON
                    + " FROM " + SubjectContract.SubjectEntry.TABLE_NAME + "," + tableName
                    + " WHERE " + tableName + "." + TimetableContract.TimetableEntry.COLUMN + " = " + SubjectContract.SubjectEntry.TABLE_NAME + "."
                    + SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME + " ORDER BY " + tableName + "." + TimetableContract.TimetableEntry._ID;

        }else if (Objects.equals(whichCLass, "editactivity")) {

            QUERY = "SELECT " + SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME + ","
                    + SubjectContract.SubjectEntry.COLUMN_CLASSES_ATTENDED + ","
                    + SubjectContract.SubjectEntry.COLUMN_TOTAL_CLASSES + ","
                    + SubjectContract.SubjectEntry.COLUMN_PERCENTAGE + " FROM "
                    + SubjectContract.SubjectEntry.TABLE_NAME;

        }else if(Objects.equals(whichCLass,"finddeleted"))
        {
           QUERY = "SELECT " + TimetableContract.TimetableEntry._ID + " FROM " + tableName
                    + " WHERE " + TimetableContract.TimetableEntry.COLUMN +" = '" + value + "'";

        }else if (Objects.equals(whichCLass,"getpercentage")){

            QUERY = "SELECT AVG("+ SubjectContract.SubjectEntry.COLUMN_PERCENTAGE + "), SUM("+SubjectContract.SubjectEntry.COLUMN_CLASSES_ATTENDED
                    +"), SUM("+SubjectContract.SubjectEntry.COLUMN_TOTAL_CLASSES +"), COUNT("+ SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME+") FROM " + SubjectContract.SubjectEntry.TABLE_NAME;

        }else if (Objects.equals(whichCLass, "updateattended")) {

            QUERY = "UPDATE " + SubjectContract.SubjectEntry.TABLE_NAME + " SET " + SubjectContract.SubjectEntry.COLUMN_CLASSES_ATTENDED + "="
                    + SubjectContract.SubjectEntry.COLUMN_CLASSES_ATTENDED + " + 1, "
                    + SubjectContract.SubjectEntry.COLUMN_TOTAL_CLASSES + "=" + SubjectContract.SubjectEntry.COLUMN_TOTAL_CLASSES + " + 1 "
                    + " WHERE " + SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME + " = '" + value + "'";

        }else if (Objects.equals(whichCLass, "updatepercentage")){
            QUERY = "SELECT "+ SubjectContract.SubjectEntry.COLUMN_CLASSES_ATTENDED + ","+ SubjectContract.SubjectEntry.COLUMN_TOTAL_CLASSES + " FROM " + SubjectContract.SubjectEntry.TABLE_NAME
                    + " WHERE " + SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME + " = '" + value + "'";

        }else if (Objects.equals(whichCLass,"updatebunked")) {

            QUERY = "UPDATE " + SubjectContract.SubjectEntry.TABLE_NAME + " SET "
                    + SubjectContract.SubjectEntry.COLUMN_TOTAL_CLASSES + "=" + SubjectContract.SubjectEntry.COLUMN_TOTAL_CLASSES + " + 1 "
                    + " WHERE " + SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME + " = '" + value + "'";

        }else if (Objects.equals(whichCLass, "delete")){

            QUERY = "DELETE FROM "+ tableName;

        }else if (Objects.equals(whichCLass,"safebunkingactivity")) {

            QUERY = "SELECT " + SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME + ","
                    + SubjectContract.SubjectEntry.COLUMN_PERCENTAGE +" FROM  "+ SubjectContract.SubjectEntry.TABLE_NAME;
        }



        if(Objects.equals(QUERY,null)) {
            Log.e(LOG_TAG, "Error in whichClass ");
            return null;
        }else
            return QUERY;

    }
    public void delete(String table, String selection, String[] selectionArgs){

        int rowsDeleted;

        rowsDeleted = db.delete(table, selection, selectionArgs);

        if (rowsDeleted == -1)
            Log.e(LOG_TAG, "Failed to Delete");

    }

}
