package com.example.appxemphim.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.appxemphim.R;
import com.example.appxemphim.data.remote.ServiceApiBuilder;
import com.example.appxemphim.data.remote.UserService;
import com.example.appxemphim.model.ForgotDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText newPasswordEditText;
    private EditText confirmPasswordEditText;
    private Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        resetButton = findViewById(R.id.confirmButton);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = newPasswordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();
                String email = getIntent().getStringExtra("email");
                int code = getIntent().getIntExtra("code", 0); // 0 là giá trị mặc định nếu không tìm thấy key
                if (TextUtils.isEmpty(newPassword)) {
                    newPasswordEditText.setError("Vui lòng nhập mật khẩu mới");
                    newPasswordEditText.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(confirmPassword)) {
                    confirmPasswordEditText.setError("Vui lòng xác nhận mật khẩu");
                    confirmPasswordEditText.requestFocus();
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    confirmPasswordEditText.setError("Mật khẩu không khớp");
                    confirmPasswordEditText.requestFocus();
                    return;
                }


                changePassword(code, email, newPassword, confirmPassword);
            }
        });
    }



    public void goBack(View view) {
        finish();
    }
    private void changePassword(int code, String email, String password, String passwordConfirm) {
        ForgotDTO forgotDTO = new ForgotDTO(code, email, password, passwordConfirm);

        UserService userService = ServiceApiBuilder.buildUserApiService(UserService.class);
        Call<Void> call = userService.forgotPasswordChange(forgotDTO);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ResetPasswordActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Không thể đổi mật khẩu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ResetPasswordActivity.this, "Có lỗi xảy ra, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
