package com.howaboutthis.satyaraj.skive;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Loader;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.howaboutthis.satyaraj.skive.subjectprovider.SqlDatabase;
import com.howaboutthis.satyaraj.skive.subjectprovider.TimetableContract;

import java.util.ArrayList;
import java.util.Objects;

public class AddTimetable extends NavigationDrawer implements LoaderManager.LoaderCallbacks<ArrayList<String>>{

    ListView listView ;
    private static final String DB_NAME = "skive_database.db";
    ArrayList<String> values;
    ArrayList<String> subjectList;
    String dayOfTheWeek;
    SQLiteDatabase database;
    ArrayAdapter<String> adapter;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        assert inflater != null;
        @SuppressLint("InflateParams") final View contentView = inflater.inflate(R.layout.activity_timetable, null, false);
        drawer.addView(contentView, 0);
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        dayOfTheWeek = bundle.getString("day_of_the_week");

        setTitle(dayOfTheWeek);

        setDayOfTheWeek();

        imageView = findViewById(R.id.timetable_empty_view);

        database = openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE,null);


        listView = findViewById(R.id.timetable_list);

        values = new ArrayList<>();

        subjectList = new ArrayList<>();

        getLoaderManager().initLoader(0, null, this);

        getLoaderManager().initLoader(1, null, this);

        FloatingActionButton fab = findViewById(R.id.fab_timetable);

        fab.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {

                                       if (values.size()==0){
                                           Toast.makeText(AddTimetable.this,"Please add the subjects first",Toast.LENGTH_LONG).show();
                                       }
                                       else {

                                           AlertDialog.Builder builder = new AlertDialog.Builder(AddTimetable.this);
                                           LayoutInflater inflater = AddTimetable.this.getLayoutInflater();

                                           @SuppressLint("InflateParams") final View builderView = inflater.inflate(R.layout.list_view, null);
                                           final ListView listView1 = builderView.findViewById(R.id.list_view);
                                           ArrayAdapter<String> adapter1 = new ArrayAdapter<>(AddTimetable.this,
                                                   android.R.layout.simple_list_item_1, android.R.id.text1, values);
                                           listView1.setAdapter(adapter1);
                                           builder.setView(builderView);
                                           AlertDialog alert = builder.create();
                                           alert.show();

                                           listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                               @Override
                                               public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                                   imageView.setVisibility(View.GONE);
                                                   SqlDatabase db = new SqlDatabase(database);
                                                   String itemValue = (String) listView1.getItemAtPosition(position);

                                                   ContentValues contentValues = new ContentValues();
                                                   contentValues.put(TimetableContract.TimetableEntry.COLUMN, itemValue);
                                                   contentValues.put(TimetableContract.TimetableEntry._ID, subjectList.size());
                                                   contentValues.put(TimetableContract.TimetableEntry.STATE, 0);

                                                   subjectList.add(itemValue);
                                                   adapter.notifyDataSetChanged();

                                                   db.insert(dayOfTheWeek, contentValues);
                                               }
                                           });
                                       }
    }
});

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {

                final SqlDatabase db = new SqlDatabase(database);
                AlertDialog.Builder builder = new AlertDialog.Builder(AddTimetable.this);
                LayoutInflater inflater = AddTimetable.this.getLayoutInflater();

                @SuppressLint("InflateParams") final View builderView = inflater.inflate(R.layout.list_view, null);
                final ListView listview = builderView.findViewById(R.id.list_view);
                ArrayList<String> value = new ArrayList<>();
                value.add("Delete");
                ArrayAdapter<String> adapter1 = new ArrayAdapter<>(AddTimetable.this,
                        R.layout.row, R.id.text_item, value);
                listview.setAdapter(adapter1);
                builder.setView(builderView);
                final AlertDialog alert = builder.create();
                alert.show();

                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                        String selection = TimetableContract.TimetableEntry._ID + "=?";
                        String[] selectionArgs = new String[]{String.valueOf(position)};

                        db.delete(dayOfTheWeek,selection,selectionArgs);

                        String QUERY = db.resetID(dayOfTheWeek,position);

                        database.execSQL(QUERY);

                        subjectList.remove(position);

                        adapter.notifyDataSetChanged();

                        alert.dismiss();
                    }
                });

            }

        });
    }

    @Override
    public Loader<ArrayList<String>> onCreateLoader(int id, Bundle args) {

        if(id == 0) {
            return new AddTimetableLoader(this, database, dayOfTheWeek);
        }
        else if(id == 1)
            return new FloatingLoader(this,database);
        return null;
    }


    @Override
    public void onLoadFinished(Loader<ArrayList<String>> loader, ArrayList<String> data) {

        int id = loader.getId();
        if (id==0) {
            subjectList=data;
            if (subjectList.size()==0){
                imageView.setVisibility(View.VISIBLE);
            }else {
                imageView.setVisibility(View.GONE);
            }
                adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, subjectList);
                listView.setAdapter(adapter);


        }else if (id == 1){
            values = data;
        }

    }


    @Override
    public void onLoaderReset(Loader<ArrayList<String>> loader) {

    }

    public void setDayOfTheWeek(){

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
    }
}