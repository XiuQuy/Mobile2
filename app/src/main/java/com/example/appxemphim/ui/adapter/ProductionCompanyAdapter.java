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
import com.example.appxemphim.model.ProductionCompanies;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductionCompanyAdapter extends RecyclerView.Adapter<ProductionCompanyAdapter.ProductionCompanyViewHolder> {
    private List<ProductionCompanies> companies;
    private Context context;

    public ProductionCompanyAdapter(Context context, List<ProductionCompanies> companies) {
        this.context = context;
        this.companies = companies;
    }

    @NonNull
    @Override
    public ProductionCompanyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_production_company, parent, false);
        return new ProductionCompanyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductionCompanyViewHolder holder, int position) {
        ProductionCompanies currentCompany = companies.get(position);


        if (currentCompany.getLogoPath() != null && !currentCompany.getLogoPath().isEmpty()) {
            Picasso.get().load("https://image.tmdb.org/t/p/w500" + currentCompany.getLogoPath()).into(holder.companyLogoImageView);
        }

        holder.companyNameTextView.setText(currentCompany.getName());
        holder.companyCountryTextView.setText(currentCompany.getOriginCountry());
    }

    @Override
    public int getItemCount() {
        return companies.size();
    }

    public class ProductionCompanyViewHolder extends RecyclerView.ViewHolder {
        ImageView companyLogoImageView;
        TextView companyNameTextView;
        TextView companyCountryTextView;

        public ProductionCompanyViewHolder(@NonNull View itemView) {
            super(itemView);
            companyLogoImageView = itemView.findViewById(R.id.company_logo);
            companyNameTextView = itemView.findViewById(R.id.company_name);
            companyCountryTextView = itemView.findViewById(R.id.company_country);
        }
    }
}