package com.example.appxemphim.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appxemphim.R;
import com.example.appxemphim.data.remote.TMDbApi;
import com.example.appxemphim.model.Movie;
import com.example.appxemphim.model.MovieResponse;
import com.example.appxemphim.ui.adapter.MovieAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


import android.content.SharedPreferences;
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
    private static final String API_KEY = "9a169454f96888fb8284d35eb3042308";

    private RecyclerView recyclerView;
    private MovieAdapter adapter;

    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rcv_allcate);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo adapter với context của activity
        adapter = new MovieAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        // Gọi API và cập nhật danh sách phim
        fetchMovies();
        adapter.setOnItemClickListener(new MovieAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Movie movie) {
                Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
                intent.putExtra("MOVIE", movie);
                startActivity(intent);
            }
        });
      
        navigationView = findViewById(R.id.nav_user);

        // Lấy headerView của NavigationView
        View headerView = navigationView.getHeaderView(0);

        // Tham chiếu đến TextView trong headerView
        TextView txtnavName = headerView.findViewById(R.id.txtNavName);
        TextView txtnavUsername = headerView.findViewById(R.id.txtUserName);

        // Nhận thông tin người dùng từ Intent
        Intent intent = getIntent();
        if (intent != null) {
            SharedPreferences prefs = getSharedPreferences("UserInfo", MODE_PRIVATE);
            userName = prefs.getString("name", "");
            userEmail = prefs.getString("email", "");
            userId = prefs.getInt("userId", -1);
            userToken = prefs.getString("token", "");
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

    private void fetchMovies() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TMDbApi tmdbApi = retrofit.create(TMDbApi.class);

        Call<MovieResponse> call = tmdbApi.getPopularMovies(API_KEY);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body().getResults();
                    adapter.setMovies(movies);
                } else {
                    Toast.makeText(MainActivity.this, "Failed to fetch movies", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
        
