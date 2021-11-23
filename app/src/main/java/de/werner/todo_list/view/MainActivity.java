package de.werner.todo_list.view;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Locale;
import de.werner.todo_list.R;
import de.werner.todo_list.controller.MainActivityListener;
import de.werner.todo_list.datenbank.Datenbank;
import de.werner.todo_list.model.Item;

public class MainActivity extends AppCompatActivity {

    MainActivityListener mainActivityListener;

    public EditText etNewItem;
    public ListView lvItems;
    public FloatingActionButton btnFab;
    public ActivityResultLauncher<Intent> activityResultLauncher;
    public AlertDialog startDialog;
    public Datenbank db = new Datenbank(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvItems = findViewById(R.id.lvItems);
        btnFab = findViewById(R.id.btnFab);

        // Neuen Controller erstellen.
        mainActivityListener = new MainActivityListener(this);

        // Listener setzen.
        lvItems.setOnItemClickListener(mainActivityListener);
        registerForContextMenu(lvItems);

        btnFab.setOnClickListener(mainActivityListener);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

            if (result.getData() != null) {
                ArrayList<String> resultString;
                resultString = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                etNewItem.setText(resultString.get(0).toUpperCase());
                setItem(etNewItem.getText().toString());
                startDialog.dismiss();
            }
        });

        try {
            load();
        } catch (Exception e) {
            Log.d("Datenbank: ", "Tabelle nicht vorhanden.");
        }
    }

    @Override
    protected void onStop() {
        setItemDB();
        super.onStop();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        mainActivityListener.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        return mainActivityListener.onContextItemSelected(item);
    }

    public void showDialogTextToSpeak() {

        View editDialog = LayoutInflater.from(this).inflate(R.layout.new_item, null);

        etNewItem = editDialog.findViewById(R.id.etNewItem);
        final ImageView ivNewItemSpeak = editDialog.findViewById(R.id.ivNewItemSpeak);

        ivNewItemSpeak.setOnClickListener(mainActivityListener);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(editDialog)
                .setPositiveButton("Bestätigen", (dialogInterface, i) -> setItem(etNewItem.getText().toString().toUpperCase()))
                .setNegativeButton("Abbrechen", null);

        startDialog = alert.create();
        startDialog.show();
    }

    public void setItem(String itemText) {

        Item item = new Item(itemText);
        mainActivityListener.itemList.add(item);
        mainActivityListener.itemListAdapter.notifyDataSetChanged();

        setItemDB();
    }

    public void setItemDB() {

        db.deleteTable();

        for (Item item : mainActivityListener.itemList) {
            db.createItem(item);
        }
    }

    public void load() {

        mainActivityListener.itemList.clear();
        mainActivityListener.itemList.addAll(db.loadTable());
        mainActivityListener.itemListAdapter.notifyDataSetChanged();
    }

    public void itemDelete(int position) {

        if (Datenbank.tableName != null) db.deleteItem(mainActivityListener.itemList.get(position));

        mainActivityListener.itemList.remove(position);
        mainActivityListener.itemListAdapter.notifyDataSetChanged();
    }

    public void startTextToSpeak() {
        int RESULT_CODE_TEXT_TO_SPEAK = 1001;

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Und los ...");

        setResult(RESULT_CODE_TEXT_TO_SPEAK, intent);
        activityResultLauncher.launch(intent);
    }
}