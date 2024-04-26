package com.example.appxemphim.ui.activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appxemphim.R;
import com.example.appxemphim.data.remote.HistoryService;
import com.example.appxemphim.data.remote.ServiceApiBuilder;
import com.example.appxemphim.model.DeleteResponse;
import com.example.appxemphim.model.History;
import com.example.appxemphim.ui.adapter.HistoryAllAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryAllActivity extends AppCompatActivity implements HistoryAllAdapter.OnDeleteItemClickListener{
    private int userId;
    private String userName;
    private String userEmail;
    private String userToken;
    private HistoryAllAdapter historyAllAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_seemore);
        // Nhận thông tin người dùng từ Intent
        Intent intent = getIntent();
        if (intent != null) {
            userName = intent.getStringExtra("userName");
            userEmail = intent.getStringExtra("userEmail");
            userId = intent.getIntExtra("userId", -1);
            userToken = intent.getStringExtra("userToken");
        }
        RecyclerView recyclerView = findViewById(R.id.recycler_view_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fetchHistories();
    }

    private void fetchHistories() {

        HistoryService tmdbApi = ServiceApiBuilder.buildUserApiService(HistoryService.class);

        Call<List<History>> call = tmdbApi.getAllHistory(userId,"Bearer " + userToken);

        call.enqueue(new Callback<List<History>>() {
            @Override
            public void onResponse(Call<List<History>> call, Response<List<History>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<History> movies = response.body();
                    RecyclerView recyclerView = findViewById(R.id.recycler_view_items);
                    historyAllAdapter = new HistoryAllAdapter(HistoryAllActivity.this, movies);
                    historyAllAdapter.setOnDeleteItemClickListener(HistoryAllActivity.this);
                    recyclerView.setAdapter(historyAllAdapter);
                } else {
                    Toast.makeText(HistoryAllActivity.this, "Failed to fetch movies", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<History>> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
                Toast.makeText(HistoryAllActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onDeleteItemClick(int position) {
        // Lấy danh sách các mục từ adapter
        List<History> histories = historyAllAdapter.getListHistory();

        // Kiểm tra xem vị trí có hợp lệ không
        if (position >= 0 && position < histories.size()) {
            // Lấy đối tượng history từ danh sách tại vị trí position
            History history = histories.get(position);

            // Lấy thông tin cần thiết từ History
            int historyId = history.getId();
            int adapterPosition = position; // Vị trí của mục trong adapter

            // Thực hiện xóa mục và cập nhật RecyclerView
            histories.remove(position);
            historyAllAdapter.notifyItemRemoved(position); // Thông báo cho adapter biết một mục đã bị xóa

            // Gọi phương thức xóa từ backend
            deleteItemFromBackend(historyId, userId, adapterPosition);
        }
    }


    // Phương thức xóa mục từ backend
    private void deleteItemFromBackend(int historyId, int userId, int adapterPosition) {
        // Gọi API để xóa từ backend
        HistoryService tmdbApi = ServiceApiBuilder.buildUserApiService(HistoryService.class);
        Call<DeleteResponse> call = tmdbApi.deleteOneHistory(historyId, userId, "Bearer " + userToken);

        call.enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                if (response.isSuccessful()) {
                    // Xử lý phản hồi từ backend
                    DeleteResponse deleteResponse = response.body();
                    if (deleteResponse != null && deleteResponse.isSuccess()) {
                        // Xóa thành công từ backend
                        // Chỉ cần thông báo cho adapter biết rằng một mục đã bị xóa
                        historyAllAdapter.notifyItemRemoved(adapterPosition);
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