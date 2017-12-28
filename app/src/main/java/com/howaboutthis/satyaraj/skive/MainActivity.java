package com.howaboutthis.satyaraj.skive;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.howaboutthis.satyaraj.skive.subjectprovider.SqlDatabase;
import com.howaboutthis.satyaraj.skive.subjectprovider.SubjectContract;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends NavigationDrawer implements LoaderManager.LoaderCallbacks {

    private static final String DB_NAME = "skive_database.db";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter<RecyclerView.ViewHolder> mAdapter;

    List<MainListItem> listItems;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferences_editor;
    Boolean first;
    SQLiteDatabase db;
    float percentage;
    float requiredPercentage;
    ProgressBar mProgressBar;
    TextView totalPercentage;
    TextView expectedPercentage;
    private int totalClasses;
    TextView emptyView;
    Point p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        assert inflater != null;
        @SuppressLint("InflateParams") View contentView = inflater.inflate(R.layout.activity_main, null, false);
        drawer.addView(contentView, 0);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        emptyView = findViewById(R.id.empty_view);

        sharedPreferences = getSharedPreferences("shared_preferences", MODE_PRIVATE);
        requiredPercentage = sharedPreferences.getFloat("required_percentage",0.0f);
        totalClasses = sharedPreferences.getInt("total_classes",0);

        mProgressBar = findViewById(R.id.circle_progress_bar);
        totalPercentage = findViewById(R.id.total_percentage);
        expectedPercentage = findViewById(R.id.expected_percentage);
        RelativeLayout percentageLayout = findViewById(R.id.percentage_layout);

        Animation animation = new TranslateAnimation(0,0,1000,0);
        animation.setDuration(1000);
        mRecyclerView.startAnimation(animation);
        emptyView.startAnimation(animation);

        Animation animation2 = new TranslateAnimation(0,0,-1000,0);
        animation2.setDuration(1000);
        percentageLayout.startAnimation(animation2);

        db = openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);

        getLoaderManager().initLoader(1, null, this);

        first = sharedPreferences.getBoolean("first", true);

        if (first) {

            SqlDatabase database = new SqlDatabase(db);
            database.create();
            sharedPreferences_editor = sharedPreferences.edit();
            sharedPreferences_editor.putBoolean("first", false);
            sharedPreferences_editor.apply();

        }

        getLoaderManager().initLoader(0, null, this);

        final FloatingActionButton fab = findViewById(R.id.fab);

       final Animation fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
       final Animation fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);


        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if ((dy > 0) && (fab.getVisibility() == View.VISIBLE)) {

                    fab.startAnimation(fab_open);
                    fab.setClickable(true);
                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {

                    fab.startAnimation(fab_close);
                    fab.setClickable(false);

                }
            }
        });



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();

                SqlDatabase database = new SqlDatabase(db);
                ArrayList<String> values = new ArrayList<>();
                String QUERY = database.query(null, "floatingbutton", null);

                Cursor c = db.rawQuery(QUERY, null);

                if (c.moveToFirst()) {
                    while (!c.isAfterLast()) {
                        values.add(c.getString(c.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME)));
                        c.moveToNext();
                    }
                }

                c.close();

                if (values.size() != 0) {

                    @SuppressLint("InflateParams") final View builderView = inflater.inflate(R.layout.list_view, null);
                    final ListView listView1 = builderView.findViewById(R.id.list_view);
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<>(MainActivity.this,
                            android.R.layout.simple_list_item_1, android.R.id.text1, values);
                    listView1.setAdapter(adapter1);
                    builder.setView(builderView);
                    builder.setTitle("EXTRA CLASS");
                    final AlertDialog alert = builder.create();
                    alert.show();

                    listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                            alert.dismiss();
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

                            alertDialog.setMessage("Did you attend this subject?");


                            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @SuppressLint("DefaultLocale")
                                @Override
                                public void onClick(DialogInterface dialog, int id) {

                                    SqlDatabase database = new SqlDatabase(db);
                                    final String itemValue = (String) listView1.getItemAtPosition(position);

                                    String QUERY = database.query(null, "updateattended", itemValue);
                                    db.execSQL(QUERY);

                                    String selection = SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME + "=?";
                                    String[] selectionArgs = new String[]{itemValue};

                                    String QUERY3 = database.query(null, "updatepercentage", itemValue);
                                    Cursor c = db.rawQuery(QUERY3, null);
                                    float percentage;
                                    float attended;
                                    float total;
                                    c.moveToFirst();
                                    attended = (c.getFloat(c.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_CLASSES_ATTENDED)));
                                    total = (c.getFloat(c.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_TOTAL_CLASSES)));
                                    c.close();

                                    percentage = (attended / total) * 100;

                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put(SubjectContract.SubjectEntry.COLUMN_PERCENTAGE, percentage);
                                    database.update(SubjectContract.SubjectEntry.TABLE_NAME, contentValues, selection, selectionArgs);


                                    String QUERY2 = database.query(null, "getpercentage", null);

                                    Cursor c2 = db.rawQuery(QUERY2, null);
                                    float percent;
                                    float expPercentage;
                                    int totalAttened;
                                    int classes;
                                    int count;
                                    c2.moveToFirst();
                                    percent = (c2.getFloat(c2.getColumnIndex("AVG(" + SubjectContract.SubjectEntry.COLUMN_PERCENTAGE + ")")));
                                    totalAttened = (c2.getInt(c2.getColumnIndex("SUM(" + SubjectContract.SubjectEntry.COLUMN_CLASSES_ATTENDED + ")")));
                                    classes = (c2.getInt(c2.getColumnIndex("SUM(" + SubjectContract.SubjectEntry.COLUMN_TOTAL_CLASSES + ")")));
                                    count = (c2.getInt(c2.getColumnIndex("COUNT(" + SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME + ")")));
                                    c2.close();

                                    int totalClassesTogether = count * totalClasses;

                                    int classesBunked = classes - totalAttened;

                                    int classesLeft = totalClassesTogether - classesBunked;

                                    expPercentage = ((float) classesLeft / (float) totalClassesTogether) * 100;

                                    c2.close();

                                    @SuppressLint("DefaultLocale") final String formattedValue = String.format("%.1f", percent);
                                    @SuppressLint("DefaultLocale") final String formattedExpectedPercentage;

                                    if (requiredPercentage ==0.0 || totalClasses == 0){
                                        formattedExpectedPercentage = "Update the required percentage and total classes";
                                        expectedPercentage.setTextColor(Color.RED);
                                        expectedPercentage.setTypeface(null, Typeface.NORMAL);
                                        expectedPercentage.setTypeface(expectedPercentage.getTypeface(), Typeface.ITALIC);
                                        expectedPercentage.setTextSize(13.0f);

                                    }else {
                                        formattedExpectedPercentage = String.format("%.1f", expPercentage)+"%";


                                        if (percent < requiredPercentage) {

                                            mProgressBar.getProgressDrawable().setColorFilter(
                                                    Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                                            totalPercentage.setTextColor(Color.RED);

                                        } else if (percent > requiredPercentage) {

                                            mProgressBar.getProgressDrawable().setColorFilter(
                                                    Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);
                                            totalPercentage.setTextColor(Color.GREEN);

                                        }

                                        if (expPercentage < requiredPercentage) {
                                            expectedPercentage.setTextColor(Color.RED);
                                        } else if (expPercentage > requiredPercentage) {
                                            expectedPercentage.setTextColor(Color.GREEN);
                                        }
                                    }

                                    expectedPercentage.setText(String.valueOf(formattedExpectedPercentage));
                                    totalPercentage.setText(String.valueOf(formattedValue + "%"));

                                    mProgressBar.setProgress(Math.round(percent));

                                }
                            })
                                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                        @SuppressLint("DefaultLocale")
                                        public void onClick(DialogInterface dialog, int id) {

                                            SqlDatabase database = new SqlDatabase(db);
                                            final String itemValue = (String) listView1.getItemAtPosition(position);

                                            String QUERY = database.query(null, "updatebunked", itemValue);
                                            db.execSQL(QUERY);

                                            String selection = SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME + "=?";
                                            String[] selectionArgs = new String[]{itemValue};

                                            String QUERY3 = database.query(null, "updatepercentage", itemValue);
                                            Cursor c = db.rawQuery(QUERY3, null);
                                            float percentage;
                                            float attended;
                                            float total;
                                            c.moveToFirst();
                                            attended = (c.getFloat(c.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_CLASSES_ATTENDED)));
                                            total = (c.getFloat(c.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_TOTAL_CLASSES)));
                                            c.close();

                                            percentage = (attended / total) * 100;

                                            ContentValues contentValues = new ContentValues();
                                            contentValues.put(SubjectContract.SubjectEntry.COLUMN_PERCENTAGE, percentage);
                                            database.update(SubjectContract.SubjectEntry.TABLE_NAME, contentValues, selection, selectionArgs);


                                            String QUERY2 = database.query(null, "getpercentage", null);

                                            Cursor c2 = db.rawQuery(QUERY2, null);
                                            float percent;
                                            float expPercentage;
                                            int totalAttened;
                                            int classes;
                                            int count;
                                            c2.moveToFirst();
                                            percent = (c2.getFloat(c2.getColumnIndex("AVG(" + SubjectContract.SubjectEntry.COLUMN_PERCENTAGE + ")")));
                                            totalAttened = (c2.getInt(c2.getColumnIndex("SUM(" + SubjectContract.SubjectEntry.COLUMN_CLASSES_ATTENDED + ")")));
                                            classes = (c2.getInt(c2.getColumnIndex("SUM(" + SubjectContract.SubjectEntry.COLUMN_TOTAL_CLASSES + ")")));
                                            count = (c2.getInt(c2.getColumnIndex("COUNT(" + SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME + ")")));
                                            c2.close();

                                            int totalClassesTogether = count * totalClasses;

                                            int classesBunked = classes - totalAttened;

                                            int classesLeft = totalClassesTogether - classesBunked;

                                            expPercentage = ((float) classesLeft / (float) totalClassesTogether) * 100;

                                            @SuppressLint("DefaultLocale") final String formattedValue = String.format("%.1f", percent);
                                            @SuppressLint("DefaultLocale") final String formattedExpectedPercentage;

                                            if (requiredPercentage ==0.0 || totalClasses == 0){
                                                formattedExpectedPercentage = "Update the required percentage and total classes";
                                                expectedPercentage.setTextColor(Color.RED);
                                                expectedPercentage.setTypeface(null, Typeface.NORMAL);
                                                expectedPercentage.setTypeface(expectedPercentage.getTypeface(), Typeface.ITALIC);
                                                expectedPercentage.setTextSize(13.0f);

                                            }else {
                                                formattedExpectedPercentage = String.format("%.1f", expPercentage) + "%";

                                                if (percent < requiredPercentage) {

                                                    mProgressBar.getProgressDrawable().setColorFilter(
                                                            Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                                                    totalPercentage.setTextColor(Color.RED);

                                                } else if (percent > requiredPercentage) {

                                                    mProgressBar.getProgressDrawable().setColorFilter(
                                                            Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);
                                                    totalPercentage.setTextColor(Color.GREEN);

                                                }

                                                if (expPercentage < requiredPercentage) {
                                                    expectedPercentage.setTextColor(Color.RED);
                                                } else if (expPercentage > requiredPercentage) {
                                                    expectedPercentage.setTextColor(Color.GREEN);
                                                }
                                            }

                                            expectedPercentage.setText(String.valueOf(formattedExpectedPercentage));
                                            totalPercentage.setText(String.valueOf(formattedValue + "%"));
                                            mProgressBar.setProgress(Math.round(percentage));

                                        }
                                    });
                            alertDialog.show();
                        }
                    });
                }else

                {
                    Toast.makeText(MainActivity.this, "No subjects have been added yet. This button is used to add extra classes.", Toast.LENGTH_LONG).show();
                }
            }
        });


        percentageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (p != null)
                    showPopup(MainActivity.this, p);
            }
        });

    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        int[] location = new int[2];
        RelativeLayout percentageLayout = findViewById(R.id.percentage_layout);

        // Get the x, y location and store it in the location[] array
        // location[0] = x, location[1] = y.
        percentageLayout.getLocationOnScreen(location);

        //Initialize the Point with x, and y positions
        p = new Point();
        p.x = location[0];
        p.y = location[1];
    }


    private void showPopup(final Activity context, Point p) {
        int popupWidth = ActionBar.LayoutParams.MATCH_PARENT;
        RelativeLayout relativeLayout = findViewById(R.id.percentage_layout);
        int popupHeight = relativeLayout.getHeight();

        // Inflate the popup_layout.xml
        LinearLayout viewGroup = context.findViewById(R.id.popup_view);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        View layout = layoutInflater.inflate(R.layout.popup_view, viewGroup);

        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);
        popup.setFocusable(true);

        // Some offset to align the popup a bit to the right, and a bit down, relative to button's position.
        int OFFSET_X = 30;
        int OFFSET_Y = 30;

        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);


    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new MainLoader(this, db, id,totalClasses);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onLoadFinished(Loader loader, Object data) {

        int loaderID = loader.getId();

        if (loaderID == 0) {

            listItems = (List<MainListItem>) data;
            if (listItems.size()==0)
                emptyView.setVisibility(View.VISIBLE);
            else {
                emptyView.setVisibility(View.GONE);


                mAdapter = new MainAdapter(listItems, db, mProgressBar, requiredPercentage, totalPercentage, expectedPercentage, totalClasses);
                mRecyclerView.setAdapter(mAdapter);
            }

        } else if (loaderID == 1) {

            PercentageValue data1 = (PercentageValue) data;
            float expPercentage ;
            percentage = data1.getPercentage();
            expPercentage = data1.getExpectedPercentage();
            @SuppressLint("DefaultLocale") String formattedValue = String.format("%.1f", percentage);
            String formattedExpectedPercentage;

            if (requiredPercentage == 0.0 || totalClasses == 0){
                formattedExpectedPercentage = "Update the required percentage and total classes";
                expectedPercentage.setTextColor(Color.RED);
                expectedPercentage.setTypeface(null, Typeface.NORMAL);
                expectedPercentage.setTypeface(expectedPercentage.getTypeface(), Typeface.ITALIC);
                expectedPercentage.setTextSize(13.0f);


            }
            else {
                formattedExpectedPercentage = String.format("%.1f", expPercentage)+"%";


                if (percentage < requiredPercentage) {

                    mProgressBar.getProgressDrawable().setColorFilter(
                            Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                    totalPercentage.setTextColor(Color.RED);

                } else if (percentage > requiredPercentage) {

                    mProgressBar.getProgressDrawable().setColorFilter(
                            Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);
                    totalPercentage.setTextColor(Color.GREEN);

                }

                if (expPercentage < requiredPercentage) {
                    expectedPercentage.setTextColor(Color.RED);
                } else if (expPercentage > requiredPercentage) {
                    expectedPercentage.setTextColor(Color.GREEN);
                }


            }
            expectedPercentage.setText(String.valueOf(formattedExpectedPercentage));
            totalPercentage.setText(String.valueOf(formattedValue+ "%"));
            mProgressBar.setProgress(Math.round(percentage));

        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    void populatedata()
    {
        listItems.clear();
        mAdapter.notifyDataSetChanged();
    }

    static class PercentageValue{
        float percentage;
        float expectedPercentage;

        PercentageValue(float percentage,float expectedPercentage){
            this.percentage = percentage;
            this.expectedPercentage = expectedPercentage;
        }

        float getPercentage (){return percentage;}
        float getExpectedPercentage() {return expectedPercentage;}
    }

}
