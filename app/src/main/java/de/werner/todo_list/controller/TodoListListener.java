package de.werner.todo_list.controller;

import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import de.werner.todo_list.R;
import de.werner.todo_list.view.MainActivity;
import de.werner.todo_list.view.TodoListActivity;

public class TodoListListener implements AdapterView.OnItemClickListener {

    public static TodoListActivity todoListActivity;

    public TodoListListener(TodoListActivity todoListActivity) {
        TodoListListener.todoListActivity = todoListActivity;
    }

    public static boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id==android.R.id.home) {
            todoListActivity.close();
        }

        return true;
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        todoListActivity.getMenuInflater().inflate(R.menu.delete_list, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        // Position in der Liste
        int listPosition = info.position;

        // Eintrag entfernen
        if (item.getItemId() == R.id.itemDelete) {
            todoListActivity.tableDelete(listPosition);
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        todoListActivity.loadList(position);
    }
}
