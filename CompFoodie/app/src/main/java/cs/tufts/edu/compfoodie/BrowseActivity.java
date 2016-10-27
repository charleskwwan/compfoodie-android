package cs.tufts.edu.compfoodie;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
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
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
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
        getUserInfo();
        // todo: add group list
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
                // todo: open fragment to create new group
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
                }
                return true;
            }
        });
        drawerLayout = (DrawerLayout)findViewById(R.id.browse_drawer_layout);
    }

    // make request to facebook for user's name and profile image (and id, not used)
    private void getUserInfo() {
        final DatabaseReference usersRef = dbRef.child("users");
        final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user_id)) {
                    user.setGroups((List<String>)(dataSnapshot.child(user_id).child("groups")
                            .getValue()));
                } else {
                    user.setGroups(new ArrayList<String>()); // new user, set empty
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("*** Firebase User Req", databaseError.toString());
            }
        });
        // make request to fb for updated name and profile pic
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject obj, GraphResponse response) {
                        if (response != null && obj != null) {
                            try {
                                final View header = navDrawer.getHeaderView(0);
                                // set name
                                user.setName(obj.getString("name"));
                                TextView user_name_text = (TextView)header
                                        .findViewById(R.id.user_name);
                                user_name_text.setText(user.getName());
                                // set pic
                                user.setPicUrl(obj.getJSONObject("picture").getJSONObject("data")
                                        .getString("url"));
                                // send to firebase
                                usersRef.child(user_id).setValue(user.toMap());
                                // load image for local bitmap
                                VolleyUtils.getImage(user.getPicUrl(), getApplicationContext(),
                                        new VolleyCallback<Bitmap>() {
                                            @Override
                                            public void onSuccessResponse(Bitmap response) {
                                                // set pic only after async return
                                                userPic = getRoundedCornerBitmap(response,
                                                        response.getWidth() / 2);
                                                ImageView user_pic_v = (ImageView)header
                                                        .findViewById(R.id.user_profile_pic);
                                                user_pic_v.setImageBitmap(userPic);
                                            }
                                        });
                            } catch (Exception e) {
                                Log.e("*** FB Graph Request", e.toString());
                            }
                        } else {
                            Log.e("*** FB Graph Request", "Could not reach Facebook");
                        }
                    }
                });
        Bundle params = new Bundle();
        params.putString("fields", "id,name,picture.type(large)");
        request.setParameters(params);
        request.executeAsync();
    }

    // logout and return to login screen
    private void logout() {
        LoginManager.getInstance().logOut(); // logout fb
        FirebaseAuth.getInstance().signOut(); // logout firebase
        Toast.makeText(BrowseActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
        Intent goToLogin = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(goToLogin);
    }

    // rounds corners of a bitmap
    // http://stackoverflow.com/questions/18229358/bitmap-in-imageview-with-rounded-corners
    private static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
