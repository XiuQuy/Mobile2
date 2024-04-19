package com.example.appxemphim.ui.adapter;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appxemphim.R;
import com.example.appxemphim.data.remote.HistoryService;
import com.example.appxemphim.data.remote.ServiceApiBuilder;
import com.example.appxemphim.model.DeleteResponse;
import com.example.appxemphim.model.History;
import com.example.appxemphim.model.History;
import com.example.appxemphim.model.PlaylistItem;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryAllAdapter extends RecyclerView.Adapter<HistoryAllAdapter.MyViewHolder>{
    private Context context;
    private List<History> listHistory;
    private OnDeleteItemClickListener deleteItemClickListener;

    public interface OnDeleteItemClickListener {
        void onDeleteItemClick(int position);
    }
    public HistoryAllAdapter(Context context, List<History> listHistory) {
        this.context = context;
        this.listHistory = listHistory;
    }
    public void setOnDeleteItemClickListener(OnDeleteItemClickListener listener) {
        this.deleteItemClickListener = listener;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_history_item_seemore, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(view);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryAllAdapter.MyViewHolder holder, int position) {
        History history = listHistory.get(position);
        holder.bind(history);

        // Gắn sự kiện onClick cho menu_button
        holder.getMenu_button().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Gọi phương thức showMenuForItem và chuyển anchorView là menu_button và vị trí từ holder
                showMenuForItem(view, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listHistory.size();
    }

    public List<History> getListHistory() {
        return listHistory;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView output_title;
        private TextView output_tag;
        private TextView output_time;
        private AppCompatImageView output_image;
        private ProgressBar output_progress;
        private ImageView menu_button;


        public MyViewHolder(View itemView) {
            super(itemView);
            output_title = itemView.findViewById(R.id.titleTextView);
            output_tag = itemView.findViewById(R.id.tagTextView);
            output_image = itemView.findViewById(R.id.item_image);
            output_time = itemView.findViewById(R.id.timeTextView);
            output_progress = itemView.findViewById(R.id.progressBar);
            menu_button = itemView.findViewById(R.id.menu_button);
        }

        public void bind(History history) {
            output_title.setText(history.getInformationMovie().getTitle());
            output_tag.setText(history.getInformationMovie().getTag());
            // Load image using Picasso or any other image loading library
            String imageUrl = history.getInformationMovie().getImageLink();
            Picasso.get().load(imageUrl).into(output_image);

            String tag = history.getInformationMovie().getTag();
            if ("YOUTUBE".equals(tag)) {
                int durationsInSeconds = history.getInformationMovie().getDurations();
                String formattedDuration = formatDuration(durationsInSeconds);
                output_time.setText(formattedDuration);
                output_time.setPadding(10,0,10,0);
                int secondsCount = history.getSecondsCount();
                // Tính phần trăm tiến trình
                int progress = (int) ((float) secondsCount / durationsInSeconds * 100);

                // Xử lý trường hợp khi progress vượt quá 100
                progress = Math.min(progress, 100);

                // Cập nhật giá trị của ProgressBar
                output_progress.setProgress(progress);

            } else {
                output_time.setText(""); // Nếu không phải là YOUTUBE, không hiển thị thời lượng
                output_progress.setProgress(100);
            }
        }
        public  ImageView getMenu_button(){
            return menu_button;
        }
        
    }

    public String formatDuration(int durationInSeconds) {
        long hours = TimeUnit.SECONDS.toHours(durationInSeconds);
        long minutes = TimeUnit.SECONDS.toMinutes(durationInSeconds) % 60;
        long seconds = durationInSeconds % 60;

        StringBuilder formattedDuration = new StringBuilder();

        if (hours > 0) {
            formattedDuration.append(String.format("%02d:", hours));
        }
        if (minutes > 0 || hours > 0) {
            formattedDuration.append(String.format("%02d:", minutes));
        }
        formattedDuration.append(String.format("%02d", seconds));

        return formattedDuration.toString();
    }
    private void showMenuForItem(View anchorView, int position) {
        PopupMenu popupMenu = new PopupMenu(context, anchorView);
        popupMenu.inflate(R.menu.vertical_menu);

        // Lấy History tại vị trí position
        History historyItem = listHistory.get(position);

        // Lấy id từ History
        int itemId = historyItem.getId();

        // Đăng ký một trình xử lý sự kiện cho mỗi mục menu
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_delete) {
                    // Hiển thị thông báo AlertDialog xác nhận trước khi xóa
                    showDeleteConfirmationDialog(itemId, position);
                    return true;
                } else {
                    return false;
                }
            }
        });

        // Hiển thị menu
        popupMenu.show();
    }

    private void showDeleteConfirmationDialog(int itemId, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc muốn xóa item với ID: " + itemId + " không?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Nếu người dùng chọn "Có", gọi phương thức onDeleteItemClick() của listener
                if (deleteItemClickListener != null) {
                    deleteItemClickListener.onDeleteItemClick(position);
                }
            }
        });
        builder.setNegativeButton("Không", null);
        builder.show();
    }
}
