package com.endlessmilkyway.mytodo;

public class EnvSettingData {
    private String settingName;
    private boolean flag;

    public EnvSettingData(String settingName, boolean flag) {
        this.settingName = settingName;
        this.flag = flag;
    }

    public String getSettingName() {
        return settingName;
    }

    public boolean getFlag() {
        return flag;
    }

    public void setFlag(boolean mFlag) {
        flag = mFlag;
    }
}
