package com.example.yumyum.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yumyum.Interface.ItemClickListener;
import com.example.yumyum.R;

public class FoodViewHolder  extends RecyclerView.ViewHolder implements
        View.OnClickListener, View.OnCreateContextMenuListener {

    public TextView txtfoodname;
    public TextView txtfoodmaterials;
    public TextView txtfoodrecipes;
    public TextView txtfoodrecipeslink;
    public ImageView imageView;

    private ItemClickListener itemClickListener;


    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);
        txtfoodname= itemView.findViewById(R.id.foodname);
        txtfoodmaterials= itemView.findViewById(R.id.foodmaterials);
        txtfoodrecipes= itemView.findViewById(R.id.foodrecipes);
        txtfoodrecipeslink= itemView.findViewById(R.id.foodrecipeslink);
        imageView= itemView.findViewById(R.id.foodpicture);

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

