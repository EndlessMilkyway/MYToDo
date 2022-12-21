package com.endlessmilkyway.mytodo;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.concurrent.atomic.AtomicReference;

public class HomeFragment extends Fragment {
    private SharedPreferences pref;
    private Context mContext;
    private List<ToDoItem> toDoAllData = new ArrayList<>();
    private List<ToDoItem> toDoDoneData = new ArrayList<>();
    private ToDoItemDB database;
    private ArrayList<String> categories;
    private String selectedCategory;
    private RecyclerView toDoAllList;
    private RecyclerView toDoDoneList;
    private ToDoItemAdapter toDoAllAdapter;
    public ToDoItemAdapter toDoDoneAdapter;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int FLAG_INIT_VALUE = 0;

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);
        mContext = requireActivity().getApplicationContext();

        String nickName = mContext.getSharedPreferences("isFirst", 0).getString("nickname", null);
        MaterialTextView welcomeMessage = rootView.findViewById(R.id.welcomeMessage);
        welcomeMessage.setText(getString(R.string.welcomeMessage, nickName));

        toDoAllList = rootView.findViewById(R.id.toDoAllList);
        MaterialTextView doneTaskCounter = rootView.findViewById(R.id.doneTaskCounter);
        toDoDoneList = rootView.findViewById(R.id.toDoDoneList);
        ExtendedFloatingActionButton floatingActionButton = rootView.findViewById(R.id.floatingButton);

        pref = mContext.getSharedPreferences("setting", 0);

        this.initializedData();

        toDoAllAdapter = new ToDoItemAdapter(getActivity(), toDoAllData);
        toDoDoneAdapter = new ToDoItemAdapter(getActivity(), toDoDoneData);
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle("현재 할 일 : " + toDoAllData.size() + "개");

        doneTaskCounter.setText(getString(R.string.taskCounter, toDoDoneData.size()));

        toDoAllList.setLayoutManager(new LinearLayoutManager(mContext));
        toDoDoneList.setLayoutManager(new LinearLayoutManager(mContext));
        toDoAllList.setAdapter(toDoAllAdapter);
        toDoDoneList.setAdapter(toDoDoneAdapter);

        toDoAllAdapter.setOnItemClickListener(new ToDoItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                updateData();

                toDoAllAdapter.notifyDataSetChanged();
                toDoDoneAdapter.notifyDataSetChanged();
                actionBar.setTitle("현재 할 일 : " + toDoAllData.size() + "개");
                doneTaskCounter.setText(getString(R.string.taskCounter, toDoDoneData.size()));
            }
        });

        toDoDoneAdapter.setOnItemClickListener(new ToDoItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                updateData();

                toDoAllAdapter.notifyDataSetChanged();
                toDoDoneAdapter.notifyDataSetChanged();
                actionBar.setTitle("현재 할 일 : " + toDoAllData.size() + "개");
                doneTaskCounter.setText(getString(R.string.taskCounter, toDoDoneData.size()));
            }
        });

        toDoAllAdapter.setOnLongItemClickListener(new ToDoItemAdapter.OnLongItemClickListener() {
            @Override
            public void onLongItemClick(ToDoItem data) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_todo);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.show();

                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

                AtomicReference<Long> selectedDate = new AtomicReference<>();

                TextInputEditText toDoInput = dialog.findViewById(R.id.toDoInput);
                AutoCompleteTextView dropDownItem = dialog.findViewById(R.id.dropDownItem);
                TextInputEditText categoryInput = dialog.findViewById(R.id.categoryInput);
                MaterialButton categorySubmitButton = dialog.findViewById(R.id.categorySubmitButton);
                ImageButton calendarButton = dialog.findViewById(R.id.calendarButton);
                TextInputEditText durationTo = dialog.findViewById(R.id.durationTo);
                MaterialButton deleteButton = dialog.findViewById(R.id.toDoCancelButton);
                MaterialButton editButton = dialog.findViewById(R.id.toDoSubmitButton);

                deleteButton.setText("삭제");
                editButton.setText("수정");

                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getActivity(), R.layout.category_list, categories);
                dropDownItem.setAdapter(categoryAdapter);
                dropDownItem.setOnItemClickListener((adapterView, view15, i, l) -> selectedCategory = ((TextView) view15).getText().toString());

                toDoInput.setText(data.getTaskName());
                dropDownItem.setText(data.getCategory());
                selectedCategory = data.getCategory();
                selectedDate.set(Long.parseLong(data.getDueDate()));

                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
                Date storedDate = new Date();
                storedDate.setTime(Long.parseLong(data.getDueDate()));

                durationTo.setText(simpleDateFormat.format(storedDate));

                categorySubmitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        categories.add(Objects.requireNonNull(categoryInput.getText()).toString());
                        categoryInput.setText("");
                        setStringArrayPref(mContext, categories);
                        getStringArrayPref(mContext);
                    }
                });

                calendarButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                                .setTitleText("마감 기한을 설정해주세요")
                                .setSelection(Long.parseLong(data.getDueDate())).build();

                        materialDatePicker.show(requireActivity().getSupportFragmentManager(), "DATE_PICKER");

                        materialDatePicker.addOnPositiveButtonClickListener((MaterialPickerOnPositiveButtonClickListener<Long>) selection -> {
                            selectedDate.set(selection);
                            @SuppressLint("SimpleDateFormat")
                            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy년 MM월 dd일");
                            Date date = new Date();
                            date.setTime(selection);
                            durationTo.setText(simpleDateFormat1.format(date));
                        });
                    }
                });

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (pref.getBoolean("checkBeforeDelete", false)) {
                            final Dialog noticeDialog = new Dialog(getActivity());
                            noticeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            noticeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            noticeDialog.setContentView(R.layout.dialog_delete);
                            noticeDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                            noticeDialog.show();

                            MaterialButton negativeButton = noticeDialog.findViewById(R.id.negativeButton);
                            MaterialButton positiveButton = noticeDialog.findViewById(R.id.positiveButton);

                            negativeButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    noticeDialog.dismiss();
                                }
                            });

                            positiveButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    database.toDoItemDao().deleteToDo(data);
                                    updateData();
                                    toDoAllAdapter.notifyDataSetChanged();
                                    toDoDoneAdapter.notifyDataSetChanged();
                                    actionBar.setTitle("현재 할 일 : " + toDoAllData.size() + "개");
                                    noticeDialog.dismiss();
                                    dialog.dismiss();
                                }
                            });
                        }

                        if (!pref.getBoolean("checkBeforeDelete", false)) {
                            database.toDoItemDao().deleteToDo(data);
                            updateData();
                            toDoAllAdapter.notifyDataSetChanged();
                            toDoDoneAdapter.notifyDataSetChanged();
                            actionBar.setTitle("현재 할 일 : " + toDoAllData.size() + "개");
                            dialog.dismiss();
                        }
                    }
                });

                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        database.toDoItemDao().updateToDo(data.getId(), Objects.requireNonNull(toDoInput.getText()).toString().trim(),
                                selectedCategory, Long.toString(selectedDate.get()));

                        updateData();
                        toDoAllAdapter.notifyDataSetChanged();
                        toDoDoneAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
            }
        });

        toDoDoneAdapter.setOnLongItemClickListener(new ToDoItemAdapter.OnLongItemClickListener() {
            @Override
            public void onLongItemClick(ToDoItem data) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_todo);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.show();

                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

                AtomicReference<Long> selectedDate = new AtomicReference<>();

                TextInputEditText toDoInput = dialog.findViewById(R.id.toDoInput);
                AutoCompleteTextView dropDownItem = dialog.findViewById(R.id.dropDownItem);
                TextInputEditText categoryInput = dialog.findViewById(R.id.categoryInput);
                MaterialButton categorySubmitButton = dialog.findViewById(R.id.categorySubmitButton);
                ImageButton calendarButton = dialog.findViewById(R.id.calendarButton);
                TextInputEditText durationTo = dialog.findViewById(R.id.durationTo);
                MaterialButton deleteButton = dialog.findViewById(R.id.toDoCancelButton);
                MaterialButton editButton = dialog.findViewById(R.id.toDoSubmitButton);

                deleteButton.setText("삭제");
                editButton.setText("수정");

                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getActivity(), R.layout.category_list, categories);
                dropDownItem.setAdapter(categoryAdapter);

                dropDownItem.setOnItemClickListener((adapterView, view15, i, l) -> selectedCategory = ((TextView) view15).getText().toString());

                toDoInput.setText(data.getTaskName());
                dropDownItem.setText(data.getCategory());
                selectedCategory = data.getCategory();
                selectedDate.set(Long.parseLong(data.getDueDate()));

                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
                Date storedDate = new Date();
                storedDate.setTime(Long.parseLong(data.getDueDate()));

                durationTo.setText(simpleDateFormat.format(storedDate));

                categorySubmitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        categories.add(Objects.requireNonNull(categoryInput.getText()).toString());
                        categoryInput.setText("");
                        setStringArrayPref(mContext, categories);
                        getStringArrayPref(mContext);
                    }
                });

                calendarButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                                .setTitleText("마감 기한을 설정해주세요")
                                .setSelection(Long.parseLong(data.getDueDate())).build();

                        materialDatePicker.show(requireActivity().getSupportFragmentManager(), "DATE_PICKER");

                        materialDatePicker.addOnPositiveButtonClickListener((MaterialPickerOnPositiveButtonClickListener<Long>) selection -> {
                            selectedDate.set(selection);
                            @SuppressLint("SimpleDateFormat")
                            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy년 MM월 dd일");
                            Date date = new Date();
                            date.setTime(selection);
                            durationTo.setText(simpleDateFormat1.format(date));
                        });
                    }
                });

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (pref.getBoolean("checkBeforeDelete", false)) {
                            final Dialog noticeDialog = new Dialog(getActivity());
                            noticeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            noticeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            noticeDialog.setContentView(R.layout.dialog_delete);
                            noticeDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                            noticeDialog.show();

                            MaterialButton negativeButton = noticeDialog.findViewById(R.id.negativeButton);
                            MaterialButton positiveButton = noticeDialog.findViewById(R.id.positiveButton);

                            negativeButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    noticeDialog.dismiss();
                                }
                            });

                            positiveButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    database.toDoItemDao().deleteToDo(data);
                                    updateData();
                                    toDoAllAdapter.notifyDataSetChanged();
                                    toDoDoneAdapter.notifyDataSetChanged();
                                    actionBar.setTitle("현재 할 일 : " + toDoAllData.size() + "개");
                                    noticeDialog.dismiss();
                                    dialog.dismiss();
                                }
                            });
                        }

                        if (!pref.getBoolean("checkBeforeDelete", false)) {
                            database.toDoItemDao().deleteToDo(data);
                            updateData();
                            toDoAllAdapter.notifyDataSetChanged();
                            toDoDoneAdapter.notifyDataSetChanged();
                            actionBar.setTitle("현재 할 일 : " + toDoAllData.size() + "개");
                            dialog.dismiss();
                        }
                    }
                });

                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        database.toDoItemDao().updateToDo(data.getId(), Objects.requireNonNull(toDoInput.getText()).toString().trim(),
                                selectedCategory, Long.toString(selectedDate.get()));

                        updateData();

                        toDoAllAdapter.notifyDataSetChanged();
                        toDoDoneAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
            }
        });

        floatingActionButton.setOnClickListener(view -> {
            final Dialog dialog = new Dialog(getActivity());
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_todo);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.show();

            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

            Long today = MaterialDatePicker.todayInUtcMilliseconds();
            AtomicReference<Long> selectedDate = new AtomicReference<>();

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
                    selectedDate.set(selection);
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
                    Date date = new Date();
                    date.setTime(selection);
                    durationTo.setText(simpleDateFormat.format(date));
                });
            });

            cancelButton.setOnClickListener(view13 -> {
                dialog.dismiss();
            });

            submitButton.setOnClickListener(view14 -> {
                ToDoItem toDoItem = new ToDoItem();
                toDoItem.setTaskName(Objects.requireNonNull(toDoInput.getText()).toString().trim());
                toDoItem.setCategory(selectedCategory);
                toDoItem.setDoneFlag(FLAG_INIT_VALUE);
                toDoItem.setImportantFlag(FLAG_INIT_VALUE);
                toDoItem.setDueDate(Objects.requireNonNull(durationTo.getText()).toString());
                toDoItem.setDueDate(Long.toString(selectedDate.get()));
                database.toDoItemDao().insertToDo(toDoItem);

                // toDoAllData.clear();
                // toDoAllData.addAll(database.toDoItemDao().getUnfinished());
                // toDoAllAdapter.notifyItemInserted(toDoAllData.size());
                updateData();
                toDoAllAdapter.notifyDataSetChanged();
                dialog.dismiss();
                actionBar.setTitle("현재 할 일 : " + toDoAllData.size() + "개");
            });
        });

        return rootView;
    }

    private void initializedData() {
        database = ToDoItemDB.getInstance(mContext);

        if (pref.getBoolean("addNewTaskAtTop", false)) {
            toDoAllData = database.toDoItemDao().getUnfinishedDesc();
            toDoDoneData = database.toDoItemDao().getDoneDesc();
        }

        if (!pref.getBoolean("addNewTaskAtTop", false)) {
            toDoAllData = database.toDoItemDao().getUnfinished();
            toDoDoneData = database.toDoItemDao().getDone();
        }
        categories = getStringArrayPref(mContext);
    }

    private void updateData() {
        if (pref.getBoolean("addNewTaskAtTop", false)) {
            toDoAllData.clear();
            toDoDoneData.clear();
            toDoAllData.addAll(database.toDoItemDao().getUnfinishedDesc());
            toDoDoneData.addAll(database.toDoItemDao().getDoneDesc());
        }

        if (!pref.getBoolean("addNewTaskAtTop", false)) {
            toDoAllData.clear();
            toDoDoneData.clear();
            toDoAllData.addAll(database.toDoItemDao().getUnfinished());
            toDoDoneData.addAll(database.toDoItemDao().getDone());
        }
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
