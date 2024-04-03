package com.example.appxemphim.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.appxemphim.R;
import com.example.appxemphim.model.Movie;

import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SearchAdapterActivitySearch extends RecyclerView.Adapter<SearchAdapterActivitySearch.MyViewHolder>{

    private Context context;
    private List<Movie> listMovies;


    public SearchAdapterActivitySearch(Context context, List<Movie> listMovies){
        this.context = context;
        this.listMovies = listMovies;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recycler_view_activity_search, parent,false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Movie movie = listMovies.get(position);
        holder.bind(movie);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return listMovies.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageThumbnail;
        private TextView title;
        private TextView date;
        private TextView language;
        private TextView rate;
        public MyViewHolder(View itemView){
            super(itemView);
            imageThumbnail = itemView.findViewById(R.id.img_thumbnail_item_rv_actv_search);
            title = itemView.findViewById(R.id.tv_title_item_rv_activity_search);
            date = itemView.findViewById(R.id.tv_date_item_rv_activity_search);
            language = itemView.findViewById(R.id.tv_language_item_rv_activity_search);
            rate = itemView.findViewById(R.id.tv_rate_item_rv_activity_search);
        }

        public void bind(Movie movie) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("vi"));
            DecimalFormat decimalFormat = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));
            Date dateInput = movie.getReleaseDate();
            if(dateInput != null){
                date.setText(dateFormat.format(dateInput));
            }
            title.setText(movie.getName());
            language.setText(new Locale(movie.getOriginalLanguage()).getDisplayLanguage());
            String rateString = decimalFormat.format(movie.getVoteAverage()*10) + "% / " + movie.getVoteCount();
            rate.setText(rateString);
            Glide.with(context)
                    .load(movie.getPosterPath())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.placeholder_img_load)
                    ).listener(new GlideLoadImgListener(imageThumbnail))
                    .into(imageThumbnail);
        }
    }

    public void clearData() {
        listMovies.clear();
        notifyDataSetChanged();
    }
}
