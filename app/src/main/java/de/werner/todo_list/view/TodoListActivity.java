package de.werner.todo_list.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import de.werner.todo_list.R;
import de.werner.todo_list.controller.TodoListListener;
import de.werner.todo_list.datenbank.Datenbank;

public class TodoListActivity extends AppCompatActivity {

    TodoListListener todoListListener;

    public ArrayAdapter adapter;
    public ListView lvMyLists;
    public List<String> todoArrayList = new ArrayList<>();
    public StringBuilder sbTodoLists;
    public String list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);

        lvMyLists = findViewById(R.id.lvMyLists);
        registerForContextMenu(lvMyLists);

        todoListListener = new TodoListListener(this);

        lvMyLists.setOnItemClickListener(todoListListener);

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, todoArrayList);
        lvMyLists.setAdapter(adapter);

        loadTodoArrayList();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return TodoListListener.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        todoListListener.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        return todoListListener.onContextItemSelected(item);
    }

    public void loadTodoArrayList() {

        sbTodoLists = new StringBuilder();

        Intent loadList = getIntent();
        list = loadList.getStringExtra("myList");

        if (list.length() > 0) {

            list = list.substring(0, list.length() -1).toUpperCase();

            String[] tmp = list.split(",");
            todoArrayList.addAll(Arrays.asList(tmp));

            adapter.notifyDataSetChanged();
        }
    }

    public void savePreferences() {

        sbTodoLists = new StringBuilder();

        for (String s : todoArrayList) {
            sbTodoLists.append(s).append(",");
        }

        MainActivity.spTodoLists = getSharedPreferences("todoLists", MODE_PRIVATE);

        SharedPreferences.Editor editor = MainActivity.spTodoLists.edit();
        editor.putString("list", sbTodoLists.toString());
        editor.apply();
    }

    public void tableDelete(int position) {

        Datenbank db = new Datenbank(TodoListActivity.this, todoArrayList.get(position));
        db.deleteTableComplete();

        todoArrayList.remove(position);
        adapter.notifyDataSetChanged();

        savePreferences();

        if (todoArrayList.size() == 0) MainActivity.aktuelleTabelle = null;
    }

    public void loadList(int position) {
        MainActivity.aktuelleTabelle = todoArrayList.get(position);
        MainActivity.btnFab.setVisibility(View.VISIBLE);
        close();
    }

    public void close() {
        finish();
    }
}