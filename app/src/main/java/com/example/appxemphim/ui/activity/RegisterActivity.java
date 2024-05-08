package com.example.appxemphim.ui.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;

import com.example.appxemphim.R;
import com.example.appxemphim.data.remote.ServiceApiBuilder;
import com.example.appxemphim.data.remote.UserService;
import com.example.appxemphim.model.UserRegister;
import com.example.appxemphim.model.UserResponse;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout nameInputLayout;
    private TextInputLayout emailInputLayout;
    private TextInputLayout passwordInputLayout;
    private TextInputLayout confirmPasswordInputLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

        // Ánh xạ các TextView từ layout
        nameInputLayout = findViewById(R.id.name_signup);
        emailInputLayout = findViewById(R.id.username_signup);
        passwordInputLayout = findViewById(R.id.password_signup);
        confirmPasswordInputLayout = findViewById(R.id.passwordConfirm_signup);

        // Xử lý sự kiện khi người dùng nhấn nút đăng ký
        AppCompatButton registerButton = findViewById(R.id.btnRegister);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy thông tin nhập từ các trường EditText
                String name = nameInputLayout.getEditText().getText().toString().trim();
                String email = emailInputLayout.getEditText().getText().toString().trim();
                String password = passwordInputLayout.getEditText().getText().toString().trim();
                String confirmPassword = confirmPasswordInputLayout.getEditText().getText().toString().trim();

                // Kiểm tra xem có trường thông tin nào bị trống không
                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    showDialog("Thông tin không đầy đủ", "Vui lòng điền đầy đủ thông tin");
                    return;
                }
                // Kiểm tra xem mật khẩu và xác nhận mật khẩu có khớp nhau không
                else if (!password.equals(confirmPassword)) {
                    showDialog("Mật khẩu không khớp", "Vui lòng nhập lại mật khẩu và xác nhận mật khẩu giống nhau");
                    return;
                } else {
                    // Tạo đối tượng User từ thông tin người dùng
                    UserRegister newUser = new UserRegister(name, email, password, confirmPassword);

                    // Gọi phương thức để đăng ký người dùng
                    registerUser(newUser);
                }
            }
        });

        // Xử lý sự kiện khi người dùng nhấn quay lai
        ImageView backButton = findViewById(R.id.btnBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void registerUser(UserRegister userRegister) {
        // Tạo một đối tượng Retrofit
        UserService userService = ServiceApiBuilder.buildUserApiService(UserService.class);

        // Gọi phương thức đăng ký từ UserService và truyền đối tượng UserRegister vào
        Call<Void> call = userService.register(userRegister);

        // Gửi yêu cầu đăng ký người dùng và xử lý kết quả
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Xử lý phản hồi từ server sau khi đăng ký thành công
                    showSuccessDialog();
                } else {
                    // Xử lý khi gặp lỗi không mong muốn từ server
                    String errorMessage = "Đã xảy ra lỗi";
                    try {
                        errorMessage = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    showDialog("Lỗi", errorMessage);                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Xử lý khi gặp lỗi trong quá trình gửi yêu cầu đến server
                Log.e("REGISTER", "FAILURE", t);
                showDialog("Lỗi", "Đã xảy ra lỗi");            }
        });
    }

    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Đăng ký thành công");
        builder.setMessage("Bạn đã đăng ký thành công. Bạn có muốn quay lại màn hình đăng nhập không?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Điều hướng người dùng đến màn hình đăng nhập hoặc màn hình khác tùy theo nhu cầu của bạn
                // Ví dụ: Chuyển đến màn hình đăng nhập
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Kết thúc hoạt động hiện tại nếu cần
            }
        });
        builder.setNegativeButton("Không", null); // Không làm gì khi nhấn nút Không
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

