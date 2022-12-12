package com.endlessmilkyway.mytodo;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

public class HomeFragment extends Fragment {
    private Context mContext;
    private List<ToDoItem> toDoAllData = new ArrayList<>();
    private List<ToDoItem> toDoDoneData = new ArrayList<>();
    private ToDoItemDB database;
    private ArrayList<String> categories;
    private String selectedCategory;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int FLAG_INIT_VALUE = 0;

        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_home, container, false);
        mContext = requireActivity().getApplicationContext();

        RecyclerView toDoAllList = rootView.findViewById(R.id.toDoAllList);
        MaterialTextView doneTaskCounter = rootView.findViewById(R.id.doneTaskCounter);
        RecyclerView toDoDoneList = rootView.findViewById(R.id.toDoDoneList);
        ExtendedFloatingActionButton floatingActionButton = rootView.findViewById(R.id.floatingButton);

        this.initializedData();
        ToDoItemAdapter toDoAllAdapter = new ToDoItemAdapter(getActivity(), toDoAllData);
        ToDoItemAdapter toDoDoneAdapter = new ToDoItemAdapter(getActivity(), toDoDoneData);

        doneTaskCounter.setText(getString(R.string.taskCounter, toDoDoneData.size()));

        toDoAllList.setLayoutManager(new LinearLayoutManager(mContext));
        toDoDoneList.setLayoutManager(new LinearLayoutManager(mContext));
        toDoAllList.setAdapter(toDoAllAdapter);
        toDoDoneList.setAdapter(toDoDoneAdapter);

        floatingActionButton.setOnClickListener(view -> {
            final Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.dialog_todo);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.show();

            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

            Long today = MaterialDatePicker.todayInUtcMilliseconds();

            TextInputEditText toDoInput = dialog.findViewById(R.id.toDoInput);
            AutoCompleteTextView dropDownItem = dialog.findViewById(R.id.dropDownItem);
            TextInputEditText categoryInput = dialog.findViewById(R.id.categoryInput);
            MaterialButton categorySubmitButton = dialog.findViewById(R.id.categorySubmitButton);
            ImageButton calendarButton = dialog.findViewById(R.id.calendarButton);
            TextInputEditText durationTo = dialog.findViewById(R.id.durationTo);
            MaterialButton cancelButton = dialog.findViewById(R.id.toDoCancelButton);
            MaterialButton submitButton = dialog.findViewById(R.id.toDoSubmitButton);

            ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getActivity(), R.layout.category_list, categories);
            dropDownItem.setAdapter(categoryAdapter);

            dropDownItem.setOnItemClickListener((adapterView, view15, i, l) -> selectedCategory = ((TextView) view15).getText().toString());

            categorySubmitButton.setOnClickListener(view12 -> {
                categories.add(Objects.requireNonNull(categoryInput.getText()).toString());
                categoryInput.setText("");
                setStringArrayPref(mContext, categories);
                getStringArrayPref(mContext);
            });

            calendarButton.setOnClickListener(view1 -> {
                MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("마감 기한을 설정해주세요")
                        .setSelection(today).build();

                materialDatePicker.show(requireActivity().getSupportFragmentManager(), "DATE_PICKER");

                materialDatePicker.addOnPositiveButtonClickListener((MaterialPickerOnPositiveButtonClickListener<Long>) selection -> {
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
                    Date date = new Date();
                    date.setTime(selection);
                    durationTo.setText(simpleDateFormat.format(date));
                });
            });

            cancelButton.setOnClickListener(view13 -> {
                toDoInput.setText("");
                dropDownItem.dismissDropDown();
                categoryInput.setText("");
                dialog.dismiss();
            });

            submitButton.setOnClickListener(view14 -> {
                ToDoItem toDoItem = new ToDoItem();
                toDoItem.setTaskName(Objects.requireNonNull(toDoInput.getText()).toString().trim());
                toDoItem.setCategory(selectedCategory);
                toDoItem.setDoneFlag(FLAG_INIT_VALUE);
                toDoItem.setImportantFlag(FLAG_INIT_VALUE);
                toDoItem.setDueDate(Objects.requireNonNull(durationTo.getText()).toString());
                database.toDoItemDao().insertToDo(toDoItem);

                toDoInput.setText("");
                dropDownItem.dismissDropDown();
                categoryInput.setText("");
                dialog.dismiss();
                toDoAllData.clear();
                toDoAllData.addAll(database.toDoItemDao().getUnfinished());
                toDoAllAdapter.notifyDataSetChanged();
            });
        });

        return rootView;
    }

    private void initializedData() {
        database = ToDoItemDB.getInstance(mContext);
        toDoAllData = database.toDoItemDao().getUnfinished();
        toDoDoneData = database.toDoItemDao().getDone();
        categories = getStringArrayPref(mContext);
    }

    private void setStringArrayPref(Context context, ArrayList<String> values) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray data = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            data.put(values.get(i));
        }

        if (!values.isEmpty()) {
            editor.putString("categories", data.toString());
        }
        if (values.isEmpty()) {
            editor.putString("categories", null);
        }
        editor.apply();
    }

    private ArrayList<String> getStringArrayPref(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString("categories", null);
        ArrayList<String> urls = new ArrayList<>();
        if (json != null) {
            try {
                JSONArray data = new JSONArray(json);
                for (int i = 0; i < data.length(); i++) {
                    String url = data.optString(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }
}