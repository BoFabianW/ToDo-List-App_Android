package de.werner.todo_list.controller;

import android.annotation.SuppressLint;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;
import de.werner.todo_list.R;
import de.werner.todo_list.model.Item;
import de.werner.todo_list.view.MainActivity;

public class MainActivityListener implements View.OnClickListener, AdapterView.OnItemClickListener, View.OnKeyListener {

    MainActivity mainActivity;

    public List<Item> itemList;
    public ItemListAdapter itemListAdapter;

    // Konstruktor.
    public MainActivityListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;

        itemList = new ArrayList<>();
        itemListAdapter = new ItemListAdapter(mainActivity, itemList);
        this.mainActivity.lvItems.setAdapter(itemListAdapter);
    }

    @Override
    public void onClick(View view) {


        if (view.getId() == R.id.btnFab) {
            mainActivity.showDialogTextToSpeak();
        }

        if (view.getId() == R.id.ivNewItemSpeak) {
            mainActivity.startTextToSpeak();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        // Item holen durch Index(position).
        Item item = itemList.get(position);

        item.setDone(!item.isDone());

        // Liste aktualisieren
        itemListAdapter.notifyDataSetChanged();

        mainActivity.setItemDB();
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        mainActivity.getMenuInflater().inflate(R.menu.delete, menu);
    }

    // Contextmenü-Aktionen
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        // Position in der Liste
        int listPosition = info.position;

        // Eintrag entfernen
        if (item.getItemId() == R.id.itemDelete) {
            mainActivity.itemDelete(listPosition);
        }

        return true;
    }

    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.optionItem01:
                mainActivity.startTodoListActivity();
                break;

            case R.id.optionItem02:
                mainActivity.showDialogSaveItemList();
                break;
        }
        return true;
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        mainActivity.checkTableName(mainActivity.etNewItemList.getText().toString());
        return false;
    }
}
