package cs.tufts.edu.compfoodie;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
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

public class LoginActivity extends AppCompatActivity {
    private CallbackManager callbackManager;
    private AccessToken accessToken;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

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
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) { // user signed in
                    Log.d("*** Firebase Auth", "User " + user.getUid() + " signed in");
                    Toast.makeText(LoginActivity.this, "Logged in as " + user.getDisplayName(),
                                   Toast.LENGTH_SHORT).show();
                    Intent browsePage = new Intent(getApplicationContext(), BrowseActivity.class);
                    startActivity(browsePage);
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
}


