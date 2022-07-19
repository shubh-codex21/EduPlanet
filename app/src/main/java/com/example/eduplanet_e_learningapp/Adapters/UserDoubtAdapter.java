package com.example.eduplanet_e_learningapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduplanet_e_learningapp.Modals.Doubt;
import com.example.eduplanet_e_learningapp.R;
import com.example.eduplanet_e_learningapp.databinding.DoubtItemviewBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class UserDoubtAdapter extends RecyclerView.Adapter<UserDoubtAdapter.ViewHolder> {
    List<Doubt> doubts;
    Context context;

    public UserDoubtAdapter(List<Doubt> doubts, Context context) {
        this.doubts = doubts;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.doubt_itemview,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return doubts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        DoubtItemviewBinding binding;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            binding = DoubtItemviewBinding.bind(itemView);
        }
    }
}
