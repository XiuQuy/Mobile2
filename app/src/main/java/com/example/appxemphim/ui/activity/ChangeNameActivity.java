package com.example.appxemphim.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appxemphim.R;


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
    }


    private void saveName() {
        String newName = editTextNewName.getText().toString().trim();
        // Kiểm tra xem tên mới có rỗng không
        if (newName.isEmpty()) {
            editTextNewName.setError("Vui lòng nhập tên mới");
            editTextNewName.requestFocus();
            return;
        }


        // Trả về tên mới cho activity gọi
        Intent resultIntent = new Intent();
        resultIntent.putExtra("new_name", newName);
        setResult(Activity.RESULT_OK, resultIntent);

        // Hiển thị thông báo thành công
        Toast.makeText(getApplicationContext(), "Đã thay đổi tên thành công", Toast.LENGTH_SHORT).show();

        // Đóng activity và trở về màn hình trước đó
        finish();
    }
    public void goBack(View view) {
        finish();
    }
}