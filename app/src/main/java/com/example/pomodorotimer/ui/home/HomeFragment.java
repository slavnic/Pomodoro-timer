package com.example.pomodorotimer.ui.home;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pomodorotimer.R;
import com.example.pomodorotimer.data.HandlerDB;
import com.example.pomodorotimer.model.Todo;
import com.example.pomodorotimer.util.HandlerColor;
import com.example.pomodorotimer.util.HandlerCountDownTime;
import com.example.pomodorotimer.util.HandlerProgressBar;
import com.example.pomodorotimer.util.HandlerSharedPreferences;
import com.example.pomodorotimer.util.HandlerTodo;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements
        HandlerSharedPreferences.OnTimeChangeListener, HandlerTodo.OnTodoClickListener {

    private static final String TAG = "HomeFragment";
    private View rootView;
    private HandlerCountDownTime handlerCountDownTime;
    private HandlerSharedPreferences handlerSharedPreferences;

    // TODO related fields
    private RecyclerView recyclerTodos;
    private HandlerTodo todoAdapter;
    private ImageView fabAddTodo;
    private TextView textEmptyTodos;
    private List<Todo> todoList;
    private HandlerDB handlerDB;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        Log.d(TAG, "onCreateView: ");

        /* SET VIEW */
        HandlerColor.setView(rootView);

        try {
            if (getActivity() != null) {
                HandlerDB.setContext(getActivity().getApplicationContext());
            } else if (getContext() != null) {
                HandlerDB.setContext(getContext().getApplicationContext());
            } else {
                Log.e(TAG, "Both activity and context are null, cannot initialize HandlerDB");
                return rootView;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing HandlerDB context", e);
            return rootView;
        }

        try {
            manageProgressBar(rootView);
        } catch (Exception e) {
            Log.e(TAG, "Error managing progress bar", e);
            e.printStackTrace();
        }

        try {
            manageCountDownTime(rootView);
        } catch (Exception e) {
            Log.e(TAG, "Error managing countdown time", e);
            e.printStackTrace();
        }

        // Initialize TODO functionality
        try {
            setupTodoComponents();
        } catch (Exception e) {
            Log.e(TAG, "Error setting up TODO components", e);
            e.printStackTrace();
        }

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            // Get HandlerCountDownTime instance
            handlerCountDownTime = HandlerCountDownTime.getInstance();
            if (handlerCountDownTime != null) {
                Log.d(TAG, "HandlerCountDownTime instance obtained");
            } else {
                Log.e(TAG, "HandlerCountDownTime instance is null");
            }

            handlerSharedPreferences = HandlerSharedPreferences.getInstance();
            if (handlerSharedPreferences != null) {
                handlerSharedPreferences.addOnTimeChangeListener(this);
                Log.d(TAG, "Registered for time change notifications");
            } else {
                Log.e(TAG, "HandlerSharedPreferences instance is null");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error registering listeners", e);
            e.printStackTrace();
        }
    }

    @Override
    public void onWorkTimeChanged(long workTimeMs) {
        Log.d(TAG, "Work time changed to: " + workTimeMs + " ms");
        try {
            if (handlerCountDownTime != null && !handlerCountDownTime.isRunning()) {
                Log.d(TAG, "Timer not running, recreating countdown with new time");
                // Reinitialize the countdown with new time
                if (rootView != null) {
                    HandlerCountDownTime.setCountDown(rootView);
                }
            } else {
                Log.d(TAG, "Timer is running, changes will apply after current session");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling work time change", e);
            e.printStackTrace();
        }
    }

    @Override
    public void onBreakTimeChanged(long breakTimeMs) {
        Log.d(TAG, "Break time changed to: " + breakTimeMs + " ms");
        try {
            if (handlerCountDownTime != null && handlerCountDownTime.isInBreakMode()) {
                Log.d(TAG, "Currently in break mode, updating timer with new break time");
                handlerCountDownTime.restartWithNewBreakTime(breakTimeMs);
            } else {
                Log.d(TAG, "Not in break mode, changes will apply when break starts");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling break time change", e);
            e.printStackTrace();
        }
    }

    @Override
    public void onLongBreakTimeChanged(long longBreakTimeMs) {
        Log.d(TAG, "Long break time changed to: " + longBreakTimeMs + " ms");
        try {
            if (handlerCountDownTime != null) {
                Log.d(TAG, "HandlerCountDownTime is not null");
                Log.d(TAG, "Current timer mode: " + handlerCountDownTime.getCurrentMode());
                Log.d(TAG, "Is in long break mode: " + handlerCountDownTime.isInLongBreakMode());
                Log.d(TAG, "Is timer running: " + handlerCountDownTime.isRunning());

                if (handlerCountDownTime.isInLongBreakMode()) {
                    long oldLongBreakTime = longBreakTimeMs;
                    try {
                        long remaining = handlerCountDownTime.getRemainingTime();
                        oldLongBreakTime = remaining;
                    } catch (Exception e) {
                        Log.e(TAG, "Error getting remaining time for long break", e);
                    }
                    handlerCountDownTime.restartWithNewLongBreakTime(longBreakTimeMs, oldLongBreakTime);
                } else {
                    Log.d(TAG, "Not in long break mode, will not update timer");
                }
            } else {
                Log.e(TAG, "HandlerCountDownTime is null!");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling long break time change", e);
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        try {
            // Unregister listeners

            if (handlerSharedPreferences != null) {
                handlerSharedPreferences.removeOnTimeChangeListener(this);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error unregistering listeners", e);
            e.printStackTrace();
        }

        super.onDestroyView();
        rootView = null;
        handlerCountDownTime = null;
        handlerSharedPreferences = null;
        Log.d(TAG, "onDestroyView: ");
    }

    private void manageCountDownTime(View root) {
        try {
            HandlerCountDownTime.setCountDown(root);
        } catch (Exception e) {
            Log.e(TAG, "Error setting countdown", e);
            e.printStackTrace();
        }
    }

    private void manageProgressBar(View root) throws Exception {
        try {
            HandlerProgressBar.setView(root);
            Log.d(TAG, "Progress bar initialized with current daily progress");
        } catch (Exception e) {
            Log.e(TAG, "Error setting progress bar view", e);
            throw e;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        try {
            if (handlerCountDownTime != null) {
                handlerCountDownTime.goOnPause();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling pause", e);
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onTodoClick(Todo todo) {
        // Handle todo click - show details or edit
        showEditTodoDialog(todo);
    }

    @Override
    public void onTodoEdit(Todo todo) {
        showEditTodoDialog(todo);
    }

    @Override
    public void onTodoDelete(Todo todo) {
        showDeleteConfirmationDialog(todo);
    }

    @Override
    public void onTodoToggle(Todo todo) {
        try {
            // Update todo in database
            handlerDB = HandlerDB.getInstance();
            handlerDB.updateTodo(todo);

            // Update UI
            todoAdapter.notifyDataSetChanged();

            String message = todo.isCompleted() ? "TODO completed!" : "TODO marked as incomplete";
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error toggling TODO", e);
            Toast.makeText(getContext(), "Error updating TODO", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddTodoDialog() {
        showTodoDialog(null, "Add TODO");
    }

    private void showEditTodoDialog(Todo todo) {
        showTodoDialog(todo, "Edit TODO");
    }

    private void showTodoDialog(Todo existingTodo, String title) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_todo, null);

        TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
        TextInputEditText editTitle = dialogView.findViewById(R.id.edit_todo_title);
        TextInputEditText editDescription = dialogView.findViewById(R.id.edit_todo_description);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnSave = dialogView.findViewById(R.id.btn_save);

        dialogTitle.setText(title);

        // Pre-fill data if editing
        if (existingTodo != null) {
            editTitle.setText(existingTodo.getTitle());
            editDescription.setText(existingTodo.getDescription());
        }

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String todoTitle = editTitle.getText().toString().trim();
            String description = editDescription.getText().toString().trim();

            if (todoTitle.isEmpty()) {
                Toast.makeText(getContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                if (existingTodo == null) {
                    // Add new TODO
                    addTodo(todoTitle, description);
                } else {
                    // Update existing TODO
                    updateTodo(existingTodo, todoTitle, description);
                }
                dialog.dismiss();
            } catch (Exception e) {
                Log.e(TAG, "Error saving TODO", e);
                Toast.makeText(getContext(), "Error saving TODO", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void addTodo(String title, String description) {
        try {
            Todo newTodo = new Todo(title, description);

            // Save to database
            handlerDB = HandlerDB.getInstance();
            long todoId = handlerDB.addTodo(newTodo);
            newTodo.setId(todoId);

            // Add to list and update UI
            todoList.add(0, newTodo); // Add to top
            todoAdapter.notifyItemInserted(0);
            updateEmptyTodosView();

            Toast.makeText(getContext(), "TODO added successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error adding TODO", e);
            Toast.makeText(getContext(), "Error adding TODO", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTodo(Todo todo, String title, String description) {
        try {
            todo.setTitle(title);
            todo.setDescription(description);

            // Update in database
            handlerDB = HandlerDB.getInstance();
            handlerDB.updateTodo(todo);

            // Update UI
            todoAdapter.notifyDataSetChanged();

            Toast.makeText(getContext(), "TODO updated successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error updating TODO", e);
            Toast.makeText(getContext(), "Error updating TODO", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmationDialog(Todo todo) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete TODO")
                .setMessage("Are you sure you want to delete this TODO?\n\n" + todo.getTitle())
                .setPositiveButton("Delete", (dialog, which) -> deleteTodo(todo))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteTodo(Todo todo) {
        try {
            // Remove from database
            handlerDB = HandlerDB.getInstance();
            handlerDB.deleteTodo(todo.getId());

            // Remove from list and update UI
            int position = todoList.indexOf(todo);
            if (position != -1) {
                todoList.remove(position);
                todoAdapter.notifyItemRemoved(position);
                updateEmptyTodosView();
            }

            Toast.makeText(getContext(), "TODO deleted successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error deleting TODO", e);
            Toast.makeText(getContext(), "Error deleting TODO", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadTodosFromDatabase() {
        try {
            handlerDB = HandlerDB.getInstance();
            List<Todo> todos = handlerDB.getAllTodos();

            todoList.clear();
            todoList.addAll(todos);
            todoAdapter.notifyDataSetChanged();
            updateEmptyTodosView();

            Log.d(TAG, "Loaded " + todos.size() + " TODOs from database");
        } catch (Exception e) {
            Log.e(TAG, "Error loading TODOs from database", e);
        }
    }

    private void setupTodoComponents() {
        try {
            // Initialize TODO list and adapter
            todoList = new ArrayList<>();
            todoAdapter = new HandlerTodo(todoList, this);

            // Setup RecyclerView
            recyclerTodos = rootView.findViewById(R.id.recycler_todos);
            recyclerTodos.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerTodos.setAdapter(todoAdapter);

            // Handle empty TODO list view
            textEmptyTodos = rootView.findViewById(R.id.text_empty_todos);

            // Setup FAB click listener
            fabAddTodo = rootView.findViewById(R.id.fab_add_todo);
            fabAddTodo.setOnClickListener(v -> showAddTodoDialog());

            // Load existing TODOs from database
            loadTodosFromDatabase();

        } catch (Exception e) {
            Log.e(TAG, "Error setting up TODO components", e);
            e.printStackTrace();
        }
    }

    private void updateEmptyTodosView() {
        try {
            if (todoList.isEmpty()) {
                textEmptyTodos.setVisibility(View.VISIBLE);
                recyclerTodos.setVisibility(View.GONE);
            } else {
                textEmptyTodos.setVisibility(View.GONE);
                recyclerTodos.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating empty TODOs view", e);
            e.printStackTrace();
        }
    }
}
