package com.example.appxemphim.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appxemphim.R;
import com.example.appxemphim.data.remote.ServiceApiBuilder;
import com.example.appxemphim.data.remote.TMDbApi;
import com.example.appxemphim.model.ChangePasswordDTO;
import com.example.appxemphim.model.Movie;
import com.example.appxemphim.model.MovieResponse;
import com.example.appxemphim.model.TMDBMovieResult;
import com.example.appxemphim.model.TMDBTVResult;
import com.example.appxemphim.model.TVResponse;
import com.example.appxemphim.ui.adapter.MovieAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private int userId;
    private String userName;
    private String userEmail;
    private String userToken;
    private String userAvatar;
    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private NavigationView navigationView;
    private TabLayout tabLayout;
    private static final int YOUR_REQUEST_CODE = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LanguageManager.initLanguage(this);
        recyclerView = findViewById(R.id.rcv_allcate);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo adapter với context của activity
        adapter = new MovieAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        tabLayout = findViewById(R.id.tabCategory);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Handle tab selection
                switch (tab.getPosition()) {
                    case 0:
                        fetchMovies();
                        break;
                    case 1:
                        fetchTVShows();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Handle tab unselection
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Handle tab reselection
            }
        });
        fetchMovies();
        adapter.setOnItemClickListener(new MovieAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Movie movie) {
                MovieDetailActivity.sendIntent(
                        MainActivity.this,
                        String.valueOf(movie.getId()),
                        movie.getTag(),
                        movie.getName(),
                        movie.getPosterPath());
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
            userAvatar = prefs.getString("avatar", "");
            // Gán tên người dùng vào TextView
            if (userName != null && !userName.isEmpty()) {
                txtnavName.setText(userName);
            }
            if (userEmail != null && !userEmail.isEmpty()) {
                txtnavUsername.setText(userEmail);
            }
        }

        CircleImageView imageAvatarInNav = headerView.findViewById(R.id.img_user);
        CircleImageView circleImageView = findViewById(R.id.imgAvatar);

        if (userAvatar != null && !userAvatar.isEmpty()) {
            RequestCreator requestCreatorImageAvatar = Picasso.get().load(userAvatar);
            requestCreatorImageAvatar.into(imageAvatarInNav);
            requestCreatorImageAvatar.into(circleImageView);
        } else {
            // Xử lý trường hợp userAvatar rỗng ở đây
        }

        SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intentSearch = new Intent(MainActivity.this, SearchActivity.class);
                intentSearch.putExtra("query", query);
                startActivity(intentSearch);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Gán trình nghe sự kiện cho NavigationView
        navigationView.setNavigationItemSelectedListener(item -> {
            // Xử lý sự kiện khi người dùng chọn một mục trong NavigationView
            if (item.getItemId() == R.id.nav_playlist) {
                Intent intentAllPlaylist = new Intent(MainActivity.this, PlaylistActivity.class);
                startActivity(intentAllPlaylist);
                return true; // Đánh dấu sự kiện đã được xử lý
            } else if (item.getItemId() == R.id.nav_his) {
                Intent intentAllHistory = new Intent(MainActivity.this, HistoryAllActivity.class);
                startActivity(intentAllHistory);
                return true; // Đánh dấu sự kiện đã được xử lý
            }else if (item.getItemId() == R.id.nav_profile) {
                Intent intent1 = new Intent(MainActivity.this, PersonalScreen.class);
                startActivity(intent1);
                return true; // Đánh dấu sự kiện đã được xử lý
            } else if (item.getItemId() == R.id.nav_pass) {
                Intent intent1 = new Intent(MainActivity.this, ChangePasswordActivity.class);
                startActivity(intent1);
                return true;
            }else if (item.getItemId() == R.id.nav_logout) {
                SharedPreferences.Editor editor = getSharedPreferences("UserInfo", MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();
                Intent intent1 = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent1);
                finish();
                return true; // Đánh dấu sự kiện đã được xử lý
            }
            else if (item.getItemId() == R.id.nav_setting) {
                Intent intent1 = new Intent(MainActivity.this, SettingActivity.class);
                startActivityForResult(intent1, YOUR_REQUEST_CODE); // Khởi động SettingActivity với yêu cầu trả về kết quả
                return true;
            }
            // Thêm các trường hợp khác nếu cần
            return false; // Trả về false để đánh dấu sự kiện chưa được xử lý
        });


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

        // Truyền ngôn ngữ được chọn từ SettingActivity vào phương thức getPopularMovies()
        String selectedLanguage = LanguageManager.getSelectedLanguage(this);
        Call<MovieResponse> call = tmdbApi.getPopularMovies(selectedLanguage, ServiceApiBuilder.API_KEY_TMDB);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TMDBMovieResult> movies = response.body().getResults();
                    adapter.setMovies(TMDBMovieResult.toListMovie(movies));
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
    private void fetchTVShows() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TMDbApi tmdbApi = retrofit.create(TMDbApi.class);

        // Truyền ngôn ngữ được chọn từ SettingActivity vào phương thức getPopularMovies()
        String selectedLanguage = LanguageManager.getSelectedLanguage(this);
        Call<TVResponse> call = tmdbApi.getTvShows(selectedLanguage, ServiceApiBuilder.API_KEY_TMDB);
        call.enqueue(new Callback<TVResponse>() {
            @Override
            public void onResponse(Call<TVResponse> call, Response<TVResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TMDBTVResult> movies = response.body().getResults();
                    adapter.setMovies(TMDBTVResult.toListMovie(movies));
                } else {
                    Toast.makeText(MainActivity.this, "Failed to fetch movies", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TVResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == YOUR_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getBooleanExtra("languageChanged", false)) {
                // Ngôn ngữ đã thay đổi, tái khởi động lại activity
                recreate();
            }
        }
    }
}