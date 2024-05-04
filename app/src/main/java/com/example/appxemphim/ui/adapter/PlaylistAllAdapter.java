package com.example.appxemphim.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appxemphim.R;
import com.example.appxemphim.model.InformationMovie;
import com.example.appxemphim.model.Playlist;
import com.example.appxemphim.model.PlaylistItem;
import com.example.appxemphim.ui.activity.PlayListItemActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PlaylistAllAdapter extends RecyclerView.Adapter<PlaylistAllAdapter.MyViewHolder> {
    private Context context;
    private List<Playlist> playlists;
    private String userToken;
    private int userId;
    private OnDeleteItemClickListener deleteItemClickListener;

    public interface OnDeleteItemClickListener {
        void onDeleteItemClick(int position);//Phương thức này được gọi khi người dùng thực hiện việc xóa một mục từ danh sách phát
    }
    public void setOnDeleteItemClickListener(OnDeleteItemClickListener listener) {
        this.deleteItemClickListener = listener;
        // Phương thức này được sử dụng để thiết lập một bộ lắng nghe (listener) để xử lý sự kiện khi người dùng muốn xóa một mục từ danh sách phát.
    }
    public List<Playlist> getPlaylists() {
        return playlists;//Phương thức này trả về danh sách các playlist.
    }
    public PlaylistAllAdapter(Context context, List<Playlist> playlists, int userId,String userToken) {
        this.context = context;
        this.playlists = playlists;
        this.userId = userId;
        this.userToken = userToken;
        //Constructor này được sử dụng để khởi tạo một đối tượng PlaylistAllAdapter với context,danh sách playlist, userId và userToken được cung cấp.
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_playlist_item_seemore, parent, false);
        return new MyViewHolder(view);
        //Mỗi ViewHolder sẽ chứa các tham chiếu đến các thành phần UI trong layout của mỗi item.
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        holder.bind(playlist);
        //Gắn sự kiện onClick cho menu_button
        holder.getMenu_button().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Gọi phương thức showMenuForItem và chuyển anchorView là menu_button và vị trí từ holder
                showMenuForItem(view, holder.getAdapterPosition());
            }
        });

        // Xử lý sự kiện click vào item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy playlistId của playlist khi click vào
                int playlistId = playlist.getId();

                //Tạo Intent để chuyển đến PlaylistItemActivity
                Intent intent = new Intent(context, PlayListItemActivity.class);

                // Đính kèm playlistId vào Intent
                intent.putExtra("playlistId", playlistId);
                intent.putExtra("userId", userId);
                intent.putExtra("userToken", userToken);
                intent.putExtra("playlistName", playlist.getTitle());

                // Chuyển đến PlaylistItemActivity
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView itemCountTextView;
        private AppCompatImageView imageView;
        private TextView menu_button;

        public MyViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            imageView = itemView.findViewById(R.id.item_image);
            itemCountTextView = itemView.findViewById(R.id.quantityTextView);
            menu_button = itemView.findViewById(R.id.menu_button);
            //gan cac tham chieu cua cac thanh phan trong giao dien
        }

        public void bind(Playlist playlist) {
            titleTextView.setText(playlist.getTitle());
            itemCountTextView.setText(String.valueOf(playlist.getItemCount())); // Hiển thị itemCount

            //Lấy danh sách các mục trong playlist
            List<PlaylistItem> playlistItems = playlist.getPlaylistItems();

            // Kiểm tra xem danh sách không rỗng và không null
            if (playlistItems != null && !playlistItems.isEmpty()) {
                // Lấy mục đầu tiên từ danh sách
                PlaylistItem firstPlaylistItem = playlistItems.get(0);

                // Kiểm tra xem mục đầu tiên không null
                if (firstPlaylistItem != null) {
                    // Lấy thông tin phim từ mục đầu tiên
                    InformationMovie informationMovie = firstPlaylistItem.getInformationMovie();

                    // Kiểm tra xem thông tin phim không null
                    if (informationMovie != null) {
                        // Lấy đường dẫn hình ảnh từ thông tin phim
                        String imageUrl = informationMovie.getImageLink();

                        // Sử dụng Picasso để tải và hiển thị hình ảnh
                        Picasso.get().load(imageUrl).into(imageView);
                    }
                }
            }
        }
        public TextView getMenu_button(){
            return menu_button;// trả về tham chiếu đến ImageView menu_button.
        }
    }
    private void showMenuForItem(View anchorView, int position) {
        PopupMenu popupMenu = new PopupMenu(context, anchorView);
        popupMenu.inflate(R.menu.vertical_menu);

        //Lấy Playlist tại vị trí position
        Playlist playlist = playlists.get(position);

        // Lấy id từ Playlist
        int itemId = playlist.getId();

        // Đăng ký một trình xử lý sự kiện cho mỗi mục menu
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_delete) {
                    //Hiển thị thông báo AlertDialog xác nhận trước khi xóa
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

    // Phương thức hiển thị thông báo AlertDialog xác nhận trước khi xóa
    private void showDeleteConfirmationDialog(int itemId, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc muốn xóa item với ID: " + itemId + " không?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Nếu người dùng chọn "Có", gọi phương thức onDeleteItemClick() của listener
                if (deleteItemClickListener != null) {
                    deleteItemClickListener.onDeleteItemClick(position);
                }
            }
        });
        builder.setNegativeButton("Không", null);
        builder.show();
    }
}


