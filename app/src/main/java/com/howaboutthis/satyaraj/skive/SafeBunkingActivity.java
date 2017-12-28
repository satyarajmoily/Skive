package com.howaboutthis.satyaraj.skive;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class SafeBunkingActivity extends NavigationDrawer implements LoaderManager.LoaderCallbacks<List<SafeBunkingListItem>> {

    private static final String DB_NAME = "skive_database.db";
    private RecyclerView mRecyclerView;

    List<SafeBunkingListItem> listItems;
    TextView emptyView;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        navigationView.getMenu().getItem(0).setChecked(true);
        assert inflater != null;
        @SuppressLint("InflateParams") View contentView = inflater.inflate(R.layout.activity_safe_bunking, null, false);
        drawer.addView(contentView, 0);
        setTitle("Safe Bunking");
        mRecyclerView = findViewById(R.id.recycler_view_safe_bunking);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        emptyView = findViewById(R.id.empty_view);
        db = openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);

        getLoaderManager().initLoader(1, null, this);





    }

    @Override
    public Loader<List<SafeBunkingListItem>> onCreateLoader(int id, Bundle args) {
        return new SafeBunkingLoader(this,db);
    }

    @Override
    public void onLoadFinished(Loader<List<SafeBunkingListItem>> loader, List<SafeBunkingListItem> data) {
        listItems =  data;
        if (listItems.size()==0){
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            emptyView.setVisibility(View.GONE);
            SharedPreferences sharedPreferences = getSharedPreferences("shared_preferences", MODE_PRIVATE);
            float requiredPercentage = sharedPreferences.getFloat("required_percentage", 0.0f);
            RecyclerView.Adapter<RecyclerView.ViewHolder> mAdapter = new SafeBunkingAdapter(listItems, requiredPercentage);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<SafeBunkingListItem>> loader) {

    }
}
