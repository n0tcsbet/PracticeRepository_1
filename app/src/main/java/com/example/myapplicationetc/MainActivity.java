package com.example.myapplicationetc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    ListView listViewTasks;
    ArrayAdapter<String> tasksAdapter;
    List<Task> tasks = new ArrayList<>();
    int selectedTaskId = -1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nav);


        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        ListView lvScreens = findViewById(R.id.lvScreens);
        String[] screens = {
                "Открыть профиль",
                "Открыть экран с расчётом",
                "Каталог картинок",
                "Медиа"
        };
        ArrayAdapter<String> navAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, screens);
        lvScreens.setAdapter(navAdapter);

        lvScreens.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                } else if (position == 1) {
                    startActivity(new Intent(MainActivity.this, CalcActivity.class));
                } else if (position == 2) {
                    startActivity(new Intent(MainActivity.this, GalleryFragment.class));
                } else if (position == 3) {
                    startActivity(new Intent(MainActivity.this, ContentFragment.class));
                }
            }
        });

        dbHelper = new DatabaseHelper(this);
        listViewTasks = findViewById(R.id.listViewTasks);

        Button btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> addTask());

        findViewById(R.id.btnRefresh).setOnClickListener(v -> refreshTasksList());

        EditText etSearch = findViewById(R.id.etSearch);
        findViewById(R.id.btnSearch).setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                try {
                    tasks.clear();
                    tasks.addAll(dbHelper.searchTasks(query));
                    refreshTasksList();
                    Toast.makeText(this, "🔍 Найдено: " + tasks.size() + " задач", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "❌ Ошибка поиска: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                refreshTasksList();
            }
        });

        listViewTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedTaskId = tasks.get(position).getId();
                Intent intent = new Intent(MainActivity.this, DetailsFragment.class);
                intent.putExtra("TASK_ID", selectedTaskId);
                startActivityForResult(intent, 1);

                view.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                Toast.makeText(MainActivity.this, "📋 Детали задачи #" + selectedTaskId, Toast.LENGTH_SHORT).show();
            }
        });

        Button btnDelete = findViewById(R.id.btnDeleteSelected);
        btnDelete.setOnClickListener(v -> {
            if (selectedTaskId != -1) {
                try {
                    if (dbHelper.deleteTask(selectedTaskId)) {
                        Toast.makeText(this, "🗑️ Задача #" + selectedTaskId + " удалена!", Toast.LENGTH_SHORT).show();
                        refreshTasksList();
                        selectedTaskId = -1;
                    } else {
                        Toast.makeText(this, "❌ Ошибка удаления из БД", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "💥 Ошибка БД: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "⚠️ Выберите задачу кликом!", Toast.LENGTH_SHORT).show();
            }
        });

        refreshTasksList();
    }

    private void addTask() {
        try {
            EditText etTitle = findViewById(R.id.etTitle);
            EditText etDesc = findViewById(R.id.etDesc);

            String title = etTitle.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();

            if (title.length() < 3) {
                Toast.makeText(this, "⚠️ Название слишком короткое (минимум 3 символа)", Toast.LENGTH_SHORT).show();
                return;
            }
            if (desc.isEmpty()) {
                Toast.makeText(this, "⚠️ Добавьте описание!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.addTask(title, desc)) {
                Toast.makeText(this, "✅ Задача '" + title + "' добавлена!", Toast.LENGTH_SHORT).show();
                refreshTasksList();
                etTitle.setText("");
                etDesc.setText("");
            } else {
                Toast.makeText(this, "❌ Не удалось сохранить в БД", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "💥 Критическая ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void refreshTasksList() {
        try {
            tasks.clear();
            tasks.addAll(dbHelper.getAllTasks());

            List<String> displayList = new ArrayList<>();
            for (Task task : tasks) {
                displayList.add("#" + task.getId() + " " + task.getTitle() + "\n   " + task.getDescription());
            }

            tasksAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, displayList);
            listViewTasks.setAdapter(tasksAdapter);
            tasksAdapter.notifyDataSetChanged();

            Toast.makeText(this, "📊 Всего задач: " + tasks.size(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "❌ Ошибка загрузки списка: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            refreshTasksList();
            Toast.makeText(this, "🔄 Список обновлен!", Toast.LENGTH_SHORT).show();
        }
    }
}