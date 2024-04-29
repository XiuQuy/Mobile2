package com.example.appxemphim.ui.activity;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Credentials;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
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
import com.example.appxemphim.model.UserLogin;
import com.example.appxemphim.model.UserResponse;
import com.example.appxemphim.ui.activity.RegisterActivity;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
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
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout emailInputLayout;
    private TextInputLayout passwordInputLayout;
    CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    GetGoogleIdOption googleIdOption;
    GetCredentialRequest requestGoogle;
    CredentialManager credentialManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        LanguageManager.initLanguage(this);
        if(checkTokenExpiration()){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        emailInputLayout = findViewById(R.id.username);
        passwordInputLayout = findViewById(R.id.password);

        // Ánh xạ các phần tử giao diện từ layout
        AppCompatButton signupButton = findViewById(R.id.btnSignUp);
        AppCompatButton loginButton = findViewById(R.id.btnLogin);

        // Xử lý sự kiện khi người dùng nhấn nút đăng ký
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Điều hướng người dùng đến màn hình đăng ký
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInputLayout.getEditText().getText().toString().trim();
                String password = passwordInputLayout.getEditText().getText().toString().trim();

                // Kiểm tra xem có trường thông tin nào bị trống không
                if (email.isEmpty() || password.isEmpty()) {
                    showDialog("Thông tin không đầy đủ", "Vui lòng điền đầy đủ thông tin");
                    return;
                } else {
                    // Tạo đối tượng User từ thông tin người dùng
                    UserLogin user = new UserLogin(email, password);

                    // Gọi phương thức để đăng ký người dùng
                    LoginUser(user);
                }
            }
        });
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

    private void LoginUser(UserLogin user) {
        // Tạo một đối tượng Retrofit
        UserService userService = ServiceApiBuilder.buildUserApiService(UserService.class);

        // Gọi phương thức đăng ký từ UserService và truyền đối tượng UserRegister vào
        Call<UserResponse> call = userService.checkLogin(user);
        // Gửi yêu cầu đăng ký người dùng và xử lý kết quả
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    // Xử lý phản hồi từ server sau khi đăng nhập thành công
                    UserResponse userResponse = response.body();
                    if (userResponse != null) {
                        // Lấy thông tin người dùng từ UserResponse
                        int id = userResponse.getId();
                        String name = userResponse.getName();
                        String email = userResponse.getEmail();
                        String token = userResponse.getToken();
                        String avatar =  userResponse.getAvatar();
                        User userDTO = new User();
                        userDTO.setId(id);
                        userDTO.setEmail(email);
                        userDTO.setName(name);
                        userDTO.setToken(token);
                        userDTO.setAvatar(avatar);
                        saveUserInfoLoginSuccess(userDTO);
                        // Hiển thị dialog thông báo đăng nhập thành công và chuyển sang MainActivity
                        showDialogAndNavigateToMain("Đăng nhập thành công", "Chào mừng " + name + " đến với ứng dụng", id, name, email, token);
                    }
                } else {
                    // Xử lý khi gặp lỗi không mong muốn từ server
                    String errorMessage = "Đã xảy ra lỗi";
                    try {
                        errorMessage = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    showDialog("Lỗi", errorMessage);
                }
            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                // Xử lý khi gặp lỗi trong quá trình gửi yêu cầu đến server
                showDialog("Lỗi", "Đã xảy ra lỗi");            }
        });
    }
    private void showDialogAndNavigateToMain(String title, String message, int id, String name, String email, String token) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        // Thêm onClickListener cho nút "OK"
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Khi nhấn vào nút "OK", chuyển sang MainActivity và truyền thông tin người dùng
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.show();
    }

    private void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.show();      
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
                        LoginManager.getInstance().logOut();
                        FirebaseAuth.getInstance().signOut();
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
                    finish();
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
                    finish();
                    Log.e("LOGIN_SUCCESS", ""+response.body());
                } else {
                    Log.e("API_LOGIN_FB_ERROR", "Failed to fetch");
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
        editor.putInt("userId", user.getId());
        editor.putString("token", user.getToken());
        editor.putString("name", user.getName());
        editor.putString("email", user.getEmail());
        editor.putString("avatar", user.getAvatar());
        editor.putString("tagSocialNetwork", user.getTagSocialNetwork());
        editor.apply();
    }

    public boolean checkTokenExpiration() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        if (token == null) {
            // Token not found, need login
            return false;
        }

        // Decode JWT token
        String[] splitToken = token.split("\\.");
        String base64EncodedBody = splitToken[1];
        byte[] decodedBytes = Base64.decode(base64EncodedBody, Base64.URL_SAFE);
        String jsonBody = new String(decodedBytes, StandardCharsets.UTF_8);
        Log.i("BODY", jsonBody);
        // Deserialize JSON body
        Gson gson = new Gson();
        TokenData tokenData = gson.fromJson(jsonBody, TokenData.class);

        // Check token expiration
        Date now = Calendar.getInstance().getTime();
        long currentTimeMillis = now.getTime() / 1000;
        if (currentTimeMillis > tokenData.getExpirationDate()) {
            // Token expired, need login
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            return false;
        }

        // Token still valid
        return true;
    }
    private static class TokenData {
        private long exp;
        public long getExpirationDate() {
            return exp;
        }
    }
}
