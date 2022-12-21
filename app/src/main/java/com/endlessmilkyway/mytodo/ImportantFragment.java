package com.endlessmilkyway.mytodo;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ImportantFragment extends Fragment {
    private Context mContext;
    private ToDoItemDB database;
    private List<ToDoItem> toDoImportantData = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_important, container, false);
        mContext = getActivity().getApplicationContext();
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle("중요한 일");

        RecyclerView toDoImportantList = rootView.findViewById(R.id.toDoImportantList);

        this.initializedData();

        toDoImportantList.setLayoutManager(new LinearLayoutManager(mContext));
        toDoImportantList.setAdapter(new ToDoItemAdapter(getActivity(), toDoImportantData));

        return rootView;
    }

    private void initializedData() {
        database = ToDoItemDB.getInstance(mContext);
        toDoImportantData = database.toDoItemDao().getImportant();
    }
}