package de.werner.todo_list.controller;

import android.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
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
    List<Item> itemList;
    ItemListAdapter itemListAdapter;

    public MainActivityListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;

        itemList = new ArrayList<>();
        itemListAdapter = new ItemListAdapter(mainActivity, itemList);
        mainActivity.lvItems.setAdapter(itemListAdapter);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btnFab:
                showCreateDialog();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

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

    private void showCreateDialog() {
        View editDialog = LayoutInflater.from(mainActivity).inflate(R.layout.new_item, null);

        final EditText etNewItem = editDialog.findViewById(R.id.etNewItem);

        AlertDialog.Builder alert = new AlertDialog.Builder(mainActivity);
        alert.setView(editDialog)
                .setPositiveButton("Bestätigen", (dialog, which) -> {

                    Item item = new Item();
                    item.setTitel(etNewItem.getText().toString());

                    itemList.add(item);

                    itemListAdapter.notifyDataSetChanged();
                })
                .setNegativeButton("Abbrechen", null)
                .show();
    }
}
