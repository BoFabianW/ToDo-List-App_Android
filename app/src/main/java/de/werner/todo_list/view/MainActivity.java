package de.werner.todo_list.view;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Locale;
import de.werner.todo_list.R;
import de.werner.todo_list.controller.MainActivityListener;
import de.werner.todo_list.datenbank.Datenbank;
import de.werner.todo_list.model.Item;

public class MainActivity extends AppCompatActivity {

    MainActivityListener mainActivityListener;

    public EditText etNewItem, etNewItemList;
    public ListView lvItems;
    public static FloatingActionButton btnFab;
    public ActivityResultLauncher<Intent> activityResultLauncher;
    public AlertDialog startDialog;
    public Datenbank db;
    public static String aktuelleTabelle;
    public StringBuilder sbTodoLists;

    // SharedPreferences zum dauerhaften speichern von primitiven Datentypen.
    public static SharedPreferences spTodoLists;

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

        loadPreferences();
        checkDB();
    }

    @Override
    protected void onRestart() {
        loadPreferences();
        if (aktuelleTabelle != null) {
            loadTable(aktuelleTabelle);
        } else {
            mainActivityListener.itemList.clear();
            mainActivityListener.itemListAdapter.notifyDataSetChanged();
            btnFab.setVisibility(View.INVISIBLE);
        }
        super.onRestart();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        mainActivityListener.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        return mainActivityListener.onContextItemSelected(item);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);

        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return mainActivityListener.onOptionsItemSelected(item);
    }

    public void showDialogTextToSpeak() {

        View editDialog = LayoutInflater.from(this).inflate(R.layout.new_item, null);

        etNewItem = editDialog.findViewById(R.id.etInput);
        final ImageView ivNewItemSpeak = editDialog.findViewById(R.id.ivNewItemSpeak);

        ivNewItemSpeak.setOnClickListener(mainActivityListener);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(editDialog)
                .setPositiveButton("Bestätigen", (dialogInterface, i) -> setItem(etNewItem.getText().toString().toUpperCase()))
                .setNegativeButton("Abbrechen", null);

        startDialog = alert.create();
        startDialog.show();
    }

    public void showDialogSaveItemList() {

        View editDialog = LayoutInflater.from(this).inflate(R.layout.save_itemlist, null);

        etNewItemList = editDialog.findViewById(R.id.etInput);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(editDialog)
                .setPositiveButton("Bestätigen", (dialogInterface, i) -> newTable(etNewItemList.getText().toString()))
                .setNegativeButton("Abbrechen", null);

        startDialog = alert.create();
        startDialog.show();
    }

    public void startTodoListActivity() {
        Intent intent = new Intent(this, TodoListActivity.class);
        intent.putExtra("myList",sbTodoLists.toString());
        startActivity(intent);
    }

    /*
     Item zu ItemList hinzufügen.
     */
    public void setItem(String itemText) {

        Item item = new Item(itemText);
        mainActivityListener.itemList.add(item);
        mainActivityListener.itemListAdapter.notifyDataSetChanged();

        setItemDB();
    }

    public void setItemDB() {

        db = new Datenbank(MainActivity.this, aktuelleTabelle);
        db.deleteTable();

        for (Item item : mainActivityListener.itemList) {
            db.createItem(item);
        }
    }

    public void loadTable(String table) {

        if (aktuelleTabelle != null) {
            aktuelleTabelle = table.toLowerCase();

            db = new Datenbank(MainActivity.this, aktuelleTabelle);

            mainActivityListener.itemList.clear();
            mainActivityListener.itemList.addAll(db.loadTable());
            mainActivityListener.itemListAdapter.notifyDataSetChanged();
        }
    }

    public void newTable(String table) {

        aktuelleTabelle = table.toLowerCase();

        db = new Datenbank(MainActivity.this, aktuelleTabelle);

        try {
            db.createTable();
            btnFab.setVisibility(View.VISIBLE);
            mainActivityListener.itemList.clear();
            mainActivityListener.itemListAdapter.notifyDataSetChanged();
            sbTodoLists.append(aktuelleTabelle).append(",");
            savePreferences();
            Toast.makeText(this, "Neue Liste angelegt!", Toast.LENGTH_SHORT).show();
        } catch (SQLiteException e) {
            Toast.makeText(this, "Liste existiert bereits!", Toast.LENGTH_SHORT).show();
        }
    }

    public void itemDelete(int position) {

        db = new Datenbank(MainActivity.this, aktuelleTabelle);
        db.deleteItem(mainActivityListener.itemList.get(position));

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

    /*
     Laden der Preferences.
     */
    public void loadPreferences() {
        sbTodoLists = new StringBuilder();
        spTodoLists = getSharedPreferences("todoLists", MODE_PRIVATE);
        sbTodoLists.append(spTodoLists.getString("list", ""));
    }

    public void savePreferences() {
        spTodoLists = getSharedPreferences("todoLists", MODE_PRIVATE);

        SharedPreferences.Editor editor = spTodoLists.edit();
        editor.putString("list", sbTodoLists.toString());
        editor.apply();
    }

    public void checkDB() {
        db = new Datenbank(MainActivity.this, "");
        db.startDatabaseCreate();
    }
}