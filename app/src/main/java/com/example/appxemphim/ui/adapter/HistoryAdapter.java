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
import com.example.appxemphim.model.History;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder>{

    private Context context;
    private List<History> listHistory;


    public HistoryAdapter(Context context, List<History> listHistory){
        this.context = context;
        this.listHistory = listHistory;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recycle_view_activity_history, parent,false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        History history = listHistory.get(position);
        holder.bind(history);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return listHistory.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView output_title;
        private TextView output_tag;
        private ImageView image_thumbnail;

        public MyViewHolder(View itemView){
            super(itemView);
            output_tag = itemView.findViewById(R.id.tv_tag_item_recycle_view_activity_history);
            output_title = itemView.findViewById(R.id.tv_title_item_recycle_view_activity_history);
            image_thumbnail = itemView.findViewById(R.id.img_icon_item_recycle_view_activity_history);
        }

        public void bind(History history) {
            output_title.setText(history.getInformationMovie().getTitle());
            output_tag.setText(String.valueOf(history.getSecondsCount()));
            Glide.with(context)
                    .load(history.getInformationMovie().getImageLink())
                    .apply(new RequestOptions().placeholder(R.drawable.ic_baseline_lock_24)) // Placeholder nếu ảnh chưa được tải
                    .into(image_thumbnail);
        }
    }
}
