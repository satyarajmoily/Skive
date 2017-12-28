package com.howaboutthis.satyaraj.skive;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EditActivity extends NavigationDrawer implements LoaderManager.LoaderCallbacks<List<EditListItem>>{

    private static final String DB_NAME = "skive_database.db";
    private RecyclerView mRecyclerView;
    List<EditListItem> subjectList;
    SQLiteDatabase db;
    TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        assert inflater != null;
        @SuppressLint("InflateParams") View contentView = inflater.inflate(R.layout.activity_edit, null, false);
        drawer.addView(contentView, 0);
        setTitle("Edit Mode");
        navigationView.getMenu().getItem(1).setChecked(true);

        emptyView = findViewById(R.id.empty_view);
        db = openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE,null);

        mRecyclerView = findViewById(R.id.recycler_view_edit);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        subjectList = new ArrayList<>();

        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public Loader<List<EditListItem>> onCreateLoader(int id, Bundle args) {
        return new EditLoader(this,db);
    }

    @Override
    public void onLoadFinished(Loader<List<EditListItem>> loader, List<EditListItem> data) {
        if (data.size()==0){
            emptyView.setVisibility(View.VISIBLE);
        }else {
            emptyView.setVisibility(View.GONE);
            RecyclerView.Adapter mAdapter = new EditAdapter(data, db);
            mRecyclerView.setAdapter(mAdapter);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<EditListItem>> loader) {

    }
}

