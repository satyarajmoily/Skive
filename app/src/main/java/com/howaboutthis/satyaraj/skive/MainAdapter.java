package com.howaboutthis.satyaraj.skive;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.howaboutthis.satyaraj.skive.subjectprovider.SqlDatabase;
import com.howaboutthis.satyaraj.skive.subjectprovider.SubjectContract;
import com.howaboutthis.satyaraj.skive.subjectprovider.TimetableContract;

import java.util.List;
import java.util.Objects;

public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<MainListItem> mListItem;
    private SQLiteDatabase db;
    private ProgressBar mProgressBar;
    private float requiredPercentage;
    private TextView totalPercentage;
    private TextView expectedPercentage;
    private int totalClasses;

    MainAdapter(List<MainListItem> listItem, SQLiteDatabase db, ProgressBar mProgressBar, float requiredPercentage, TextView totalPercentage, TextView expectedPercentage, int totalClasses) {
        this.mListItem = listItem;
        this.db = db;
        this.mProgressBar = mProgressBar;
        this.requiredPercentage = requiredPercentage;
        this.totalPercentage = totalPercentage;
        this.expectedPercentage = expectedPercentage;
        this.totalClasses = totalClasses;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.main_card_view,parent,false);
        return new ViewItem(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final MainListItem listItemView = mListItem.get(position);
            final ViewItem viewItem = (ViewItem) holder;
        final int state = listItemView.getState();
        String button = listItemView.getButton();
        viewItem.setIsRecyclable(false);


        if (state == 1 && Objects.equals(button, "attended")){
            viewItem.bunked.setVisibility(View.GONE);
            viewItem.notTaken.setVisibility(View.GONE);
            viewItem.attended.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            viewItem.attended.setEnabled(false);

        }else if(state == 1 && Objects.equals(button,"bunked")) {
            viewItem.attended.setVisibility(View.GONE);
            viewItem.notTaken.setVisibility(View.GONE);
            viewItem.bunked.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            viewItem.bunked.setEnabled(false);
        }
        else if ( state == 1 && Objects.equals(button, "taken")){
            viewItem.attended.setVisibility(View.GONE);
            viewItem.bunked.setVisibility(View.GONE);
            viewItem.notTaken.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            viewItem.notTaken.setEnabled(false);
        }

        @SuppressLint("DefaultLocale") final String formattedValue = String.format("%.1f", listItemView.getSubjectPercentage());

        viewItem.subjectNameTextView.setText(listItemView.getSubjectName());

        if (requiredPercentage > listItemView.getSubjectPercentage() ){
            viewItem.subjectPercentageTextView.setTextColor(Color.RED);
        }

        viewItem.subjectPercentageTextView.setText(String.valueOf(formattedValue+"%"));


                viewItem.attended.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {

                SqlDatabase database = new SqlDatabase(db);
                String QUERY = database.query(null,"updateattended",listItemView.getSubjectName());
                db.execSQL(QUERY);

                String selection = SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME+ "=?";
                String[] selectionArgs = new String[]{listItemView.getSubjectName()};

                String QUERY3 = database.query(null,"updatepercentage",listItemView.getSubjectName());
                Cursor c = db.rawQuery(QUERY3,null);
                float percentage ;
                float attended;
                float total;
                c.moveToFirst();
                attended = (c.getFloat(c.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_CLASSES_ATTENDED)));
                total = (c.getFloat(c.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_TOTAL_CLASSES)));
                c.close();

                percentage = (attended/total)*100;



                MainListItem item;
                MainListItem listItem ;
                int size =mListItem.size();
                for(int i = 0; i<size;i++){
                    item = mListItem.get(i);

                    if (Objects.equals(item.getSubjectName(), listItemView.getSubjectName())){
                        int val;
                        String button = null;
                        if (i == position) {
                            String selectionID = TimetableContract.TimetableEntry._ID + "=?";

                            String[] selectionArgsID = new String[]{String.valueOf(position)};

                            val = 1;
                            button = "attended";

                            ContentValues contentValues = new ContentValues();
                            contentValues.put(TimetableContract.TimetableEntry.BUTTON,button);

                            ContentValues contentValues1 = new ContentValues();
                            contentValues1.put(TimetableContract.TimetableEntry.STATE,1);

                            database.update(item.getDayOfTheWeek(),contentValues1,selectionID,selectionArgsID);
                            database.update(item.getDayOfTheWeek(),contentValues,selectionID,selectionArgsID);

                        }
                        else if(item.getState()==1){
                            val = item.getState();
                            button = item.getButton();
                        }else val = 0;


                        listItem = new MainListItem(listItemView.getSubjectName(),percentage,val,button, listItemView.getDayOfTheWeek());
                        mListItem.set(i,listItem);

                    }
                }
                notifyDataSetChanged();

                ContentValues contentValues = new ContentValues();
                contentValues.put(SubjectContract.SubjectEntry.COLUMN_PERCENTAGE,percentage);
                database.update(SubjectContract.SubjectEntry.TABLE_NAME,contentValues,selection,selectionArgs);

                String QUERY2 = database.query(null,"getpercentage",null);

                Cursor c2 = db.rawQuery(QUERY2,null);

                float percent ;
                float expPercentage;
                int totalAttened;
                int classes;
                int count;
                c2.moveToFirst();
                percent = (c2.getFloat(c2.getColumnIndex("AVG("+ SubjectContract.SubjectEntry.COLUMN_PERCENTAGE+")")));
                totalAttened =(c2.getInt(c2.getColumnIndex("SUM("+ SubjectContract.SubjectEntry.COLUMN_CLASSES_ATTENDED+")")));
                classes = (c2.getInt(c2.getColumnIndex("SUM("+ SubjectContract.SubjectEntry.COLUMN_TOTAL_CLASSES+")")));
                count = (c2.getInt(c2.getColumnIndex("COUNT("+ SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME+")")));
                c2.close();

                int totalClassesTogether = count * totalClasses;

                    int classesBunked = classes - totalAttened ;

                int classesLeft = totalClassesTogether - classesBunked;

                expPercentage = ((float)classesLeft/(float)totalClassesTogether)*100;

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
                totalPercentage.setText(String.valueOf(formattedValue+ "%"));
                mProgressBar.setProgress(Math.round(percent));

            }
        });

        viewItem.bunked.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {


                SqlDatabase database = new SqlDatabase(db);
                String QUERY = database.query(null,"updatebunked",listItemView.getSubjectName());
                db.execSQL(QUERY);

                String selection = SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME+ "=?";
                String[] selectionArgs = new String[]{listItemView.getSubjectName()};

                String QUERY3 = database.query(null,"updatepercentage",listItemView.getSubjectName());
                Cursor c = db.rawQuery(QUERY3,null);
                float percentage ;
                float attended;
                float total;
                c.moveToFirst();
                attended = (c.getFloat(c.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_CLASSES_ATTENDED)));
                total = (c.getFloat(c.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_TOTAL_CLASSES)));
                c.close();

                percentage = (attended/total)*100;

                MainListItem item;
                MainListItem listItem ;
                int size =mListItem.size();
                for(int i = 0; i<size;i++){
                    item = mListItem.get(i);

                    if (Objects.equals(item.getSubjectName(), listItemView.getSubjectName())){

                        int val;
                        String button = null;
                        if (i == position) {
                            String selectionID = TimetableContract.TimetableEntry._ID+ "=?";

                            String[] selectionArgsState = new String[]{String.valueOf(position)};

                            val = 1;
                            button = "bunked";

                            ContentValues contentValues = new ContentValues();
                            contentValues.put(TimetableContract.TimetableEntry.BUTTON,button);

                            ContentValues contentValues1 = new ContentValues();
                            contentValues1.put(TimetableContract.TimetableEntry.STATE,1);

                            database.update(listItemView.getDayOfTheWeek(),contentValues1,selectionID,selectionArgsState);
                            database.update(listItemView.getDayOfTheWeek(),contentValues,selectionID,selectionArgsState);

                        }else if (item.getState() ==1){
                            val = item.getState();
                            button = item.getButton();
                        }
                        else
                            val = 0;


                        listItem = new MainListItem(listItemView.getSubjectName(),percentage,val,button, listItemView.getDayOfTheWeek());
                        mListItem.set(i,listItem);

                    }
                }
                notifyDataSetChanged();

                ContentValues contentValues = new ContentValues();
                contentValues.put(SubjectContract.SubjectEntry.COLUMN_PERCENTAGE,percentage);
                database.update(SubjectContract.SubjectEntry.TABLE_NAME,contentValues,selection,selectionArgs);


                String QUERY2 = database.query(null,"getpercentage",null);

                Cursor c2 = db.rawQuery(QUERY2,null);
                float percent ;
                float expPercentage;
                int totalAttened;
                int classes;
                int count;
                c2.moveToFirst();
                percent = (c2.getFloat(c2.getColumnIndex("AVG("+ SubjectContract.SubjectEntry.COLUMN_PERCENTAGE+")")));
                totalAttened =(c2.getInt(c2.getColumnIndex("SUM("+ SubjectContract.SubjectEntry.COLUMN_CLASSES_ATTENDED+")")));
                classes = (c2.getInt(c2.getColumnIndex("SUM("+ SubjectContract.SubjectEntry.COLUMN_TOTAL_CLASSES+")")));
                count = (c2.getInt(c2.getColumnIndex("COUNT("+ SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME+")")));
                c2.close();

                int totalClassesTogether = count * totalClasses;

                int classesBunked = classes - totalAttened ;

                int classesLeft = totalClassesTogether - classesBunked;

                expPercentage = ((float)classesLeft/(float)totalClassesTogether)*100;

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
                totalPercentage.setText(String.valueOf(formattedValue+ "%"));

                mProgressBar.setProgress(Math.round(percent));
            }
        });

        viewItem.notTaken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SqlDatabase database = new SqlDatabase(db);
                MainListItem listItem ;

                listItem = new MainListItem(listItemView.getSubjectName(),listItemView.getSubjectPercentage(),1,"taken",listItemView.getDayOfTheWeek());

                mListItem.set(position,listItem);
                notifyDataSetChanged();

                String selectionID = TimetableContract.TimetableEntry._ID+ "=?";

                String[] selectionArgsState = new String[]{String.valueOf(position)};

                ContentValues contentValues = new ContentValues();
                contentValues.put(TimetableContract.TimetableEntry.BUTTON,"taken");

                ContentValues contentValues1 = new ContentValues();
                contentValues1.put(TimetableContract.TimetableEntry.STATE,1);

                database.update(listItemView.getDayOfTheWeek(),contentValues1,selectionID,selectionArgsState);
                database.update(listItemView.getDayOfTheWeek(),contentValues,selectionID,selectionArgsState);

            }
        });


    }

    @Override
    public int getItemCount() {
        return mListItem.size();
    }

    private class ViewItem extends RecyclerView.ViewHolder{
        TextView subjectNameTextView;
        TextView subjectPercentageTextView;
        Button attended;
        Button bunked;
        Button notTaken;
        ViewItem(View itemView){
            super(itemView);

            subjectNameTextView = itemView.findViewById(R.id.subject_name);
            subjectPercentageTextView = itemView.findViewById(R.id.subject_percentage);
            attended = itemView.findViewById(R.id.attended);
            bunked = itemView.findViewById(R.id.bunked);
            notTaken = itemView.findViewById(R.id.not_taken);

        }
    }


}
