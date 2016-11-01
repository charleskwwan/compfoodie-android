package cs.tufts.edu.compfoodie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by charlw on 10/31/16.
 */

public class GroupsAdapter extends ArrayAdapter<String> {
    private Context context;
    private String groupIds[] = null;
    private DatabaseReference groupsRef;

    // constructor
    public GroupsAdapter(Context context, String[] groupIds) {
        super(context, 0, groupIds); // 0 for null item layout, all in item_user
        this.context = context;
        this.groupIds = groupIds;
        groupsRef = FirebaseDatabase.getInstance().getReference().child("users");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // group id at position
        String groupId = getItem(position);
        // check if view is reused, otherwise inflate
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_group, parent, false);
        }
        // todo: populate group item layout
        return convertView;
    }
}
