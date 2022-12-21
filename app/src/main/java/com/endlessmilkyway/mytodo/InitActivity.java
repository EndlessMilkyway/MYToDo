package com.endlessmilkyway.mytodo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.Objects;

public class InitActivity extends AppCompatActivity {
    private SharedPreferences pref;
    private SharedPreferences prefEnv;
    private MaterialTextView initTextView;
    private TextInputLayout initInputLayout;
    private TextInputEditText nickInput;
    private MaterialButton submitButton;

    int DELAY_TIME = 2000;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        initTextView = (MaterialTextView) findViewById(R.id.initTextView);
        initInputLayout = (TextInputLayout) findViewById(R.id.initInputLayout);
        nickInput = (TextInputEditText) findViewById(R.id.nickInput);
        submitButton = (MaterialButton) findViewById(R.id.submitButton);

        pref = getSharedPreferences("isFirst", MODE_PRIVATE);
        prefEnv = getSharedPreferences("setting", Context.MODE_PRIVATE);

        nickInput.setOnTouchListener((view, motionEvent) -> {
            submitButton.setVisibility(View.VISIBLE);
            return false;
        });


        submitButton.setOnClickListener(view -> {
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isFirst", true);
            editor.putString("nickname", Objects.requireNonNull(nickInput.getText()).toString());
            editor.apply();

            SharedPreferences.Editor settingEditor = prefEnv.edit();
            settingEditor.putBoolean("addNewTaskAtTop", false);
            settingEditor.putBoolean("moveImportantToTop", false);
            settingEditor.putBoolean("checkBeforeDelete", true);

            nickInput.setVisibility(View.GONE);
            initInputLayout.setVisibility(View.GONE);
            submitButton.setVisibility(View.GONE);
            initTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            initTextView.setText("닉네임 설정이 완료되었습니다!\n환영합니다");

            transferToMain(DELAY_TIME);
        });
    }

    private void transferToMain(int sec) {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }, sec);
    }
}