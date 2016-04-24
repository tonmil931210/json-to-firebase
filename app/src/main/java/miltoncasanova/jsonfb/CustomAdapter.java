package miltoncasanova.jsonfb;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomAdapter extends BaseAdapter implements View.OnClickListener {

    private final Context context;
    private List<DataEntry> listEntries;
    private List<String> ids;
    private Firebase rootRef;

    public CustomAdapter(Context context, List<DataEntry> listEntries, List<String> ids, Firebase rootRef) {
        this.context = context;
        this.listEntries = listEntries;
        this.ids = ids;
        this.rootRef = rootRef;
    }

    @Override
    public int getCount() {
        return listEntries.size();
    }

    @Override
    public Object getItem(int position) {
        return listEntries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final DataEntry entry = listEntries.get(position);

        final String key = ids.get(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_layout, null);
        }

        TextView f1 = (TextView) convertView.findViewById(R.id.tvField1);
        TextView f2 = (TextView) convertView.findViewById(R.id.tvField2);

        f1.setText(String.valueOf(entry.getFirstName()));
        f2.setText(String.valueOf(entry.getLastName()));

        Button deleteBtn = (Button) convertView.findViewById(R.id.userDeleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootRef.child(key).removeValue();
            }
        });

        convertView.setTag(entry);

        return convertView;
    }

    @Override
    public void onClick(View v) {
    }
}
