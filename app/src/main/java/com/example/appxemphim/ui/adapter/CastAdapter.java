package com.example.appxemphim.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.appxemphim.R;
import com.example.appxemphim.ui.viewmodel.CreditsResponse;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {

    private Context mContext;
    private List<CreditsResponse.Cast> mCastList;

    public CastAdapter(Context context, List<CreditsResponse.Cast> castList) {
        mContext = context;
        mCastList = castList;
    }

    @NonNull
    @Override
    public CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cast_item_layout, parent, false);
        return new CastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CastViewHolder holder, int position) {
        CreditsResponse.Cast cast = mCastList.get(position);
        holder.textViewName.setText(cast.getName());
        holder.textViewCharacter.setText(cast.getCharacter());
        Picasso.get().load("https://image.tmdb.org/t/p/w500" + cast.getImageUrl()).into(holder.imageViewCast);
    }

    @Override
    public int getItemCount() {
        return mCastList.size();
    }

    public static class CastViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewCast;
        TextView textViewName;
        TextView textViewCharacter;

        public CastViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCast = itemView.findViewById(R.id.imageViewCast);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewCharacter = itemView.findViewById(R.id.textViewCharacter);
        }
    }
    public void setCastList(List<CreditsResponse.Cast> castList) {
        mCastList = castList;
        notifyDataSetChanged();
    }
}
