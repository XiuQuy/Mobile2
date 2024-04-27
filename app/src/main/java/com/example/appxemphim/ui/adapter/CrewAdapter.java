package com.example.appxemphim.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appxemphim.R;
import com.example.appxemphim.ui.viewmodel.CreditsResponse;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CrewAdapter extends RecyclerView.Adapter<CrewAdapter.CrewViewHolder> {

    private Context mContext;
    private List<CreditsResponse.Crew> mCrewList;

    public CrewAdapter(Context context, List<CreditsResponse.Crew> crewList) {
        mContext = context;
        mCrewList = crewList;
    }

    @NonNull
    @Override
    public CrewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.crew_item_layout, parent, false);
        return new CrewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CrewViewHolder holder, int position) {
        CreditsResponse.Crew crewMember = mCrewList.get(position);
        holder.textViewName.setText(crewMember.getName());
        holder.textViewJob.setText(crewMember.getJob());

        // Load hình ảnh của crew vào imageViewCrew
        Picasso.get()
                .load("https://image.tmdb.org/t/p/w500" + crewMember.getImageUrl())
                .fit()
                .centerCrop()
                .into(holder.imageViewCrew);
    }

    @Override
    public int getItemCount() {
        return mCrewList.size();
    }

    public static class CrewViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewCrew;
        TextView textViewName;
        TextView textViewJob;

        public CrewViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCrew = itemView.findViewById(R.id.imageViewCrew);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewJob = itemView.findViewById(R.id.textViewJob);
        }
    }
}

