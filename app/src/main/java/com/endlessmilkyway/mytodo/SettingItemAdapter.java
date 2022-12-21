package com.endlessmilkyway.mytodo;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;

public class SettingItemAdapter extends RecyclerView.Adapter<SettingItemAdapter.ViewHolder> {
    private ArrayList<EnvSettingData> mData;

    public interface OnItemClickListener {
        void onItemClick(String name, boolean flag);
    }

    private OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView item;
        SwitchMaterial itemSwitch;

        ViewHolder(View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.generalItemDesc);
            itemSwitch = itemView.findViewById(R.id.generalItemSwitch);
        }
    }

    SettingItemAdapter(ArrayList<EnvSettingData> list) { mData = list; }

    @Override
    public SettingItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.setting_general_item, parent, false);

        return new SettingItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SettingItemAdapter.ViewHolder holder, int position) {
        final EnvSettingData data = mData.get(position);

        holder.item.setText(data.getSettingName());
        holder.itemSwitch.setChecked(data.getFlag());

        holder.itemSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                data.setFlag(holder.itemSwitch.isChecked());

                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(holder.item.getText().toString(), holder.itemSwitch.isChecked());
                }
            }
        });
    }

    @Override
    public int getItemCount() { return mData.size(); }
}
