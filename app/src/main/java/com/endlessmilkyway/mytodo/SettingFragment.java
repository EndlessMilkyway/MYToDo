package com.endlessmilkyway.mytodo;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Objects;

public class SettingFragment extends Fragment {
    private TextView userInfoItem;
    private RecyclerView settingGeneralMenu;
    private TextView currentVersion;
    private Context mContext;

    private ArrayList<EnvSettingData> envSettingData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_setting, container, false);
        mContext = getActivity().getApplicationContext();
        SharedPreferences prefEnv = mContext.getSharedPreferences("setting", Context.MODE_PRIVATE);

        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle("설정");

        userInfoItem = rootView.findViewById(R.id.userInfoItem);
        settingGeneralMenu = rootView.findViewById(R.id.settingGeneralMenu);
        currentVersion = rootView.findViewById(R.id.currentVersion);

        initCondition(prefEnv);

        userInfoItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = mContext.getSharedPreferences("isFirst", Context.MODE_PRIVATE);

                final Dialog dialog = new Dialog(getActivity());
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_nickname);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.show();

                TextInputEditText nickNameInput = dialog.findViewById(R.id.nickNameInput);
                MaterialButton nickCancelButton = dialog.findViewById(R.id.nickCancelButton);
                MaterialButton nickEditButton = dialog.findViewById(R.id.nickEditButton);

                nickNameInput.setText(pref.getString("nickname", null));

                nickCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                nickEditButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("nickname", Objects.requireNonNull(nickNameInput.getText()).toString());
                        editor.apply();
                        dialog.dismiss();
                    }
                });
            }
        });

        SettingItemAdapter adapter = new SettingItemAdapter(envSettingData);

        settingGeneralMenu.setLayoutManager(new LinearLayoutManager(mContext));
        settingGeneralMenu.setAdapter(adapter);

        adapter.setOnItemClickListener(new SettingItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String name, boolean flag) {
                if (Objects.equals(name, "새로운 작업을 상단에 추가")) {
                    SharedPreferences.Editor editor = prefEnv.edit();
                    editor.putBoolean("addNewTaskAtTop", flag);
                    editor.apply();
                }

                if (Objects.equals(name, "중요한 일을 상단으로 이동")) {
                    SharedPreferences.Editor editor = prefEnv.edit();
                    editor.putBoolean("moveImportantToTop", flag);
                    editor.apply();
                }

                if (Objects.equals(name, "삭제하기 전에 확인")) {
                    SharedPreferences.Editor editor = prefEnv.edit();
                    editor.putBoolean("checkBeforeDelete", flag);
                    editor.apply();
                }
            }
        });

        currentVersion.setText(getVersion(mContext));

        return rootView;
    }

    private String getVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "[ERROR] Application can't recognize version";
        }
    }

    private void initCondition(SharedPreferences pref) {
        envSettingData = new ArrayList<>();

        envSettingData.add(new EnvSettingData("새로운 작업을 상단에 추가", pref.getBoolean("addNewTaskAtTop", false)));
        envSettingData.add(new EnvSettingData("중요한 일을 상단으로 이동", pref.getBoolean("moveImportantToTop", false)));
        envSettingData.add(new EnvSettingData("삭제하기 전에 확인", pref.getBoolean("checkBeforeDelete", false)));
    }
}