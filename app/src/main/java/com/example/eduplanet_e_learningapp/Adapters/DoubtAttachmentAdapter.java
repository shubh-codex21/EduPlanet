package com.example.eduplanet_e_learningapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduplanet_e_learningapp.Activities.ShowAttachImgActivity;
import com.example.eduplanet_e_learningapp.Modals.DoubtAttachments;
import com.example.eduplanet_e_learningapp.R;
import com.example.eduplanet_e_learningapp.databinding.DoubtAttachmentItemviewBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class DoubtAttachmentAdapter extends RecyclerView.Adapter<DoubtAttachmentAdapter.ViewHolder> {
    ArrayList<DoubtAttachments> attachments;
    Context context;

    public DoubtAttachmentAdapter(ArrayList<DoubtAttachments> attachments, Context context) {
        this.attachments = attachments;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.doubt_attachment_itemview,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        DoubtAttachments attachment = attachments.get(position);
        holder.binding.image.setText(attachment.getImageUrl());

        holder.binding.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ShowAttachImgActivity.class);
                intent.putExtra("imageUrl",attachment.getImageUrl());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return attachments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        DoubtAttachmentItemviewBinding binding;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            binding = DoubtAttachmentItemviewBinding.bind(itemView);
        }
    }
}
