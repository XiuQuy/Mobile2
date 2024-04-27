package com.example.appxemphim.ui.activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appxemphim.R;
import com.example.appxemphim.data.remote.PlaylistService;
import com.example.appxemphim.data.remote.ServiceApiBuilder;
import com.example.appxemphim.model.DeleteResponse;
import com.example.appxemphim.model.PlaylistItem;
import com.example.appxemphim.ui.adapter.PlayListItemAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PlayListItemActivity extends AppCompatActivity implements PlayListItemAdapter.OnDeleteItemClickListener {
    private int userId;
    private String userName;
    private String userEmail;
    private String userToken;
    private int playlistId;
    private PlayListItemAdapter playListItemAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_seemore);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        if (intent != null) {
            SharedPreferences prefs = getSharedPreferences("UserInfo", MODE_PRIVATE);
            userName = prefs.getString("name", "");
            userEmail = prefs.getString("email", "");
            userId = prefs.getInt("userId", -1);
            userToken = prefs.getString("token", "");
            playlistId = getIntent().getIntExtra("playlistId", -1);
        }

        fetchPlayListItem();
        ImageView btnBack = findViewById(R.id.back_button);
        btnBack.setOnClickListener(v -> {
            finish();
        });
    }


    private void fetchPlayListItem() {
        PlaylistService tmdbApi = ServiceApiBuilder.buildUserApiService(PlaylistService.class);

        Call<List<PlaylistItem>> call = tmdbApi.getAllPlaylistItem(playlistId, userId, "Bearer " + userToken);
        call.enqueue(new Callback<List<PlaylistItem>>() {
            @Override
            public void onResponse(Call<List<PlaylistItem>> call, Response<List<PlaylistItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PlaylistItem> movies = response.body();
                    RecyclerView recyclerView = findViewById(R.id.recycler_view_items);
                    playListItemAdapter = new PlayListItemAdapter(PlayListItemActivity.this, movies);
                    // Thiết lập listener để nhận sự kiện xóa từ Adapter
                    playListItemAdapter.setOnDeleteItemClickListener(PlayListItemActivity.this);
                    recyclerView.setAdapter(playListItemAdapter);

                } else {
                    Toast.makeText(PlayListItemActivity.this, "Failed to fetch movies", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PlaylistItem>> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
                Toast.makeText(PlayListItemActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Phương thức xóa mục từ Adapter
    @Override
    public void onDeleteItemClick(int position) {
        // Lấy danh sách các mục từ adapter
        List<PlaylistItem> playListItems = playListItemAdapter.getPlayListItems();

        // Kiểm tra xem vị trí có hợp lệ không
        if (position >= 0 && position < playListItems.size()) {
            // Lấy đối tượng PlaylistItem từ danh sách tại vị trí position
            PlaylistItem playlistItem = playListItems.get(position);

            // Lấy thông tin cần thiết từ PlaylistItem
            int watchListItemId = playlistItem.getId();
            int adapterPosition = position; // Vị trí của mục trong adapter

            // Thực hiện xóa mục và cập nhật RecyclerView
            playListItems.remove(position);
            //playListItemAdapter.notifyItemRemoved(position); // Thông báo cho adapter biết một mục đã bị xóa

            // Gọi phương thức xóa từ backend
            deleteItemFromBackend(watchListItemId, userId, adapterPosition);
        }
    }


    // Phương thức xóa mục từ backend
    private void deleteItemFromBackend(int watchListItemId, int userId, int adapterPosition) {
        // Gọi API để xóa từ backend
        PlaylistService tmdbApi = ServiceApiBuilder.buildUserApiService(PlaylistService.class);
        Call<DeleteResponse> call = tmdbApi.deleteOnePlaylistItem(watchListItemId, userId, "Bearer " + userToken);

        call.enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                if (response.isSuccessful()) {
                    // Xử lý phản hồi từ backend
                    DeleteResponse deleteResponse = response.body();
                    if (deleteResponse != null && deleteResponse.isSuccess()) {
                        // Xóa thành công từ backend
                        // Chỉ cần thông báo cho adapter biết rằng một mục đã bị xóa
                        playListItemAdapter.notifyItemRemoved(adapterPosition);
                    } else {
                        // Xử lý trường hợp không thành công từ backend
                        Log.e(TAG, "Delete request failed: API response indicated failure.");
                        // Hiển thị thông báo lỗi cho người dùng nếu cần
                    }
                } else {
                    // Xử lý trường hợp không thành công từ backend
                    Log.e(TAG, "Delete request failed: " + response.message());
                    // Hiển thị thông báo lỗi cho người dùng nếu cần
                }
            }

            @Override
            public void onFailure(Call<DeleteResponse> call, Throwable t) {
                // Xử lý lỗi nếu có
                Log.e(TAG, "Delete request failed: " + t.getMessage());
                // Hiển thị thông báo lỗi cho người dùng nếu cần
            }
        });
    }
}
