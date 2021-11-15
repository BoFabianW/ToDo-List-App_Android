package de.werner.todo_list.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import de.werner.todo_list.R;
import de.werner.todo_list.controller.MainActivityListener;

public class MainActivity extends AppCompatActivity {

    MainActivityListener mainActivityListener;

    public ListView lvItems;
    public FloatingActionButton btnFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvItems = findViewById(R.id.lvItems);
        btnFab  = findViewById(R.id.btnFab);

        // Neuen Controller erstellen.
        mainActivityListener = new MainActivityListener(this);

        // Listener setzen.
        lvItems.setOnItemClickListener(mainActivityListener);
        registerForContextMenu(lvItems);

        btnFab.setOnClickListener(mainActivityListener);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        mainActivityListener.onCreateContextMenu(menu, v, menuInfo);
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        return mainActivityListener.onContextItemSelected(item);
    }
}