package cs.tufts.edu.compfoodie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class BrowseActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ListView groupsLV;
    private DatabaseReference groupsRef;
    private DatabaseReference dbRef;
    private NavigationView navDrawer;
    private DrawerLayout drawerLayout;
    private User user;
    private Bitmap userPic;
    private SharedPreferences prefs;

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_browse);
        // ToolBar
        toolbar = (Toolbar) findViewById(R.id.browse_toolbar);
        toolbar.setTitle(getString(R.string.browse_toolbar_title));
        setSupportActionBar(toolbar);
        // set up drawer
        dbRef = FirebaseDatabase.getInstance().getReference();
        initNavDrawer();

        // get user
        user = (User) getIntent().getSerializableExtra(getString(R.string.currentUserKey));

        setUserProfile();
        // populate list view with groups
        populateGroups();

        FirebaseInstanceId.getInstance().getToken();
    }

    private void populateGroups() {
        groupsRef = dbRef.child("groups");
        groupsLV = (ListView) findViewById(R.id.browse_other_groups);
        GroupsAdapter adapter = new GroupsAdapter(BrowseActivity.this, groupsRef, false);
        groupsLV.setAdapter(adapter);
    }

    // inflates menu if action bar present (toolbar)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.user_display_item:
                drawerLayout.openDrawer(GravityCompat.START);
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
        navDrawer = (NavigationView) findViewById(R.id.browse_drawer);
        navDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.drawer_logout:
                        logout();
                        break;
                }
                return true;
            }
        });
        drawerLayout = (DrawerLayout) findViewById(R.id.browse_drawer_layout);
    }

    private void setUserProfile() {
        VolleyUtils.getImage(user.picUrl, getApplicationContext(), new VolleyCallback<Bitmap>() {
            @Override
            public void onSuccessResponse(Bitmap response) {
                View header = navDrawer.getHeaderView(0);
                // set name
                TextView userNameView = (TextView) header.findViewById(R.id.user_name);
                userNameView.setText(user.name);
                // set image
                userPic = ImageTransform.getRoundedCornerBitmap(response, response.getWidth() / 2);
                userPic = Bitmap.createScaledBitmap(userPic, 750, 750, false);
                ImageView userPicView = (ImageView) header.findViewById(R.id.user_profile_pic);
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
