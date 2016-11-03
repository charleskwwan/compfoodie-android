package cs.tufts.edu.compfoodie;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.LoginButton;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import org.json.JSONObject;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private CallbackManager callbackManager;
    private AccessToken accessToken;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialze fb sdk
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        // ToolBar
        Toolbar toolbar = (Toolbar)findViewById(R.id.login_toolbar);
        toolbar.setTitle(getString(R.string.login_toolbar_title));
        setSupportActionBar(toolbar);

        // fb login button
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
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

        // firebase auth
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) { // user signed in
                    Log.d("*** Firebase Auth", "User " + firebaseUser.getUid() + " signed in");
                    Toast.makeText(LoginActivity.this, "Logged in as " + firebaseUser
                            .getDisplayName(), Toast.LENGTH_SHORT).show();
                    getUser(); // also handles go to browse
                } else { // user not signed in/signed out
                    Log.d("*** Firebase Auth", "User not signed in");
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuth != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    // exchange fb token for firebase token
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
            user.groups = new ArrayList<String>();
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
                                user.groups = new ArrayList<String>();
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
    private void goToBrowse() {
        // add user to firebase
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");
        userRef.child(userId).setValue(user);
        // goto browse
        Intent browsePage = new Intent(getApplicationContext(), BrowseActivity.class);
        browsePage.putExtra(getString(R.string.currentUserKey), user);
        startActivity(browsePage);
    }
}


