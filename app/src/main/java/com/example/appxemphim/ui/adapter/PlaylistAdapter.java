package com.example.appxemphim.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appxemphim.R;
import com.example.appxemphim.model.InformationMovie;
import com.example.appxemphim.model.Playlist;
import com.example.appxemphim.model.Playlist;
import com.example.appxemphim.model.PlaylistItem;
import com.squareup.picasso.Picasso;

import java.util.List;


public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.MyViewHolder> {
    private Context context;
    private List<Playlist> Playlists;

    public PlaylistAdapter(Context context, List<Playlist> Playlists) {
        this.context = context;
        this.Playlists = Playlists;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_playlist_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Playlist Playlist = Playlists.get(position);
        holder.bind(Playlist);
    }

    @Override
    public int getItemCount() {
        return Playlists.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView itemCountTextView;
        private AppCompatImageView imageView;


        public MyViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            imageView = itemView.findViewById(R.id.item_image);
            itemCountTextView = itemView.findViewById(R.id.timeTextView);
        }

        public void bind(Playlist playlist) {
            titleTextView.setText(playlist.getTitle());
            itemCountTextView.setText(String.valueOf(playlist.getItemCount())); // Hiển thị itemCount

            // Lấy danh sách các mục trong playlist
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
    }

}

