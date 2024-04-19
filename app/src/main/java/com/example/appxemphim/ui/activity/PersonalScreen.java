package com.example.appxemphim.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appxemphim.R;
import com.example.appxemphim.data.remote.HistoryService;
import com.example.appxemphim.data.remote.PlayListService;
import com.example.appxemphim.data.remote.ServiceApiBuilder;
import com.example.appxemphim.model.History;
import com.example.appxemphim.model.Playlist;
import com.example.appxemphim.model.PlaylistItem;
import com.example.appxemphim.ui.adapter.HistoryAdapter;
import com.example.appxemphim.ui.adapter.PlaylistAdapter;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PersonalScreen extends AppCompatActivity {

    private int userId;
    private String userName;
    private String userEmail;
    private String userToken;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        TextView txtName = findViewById(R.id.name_label);
        TextView txtUsername = findViewById(R.id.email_label);

        // Nhận thông tin người dùng từ Intent
        Intent intent = getIntent();
        if (intent != null) {
            userName = intent.getStringExtra("userName");
            userEmail = intent.getStringExtra("userEmail");
            userId = intent.getIntExtra("userId", -1);
            userToken = intent.getStringExtra("userToken");
            // Gán tên người dùng vào TextView
            if (userName != null && !userName.isEmpty()) {
                txtName.setText(userName);
            }
            if (userEmail != null && !userEmail.isEmpty()) {
                txtUsername.setText(userEmail);
            }
        }

        Button btnSeeMorePlaylist = findViewById(R.id.btnSeeMorePlaylist);
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

        Button btnSeeMoreHistory = findViewById(R.id.btnSeeMoreHistory);
        btnSeeMoreHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Điều hướng người dùng đến màn hình đăng ký
                Intent intent = new Intent(PersonalScreen.this, HistoryActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("userName", userName);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("userToken", userToken);
                startActivity(intent);
            }
        });

        fetchHistories();
        fetchPlaylists();
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Gọi fetch() khi activity quay lại trạng thái active
        fetchHistories();
        fetchPlaylists();
    }
    private void fetchHistories() {

        HistoryService tmdbApi = ServiceApiBuilder.buildUserApiService(HistoryService.class);

        Call<List<History>> call = tmdbApi.getNewestHistory(5, userId, "Bearer " + userToken);

        call.enqueue(new Callback<List<History>>() {
            @Override
            public void onResponse(Call<List<History>> call, Response<List<History>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<History> movies = response.body();
                    RecyclerView recyclerView = findViewById(R.id.recycler_view_history);
                    recyclerView.setAdapter(new HistoryAdapter(PersonalScreen.this, movies));
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
        PlayListService tmdbApi = ServiceApiBuilder.buildUserApiService(PlayListService.class);

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
                                    recyclerView.setAdapter(new PlaylistAdapter(PersonalScreen.this, playlists));
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
        PlayListService playlistItemService = ServiceApiBuilder.buildUserApiService(PlayListService.class);

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
