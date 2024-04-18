package com.example.appxemphim.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.appcompat.app.AppCompatActivity;



public class PersonalScreen extends AppCompatActivity {
    private TextView textViewUsername;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_CODE_CHANGE_NAME = 1;
    private int userId;
    private String userName;
    private String userEmail;
    private String userToken;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        // Nhận thông tin tài khoản SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserInfo", MODE_PRIVATE);
        String username = prefs.getString("name", "");
        String email = prefs.getString("email", "");

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
                Intent intent = new Intent(PersonalScreen.this, ChangePasswordActivity.class);
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
                Intent intent = new Intent(PersonalScreen.this, ChangeNameActivity.class);
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
        TextView txtName = findViewById(R.id.name_label);
        TextView txtUsername = findViewById(R.id.email_label);

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

        fetchHistories();
        fetchPlaylists();
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
            Toast.makeText(PersonalScreen.this, "Đã thay đổi hình ảnh thành công", Toast.LENGTH_SHORT).show();
        }

        if (requestCode == REQUEST_CODE_CHANGE_NAME && resultCode == Activity.RESULT_OK && data != null) {
            // Nhận tên mới từ Intent và cập nhật giao diện
            String newName = data.getStringExtra("new_name");
            if (newName != null) {
                textViewUsername.setText("Username: " + newName);
            }
        }
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
