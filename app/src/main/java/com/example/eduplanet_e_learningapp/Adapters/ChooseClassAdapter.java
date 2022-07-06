package com.example.eduplanet_e_learningapp.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduplanet_e_learningapp.Modals.ChooseClass;
import com.example.eduplanet_e_learningapp.R;
import com.google.android.material.card.MaterialCardView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class ChooseClassAdapter extends RecyclerView.Adapter<ChooseClassAdapter.ViewHolder> {
    ArrayList<ChooseClass> arrayList;
    Context context;
    String CNo = null;

    public ChooseClassAdapter(ArrayList<ChooseClass> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.choose_class_itemview,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        ChooseClass classNo = arrayList.get(position);
        holder.className.setText(classNo.getClassName());

        if(arrayList.get(position).isSelected()){
            holder.classCard.setStrokeColor(context.getResources().getColor(R.color.blue2));
        }else{
            holder.classCard.setStrokeColor(context.getResources().getColor(R.color.grey1));
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView className;
        MaterialCardView classCard;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            className = itemView.findViewById(R.id.className);
            classCard = itemView.findViewById(R.id.classCard);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    for(int i=0; i<arrayList.size();i++)
                    {
                        arrayList.get(i).setSelected(false);
                    }
                    arrayList.get(getAdapterPosition()).setSelected(true);
                    CNo = arrayList.get(getAdapterPosition()).getClassName();
                    storeClassNo();
                    notifyDataSetChanged();
                }

            });
        }

        void storeClassNo(){
            SharedPreferences sharedPref = context.getSharedPreferences("application",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            if (CNo == null) {
                editor.putString("Class", "None");
                editor.apply();
            } else {
                editor.putString("Class", CNo);
                editor.apply();
            }
        }
    }
}
