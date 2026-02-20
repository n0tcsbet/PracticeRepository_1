package com.example.myapplicationetc;

import android.annotation.SuppressLint;

import android.content.Intent;

import android.os.Bundle;

import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView;

import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;
    ListView listViewTasks;
    ArrayAdapter<String> adapter;
    List<Task> tasks = new ArrayList<>();
    int selectedTaskId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
                });

        ListView screen = findViewById(R.id.lvScreens);

        String[] screens ={
                "Открыть профиль",
                "Открыть экран с расчетом",
                "Открыть экран настроек"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                screens
        );

        screen.setAdapter(adapter);

        screen.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                } else if (position == 1) {
                    Intent intent = new Intent(MainActivity.this, CalcActivity.class);
                    startActivity(intent);
                } else if (position == 2) {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }
            }
        });
        dbHelper = new DatabaseHelper(this);
        listViewTasks = findViewById(R.id.listViewTasks);

        findViewById(R.id.btnAdd).setOnClickListener(v -> {
            EditText etTitle = findViewById(R.id.etTitle);
            EditText etDesc = findViewById(R.id.etDesc);
            if (dbHelper.addTask(etTitle.getText().toString(), etDesc.getText().toString())) {
                Toast.makeText(this, "Задача добавлена!", Toast.LENGTH_SHORT).show();
                refreshList();
                etTitle.setText(""); etDesc.setText("");
            }
        });

        findViewById(R.id.btnRefresh).setOnClickListener(v -> refreshList());

        listViewTasks.setOnItemClickListener((parent, view, position, id) -> {
            selectedTaskId = tasks.get(position).getId();
            Toast.makeText(this, "Выбрана задача ID: " + selectedTaskId, Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnDeleteSelected).setOnClickListener(v -> {
            if (selectedTaskId != -1 && dbHelper.deleteTask(selectedTaskId)) {
                Toast.makeText(this, "Задача удалена!", Toast.LENGTH_SHORT).show();
                refreshList();
                selectedTaskId = -1;
            }
        });
        refreshList();
    }
    private void refreshList() {
        tasks.clear();
        tasks.addAll(dbHelper.getAllTasks());
        List<String> taskTitles = new ArrayList<>();
        for (Task task : tasks) {
            taskTitles.add(task.getTitle() + " (" + task.getDescription() + ")");
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,taskTitles);
        listViewTasks.setAdapter(adapter);
    }
}

