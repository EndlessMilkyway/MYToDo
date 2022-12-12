package com.endlessmilkyway.mytodo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.endlessmilkyway.mytodo.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    HomeFragment homeFragment;
    ImportantFragment importantFragment;
    SettingFragment settingFragment;
    //private ToDoItemDB database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //database = ToDoItemDB.getInstance(this);

        /*class DBRunnable implements Runnable {
            @Override
            public void run() {  }
        }

        DBRunnable dbRunnable = new DBRunnable();
        Thread t = new Thread(dbRunnable);
        t.start();*/

        homeFragment = new HomeFragment();
        importantFragment = new ImportantFragment();
        settingFragment = new SettingFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.containers, homeFragment).commit();

        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);

        navigationView.setOnItemSelectedListener(item -> navItemCommit(item.getItemId()));
    }

    /*@Override
    protected void onDestroy() {
        super.onDestroy();
        ToDoItemDB.destroyDatabase();
    }*/

    private boolean navItemCommit(int id) {
        if (id == R.id.currentTaskMenu) {
            getSupportFragmentManager().beginTransaction().replace(R.id.containers, homeFragment).commit();
            return true;
        }
        if (id == R.id.importantTaskMenu) {
            getSupportFragmentManager().beginTransaction().replace(R.id.containers, importantFragment).commit();
            return true;
        }
        if (id == R.id.settingMenu) {
            getSupportFragmentManager().beginTransaction().replace(R.id.containers, settingFragment).commit();
            return true;
        }
        return false;
    }
}