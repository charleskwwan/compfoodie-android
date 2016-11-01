package cs.tufts.edu.compfoodie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class GroupStatusActivity extends AppCompatActivity {
//
//    private String userID;
//    private FirebaseDatabase database;
//    private DatabaseReference userRef;
//    private ListView lv;
    private User user;
    private Group group;
    private String groupId;
    private TextView locationOutput;
    private TextView orderTimeOutput;
    private TextView messageOutput;
    private TextView partyCountOutput;
    private ListView partyList;
    private UsersAdapter partyListAdapter;
    private DatabaseReference guestsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_status);
        // toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.group_status_toolbar);
        toolbar.setTitle(getString(R.string.group_status_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // allows return to browse page
        // get intent info
        user = (User)getIntent().getSerializableExtra(getString(R.string.currentUserKey));
        group = (Group)getIntent().getSerializableExtra(getString(R.string.currentGroupKey));
        groupId = (String)getIntent().getSerializableExtra(getString(R.string.currentGroupIdKey));
        // get status page text fields
        locationOutput = (TextView)findViewById(R.id.location_output);
        orderTimeOutput = (TextView)findViewById(R.id.order_time_output);
        messageOutput = (TextView)findViewById(R.id.message_output);
        partyCountOutput = (TextView)findViewById(R.id.party_count_output);
        partyList = (ListView)findViewById(R.id.party_list);
        // set status first time
        setStatus();
        // create listener for party
        guestsRef = FirebaseDatabase.getInstance().getReference().child("groups").child(groupId)
            .child("guests");
        ValueEventListener guestsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                List<String> guests = dataSnapshot.getValue(t);
                populateParty(guests);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("*** Users Listener", databaseError.toString());
            }
        };
        guestsRef.addValueEventListener(guestsListener);
    }

    // fills all relevant text fields in the layout
    private void setStatus() {
        locationOutput.setText(group.location);
        int orderHour = group.hour.intValue();
        String tformat = orderHour < 12 ? "AM" : "PM";
        String hstr = String.format(Locale.ENGLISH, "%02d", orderHour % 12);
        String mstr = String.format(Locale.ENGLISH, "%02d", group.minute.intValue());
        orderTimeOutput.setText(hstr + ":" + mstr + " " + tformat);
        messageOutput.setText(group.message);
        String orderTimeStr = String.format(Locale.ENGLISH, "%d/%d", group.partySize.intValue(),
                group.partyCap.intValue());
        partyCountOutput.setText(orderTimeStr);
    }

    // puts a list of guests (ids) into the guests list view
    private void populateParty(List<String> guests) {
        partyListAdapter = new UsersAdapter(this, guests.toArray(new String[guests.size()]));
        partyList.setAdapter(partyListAdapter); // can add to adapter using add function
    }
}
