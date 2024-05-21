package com.random.artbook;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.random.artbook.databinding.ActivityMainBinding;
import com.random.artbook.databinding.RecyclerviewRowBinding;

import java.util.ArrayList;

public class artAdaptar extends RecyclerView.Adapter<artAdaptar.ArtHolder>  {

    ArrayList<Art> artArrayList;

    public artAdaptar(ArrayList<Art> artArrayList){
        this.artArrayList = artArrayList;
    }

    @NonNull
    @Override
    public ArtHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerviewRowBinding recyclerviewRowBinding = RecyclerviewRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ArtHolder(recyclerviewRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtHolder holder, int position) {
        holder.binding.recyclerViewTextView.setText(artArrayList.get(holder.getAdapterPosition()).name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickedPosition = holder.getAdapterPosition();
                if (clickedPosition != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(holder.itemView.getContext(), artActivity.class);
                    intent.putExtra("info", "old");
                    intent.putExtra("artId", artArrayList.get(clickedPosition).id);
                    holder.itemView.getContext().startActivity(intent);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return artArrayList.size();
    }

    public class ArtHolder extends RecyclerView.ViewHolder {
        private RecyclerviewRowBinding binding;

        public ArtHolder(RecyclerviewRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

