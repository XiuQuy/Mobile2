package com.example.appxemphim.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.appxemphim.R;


public class AccountActivity extends AppCompatActivity {
    private TextView textViewUsername;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_CODE_CHANGE_NAME = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        // Nhận thông tin tài khoản từ Intent hoặc SharedPreferences hoặc bất kỳ nguồn dữ liệu nào khác
        String username = "MaiAnh";
        String email = "maianh123@gmail.com";
        String password = "123456";


        // Hiển thị thông tin tài khoản trên TextViews
        textViewUsername = findViewById(R.id.textViewUsername);
        TextView textViewEmail = findViewById(R.id.textViewEmail);

        textViewUsername.setText("Username: " + username);
        textViewEmail.setText("Email: " + email);

        // Xử lý sự kiện khi người dùng nhấn vào nút đăng xuất
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //chuyển về màn hình đăng nhập
//                startActivity(new Intent(AccountActivity.this, LoginActivity.class));
//                finish();
            }
        });

        // Xử lý sự kiện khi người dùng nhấn vào nút đổi mật khẩu
        Button btnChangePassword = findViewById(R.id.btnChangePassword);
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Thực hiện chuyển sang màn hình đổi mật khẩu
                Intent intent = new Intent(AccountActivity.this, ChangePasswordActivity.class);
                intent.putExtra("temp_password", password);
                startActivity(intent);
            }
        });

        // Xử lý sự kiện khi người dùng nhấn vào nút đổi tên
        Button btnChangeName = findViewById(R.id.btnChangeName);
        btnChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Thực hiện chuyển sang màn hình đổi tên
                Intent intent = new Intent(AccountActivity.this, ChangeNameActivity.class);
                startActivityForResult(intent, REQUEST_CODE_CHANGE_NAME);

            }
        });

        // Xử lý sự kiện khi người dùng nhấn vào nút đổi avatar
        Button btnChangeAvatar = findViewById(R.id.btnChangeAvatar);
        btnChangeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Thực hiện chọn hình ảnh mới từ bộ nhớ hoặc máy ảnh
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Lấy địa chỉ URI của hình ảnh đã chọn
            Uri uri = data.getData();

            // Hiển thị hình ảnh đã chọn lên ImageView
            ImageView imageViewAvatar = findViewById(R.id.imageViewAvatar);
            imageViewAvatar.setImageURI(uri);
            Toast.makeText(AccountActivity.this, "Đã thay đổi hình ảnh thành công", Toast.LENGTH_SHORT).show();
        }

        if (requestCode == REQUEST_CODE_CHANGE_NAME && resultCode == Activity.RESULT_OK && data != null) {
            // Nhận tên mới từ Intent và cập nhật giao diện
            String newName = data.getStringExtra("new_name");
            if (newName != null) {
                textViewUsername.setText("Username: " + newName);
            }
        }
    }





}
