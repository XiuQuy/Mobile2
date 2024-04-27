package com.example.appxemphim.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.appxemphim.R;
import com.example.appxemphim.model.YoutubeVideoItem;
import com.example.appxemphim.ui.activity.VideoYoutubePlayerActivity;

import java.util.List;

public class MoreTrailerAdapter extends RecyclerView.Adapter<MoreTrailerAdapter.MyViewHolder>{

    private Context context;
    private List<YoutubeVideoItem> listVideos;

    public MoreTrailerAdapter(Context context, List<YoutubeVideoItem> listVideos){
        this.context = context;
        this.listVideos = listVideos;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_recycler_view_more_trailer_video, parent,false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        YoutubeVideoItem video = listVideos.get(position);
        holder.bind(video);
        holder.itemView.setOnClickListener(view -> {
            //VideoYoutubePlayerActivity.sendIntent(context, video, listVideos);
        });
    }

    @Override
    public int getItemCount() {
        return listVideos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageThumbnail;
        private TextView title;
        private TextView channelName;

        public MyViewHolder(View itemView){
            super(itemView);
            imageThumbnail = itemView.findViewById(R.id.img_thumbnail);
            title = itemView.findViewById(R.id.tv_title);
            channelName = itemView.findViewById(R.id.tv_channel);
        }

        public void bind(YoutubeVideoItem video) {
            title.setText(video.getSnippet().getTitle());
            channelName.setText(video.getSnippet().getChannelTitle());
            Glide.with(context)
                    .load(video.getSnippet().getThumbnails().getMediumThumbnail().getUrl())
                    .apply(new RequestOptions().placeholder(R.drawable.placeholder_img_load))
                    .listener(new GlideLoadImgListener(imageThumbnail))
                    .into(imageThumbnail);
        }
    }

    public void addAllData(List<YoutubeVideoItem> newData) {
        int startPosition = listVideos.size();
        listVideos.addAll(newData);
        notifyItemRangeInserted(startPosition, newData.size());
    }

    public void addData(YoutubeVideoItem movie) {
        int position = listVideos.size();
        listVideos.add(movie);
        notifyItemInserted(position);
    }

    public void clear() {
        listVideos.clear();
        notifyDataSetChanged();
    }
}
