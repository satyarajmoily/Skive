package com.howaboutthis.satyaraj.skive;

import android.annotation.SuppressLint;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.howaboutthis.satyaraj.skive.subjectprovider.SqlDatabase;
import com.howaboutthis.satyaraj.skive.subjectprovider.SubjectContract;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class SafeBunkingLoader extends AsyncTaskLoader<List<SafeBunkingListItem>> {

    private SQLiteDatabase mDatabase;
    @SuppressLint("StaticFieldLeak")
    private Context mContext;

    SafeBunkingLoader(Context context, SQLiteDatabase database) {
        super(context);
        this.mDatabase = database;
        this.mContext = context;
    }

    @Override
    protected void onStartLoading(){
        forceLoad();
    }

    @Override
    public List<SafeBunkingListItem> loadInBackground() {

        SafeBunkingListItem mlistItem;
        List<SafeBunkingListItem> subjectList = new ArrayList<>();
        SqlDatabase db = new SqlDatabase(mDatabase);
        String QUERY = db.query(null,"editactivity",null);
        Cursor c = mDatabase.rawQuery(QUERY,null);

        SharedPreferences sharedPreferences=  mContext.getSharedPreferences("shared_preferences", MODE_PRIVATE);
        float requiredPercentage = sharedPreferences.getFloat("required_percentage", 0.0f);
        int totalClasses = sharedPreferences.getInt("total_classes", 0);
        int attend;
        float percentageAfter;

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                String subjectName = c.getString( c.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_SUBJECT_NAME));
                Float subjectPercentage = c.getFloat( c.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_PERCENTAGE));
                int attended = c.getInt(c.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_CLASSES_ATTENDED));
                int total = c.getInt(c.getColumnIndex(SubjectContract.SubjectEntry.COLUMN_TOTAL_CLASSES));
                String comment = "";

                attend = (attended + (totalClasses - total));

                percentageAfter = ((float)attend/(float)totalClasses) *100;
                @SuppressLint("DefaultLocale")
                String formattedValue = String.format("%.1f",percentageAfter);

                int mustAttend = (int)(requiredPercentage*totalClasses)/100;

              int classesLeft = totalClasses - total;
                if (classesLeft <= 0)
                {
                    @SuppressLint("DefaultLocale")
                    String value = String.format("%.1f",subjectPercentage);
                    if(subjectPercentage > requiredPercentage)
                        comment = "Congratulations!!! You have made it pass "+requiredPercentage+"%.";
                    else if (subjectPercentage > requiredPercentage - 5 && subjectPercentage < requiredPercentage )
                        comment = "Glad you have made it up to here, but I don't think "+value+"% is bad right.";
                    else
                        comment = "Sorry that I couldn't help you pass "+requiredPercentage+"%. You wouldn't listen. You don't have anymore classes.";


                }
                else if (percentageAfter == 100)
                {
                    int left = attend - mustAttend;
                    comment = "Hope they give you an award for your 100%, anyways getting back to the subject you can still skip "+left
                            +" classes by maintaining the required percentage.";

                }else if (percentageAfter >= 90 && percentageAfter <100)
                {
                    int left = attend - mustAttend;
                    comment = "You can still skip "+ left + " classes by maintaining the required percentage. And if you plan to attend all then you will have "+formattedValue+"%.";

                }else if (percentageAfter  == requiredPercentage){
                    comment = "No way you can bunk any class from this subject. You skip one class you would go below "
                            + requiredPercentage + "%.";

                }else if (percentageAfter > requiredPercentage && percentageAfter< 90){
                    int left = attend - mustAttend;
                    comment = "Be careful from now on, you just have "+ left + " classes left. Do blame me if you skip more classes than I tell.";

                }else if (percentageAfter < requiredPercentage && percentageAfter > 80)
                    comment = "OH no, You just went below "+requiredPercentage+"% but if you attend all the classes from now, you " +
                            "will have "+formattedValue+"%.";

                else if (percentageAfter <= 80 && percentageAfter > 70)
                    comment = "It will kepp getting worst if you keep skipping classes, attended all and you will get at least "+formattedValue+"%.";

                else if (percentageAfter <= 70 && percentageAfter > 60)
                    comment = "And it is getting worst. PLEASE ATTEND FOR GOD'S SAKE. See if you attend all the classes from now I will guarantee you at you will have "+ formattedValue +"%.";

                else if (percentageAfter <= 60 && percentageAfter > 50)
                    comment = "Hmmm, I lost hopes in this subject but if you attend all the classes left then I can assure you that you are gonna improve the overall percentage but this subject you can only achieve "+formattedValue+"%.";

                else if (percentageAfter <= 50 && percentageAfter > 40)
                    comment = "I'am tired of helping you anymore, you wouldn't still listen and even if you attend you are gonna get at the most "+ formattedValue+"%.";

                else if (percentageAfter <= 40 && percentageAfter > 30)
                    comment = "OK, It's better you enjoy bunking rather than attending because its not gonna effect. You can barely make it to "+ formattedValue+"% if you attend all the classes.";

                else if (percentageAfter <= 30)
                    comment = "It's better you sleep, Why miss sleep when you are not gonna effect your percentage anymore.";

                mlistItem = new SafeBunkingListItem(subjectName,subjectPercentage,comment,attended,total);
                subjectList.add(mlistItem);
                c.moveToNext();
            }
        }

        c.close();

        return subjectList ;
    }
}
