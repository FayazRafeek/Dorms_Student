package com.example.dormsuser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dormsuser.databinding.MenuItemLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuVH> {

    Context context;
    MenuAction listner;
    Boolean selectable = false;

    public MenuAdapter(Context context, MenuAction listner) {
        this.context = context;
        this.listner = listner;
    }

    public MenuAdapter(Context context, MenuAction listner, Boolean selectable) {
        this.context = context;
        this.listner = listner;
        this.selectable = selectable;
    }

    List<FoodItem> list = new ArrayList<>();
    List<FoodItem> selected = new ArrayList<>();

    @NonNull
    @Override
    public MenuVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MenuItemLayoutBinding binding = MenuItemLayoutBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MenuVH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuVH holder, int position) {

        FoodItem item = list.get(position);

        Glide.with(context)
                .load(item.getImgUrl())
                .centerCrop()
                .into(holder.binding.itemImage);

        holder.binding.itemName.setText(item.getItemName());
        holder.binding.itemPrice.setText("₹" + item.getPrice());

        Boolean isSelected = false;
        if(selected != null)
            for (FoodItem f : selected)
                if(f.getItemId().equals(item.getItemId())){
                    isSelected = true;
                    break;
                }

        if(selectable) {
            if (isSelected)
                holder.binding.getRoot().setCardBackgroundColor(ContextCompat.getColor(context, R.color.green));
            else
                holder.binding.getRoot().setCardBackgroundColor(ContextCompat.getColor(context, R.color.grey_2));

            holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listner.onClick(item);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(List<FoodItem> list){
        this.list = list;
        notifyDataSetChanged();
    }

    public void updateSelection(FoodItem item){

        if(selected == null)
            selected = new ArrayList<>();

        Boolean isPresent = false;
        for (FoodItem f : selected)
            if(f.getItemId().equals(item.getItemId())){
                isPresent = true;
                break;
            }

        if(isPresent)
            selected.remove(item);
        else
            selected.add(item);

        notifyDataSetChanged();
    }

    public List<FoodItem> getSelected(){return selected;}

    class MenuVH extends RecyclerView.ViewHolder{

        MenuItemLayoutBinding binding;
        public MenuVH(@NonNull MenuItemLayoutBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }

    public interface MenuAction {
        void onClick(FoodItem item);
    }
}
