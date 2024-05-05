package com.example.appxemphim.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.appxemphim.R;
import com.example.appxemphim.data.remote.HistoryService;
import com.example.appxemphim.data.remote.PlaylistService;
import com.example.appxemphim.data.remote.ServiceApiBuilder;
import com.example.appxemphim.model.History;
import com.example.appxemphim.model.Playlist;
import com.example.appxemphim.model.PlaylistItem;
import com.example.appxemphim.ui.adapter.HistoryAdapterPersonal;
import com.example.appxemphim.ui.adapter.PlaylistAdapterPersonal;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PersonalScreen extends AppCompatActivity {

    private int userId;
    private String userName;
    private String userEmail;
    private String userToken;
    private TextView txtUsername;
    private CircleImageView imageViewAvatar;
    private TextView txtName;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_CODE_CHANGE_NAME = 1;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        txtName = findViewById(R.id.name_label);
        txtUsername = findViewById(R.id.email_label);
        imageViewAvatar = findViewById(R.id.imgAvatar);

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
                txtName.setText(userName);
            }
            if (userEmail != null && !userEmail.isEmpty()) {
                txtUsername.setText(userEmail);
            }
        }
        Button btnSeeMorePlaylist = findViewById(R.id.btn_view_all_playlist);
        btnSeeMorePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Điều hướng người dùng đến màn hình đăng ký
                Intent intent = new Intent(PersonalScreen.this, PlaylistActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("userName", userName);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("userToken", userToken);
                startActivity(intent);
            }
        });

        Button btnSeeMoreHistory = findViewById(R.id.btn_view_all_history);
        btnSeeMoreHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Điều hướng người dùng đến màn hình đăng ký
                Intent intent = new Intent(PersonalScreen.this, HistoryAllActivity.class);
                startActivity(intent);
            }
        });
        // Lấy dữ liệu từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserInfo", MODE_PRIVATE);
        String avatarUrl = prefs.getString("avatar", "");

        // Sử dụng Glide để tải hình ảnhgit
        Glide.with(this)
                .load(avatarUrl) // Load từ URL lấy từ SharedPreferences
                .apply(new RequestOptions().placeholder(R.drawable.placeholder_img_load))
                .into(imageViewAvatar);

        // hiển thị danh sách lịch sử, danh sách phát
        fetchHistories();
        fetchPlaylists();

        // Xử lý sự kiện khi người dùng nhấn vào nút đổi mật khẩu
        Button btnChangePassword = findViewById(R.id.btnChangePassword);
        String tagSocialNetwork = prefs.getString("tagSocialNetwork", "");

        // ẩn nút đổi mật khẩu khi người dùng đăng nhập bằng facebook hoặc google
        if(tagSocialNetwork.equals("FACEBOOK") || tagSocialNetwork.equals("GOOGLE")){
            btnChangePassword.setVisibility(View.GONE);
        }

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Thực hiện chuyển sang màn hình đổi mật khẩu
                Intent intent = new Intent(PersonalScreen.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        // Xử lý sự kiện khi người dùng nhấn vào nút đổi tên
        Button btnChangeName = findViewById(R.id.btnChangeName);
        btnChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Thực hiện chuyển sang màn hình đổi tên
                Intent intent = new Intent(PersonalScreen.this, ChangeNameActivity.class);
                startActivityForResult(intent, REQUEST_CODE_CHANGE_NAME);
            }
        });

        // Xử lý sự kiện khi người dùng nhấn vào nút đổi avatar
        Button btnChangeAvatar = findViewById(R.id.btnChangeAvatar);
        btnChangeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalScreen.this, ChangeAvatarActivity.class);
                startActivity(intent);
            }
        });

    }

    public void goBack(View view) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Load ảnh đại diện từ SharedPreferences và hiển thị nó lên ImageView
            SharedPreferences prefs = getSharedPreferences("UserInfo", MODE_PRIVATE);
            String avatarUrl = prefs.getString("avatar", "");

            // Sử dụng Glide để hiển thị ảnh đại diện
            Glide.with(this)
                    .load(avatarUrl)
                    .placeholder(R.drawable.placeholder_img_load) // Placeholder nếu đường dẫn ảnh trống hoặc không hợp lệ
                    .into(imageViewAvatar);
        }

        if (requestCode == REQUEST_CODE_CHANGE_NAME && resultCode == Activity.RESULT_OK && data != null) {
            // Nhận tên mới từ Intent và cập nhật giao diện
            String newName = data.getStringExtra("new_name");
            if (newName != null) {
                txtName.setText(newName); // Cập nhật tên mới trên giao diện
                // Cập nhật tên mới trong SharedPreferences
                SharedPreferences.Editor editor = getSharedPreferences("UserInfo", MODE_PRIVATE).edit();
                editor.putString("name", newName);
                editor.apply();
            }
        }
        Button btnViewAllHistory = findViewById(R.id.btn_view_all_history);
        btnViewAllHistory.setOnClickListener(v -> {
            Intent intentAllHistory = new Intent(PersonalScreen.this, HistoryAllActivity.class);
            startActivity(intentAllHistory);
        });
        Button btnViewAllPlaylist = findViewById(R.id.btn_view_all_playlist);
        btnViewAllPlaylist.setOnClickListener(v -> {
            Intent intentAllPlaylist = new Intent(PersonalScreen.this, PlaylistActivity.class);
            startActivity(intentAllPlaylist);
        });
    }

    private void fetchHistories() {

        HistoryService tmdbApi = ServiceApiBuilder.buildUserApiService(HistoryService.class);

        Call<List<History>> call = tmdbApi.getHistory(5, userId, "Bearer " + userToken);


        call.enqueue(new Callback<List<History>>() {
            @Override
            public void onResponse(Call<List<History>> call, Response<List<History>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<History> movies = response.body();
                    RecyclerView recyclerView = findViewById(R.id.recycler_view_history);
                    recyclerView.setAdapter(new HistoryAdapterPersonal(PersonalScreen.this, movies));
                } else {
                    Toast.makeText(PersonalScreen.this, "Failed to fetch movies", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<History>> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
                Toast.makeText(PersonalScreen.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    interface OnFetchCompletedListener {
        void onFetchCompleted();
    }

    private void fetchPlaylists() {
        PlaylistService tmdbApi = ServiceApiBuilder.buildUserApiService(PlaylistService.class);

        Call<List<Playlist>> call = tmdbApi.getPlaylist(5, userId, "Bearer " + userToken);


        call.enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Call<List<Playlist>> call, Response<List<Playlist>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    final List<Playlist> playlists = response.body();
                    final int totalPlaylists = playlists.size();
                    final AtomicInteger playlistsProcessed = new AtomicInteger(0);

                    for (Playlist playlist : playlists) {
                        fetchPlaylistItems(playlist.getId(), playlist, new OnFetchCompletedListener() {
                            @Override
                            public void onFetchCompleted() {
                                int playlistsProcessedSoFar = playlistsProcessed.incrementAndGet();
                                if (playlistsProcessedSoFar == totalPlaylists) {
                                    // Tất cả các playlist đã được xử lý, vì vậy bạn có thể thiết lập Adapter ngay bây giờ
                                    RecyclerView recyclerView = findViewById(R.id.recycler_view_playlist);
                                    recyclerView.setAdapter(new PlaylistAdapterPersonal(PersonalScreen.this, playlists));
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(PersonalScreen.this, "Failed to fetch playlists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Playlist>> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
                Toast.makeText(PersonalScreen.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPlaylistItems(int watchListId, Playlist playlist, final OnFetchCompletedListener listener) {
        PlaylistService playlistItemService = ServiceApiBuilder.buildUserApiService(PlaylistService.class);

        Call<List<PlaylistItem>> call = playlistItemService.getPlaylistItem(1, watchListId, userId, "Bearer " + userToken);
        call.enqueue(new Callback<List<PlaylistItem>>() {
            @Override
            public void onResponse(Call<List<PlaylistItem>> call, Response<List<PlaylistItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PlaylistItem> playlistItems = response.body();
                    // Kiểm tra xem playlistItems của playlist có null hay không trước khi thêm vào
                    if (playlist.getPlaylistItems() == null) {
                        // Nếu null, khởi tạo danh sách mới
                        playlist.setPlaylistItems(new ArrayList<PlaylistItem>());
                    }
                    // Thêm danh sách mục mới vào danh sách phát
                    playlist.getPlaylistItems().addAll(playlistItems);
                    // Gọi listener để thông báo rằng việc lấy danh sách mục đã hoàn tất
                    listener.onFetchCompleted();
                } else {
                    Toast.makeText(PersonalScreen.this, "Failed to fetch playlist items", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PlaylistItem>> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
                Toast.makeText(PersonalScreen.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
