package com.example.appxemphim;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.layout.R;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movies;
    private Context context;

    // Interface để xử lý sự kiện khi item được nhấn
    public interface OnItemClickListener {
        void onItemClick(Movie movie);
    }

    private OnItemClickListener listener;

    // Constructor để khởi tạo Adapter với danh sách phim được cung cấp
    public MovieAdapter(List<Movie> movies, Context context) {
        this.movies = movies;
        this.context = context;
    }

    // Phương thức này được gọi khi RecyclerView cần tạo một ViewHolder mới
    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo một View từ layout của một item trong RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_suggestion, parent, false);
        return new MovieViewHolder(view);
    }

    // Phương thức này được gọi khi RecyclerView cần hiển thị dữ liệu trong một ViewHolder
    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        // Lấy đối tượng Movie tương ứng với vị trí position trong danh sách
        Movie movie = movies.get(position);
        // Gọi phương thức bind() để gắn dữ liệu vào ViewHolder
        holder.bind(movie);
    }

    // Phương thức này trả về số lượng item trong danh sách phim
    @Override
    public int getItemCount() {
        return movies.size();
    }

    // Phương thức này được sử dụng để cập nhật danh sách phim khi có thay đổi
    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        // Thông báo cho RecyclerView biết rằng dữ liệu đã thay đổi, cần cập nhật giao diện
        notifyDataSetChanged();
    }

    // Lớp MovieViewHolder, là một ViewHolder để hiển thị dữ liệu của một item phim
    public class MovieViewHolder extends RecyclerView.ViewHolder {

        private ImageView poster;
        private TextView title;
//        private TextView overview;

        // Constructor để khởi tạo ViewHolder
        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các thành phần UI từ layout của item
            poster = itemView.findViewById(R.id.movie_poster);
            title = itemView.findViewById(R.id.movie_title);
//            overview = itemView.findViewById(R.id.movie_overview);

            // Gắn sự kiện click vào itemView
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(movies.get(position));
                    }
                }
            });
        }

        // Phương thức này được gọi để gắn dữ liệu của một đối tượng Movie vào ViewHolder
        public void bind(Movie movie) {
            title.setText(movie.getTitle()); // Gắn tiêu đề của phim
//            overview.setText(movie.getOverview()); // Gắn tóm tắt của phim
            // Sử dụng thư viện Glide để tải và hiển thị hình ảnh của phim từ URL
            Glide.with(itemView.getContext())
                    .load("http://image.tmdb.org/t/p/w500" + movie.getPosterPath())
                    .into(poster);
        }
    }

    // Phương thức này để thiết lập người nghe sự kiện khi item được nhấn
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
