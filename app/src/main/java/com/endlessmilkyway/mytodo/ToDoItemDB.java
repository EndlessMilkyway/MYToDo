package com.endlessmilkyway.mytodo;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {ToDoItem.class}, version = 2, exportSchema = false)
public abstract class ToDoItemDB extends RoomDatabase {
    private static ToDoItemDB database = null;
    private static String INSTANCE_NAME = "database";

    public abstract ToDoItemDao toDoItemDao();

    public static ToDoItemDB getInstance(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context.getApplicationContext(), ToDoItemDB.class, INSTANCE_NAME)
                    .addMigrations(MIGRATION_1_2).allowMainThreadQueries().build();
            //database = Room.databaseBuilder(context.getApplicationContext(), ToDoItemDB.class, INSTANCE_NAME).build();
        }

        return database;
    }

    public static void destroyDatabase() {
        database = null;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE ToDoItem ADD COLUMN dueDate TEXT");
        }
    };
}
