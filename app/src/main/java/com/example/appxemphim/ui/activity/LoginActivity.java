package com.example.appxemphim.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.appxemphim.R;
import com.example.appxemphim.data.remote.ServiceApiBuilder;
import com.example.appxemphim.data.remote.UserService;
import com.example.appxemphim.model.UserLogin;
import com.example.appxemphim.model.UserResponse;
import com.example.appxemphim.ui.activity.RegisterActivity;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout emailInputLayout;
    private TextInputLayout passwordInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

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
                intent.putExtra("userId", id);
                intent.putExtra("userName", name);
                intent.putExtra("userEmail", email);
                intent.putExtra("userToken", token);
                startActivity(intent);
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
}
