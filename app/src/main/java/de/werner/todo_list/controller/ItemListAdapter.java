package de.werner.todo_list.controller;

import static de.werner.todo_list.R.drawable.checked;
import static de.werner.todo_list.R.drawable.unchecked;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;
import de.werner.todo_list.R;
import de.werner.todo_list.model.Item;

public class ItemListAdapter extends ArrayAdapter<Item> {

    private final Context context;
    private final List<Item> itemList;
    private final LayoutInflater layoutInflater;

    // Konstruktor.
    public ItemListAdapter(@NonNull Context context, List<Item> itemList) {
        super(context, R.layout.custom_item, itemList);
        this.context = context;
        this.itemList = itemList;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view;

        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.custom_item, parent, false);
        } else {
            view = convertView;
        }

        TextView tvItemTitel = view.findViewById(R.id.tvTodoListTitel);
        ImageView ivItemImage = view.findViewById(R.id.ivItemIcon);

        Item item = itemList.get(position);
        tvItemTitel.setText(item.getTitel());

        // Eigenschaft in Objekt 'item' prüfen und entsprechendes Image setzen.
        if (item.isDone()) {
            ivItemImage.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), checked));
        } else {
            ivItemImage.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), unchecked));
        }

        return view;
    }
}
