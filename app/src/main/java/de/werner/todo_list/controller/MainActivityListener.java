package de.werner.todo_list.controller;

import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import java.util.ArrayList;
import java.util.List;
import de.werner.todo_list.R;
import de.werner.todo_list.model.Item;
import de.werner.todo_list.view.MainActivity;

public class MainActivityListener implements View.OnClickListener, AdapterView.OnItemClickListener {

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
            mainActivity.showCreateDialog();
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
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        mainActivity.getMenuInflater().inflate(R.menu.delete, menu);
    }


    // Contextmenü-Aktionen
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        // Position in der Liste
        int listPosition = info.position;

        // Eintrag entfernen
        if (item.getItemId() == R.id.itemDelete) {
            itemList.remove(listPosition);
            itemListAdapter.notifyDataSetChanged();
        }

        return true;
    }
}
