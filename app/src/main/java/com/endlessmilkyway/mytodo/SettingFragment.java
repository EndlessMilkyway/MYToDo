package com.endlessmilkyway.mytodo;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class SettingFragment extends Fragment {
    private TextView settingUserInfo;
    private RecyclerView settingGeneralMenu;
    private TextView currentVersion;
    private Context mContext;

    private ArrayList<EnvSettingData> envSettingData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_setting, container, false);
        mContext = getActivity().getApplicationContext();

        settingUserInfo = rootView.findViewById(R.id.settingUserInfo);
        settingGeneralMenu = rootView.findViewById(R.id.settingGeneralMenu);
        currentVersion = rootView.findViewById(R.id.currentVersion);

        initCondition();

        /*settingUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/

        settingGeneralMenu.setLayoutManager(new LinearLayoutManager(mContext));
        settingGeneralMenu.setAdapter(new SettingItemAdapter(envSettingData));

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

    private void initCondition() {
        envSettingData = new ArrayList<>();

        envSettingData.add(new EnvSettingData("새로운 작업을 상단에 추가", false));
        envSettingData.add(new EnvSettingData("중요한 일을 상단으로 이동", false));
        envSettingData.add(new EnvSettingData("삭제하기 전에 확인", true));
    }
}