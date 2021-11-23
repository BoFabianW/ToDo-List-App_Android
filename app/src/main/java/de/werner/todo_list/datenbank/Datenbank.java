package de.werner.todo_list.datenbank;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import de.werner.todo_list.model.Item;

public class Datenbank extends SQLiteOpenHelper {

    // Tabellenname
    public static String tableName = "liste";

    // Konstanten für DB-Tabelle
    public static final int VERSION = 1;
    public static final String DB_NAME = "todos";
    public static final String ID_COLUMN = "ID";
    public static final String ITEM_COLUMN = "inhalt_item";
    public static final String DONE_COLUMN = "erledigt";

    public Datenbank(@Nullable Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // DB-Tabelle erstellen
        String createQuery = "CREATE TABLE " + tableName + " (" + ID_COLUMN + " INTEGER PRIMARY KEY, " + ITEM_COLUMN + " TEXT NOT NULL, " + DONE_COLUMN + " BOOLEAN)";
        sqLiteDatabase.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String dropTable = "DROP TABLE IF EXISTS " + tableName;
        sqLiteDatabase.execSQL(dropTable);
        onCreate(sqLiteDatabase);
    }

    // Neues Item in die DB schreiben.
    public Item createItem(Item item) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ITEM_COLUMN, item.getTitel());
        values.put(DONE_COLUMN, item.isDone());

        // Zugewiesenen 'long' aus der Tabelle speichern, durch Schreiben des neuen Items in der DB.
        long newID = database.insert(tableName, null, values);

        // DB schließen.
        database.close();

        return readItem(newID);
    }

    @SuppressLint("Range")
    public Item readItem(long id) {
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(
                tableName, new String[]{ID_COLUMN, ITEM_COLUMN, DONE_COLUMN}, ID_COLUMN + " = ? ", new String[]{String.valueOf(id)}, null, null, null);

        // Variable von Item erstellen mit dem Wert 'null'.
        Item item = null;

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            // Neue Instanz von Item erstellen
            item = new Item(cursor.getString(cursor.getColumnIndex(ITEM_COLUMN)));

            // Eigenschaft auf 'true' oder 'false' setzen.
            item.setDone(cursor.getString(cursor.getColumnIndex(DONE_COLUMN)).equals("1"));

            item.setId(cursor.getLong(cursor.getColumnIndex(ID_COLUMN)));

            // Cursor schließen.
            cursor.close();
        }

        // DB schließen.
        database.close();

        return item;
    }

    public void deleteItem(Item item) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(tableName, ID_COLUMN + " =?", new String[] {String.valueOf(item.getId())});
        database.close();
    }

    public void deleteTable() {
        SQLiteDatabase database = this.getWritableDatabase();
        String dropTable = "DROP TABLE IF EXISTS " + tableName;
        database.execSQL(dropTable);
        onCreate(database);
    }

    // Laden der Items aus der Datenbank.
    @SuppressLint("Range")
    public List<Item> loadTable() {
        SQLiteDatabase database = this.getReadableDatabase();

        List<Item> items = new ArrayList<>();

        Cursor cursor = database.rawQuery("SELECT * FROM " + tableName, null);

        if(cursor.moveToFirst()) {
            do {
                Item item = readItem(cursor.getLong(cursor.getColumnIndex(ID_COLUMN)));
                items.add(item);
            } while (cursor.moveToNext());
        }

        // DB und Cursor schließen.
        cursor.close();
        database.close();

        return items;
    }
}
