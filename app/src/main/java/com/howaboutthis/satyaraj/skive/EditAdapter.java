package com.howaboutthis.satyaraj.skive;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.howaboutthis.satyaraj.skive.subjectprovider.SubjectContract;
import com.howaboutthis.satyaraj.skive.subjectprovider.TimetableContract;

import java.util.List;

public class EditAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<EditListItem> mListItem;
    private SQLiteDatabase db;

    EditAdapter(List<EditListItem> listItem, SQLiteDatabase db) {
        this.mListItem = listItem;
        this.db = db;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.edit_card_view,parent,false);
        return new ViewItem(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final EditListItem listItemView = mListItem.get(position);
        final ViewItem viewItem = (ViewItem) holder;

        @SuppressLint("DefaultLocale") String formattedValue = String.format("%.1f", listItemView.getSubjectPercentage());

        viewItem.subjectNameTextView.setText(listItemView.getSubjectName());
        viewItem.subjectPercentageTextView.setText(String.valueOf(formattedValue+"%"));
        viewItem.subjectClassesAttended.setText(String.valueOf(listItemView.getClassesAttended()));
        viewItem.subjectTotalClasses.setText(String.valueOf(listItemView.getTotalClasses()));

        viewItem.subjectClassesAttended.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!viewItem.subjectClassesAttended.getText().toString().trim().equals("")) {
                    float total = Float.parseFloat(viewItem.subjectTotalClasses.getText().toString());
                    float attend = Float.parseFloat(String.valueOf(s));

                    if (attend <= total) {
                        float percent =  (attend / total) * 100;
                        @SuppressLint("DefaultLocale") String formattedValue = String.format("%.1f", percent);
                        viewItem.subjectPercentageTextView.setText(String.valueOf(formattedValue+"%"));
                    }else
                        viewItem.subjectPercentageTextView.setText("0.0%");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!TextUtils.isEmpty(s.toString().trim())) {
                    float attend = Float.parseFloat(s.toString());
                    float total = Float.parseFloat(viewItem.subjectTotalClasses.getText().toString());
                    float percent = 0;
                    if (attend <= total) {
                        percent = (attend / total) * 100;
                        @SuppressLint("DefaultLocale") String formattedValue = String.format("%.1f", percent);
                        viewItem.subjectPercentageTextView.setText(String.valueOf(formattedValue+"%"));


                    }else
                        viewItem.subjectPercentageTextView.setText("0.0%");
                    String selection = SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME + "=?";
                    String[] selectionArgs = new String[]{viewItem.subjectNameTextView.getText().toString()};

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(SubjectContract.SubjectEntry.COLUMN_CLASSES_ATTENDED,attend);
                    contentValues.put(SubjectContract.SubjectEntry.COLUMN_PERCENTAGE,percent);

                    db.update(SubjectContract.SubjectEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                }
            }
        });

         viewItem.subjectTotalClasses.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!viewItem.subjectTotalClasses.getText().toString().trim().equals("")) {
                    float totalClasses = Float.parseFloat(String.valueOf(s));
                    float attended = Float.parseFloat(viewItem.subjectClassesAttended.getText().toString());

                    if (attended <= totalClasses) {
                       float percent = ( attended/ totalClasses) * 100;
                        @SuppressLint("DefaultLocale") String formattedValue = String.format("%.1f", percent);
                        viewItem.subjectPercentageTextView.setText(String.valueOf(formattedValue+"%"));
                    }else
                        viewItem.subjectPercentageTextView.setText("0.0%");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!TextUtils.isEmpty(s.toString().trim())) {
                    float totalClasses = Float.parseFloat(String.valueOf(s));
                    float attended = Float.parseFloat(viewItem.subjectClassesAttended.getText().toString());
                    float percent = 0;

                    if (attended <= totalClasses) {
                        percent = (attended / totalClasses) * 100;
                        @SuppressLint("DefaultLocale") String formattedValue = String.format("%.1f", percent);
                        viewItem.subjectPercentageTextView.setText(String.valueOf(formattedValue+"%"));
                    }else
                        viewItem.subjectPercentageTextView.setText("0.0%");

                        String selection = SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME + "=?";
                        String[] selectionArgs = new String[]{viewItem.subjectNameTextView.getText().toString()};

                        ContentValues contentValues = new ContentValues();
                        contentValues.put(SubjectContract.SubjectEntry.COLUMN_TOTAL_CLASSES,totalClasses);
                        contentValues.put(SubjectContract.SubjectEntry.COLUMN_PERCENTAGE,percent);

                        db.update(SubjectContract.SubjectEntry.TABLE_NAME, contentValues, selection, selectionArgs);

                }

            }
        });

       viewItem.subjectNameTextView.addTextChangedListener(new TextWatcher() {
           String subject = listItemView.getSubjectName();
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
               Log.i("this","hey");

           }

           @Override
           public void afterTextChanged(Editable s) {

                   if (!TextUtils.isEmpty(s.toString().trim())) {

                       String monday = TimetableContract.TimetableEntry.TABLE_MONDAY;
                       String tuesday = TimetableContract.TimetableEntry.TABLE_TUESDAY ;
                       String wednesday =  TimetableContract.TimetableEntry.TABLE_WEDNESDAY;
                       String thursday =  TimetableContract.TimetableEntry.TABLE_THURSDAY ;
                       String friday =  TimetableContract.TimetableEntry.TABLE_FRIDAY ;
                       String saturday =  TimetableContract.TimetableEntry.TABLE_SATURDAY ;
                       String sunday =  TimetableContract.TimetableEntry.TABLE_SUNDAY ;

                       String column = TimetableContract.TimetableEntry.COLUMN + "=?";

                       String selection = SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME + "=?";

                       String[] selectionArgs = new String[]{subject};

                       subject = String.valueOf(s);

                       ContentValues contentValues = new ContentValues();
                       ContentValues contentValues1 = new ContentValues();

                       contentValues.put(SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME, subject);

                       contentValues1.put(TimetableContract.TimetableEntry.COLUMN, subject);

                       db.update(SubjectContract.SubjectEntry.TABLE_NAME, contentValues, selection, selectionArgs);

                       db.update(monday,contentValues1,column,selectionArgs);
                       db.update(tuesday,contentValues1,column,selectionArgs);
                       db.update(wednesday,contentValues1,column,selectionArgs);
                       db.update(thursday,contentValues1,column,selectionArgs);
                       db.update(friday,contentValues1,column,selectionArgs);
                       db.update(saturday,contentValues1,column,selectionArgs);
                       db.update(sunday,contentValues1,column,selectionArgs);

               }
           }
       });



    }

    @Override
    public int getItemCount() {
        return mListItem.size();
    }

    private class ViewItem extends RecyclerView.ViewHolder{
        EditText subjectNameTextView;
        TextView subjectPercentageTextView;
        EditText subjectClassesAttended;
        EditText subjectTotalClasses;

        ViewItem(View itemView){
            super(itemView);

            subjectNameTextView = itemView.findViewById(R.id.subject_name);
            subjectPercentageTextView = itemView.findViewById(R.id.subject_percentage);
            subjectClassesAttended = itemView.findViewById(R.id.classes_attended);
            subjectTotalClasses = itemView.findViewById(R.id.total_classes);
        }
    }
}



