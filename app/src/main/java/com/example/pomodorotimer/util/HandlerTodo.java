package com.example.pomodorotimer.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pomodorotimer.R;
import com.example.pomodorotimer.model.Todo;

import java.util.List;

public class HandlerTodo extends RecyclerView.Adapter<HandlerTodo.TodoViewHolder> {

    private List<Todo> todoList;
    private OnTodoClickListener listener;

    public interface OnTodoClickListener {
        void onTodoClick(Todo todo);
        void onTodoEdit(Todo todo);
        void onTodoDelete(Todo todo);
        void onTodoToggle(Todo todo);
    }

    public HandlerTodo(List<Todo> todoList, OnTodoClickListener listener) {
        this.todoList = todoList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        Todo todo = todoList.get(position);
        holder.bind(todo);
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public void updateTodos(List<Todo> newTodos) {
        this.todoList = newTodos;
        notifyDataSetChanged();
    }

    class TodoViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView descriptionTextView;
        private CheckBox completedCheckBox;
        private ImageButton editButton;
        private ImageButton deleteButton;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.todo_title);
            descriptionTextView = itemView.findViewById(R.id.todo_description);
            completedCheckBox = itemView.findViewById(R.id.todo_checkbox);
            editButton = itemView.findViewById(R.id.btn_edit_todo);
            deleteButton = itemView.findViewById(R.id.btn_delete_todo);
        }

        public void bind(Todo todo) {
            titleTextView.setText(todo.getTitle());

            if (todo.getDescription() != null && !todo.getDescription().isEmpty()) {
                descriptionTextView.setText(todo.getDescription());
                descriptionTextView.setVisibility(View.VISIBLE);
            } else {
                descriptionTextView.setVisibility(View.GONE);
            }

            completedCheckBox.setChecked(todo.isCompleted());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTodoClick(todo);
                }
            });

            completedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                todo.setCompleted(isChecked);
                if (listener != null) {
                    listener.onTodoToggle(todo);
                }
            });

            editButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTodoEdit(todo);
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTodoDelete(todo);
                }
            });

            // Apply strikethrough if completed
            if (todo.isCompleted()) {
                titleTextView.setPaintFlags(titleTextView.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                titleTextView.setAlpha(0.6f);
                descriptionTextView.setPaintFlags(descriptionTextView.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                descriptionTextView.setAlpha(0.6f);
            } else {
                titleTextView.setPaintFlags(titleTextView.getPaintFlags() & (~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG));
                titleTextView.setAlpha(1.0f);
                descriptionTextView.setPaintFlags(descriptionTextView.getPaintFlags() & (~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG));
                descriptionTextView.setAlpha(1.0f);
            }
        }
    }
}
