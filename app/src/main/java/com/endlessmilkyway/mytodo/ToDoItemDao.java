package com.endlessmilkyway.mytodo;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ToDoItemDao {
    @Insert
    void insertToDo(ToDoItem toDoItem);

    @Query("SELECT * FROM ToDoItem")
    List<ToDoItem> getAll();

    @Query("SELECT * FROM ToDoItem WHERE doneFlag = 0")
    List<ToDoItem> getUnfinished();

    @Query("SELECT * FROM ToDoItem WHERE doneFlag = 0 ORDER BY id DESC")
    List<ToDoItem> getUnfinishedDesc();

    @Query("SELECT * FROM ToDoItem WHERE doneFlag = 1")
    List<ToDoItem> getDone();

    @Query("SELECT * FROM ToDoItem WHERE doneFlag = 1 ORDER BY id DESC")
    List<ToDoItem> getDoneDesc();

    @Query("SELECT * FROM ToDoItem WHERE importantFlag = 1")
    List<ToDoItem> getImportant();

    @Query("SELECT * FROM ToDoItem WHERE importantFlag = 1 ORDER BY id DESC")
    List<ToDoItem> getImportantDesc();

    @Query("UPDATE ToDoItem SET taskName = :mTaskName, category = :mCategory, dueDate = :mDueDate WHERE id = :mid")
    void updateToDo(int mid, String mTaskName, String mCategory, String mDueDate);

    @Query("UPDATE ToDoItem SET doneFlag = :flag WHERE id = :mid")
    void updateDoneFlag(int mid, int flag);

    @Query("UPDATE ToDoItem SET importantFlag = :flag WHERE id = :mid")
    void updateImportantFlag(int mid, int flag);

    @Delete
    void deleteToDo(ToDoItem toDoItem);
}
