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
    private DatabaseReference groupsRef;
    private ListView groupsLV;

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
        populateMyGroups();
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

    public void populateMyGroups() {
        groupsRef = dbRef.child("groups");
        groupsLV = (ListView)findViewById(R.id.my_groups_user_groups);
        GroupsAdapter adapter = new GroupsAdapter(MyGroupsActivity.this, groupsRef);
        groupsLV.setAdapter(adapter);
    }
}