package com.example.appxemphim;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class ChangePasswordActivity extends AppCompatActivity {

    private EditText editTextCurrentPassword, editTextNewPassword, editTextConfirmPassword;
    private Button buttonChangePassword;
    private String tempPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        // Lấy dữ liệu từ Intent
        tempPassword = getIntent().getStringExtra("temp_password");
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
    }

    // Phương thức để thay đổi mật khẩu
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

        // So sánh mật khẩu hiện tại với mật khẩu ban đầu
        if (!currentPassword.equals(tempPassword)) {
            // Nếu không trùng khớp, hiển thị thông báo lỗi
            editTextCurrentPassword.setError("Mật khẩu hiện tại không đúng");
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
        // Hiển thị thông báo thành công
        Toast.makeText(getApplicationContext(), "Đã thay đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();

        // Đóng activity và trở về màn hình trước đó
        finish();
    }
    public void goBack(View view) {
        finish();
    }
}
