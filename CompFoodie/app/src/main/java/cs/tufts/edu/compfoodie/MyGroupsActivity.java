package cs.tufts.edu.compfoodie;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyGroupsActivity extends AppCompatActivity {
    private DatabaseReference dbRef;
    private Toolbar toolbar;
    private User user;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);

        // ToolBar
        toolbar = (Toolbar)findViewById(R.id.my_groups_toolbar);
        toolbar.setTitle(getString(R.string.my_groups_toolbar_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get user
        user = (User)getIntent().getSerializableExtra(getString(R.string.currentUserKey));
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // work with firebase
        dbRef = FirebaseDatabase.getInstance().getReference();
        populateUserGroups();
    }

    // add user back when going back to parent
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                upIntent.putExtra(getString(R.string.currentUserKey), user); // add user
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) { // not part of app tasks
                    TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent)
                            .startActivities();
                } else {
                    NavUtils.navigateUpTo(this, upIntent); // part of app, just go
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void populateUserGroups() {
        DatabaseReference userRef = dbRef.child("users").child(userID).child("groups");
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot userSnap) {
                List<String> userGroups = new ArrayList<>();
                if (userSnap.getValue() != null) {
                    userGroups = (List<String>) userSnap.getValue();
                }
                if (userGroups.size() == 0) {
                    TextView noGroupsAlert = (TextView)findViewById(R.id.no_groups_alert);
                    noGroupsAlert.setText(getString(R.string.my_groups_no_groups));
                } else {
                    populateGroupsLV(userGroups, R.id.my_groups_user_groups);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        userRef.addValueEventListener(userListener);
    }

    public void populateGroupsLV(List<String> groups, int viewID) {
        GroupsAdapter groupsAdapter = new GroupsAdapter(this, groups.toArray(new String[groups.size()]));
        ListView groupsLV = (ListView)findViewById(viewID);
        groupsLV.setAdapter(groupsAdapter);
    }
}