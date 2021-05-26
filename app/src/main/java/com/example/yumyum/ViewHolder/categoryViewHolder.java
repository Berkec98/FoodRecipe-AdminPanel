package com.example.yumyum.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yumyum.Interface.ItemClickListener;
import com.example.yumyum.R;

public class categoryViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener, View.OnCreateContextMenuListener {

    public TextView txtcategoryname;
    public ImageView imageView;

    private ItemClickListener itemClickListener;


    public categoryViewHolder(@NonNull View itemView) {
        super(itemView);
        txtcategoryname= itemView.findViewById(R.id.categoryname);
        imageView= itemView.findViewById(R.id.categorypicture);

        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener=itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.OnClick(v,getAdapterPosition(),false);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Choose");
        menu.add(0,0,getAdapterPosition(),"Update");
        menu.add(0,1,getAdapterPosition(),"Delete");
    }
}
