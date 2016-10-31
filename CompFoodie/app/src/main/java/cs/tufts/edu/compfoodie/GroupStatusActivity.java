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
    private TextView locationOutput;
    private TextView orderTimeOutput;
    private TextView messageOutput;
    private TextView partyCountOutput;
    private ListView partyList;
    private UsersAdapter partyListAdapter;

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
        // get status page text fields
        locationOutput = (TextView)findViewById(R.id.location_output);
        orderTimeOutput = (TextView)findViewById(R.id.order_time_output);
        messageOutput = (TextView)findViewById(R.id.message_output);
        partyCountOutput = (TextView)findViewById(R.id.party_count_output);
        partyList = (ListView)findViewById(R.id.party_list);
        // set status first time
        setStatus();
//
//        database = FirebaseDatabase.getInstance();
//        userID = getIntent().getStringExtra("userID");
//        userRef = database.getReference("users").child(userID).child("groups");
//
//
//        ValueEventListener userListener = new ValueEventListener() {
//            List<String> textList = new ArrayList<>();
//
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
//                    String groupID = snapshot.getValue(String.class);
//                    DatabaseReference groupRef = database.getReference("groups").child(groupID);
//
//                    ValueEventListener groupListener = new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            Group group = dataSnapshot.getValue(Group.class);
//                            String text = String.format("%s. Order at %f:%f in %s. %.0f / %.0f have joined you.", group.message, group.hour, group.minute, group.location, group.partySize, group.partyCap);
//                            Log.v("text", text);
//                            //textList.add(text);
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//                            // Getting Post failed, log a message
//                            Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
//                            // ...
//                        }
//                    };
//
//                    // TODO populate the page with everything in textList
//
//                    groupRef.addValueEventListener(groupListener);
//
//                    /*
//                    lv = (ListView)findViewById(R.id.group_status_list);
//
//                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, textList);
//                    lv.setAdapter(arrayAdapter);
//                    */
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Getting Post failed, log a message
//                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
//                // ...
//            }
//        };
//        userRef.addValueEventListener(userListener);
    }

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
        partyListAdapter = new UsersAdapter(this, group.guests.toArray(
                new String[group.guests.size()]));
        partyList.setAdapter(partyListAdapter); // can add to adapter at any time by add
    }
}
