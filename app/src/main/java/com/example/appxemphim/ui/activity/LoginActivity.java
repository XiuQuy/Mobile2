package com.example.appxemphim.ui.activity;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.PasswordCredential;
import androidx.credentials.PublicKeyCredential;
import androidx.credentials.exceptions.GetCredentialException;

import com.example.appxemphim.R;
import com.example.appxemphim.data.remote.ServiceApiBuilder;
import com.example.appxemphim.data.remote.UserService;
import com.example.appxemphim.model.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    GetGoogleIdOption googleIdOption;
    GetCredentialRequest requestGoogle;
    CredentialManager credentialManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        //google
        googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getResources().getString(R.string.google_signin_client_id))
                .build();
        requestGoogle = new GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build();
        credentialManager =  CredentialManager.create(this);
        //Button google click
        AppCompatImageButton button = findViewById(R.id.btn_login_google);
        button.setOnClickListener(v -> {
            Executor executor = Executors.newSingleThreadExecutor();
            // Launch sign in flow and do getCredential Request to retrieve the credentials
            credentialManager.getCredentialAsync(this, requestGoogle, null, executor,
                    new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                        @Override
                        public void onResult(GetCredentialResponse result) {
                            handleSignInGoogle(result);
                        }
                        @Override
                        public void onError(GetCredentialException e) {
                            showErrorMessage();
                            Log.e(TAG, "GetCredentialException", e);
                        }
                    }
            );
        });

        //facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().registerCallback(callbackManager,
            new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.d(TAG, "facebook:onSuccess:$loginResult");
                    handleFacebookAccessToken(loginResult.getAccessToken());
                }
                @Override
                public void onCancel() {
                    Log.d(TAG, "facebook:onCancel");
                }
                @Override
                public void onError(@NonNull FacebookException exception) {
                    showErrorMessage();
                    Log.d(TAG, "facebook:onError", exception);
                    if (exception instanceof FacebookAuthorizationException) {
                        if (AccessToken.getCurrentAccessToken() != null) {
                            LoginManager.getInstance().logOut();
                        }
                    }
                }
            });
        //Button facebook click
        AppCompatImageButton buttonFace = findViewById(R.id.btn_login_facebook);
        buttonFace.setOnClickListener(v -> {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        GetTokenResult tokenTokenResult = user.getIdToken(false).getResult();
                        String tokenFirebaseUser = tokenTokenResult.getToken();
                        handleSignFacebookInUserAPI(tokenFirebaseUser);
                    } else {
                        showErrorMessage();
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                    }
                }
            });
    }

    public void handleSignInGoogle(GetCredentialResponse result) {
        // Handle the successfully returned credential.
        Credential credential = result.getCredential();

        if (credential instanceof CustomCredential) {
            if (GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(credential.getType())) {
                // authenticate on your server
                GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(((CustomCredential) credential).getData());
                handleSignGoogleInUserAPI(googleIdTokenCredential.getIdToken());
            } else {
                Log.e(TAG, "Unexpected type of credential");
                showErrorMessage();
            }
        } else {
            Log.e(TAG, "Unexpected type of credential");
            showErrorMessage();
        }
    }

    public void handleSignGoogleInUserAPI(String googleIdToken) {
        UserService userService = ServiceApiBuilder.buildUserApiService(UserService.class);

        Call<User> call = userService.checkLoginGoogle(googleIdToken);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    saveUserInfoLoginSuccess(response.body());
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    Log.e("LOGIN_SUCCESS", ""+response.body());
                } else {
                    Log.e("API_ERROR", "Failed to fetch");
                    showErrorMessage();
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("API_ERROR", "Network error", t);
                showErrorMessage();
            }
        });
    }

    public void handleSignFacebookInUserAPI(String firebaseAuthenFbIdToken) {
        UserService userService = ServiceApiBuilder.buildUserApiService(UserService.class);
        Call<User> call = userService.checkLoginFacebook(firebaseAuthenFbIdToken);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    saveUserInfoLoginSuccess(response.body());
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    Log.e("LOGIN_SUCCESS", ""+response.body());
                } else {
                    Log.e("API_ERROR", "Failed to fetch");
                    showErrorMessage();
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("API_ERROR", "Network error", t);
                showErrorMessage();
            }
        });
    }

    public void showErrorMessage(){
        Toast.makeText(LoginActivity.this,
                getResources().getString(R.string.message_error_login), Toast.LENGTH_LONG).show();
    }

    public void saveUserInfoLoginSuccess(User user){
        // Lưu thông tin người dùng vào SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences("UserInfo", MODE_PRIVATE).edit();
        editor.putString("token", user.getToken());
        editor.putString("name", user.getName());
        editor.putString("email", user.getEmail());
        editor.putString("avatar", user.getAvatar());
        editor.putString("tagSocialNetwork", user.getTagSocialNetwork());
        editor.apply();
    }
}
