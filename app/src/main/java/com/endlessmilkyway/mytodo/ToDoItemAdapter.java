package com.endlessmilkyway.mytodo;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.List;
import android.os.Handler;

public class ToDoItemAdapter extends RecyclerView.Adapter<ToDoItemAdapter.ViewHolder> {
    private List<ToDoItem> mData;
    private Activity context;
    private ToDoItemDB database;

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    private OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnLongItemClickListener {
        void onLongItemClick(ToDoItem data);
    }

    private OnLongItemClickListener onLongItemClickListener = null;

    public void setOnLongItemClickListener(OnLongItemClickListener listener) {
        this.onLongItemClickListener = listener;
    }

    public ToDoItemAdapter(Activity context, List<ToDoItem> list) {
        this.context = context;
        this.mData = list;
        database = ToDoItemDB.getInstance(context);
        notifyDataSetChanged();
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView taskName;
        TextView category;
        MaterialCheckBox doneCheck;
        MaterialCheckBox importantCheck;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // 뷰 객체에 대한 참조
            taskName = itemView.findViewById(R.id.taskName);
            category = itemView.findViewById(R.id.category);
            doneCheck = itemView.findViewById(R.id.taskDoneCheck);
            importantCheck = itemView.findViewById(R.id.taskImportantCheck);
        }
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public ToDoItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item, parent, false);

        return new ToDoItemAdapter.ViewHolder(view);
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ToDoItem data = mData.get(position);
        database = ToDoItemDB.getInstance(context);

        holder.doneCheck.setOnCheckedChangeListener(null);
        holder.doneCheck.setCheckedState(convertIntToState(data.getDoneFlag()));

        holder.doneCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                data.setDoneFlag(convertBooleanToInt(b));
                database.toDoItemDao().updateDoneFlag(data.getId(), holder.doneCheck.getCheckedState());
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(holder.getAdapterPosition());
                }

            }
        });

        holder.importantCheck.setOnCheckedChangeListener(null);
        holder.importantCheck.setCheckedState(convertIntToState(data.getImportantFlag()));
        holder.importantCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                data.setImportantFlag(convertBooleanToInt(b));
                database.toDoItemDao().updateImportantFlag(data.getId(), holder.importantCheck.getCheckedState());
                notifyItemChanged(holder.getAdapterPosition());
            }
        });

        holder.taskName.setText(data.getTaskName());
        holder.category.setText(data.getCategory());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onLongItemClickListener.onLongItemClick(data);
                }
                return true;
            }
        });
    }

    // getItemCount() - 전체 데이터 개수 리턴.
    @Override
    public int getItemCount() {
        return mData.size();
    }

    private int convertIntToState(int input) {
        if (input == 1) return MaterialCheckBox.STATE_CHECKED;

        return MaterialCheckBox.STATE_UNCHECKED;
    }

    private int convertBooleanToInt(boolean input) {
        if (input) return 1;

        return 0;
    }
}
