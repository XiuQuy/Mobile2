package com.example.appxemphim.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.appxemphim.R;
import com.google.android.material.navigation.NavigationView;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    // Khai báo biến để lưu thông tin người dùng
    private int userId;
    private String userName;
    private String userEmail;
    private String userToken;

    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        navigationView = findViewById(R.id.nav_user);

        // Lấy headerView của NavigationView
        View headerView = navigationView.getHeaderView(0);

        // Tham chiếu đến TextView trong headerView
        TextView txtnavName = headerView.findViewById(R.id.txtNavName);
        TextView txtnavUsername = headerView.findViewById(R.id.txtUserName);

        // Nhận thông tin người dùng từ Intent
        Intent intent = getIntent();
        if (intent != null) {
            userName = intent.getStringExtra("userName");
            userEmail = intent.getStringExtra("userEmail");
            userId = intent.getIntExtra("userId", -1);
            userToken = intent.getStringExtra("userToken");
            // Gán tên người dùng vào TextView
            if (userName != null && !userName.isEmpty()) {
                txtnavName.setText(userName);
            }
            if (userEmail != null && !userEmail.isEmpty()) {
                txtnavUsername.setText(userEmail);
            }
        }


        // Gán trình nghe sự kiện cho NavigationView
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Xử lý sự kiện khi người dùng chọn một mục trong NavigationView
                if (item.getItemId() == R.id.nav_profile) {
                    // Mở màn hình profile
                    Intent intent = new Intent(MainActivity.this, PersonalScreen.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("userName", userName);
                    intent.putExtra("userEmail", userEmail);
                    intent.putExtra("userToken", userToken);
                    startActivity(intent);
                    return true; // Đánh dấu sự kiện đã được xử lý
                } else if (item.getItemId() == R.id.nav_pass) {
                    // Hiển thị Toast cho mục nav_pass
                    Toast.makeText(MainActivity.this, "Navigation Pass Item Clicked", Toast.LENGTH_SHORT).show();
                    return true; // Đánh dấu sự kiện đã được xử lý
                }

                // Thêm các trường hợp khác nếu cần

                return false; // Trả về false để đánh dấu sự kiện chưa được xử lý
            }
        });

        CircleImageView circleImageView = findViewById(R.id.imgAvatar);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở NavigationView từ bên phải
                if (drawerLayout != null) {
                    drawerLayout.openDrawer(GravityCompat.END);
                }
            }
        });

    }



}