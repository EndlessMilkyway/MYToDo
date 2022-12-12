package com.endlessmilkyway.mytodo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {
    SharedPreferences pref;

    int SHOW_DURATION = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Intent intent = inspectFirstExecution();

        transferToMain(SHOW_DURATION, intent);
    }

    private Intent inspectFirstExecution() {
        pref = getSharedPreferences("isFirst", Activity.MODE_PRIVATE);
        boolean first = pref.getBoolean("isFirst", false);

        if (!first) {
            return new Intent(getApplicationContext(), InitActivity.class);
        }
        return new Intent(getApplicationContext(), MainActivity.class);
    }

    private void transferToMain(int sec, Intent intent) {
        new Handler().postDelayed(() -> {
            startActivity(intent);
            finish();
        }, sec);
    }
}