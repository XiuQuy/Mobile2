package com.example.appxemphim.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.appxemphim.R;
import com.example.appxemphim.model.PlaylistItem;

import java.util.List;

public class PlayListItemAdapter extends RecyclerView.Adapter<PlayListItemAdapter.MyViewHolder>{
    //Khai báo Interface để gửi sự kiện xóa về Activity
    public interface OnDeleteItemClickListener {
        void onDeleteItemClick(int position);//Phương thức này được gọi khi người dùng thực hiện việc xóa một mục từ danh sách phát
    }
    private Context context;
    private List<PlaylistItem> playListItems;
    private OnDeleteItemClickListener deleteItemClickListener;


    public PlayListItemAdapter(Context context, List<PlaylistItem> playListItems){
        this.context = context;
        this.playListItems = playListItems;//contructor nhan contex va danh sach cac playlist va gan cho cac bien tuong ung
    }

    public void setOnDeleteItemClickListener(OnDeleteItemClickListener listener) {
        this.deleteItemClickListener = listener;//xử lý sự kiện xóa mục từ danh sách phát.

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(view);

        return new MyViewHolder(view);
        //Mỗi ViewHolder sẽ chứa các tham chiếu đến các thành phần UI trong layout của mỗi item.
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PlaylistItem playListItem = playListItems.get(position);
        holder.bind(playListItem);

        //Gắn sự kiện onClick cho menu_button
        holder.getMenu_button().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Gọi phương thức showMenuForItem và chuyển anchorView là menu_button và vị trí từ holder
                showMenuForItem(view, holder.getAdapterPosition());
            }
        });
    }


    public List<PlaylistItem> getPlayListItems() {
        return playListItems; // Phương thức để trả về danh sách các mục
    }

    @Override
    public int getItemCount() {
        return playListItems.size();//tra ve so luong phan tu danh sach
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView output_title;
        private TextView output_tag;
        private ImageView image_thumbnail;
        private ImageView menu_button;

        public MyViewHolder(View itemView){
            super(itemView);
            output_tag = itemView.findViewById(R.id.text_view_tag);
            output_title = itemView.findViewById(R.id.text_view_title);
            image_thumbnail = itemView.findViewById(R.id.image_view_icon);
            menu_button = itemView.findViewById(R.id.menu_button);
            //gan cac tham chieu cua cac thanh phan trong giao dien
        }

        public void bind(PlaylistItem playListItem) {
            output_title.setText(playListItem.getInformationMovie().getTitle());
            output_tag.setText(playListItem.getInformationMovie().getTag());

            Glide.with(context)
                    .load(playListItem.getInformationMovie().getImageLink())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_baseline_lock_24) //Placeholder nếu ảnh chưa được tải
                            .override(100, 70) // Kích thước mong muốn của ảnh (width, height)
                            .centerCrop()) // Căn chỉnh hình ảnh để lấp đầy ImageView và cắt bớt phần thừa nếu cần
                    .into(image_thumbnail);

        }
        public  ImageView getMenu_button(){
            return menu_button;//trả về tham chiếu đến ImageView "menu_button".
        }
    }

    private void showMenuForItem(View anchorView, int position) {
        PopupMenu popupMenu = new PopupMenu(context, anchorView);
        popupMenu.inflate(R.menu.vertical_menu);

        //Lấy PlaylistItem tại vị trí position
        PlaylistItem playlistItem = playListItems.get(position);

        // Lấy id từ PlaylistItem
        int itemId = playlistItem.getId();

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