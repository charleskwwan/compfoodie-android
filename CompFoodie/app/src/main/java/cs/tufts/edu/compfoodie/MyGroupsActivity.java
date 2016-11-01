package cs.tufts.edu.compfoodie;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.R.string.no;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static com.facebook.internal.SmartLoginOption.None;

public class MyGroupsActivity extends AppCompatActivity {
    private DatabaseReference dbRef;
    private Toolbar toolbar;
    private String userID;
    private List<String> userGroups = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);
        // ToolBar
        toolbar = (Toolbar)findViewById(R.id.my_groups_toolbar);
        toolbar.setTitle(getString(R.string.my_groups_toolbar_title));
        dbRef = FirebaseDatabase.getInstance().getReference();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        populateUserGroups();
    }

    public void populateUserGroups() {
        DatabaseReference userRef = dbRef.child("users").child(userID).child("groups");
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot userSnap) {
                if (userSnap.getValue() != null) {
                    userGroups = (List<String>) userSnap.getValue();
                }
                populateGroupsLV(userGroups, R.id.my_groups_user_groups);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        userRef.addValueEventListener(userListener);
    }

    public void populateGroupsLV(List<String> groups, int viewID) {
        Log.v("groupSize", String.valueOf(groups.size()));
        GroupsAdapter groupsAdapter = new GroupsAdapter(this, groups.toArray(new String[groups.size()]));
        ListView groupsLV = (ListView)findViewById(viewID);
        groupsLV.setAdapter(groupsAdapter);
    }
}