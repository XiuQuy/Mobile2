package com.example.appxemphim.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appxemphim.R;
import com.example.appxemphim.data.remote.ServiceApiBuilder;
import com.example.appxemphim.data.remote.UserService;
import com.example.appxemphim.model.ChangeInfoUserDTO;
import com.example.appxemphim.model.User;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChangeNameActivity extends AppCompatActivity {

    private EditText editTextNewName;
    private Button buttonSaveName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_name);

        // Ánh xạ các thành phần giao diện
        editTextNewName = findViewById(R.id.editTextNewName);
        buttonSaveName = findViewById(R.id.buttonSaveName);

        // Xử lý sự kiện khi người dùng nhấn nút "Lưu"
        buttonSaveName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveName();

            }
        });
        ImageView btnBack = findViewById(R.id.back_button);
        btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    public void goBack(View view) {
        finish();
    }

    private void saveName() {
        String newName = editTextNewName.getText().toString().trim();
        // Kiểm tra xem tên mới có rỗng không
        if (newName.isEmpty()) {
            editTextNewName.setError("Vui lòng nhập tên mới");
            editTextNewName.requestFocus();
            return;
        }
        SharedPreferences prefs = getSharedPreferences("UserInfo", MODE_PRIVATE);
        String email = prefs.getString("email", "");
        String token = prefs.getString("token", "");
        ChangeInfoUserDTO changeInfoUserDTO =  new ChangeInfoUserDTO();
        changeInfoUserDTO.setName(newName);
        changeInfoUserDTO.setEmail(email);

        UserService userService = ServiceApiBuilder.buildUserApiService(UserService.class);
        Call<User> call =  userService.changeName(changeInfoUserDTO, "Bearer "+ token);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Trả về tên mới cho activity gọi
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("new_name", response.body().getName());
                    setResult(Activity.RESULT_OK, resultIntent);

                    prefs.edit().putString("name", response.body().getName()).apply();
                    // Hiển thị thông báo thành công
                    Toast.makeText(getApplicationContext(), "Đã thay đổi tên thành công", Toast.LENGTH_SHORT).show();
                    // Đóng activity và trở về màn hình trước đó
                    finish();
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("ERROR_API", "", t);
                Toast.makeText(getApplicationContext(), "Có lỗi xảy ra, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}