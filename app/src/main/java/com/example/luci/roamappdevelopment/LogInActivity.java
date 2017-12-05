package com.example.luci.roamappdevelopment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LogInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    private GoogleApiClient googleApiClient;
    Button button;
    private static UserProfile profile;
    Button signOut;
    private FirebaseAuth mAuth;
    static boolean isSuccessful;
    static boolean intentCheck = false;
    ConstraintLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        button = (Button) findViewById(R.id.sign_in_btn);
//        button.setEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window  w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        mAuth = FirebaseAuth.getInstance();
        intentCheck = checkForIntent();
        if(intentCheck)
        {
            setUpLogOutScreen();
            getSupportActionBar().hide();
//            googleApiClient.connect();
//            googleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
//                @Override
//                public void onConnected(@Nullable Bundle bundle) {
//                    onSignOut();
//                }
//
//                @Override
//                public void onConnectionSuspended(int i) {
//
//                }
//            });
        }
        else
        {
            setUpLogInScreen();
            getSupportActionBar().hide();
            checkForLogInAlready();
        }
    }
    @Override
    public void onStart()
    {
        super.onStart();
//        if(intentCheck)
//        {
//            signOut.performClick();
//        }
    }
    private void setUpLogInScreen()
    {
        setContentView(R.layout.activity_log_in);
        button = (Button) findViewById(R.id.sign_in_btn);
        button.setEnabled(true);
    }
    private void setUpLogOutScreen()
    {

        setContentView(R.layout.activity_log_in_already_logged_in);
        root = (ConstraintLayout) findViewById(R.id.root_for_logged_in);
        setUpUserPic();
        signOut = (Button) findViewById(R.id.sign_out);

    }
    public void onSignOut()
    {
        profile = UserProfile.getInstance();
        signOut();
        profile.erase();
        FirebaseDatabaseManager.destroy();
        MainActivity.main.finish();
        setUpLogInScreen();

    }
    public void onSignOut(View v)
    {
        profile = UserProfile.getInstance();
        signOut();
        profile.erase();
        FirebaseDatabaseManager.destroy();
        MainActivity.main.finish();
        setUpLogInScreen();

    }
    private void setUpUserPic()
    {
        ImageView view = (ImageView)findViewById(R.id.log_out_screen_user_image);
        try
        {
            profile = UserProfile.getInstance();
            Glide.with(this).load(profile.imagePath).into(view);
        }catch(Exception e){}
    }
    private void checkForLogInAlready()
    {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
           //Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            //showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                  //  hideProgressDialog();
                    handleResult(googleSignInResult);
                }
            });
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    private boolean checkForIntent()
    {
       try
       {
           Intent i = getIntent();
           if(i != null)
           {
               if(i.getBooleanExtra("isLogOut", false))
               {
                   setContentView(R.layout.activity_log_in_already_logged_in);
                   return true;
               }
           }
       }
       catch(Exception e){}
        return false;
    }
    private void signOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient);
    }
    private boolean handleResult(GoogleSignInResult result)
    {
       if(result.isSuccess())
       {
           GoogleSignInAccount account = result.getSignInAccount();
           Intent i = new Intent(this,MainActivity.class);
           Bundle b = new Bundle();
           b.putString("name", account.getDisplayName());
           b.putString("email",account.getEmail());
           i.setData(account.getPhotoUrl());
           i.putExtras(b);
           startActivity(i);
           finish();
           return true;
       }
       else
       {
 //          Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();
           return false;
       }
    }

    public void signIn(View v) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, 9001);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 9001) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
               boolean b =  handleResult(result);
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                if (!b)
                {
                    Toast.makeText(this, "Authenticaton failed ", Toast.LENGTH_SHORT).show();
                }
//                if(isSuccessful)
//                {
//                    handleResult(result);
//                    isSuccessful = false;
//                }
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            isSuccessful = true;
                        } else {
//                            Toast.makeText(LogInActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
                            isSuccessful = false;
                        }

                        // ...
                    }
                });
    }


}
