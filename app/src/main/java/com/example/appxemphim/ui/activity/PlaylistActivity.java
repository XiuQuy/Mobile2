package com.example.appxemphim.ui.activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appxemphim.R;
import com.example.appxemphim.data.remote.PlayListService;
import com.example.appxemphim.data.remote.ServiceApiBuilder;
import com.example.appxemphim.model.DeleteResponse;
import com.example.appxemphim.model.Playlist;
import com.example.appxemphim.model.PlaylistItem;
import com.example.appxemphim.ui.adapter.PlayListItemAdapter;
import com.example.appxemphim.ui.adapter.PlaylistAdapter;
import com.example.appxemphim.ui.adapter.PlaylistAllAdapter;
import com.example.appxemphim.ui.adapter.PlaylistAllAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PlaylistActivity extends AppCompatActivity implements PlaylistAllAdapter.OnDeleteItemClickListener{
    private int userId;
    private String userName;
    private String userEmail;
    private String userToken;
    private PlaylistAllAdapter playlistAllAdapter;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_seemore);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        if (intent != null) {
            userName = intent.getStringExtra("userName");
            userEmail = intent.getStringExtra("userEmail");
            userId = intent.getIntExtra("userId", -1);
            userToken = intent.getStringExtra("userToken");
        }

        fetchPlaylists();

    }
    @Override
    protected void onResume() {
        super.onResume();
        // Gọi fetchPlaylists() khi activity quay lại trạng thái active
        fetchPlaylists();
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
                        fetchPlaylistItems(playlist.getId(), playlist, new PlaylistActivity.OnFetchCompletedListener() {
                            @Override
                            public void onFetchCompleted() {
                                int playlistsProcessedSoFar = playlistsProcessed.incrementAndGet();
                                if (playlistsProcessedSoFar == totalPlaylists) {
                                    // Tất cả các playlist đã được xử lý, vì vậy bạn có thể thiết lập Adapter ngay bây giờ
                                    RecyclerView recyclerView = findViewById(R.id.recycler_view_items);
                                    playlistAllAdapter = new PlaylistAllAdapter(PlaylistActivity.this, playlists, userId, userToken);
                                    playlistAllAdapter.setOnDeleteItemClickListener(PlaylistActivity.this);
                                    recyclerView.setAdapter(playlistAllAdapter);
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(PlaylistActivity.this, "Failed to fetch playlists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Playlist>> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
                Toast.makeText(PlaylistActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPlaylistItems(int watchListId, Playlist playlist, final PlaylistActivity.OnFetchCompletedListener listener) {
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
                    Toast.makeText(PlaylistActivity.this, "Failed to fetch playlist items", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PlaylistItem>> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
                Toast.makeText(PlaylistActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // Phương thức xóa mục từ Adapter
    @Override
    public void onDeleteItemClick(int position) {
        // Lấy danh sách các mục từ adapter
        List<Playlist> playlists = playlistAllAdapter.getPlaylists();

        // Kiểm tra xem vị trí có hợp lệ không
        if (position >= 0 && position < playlists.size()) {
            // Lấy đối tượng Playlist từ danh sách tại vị trí position
            Playlist playlist = playlists.get(position);

            // Lấy thông tin cần thiết từ PlaylistItem
            int watchListId = playlist.getId();
            int adapterPosition = position; // Vị trí của mục trong adapter

            // Thực hiện xóa mục và cập nhật RecyclerView
            playlists.remove(position);
            playlistAllAdapter.notifyItemRemoved(position); // Thông báo cho adapter biết một mục đã bị xóa

            // Gọi phương thức xóa từ backend
            deleteItemFromBackend(watchListId, userId, adapterPosition);
        }
    }


    // Phương thức xóa mục từ backend
    private void deleteItemFromBackend(int watchListId, int userId, int adapterPosition) {
        // Gọi API để xóa từ backend
        PlayListService tmdbApi = ServiceApiBuilder.buildUserApiService(PlayListService.class);
        Call<DeleteResponse> call = tmdbApi.deleteonePlaylist(watchListId, userId, "Bearer " + userToken);

        call.enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                if (response.isSuccessful()) {
                    // Xử lý phản hồi từ backend
                    DeleteResponse deleteResponse = response.body();
                    if (deleteResponse != null && deleteResponse.isSuccess()) {
                        // Xóa thành công từ backend
                        // Chỉ cần thông báo cho adapter biết rằng một mục đã bị xóa
                        playlistAllAdapter.notifyItemRemoved(adapterPosition);
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

