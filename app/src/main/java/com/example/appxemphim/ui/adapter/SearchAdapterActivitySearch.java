package com.example.appxemphim.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.appxemphim.R;
import com.example.appxemphim.model.Movie;
import com.example.appxemphim.ui.activity.MainActivity;
import com.example.appxemphim.ui.activity.MovieDetailActivity;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SearchAdapterActivitySearch extends RecyclerView.Adapter<SearchAdapterActivitySearch.MyViewHolder>{

    private Context context;
    private List<Movie> listMovies;
    private IPopupPlaylist iPopupPlaylist;

    public SearchAdapterActivitySearch(Context context, IPopupPlaylist iPopupPlaylist){
        this.context = context;
        listMovies = new ArrayList<>();
        this.iPopupPlaylist = iPopupPlaylist;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recycler_view_activity_search, parent,false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Movie movie = listMovies.get(position);
        holder.bind(movie);
        holder.itemView.setOnClickListener(view -> {
            Intent intent =  new Intent(context, MovieDetailActivity.class);
            intent.putExtra("movieId", String.valueOf(movie.getId()));
            intent.putExtra("tag", movie.getTag());
            context.startActivity(intent);
        });
        holder.btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionMenu(v, movie);
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
        private Locale contextLanguageLocale;
        private TextView btnMenu;

        public MyViewHolder(View itemView){
            super(itemView);
            imageThumbnail = itemView.findViewById(R.id.img_thumbnail_item_rv_actv_search);
            title = itemView.findViewById(R.id.tv_title_item_rv_activity_search);
            date = itemView.findViewById(R.id.tv_date_item_rv_activity_search);
            language = itemView.findViewById(R.id.tv_language_item_rv_activity_search);
            rate = itemView.findViewById(R.id.tv_rate_item_rv_activity_search);
            btnMenu = itemView.findViewById(R.id.menu_item_rv_activity_search);
            contextLanguageLocale = context.getResources().getConfiguration().getLocales().get(0);
        }

        public void bind(Movie movie) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", contextLanguageLocale);
            DecimalFormat decimalFormat = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));
            Date dateInput = movie.getReleaseDate();
            if(dateInput != null){
                date.setText(dateFormat.format(dateInput));
            }
            title.setText(movie.getName());
            language.setText(new Locale(movie.getOriginalLanguage()).getDisplayLanguage(contextLanguageLocale));
            String rateString = decimalFormat.format(movie.getVoteAverage()*10) + "% / " + movie.getVoteCount();
            rate.setText(rateString);
            Glide.with(context)
                    .load(movie.getPosterPath())
                    .apply(new RequestOptions().placeholder(R.drawable.placeholder_img_load))
                    .listener(new GlideLoadImgListener(imageThumbnail))
                    .into(imageThumbnail);
        }
    }

    public void updateNewData(List<Movie> newData) {
        listMovies.clear();
        listMovies.addAll(newData);
        notifyDataSetChanged();
    }

    public void addAllData(List<Movie> newData) {
        int startPosition = listMovies.size();
        listMovies.addAll(newData);
        notifyItemRangeInserted(startPosition, newData.size());
    }

    public void addData(Movie movie) {
        int position = listMovies.size();
        listMovies.add(movie);
        notifyItemInserted(position);
    }

    public void clear() {
        listMovies.clear();
        notifyDataSetChanged();
    }

    private void showOptionMenu(View view, Movie movie) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_item_search, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            int idItem = item.getItemId();
            if(idItem == R.id.item_search_add_to_playlist_option){
                iPopupPlaylist.showPopupAddToPlaylist(movie);
                Log.i("ITEM RECYCLER VIEW", "click add playlist option");
                return true;
            }else if(idItem == R.id.item_search_view_detail_option){
                //Intent intent =  new Intent(context, MovieDetailActivity.class);
                //context.startActivity(intent);
                Log.i("ITEM MENU RECYCLER VIEW", "click view detail");
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    public interface IPopupPlaylist {
        void showPopupAddToPlaylist(Movie movie);
    }
}
