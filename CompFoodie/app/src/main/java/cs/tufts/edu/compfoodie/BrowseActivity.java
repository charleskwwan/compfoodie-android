package cs.tufts.edu.compfoodie;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BrowseActivity extends AppCompatActivity {
    private DatabaseReference dbRef;
    private NavigationView navDrawer;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private User user;
    private String userID;
    private Bitmap userPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        // ToolBar
        toolbar = (Toolbar)findViewById(R.id.browse_toolbar);
        toolbar.setTitle(getString(R.string.browse_toolbar_title));
        setSupportActionBar(toolbar);
        // set up drawer
        dbRef = FirebaseDatabase.getInstance().getReference();
        initNavDrawer();
        // get user
        user = (User)getIntent().getSerializableExtra(getString(R.string.currentUserKey));
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        setUserProfile();
        // populate list view with groups
        populateGroups();
    }

    public void populateGroups() {
        DatabaseReference groupRef = dbRef.child("groups");

        ValueEventListener groupListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot groupsSnap) {
                List<String> otherGroups = new ArrayList<>();
                for (DataSnapshot groupSnap : groupsSnap.getChildren()) {
                    otherGroups.add(groupSnap.getKey());
                }
                Log.v("OTHER GROUPS", String.valueOf(otherGroups.size()));
                populateGroupsLV(otherGroups, R.id.browse_other_groups);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        groupRef.addValueEventListener(groupListener);
    }

    public void populateGroupsLV(List<String> groups, int viewID) {
        GroupsAdapter groupsAdapter = new GroupsAdapter(this, groups.toArray(new String[groups.size()]));
        ListView groupsLV = (ListView)findViewById(viewID);
        groupsLV.setAdapter(groupsAdapter);
    }

    // inflates menu if action bar present (toolbar)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.user_display_item:
                drawerLayout.openDrawer(Gravity.RIGHT);
                return true;
            case R.id.add_group_item:
                Intent createPage = new Intent(getApplicationContext(), CreateGroupActivity.class);
                createPage.putExtra(getString(R.string.currentUserKey), user);
                startActivity(createPage);
                return true;
            case R.id.my_groups_item:
                Intent myGroupsPage = new Intent(getApplicationContext(), MyGroupsActivity.class);
                myGroupsPage.putExtra(getString(R.string.currentUserKey), user);
                startActivity(myGroupsPage);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // initialize nav drawer including buttons and related variables
    private void initNavDrawer() {
        navDrawer = (NavigationView)findViewById(R.id.browse_drawer);
        navDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch(id) {
                    case R.id.drawer_logout:
                        logout();
                        break;
                }
                return true;
            }
        });
        drawerLayout = (DrawerLayout)findViewById(R.id.browse_drawer_layout);
    }

    private void setUserProfile() {
        VolleyUtils.getImage(user.picUrl, getApplicationContext(), new VolleyCallback<Bitmap>() {
            @Override
            public void onSuccessResponse(Bitmap response) {
                View header = navDrawer.getHeaderView(0);
                // set name
                TextView userNameView = (TextView)header.findViewById(R.id.user_name);
                userNameView.setText(user.name);
                // set image
                userPic = ImageTransform.getRoundedCornerBitmap(response, response.getWidth() / 2);
                ImageView userPicView = (ImageView)header.findViewById(R.id.user_profile_pic);
                userPicView.setImageBitmap(userPic);
            }
        });
    }

    // logout and return to login screen
    private void logout() {
        LoginManager.getInstance().logOut(); // logout fb
        FirebaseAuth.getInstance().signOut(); // logout firebase
        Toast.makeText(BrowseActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
        Intent goToLogin = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(goToLogin);
    }
}
