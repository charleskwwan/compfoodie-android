package cs.tufts.edu.compfoodie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashSet;

public class MyGroupsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private DatabaseReference groupsRef;
    private ListView groupsLV;
    private DatabaseReference dbRef;
    private User user;
    private SharedPreferences prefs;

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);

        // ToolBar
        toolbar = (Toolbar) findViewById(R.id.my_groups_toolbar);
        toolbar.setTitle(getString(R.string.my_groups_toolbar_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get user
        prefs = this.getSharedPreferences("", Context.MODE_PRIVATE);

        user = (User) getIntent().getSerializableExtra(getString(R.string.currentUserKey));
        if (user == null) {
            user = new User();
            user.name = prefs.getString(getString(R.string.user_name), "");
            user.picUrl = prefs.getString(getString(R.string.user_picURL), "");
            user.groups = new ArrayList(prefs.getStringSet(getString(R.string.user_groups), new HashSet<String>()));
        }

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

    private void populateMyGroups() {
        groupsRef = dbRef.child("groups");
        groupsLV = (ListView) findViewById(R.id.my_groups_user_groups);
        GroupsAdapter adapter = new GroupsAdapter(MyGroupsActivity.this, groupsRef, true);
        groupsLV.setAdapter(adapter);
    }
}