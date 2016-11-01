package cs.tufts.edu.compfoodie;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

/**
 * Created by charlw on 10/31/16.
 *      for populating a list view with groups
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
        groupsRef = FirebaseDatabase.getInstance().getReference().child("groups");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // group id at position
        String groupId = getItem(position);
        // check if view is reused, otherwise inflate
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_group, parent, false);
        }
        // get all textviews
        final TextView msgOutput = (TextView)convertView.findViewById(R.id.group_message);
        final TextView orderTimeOutput = (TextView)convertView.findViewById(R.id.group_order_time);
        final TextView locationOutput = (TextView)convertView.findViewById(R.id.group_location);
        final TextView partyCntOutput = (TextView)convertView.findViewById(R.id.group_party_cnt);
        // get group from firebase
        DatabaseReference groupRef = groupsRef.child(groupId);
        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Group group = dataSnapshot.getValue(Group.class);
                msgOutput.setText(group.message);
                locationOutput.setText(group.location);
                // assign party cnt
                String partyCnt = String.format(Locale.ENGLISH, "%d/%d",
                        group.partySize.intValue(), group.partyCap.intValue());
                partyCntOutput.setText(partyCnt);
                // assign order time
                int orderHour = group.hour.intValue();
                String tformat = orderHour < 12 ? "AM" : "PM";
                String hstr = String.format(Locale.ENGLISH, "%02d", orderHour % 12);
                String mstr = String.format(Locale.ENGLISH, "%02d", group.minute.intValue());
                orderTimeOutput.setText(hstr + ":" + mstr + " " + tformat);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("*** Group Adapter", databaseError.toString());
            }
        });
        return convertView;
    }
}
