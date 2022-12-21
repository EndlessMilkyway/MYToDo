package com.endlessmilkyway.mytodo;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ToDoItem {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "taskName")
    public String taskName;

    @ColumnInfo(name = "category")
    public String category;

    @ColumnInfo(name = "doneFlag")
    public int doneFlag;

    @ColumnInfo(name = "importantFlag")
    public int importantFlag;

    @ColumnInfo(name = "dueDate")
    public String dueDate;

    public int getId() {
        return id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getDoneFlag() {
        return doneFlag;
    }

    public void setDoneFlag(int flag) {
        this.doneFlag = flag;
    }

    public int getImportantFlag() {
        return importantFlag;
    }

    public void setImportantFlag(int flag) {
        this.importantFlag = flag;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String date) {
        this.dueDate = date;
    }
}
