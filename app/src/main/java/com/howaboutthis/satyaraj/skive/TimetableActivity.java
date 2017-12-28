package com.howaboutthis.satyaraj.skive;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TimetableActivity extends NavigationDrawer{

    ListView listView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        navigationView.getMenu().getItem(3).setChecked(true);
        assert inflater != null;
        @SuppressLint("InflateParams") final View contentView = inflater.inflate(R.layout.activity_days, null, false);
        drawer.addView(contentView, 0);

        setTitle("TimeTable");

        listView = findViewById(R.id.timetable_days_list);

        String[] values = new String[] {
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday",
                "Sunday" };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String  itemValue    = (String) listView.getItemAtPosition(position);
                Intent intent = new Intent(TimetableActivity.this, AddTimetable.class);
                intent.putExtra("day_of_the_week", itemValue);
                startActivity(intent);

            }

        });
    }


}
