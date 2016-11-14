package cs.tufts.edu.compfoodie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class LoginActivity extends AppCompatActivity {
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1972;
    private CallbackManager callbackManager;
    private AccessToken accessToken;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private User user;
    private SharedPreferences prefs;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize sharedPreferences
        prefs = getSharedPreferences("", Context.MODE_PRIVATE);

        // initialize fb sdk
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        // ToolBar - distorts look, commented out
//        Toolbar toolbar = (Toolbar) findViewById(R.id.login_toolbar);
//        toolbar.setTitle(getString(R.string.login_toolbar_title));
//        setSupportActionBar(toolbar);

        // Set logo
        ImageView logoView = (ImageView)findViewById(R.id.login_logo);
        BitmapDrawable logo = (BitmapDrawable)ContextCompat.getDrawable(this, R.mipmap.ic_app_logo);
        Bitmap logoBitmap = logo.getBitmap();
        logoBitmap = Bitmap.createScaledBitmap(logoBitmap, logoBitmap.getWidth() * 2,
                logoBitmap.getHeight() * 2, false);
        logoView.setImageBitmap(logoBitmap);

        Log.e("*** Login GMS", Integer.toString(R.integer.google_play_services_version));

        // fb login button
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        TextView alertView = (TextView) findViewById(R.id.login_alert);
        if (checkGooglePlayServices()) { // only allow login if up to date gms, otherwise scold
            alertView.setText("");
            loginButton.setEnabled(true);
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.v("*** Facebook Login", "Login successful, saving access token");
                    accessToken = loginResult.getAccessToken();
                    handleFacebookAccessToken(accessToken);
                }

                @Override
                public void onCancel() {
                    Log.v("*** Facebook Login", "Login cancelled");
                }

                @Override
                public void onError(FacebookException e) {
                    Log.e("*** Facebook Login", e.toString());
                }
            });
        } else {
            alertView.setText(getString(R.string.login_alert_text));
            loginButton.setEnabled(false);

        }

        // firebase auth
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) { // user signed in
                    if (AccessToken.getCurrentAccessToken() != null) {
                        Log.d("*** Firebase Auth", "User " + firebaseUser.getUid() + " signed in");
                        Toast.makeText(LoginActivity.this, "Logged in as " + firebaseUser
                                .getDisplayName(), Toast.LENGTH_SHORT).show();
                        getUser(); // also handles go to browse
                    } else {
                        firebaseAuth.signOut();
                    }
                } else { // user not signed in/signed out
                    Log.d("*** Firebase Auth", "User not signed in");
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
                }
            }
        };

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        mAuth.addAuthStateListener(mAuthListener);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    protected void onStop() {
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        if (mAuth != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    // http://stackoverflow.com/questions/17858215/google-play-services-out-of-date-requires-3159100-but-found-3158130
    // checks if google play services is up to date or not, otherwise show dialog
    private boolean checkGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        REQUEST_GOOGLE_PLAY_SERVICES).show();
            }
            return false;
        }
        return true;
    }

    // exchange fb token for firebase token
    @SuppressWarnings("ConstantConditions")
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("*** Token Handling", "Handling " + token.toString());
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("*** Attempted DB SignIn", "Was " + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w("Failed DB SignIn", task.getException().toString());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // snapshot MUST be from database users child
    private void getUserFromFirebase(DataSnapshot snapshot, String userId) {
        user = new User();
        user = snapshot.child(userId).getValue(User.class);
        if (user.groups == null) { // might have no groups on firebase
            user.groups = new ArrayList<>();
        }
        goToBrowse();
    }

    // creates a new user from facebook
    private void getUserFromFacebook() {
        user = new User();
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        if (response != null && object != null) {
                            try {
                                user.name = object.getString("name");
                                user.picUrl = object.getJSONObject("picture").getJSONObject("data")
                                        .getString("url");
                                user.groups = new ArrayList<>();
                                goToBrowse();
                            } catch (Exception e) {
                                Log.e("*** Graph Request", e.toString());
                            }
                        } else {
                            Log.e("*** Graph Request", "Facebook did return data");
                        }
                    }
                }
        );
        Bundle params = new Bundle();
        params.putString("fields", "id,name,picture.type(large)");
        request.setParameters(params);
        request.executeAsync();
    }

    // checks if the user is in firebase. if yes, get user from there. if not, get from facebook.
    @SuppressWarnings("ConstantConditions")
    private void getUser() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference()
                .child("users");
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userId)) {
                    getUserFromFirebase(dataSnapshot, userId);
                } else {
                    getUserFromFacebook();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("*** Firebase Request", "Could not reach Firebase");
            }
        });
    }

    // goto browse, while passing user browse
    // only USE when have all user data
    @SuppressWarnings("ConstantConditions")
    private void goToBrowse() {
        // add user to firebase
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");
        userRef.child(userId).updateChildren(user.toMap());

        // add user to sharedPreferences
        prefs.edit().putString(getString(R.string.user_name), user.name).apply();
        prefs.edit().putString(getString(R.string.user_picURL), user.picUrl).apply();
        Set<String> groupSet = new HashSet<String>();
        groupSet.addAll(user.groups);
        prefs.edit().putStringSet(getString(R.string.user_groups), groupSet).apply();

        for (String groupID : user.groups) {
            FirebaseMessaging.getInstance().subscribeToTopic("groupID_"+groupID);
        }

        // goto browse
        Intent browsePage = new Intent(getApplicationContext(), BrowseActivity.class);
        browsePage.putExtra(getString(R.string.currentUserKey), user);
        startActivity(browsePage);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Login Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }
}


