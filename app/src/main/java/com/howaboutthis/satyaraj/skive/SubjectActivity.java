package com.howaboutthis.satyaraj.skive;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.howaboutthis.satyaraj.skive.subjectprovider.SqlDatabase;
import com.howaboutthis.satyaraj.skive.subjectprovider.SubjectContract;
import com.howaboutthis.satyaraj.skive.subjectprovider.TimetableContract;

import java.util.ArrayList;

public class SubjectActivity extends NavigationDrawer implements LoaderManager.LoaderCallbacks<ArrayList<String>> {

    private static final String DB_NAME = "skive_database.db";
    ArrayList<String> subjectList;
    SQLiteDatabase database;
    ListView listView;
    ArrayAdapter<String> adapter1;
    TextView emptyView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        assert inflater != null;
        @SuppressLint("InflateParams") final View contentView = inflater.inflate(R.layout.activity_subject, null, false);
        drawer.addView(contentView, 0);
        navigationView.getMenu().getItem(2).setChecked(true);

        database = openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE,null);

        emptyView = findViewById(R.id.empty_view);
        setTitle(getString(R.string.add_subjects));

        listView = findViewById(R.id.subject_list);

        subjectList = new ArrayList<>();

        getLoaderManager().initLoader(0, null, this);

        FloatingActionButton fab = findViewById(R.id.fab_add_subject);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SubjectActivity.this);
                LayoutInflater inflater = SubjectActivity.this.getLayoutInflater();

                builder.setTitle("Add a subject");

                @SuppressLint("InflateParams") final View builderView = inflater.inflate(R.layout.subject_custom_dialog, null);
                builder.setView(builderView)

                        .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                emptyView.setVisibility(View.GONE);

                                SqlDatabase db = new SqlDatabase(database);

                                EditText subjectNameEditText = builderView.findViewById(R.id.subject_name_edit_text);
                                String subjectName = subjectNameEditText.getText().toString();

                                ContentValues values = new ContentValues();
                                values.put(SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME, subjectName);
                                values.put(SubjectContract.SubjectEntry.COLUMN_CLASSES_ATTENDED, 0);
                                values.put(SubjectContract.SubjectEntry.COLUMN_TOTAL_CLASSES, 0);

                                subjectList.add(subjectName);
                                adapter1.notifyDataSetChanged();
                                db.insert(SubjectContract.SubjectEntry.TABLE_NAME, values);

                            }
                        })

                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {

                final SqlDatabase db = new SqlDatabase(database);
                final String  itemValue    = (String) listView.getItemAtPosition(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(SubjectActivity.this);
                LayoutInflater inflater = SubjectActivity.this.getLayoutInflater();

                @SuppressLint("InflateParams") final View builderView = inflater.inflate(R.layout.list_view, null);
                final ListView listview = builderView.findViewById(R.id.list_view);
                builder.setTitle("Deleting any subject will subject to removal from the TimeTable Directory");
                ArrayList<String> value = new ArrayList<>();
                value.add("Delete");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(SubjectActivity.this,
                        R.layout.row, R.id.text_item, value);
                listview.setAdapter(adapter);
                builder.setView(builderView);
                final AlertDialog alert = builder.create();
                alert.show();

                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                        ArrayList<Integer> list;

                        String selection = SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME + "=?";
                        String selection2 = TimetableContract.TimetableEntry.COLUMN + "=?";

                        String[] selectionArgs2 = new String[]{itemValue};

                        String mondayQuery = db.query(TimetableContract.TimetableEntry.TABLE_MONDAY,"finddeleted",itemValue);
                        String tuesdayQuery = db.query(TimetableContract.TimetableEntry.TABLE_TUESDAY,"finddeleted",itemValue);
                        String wednesdayQuery = db.query(TimetableContract.TimetableEntry.TABLE_WEDNESDAY,"finddeleted",itemValue);
                        String thursdayQuery = db.query(TimetableContract.TimetableEntry.TABLE_THURSDAY,"finddeleted",itemValue);
                        String firdayQuery = db.query(TimetableContract.TimetableEntry.TABLE_FRIDAY,"finddeleted",itemValue);
                        String saturdayQuery = db.query(TimetableContract.TimetableEntry.TABLE_SATURDAY,"finddeleted",itemValue);
                        String sundayQuery = db.query(TimetableContract.TimetableEntry.TABLE_SUNDAY,"finddeleted",itemValue);

                        Cursor c1 = database.rawQuery(mondayQuery,null);
                        Cursor c2 = database.rawQuery(tuesdayQuery,null);
                        Cursor c3 = database.rawQuery(wednesdayQuery,null);
                        Cursor c4 = database.rawQuery(thursdayQuery,null);
                        Cursor c5 = database.rawQuery(firdayQuery,null);
                        Cursor c6 = database.rawQuery(saturdayQuery,null);
                        Cursor c7 = database.rawQuery(sundayQuery,null);

                         list = getID(c1);
                         db.delete(TimetableContract.TimetableEntry.TABLE_MONDAY,selection2,selectionArgs2);
                         updateID(list, TimetableContract.TimetableEntry.TABLE_MONDAY,db);

                        list = getID(c2);
                        db.delete(TimetableContract.TimetableEntry.TABLE_TUESDAY,selection2,selectionArgs2);
                        updateID(list, TimetableContract.TimetableEntry.TABLE_TUESDAY,db);

                        list = getID(c3);
                        db.delete(TimetableContract.TimetableEntry.TABLE_WEDNESDAY,selection2,selectionArgs2);
                        updateID(list, TimetableContract.TimetableEntry.TABLE_WEDNESDAY,db);

                        list = getID(c4);
                        db.delete(TimetableContract.TimetableEntry.TABLE_THURSDAY,selection2,selectionArgs2);
                        updateID(list, TimetableContract.TimetableEntry.TABLE_THURSDAY,db);

                        list = getID(c5);
                        db.delete(TimetableContract.TimetableEntry.TABLE_FRIDAY,selection2,selectionArgs2);
                        updateID(list, TimetableContract.TimetableEntry.TABLE_FRIDAY,db);


                        list = getID(c6);
                        db.delete(TimetableContract.TimetableEntry.TABLE_SATURDAY,selection2,selectionArgs2);
                        updateID(list, TimetableContract.TimetableEntry.TABLE_SATURDAY,db);

                        list = getID(c7);
                       db.delete(TimetableContract.TimetableEntry.TABLE_SUNDAY,selection2,selectionArgs2);
                        updateID(list, TimetableContract.TimetableEntry.TABLE_SUNDAY,db);


                        db.delete(SubjectContract.SubjectEntry.TABLE_NAME,selection,selectionArgs2);

                        String QUERY = db.resetID(SubjectContract.SubjectEntry.TABLE_NAME,position);

                        database.execSQL(QUERY);

                        subjectList.remove(position);

                        adapter1.notifyDataSetChanged();
                        alert.dismiss();

                    }
                });

            }

        });
    }

    @Override
    public Loader<ArrayList<String>>  onCreateLoader(int id, Bundle args) {
        return new SubjectLoader(this,database);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<String>> loader, ArrayList<String> data) {
        subjectList = data;
        if (subjectList.size()==0){
            emptyView.setVisibility(View.VISIBLE);
        }else {
            emptyView.setVisibility(View.GONE);
        }
            adapter1 = new ArrayAdapter<>(SubjectActivity.this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, data);
            listView.setAdapter(adapter1);

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    private ArrayList<Integer> getID(Cursor c){

        ArrayList<Integer> list = new ArrayList<>();

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                list.add(c.getInt(c.getColumnIndex(TimetableContract.TimetableEntry._ID)));
                c.moveToNext();
            }
        }
        c.close();
     return list;
}

    private void updateID(ArrayList<Integer> list,String table, SqlDatabase db)
    {
        for (int i = list.size() - 1; i>=0;i--){
            String QUERY = db.resetID(table,list.get(i));
            database.execSQL(QUERY);
        }

    }

}
