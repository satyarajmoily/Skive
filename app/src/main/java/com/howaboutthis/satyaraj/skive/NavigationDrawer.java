package com.howaboutthis.satyaraj.skive;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.howaboutthis.satyaraj.skive.subjectprovider.SqlDatabase;
import com.howaboutthis.satyaraj.skive.subjectprovider.SubjectContract;
import com.howaboutthis.satyaraj.skive.subjectprovider.TimetableContract;

import java.util.Calendar;
import java.util.Objects;


public class NavigationDrawer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected DrawerLayout drawer;
    private static final String DB_NAME = "skive_database.db";
    SQLiteDatabase db;
    SharedPreferences sharedPreferences;
    boolean first;
    protected NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        db = openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE,null);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        NavigationView navigationView1 = findViewById(R.id.nav_view2);

        final Menu menu = navigationView1.getMenu();

        menu.add("TOMORROW");

        sharedPreferences = getSharedPreferences("shared_preferences", MODE_PRIVATE);
        first = sharedPreferences.getBoolean("first", true);
        if(!first) {
            int holiday = 0;

            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);

            SqlDatabase database = new SqlDatabase(db);
            String dayOfTheWeek = null;

            switch (day) {
                case Calendar.SUNDAY:
                    dayOfTheWeek = TimetableContract.TimetableEntry.TABLE_MONDAY;
                    break;

                case Calendar.MONDAY:
                    dayOfTheWeek = TimetableContract.TimetableEntry.TABLE_TUESDAY;
                    break;

                case Calendar.TUESDAY:
                    dayOfTheWeek = TimetableContract.TimetableEntry.TABLE_WEDNESDAY;
                    break;

                case Calendar.WEDNESDAY:
                    dayOfTheWeek = TimetableContract.TimetableEntry.TABLE_THURSDAY;
                    break;
                case Calendar.THURSDAY:
                    dayOfTheWeek = TimetableContract.TimetableEntry.TABLE_FRIDAY;
                    break;
                case Calendar.FRIDAY:
                    dayOfTheWeek = TimetableContract.TimetableEntry.TABLE_SATURDAY;
                    break;
                case Calendar.SATURDAY:
                    dayOfTheWeek = TimetableContract.TimetableEntry.TABLE_SUNDAY;
                    break;
            }


            String QUERY = database.query(dayOfTheWeek, "mainactivity", null);
            Cursor c = database.db.rawQuery(QUERY, null);

            if (c.moveToFirst()) {
                while (!c.isAfterLast()) {
                    holiday = 1;
                    menu.add(c.getString(c.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME)));
                    c.moveToNext();
                }
            }

            if (holiday == 0) {
                menu.add("IT'S A HOLIDAY");
            }
            c.close();
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.safe_bunking) {
            final SharedPreferences sharedPreferences;
            sharedPreferences = getSharedPreferences("shared_preferences", MODE_PRIVATE);
            float percentage = sharedPreferences.getFloat("required_percentage", 0.0f);
            int classes = sharedPreferences.getInt("total_classes",0);

            if (percentage==0.0 || classes ==0){

                AlertDialog.Builder builder = new AlertDialog.Builder(NavigationDrawer.this);
                LayoutInflater inflater = NavigationDrawer.this.getLayoutInflater();

                builder.setTitle("Enter Details");

                @SuppressLint("InflateParams") final View builderView = inflater.inflate(R.layout.details_custom_dialog, null);


                final EditText percentageEditText = builderView.findViewById(R.id.percentage_edit_text);
                final EditText totalEditText = builderView.findViewById(R.id.total_classes_edit_text);

                builder.setView(builderView)

                        .setPositiveButton( "SAVE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                SharedPreferences.Editor sharedPreferences_editor;
                                Float percentage = Float.parseFloat(percentageEditText.getText().toString());
                                int totalClasses = Integer.parseInt(totalEditText.getText().toString());
                                sharedPreferences_editor = sharedPreferences.edit();

                                sharedPreferences_editor.putFloat("required_percentage", percentage);
                                sharedPreferences_editor.putInt("total_classes",totalClasses);
                                sharedPreferences_editor.apply();

                            }
                        })

                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();

            }
            else {

                Intent intent = new Intent(getApplicationContext(), SafeBunkingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }

        } else if (id == R.id.edit) {
            Intent intent = new Intent(getApplicationContext(),EditActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);


        } else if (id == R.id.subjects) {
            Intent intent = new Intent(getApplicationContext(),SubjectActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);

        } else if (id == R.id.timetable) {
            Intent intent = new Intent(getApplicationContext(),TimetableActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);

        } else if (id == R.id.add_classes) {

            AlertDialog.Builder builder = new AlertDialog.Builder(NavigationDrawer.this);
            LayoutInflater inflater = NavigationDrawer.this.getLayoutInflater();

            builder.setTitle("Enter Details");

            @SuppressLint("InflateParams") final View builderView = inflater.inflate(R.layout.details_custom_dialog, null);

            final SharedPreferences sharedPreferences;

            sharedPreferences = getSharedPreferences("shared_preferences", MODE_PRIVATE);
            float percentage = sharedPreferences.getFloat("required_percentage", 0.0f);
            int classes = sharedPreferences.getInt("total_classes",0);

            final EditText percentageEditText = builderView.findViewById(R.id.percentage_edit_text);
            final EditText totalEditText = builderView.findViewById(R.id.total_classes_edit_text);

            if (percentage!=0.0)
                percentageEditText.setText(String.valueOf(percentage));
            if (classes != 0)
                totalEditText.setText(String.valueOf(classes));


            builder.setView(builderView)

                    .setPositiveButton( "SAVE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            SharedPreferences.Editor sharedPreferences_editor;
                            Float percentage = Float.parseFloat(percentageEditText.getText().toString());
                            int totalClasses = Integer.parseInt(totalEditText.getText().toString());
                            sharedPreferences_editor = sharedPreferences.edit();

                            sharedPreferences_editor.putFloat("required_percentage", percentage);
                            sharedPreferences_editor.putInt("total_classes",totalClasses);
                            sharedPreferences_editor.apply();

                        }
                    })

                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();



        } else if (id == R.id.restore) {

            AlertDialog.Builder builder = new AlertDialog.Builder(NavigationDrawer.this);
            LayoutInflater inflater = NavigationDrawer.this.getLayoutInflater();

            builder.setTitle("RESET WILL RESULT IN DELETING ALL THE DETAILS FROM THE APP.");

            @SuppressLint("InflateParams") final View builderView = inflater.inflate(R.layout.reset_custom_dialog, null);
            builder.setView(builderView)

                    .setPositiveButton("RESET", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {



                            EditText valueEditText = builderView.findViewById(R.id.reset_edit_text);
                            String value = valueEditText.getText().toString();

                            if (Objects.equals(value, "yes") || Objects.equals(value, "YES")){
                                SqlDatabase database = new SqlDatabase(db);

                                String QUERY1 = database.query(SubjectContract.SubjectEntry.TABLE_NAME,"delete",null);
                                String QUERY2 = database.query(TimetableContract.TimetableEntry.TABLE_SUNDAY,"delete",null);
                                String QUERY3 = database.query(TimetableContract.TimetableEntry.TABLE_SATURDAY,"delete",null);
                                String QUERY4= database.query(TimetableContract.TimetableEntry.TABLE_FRIDAY,"delete",null);
                                String QUERY5 = database.query(TimetableContract.TimetableEntry.TABLE_THURSDAY,"delete",null);
                                String QUERY6 = database.query(TimetableContract.TimetableEntry.TABLE_WEDNESDAY,"delete",null);
                                String QUERY7 = database.query(TimetableContract.TimetableEntry.TABLE_MONDAY,"delete",null);
                                String QUERY8 = database.query(TimetableContract.TimetableEntry.TABLE_TUESDAY,"delete",null);

                                db.execSQL(QUERY1);
                                db.execSQL(QUERY2);
                                db.execSQL(QUERY3);
                                db.execSQL(QUERY4);
                                db.execSQL(QUERY5);
                                db.execSQL(QUERY6);
                                db.execSQL(QUERY7);
                                db.execSQL(QUERY8);

                                Toast.makeText(NavigationDrawer.this,"RESET SUCCESSFUL",Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivityForResult(intent, 1);


                            }else
                                Toast.makeText(NavigationDrawer.this,"Sorry, Wrong Input",Toast.LENGTH_SHORT).show();
                        }
                    })

                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();

        }else if (id == R.id.about){
            Intent intent = new Intent(getApplicationContext(),aboutActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MainActivity activity = new MainActivity();
        if(resultCode==RESULT_OK){
this.finish();
           activity.populatedata();
        }
    }
}
