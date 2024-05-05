package com.example.appxemphim.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appxemphim.R;
import com.example.appxemphim.data.remote.ServiceApiBuilder;
import com.example.appxemphim.data.remote.UserService;
import com.example.appxemphim.model.ChangePasswordDTO;
import com.example.appxemphim.model.User;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChangePasswordActivity extends AppCompatActivity {

    private EditText editTextCurrentPassword, editTextNewPassword, editTextConfirmPassword;
    private Button buttonChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        // Ánh xạ các thành phần giao diện
        editTextCurrentPassword = findViewById(R.id.editTextCurrentPassword);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        buttonChangePassword = findViewById(R.id.buttonChangePassword);

        // Xử lý sự kiện khi người dùng nhấn nút đổi mật khẩu
        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
        ImageView btnBack = findViewById(R.id.back_button);
        btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void changePassword() {
        String currentPassword = editTextCurrentPassword.getText().toString().trim();
        String newPassword = editTextNewPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        // Kiểm tra xem mật khẩu hiện tại có trống không
        if (currentPassword.isEmpty()) {
            editTextCurrentPassword.setError("Vui lòng nhập mật khẩu hiện tại");
            editTextCurrentPassword.requestFocus();
            return;
        }
        // Kiểm tra xem mật khẩu mới có trống không
        if (newPassword.isEmpty()) {
            editTextNewPassword.setError("Vui lòng nhập mật khẩu mới");
            editTextNewPassword.requestFocus();
            return;
        }
        // Kiểm tra xác nhận mật khẩu có trống không
        if (confirmPassword.isEmpty()) {
            editTextConfirmPassword.setError("Vui lòng nhập lại mật khẩu mới");
            editTextConfirmPassword.requestFocus();
            return;
        }
        // Kiểm tra độ dài tối thiểu
        int minPasswordLength = 6;
        if (newPassword.length() < minPasswordLength) {
            editTextNewPassword.setError("Mật khẩu phải chứa ít nhất " + minPasswordLength + " ký tự");
            editTextNewPassword.requestFocus();
            return;
        }
        // Kiểm tra xác nhận mật khẩu có trùng khớp với mật khẩu mới không
        if (!newPassword.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Mật khẩu xác nhận không trùng khớp");
            editTextConfirmPassword.requestFocus();
            return;
        }
        SharedPreferences prefs = getSharedPreferences("UserInfo", MODE_PRIVATE);
        String userEmail = prefs.getString("email", "");
        String userToken = prefs.getString("token", "");
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setOldPassword(currentPassword);
        changePasswordDTO.setNewPassword(newPassword);
        changePasswordDTO.setEmail(userEmail);
        changePasswordDTO.setPasswordConfirm(confirmPassword);

        UserService userService = ServiceApiBuilder.buildUserApiService(UserService.class);
        Call<Void> call =  userService.changePassword(changePasswordDTO, "Bearer "+userToken);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                try {
                    if (response.code() == 400) {
                        assert response.errorBody() != null;
                        String errorBodyString = response.errorBody().string();
                        // So sánh mật khẩu hiện tại với mật khẩu ban đầu
                        if ("Old password not match!".equals(errorBodyString)) {
                            // Nếu không trùng khớp, hiển thị thông báo lỗi
                            editTextCurrentPassword.setError("Mật khẩu hiện tại không đúng");
                            editTextCurrentPassword.requestFocus();
                            return;
                        }
                    }
                    if (response.isSuccessful()){
                        // Hiển thị thông báo thành công
                        Toast.makeText(getApplicationContext(), "Đã thay đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                        // Đóng activity và trở về màn hình trước đó
                        finish();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ERROR_API", "", t);
                Toast.makeText(getApplicationContext(), "Có lỗi xảy ra, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void goBack(View view) {
        finish();
    }


}
