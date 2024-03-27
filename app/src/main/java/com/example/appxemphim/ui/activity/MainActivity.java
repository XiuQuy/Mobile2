package com.example.appxemphim.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appxemphim.R;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        navigationView = findViewById(R.id.nav_user);

        // Gán trình nghe sự kiện cho NavigationView
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Xử lý sự kiện khi người dùng chọn một mục trong NavigationView
                if (item.getItemId() == R.id.nav_pass) {
                    // Xử lý khi người dùng chọn mục có ID là nav_pass
                    Toast.makeText(MainActivity.this, "Navigation Pass Item Clicked", Toast.LENGTH_SHORT).show();

                    return true; // Đánh dấu sự kiện đã được xử lý
                }
                // Thêm các trường hợp khác nếu cần
                return false; // Trả về false để đánh dấu sự kiện chưa được xử lý
            }
        });
    }

}