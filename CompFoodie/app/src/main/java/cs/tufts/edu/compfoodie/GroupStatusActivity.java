package cs.tufts.edu.compfoodie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

    private String userID;
    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_group_status);

        database = FirebaseDatabase.getInstance();

        /*
        // ToolBar
        Toolbar toolbar = (Toolbar)findViewById(R.id.group_status_toolbar);
        toolbar.setTitle(getString(R.string.group_status_title));
        setSupportActionBar(toolbar);
        */

        userID = getIntent().getStringExtra("userID");
        userRef = database.getReference("users").child(userID).child("groups");


        ValueEventListener userListener = new ValueEventListener() {
            List<String> textList = new ArrayList<>();

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String groupID = snapshot.getValue(String.class);
                    DatabaseReference groupRef = database.getReference("groups/" + groupID);

                    ValueEventListener groupListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Group group = dataSnapshot.getValue(Group.class);
                            String text = String.format("%s. Order at %d in %s. %.0f / %.0f have joined you.", group.message, group.orderTime, group.location, group.partySize, group.partyCap);
                            Log.v("text", text);
                            textList.add(text);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                            // ...
                        }
                    };

                    // TODO populate the page with everything in textList
                    /*
                    groupRef.addValueEventListener(groupListener);

                    lv = (ListView)findViewById(R.id.group_status_list);

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, textList);
                    lv.setAdapter(arrayAdapter);
                    */
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        userRef.addValueEventListener(userListener);

        /*
        // groups handling
        Group group = (Group) getIntent().getSerializableExtra("group");

        TextView locationStatus = (TextView)findViewById(R.id.location_status);
        TextView partySizeStatus = (TextView)findViewById(R.id.party_size_status);
        TextView orderTimeStatus = (TextView)findViewById(R.id.order_time_status);
        TextView messageStatus = (TextView)findViewById(R.id.message_status);

        locationStatus.setText(String.format("Meet at %s", group.location));
        partySizeStatus.setText(String.format("%d out of %d people have joined you", group.partySize, group.partyCap));
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        orderTimeStatus.setText("Order at " + dateFormat.format(group.orderTime));
        messageStatus.setText(group.message);
        */
    }



}
